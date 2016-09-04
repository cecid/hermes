<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/properties">
<style type="text/css">
	label{
			display:block;
			font-size:7pt;
		}
</style>

<table border="0" cellpadding="2" cellspacing="2" width="100%">
  
  <xsl:if test="page_type/text() = 'coreProps'">
  <form name="housecleaningForm" method="post" action="?action=update_hc" onSubmit="return confirm('Are you sure to update the housecleaning settings?');">
  	<tr>
  		<th colspan="2">House Cleaning Settings</th>
  	</tr>
  	<tr>
  		<th colspan="2"></th>
  	</tr>
  	<tr>
  		<td>Activate housecleaning?</td>
  		<xsl:if test="hc/on/text() = 'true'">
  		<td>yes<input type="radio" name="hc:on" value="true" checked="checked"/>no<input type="radio" name="hc:on" value="false"/></td>
  		</xsl:if>
  		<xsl:if test="hc/on/text() != 'true'">
  		<td>yes<input type="radio" name="hc:on" value="true"/>no<input type="radio" name="hc:on" value="false" checked="checked"/></td>
  		</xsl:if>
  	</tr>

 	<tr>
 		<td>Send notification email?</td>
  	 	<td><label>email:</label><input type="text" name="hc:email" size="40"><xsl:attribute name="value"><xsl:value-of select="hc/email"/></xsl:attribute></input></td>
  	</tr>
  	
  	<tr>
  		<td></td>
  		<xsl:if test="required/text() = 'true'">
  		<td><label>smtp server:</label><input type="text" name="hc:smtp" MAXLENGTH="67" size="40"><xsl:attribute name="value"><xsl:value-of select="hc/smtp"/></xsl:attribute></input></td>
  		</xsl:if>
  		<xsl:if test="required/text() != 'true'">
  		<td><label><font color="red">*required</font> smtp server:</label><input type="text" name="hc:smtp" MAXLENGTH="67" size="40"><xsl:attribute name="value"><xsl:value-of select="hc/smtp"/></xsl:attribute></input></td>
  		</xsl:if>
  	</tr>
  	
  	<tr>
  		<td></td>
  		<td><label>username:</label><input type="text" name="hc:username" size="40"><xsl:attribute name="value"><xsl:value-of select="hc/username"/></xsl:attribute></input></td>
  	</tr>

    <tr>
  		<td></td>
  		<td><label>password:</label><input type="password" name="hc:password" size="40"><xsl:attribute name="value"><xsl:value-of select="hc/password"/></xsl:attribute></input></td>
  	</tr>
  	<xsl:if test="hc/lastrun/text() != ''">
  		<tr>
  			<td colspan="2"><i>The last time house cleaning was run was on <xsl:value-of select="hc/lastrun"/>.</i></td>
  		</tr>
  	</xsl:if>
  <xsl:if test="hc/on/text() = 'true'">
  <tr>
  	<td colspan="2"><i>The next house cleaning is scheduled for <xsl:value-of select="hc/nextrun"/>.</i></td>
  </tr>
  </xsl:if>
   <xsl:if test="hc/on/text() != 'true'">
  <tr>
  	<td colspan="2"><i>House cleaning is currently disabled.</i></td>
  </tr>
  </xsl:if>
  	
  	<tr>
  	<td></td>
  	<td><input type="submit" value="Update"/></td>
  	</tr>

  	<tr>
  		<th colspan="2">Application Properties</th>
  	</tr>
  	<tr>
  		<th colspan="2"></th>
  	</tr>
  	<tr>
  		<td colspan="2"></td>
  	</tr>
  	</form>
  </xsl:if>
  
  <form name="propertiesForm" method="post" action="?action=update" onSubmit="return confirm('Are you sure to update the properties?');">
  
  <tr>
    <th width="30%">Property Name</th>
    <th width="70%">Property Value</th>
  </tr>

  <xsl:for-each select="property">
  
  <tr>
    <td>
        <xsl:attribute name="title"><xsl:value-of select="./name" /></xsl:attribute>
        <input type="text" size="50" style="border: 1px solid #FFFFFF;" readonly="true">
          <xsl:attribute name="value"><xsl:value-of select="./name" /></xsl:attribute>
        </input>
    </td>
    <td>
        <input type="text" size="50">
          <xsl:attribute name="name"><xsl:value-of select="concat('property:',./name)" /></xsl:attribute>
          <xsl:attribute name="value"><xsl:value-of select="./value" /></xsl:attribute>
        </input>
    </td>
  </tr>
     </xsl:for-each>
  <tr>
    <td></td>
    <td><input type="submit" value="Update"/></td>
  </tr>
</form>

</table>
 
</xsl:template>
</xsl:stylesheet>