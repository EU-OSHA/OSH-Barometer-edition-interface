<%@include file="includes/head.jsp"%>
<%@include file="includes/header.jsp"%>
<div class="container">
	<%@include file="includes/breadcrumb.jsp"%>
	<h1>Edition tool</h1>
	<section id="home-boxes">
		<div class="row1">
			<a class="box" href="user?page=quantitative_eurofound">
				<span class="content-box">
					<span class="box-title">Quantitative data<br>from Eurofound</span>
					<span class="box-ico"><img src="images/logo-eurofound.png"></span>
				</span>
				<span class="tootip-box">Upload Eurofound excel template with the new data.</span>
			</a>
			<a class="box eurostat" href="user?page=quantitative_eurostat">
				<span class="content-box">
					<span class="box-title">Quantitative<br>data from<br>Eurostat</span>
					<span class="box-ico"><img src="images/logo-eurostat.png"></span>
				</span>
				<span class="tootip-box">Upload Eurostat the corresponding excel template with the new data.</span>
			</a>
			<a class="box" href="">
				<span class="content-box">
					<span class="box-title">Explanations<br>related to the<br>quantitative data</span>
					<span class="box-ico"><img src="images/complements.png"></span>
				</span>
				<span class="tootip-box">Qualitative data related to the quantitative data.</span>
			</a>
			<a class="box" href="">
				<span class="content-box">
					<span class="box-title">Country Reports<br>from Member<br>States</span>
					<span class="box-ico"><img src="images/report.png"></span>
				</span>
				<span class="tootip-box">Upload of OSH authorities, Social Dialogue and National strategies pdfs.</span>
			</a>
		</div>
		<div class="row2">
			<a class="box" href="">
				<span class="content-box">
					<span class="box-title">Qualitative<br>data from<br>Member States,<br>EU and International</span>
					<span class="box-ico"><img src="images/qualitative-data.png"></span>
				</span>
				<span class="tootip-box more-text">Update the qualitative data for OSH authorities, National strategies, Enforcement capacity and OSH statistics, surveys and research.</span>
			</a>
			<a class="box" href="">
				<span class="content-box">
					<span class="box-title">Methodology<br>data</span>
					<span class="box-ico"><img src="images/methodology.png"></span>
				</span>
			</a>
			<a class="box" href="user?page=update_datasets">
				<span class="content-box">
					<span class="box-title">Update year /<br>period of the<br>DVT's data</span>
					<span class="box-ico"><img src="images/update-data.png"></span>
				</span>
				<span class="tootip-box more-text">Select the year that should be displayed in the DVT for each indicator. (my name or title change proposals are also meant for the tooltips)</span>
			</a>
		</div>
	</section>
</div>
<%@include file="includes/footer.jsp"%>