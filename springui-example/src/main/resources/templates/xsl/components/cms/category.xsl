<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/category">

		<h2>
			<strong>
				<xsl:value-of select="title" />
			</strong>
		</h2>
		<hr />
		<xsl:for-each select="subdocuments/document">
			<div>
				<h1>
					<a href="/cms/{alias}">
						<xsl:value-of select="title" />
					</a>
				</h1>
				<div>
					<xsl:value-of select="content" />
				</div>
			</div>
		</xsl:for-each>
		<i>
			<xsl:value-of select="@created-ts" />
		</i>
	</xsl:template>
</xsl:stylesheet>