package ru.bcs.creditmarkt.strapi.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.client.WscoClient;
import ru.bcs.creditmarkt.strapi.dto.Bank;
import ru.bcs.creditmarkt.strapi.dto.empty.BicCode;
import ru.bcs.creditmarkt.strapi.dto.empty.CreditOrganization;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.BicToIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.BodyIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.EnvelopBicToIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.BicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.BodyRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.EnvelopeBicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToIntCodeResponse;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToRegNumberResponse;

import javax.xml.bind.*;
import javax.xml.soap.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
    private final StrapiClient strapiClient;
    private final WscoClient wscoClient;

    //функция для обновление данных в страпи из цбрф
    @Override
    public void sync() {
        //получение банков из страпи, у которых одинаковые даты создания и обновления:
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Bank> banksForUpdate = strapiClient.getBanks().stream()
                .filter(bank -> formatter.format(bank.getCreated_at()).equals(formatter.format(bank.getUpdated_at())))
                .collect(Collectors.toList());
        System.out.println("banksForUpdate with equals date:" + banksForUpdate);

        //получение бик кодов из цбрф:
        BicCode bicCode = wscoClient.getCreditOrganizations();
        System.out.println(bicCode);

        List<CreditOrganization> creditOrganizations = bicCode.getCreditOrganizations();
        List<Bank> banks = new ArrayList<>();
        creditOrganizations
                .forEach(co -> banksForUpdate
                        .forEach(bank -> {
                            if (StringUtils.toRootLowerCase(co.getShortName()).equals(StringUtils.toRootLowerCase(bank.getName()))) {
                                Bank bankEqualsName = new Bank();
                                bankEqualsName.setName(co.getShortName());
                                bankEqualsName.setBic(co.getBic());
                                bankEqualsName.setCbrId(getBicToIntCodeResponse(wscoClient
                                        .bicToIntCode(getEnvelopBicToIntCode(co.getBic()))).getBicToIntCodeResult());
                                bankEqualsName.setLicenseNumber(getBicToRegNumberResponse(wscoClient
                                        .bicToRegNumber(getEnvelopBicToRegNumber(co.getBic()))).getBicToRegNumberResult());
                                banks.add(bankEqualsName);
                            }
                        }));
        System.out.println("banks with equals name for update = " + banks);
//
//        //печать регистрационного кода по бик-у из цбрф:
//        System.out.println();
//        String stringBicToRegNumber = wscoClient.bicToRegNumber(getEnvelopBicToRegNumber("040173745"));
//        System.out.println("regNumber=" + stringBicToRegNumber);


//        //распечатывание первого банка (имя, бик):
//        System.out.println("getShortName = "
//                + creditOrganizations.get(0).getShortName()
//                + " getBic= "
//                + creditOrganizations.get(0).getBic());

//        //печать SOAP ответа на запрос регистрационного номера по бик-у (по первому из полученного выше списка):
//        System.out.println("regNo=" + wscoClient.bicToRegNumber(getEnvelopBicToRegNumber("040173745")));

//        //печать внутреннего кода по бик-у из цбрф (по первому банку):
//        String stringBicToIntCode = wscoClient.bicToIntCode(getEnvelopBicToIntCode(creditOrganizations.get(0).getBic()));
//        System.out.println("intCode=" + stringBicToIntCode);

//        //объект envelop - объект EnvelopBicToIntCodeResponse - внутренний код по бик-у - SOAP ответ через объект EnvelopBicToIntCodeResponse
//        System.out.println("envelop:" + wscoClient.bicToIntCode(getEnvelopBicToIntCode("040173745")));


//        try {
//            BicToIntCodeResponse extracted = getBicToIntCodeResponse(stringBicToIntCode);
//
//        } catch (JAXBException | IOException | SOAPException e) {
//            e.printStackTrace();
//        }
    }

    private BicToIntCodeResponse getBicToIntCodeResponse(String stringBicToIntCode) {
        BicToIntCodeResponse value = new BicToIntCodeResponse();
        try {
            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(stringBicToIntCode.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(BicToIntCodeResponse.class).createUnmarshaller();
            Document document = message.getSOAPBody().extractContentAsDocument();
            JAXBElement<BicToIntCodeResponse> response = unmarshaller.unmarshal(document.getFirstChild(), BicToIntCodeResponse.class);
            value = response.getValue();
//            System.out.println("bicToIntCode = " + value.getBicToIntCodeResult());
        } catch (IOException | SOAPException | JAXBException e) {
            e.printStackTrace();
        }
        return value;
    }

    private BicToRegNumberResponse getBicToRegNumberResponse(String stringBicToRegNumber) {
        BicToRegNumberResponse value = new BicToRegNumberResponse();
        try {
            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(stringBicToRegNumber.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(BicToRegNumberResponse.class).createUnmarshaller();
            Document document = message.getSOAPBody().extractContentAsDocument();
            JAXBElement<BicToRegNumberResponse> response = unmarshaller.unmarshal(document.getFirstChild(), BicToRegNumberResponse.class);
            value = response.getValue();
//            System.out.println("BicToRegNumber = " + value.getBicToRegNumberResult());
        } catch (IOException | SOAPException | JAXBException e) {
            e.printStackTrace();
        }
        return value;
    }

    private EnvelopeBicToRegNumber getEnvelopBicToRegNumber(String bic) {
        EnvelopeBicToRegNumber envelopeBicToRegNumber = new EnvelopeBicToRegNumber();
        BodyRegNumber bodyRegNumber = new BodyRegNumber();
        BicToRegNumber bicToRegNumber = new BicToRegNumber();
        bicToRegNumber.setBicCode(bic);
        bodyRegNumber.setBicToRegNumber(bicToRegNumber);
        envelopeBicToRegNumber.setBodyRegNumber(bodyRegNumber);
        return envelopeBicToRegNumber;
    }

    //объект для SOAP запроса получения внутреннего кода по бик-у
    private EnvelopBicToIntCodeRequest getEnvelopBicToIntCode(String bic) {
        EnvelopBicToIntCodeRequest envelopBicToIntCodeRequest = new EnvelopBicToIntCodeRequest();
        BodyIntCodeRequest bodyIntCodeRequest = new BodyIntCodeRequest();
        BicToIntCodeRequest bicToIntCode = new BicToIntCodeRequest();
        bicToIntCode.setBicCode(bic);
        bodyIntCodeRequest.setBicToIntCodeRequest(bicToIntCode);
        envelopBicToIntCodeRequest.setBodyIntCodeRequest(bodyIntCodeRequest);
        return envelopBicToIntCodeRequest;
    }


}
