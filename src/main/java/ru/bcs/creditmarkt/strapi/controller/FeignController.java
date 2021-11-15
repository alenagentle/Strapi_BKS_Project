package ru.bcs.creditmarkt.strapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.client.WscoClient;
import ru.bcs.creditmarkt.strapi.dto.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.BicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.Body;
import ru.bcs.creditmarkt.strapi.dto.Envelope;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feign")
public class FeignController {

    private final StrapiClient strapiClient;
    private final WscoClient wscoClient;

    @GetMapping("/bank-branches")
    List<BankBranch> getBanks() {
        System.out.println("getBanks");
        return strapiClient.getBankBranches();
    }

    @GetMapping("/creditOrganizations")
    void getCreditOrganizations() {
        System.out.println("result: " + wscoClient.getCreditOrganizations());
    }

    @PostMapping("/licenseNo")
    void getLicenseNo() {
//        try {
//            System.out.println("try MessageFactory");
//            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//            SOAPMessage soapMessage = messageFactory.createMessage();
//            SOAPPart soapPart = soapMessage.getSOAPPart();
//            SOAPEnvelope envelope = soapPart.getEnvelope();
//
//            soapMessage.getSOAPPart().getEnvelope().setPrefix("soap12");
//            soapMessage.getSOAPPart().getEnvelope().removeNamespaceDeclaration("env");
//            soapMessage.getSOAPHeader().setPrefix("soap12");
//            soapMessage.getSOAPBody().setPrefix("soap12");
//
//            SOAPBody soapBody = envelope.getBody();
//            SOAPElement soapBodyElem = soapBody.addChildElement("BicToRegNumber", "", "http://web.cbr.ru/");
//
//            SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("BicCode");
//            soapBodyElem1.addTextNode("040173745");
//
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            soapMessage.writeTo(out);
//            String strMsg = out.toString();
//
//            System.out.println("soapMessage: " + strMsg);

//            JAXBContext jaxbContext = JAXBContext.newInstance(BicToRegNumber.class);
//            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//            StringWriter sw = new StringWriter();
//            BicToRegNumber licenseNoRequest = new BicToRegNumber();
//            licenseNoRequest.setBicCode("040173745");
//
//            jaxbMarshaller.marshal(soapMessage, sw);
//            String xmlString = sw.toString();
//            System.out.println("xmlString = " + xmlString);

//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.TEXT_XML);
//            HttpEntity<String> request = new HttpEntity<>(strMsg, headers);
//            String fooResourceUrl = "http://cbr.ru/CreditInfoWebServ/CreditOrgInfo.asmx";
//            System.out.println("restTemplate:" + restTemplate.postForEntity(fooResourceUrl, request, String.class));
//
//        } catch (SOAPException | IOException e) {
//            e.printStackTrace();
//        }


        Envelope envelope = new Envelope();
        Body body = new Body();
        BicToRegNumber bicToRegNumber = new BicToRegNumber();
        bicToRegNumber.setBicCode("044525225");
        body.setBicToRegNumber(bicToRegNumber);
        envelope.setBody(body);

        System.out.println("getLicenseNo: " + wscoClient.getLicenseNo(envelope));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<Envelope> request = new HttpEntity<>(envelope, headers);
        String fooResourceUrl = "http://cbr.ru/CreditInfoWebServ/CreditOrgInfo.asmx";
        System.out.println("restTemplate:" + restTemplate.postForEntity(fooResourceUrl, request, String.class));


//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            StringWriter sw = new StringWriter();
//            marshaller.marshal(envelope, sw);
//            String xmlString = sw.toString();
//            System.out.println(xmlString);
//            //wscoClient.getLicenseNo(xmlString);
//
//            //System.out.println("getLicenseNo: " + wscoClient.getLicenseNo(envelope));
//
//        } catch (JAXBException ex) {
//            System.out.println(ex.toString());
//        }








    }

}

