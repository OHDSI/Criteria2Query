<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>

  <head>
    
    <meta charset="utf-8">
    <title>Criteria2Query</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="keywords" content="Clinical trial,">
    <meta name="author" content="Chi Yuan, Chunhua Weng">
    <meta name="robots" content="index,follow">
    
    <!-- Site CSS -->
   <!--  <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"> -->
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="https://static.bootcss.com/www/assets/css/site.min.css?v5" rel="stylesheet">
    <style>
    .job-hot {
    	position: absolute;
    	color: #d9534f;
    	right: 0;
    	top: 15px;
    }
    .eliback{
    	color:white;
    	background-color:#1E90FF;
    	position: relative;
    }
     .navback{
    	
    	background-color:#428bca;
    	color:white;
    	
    }
    .gradient{
    background: #428bca;
    background: -moz-linear-gradient(top,  #428bca 0%, #4169E1 100%);
    background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#428bca), color-stop(100%,#4169E1));
    background: -webkit-linear-gradient(top,  #428bca 0%,#4169E1 100%);
    background: -o-linear-gradient(top,  #428bca 0%,#4169E1 100%);
    background: -ms-linear-gradient(top,  #428bca 0%,#4169E1 100%);
    background: linear-gradient(to bottom,  #428bca 0%,#4169E1 100%);
    filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#428bca', endColorstr='#4169E1',GradientType=0 );

}
.inctextbg{
background: #fefefe url(../img/inc.png) no-repeat center;
}
.exctextbg{
background: #fefefe url(../img/exc.png) no-repeat center;
}
    </style>
    
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  <!-- Custom styles for this template -->
<link href="../css/dashboard.css" rel="stylesheet">
<link href="../css/ner.css" rel="stylesheet">
<link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="https://static.bootcss.com/www/assets/css/site.min.css?v5" rel="stylesheet">
<link rel="stylesheet" href="../css/bootstrap-table.min.css">
<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="../js/ie-emulation-modes-warning.js"></script>
<script src="../js/jquery.min.js"></script>
<script src="../js/bootstrap.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="../js/ie10-viewport-bug-workaround.js"></script>
<script src="../js/bootstrap-table.min.js"></script>
    <script type="text/javascript" src="http://malsup.github.io/min/jquery.blockUI.min.js"></script>
  </head>
  <body>

    <div class="navbar navbar-inverse navbar-fixed-top navback">
      <div class="container">
        <div class="navbar-header">
          <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand hidden-sm" href="http://www.bootcss.com" style="color:white" onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'navbar-mainpage'])">Criteria2Query</a>
        </div>
        <div class="navbar-collapse collapse" role="navigation">
          <ul class="nav navbar-nav">
          
          </ul>
          <!-- <ul class="nav navbar-nav navbar-right hidden-sm">
            <li><a href="/about/" onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'about'])">About</a></li>
          </ul> -->
        </div>
      </div>
    </div>

    <div class="gradient masthead">
      <div class="container">
        <h1 style="font-size:300%">Criteria2Query</h1>
        <h3>A system for automatically transforming clinical research eligibility criteria to OMOP Common Data Model-based executable cohort queries.</h3>
        <p class="masthead-button-links">
          <a class="btn btn-lg btn-success" href="http://v3.bootcss.com/" target="_blank" role="button" onclick="_hmt.push(['_trackEvent', 'masthead', 'click', 'masthead-Bootstrap3中文文档'])">&nbsp;&nbsp;Start&nbsp;&nbsp;</a>
        </p>
      </div>
    </div>


    <div class="container projects">

 <div class="projects-header page-header">
      <!--   <h2>Critera2Query</h2> -->
     <p>Transforming Eligibility Criteria Text to Cohort Queries</p>
      </div> 

      <div class="row">

        <div class="col-sm-12 col-md-12 col-lg-12">	
       
       
        <label class="col-sm-3 col-md-3 col-lg-3">ClinicalTrials.gov Identifier:</label> 
        <div class="col-sm-4 col-md-4 col-lg-4" style="margin-bottom: 15px">
        <input type="text" class="form-control form-control-lg" id="nctid" placeholder="e.g. NCT01136369">
        </div>
        <button id="fetchct" type="button" class="btn btn-primary">Get From ClinicalTrials.gov</button>
     <div class="col-sm-6 col-md-6 col-lg-6">	
				<textarea class="form-control inctextbg"  rows="12" id="incriteria">
				</textarea>
			  </div>
			 
       
        <div class="col-sm-6 col-md-6 col-lg-6">	
				<textarea class="form-control exctextbg"  rows="12" id="excriteria">
				</textarea>
			  </div>
			 
        </div>
       
       <div class="projects-header" >
      <!--  <h2>Result</h2> -->
      <div >
      <button type="button"  style="margin-top: 15px" id="start" class="btn btn-primary glyphicon glyphicon-pencil ">Parsing</button>
	<!--  <button id="format" style="margin-top: 15px" type="button" class="btn btn-info glyphicon glyphicon-filter">Filtering</button>
	  --> 
	  <button id="format"style="margin-top: 15px"  type="button" class="btn btn-success glyphicon glyphicon-search">Searching</button>
      </div>
       </div>
       <!--  <div class="col-sm-12 col-md-12 col-lg-12">
        
    	<table id="intable" class="table table-bordered col-sm-12 col-md-12 col-lg-12" >
				<thead>
					<tr>
						<th data-field="id" data-width="2%">#</th>
						<th data-field="criteria" data-width="68%">Inclusion Criteria:</th>
						<th data-field="database" data-width="20%">DataBase</th>
						<th data-field="patient" data-width="10%">Patient Count</th>
					</tr>
				</thead>
			</table>
			 <table id="extable" class="table table-bordered col-sm-12 col-md-12 col-lg-12" >
				<thead>
					<tr>
						<th data-field="id" data-width="2%">#</th>
						<th data-field="criteria" data-width="68%">Exclusion Criteria:</th>
						<th data-field="database" data-width="20%">DataBase</th>
						<th data-field="patient" data-width="10%">Patient Count</th>
					</tr>
				</thead>
			</table>
        </div> -->
</div>
</div>
<!-- /.container -->
    <footer class="footer ">
      <div class="container">
      <div class="row footer-top">
          <div class="col-sm-6 col-lg-6">
            
            <p>This website was developed by ChiYuan, Yixuan Guo,Chunhua Weng</p>
          </div>
			
        </div> 
        
       
      </div>
    </footer>
    

  <script type="text/javascript">
  $(document).ajaxStop($.unblockUI);
  var $intable = $('#intable');
  var $extable = $('#extable');


  var basePath = "<%=basePath%>";
		$(function() {
			$("#incriteria").val('');
			$("#excriteria").val('');
			$("#fetchct").click(function() {
				if ($("#nctid").val() == '') {
					alert('Please input a nctid!');
					return;
				}
				getCTinfo();
			});
			$("#format").click(function() {
				formattext();
			});
			$("#start").click(function() {
				parse();
			});
			/* $('#intable').bootstrapTable({
		        data: mydata
		    }); */
			
		})
		function getCTinfo() {
			var nctid = $("#nctid").val();
			$('body').block({
				message : '<h3><img src="../img/squares.gif" /> Data Loading...</h3>',
				css : {
					border : '1px solid khaki'
				}
			});
			$.ajax({
				type : 'POST',
				url : basePath + "nlpmethod/getct",
				data : {
					'nctid' : nctid
				},
				dataType : "json",
				success : function(data) {
					/* alert("Success!"); */
					$("#incriteria").val(data["inc"]);
					$("#excriteria").val(data["exc"]);
				}
			});
		}	
		function parse2(){
			var inc = $("#incriteria").val();
			var exc=  $("#excriteria").val();
			
		}
		function parse() {
			var inc = $("#incriteria").val();
			var exc=  $("#excriteria").val();
			$('body').block({
				message : '<h3><img src="../img/squares.gif" /> Parsing...</h3>',
				css : {
					border : '1px solid khaki'
				}
			});
			$.ajax({
				type : 'POST',
				url : basePath + "nlpmethod/parse",
				data : {
					'inc' : inc,
					'exc' : exc
				},
				dataType : "json",
				success : function(data) {
					window.location.href=basePath + "nlpmethod/conceptset";
				
					/* $('#intable').bootstrapTable('destroy');
					 $('#intable').bootstrapTable({
					        data: data["include"]
					    });
					 $('#extable').bootstrapTable('destroy');
					 $('#extable').bootstrapTable({
					        data: data["exclude"] */
					        
					/*  $('#table').bootstrapTable({
					        data: mydata
					    });
					 $('#extable').bootstrapTable('refresh') */
					/* $("#incriteria").val(data["inc"]);
					$("#excriteria").val(data["exc"]); */
				}
			});
		}
		
		
</script>
    </body>
    

</html>
