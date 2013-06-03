<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>PICKScreens Database Online</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<!-- Le styles -->
<link media="screen" href="${pageContext.request.contextPath}/resources/lib/bootstrap/css/bootstrap.css" rel="stylesheet">
<link media="screen" href="${pageContext.request.contextPath}/resources/lib/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
<link media="screen" href="${pageContext.request.contextPath}/resources/lib/jquery-ui/css/custom-theme/jquery-ui-1.10.2.custom.min.css" rel="stylesheet">
<link media="screen" href="${pageContext.request.contextPath}/resources/lib/jqgrid/css/ui.jqgrid.css" rel="stylesheet">

<style media="screen">
  <%@ include file="pickscreens.css" %>
  <%@ include file="fix-jqgrid.css" %>
</style>
<style media="print">
  <%@ include file="pickscreens_print.css" %>
</style>

<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="${pageContext.request.contextPath}/resources/lib/html5shiv.js"></script>
    <![endif]-->

<!-- Fav and touch icons -->
<%-- 
<link rel="apple-touch-icon-precomposed" sizes="144x144"
	href="${pageContext.request.contextPath}/resources/lib/bootstrap/ico/apple-touch-icon-144-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="114x114"
	href="${pageContext.request.contextPath}/resources/lib/bootstrap/ico/apple-touch-icon-114-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="72x72"
	href="${pageContext.request.contextPath}/resources/lib/bootstrap/ico/apple-touch-icon-72-precomposed.png">
<link rel="apple-touch-icon-precomposed"
	href="${pageContext.request.contextPath}/resources/lib/bootstrap/ico/apple-touch-icon-57-precomposed.png">
