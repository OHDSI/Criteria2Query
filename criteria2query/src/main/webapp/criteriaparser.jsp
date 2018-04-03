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
    <title>Criteria2XML</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="keywords" content="Clinical trial,">
    <meta name="author" content="Chi Yuan, Chunhua Weng">
    <meta name="robots" content="index,follow">
    
    <!-- Site CSS -->
   <!--  <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"> -->
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
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
<link href="<%=basePath%>/css/dashboard.css" rel="stylesheet">
<link href="<%=basePath%>/css/ner.css" rel="stylesheet">
<link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="https://static.bootcss.com/www/assets/css/site.min.css?v5" rel="stylesheet">
<link rel="stylesheet" href="<%=basePath%>/css/bootstrap-table.min.css">
<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="<%=basePath%>/js/ie-emulation-modes-warning.js"></script>
<script src="<%=basePath%>/js/jquery.min.js"></script>
<script src="<%=basePath%>/js/bootstrap.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="<%=basePath%>/js/ie10-viewport-bug-workaround.js"></script>
<script src="<%=basePath%>/js/bootstrap-table.min.js"></script>
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
          <a class="navbar-brand hidden-sm" href="<%=basePath%>/ct/nlp" style="color:white" onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'navbar-mainpage'])">Criteria2XML</a>
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
        <h1 style="font-size:300%">Criteria2XML</h1>
        <h3>A natural language processing tools for transforming clinical research eligibility criteria to structure data.</h3>
        <p class="masthead-button-links">
          <a class="btn btn-lg btn-success" id="starttoinput" target="_blank" role="button" >&nbsp;&nbsp;Start&nbsp;&nbsp;</a>
        </p>
      </div>
    </div>


    <div class="container projects">

 <div class="projects-header page-header">
      <!--   <h2>Critera2Query</h2> -->
     <h3><p>Data Extraction for Clinical Eligibility Criteria</p></h3>
      </div> 

      <div class="row">

        <div class="col-sm-12 col-md-12 col-lg-12">	
       
       
        <label class="col-sm-3 col-md-3 col-lg-3">ClinicalTrials.gov Identifier:</label> 
        <div class="col-sm-4 col-md-4 col-lg-4" style="margin-bottom: 15px">
        <input type="text" class="form-control form-control-lg" id="nctid" placeholder="e.g. NCT01136369">
        </div>
        <button id="fetchct" type="button" class="btn btn-primary">Get From ClinicalTrials.gov</button>
     <div id="attforec" style="display: none;">
     <div class="col-sm-12 col-md-12 col-lg-12">	
     <label style="font-weight:normal">Minimum Ages Eligible for Study: </label><label id="minimum_age" style="font-weight:normal"></label>
    
     </div>
     <div class="col-sm-12 col-md-12 col-lg-12">	
      <label style="font-weight:normal">Maxmum Ages Eligible for Study: </label><label id="maxmum_age" style="font-weight:normal"></label>
     </div>
     <div class="col-sm-12 col-md-12 col-lg-12">	
    <label style="font-weight:normal">Sexes Eligible for Study: </label> <label id="gender" style="font-weight:normal"></label>
    </div>
     <div class="col-sm-12 col-md-12 col-lg-12">	
    <label style="font-weight:normal"> Accepts Healthy Volunteers: </label><label style="font-weight:normal" id="healthy_volunteers"></label>
    </div>
     <div class="col-sm-12 col-md-12 col-lg-12">	
    <label style="font-weight:normal">Sampling Method:</label><label id="sampling_method" style="font-weight:normal"></label>
    </div>
     <div class="col-sm-12 col-md-12 col-lg-12">	
    <label style="font-weight:normal" >Study Population:</label><label id="study_pop" style="font-weight:normal"></label>
    </div>
    </div>
     <form id="formSearch">
     <div class="col-sm-6 col-md-6 col-lg-6">	
     <label>Inclusion Criteria:(one criterion per line)</label>
				<textarea class="form-control"  rows="12" id="incriteria" name="incriteria">
	</textarea>
			  </div>
			 
       
        <div class="col-sm-6 col-md-6 col-lg-6">
        <label>Exclusion Criteria:(one criterion per line)</label>	
				<textarea class="form-control"  rows="12" id="excriteria" name="excriteria">
				</textarea>
			  </div>
			 
        </div>
       </form>
       <div class="projects-header" >
      <!--  <h2>Result</h2> -->
      <div >
      <button type="button"  style="margin-top: 15px" id="start" class="btn btn-primary glyphicon glyphicon-pencil ">Parse</button>
	 <button id="format" style="margin-top: 15px" type="button" class="btn btn-info glyphicon glyphicon-filter">Format</button>
	  
	   </div>
       </div>
        <div class="col-sm-12 col-md-12 col-lg-12">
     <label id="timecost"></label>
    	<table id="intable" class="table table-bordered col-sm-12 col-md-12 col-lg-12" >
			</table>
			 <table id="extable" class="table table-bordered col-sm-12 col-md-12 col-lg-12" >
				
			</table>
			</div>
			  <div class="projects-header" >
      <!--  <h2>Result</h2> -->
      <div >
      <center>
			 <button id="download" style="margin-top: 15px; display:none " type="button" class="btn btn-success glyphicon glyphicon-search">Download</button>
        </center> 
        </div>
        </div>
