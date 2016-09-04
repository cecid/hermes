<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template name="linkage">
<xsl:element name="a">
   <xsl:attribute name="href">
      <xsl:value-of select="./link" />
   </xsl:attribute> 
   <xsl:attribute name="title">
      <xsl:value-of select="./description" />
   </xsl:attribute> 
   <xsl:value-of select="./name"/>
</xsl:element>
</xsl:template>

<xsl:template match="/modules">
 
    <table border="0" cellspacing="3" cellpadding="3" width="100%" height="100%">

	    <xsl:for-each select="module">

	        <xsl:choose>
	
	            <xsl:when test="./selected=''">
	                  <tr>
	                    <td width="32" height="32" class="module_sel" align="center" valign="top">
                        <img border="0" src="?mode=raw&amp;pagelet=module_sel" align="middle" width="32" height="32"/></td>
	                    <td width="100%" class="module_sel">
	                       <xsl:call-template name="linkage"/>
	                    </td>
	                  </tr>
	            </xsl:when>
	                  
	            <xsl:otherwise>
	                  <tr>
	                    <td width="32" height="32" class="module" align="center" valign="top">
                        <img border="0" src="?mode=raw&amp;pagelet=module" align="middle" width="32" height="32"/></td>
	                    <td width="100%" class="module">
	                       <xsl:call-template name="linkage"/>
	                    </td>
	                  </tr>
	            </xsl:otherwise>
	            
	        </xsl:choose>
	                  
	    </xsl:for-each>
              
        <tr>
          <td class="module"></td>
          <td class="module" width="100%"></td>
        </tr>
              	                  
    </table>
    
</xsl:template>
</xsl:stylesheet>