package ru.bcs.creditmarkt.strapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.client.WscoClient;
import ru.bcs.creditmarkt.strapi.dto.Bank;
import ru.bcs.creditmarkt.strapi.dto.empty.BicCode;
import ru.bcs.creditmarkt.strapi.dto.empty.CreditOrganization;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo.BodyCreditInfoByIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo.CreditInfoByIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo.EnvelopCreditInfoByIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo.InternalCodesRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.BicToIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.BodyIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.EnvelopBicToIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.BicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.BodyRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.EnvelopeBicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.response.creditinfo.CO;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToIntCodeResponse;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToRegNumberResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
    private final StrapiClient strapiClient;
    private final WscoClient wscoClient;

    //KB-10053
    //функция для обновление данных в страпи из цбрф
    @Override
    public void sync() {
        //получение банков из страпи, у которых одинаковые даты создания и обновления:
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Bank> banksForUpdate = strapiClient.getBanks().stream()
                .filter(bank -> formatter.format(bank.getCreated_at()).equals(formatter.format(bank.getUpdated_at())))
                .collect(Collectors.toList());
        System.out.println("banksForUpdate with equals date:" + banksForUpdate);

        //получение из цбрф кредитных организация с названием и бик:
        BicCode bicCode = wscoClient.getCreditOrganizations();
        System.out.println("кредитные организации из ЦБРФ:" + bicCode);

        List<CreditOrganization> creditOrganizations = bicCode.getCreditOrganizations();
        List<Bank> banks = new ArrayList<>();
        creditOrganizations
                .forEach(creditOrg -> banksForUpdate
                        .forEach(bankForUpdate -> {
                            //KB-10053
                            //Сравнивать по бик (но в страпи бик - null) если оставить сравнение по бик, то банки не обновятся
                            if (StringUtils.toRootLowerCase(creditOrg.getShortName()).equals(StringUtils.toRootLowerCase(bankForUpdate.getName()))) {
//                                if (co.getBic().equals(bank.getBic())) {
                                banks.add(getBankWithFieldsFromCreditOrg(creditOrg));
                            }
                        }));
        System.out.println("banks with equals name for update = " + banks);
    }

    private Bank getBankWithFieldsFromCreditOrg(CreditOrganization creditOrg) {
        Bank bank = new Bank();
        BicToIntCodeResponse bicToIntCodeResponse = getObjectResponse(wscoClient.bicToIntCode(getEnvelopBicToIntCode(creditOrg.getBic())),
                BicToIntCodeResponse.class, "BicToIntCodeResponse");
        if (bicToIntCodeResponse != null)
            bank.setCbrId(bicToIntCodeResponse.getBicToIntCodeResult());

        BicToRegNumberResponse bicToRegNumberResponse = getObjectResponse(wscoClient.bicToRegNumber(getEnvelopBicToRegNumber(creditOrg.getBic())),
                BicToRegNumberResponse.class, "BicToRegNumberResponse");
        if (bicToRegNumberResponse != null)
            bank.setLicenseNumber(bicToRegNumberResponse.getBicToRegNumberResult());

        CO creditInfo = getObjectResponse(wscoClient.creditInfoByIntCode(getEnvelopCreditInfoByIntCode(bank.getCbrId())),
                CO.class, "CO");
        if (creditInfo != null) {
            bank.setLetterBank(Character.toString(creditInfo.getOrgName().charAt(0)));
            bank.setTelephones(creditInfo.getPhones());
            bank.setRegistrationDate(creditInfo.getDateKGRRegistration());
            bank.setHeadOffice(creditInfo.getUstavAdr());
            bank.setRealAddress(creditInfo.getFactAdr());
            bank.setAuthorizedCapital(creditInfo.getUstMoney());
            bank.setSystemParticipation(creditInfo.getSsv_Date());
            bank.setIsLicenseActive(creditInfo.getOrgStatus());
            bank.setLeadership(creditInfo.getIsRBFileExist());
        }

        bank.setName(creditOrg.getShortName());
        bank.setBic(creditOrg.getBic());
        return bank;
    }

    //KB-10053
    //конвертация полученого SOAP ответа от ЦБРФ в java объект с именем, соответствующего имени тега в ответе
    private <T> T getObjectResponse(String stringXml, Class<T> typedClass, String tagName) {
        try {
            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(stringXml.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(typedClass).createUnmarshaller();
            Document document = message.getSOAPBody().extractContentAsDocument();
            JAXBElement<T> response = unmarshaller.unmarshal(document.getElementsByTagName(tagName).item(0), typedClass);
            return response.getValue();

        } catch (IOException | SOAPException | JAXBException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    //KB-10053
    //объект для SOAP запроса - для получения регистрационного номера по бик-у
    private EnvelopeBicToRegNumber getEnvelopBicToRegNumber(String bic) {
        EnvelopeBicToRegNumber envelopeBicToRegNumber = new EnvelopeBicToRegNumber();
        BodyRegNumber bodyRegNumber = new BodyRegNumber();
        BicToRegNumber bicToRegNumber = new BicToRegNumber();
        bicToRegNumber.setBicCode(bic);
        bodyRegNumber.setBicToRegNumber(bicToRegNumber);
        envelopeBicToRegNumber.setBodyRegNumber(bodyRegNumber);
        return envelopeBicToRegNumber;
    }

    //KB-10053
    //объект для SOAP запроса - для получения внутреннего кода по бик-у
    private EnvelopBicToIntCodeRequest getEnvelopBicToIntCode(String bic) {
        EnvelopBicToIntCodeRequest envelopBicToIntCodeRequest = new EnvelopBicToIntCodeRequest();
        BodyIntCodeRequest bodyIntCodeRequest = new BodyIntCodeRequest();
        BicToIntCodeRequest bicToIntCode = new BicToIntCodeRequest();
        bicToIntCode.setBicCode(bic);
        bodyIntCodeRequest.setBicToIntCodeRequest(bicToIntCode);
        envelopBicToIntCodeRequest.setBodyIntCodeRequest(bodyIntCodeRequest);
        return envelopBicToIntCodeRequest;
    }

    //KB-10053
    //объект для SOAP запроса - для получения информации о кредитной организации
    private EnvelopCreditInfoByIntCodeRequest getEnvelopCreditInfoByIntCode(String intCode) {
        EnvelopCreditInfoByIntCodeRequest envelop = new EnvelopCreditInfoByIntCodeRequest();
        BodyCreditInfoByIntCodeRequest body = new BodyCreditInfoByIntCodeRequest();
        CreditInfoByIntCodeRequest creditInfo = new CreditInfoByIntCodeRequest();
        InternalCodesRequest internalCode = new InternalCodesRequest();
        internalCode.setIntCode(intCode);
        creditInfo.setInternalCodesRequest(internalCode);
        body.setCreditInfoByIntCodeRequest(creditInfo);
        envelop.setBodyIntCodeRequest(body);
        return envelop;
    }

}
