# Apigee - MTOM Support

## Use Case

A Company wants to be able to proxy a SOAP service that uses [MTOM](https://en.wikipedia.org/wiki/Message_Transmission_Optimization_Mechanism) to optimize binary data transfer through multipart attachments.

## Solution

This repository contains a sample proxy that exposes a REST-SOAP-REST service. The original proxy was generated using [this](https://alvinalexander.com/java/jwarehouse/axis2-1.3/modules/samples/mtom/resources/MTOMSample.wsdl.shtml) WSDL.
The proxy acts as a mirror, meaning the generated SOAP request will be returned to the client. The optimization from REST to SOAP+MTOM is implemented in a Java Callout Policy.

## GET /attachment
 
This resource will generate a SOAP envelope using MTOM to separate binary data into parts. It only supports two requests:

### Request with a binary attachment
Request:
```
GET /sample/attachment?contentType=application/octet-stream&binaryData=ZmZkZHZmdnZidg==&fileName=customFileName
``` 
Note: the binaryData must be base64 encoded. 
Response:
```
Content-Type: multipart/related; boundary=----=_Part_G4AgjAtRnE3DTj-xdAr0xU6mppuVCZmrNzj4i; type="application/xop+xml"; start="<e1e2c342-03ec-4899-bd6a-6eaa9592ac8f@apigee.com>"; start-info="text/xml; charset=utf-8"

------=_Part_G4AgjAtRnE3DTj-xdAr0xU6mppuVCZmrNzj4i
Content-Type: application/xop+xml; charset=UTF-8; type="text/xml"
Content-Transfer-Encoding: 8bit
Content-ID: 
<e1e2c342-03ec-4899-bd6a-6eaa9592ac8f@apigee.com>
    <s11:Envelope 
        xmlns:s11="http://schemas.xmlsoap.org/soap/envelope/">
        <s11:Body>
            <ns1:AttachmentRequest 
                xmlns:ns1="http://ws.apache.org/axis2/mtomsample/">
                <!-- optional -->
                <ns1:fileName>coso</ns1:fileName>
                <!-- optional -->
                <ns1:binaryData>
                    <xop:Include 
                        xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:7c6147c6-0343-428b-ab85-fd685ff220b0@apigee.com"/>
                    </ns1:binaryData>
                </ns1:AttachmentRequest>
            </s11:Body>
        </s11:Envelope>
            
        
------=_Part_G4AgjAtRnE3DTj-xdAr0xU6mppuVCZmrNzj4i
Content-Type: application/octet-stream
Content-Transfer-Encoding: binary
Content-ID: 
        <7c6147c6-0343-428b-ab85-fd685ff220b0@apigee.com>

ffddvfvvbv
------=_Part_G4AgjAtRnE3DTj-xdAr0xU6mppuVCZmrNzj4i--
```
### Request with Plain Text attachment (for readable tests)
Request:
```
GET /sample/attachment?contentType=text/plain&binaryData=randomtextdata&fileName=customFileName
```
Note: the binaryData doesn't need to be base64 encoded.
Response:
```
Content-Type: multipart/related; boundary=----=_Part_F00QIJ41JsSh8GR1BP0fCxTF8z9IPwypyWTehlM; type="application/xop+xml"; start="<13937deb-b6b7-449d-bf29-56b4a1a0c3ca@apigee.com>"; start-info="text/xml; charset=utf-8"

------=_Part_F00QIJ41JsSh8GR1BP0fCxTF8z9IPwypyWTehlM
Content-Type: application/xop+xml; charset=UTF-8; type="text/xml"
Content-Transfer-Encoding: 8bit
Content-ID: 
<13937deb-b6b7-449d-bf29-56b4a1a0c3ca@apigee.com>
    <s11:Envelope 
        xmlns:s11="http://schemas.xmlsoap.org/soap/envelope/">
        <s11:Body>
            <ns1:AttachmentRequest 
                xmlns:ns1="http://ws.apache.org/axis2/mtomsample/">
                <!-- optional -->
                <ns1:fileName>customFileName</ns1:fileName>
                <!-- optional -->
                <ns1:binaryData>
                    <xop:Include 
                        xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:6729e941-0cbf-4fe6-81d0-d6f75c05d71e@apigee.com"/>
                    </ns1:binaryData>
                </ns1:AttachmentRequest>
            </s11:Body>
        </s11:Envelope>
            
        
------=_Part_F00QIJ41JsSh8GR1BP0fCxTF8z9IPwypyWTehlM
Content-Type: text/plain; charset=UTF-8
Content-Transfer-Encoding: 8bit
Content-ID: 
        <6729e941-0cbf-4fe6-81d0-d6f75c05d71e@apigee.com>

randomTextData
------=_Part_F00QIJ41JsSh8GR1BP0fCxTF8z9IPwypyWTehlM--
```