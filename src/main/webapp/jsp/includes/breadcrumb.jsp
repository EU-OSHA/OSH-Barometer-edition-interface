<%@page import="java.util.ResourceBundle" %>
<% ResourceBundle configurationData = ResourceBundle.getBundle("eu.europa.osha.barometer.edition.webui.conf.breadcrumb");%>
<div class="breadcrumbs">
	<ul>
		<%= configurationData.getString("breadcrumb."+currentPage) %>
	</ul>
</div>