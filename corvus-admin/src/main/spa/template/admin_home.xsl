<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/home">

<form>
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <td width="30%"><b>System Name</b></td>
    <td width="70%"><b><xsl:value-of select="./system/name" /></b></td>
  </tr>
  <tr>
    <td>System Version</td>
    <td><xsl:value-of select="./system/version" /></td>
  </tr>
  <tr>
    <td>Startup Time</td>
    <td><xsl:value-of select="./system/startup-time" /></td>
  </tr>
  <tr>
    <td>Current Time</td>
    <td><xsl:value-of select="./system/current-time" /></td>
  </tr>
  <tr>
    <td>System Up Time</td>
    <td><xsl:value-of select="./system/up-time" /></td>
  </tr>
  <tr>
    <td>Number of Processors</td>
    <td><xsl:value-of select="./system/processors" /></td>
  </tr>
</table>
<br/>
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <th colspan="2">Memory Summary</th>
  </tr>
  <tr>
    <th width="30%">Memory</th>
    <th width="70%">Size</th>
  </tr>
  <tr>
    <td>Available</td>
    <td><xsl:value-of select="./memory/max" /></td>
  </tr>
  <tr>
    <td>Allocated</td>
    <td><xsl:value-of select="./memory/total" /></td>
  </tr>
  <tr>
    <td>Used</td>
    <td><xsl:value-of select="./memory/used" /></td>
  </tr>
  <tr>
    <td>Free</td>
    <td><xsl:value-of select="./memory/free" /></td>
  </tr>
  <tr>
    <td><b>Usage</b></td>
    <td><b><xsl:value-of select="./memory/usage" /></b></td>
  </tr>
</table>
<br/>
<p align="right">
<table border="0" cellpadding="2" cellspacing="2">
<tr>
<td>
      <input type="button" value="Garbage Collection">
      	<xsl:attribute name="onclick">if (confirm('Are you sure to run garbage collection?')) {document.location='?action=gc';}</xsl:attribute>
      </input>
</td>
<td>
      <input type="button" value="Finalization">
      	<xsl:attribute name="onclick">if (confirm('Are you sure to run finalization?')) {document.location='?action=final';}</xsl:attribute>
      </input>
</td>
<td>
      <input type="button" value="Refresh" onclick="document.location='?action=refresh'"/>
</td>
</tr>
</table>
</p>

</form>
 
</xsl:template>
</xsl:stylesheet>