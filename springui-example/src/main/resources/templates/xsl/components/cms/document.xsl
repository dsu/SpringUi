<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/doc">
		<div>
			<h1>
				<xsl:value-of select="title" />
			</h1>
			<div>
				<xsl:value-of select="content" />
			</div>
		</div>
		<i>
			<xsl:value-of select="@created-ts" />
		</i>
	</xsl:template>
</xsl:stylesheet>