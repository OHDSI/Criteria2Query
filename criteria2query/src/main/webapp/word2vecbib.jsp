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
				<%-- <li><a href="<%=basePath%>tcom/mcpage">Reference</a></li> --%>
			</ul>
		</div>
	</div>
	</nav>

	<div class="container-fluid">
		<div class="row">
			<!-- <div class="col-sm-4 col-md-3 sidebar">
				
			</div> -->
			<div class="col-sm-10 col-sm-offset-1 col-md-10 col-md-offset-1 main">
			<h3>Google’s Word2vec Patent</h3>
			<p style="font-size:16px">Word2vec is <a href="https://arxiv.org/pdf/1301.3781.pdf">a method of computing vector representations of words</a> introduced by a team of researchers at Google led by Tomas Mikolov. Google <a href="https://code.google.com/p/word2vec/">hosts an open-source version of Word2vec</a> released under an Apache 2.0 license. In 2014, Mikolov left Google for Facebook, and in May 2015, <a href="http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&amp;Sect2=HITOFF&amp;p=1&amp;u=%2Fnetahtml%2FPTO%2Fsearch-bool.html&amp;r=1&amp;f=G&amp;l=50&amp;co1=AND&amp;d=PTXT&amp;s1=9037464&amp;OS=9037464&amp;RS=9037464">Google was granted a patent for the method</a>, which does not abrogate the Apache license under which it has been released.</p>
			<h3>Bibliography</h3>
			<p style="font-size:16px">1) Mikolov T, Chen K, Corrado G, et al. Efficient estimation of word representations in vector space[J]. arXiv preprint arXiv:1301.3781, 2013.</p>
			<p style="font-size:16px">2) <a href="https://github.com/William-Yeh/word2vec-mac">Word2Vec for Mac OS </a></p>
			<p style="font-size:16px">3) <a href="http://blog.csdn.net/zhaoxinfan/article/details/11640573">ANSL calling Word2Vec model </a></p>
			<h3>Contact</h3>
			
			<p style="font-size:16px">Please feel free to tell us if you have any ideas about how to use Word2Vec to do some interesting things.</p>
			<p style="font-size:16px"><a href="mailto:cy2465@cumc.columbia.edu">Chi Yuan</a>, <a href="mailto:cw2384@cumc.columbia.edu">Chunhua Weng</a> </p>
			</div>
		</div>

		<!-- Bootstrap core JavaScript
    ================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->
		<script type="text/javascript">
  var basePath = "<%=basePath%>";
  
		</script>
</body>
</html>
