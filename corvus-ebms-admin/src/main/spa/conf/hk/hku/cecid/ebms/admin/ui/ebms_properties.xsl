<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/ebms">

<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <td width="40%"><b>Ebms Plugin Properties</b></td>
    <td width="60%"></td>
  </tr>
</table>
<br/>
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <form name="ebmsPropertiesForm" method="post" action="ebms_properties" onSubmit="return confirm('Are you sure to update the ebms properties?');">
    
<!-- Outbox Collector -->

  <tr>
    <th colspan="2">Outbox Delivery Manager</th>
  </tr>    
  <tr>
    <th colspan="2"/>
  </tr>
  <tr>
    <td width="40%">Collection Interval (ms)</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/delivery_manager/outbox_delivery_manager/delivery_manager_interval</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./delivery_manager/outbox_delivery_manager/delivery_manager_interval" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Maximum Number of Threads</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/delivery_manager/outbox_delivery_manager/max_thread_count</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./delivery_manager/outbox_delivery_manager/max_thread_count" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Current Number of Threads</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">/ebms/delivery_manager/outbox_delivery_manager/current_thread_count</xsl:attribute>
            <xsl:attribute name="readonly"></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./delivery_manager/outbox_delivery_manager/current_thread_count" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Peek Number of Threads</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">/ebms/delivery_manager/outbox_delivery_manager/peek_thread_count</xsl:attribute>
            <xsl:attribute name="readonly"></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./delivery_manager/outbox_delivery_manager/peek_thread_count" /></xsl:attribute>
        </input>
    </td>
  </tr>
  
<!-- Inbox Collector -->
  
  <tr>
    <th colspan="2">Inbox Delivery Manager</th>
  </tr>    
  <tr>
    <th colspan="2"/>
  </tr>
  <tr>
    <td width="40%">Collection Interval (ms)</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/delivery_manager/inbox_delivery_manager/delivery_manager_interval</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./delivery_manager/inbox_delivery_manager/delivery_manager_interval" /></xsl:attribute>
        </input>
    </td>
  </tr>
  
<!-- Digital Signature -->  

  <tr>
    <th colspan="2">Digital Signature</th>
  </tr>    
  <tr>
    <th colspan="2"/>
  </tr>
  <tr>
    <td width="40%">Username</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/digital_signature/username</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./digital_signature/username" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
  <td width="40%">Password</td>
    <td width="60%">
        <input type="password">
            <xsl:attribute name="name">property:/ebms/digital_signature/password</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./digital_signature/password" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
  <td width="40%">Keystore Location</td>
    <td width="60%">
        <input type="text" size="50">
            <xsl:attribute name="name">property:/ebms/digital_signature/key_store_location</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./digital_signature/key_store_location" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Store Type</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/digital_signature/store_type</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./digital_signature/store_type" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Provider</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/digital_signature/provider</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./digital_signature/provider" /></xsl:attribute>
        </input>
    </td>
  </tr>
  
  <!-- Mail Setting -->
  
  <tr>
    <th colspan="2">SMTP Mail Setting</th>
  </tr>    
  <tr>
    <th colspan="2"/>
  </tr>
  <tr>
    <td width="40%">Host</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smtp/host</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smtp/host" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Protocol</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smtp/protocol</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smtp/protocol" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Port</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smtp/port</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smtp/port" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">From Mail Address</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smtp/from_mail_address</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smtp/from_mail_address" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">User Name</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smtp/username</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smtp/username" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Password</td>
    <td width="60%">
        <input type="password">
            <xsl:attribute name="name">property:/ebms/mail/smtp/password</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smtp/password" /></xsl:attribute>
        </input>
    </td>
  </tr>
  
<!-- POP Setting -->

  <tr>
    <th colspan="2">POP Mail Setting</th>
  </tr>    
  <tr>
    <th colspan="2"/>
  </tr>  
  <tr>
    <td width="40%">Host</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/pop/host</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/pop/host" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Protocol</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/pop/protocol</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/pop/protocol" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Port</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/pop/port</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/pop/port" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Folder</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/pop/folder</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/pop/folder" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Username</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/pop/username</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/pop/username" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Password</td>
    <td width="60%">
        <input type="password">
            <xsl:attribute name="name">property:/ebms/mail/pop/password</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/pop/password" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Polling Interval</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/pop/delivery_manager_interval</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/pop/delivery_manager_interval" /></xsl:attribute>
        </input>
    </td>
  </tr>
  
<!-- S/MIME Decryption -->
  
  <tr>
    <th colspan="2">S/MIME Decryption Setting</th>
  </tr>    
  <tr>
    <th colspan="2"/>
  </tr>
  <tr>
    <td width="40%">Keystore Location</td>
    <td width="60%">
        <input type="text" size="50">
            <xsl:attribute name="name">property:/ebms/mail/smime/decryption/key_store_location</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smime/decryption/key_store_location" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Keystore Password</td>
    <td width="60%">
        <input type="password">
            <xsl:attribute name="name">property:/ebms/mail/smime/decryption/key_store_password</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smime/decryption/key_store_password" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Keystore Alias</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smime/decryption/alias</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smime/decryption/alias" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Key Password</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smime/decryption/key_password</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smime/decryption/key_password" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Store Type</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smime/decryption/store_type</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smime/decryption/store_type" /></xsl:attribute>
        </input>
    </td>
  </tr>
  <tr>
    <td width="40%">Provider</td>
    <td width="60%">
        <input type="text">
            <xsl:attribute name="name">property:/ebms/mail/smime/decryption/provider</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./mail/smime/decryption/provider" /></xsl:attribute>
        </input>
    </td>
  </tr>
  
  <tr>
    <td width="40%"></td>
    <td width="60%"><input type="Submit" name="action" value="update"/></td>
  </tr>
  </form>
  
</table>

 
</xsl:template>
</xsl:stylesheet>