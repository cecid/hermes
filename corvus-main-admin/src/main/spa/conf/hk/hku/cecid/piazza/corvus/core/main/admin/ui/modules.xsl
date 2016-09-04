<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" />

  <xsl:variable name="indent-increment" select="'| '" />

  <xsl:template name="print-module-group">
    <xsl:text>+-</xsl:text>
    <xsl:value-of select="./name"/>
    <xsl:text> [System module: </xsl:text><xsl:value-of select="./sysmodule"/><xsl:text>]</xsl:text>
  </xsl:template>

  <xsl:template match="/module-info">
    <table border="0" cellpadding="2" cellspacing="2" width="100%">
	  <tr>
	    <td width="30%"><b>Module Group Name</b></td>
	    <td width="70%"><b><xsl:value-of select="./module-group/name" /></b></td>
	  </tr>
	  <tr>
	    <td>Numbder of Child Groups</td>
	    <td><xsl:value-of select="./module-group/subgroups" /></td>
	  </tr>
	  <tr>
	    <td>System Module</td>
	    <td><xsl:value-of select="./module-group/sysmodule" /></td>
	  </tr>
	</table>
	<br/>
	<table border="0" cellpadding="2" cellspacing="2" width="100%">
	  <tr>
	    <th colspan="2">Module Group Details</th>
	  </tr>
	
	  <xsl:for-each select="module-group/module">
	  <tr>
	    <th colspan="2"/>
	  </tr>
	  <tr>
	    <td><b>Module Name</b></td>
	    <td><b><xsl:value-of select="./name" /></b></td>
	  </tr>
	  <tr>
	    <td>Version</td>
	    <td><xsl:value-of select="./version" /></td>
	  </tr>
	  <tr>
	    <td>No of Components</td>
	    <td><xsl:value-of select="./components" /></td>
	  </tr>
	  <tr>
	    <td>Descriptor</td>
	    <td><xsl:value-of select="./descriptor" /></td>
	  </tr>
	  </xsl:for-each>
	  
	</table>
    <br/>  
    
    <table border="0" cellpadding="2" cellspacing="2" width="100%">
	  <tr>
	    <th>Module Group Hierarchy</th>
	  </tr>
	  <tr>
	    <th />
	  </tr>
	</table>
    <br/>  
    
    <xsl:text disable-output-escaping="yes">&lt;pre&gt;</xsl:text>
    <xsl:apply-templates select="./all-module-groups/module-group"/>
    <xsl:text disable-output-escaping="yes">&lt;/pre&gt;</xsl:text>
  </xsl:template>

  <xsl:template match="module-group">
    <xsl:param name="indent-string" select="$indent-increment" />
    <xsl:value-of select="$indent-string" />
    <xsl:call-template name="print-module-group" />
    <xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
    <xsl:apply-templates select="module-group">
    <xsl:with-param name="indent-string" select="concat($indent-string, $indent-increment)" />
    </xsl:apply-templates>
  </xsl:template>

</xsl:stylesheet>