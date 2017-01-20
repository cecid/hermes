<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/message_history">
    

<!-- Style Sheet for text area -->
<style type="text/css">
textarea.special {
font-size:12;
background-color:#C0C0C0;
}
</style>

<!-- Java Script for download the message -->
<script>
function viewMessage(mid, mbox) {
    document.viewMessageForm.message_id.value=mid;
    document.viewMessageForm.message_box.value=mbox;
    document.viewMessageForm.submit();
}

function showMDN(omid, ombox, reftype) {
    document.showMDNForm.original_message_id.value=omid;
    document.showMDNForm.original_message_box.value=ombox;
    document.showMDNForm.ref_to_message_type.value=reftype;
    document.showMDNForm.submit();
}

function showMessage(mid, mbox) {
	document.showMessageForm.message_id.value=mid;
	document.showMessageForm.message_box.value=mbox;
	document.showMessageForm.submit();
}

function showResendAsNewMessage(pmid) {
	document.showResendMessageForm.primal_message_id.value=pmid;
	document.showResendMessageForm.submit();
}
</script>

<br/>

<!-- search form - start -->
<table border="0" cellpadding="2" cellspacing="2" width="100%">
    <tr><td align="center" bgcolor="#6699ff"><font color="white"><b>Search Message(s)</b></font></td></tr>
    <tr><td bgcolor="#6699ff"/></tr>
</table>