</div>
</div>
<!-- /.container -->
    <footer class="footer ">
      <div class="container">
      <div class="row footer-top">
          <div class="col-sm-6 col-lg-6">
            
            <p>This website was developed by ChiYuan ,Chunhua Weng</p>
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
			$("#format").click(function(){
				formatdata();
			});
			$("#incriteria").val('');
			$("#excriteria").val('');
			$("#fetchct").click(function() {
				if ($("#nctid").val() == '') {
					alert('Please input a nctid!');
					return;
				}
				getCTinfo();
			});
			$('#nctid').bind('keypress',function(event){
	             if(event.keyCode == "13")   
	             {

	            	 if ($("#nctid").val() == '') {
	 					alert('Please input a nctid!');
	 					return;
	 				}
	 				getCTinfo();

	             }
	         });
			
			$("#starttoinput").click(function(){
			    var t = $(window).scrollTop();
			    $('body,html').animate({'scrollTop':t+410},200)
			})
			$("#download").click(function() {		
				download();
			});
			$("#start").click(function() {
				
				parse();
			});
			
			/* $('#intable').bootstrapTable({
		        data: mydata
		    }); */
			
		})
		function formatdata(){
			var inc = $("#incriteria").val();
			var exc=  $("#excriteria").val();
			$.blockUI({
				message : '<h3><img src="'+basePath+'/img/squares.gif" /> Formatting...</h3>',
				css : {
					border : '1px solid khaki'
				}
			});
			$.ajax({
				type : 'POST',
				url : basePath + "nlpmethod/formatdata",
				data : {
					'inc' : inc,
					'exc' : exc

				},
				dataType : "json",
				success : function(data) {
					$("#incriteria").val(data["afterinc"]);
					$("#excriteria").val(data["afterexc"]);
				}
			})
		}
		function getCTinfo() {
			var nctid = $("#nctid").val();
			$.blockUI({
				message : '<h3><img src="'+basePath+'/img/squares.gif" /> Data Loading...</h3>',
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
					 $("#gender").html("&nbsp;"+data['gender']);
					 $("#minimum_age").html("&nbsp;"+data['minimum_age']);
					 $("#maxmum_age").html("&nbsp;"+data['maxmum_age']);	
					 $("#sampling_method").html("&nbsp;"+data['sampling_method']);
					 $("#study_pop").html("&nbsp;"+data['study_pop']);	
					 $("#healthy_volunteers").html("&nbsp;"+data['healthy_volunteers']);
					 //$("#attforec").show();
						
				},
				error:function(){
					 $(document).ajaxStop($.unblockUI);
					alert("Please check your NCTID");
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
			$.blockUI({
				message : '<h3><img src="'+basePath+'/img/squares.gif" /> Parsing...</h3>',
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
					$('#intable').bootstrapTable('destroy');
					 $('#intable').bootstrapTable({
					        data: data["include"],
					        columns : [ {
								field : 'id',	
								title : '#',
								width : '10%'
							}, {
								field : 'criteria',
								width : '80%',
								title : 'Inclusion Criteria:'
							}, {
								field : 'ehrstatus',
								width : '10%',
								title : 'EHR Availability',
									 formatter: 
								            function (value, row, index) {
												if(value==true){
													return "<label style=\"color:green\">YES</label>"; 
												}else{
													return "<label style=\"color:red\">NO</label>"; 
												}
								             }
							}]
					    });
					 $('#extable').bootstrapTable('destroy');
					 $('#extable').bootstrapTable({
					        data: data["exclude"] ,
					        columns : [ {
								field : 'id',	
								title : '#',
								width : '10%',
							}, {
								field : 'criteria',
								title : 'Exclusion Criteria:',
								width : '80%',
							}, {
								field : 'ehrstatus',
								title : 'EHR Availability',
								width : '10%',
								 formatter: 
							            function (value, row, index) {
											if(value==true){
												return "<label style=\"color:green\">YES</label>"; 
											}else{
												return "<label style=\"color:red\">NO</label>"; 
											}
							             }
							}]
					  });
					 var t = $(window).scrollTop();
					 $('body,html').animate({'scrollTop':t+1000},200);
					 $("#timecost").html('Time cost &nbsp;:&nbsp;'+data['time']+" &nbsp;s");
					 $("#timecost").show();
					 $("#download").show();
					        
				}
			});
		}
		function download(){
			window.open(basePath + "nlpmethod/download");  
		}

</script>
    </body>
    

</html>
