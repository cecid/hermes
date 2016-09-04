<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/registry">

<form>
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <td width="30%">Location</td>
    <td width="70%"><xsl:value-of select="./location" /></td>
  </tr>
  <tr>
    <td>Activation</td>
    <td><xsl:value-of select="./activation" /></td>
  </tr>
  <tr>
    <td>Number of plugins</td>
    <td><xsl:value-of select="count(plugins/plugin)" /></td>
  </tr>
</table>
<br/>
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <th colspan="2">Registered Plugins</th>
  </tr>
  <xsl:for-each select="plugins/plugin">
  <tr>
    <th colspan="2"/>
  </tr>
  <tr>
    <td><b>ID</b></td>
    <td><b><xsl:value-of select="./id" /></b></td>
  </tr>
  <tr>
    <td>Name</td>
    <td><xsl:value-of select="./name" /></td>
  </tr>
    <tr>
    <td>Version</td>
    <td><xsl:value-of select="./version" /></td>
  </tr>
  <tr>
    <td>Extension Points</td>
    <td><xsl:value-of select="./points" /></td>
  </tr>
  <tr>
    <td>Extensions</td>
    <td><xsl:value-of select="./extensions" /></td>
  </tr>
  </xsl:for-each>

</table>
</form>
 
</xsl:template>
</xsl:stylesheet>