<form name="messageSearchForm" method="post" action="message_history">
    <table border="0" cellpadding="2" cellspacing="2" width="100%">  
      <tr>
        <td width="20%">Message ID</td>
        <td width="30%">
            <input type="text">
                <xsl:attribute name="name">message_id</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="/message_history/search_criteria/message_id" /></xsl:attribute>
            </input>
        </td>
        <td width="20%">CPA ID</td>
        <td width="30%">
            <input type="text">
                <xsl:attribute name="name">cpa_id</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="/message_history/search_criteria/cpa_id" /></xsl:attribute>
            </input>
        </td>
	  </tr>        
      <tr>
        <td width="20%">Service</td>
        <td width="30%">
            <input type="text">
                <xsl:attribute name="name">service</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="/message_history/search_criteria/service" /></xsl:attribute>
            </input>
        </td>
        <td width="20%">Action</td>
        <td width="30%">
            <input type="text">
                <xsl:attribute name="name">action</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="/message_history/search_criteria/action" /></xsl:attribute>
            </input>
        </td>
	  </tr>
	  <tr>
        <td width="20%">Conversation ID</td>
        <td width="30%">
            <input type="text">
                <xsl:attribute name="name">conv_id</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="/message_history/search_criteria/conv_id" /></xsl:attribute>
            </input>
        </td>
        <td width="20%">Message Box</td>
        <td width="30%">
            Inbox
            <input type="radio">
                <xsl:attribute name="name">message_box</xsl:attribute>
                <xsl:attribute name="value">inbox</xsl:attribute>
                <xsl:if test="'inbox'=/message_history/search_criteria/message_box">
                    <xsl:attribute name="CHECKED"></xsl:attribute>
                </xsl:if>                
            </input>
            Outbox
            <input type="radio">
                <xsl:attribute name="name">message_box</xsl:attribute>
                <xsl:attribute name="value">outbox</xsl:attribute>
                <xsl:if test="'outbox'=/message_history/search_criteria/message_box">
                    <xsl:attribute name="CHECKED"></xsl:attribute>
                </xsl:if>  
            </input>
            All
            <input type="radio">
                <xsl:attribute name="name">message_box</xsl:attribute>
                <xsl:attribute name="value"></xsl:attribute>
                <xsl:if test="''=/message_history/search_criteria/message_box">
                    <xsl:attribute name="CHECKED"></xsl:attribute>
                </xsl:if>
                <xsl:if test="''=/message_history/search_criteria/message_box">
                    <xsl:attribute name="CHECKED"></xsl:attribute>
                </xsl:if>  
            </input>
        </td>
      </tr>
      <tr>
        <td width="20%">Status</td>
        <td width="30%">
            <select name="status">
                <xsl:element name="option">
                    <xsl:attribute name="value">PD</xsl:attribute>
                    <xsl:if test="'PD'=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>Pending
                </xsl:element>
                <xsl:element name="option">
                    <xsl:attribute name="value">PR</xsl:attribute>
                    <xsl:if test="'PR'=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>Processing   
                </xsl:element>
                <xsl:element name="option">
                    <xsl:attribute name="value">DL</xsl:attribute>
                    <xsl:if test="'DL'=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>Delivered    
                </xsl:element>
                <xsl:element name="option">
                    <xsl:attribute name="value">DF</xsl:attribute>
                    <xsl:if test="'DF'=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>Delivery Failure    
                </xsl:element>
                <xsl:element name="option">
                    <xsl:attribute name="value">PS</xsl:attribute>
                    <xsl:if test="'PS'=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>Processed
                </xsl:element>
                <xsl:element name="option">
                    <xsl:attribute name="value">PE</xsl:attribute>
                    <xsl:if test="'PE'=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>Processed Error
                </xsl:element>
                <xsl:element name="option">
                    <xsl:attribute name="value"></xsl:attribute>
                    <xsl:if test="''=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="''=/message_history/search_criteria/status">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>All    
                </xsl:element>
            </select>
        </td>              
        <td width="20%">Show Detail?</td>
        <td width="30%">
            Yes
            <input type="radio">
                <xsl:attribute name="name">is_detail</xsl:attribute>
                <xsl:attribute name="value">true</xsl:attribute>     
                <xsl:if test="'true'=/message_history/search_criteria/is_detail">
                    <xsl:attribute name="CHECKED"></xsl:attribute>
                </xsl:if>       
            </input>
            No
            <input type="radio">
                <xsl:attribute name="name">is_detail</xsl:attribute>
                <xsl:attribute name="value">false</xsl:attribute>
                <xsl:if test="'false'=/message_history/search_criteria/is_detail">
                    <xsl:attribute name="CHECKED"></xsl:attribute>
                </xsl:if>
                <xsl:if test="''=/message_history/search_criteria/is_detail">
                    <xsl:attribute name="CHECKED"></xsl:attribute>
                </xsl:if>
            </input>
        </td>
      </tr>
      <tr>
        <td width="20%">Number Of Messages</td>
        <td width="30%">
            <select name="num_of_messages">
                <xsl:element name="option">
                    <xsl:if test="'20'=/message_history/search_criteria/num_of_messages">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="''=/message_history/search_criteria/num_of_messages">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>20    
                </xsl:element>
                <xsl:element name="option">
                    <xsl:if test="'50'=/message_history/search_criteria/num_of_messages">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>50    
                </xsl:element>
                <xsl:element name="option">
                    <xsl:if test="'100'=/message_history/search_criteria/num_of_messages">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>100    
                </xsl:element>
                <xsl:element name="option">
                    <xsl:if test="'500'=/message_history/search_criteria/num_of_messages">
                        <xsl:attribute name="SELECTED"></xsl:attribute>
                    </xsl:if>500    
                </xsl:element>
            </select>
        </td>        
        <td width="20%">Messages for the Last</td>
        <td width="30%">        	
        	<select name="message_time">
        		<xsl:element name="option">
        			<xsl:attribute name="value"></xsl:attribute>
        			<xsl:if test="'all'=/message_history/search_criteria/message_time">
        				<xsl:attribute name="SELECTED"></xsl:attribute>
        			</xsl:if>
        			<xsl:if test="''=/message_history/search_criteria/message_time">
        				<xsl:attribute name="SELECTED"></xsl:attribute>
        			</xsl:if>All
        		</xsl:element>
        		<xsl:element name="option">
        			<xsl:attribute name="value">1</xsl:attribute>
        			<xsl:if test="'1'=/message_history/search_criteria/message_time">
        				<xsl:attribute name="SELECTED"></xsl:attribute>
        			</xsl:if>One Month
        		</xsl:element>
        		<xsl:element name="option">
        			<xsl:attribute name="value">3</xsl:attribute>
        			<xsl:if test="'3'=/message_history/search_criteria/message_time">
        				<xsl:attribute name="SELECTED"></xsl:attribute>
        			</xsl:if>Three Months
        		</xsl:element>
        		<xsl:element name="option">
        			<xsl:attribute name="value">6</xsl:attribute>
        			<xsl:if test="'6'=/message_history/search_criteria/message_time">
        				<xsl:attribute name="SELECTED"></xsl:attribute>
        			</xsl:if>Six Months
        		</xsl:element>
        	</select>	
        </td>
      </tr>
      <tr>
        <td width="20%"></td>
        <td width="30%"></td>
        <td width="20%"></td>
        <td width="30%"><input type="Submit" name="action" value="search"/></td>        
      </tr> 
    </table>
