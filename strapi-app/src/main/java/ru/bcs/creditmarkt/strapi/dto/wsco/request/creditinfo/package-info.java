@XmlSchema(namespace = "http://www.w3.org/2003/05/soap-envelope",
        xmlns = {
                @XmlNs(prefix = "soap12",
                        namespaceURI="http://www.w3.org/2003/05/soap-envelope")
        }
)
package ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;