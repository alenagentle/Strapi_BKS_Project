package ru.bcs.creditmarkt.strapi.dto.wsco.response.creditinfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CO", propOrder = {
        "intCode", "regNumber", "bic", "orgName", "orgFullName", "phones", "dateKGRRegistration", "mainRegNumber",
        "mainDateReg", "ustavAdr", "factAdr", "ustMoney", "ssv_Date", "orgStatus", "isRBFileExist"
})
public class CO {

    @XmlElement(name = "IntCode")
    private String intCode;

    @XmlElement(name = "RegNumber")
    private String regNumber;

    @XmlElement(name = "BIC")
    private String bic;

    @XmlElement(name = "OrgName")
    private String orgName;

    @XmlElement(name = "OrgFullName")
    private String orgFullName;

    @XmlElement(name = "phones")
    private String phones;

    @XmlElement(name = "DateKGRRegistration")
    private String dateKGRRegistration;

    @XmlElement(name = "MainRegNumber")
    private String mainRegNumber;

    @XmlElement(name = "MainDateReg")
    private String mainDateReg;

    @XmlElement(name = "UstavAdr")
    private String ustavAdr;

    @XmlElement(name = "FactAdr")
    private String factAdr;

    @XmlElement(name = "UstMoney")
    private String ustMoney;

    @XmlElement(name = "SSV_Date")
    private String ssv_Date;

    @XmlElement(name = "OrgStatus")
    private String orgStatus;

    @XmlElement(name = "IsRBFileExist")
    private String isRBFileExist;


}