</form>
<!-- search form - end --> 

<br/>

<table border="0" cellpadding="2" cellspacing="2" width="100%">
    <tr><td align="center" bgcolor="#6699ff"><font color="white"><b>Search Result:</b></font></td></tr>
    <tr><td bgcolor="#6699ff"/></tr>
</table>

<table border="0" cellpadding="2" cellspacing="2" width="100%">

  <tr>
    <td width="40%">Total Number of Messages</td>
    <td width="60%"><xsl:value-of select="/message_history/total_no_of_messages" /></td>
  </tr>
  
  <tr>
    <td width="40%">Number of Messages Returned</td>
    <td width="60%"><xsl:value-of select="count(message)" /></td>
  </tr>

</table>

<br/>

<br/>

<table border="0" cellpadding="2" cellspacing="2" width="100%">
<tr>
    <td align="left">
        <xsl:if test="number(/message_history/search_criteria/offset) - number(/message_history/search_criteria/num_of_messages) >= 0">
            <a>
              <xsl:attribute name="href">message_history?offset=<xsl:value-of select="number(/message_history/search_criteria/offset) - number(/message_history/search_criteria/num_of_messages)" />&amp;message_id=<xsl:value-of select="/message_history/search_criteria/message_id" />&amp;message_box=<xsl:value-of select="/message_history/search_criteria/message_box" />&amp;cpa_id=<xsl:value-of select="/message_history/search_criteria/cpa_id" />&amp;service=<xsl:value-of select="/message_history/search_criteria/service" />&amp;action=<xsl:value-of select="/message_history/search_criteria/action" />&amp;conv_id=<xsl:value-of select="/message_history/search_criteria/conv_id" />&amp;status=<xsl:value-of select="/message_history/search_criteria/status" />&amp;num_of_messages=<xsl:value-of select="/message_history/search_criteria/num_of_messages" />&amp;is_detail=<xsl:value-of select="/message_history/search_criteria/is_detail" />&amp;primal_message_id=<xsl:value-of select="/message_history/search_criteria/primal_message_id"/></xsl:attribute>
              <img border="0" src="?mode=raw&amp;pagelet=ebms.left_arrow" align="absmiddle"/>
              Previous Page
            </a>            
        </xsl:if>
    </td>
    <td align="right"> 
        <xsl:if test="(number(/message_history/search_criteria/offset) + number(/message_history/search_criteria/num_of_messages)) != number(/message_history/total_no_of_messages)">           
            <xsl:if test="count(message) = number(/message_history/search_criteria/num_of_messages)">
                <a>
                  <xsl:attribute name="href">message_history?offset=<xsl:value-of select="number(/message_history/search_criteria/offset) + count(message)" />&amp;message_id=<xsl:value-of select="/message_history/search_criteria/message_id" />&amp;message_box=<xsl:value-of select="/message_history/search_criteria/message_box" />&amp;cpa_id=<xsl:value-of select="/message_history/search_criteria/cpa_id" />&amp;service=<xsl:value-of select="/message_history/search_criteria/service" />&amp;action=<xsl:value-of select="/message_history/search_criteria/action" />&amp;conv_id=<xsl:value-of select="/message_history/search_criteria/conv_id" />&amp;status=<xsl:value-of select="/message_history/search_criteria/status" />&amp;num_of_messages=<xsl:value-of select="/message_history/search_criteria/num_of_messages" />&amp;is_detail=<xsl:value-of select="/message_history/search_criteria/is_detail" />&amp;primal_message_id=<xsl:value-of select="/message_history/search_criteria/primal_message_id"/></xsl:attribute>
                  Next Page
                  <img border="0" src="?mode=raw&amp;pagelet=ebms.right_arrow" align="absmiddle"/>
                </a>            
            </xsl:if>
        </xsl:if>
    </td>
