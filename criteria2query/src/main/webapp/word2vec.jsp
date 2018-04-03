<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../img/word2vec.png">

<title>Word2Vec</title>

<!-- Bootstrap core CSS -->
<link href="../css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="../css/dashboard.css" rel="stylesheet">
<link rel="stylesheet" href="../css/bootstrap-table.min.css">
<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="../js/ie-emulation-modes-warning.js"></script>
<script src="../js/jquery.min.js"></script>
<script src="../js/bootstrap.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="../js/ie10-viewport-bug-workaround.js"></script>
<script src="../js/bootstrap-table.min.js"></script>
<script src="../js/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript" src="http://malsup.github.io/min/jquery.blockUI.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>

	<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="<%=basePath%>ohdsi/word2vec">Word2Vec-Samples (model trained by CT.gov & Pubmed)</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="<%=basePath%>ohdsi/word2vecbib">About</a></li>
			</ul>
		</div>
	</div>
	</nav>

	<div class="container-fluid">
		<div class="row">
			<!-- <div class="col-sm-4 col-md-3 sidebar">
				
			</div> -->
			<div class="col-sm-12 col-md-12  main">
				<div class="panel-body" style="padding-bottom: 0px;">
					<div class="panel panel-default">
						<div class="panel-heading">Search for similar words</div>
						<div class="panel-body">
							<form id="formSearch" class="form-horizontal">
								<div class="form-group" style="margin-top: 5px">
									<label class="control-label col-sm-1"
										for="txt_search_departmentname">Word</label>
									<div class="col-sm-2">
										<input type="text" class="form-control"
											id="word1">
									</div>
							
									<div class="col-sm-4" style="text-align: left;">
										<button type="button" style="margin-left: 10px" id="getsmword"
											class="btn btn-primary">Search</button>
									</div>
								</div>
							</form>
						</div>
					</div>


				</div>
				<div class="panel-body" style="padding-bottom: 0px;">
					<div class="panel panel-default">
						<div class="panel-heading">Similarity Calculation</div>
						<div class="panel-body">
							<form id="formSearch" class="form-horizontal">
								<div class="form-group" style="margin-top: 5px">
									<label class="control-label col-sm-1"
										for="txt_search_departmentname">Word</label>
									<div class="col-sm-2">
										<input type="text" class="form-control"
											id="word2">
									</div>
									<label class="control-label col-sm-1" for="txt_search_statu">Word
										</label>
									<div class="col-sm-2">
										<input type="text" class="form-control" id="word3">
									</div>
									<div class="col-sm-2" style="text-align: left;">
										<button type="button" style="margin-left: 10px" id="calcuw2w"
											class="btn btn-primary">Calculate</button>
									</div>
								</div>
							</form>
						</div>
					</div>


				</div>
				<!-- <div class="panel-body" style="padding-bottom: 0px;">
					<div class="panel panel-default">
						<div class="panel-heading">Similar Relation </div>
						<div class="panel-body">
							<form id="formSearch" class="form-horizontal">
								<div class="form-group" style="margin-top: 5px">
									
									<div class="col-sm-2">
										<input type="text" class="form-control" 
											id="word4">
									</div>
									<label class="control-label col-sm-1" for="txt_search_statu">---</label>
									<div class="col-sm-2">
										<input type="text" class="form-control" id="word5">
									</div>
									<label class="control-label col-sm-1" for="txt_search_statu">==</label>
									<div class="col-sm-2">
										<input type="text" class="form-control" id="word6">
									</div>
									<label class="control-label col-sm-1" for="txt_search_statu">---</label>
									<label class="control-label col-sm-1" for="txt_search_statu">????</label>
									<div class="col-sm-2" style="text-align: left;" style="margin-top: 15px">
										<button type="button" id="calcuw3w" class="btn btn-primary pull-right">Calculate</button>
									</div>
								</div>
							</form>
						</div>
					</div>


				</div> -->
				<div class="panel-body" style="padding-bottom: 0px;">
				<textarea id="wordlist" name="wordlist" class="form-control" rows="10" placeholder="OUTPUT"></textarea>
				</div>
			</div>
		</div>

		<!-- Bootstrap core JavaScript
    ================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->
		<script type="text/javascript">
  var basePath = "<%=basePath%>";
  $(document).ajaxStop($.unblockUI);
  var basePath = "<%=basePath%>";  
      $(function () {
    	  $("#wordlist").val('');
    	  $("#getsmword").click(function(){
    		  getSmword();
    		  });
    	  $("#calcuw2w").click(function(){
    		  cal2word();
    		  });
    	  $("#calcuw3w").click(function(){
    		  cal3word();
    		  });
      })
      function getSmword(){
    	  var word1=$("#word1").val();
    	  if(word1==''){
    		  alert('Please check your input');
    		  return;
    	  }
    	  $('body').block( {  
    	        message : '<h3>Data loading……</h3>',  
    	        css : {  
    	            border : '1px solid khaki'  
    	        }  
    	    });  
		  $.ajax({
			  type: 'POST',
			  url: basePath+"ohdsi/getsimiword",
			  data: {'word1':word1},
		      dataType: "json",
		        success: function(data){
		        	/* alert("Success!"); */
		        	$("#wordlist").val(data);
		        }
			});
      }
      
      function cal2word(){
    	  var word1=$("#word2").val();
    	  var word2=$("#word3").val();
    	  if(word2=='' || word1==''){
    		  alert('Please check your input');
    		  return;
    	  }
    	  $('body').block( {  
    	        message : '<h3>Data loading……</h3>',  
    	        css : {  
    	            border : '1px solid khaki'  
    	        }  
    	    });  
		  $.ajax({
			  type: 'POST',
			  url: basePath+"ohdsi/cal2word",
			  data: {'word1':word1,
				  'word2':word2
				  },
		      dataType: "json",
		        success: function(data){
		        	/* alert("Success!"); */
		        	$("#wordlist").val(data);
		        }
			});
      }
      
      function cal3word(){
    	  alert('!!');
    	  var word1=$("#word4").val();
    	  var word2=$("#word5").val();
    	  var word3=$("#word6").val();
    	  if(word1=='' || word2=='' || word3==''){
    		  alert('Please check your input');
    		  return;
    	  }
    	  $('body').block( {  
    	        message : '<h3>Data loading……</h3>',  
    	        css : {  
    	            border : '1px solid khaki'  
    	        }  
    	    });  
		  $.ajax({
			  type: 'POST',
			  url: basePath+"ohdsi/cal3word",
			  data: {'word1':word1,
				  'word2':word2,
				  'word3':word3,
				  },
		      dataType: "json",
		        success: function(data){
		        	/* alert("Success!"); */
		        	$("#wordlist").val(data);
		        }
			});
      }
		</script>
</body>
</html>
