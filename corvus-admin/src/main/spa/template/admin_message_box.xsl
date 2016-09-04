<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/message">

	  <table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" width="100%" height="100%">
	    <tr>
	        <td>
	          <a name="message"/>
	          <xsl:choose>
	            <xsl:when test="./description">
	              <b>Message: </b>
	              <font color="blue"><xsl:value-of select="./description"/></font>
	            </xsl:when>
	            <xsl:otherwise>
	              <b>Ready</b>
	            </xsl:otherwise>
	          </xsl:choose>
	        </td>
	        <td align="right">
	          <a href="#">Top</a>
	        </td>
	    </tr>
	  </table>  

      <xsl:if test="./description">
        <script>document.location='#message';</script>
      </xsl:if>
	
</xsl:template>

</xsl:stylesheet>