</tr>
</table>

<form name="viewMessageForm" method="post" action="./repository">
    <input type="hidden" name="message_id" value="" />
    <input type="hidden" name="message_box" value="" />
</form> 

<form name="showMDNForm" method="post" action="./message_history">
    <input type="hidden" name="original_message_id" value="" />
    <input type="hidden" name="original_message_box" value="" />
    <input type="hidden" name="ref_to_message_type" value="" />
</form>

<!--
	use separate form instead of using "messageSearchForm" form,
	so no need to "clear" dirty fields in the form 
-->
<form name="showMessageForm" method="post" action="./message_history">
	<input type="hidden" name="message_id" value=""/>
	<input type="hidden" name="message_box" value=""/>
</form>

<form name="showResendMessageForm" method="post" action="./message_history">
	<input type="hidden" name="primal_message_id" value=""/>
</form>

<!-- Loop the message history - start-->
<table border="0" cellpadding="2" cellspacing="2" width="100%">          
  <xsl:for-each select="message">
  <tr>
    <th colspan="2"/>
  </tr>
  <tr>
    <td width="40%"><xsl:value-of select="number(/message_history/search_criteria/offset) + position()" /></td>
    <td width="60%" align="right"><a href="#">Top</a></td>
  </tr>
  <tr>
    <td width="40%"><b>Message ID</b></td>
    <td width="60%"><b>
        <xsl:value-of select="./message_id" /><font> - </font>
        <a href="#1" title="Click here to download the message">
            <xsl:attribute name="onclick">viewMessage('<xsl:value-of select="./message_id" />','<xsl:value-of select="./message_box" />')</xsl:attribute>
            <img border="0" src="?mode=raw&amp;pagelet=ebms.download_arrow" align="absmiddle"/>
        </a>
    </b></td>
  </tr>
  <tr>
    <td width="40%"><b>Message Box</b></td>
    <td width="60%"><b><xsl:value-of select="./message_box" /></b></td>
  </tr>
  <xsl:if test="./ref_to_message_id != ''">
      <tr>
        <td width="40%">Ref To Message ID</td>
        <td width="60%"><xsl:value-of select="./ref_to_message_id" /></td>
      </tr>
  </xsl:if>
  <tr>
    <td width="40%">CPA ID</td>
    <td width="60%"><xsl:value-of select="./cpa_id" /></td>
  </tr>
  <tr>
    <td width="40%">Service</td>
    <td width="60%"><xsl:value-of select="./service" /></td>
  </tr>
  <tr>
    <td width="40%">Action</td>
    <td width="60%"><xsl:value-of select="./action" /></td>
  </tr>
  <tr>
    <td width="40%">Conversation ID</td>
    <td width="60%"><xsl:value-of select="./conv_id" /></td>
  </tr>
  <xsl:if test="./from_party_id != ''">
      <tr>
        <td width="40%">From Party ID</td>
        <td width="60%"><xsl:value-of select="./from_party_id" /></td>
      </tr>
  </xsl:if> 
    <xsl:if test="./from_party_type != ''">
        <tr>
          <td width="40%">From Party Type</td>
          <td width="60%"><xsl:value-of select="./from_party_type" /></td>
        </tr>
  </xsl:if> 
  <xsl:if test="./to_party_id != ''">
      <tr>
        <td width="40%">To Party ID</td>
        <td width="60%"><xsl:value-of select="./to_party_id" /></td>
      </tr>
  </xsl:if>
    <xsl:if test="./to_party_type != ''">
        <tr>
          <td width="40%">To Party Type</td>
          <td width="60%"><xsl:value-of select="./to_party_type" /></td>
        </tr>
  </xsl:if>
  <xsl:if test="./ack_requested != ''">
      <tr>
        <td width="40%">Acknowledgement Requested</td>
        <td width="60%"><xsl:value-of select="./ack_requested" /></td>
      </tr>
  </xsl:if>
  <xsl:if test="./ack_sign_requested != ''">
      <tr>
        <td width="40%">Acknowledgement Sign Requested</td>
        <td width="60%"><xsl:value-of select="./ack_sign_requested" /></td>
      </tr>
  </xsl:if>
  <xsl:if test="./sequence_group != ''">
      <tr>
        <td width="40%">Sequence Group</td>
        <td width="60%"><xsl:value-of select="./sequence_group" /></td>
      </tr>
  </xsl:if>
  <xsl:if test="./sequence_no != ''">
      <tr>
        <td width="40%">Sequence Number</td>
        <td width="60%"><xsl:value-of select="./sequence_no" /></td>
      </tr>
  </xsl:if>
  <xsl:if test="./sequence_status != ''">
      <tr>
        <td width="40%">Sequence Status</td>
        <td width="60%"><xsl:value-of select="./sequence_status" /></td>
      </tr>
  </xsl:if>
  <xsl:if test="./ack_requested = 'true'">
      <tr>
        <td width="40%">Is Acknowledged</td>
        <td width="60%">
            <xsl:if test="./message_box = 'outbox'">
                <xsl:if test="./status = 'PS'">
                    <a href="#1" title="Click here to show the acknowledgement">   
                        <xsl:attribute name="onclick">showMDN('<xsl:value-of select="./message_id" />','<xsl:value-of select="./message_box" />','Acknowledgement')</xsl:attribute>
                        <font color="blue">Positive Acknowledgement</font>
                    </a>
                </xsl:if>
                <xsl:if test="./status = 'PE'">
                    <a href="#1" title="Click here to show the acknowledgement">   
                        <xsl:attribute name="onclick">showMDN('<xsl:value-of select="./message_id" />','<xsl:value-of select="./message_box" />','Error')</xsl:attribute>
                        <font color="red">Negative Acknowledgement</font>
                    </a>
                </xsl:if> 
                <xsl:if test="./status != 'PS'">
                    <xsl:if test="./status != 'PE'">false</xsl:if>
                </xsl:if>
            </xsl:if>
            <xsl:if test="./message_box = 'inbox'">
                <xsl:if test="./status = 'PS'">
                    <a href="#1" title="Click here to show the acknowledgement">   
                        <xsl:attribute name="onclick">showMDN('<xsl:value-of select="./message_id" />','<xsl:value-of select="./message_box" />','Acknowledgement')</xsl:attribute>
                        <font color="blue">Positive Acknowledgement</font>
                    </a>
                </xsl:if>
                <xsl:if test="./status = 'DL'">
                    <a href="#1" title="Click here to show the acknowledgement">   
                        <xsl:attribute name="onclick">showMDN('<xsl:value-of select="./message_id" />','<xsl:value-of select="./message_box" />','Acknowledgement')</xsl:attribute>
                        <font color="blue">Positive Acknowledgement</font>
                    </a>
                </xsl:if>
                <xsl:if test="./status != 'PS'">
                    <xsl:if test="./status != 'DL'">false</xsl:if>
                </xsl:if>
            </xsl:if>
        </td>
      </tr>
  </xsl:if>  
  <xsl:if test="./ack_requested = 'false'">
    <xsl:if test="./status = 'failed'">
      <tr>
        <td width="40%">Warning !</td>
        <td width="60%">
            <a href="#1" title="Click here to show the acknowledgement">   
                <xsl:attribute name="onclick">showMDN('<xsl:value-of select="./message_id" />','<xsl:value-of select="./message_box" />','Error')</xsl:attribute>
                <font color="red">Negative Acknowledgement</font>
            </a>
        </td>
      </tr>
    </xsl:if>
  </xsl:if>
  <tr>
    <td width="40%">Timestamp</td>
    <td width="60%"><xsl:value-of select="./time_stamp" /></td>
  </tr>
  <xsl:if test="./primal_message_id != ''">
  <tr>
    <td width="40%">Primal Message ID<br /> <small><font color="gray">Message that triggers [Resend as New]</font></small></td>
    <td width="60%">                        
      <a href="#1" title="Click here to show the primal message">
        <xsl:attribute name="onclick">showMessage('<xsl:value-of select="./primal_message_id" />','outbox')</xsl:attribute>
        <xsl:value-of select="./primal_message_id" />
	  </a>
	</td>
  </tr>
  </xsl:if>
  <xsl:if test="./has_resend_as_new = 'true'">
  <tr>
    <td width="40%">Has Resend As New</td>
    <td width="60%">                        
      <a href="#1" title="Click here to show the &quot;Resent As New&quot; messages">
        <xsl:attribute name="onclick">showResendAsNewMessage('<xsl:value-of select="./message_id" />')</xsl:attribute>
        Click here to show the "Resend As New" messages
	  </a>
	</td>
  </tr>
  </xsl:if>
  <tr>
    <td width="40%">Status</td>
    <td width="60%">
        <xsl:if test="./status = 'PD'">
            Pending
        </xsl:if>
        <xsl:if test="./status = 'PR'">
            Processing
        </xsl:if>    
        <xsl:if test="./status = 'PS'">
        	Processed  
            <xsl:if test="(./message_box = 'outbox') and (./message_type = 'Order')">
				<form style="display: inline" name="resendAsNewForm" method="post" action="./resend_as_new">				
	                <input type="hidden" name="primal_message_id">
	                    <xsl:attribute name="value">
	                        <xsl:value-of select="./message_id" />
	                    </xsl:attribute>
	                 </input>
	                <input type="submit" name="submit" value="Resend as new" />
	            </form>
            </xsl:if>
        </xsl:if>
        <xsl:if test="./status = 'PE'">
        	Processed Error
			<xsl:if test="(./message_box = 'outbox') and (./message_type = 'Order')">
				<form style="display: inline" name="resendAsNewForm" method="post" action="./resend_as_new">
	                <input type="hidden" name="primal_message_id">
	                    <xsl:attribute name="value">
	                        <xsl:value-of select="./message_id" />
	                    </xsl:attribute>
	                 </input>
	                <input type="submit" name="submit" value="Resend as new" />
	            </form>
            </xsl:if>
        </xsl:if>
        <xsl:if test="./status = 'DL'">
        	Delivered
        	<xsl:if test="(./message_box = 'outbox') and (./message_type = 'Order')">
				<form style="display: inline" name="resendAsNewForm" method="post" action="./resend_as_new">
	                <input type="hidden" name="primal_message_id">
	                    <xsl:attribute name="value">
	                        <xsl:value-of select="./message_id" />
	                    </xsl:attribute>
	                 </input>
	                <input type="submit" name="submit" value="Resend as new" />
	            </form>
            </xsl:if>
        </xsl:if>
        <xsl:if test="./status = 'DF'">
        	Delivery Failure
        	<xsl:if test="(./message_box = 'outbox') and (./message_type = 'Order')">
            	<form style="display: inline" name="changeMessageStatusForm" method="post" action="./change_message_status" onSubmit="return confirm('Are you sure to retry this message?\r\nTo know more, please refer to\r\nAdministration Tool User Guide [Section 5.4.1]');">
                	<input type="hidden" name="message_id">
                    	<xsl:attribute name="value">
                        	<xsl:value-of select="./message_id" />
                    	</xsl:attribute>
                 	</input>
                	<input type="submit" name="submit" value="Retry" />
            	</form>            
				<form style="display: inline" name="resendAsNewForm" method="post" action="./resend_as_new">
	                <input type="hidden" name="primal_message_id">
	                    <xsl:attribute name="value">
	                        <xsl:value-of select="./message_id" />
	                    </xsl:attribute>
	                 </input>
	                <input type="submit" name="submit" value="Resend as new" />
	            </form>
            </xsl:if>
        </xsl:if>
    </td>
  </tr>
  <xsl:if test="./status_description != ''">
      <tr>
        <td width="40%">Status Description</td>
        <td width="60%"><textarea class="special" rows="5" cols="55" wrap="HARD"><xsl:attribute name="readonly"></xsl:attribute><xsl:value-of select="./status_description" /></textarea></td>
      </tr>
  </xsl:if>
  
