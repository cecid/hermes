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

<xsl:template match="/tabs">

	    <table border="0" cellpadding="0" cellspacing="0">
	    <tr>
	    <xsl:for-each select="tab">
	    
	        <xsl:if test="position()=1 and not(./selected='')"> 
	            <td width="1"><img border="0" src="?mode=raw&amp;pagelet=tab_left"/></td>
	        </xsl:if>
	
	        <xsl:choose>
	
	            <xsl:when test="./selected=''">
	
	                <xsl:choose>
	                    <xsl:when test="position()=1">
	                        <td width="1"><img border="0" src="?mode=raw&amp;pagelet=tab_sel_open"/></td>
	                    </xsl:when>
	                    <xsl:otherwise>
	                        <td width="1"><img border="0" src="?mode=raw&amp;pagelet=tab_sel_left"/></td>
	                    </xsl:otherwise>
	                </xsl:choose>
	        
	                <td background="?mode=raw&amp;pagelet=tab_sel_bg" class="tab_sel" nowrap="">
	                    <img border="0" src="?mode=raw&amp;pagelet=logo_icon" align="absmiddle"/>
	                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	                       <xsl:call-template name="linkage"/>
	                </td>
	                <td width="1"><img border="0" src="?mode=raw&amp;pagelet=tab_sel_right"/></td>
	
	            </xsl:when>
	
	            <xsl:otherwise>
	                <xsl:variable name="lastPosition" select="position()-1"/>
	                <xsl:if test="position()>1 and not(../tab[$lastPosition]/selected='')">
	                    <td width="1"><img border="0" src="?mode=raw&amp;pagelet=tab_separator"/></td>
	                </xsl:if>
	                <td background="?mode=raw&amp;pagelet=tab_bg" class="tab" nowrap="">
	                    <img border="0" src="?mode=raw&amp;pagelet=logo_icon" align="absmiddle"/>
	                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	                       <xsl:call-template name="linkage"/>
	                </td>
	            </xsl:otherwise>
	
	        </xsl:choose>
	
	        <xsl:if test="position()=last()">
	            <td width="1"><img border="0" src="?mode=raw&amp;pagelet=tab_right"/></td>
	        </xsl:if>
	
	    </xsl:for-each>
	    </tr>
	    </table>
    
</xsl:template>
</xsl:stylesheet>