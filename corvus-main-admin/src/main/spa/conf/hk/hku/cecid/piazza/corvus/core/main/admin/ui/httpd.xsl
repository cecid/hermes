<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/httpd">

<form>
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <td width="30%">Status</td>
    <td width="70%"><xsl:value-of select="./status/state" /></td>
  </tr>
  <tr>
    <td>Current threads</td>
    <td><xsl:value-of select="./status/threads" /></td>
  </tr>
  <tr>
    <td>Number of stateful context listener</td>
    <td><xsl:value-of select="count(context-listeners/listener)" /></td>
  </tr>
  <tr>
    <td>Number of request filters</td>
    <td><xsl:value-of select="count(request-filters/listener)" /></td>
  </tr>
  <tr>
    <td>Number of request listeners</td>
    <td><xsl:value-of select="count(request-listeners/listener)" /></td>
  </tr>
</table>
<br/>
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <th colspan="2">Request Listener Details</th>
  </tr>
  <tr>
    <th width="30%">Context</th>
    <th width="70%">Listener</th>
  </tr>

  <xsl:for-each select="request-listeners/listener">
  <tr>
    <td><xsl:value-of select="./context" /></td>
    <td><xsl:value-of select="./listener" /></td>
  </tr>
  </xsl:for-each>

</table>

<p align="right">
<xsl:choose>
   <xsl:when test="./status/action='resume'">
      <input type="button" value="Resume">
      	<xsl:attribute name="onclick">if (confirm('Are you sure to resume the dispatcher?')) {document.location='./httpd?action=resume';}</xsl:attribute>
      </input>
   </xsl:when>
   <xsl:when test="./status/action='halt'">
      <input type="button" value="Halt">
      	<xsl:attribute name="onclick">if (confirm('Are you sure to halt the dispatcher?')) {document.location='./httpd?action=halt';}</xsl:attribute>
      </input>
   </xsl:when>
   <xsl:otherwise/>
</xsl:choose>
<xsl:text>  </xsl:text>
<input type="button" value="Refresh" onclick="document.location='./httpd'"/>
</p>

</form>
 
</xsl:template>
</xsl:stylesheet>