</xsl:for-each>
</table>
  
  <!-- Loop the message history - end-->
<br/>

<table border="0" cellpadding="2" cellspacing="2" width="100%">
<tr>
    <td align="left">
        <xsl:if test="number(/message_history/search_criteria/offset) - number(/message_history/search_criteria/num_of_messages) >= 0">
            <a>
              <xsl:attribute name="href">message_history?offset=<xsl:value-of select="number(/message_history/search_criteria/offset) - number(/message_history/search_criteria/num_of_messages)" />&amp;message_id=<xsl:value-of select="/message_history/search_criteria/message_id" />&amp;message_box=<xsl:value-of select="/message_history/search_criteria/message_box" />&amp;cpa_id=<xsl:value-of select="/message_history/search_criteria/cpa_id" />&amp;service=<xsl:value-of select="/message_history/search_criteria/service" />&amp;action=<xsl:value-of select="/message_history/search_criteria/action" />&amp;conv_id=<xsl:value-of select="/message_history/search_criteria/conv_id" />&amp;status=<xsl:value-of select="/message_history/search_criteria/status" />&amp;num_of_messages=<xsl:value-of select="/message_history/search_criteria/num_of_messages" />&amp;is_detail=<xsl:value-of select="/message_history/search_criteria/is_detail" />&amp;primal_message_id=<xsl:value-of select="/message_history/search_criteria/primal_message_id"/></xsl:attribute>
              <img border="0" src="?mode=raw&amp;pagelet=ebms.left_arrow" align="absmiddle"/>
              Previous Page
            </a>            
        </xsl:if>
    </td>
    <td align="right"> 
        <xsl:if test="(number(/message_history/search_criteria/offset) + number(/message_history/search_criteria/num_of_messages)) != number(/message_history/total_no_of_messages)">           
            <xsl:if test="count(message) = number(/message_history/search_criteria/num_of_messages)">
                <a>
                  <xsl:attribute name="href">message_history?offset=<xsl:value-of select="number(/message_history/search_criteria/offset) + count(message)" />&amp;message_id=<xsl:value-of select="/message_history/search_criteria/message_id" />&amp;message_box=<xsl:value-of select="/message_history/search_criteria/message_box" />&amp;cpa_id=<xsl:value-of select="/message_history/search_criteria/cpa_id" />&amp;service=<xsl:value-of select="/message_history/search_criteria/service" />&amp;action=<xsl:value-of select="/message_history/search_criteria/action" />&amp;conv_id=<xsl:value-of select="/message_history/search_criteria/conv_id" />&amp;status=<xsl:value-of select="/message_history/search_criteria/status" />&amp;num_of_messages=<xsl:value-of select="/message_history/search_criteria/num_of_messages" />&amp;is_detail=<xsl:value-of select="/message_history/search_criteria/is_detail" />&amp;primal_message_id=<xsl:value-of select="/message_history/search_criteria/primal_message_id"/></xsl:attribute>
                  Next Page
                  <img border="0" src="?mode=raw&amp;pagelet=ebms.right_arrow" align="absmiddle"/>
                </a>       
            </xsl:if>
        </xsl:if>
    </td>
</tr>
</table>

</xsl:template>

<xsl:template match="/error">

<b>Error occurs</b>
<p>Operation : <xsl:value-of select="./operation" /></p>

<p>Exception :</p>
<pre><xsl:value-of select="./exception_message"/></pre>
</xsl:template>
</xsl:stylesheet>
