package ru.bcs.creditmarkt.strapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.client.WscoClient;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.BicToIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.BodyIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.EnvelopBicToIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.BicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.BodyRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.EnvelopeBicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToIntCode;
import ru.bcs.creditmarkt.strapi.dto.wsco.soap.BicToIntCodeResponse;

import javax.xml.bind.*;
import javax.xml.soap.*;
import java.io.*;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
    private final StrapiClient strapiClient;
    private final WscoClient wscoClient;

    //функция для обновление данных в страпи из цбрф
    @Override
    public void sync() {
//
//        //создание объекта, имитация SOAP ответа
//        EnvelopBicToIntCodeResponse envelopBicToIntCodeResponse = new EnvelopBicToIntCodeResponse();
//        BodyIntCodeResponse bodyIntCodeResponse = new BodyIntCodeResponse();
//        BicToIntCodeResponse bicToIntCodeResponse = new BicToIntCodeResponse();
//        bicToIntCodeResponse.setBicToIntCodeResult("040173745");
//        bodyIntCodeResponse.setBicToIntCodeResponse(bicToIntCodeResponse);
//

//        //envelopBicToIntCodeResponse.setBodyIntCodeResponse(bodyIntCodeResponse);
//
//        //преобразование объекта в XML и печать:
//        //просмотр созданного объекта через XML:
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(EnvelopBicToIntCodeResponse.class);
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            StringWriter sw = new StringWriter();
//            marshaller.marshal(envelopBicToIntCodeResponse, sw);
//            String xmlString = sw.toString();
//            System.out.println("..xmlString = "+xmlString);
//
//            //попытка распечатать это обратно через объект:
//            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, new ByteArrayInputStream(xmlString.getBytes()));
//            Unmarshaller unmarshaller = JAXBContext.newInstance(EnvelopBicToIntCodeResponse.class).createUnmarshaller();
//            EnvelopBicToIntCodeResponse env = (EnvelopBicToIntCodeResponse)unmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
//            System.out.println("env = " + env);
//
//        } catch (JAXBException | SOAPException | IOException ex) {
//        System.out.println(ex.toString());
//    }



//
//        //получение банков из страпи, у которых одинаковые даты создания и обновления:
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        List<Bank> banksForUpdate = strapiClient.getBanks().stream()
//                .filter(bank -> formatter.format(bank.getCreated_at()).equals(formatter.format(bank.getUpdated_at())))
//                .collect(Collectors.toList());
//        System.out.println("banks:" + banksForUpdate);
//        System.out.println("banks.size:" + banksForUpdate.size());
//
//        //получение бик кодов из цбрф:
//        BicCode bicCode = wscoClient.getCreditOrganizations();
//        System.out.println(bicCode);
//
//        List<CreditOrganization> creditOrganizations = bicCode.getCreditOrganizations();
//
//        //распечатывание первого банка (имя, бик):
//        System.out.println("getShortName = "
//                + creditOrganizations.get(0).getShortName()
//                + " getBic= "
//                + creditOrganizations.get(0).getBic());

//        //печать SOAP ответа на запрос регистрационного номера по бик-у (по первому из полученного выше списка):
//        System.out.println("regNo=" + wscoClient.bicToRegNumber(getEnvelopBicToRegNumber("040173745")));

        //печать внутреннего кода по бик-у из цбрф (по первому банку):
        String stringBicToIntCode = wscoClient.bicToIntCode(getEnvelopBicToIntCode("040173745"));
        System.out.println("intCode=" + stringBicToIntCode);

//        //объект envelop - объект EnvelopBicToIntCodeResponse - внутренний код по бик-у - SOAP ответ через объект EnvelopBicToIntCodeResponse
//        System.out.println("envelop:" + wscoClient.bicToIntCode(getEnvelopBicToIntCode("040173745")));




//        try {
//            XMLInputFactory xif = XMLInputFactory.newFactory();
//            XMLStreamReader xsr = xif.createXMLStreamReader(new ByteArrayInputStream(stringBicToIntCode.getBytes()));
//            xsr.nextTag(); // Advance to Envelope tag
//            xsr.nextTag(); // Advance to Body tag
//            xsr.nextTag(); // Advance to getNumberResponse tag
//            System.out.println(xsr.getNamespaceContext().getNamespaceURI("http://web.cbr.ru/"));
//
//            JAXBContext jc = JAXBContext.newInstance(BodyIntCodeResponse.class);
//            Unmarshaller unmarshaller = jc.createUnmarshaller();
//            JAXBElement<BodyIntCodeResponse> je = unmarshaller.unmarshal(xsr, BodyIntCodeResponse.class);
//            System.out.println(je.getName());
//            System.out.println(je.getValue());
//
//        } catch (XMLStreamException | JAXBException e) {
//            e.printStackTrace();
//        }





        try {
//            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, new ByteArrayInputStream(stringBicToIntCode.getBytes()));
//            Unmarshaller unmarshaller = JAXBContext.newInstance(BicToIntCodeResponse.class).createUnmarshaller();
//            BicToIntCodeResponse response = (BicToIntCodeResponse) unmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
//            System.out.println("BicToIntCodeResponse = " + response);

            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(stringBicToIntCode.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(BicToIntCodeResponse.class).createUnmarshaller();
            Document document = message.getSOAPBody().extractContentAsDocument();
            JAXBElement<BicToIntCodeResponse> response = unmarshaller.unmarshal(document.getFirstChild(), BicToIntCodeResponse.class);
            BicToIntCodeResponse value = response.getValue();
            System.out.println("bicToIntCode = " +value.getBicToIntCodeResult());

        } catch (JAXBException | IOException | SOAPException e) {
            e.printStackTrace();
        }








//        //попытка распечатать SOAP(Xml) ответ через объект:
//        try {
//            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, new ByteArrayInputStream(stringBicToIntCode.getBytes()));
//            Unmarshaller unmarshaller = JAXBContext.newInstance(EnvelopBicToIntCodeResponse.class).createUnmarshaller();
//            EnvelopBicToIntCodeResponse env = (EnvelopBicToIntCodeResponse)unmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
//            System.out.println("env = " + env);
//        } catch (SOAPException | JAXBException | IOException e) {
//            e.printStackTrace();
//        }


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
