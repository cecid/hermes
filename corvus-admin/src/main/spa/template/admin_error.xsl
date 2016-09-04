<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/error">
<table border="0" cellpadding="2" cellspacing="2" width="100%">
  <tr>
    <td width="30%"><b>Error</b></td>
    <td width="70%">Error occurs when plugin <xsl:value-of select="./plugin" /> initiates.</td>
  </tr>
  <tr>
    <td><b>Message</b></td>
    <td>Please refer to the log in the directory <xsl:value-of select="./home_directory" /></td>
  </tr>
  <tr>
    <td><b>Time</b></td>
    <td><xsl:value-of select="./time" /></td>
  </tr>
</table>
</xsl:template>
</xsl:stylesheet>