--%>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/style/favicon.ico">
</head>
<body>
	<noscript>
	  <div class="ui-state-error ui-corner-all" style="padding: 20px;text-align: center;"><h1>You need to activate JavaScript for this service!</h1></div>
	</noscript>
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<button type="button" class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="brand" href="#">PICKScreens Database Online</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li id="nav_home" class="active"><a href="#">Home</a></li>
						<li id="nav_login">${requestScope.loggedIn ? "" : "<a href=\"#login\">Login</a>"}</li>
						<li class="dropdown">
				    	<a class="dropdown-toggle" data-toggle="dropdown" href="#">?</a>
				    	<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
								<li id="nav_help"><a href="#help">Help</a></li>
								<li id="nav_imprint"><a href="/fam-core/imprint-imprint.html">Imprint</a></li>
					    </ul>
				    </li>
					</ul>
					<ul class="nav pull-right" id="itp-brand">
						<li><a class="brand" href="http://www.it-power.org" alt="© IT-Power GmbH"><img src="${pageContext.request.contextPath}/resources/content/itp_brand.png" alt="IT-Power GmbH" /></a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container">
		<div class="row" id="intro">
			<div class="span6">
				<img class="border" src="${pageContext.request.contextPath}/resources/style/fam_logo_sub.gif"
					alt="Logo ESFRI Instruct Core Centre" />
			</div>
			<div class="span4">
				<span class="pointOut">PICKScreens Database Online</span> is for the
				comparison of crystallization screen formulations. <a href="#help"
					class="hideOn hideOn_help">Read more ...</a>
			</div>
		</div>
		<div class="page" id="page_result">
			<div class="row">
				<div class="content span9"></div>
			</div>
			<div class="row">
				<div class="clear span9">
					<a href="#home" class="btn btn-primary"><i
						class="icon-chevron-left icon-white"></i> Back</a>
					<a href="#result" class="btn btn-primary crossTableResult hideOnResult" id="crossTableResultNumber"><i
						class="icon-refresh icon-left icon-white"></i> Show Count</a>
					<a href="#result" class="btn btn-primary crossTableResult hideOnResult" id="crossTableResultPercentage"><i
						class="icon-refresh icon-left icon-white"></i> Show Percentage</a>
				</div>
			</div>
		</div>
		<div class="page" id="page_home">
		  <div class="row alertlabels">
		    ${requestScope.loggedIn && requestScope.loginTry ? "<span class=\"span9 label label-success\">You are logged in</span>" : ""}
		    ${requestScope.logoutTry ? "<span class=\"span9 label label-success\">You are logged out</span>" : ""}
		    ${requestScope.loggedIn==false && requestScope.loginTry ? "<span class=\"span9 label label-important\">Wrong password</span>" : ""}
		  </div>
			<div class="row">
				<h1 class="span9">Comparison of Screens</h1>
			</div>
			<div class="row">
				<div id="screenWrapper" class="span6">
					<h2>
						Screens Available: <span class="screensAvailable"></span>
					</h2>
					<p>Choose Screens to research for.<br /><button name="substances" class="btn btn-primary">Click here to get an overview of all substances</button></p>
					<table id="screens">
					</table>
					<div id="jq-pager"></div>
				</div>
				<div class="span5 ifNothingSelected">
					<h2>No Screen selected</h2>
					<p>Please select some Screens on the left side.</p>
				</div>
				<div id="userControlWrapper" class="span4 onlyIfSelected">
					<h2>
						With <span class="selectionCount"></span> selected <span
							class="screenSingPlur"></span>
					</h2>
					<p>
						<input type="text" name="search" class="fullWidth" value="" /><br />
						<button name="search" value="1" class="fullWidth btn btn-primary">
							<i class="icon-search icon-white"></i> Search
						</button>
					</p>
					<p id="compareButtons">
						<button name="crosstable" value="1"
							class="fullWidth btn btn-primary disableOnMinMaxScreens">
							<i class="icon-th icon-white"></i> Show Cross Table
						</button>
						<br />
					</p>
					<p>
						<button name="details-duplicates" value="1" class="fullWidth btn btn-primary disableOnMinMaxScreens">
							<i class="icon-info-sign icon-white"></i> Details Of Duplicates
						</button>
					</p>
          <p>
            <button name="details-screens" value="1" class="fullWidth btn btn-primary">
              <i class="icon-info-sign icon-white"></i> Details Of Screens
            </button>
          </p>
				</div>
			</div>
		</div>
		<div class="page" id="page_login">
			<div class="row">
				<h1 class="span9">Login</h1>
			</div>
			<div class="row">
				<div class="span9">You may have the right to get advanced
					options as a member of the ESFRI Instruct Core Centre.</div>
			</div>
			<div class="row" style="margin-top: 10px">
			  <form action="${pageContext.request.contextPath}/login.html" method="POST">
					<div class="span4">
						Password: <input type="password" name="password" />
					</div>
					<div class="span2">
						<button name="login" value="1" class="btn btn-primary">
							<i class="icon-chevron-right icon-white"></i> Login
						</button>
					</div>
				</form>
			</div>
		</div>
		<div class="page" id="page_help">
	    <%@ include file="page_help.jsp" %>
		</div>
	</div>
	<div class="modal"></div>
	<!-- /container -->
	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="${pageContext.request.contextPath}/resources/lib/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/resources/lib/jquery.cookie.js"></script>
	<script src="${pageContext.request.contextPath}/resources/lib/bootstrap/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/lib/jquery-ui/js/jquery-ui-1.10.2.custom.min.js"></script>
  <script src="${pageContext.request.contextPath}/resources/lib/jqgrid/js/i18n/grid.locale-en.js"></script>
	<script src="${pageContext.request.contextPath}/resources/lib/jqgrid/js/jquery.jqGrid.src.js"></script>
	<script type="text/javascript">
	  var screens = ${screensAsJSON};
	  <%@ include file="ITPBootstrapBrand.js" %>
	  <%@ include file="PICKScreens.js" %>
	  <%@ include file="PICKScreensConfig.js" %>
	  <%@ include file="TextFieldValueControl.js" %>
	  <%@ include file="PICKScreensHome.js" %>
	  <%@ include file="PICKScreensJQGrid.js" %>
	  <%@ include file="PICKScreensSubstances.js" %>
	  <%@ include file="PICKScreensSearch.js" %>
	  <%@ include file="PICKScreensDetailsScreens.js" %>
	  <%@ include file="PICKScreensDetailsDuplicates.js" %>
    <%@ include file="PICKScreensCrossTable.js" %>
    var ps = new PICKScreens;
    ps.init();
  </script>
</body>
</html>