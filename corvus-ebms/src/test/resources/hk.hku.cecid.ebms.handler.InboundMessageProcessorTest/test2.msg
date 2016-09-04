POST /corvus/httpd/ebms/inbound HTTP/1.1
Content-Type: multipart/related; type="text/xml"; boundary="----=_Part_10_15889049.1216625212140"
SOAPAction: "ebXML"
Content-Length: 3390
Cache-Control: no-cache
Pragma: no-cache
User-Agent: Java/1.5.0_13
Host: localhost:8081
Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
Connection: keep-alive

------=_Part_10_15889049.1216625212140
Content-Type: text/xml; charset=UTF-8
Content-Id: <soappart>

<?xml version="1.0" encoding="UTF-8"?><SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://schemas.xmlsoap.org/soap/envelope/ http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd"><SOAP-ENV:Header xsi:schemaLocation="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd"><eb:MessageHeader xmlns:eb="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" SOAP-ENV:mustUnderstand="1" eb:version="2.0"><eb:From><eb:PartyId eb:type="fromPartyType">fromPartyId</eb:PartyId></eb:From><eb:To><eb:PartyId eb:type="toPartyType">toPartyId</eb:PartyId></eb:To><eb:CPAId>cpaid</eb:CPAId><eb:ConversationId>convId</eb:ConversationId><eb:Service>http://localhost:8080/corvus/httpd/ebms/inbound</eb:Service><eb:Action>action</eb:Action><eb:MessageData><eb:MessageId>20080721-152652-07404@192.168.0.51</eb:MessageId><eb:Timestamp>2008-07-21T15:26:52.075+08:00</eb:Timestamp><eb:TimeToLive>2099-07-21T15:26:52.075+08:00</eb:TimeToLive></eb:MessageData></eb:MessageHeader></SOAP-ENV:Header><SOAP-ENV:Body xsi:schemaLocation="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd"><eb:Manifest xmlns:eb="http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" eb:version="2.0"><eb:Reference eb:id="Payload-0" xlink:href="cid:Payload-0" xlink:type="simple"/></eb:Manifest></SOAP-ENV:Body></SOAP-ENV:Envelope>
------=_Part_10_15889049.1216625212140
Content-Type: application/octream
Content-Id: <Payload-0>

This is an sample message.

                  :#+,                                               
                 +'++                                                
               ,++'+                                                 
              ++'+#`                                                 
            ;+''++             `           `           `             
           #++''+;`        `++++` ``,:;::   `,::::  ++  ;+'++;       
         ;++''+++         '++++#` `;:;;;;  `;;;;;;  ++  ;'+++++:     
        +'''''++:``;;;:   +#`     ,;;     `::,      ++  ;+   `+#     
       +''''''''  ;:;;;: ;+:      ;;``     ;;`      ++  ;+`   ++`    
      ,+'+''''++ .:;:;;; ;+.     `;;,.,,, `;;`      ++  ;+    ;'.    
      ''+''''''+`,;;;::;`'+......`;::;;;;`.;;``..`.`+#``;+``..:+:`..`
      `++'''+'++ `;;;;;; ''`      :;.```` `:;       ++  ;+`   :'.    
       ;'''+'''+` ;::;;. :':`     ;; `     ;;       ++  ;+    +'`    
        :+'+'+''+  .,,` ` ++`     ::,````  ::,````  ++  ;+   .+#     
      `   #'+'''+`        ''++++` `;;;:;;  `;;:;;;  ++  ;'+++++,`    
           ,++'''#         .;;''`   .:::,`  `,:::,  ''  :'''';       
             +'+''; `                          `                     
             `,+''',                                                 
                '''+.                                                
                 `+++`                                               
                  :+:     
                  
This is an sample message.
------=_Part_10_15889049.1216625212140--
