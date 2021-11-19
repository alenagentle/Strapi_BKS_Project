package ru.bcs.creditmarkt.strapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.client.WscoClient;
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
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToIntCodeResponse;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToRegNumberResponse;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.CO;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.CreditOrgInfo;

import javax.xml.bind.*;
import javax.xml.soap.*;
import java.io.*;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
    private final StrapiClient strapiClient;
    private final WscoClient wscoClient;

    //KB-10053
    //функция для обновление данных в страпи из цбрф
    @Override
    public void sync() {
//        //получение банков из страпи, у которых одинаковые даты создания и обновления:
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        List<Bank> banksForUpdate = strapiClient.getBanks().stream()
//                .filter(bank -> formatter.format(bank.getCreated_at()).equals(formatter.format(bank.getUpdated_at())))
//                .collect(Collectors.toList());
//        System.out.println("banksForUpdate with equals date:" + banksForUpdate);
//
//        //получение из цбрф кредитных организация с названием и бик:
//        BicCode bicCode = wscoClient.getCreditOrganizations();
//        System.out.println("кредитные организации из ЦБРФ:" + bicCode);
//
//        List<CreditOrganization> creditOrganizations = bicCode.getCreditOrganizations();
//        List<Bank> banks = new ArrayList<>();
//        creditOrganizations
//                .forEach(co -> banksForUpdate
//                        .forEach(bank -> {
//                            //TODO!!!!! СРАВИНИВАТЬ ПО БИК!!!!
//                            if (StringUtils.toRootLowerCase(co.getShortName()).equals(StringUtils.toRootLowerCase(bank.getName()))) {
//                                Bank bankEqualsName = new Bank();
//                                bankEqualsName.setName(co.getShortName());
//                                bankEqualsName.setBic(co.getBic());
//                                bankEqualsName.setCbrId(getBicToIntCodeResponse(wscoClient
//                                        .bicToIntCode(getEnvelopBicToIntCode(co.getBic()))).getBicToIntCodeResult());
//                                bankEqualsName.setLicenseNumber(getBicToRegNumberResponse(wscoClient
//                                        .bicToRegNumber(getEnvelopBicToRegNumber(co.getBic()))).getBicToRegNumberResult());
//                                banks.add(bankEqualsName);
//                            }
//                        }));
//        System.out.println("banks with equals name for update = " + banks);
//


//        //печать регистрационного кода по бик-у из цбрф:
//        System.out.println();
//        String stringBicToRegNumber = wscoClient.bicToRegNumber(getEnvelopBicToRegNumber("040173745"));
//        System.out.println("regNumber=" + stringBicToRegNumber);
//        //конвертация в объект
//        getBicToRegNumberResponse(stringBicToRegNumber);

//        //печать внутреннего кода по бик-у из цбрф (по первому банку):
//        String stringBicToIntCode = wscoClient.bicToIntCode(getEnvelopBicToIntCode("040173745"));
//        System.out.println("intCode=" + stringBicToIntCode);
//        //конвертация в обект
//        getBicToIntCodeResponse(stringBicToIntCode);

        //печать информации о кредитной организации от ЦБРФ по внутреннему коду (intCode)
        String creditInfoXMLResponse = wscoClient.creditInfoByIntCode(getEnvelopCreditInfoByIntCode("350000004"));
        System.out.println("CreditInfoXMLResponse: " + creditInfoXMLResponse);
        //конвертация полученного soap ответа в объект и доступ к его полям
        getCreditInfoByIntCode(creditInfoXMLResponse);


        //Информация о кредитной организации от ЦБРФ по внутреннему коду (intCode)
//        //запрос кредитной информации:
//        EnvelopCreditInfoByIntCodeRequest envelop = getEnvelopCreditInfoByIntCode("350000004");
//        //преобразование объекта в XML и печать:
//        //просмотр созданного объекта через XML:
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(EnvelopCreditInfoByIntCodeRequest.class);
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            StringWriter sw = new StringWriter();
//            marshaller.marshal(envelop, sw);
//            String xmlString = sw.toString();
//            System.out.println("..xmlString = " + xmlString);
//
//        } catch (JAXBException ex) {
//            System.out.println(ex.toString());
//        }


    }


    //KB-10053
    //конвертация полученого ответа (Информация о кредитной организации) в java объект
    private void getCreditInfoByIntCode(String stringXml) {
        CO creditInfo = new CO();
        try {
            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(stringXml.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(CO.class).createUnmarshaller();
            Document document = message.getSOAPBody().extractContentAsDocument();

//            JAXBElement<CO> response1 = unmarshaller.unmarshal(document.getFirstChild(), CO.class);
//            creditInfo = response1.getValue();
//            System.out.println("creditInfo = " + creditInfo);

            JAXBElement<CO> response2 =
                    unmarshaller.unmarshal(document.getElementsByTagName("CO").item(0), CO.class);
            creditInfo = response2.getValue();
            System.out.println("creditInfo2 = " + creditInfo);

            Node node = document.getElementsByTagName("CO").item(0);

            System.out.println(document.getElementsByTagName("IntCode").item(0).getNodeName() + " = "
                    + document.getElementsByTagName("IntCode").item(0).getTextContent());
            System.out.println(document.getElementsByTagName("RegNumber").item(0).getNodeName() + " = "
                    + document.getElementsByTagName("RegNumber").item(0).getTextContent());
            System.out.println(document.getElementsByTagName("OrgName").item(0).getNodeName() + " = "
                    + document.getElementsByTagName("OrgName").item(0).getTextContent());

//            System.out.println("node.getNodeName() = " + node.getFirstChild().getNodeName());
//            System.out.println("node.getTextContent() = " + node.getFirstChild().getTextContent());

//            System.out.println("document.getFirstChild() = " + document.getFirstChild());
//            System.out.println("document.getElementsByTagName(CO) = " + document.getElementsByTagName("CO").item(0));

            System.out.println("creditInfo2.getRegNumber() = " + creditInfo.getRegNumber());

        } catch (IOException | SOAPException | JAXBException e) {
            e.printStackTrace();
        }
    }

    //KB-10053
    //конвертация полученого ответа (Внутренний код) в java объект
    private BicToIntCodeResponse getBicToIntCodeResponse(String stringBicToIntCode) {
        BicToIntCodeResponse value = new BicToIntCodeResponse();
        BicToIntCodeResponse value2 = new BicToIntCodeResponse();
        try {
            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(stringBicToIntCode.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(BicToIntCodeResponse.class).createUnmarshaller();
            Document document = message.getSOAPBody().extractContentAsDocument();

            JAXBElement<BicToIntCodeResponse> response = unmarshaller.unmarshal(document.getFirstChild(), BicToIntCodeResponse.class);
            value = response.getValue();
            System.out.println("value = " + value);

            JAXBElement<BicToIntCodeResponse> response2 =
                    unmarshaller.unmarshal(document.getElementsByTagName("BicToIntCodeResponse").item(0), BicToIntCodeResponse.class);
            value2 = response2.getValue();
            System.out.println("value2 = " + value2);


            System.out.println("value.bicToIntCode = " + value.getBicToIntCodeResult());
            System.out.println("value2.bicToIntCode = " + value2.getBicToIntCodeResult());
        } catch (IOException | SOAPException | JAXBException e) {
            e.printStackTrace();
        }
        return value;
    }

    //KB-10053
    //конвертация полученого ответа (Регистрационный номер) в java объект
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
