package ru.bcs.creditmarkt.strapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import ru.bcs.creditmarkt.strapi.client.PhrasyClient;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.client.WscoClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.Bank;
import ru.bcs.creditmarkt.strapi.dto.strapi.Meta;
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
import ru.bcs.creditmarkt.strapi.dto.wsco.response.nonamespace.BicCode;
import ru.bcs.creditmarkt.strapi.dto.wsco.response.nonamespace.CO;
import ru.bcs.creditmarkt.strapi.dto.wsco.response.nonamespace.CreditOrganization;
import ru.bcs.creditmarkt.strapi.dto.wsco.response.withnamespase.BicToIntCodeResponse;
import ru.bcs.creditmarkt.strapi.dto.wsco.response.withnamespase.BicToRegNumberResponse;
import ru.bcs.creditmarkt.strapi.utils.constants.Cases;
import ru.bcs.creditmarkt.strapi.utils.constants.SeparatorConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class BankServiceImpl implements BankService {
    private final StrapiClient strapiClient;
    private final WscoClient wscoClient;
    private final PhrasyClient phrasyClient;
    private static final String[] EXCLUSION_LIST = {
            "идея банк"
    };

    //KB-10053
    //функция для обновление данных в страпи из цбрф
    @Scheduled(cron = "${schedule-time}")
    @Override
    public void sync() {

        //получение банков из страпи, у которых одинаковые даты создания и обновления:
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Bank> banksForUpdate = strapiClient.getBanks().stream()
                .filter(bank -> formatter.format(bank.getCreatedAt()).equals(formatter.format(bank.getUpdatedAt())))
                .collect(Collectors.toList());
//        log.info("banksForUpdate with equals date:" + banksForUpdate);

        if (banksForUpdate.size() != 0)
            updateBanks(banksForUpdate);
    }

    private void updateBanks(List<Bank> banksForUpdate) {
//        System.out.println("updateBanks");
        //получение из цбрф кредитных организация с названием и бик:
        BicCode bicCode = wscoClient.getCreditOrganizations();
//        System.out.println("кредитные организации из ЦБРФ:" + bicCode);
        log.info("credit organizations: " + bicCode);

        List<CreditOrganization> creditOrganizations = bicCode.getCreditOrganizations();
        List<Bank> banks = new ArrayList<>();
        creditOrganizations
                .forEach(creditOrg -> banksForUpdate
                        .forEach(bankForUpdate -> {
                            //KB-10053
                            //Сравнивать по бик (но в страпи бик - null) если оставить сравнение по бик, то банки не обновятся
                            if (creditOrg.getBic().equals(bankForUpdate.getBic()) ||
                                    StringUtils.toRootLowerCase(creditOrg.getShortName())
                                            .equals(StringUtils.toRootLowerCase(bankForUpdate.getName()))) {
//                                System.out.println("name " + creditOrg.getShortName() + " equals with " + bankForUpdate.getName());
                                Bank updateStrapiBank = getBankWithFieldsFromCreditOrg(creditOrg, bankForUpdate);
                                banks.add(updateStrapiBank);
                                strapiClient.updateBank(updateStrapiBank.getId(), updateStrapiBank);
                            }
                        }));
        System.out.println("banks with equals name for update = " + banks);
    }

    private Bank getBankWithFieldsFromCreditOrg(CreditOrganization creditOrg, Bank strapiBank) {
        Bank bank = new Bank();
        Meta meta = new Meta();
        bank.setId(strapiBank.getId());
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
            StringBuilder name_ablt = getDeclinableName(creditInfo.getOrgName(), Cases.ABLATIVE_CASE);
            StringBuilder name_gent = getDeclinableName(creditInfo.getOrgName(), Cases.GENITIVE_CASE);
            StringBuilder name_loct = getDeclinableName(creditInfo.getOrgName(), Cases.PREPOSITIONAL_CASE);
            StringBuilder name_datv = getDeclinableName(creditInfo.getOrgName(), Cases.DATIVE_CASE);
            StringBuilder name_accs = getDeclinableName(creditInfo.getOrgName(), Cases.ACCUSATIVE_CASE);

            bank.setSlug(strapiBank.getSlug());
            bank.setLetterBank(Character.toString(creditInfo.getOrgName().charAt(0)));
            bank.setTelephones(creditInfo.getPhones());
            bank.setRegistrationDate(creditInfo.getDateKGRRegistration());
            bank.setHeadOffice(creditInfo.getUstavAdr());
            bank.setRealAddress(creditInfo.getFactAdr());
            bank.setAuthorizedCapital(creditInfo.getUstMoney());
            bank.setSystemParticipation(creditInfo.getSsvDate());
            bank.setIsLicenseActive(creditInfo.getOrgStatus().equals("норм."));
            bank.setLeadership(creditInfo.getIsRBFileExist());
            bank.setName_ablt(name_ablt.toString());
            bank.setName_gent(name_gent.toString());
            bank.setName_loct(name_loct.toString());
            bank.setName_datv(name_datv.toString());
            bank.setName_accs(name_accs.toString());
            meta.setDescription(creditInfo.getOrgName());
            meta.setOgDescription(creditInfo.getOrgName());
            meta.setOgTitle(creditInfo.getOrgName());
            meta.setTitle(creditInfo.getOrgName());
            bank.setH1(creditInfo.getOrgName());
            bank.setMeta(meta);
            bank.setName(creditInfo.getOrgName());
        }

        bank.setBic(creditOrg.getBic());
        return bank;
    }

    private StringBuilder getDeclinableName(String originalName, String phrase) {
        StringBuilder totalName = new StringBuilder("");
        StringBuilder partOfDeclinableName = new StringBuilder("");
        if (Arrays.stream(EXCLUSION_LIST).anyMatch(exclusion ->
                StringUtils.toRootLowerCase(originalName).equals(exclusion))) {
            int indexOfSpace = originalName.indexOf(" ");
            partOfDeclinableName.append(originalName.substring(indexOfSpace));
            totalName.append(originalName, 0, indexOfSpace).append(" ");
        } else
            partOfDeclinableName.append(originalName);

        switch (phrase) {
            case (Cases.ABLATIVE_CASE):
                totalName.append(phrasyClient.getDeclinablePhrase(partOfDeclinableName.toString(), phrase).getAblt());
                break;
            case (Cases.GENITIVE_CASE):
                totalName.append(phrasyClient.getDeclinablePhrase(partOfDeclinableName.toString(), phrase).getGent());
                break;
            case (Cases.PREPOSITIONAL_CASE):
                totalName.append(phrasyClient.getDeclinablePhrase(partOfDeclinableName.toString(), phrase).getLoct());
                break;
            case (Cases.DATIVE_CASE):
                totalName.append(phrasyClient.getDeclinablePhrase(partOfDeclinableName.toString(), phrase).getDatv());
                break;
            case (Cases.ACCUSATIVE_CASE):
                totalName.append(phrasyClient.getDeclinablePhrase(partOfDeclinableName.toString(), phrase).getAccs());
                break;
        }
        return setRegister(originalName, totalName);
    }

    private StringBuilder setRegister(String originalName, StringBuilder declinableName) {

        int upperCaseCount = 0;
        for (int i = 0; i < originalName.length(); i++) {
            for (int j = i; j < declinableName.length(); j++) {
                if (StringUtils.toRootLowerCase(String.valueOf(originalName.charAt(i)))
                        .equals(StringUtils.toRootLowerCase(String.valueOf(declinableName.charAt(j))))) {

                    if (Character.isUpperCase(originalName.charAt(i))) {
                        declinableName.setCharAt(j, Character.toUpperCase(declinableName.charAt(j)));
                        upperCaseCount++;
                    }
                    break;
                }
            }
        }
        int lengthWithoutSpaces = originalName.replaceAll("\\s+", "").length();
        int lengthWithoutEndAndSpaces = lengthWithoutSpaces - 1;

        if (upperCaseCount == lengthWithoutSpaces || upperCaseCount == lengthWithoutEndAndSpaces) {
            for (int i = 0; i < declinableName.length(); i++) {
                declinableName.setCharAt(i, Character.toUpperCase(declinableName.charAt(i)));
            }
        }
        return  declinableName;
    }

    //KB-10053
    //конвертация полученого SOAP ответа от ЦБРФ в java объект с именем, соответствующего имени тега в ответе
    private <T> T getObjectResponse(String stringXml, Class<T> typedClass, String tagName) {
        try {
            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(stringXml.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(typedClass).createUnmarshaller();
            Document document = message.getSOAPBody().extractContentAsDocument();
            JAXBElement<T> response = unmarshaller.
                    unmarshal(document.getElementsByTagName(tagName).item(SeparatorConstants.FIRST_ITEM), typedClass);
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
    //объект для SOAP запроса - для получения информации о кредитной организации по внутреннему коду
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