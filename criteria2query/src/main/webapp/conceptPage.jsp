<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../../favicon.ico">

<title>OHDSI Matcher</title>

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
			<a class="navbar-brand" href="#">OHDSI Matcher</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="#">About</a></li>
			</ul>
		</div>
	</div>
	</nav>

	<div class="container-fluid">
		<div class="row">
			<div class="col-sm-3 col-md-2 sidebar">
				<ul class="nav">
					<li>
						<div align="center" >
							<span>Input Free-text</span>
						</div>
					</li>
				</ul>
				<ul class="nav">
					<li>&nbsp;</li>
					<div align="center">
						<li><span class="glyphicon glyphicon-arrow-down"></span></li>
					</div>
					<li>&nbsp;</li>
				</ul>
				<ul class="nav">
					<li class="active" >
						<div align="center" >
							<span>Generate XML </span>
						</div>
					</li>
				</ul>
				<ul class="nav">
					<li>&nbsp;</li>
					<div align="center">
						<li><span class="glyphicon glyphicon-arrow-down"></span></li>
					</div>
					<li>&nbsp;</li>
				</ul>
				<ul class="nav">
					<li class="active" >
						<div align="center" style="color: blue">
							<span>Concept Selection </span>
						</div>
					</li>
				</ul>
				<ul class="nav">
					<li>&nbsp;</li>
					<div align="center">
						<li><span class="glyphicon glyphicon-arrow-down"></span></li>
					</div>
					<li>&nbsp;</li>
				</ul>
				<ul class="nav">
					<li class="active" >
						<div align="center">
							<span>ATLAS Results </span>
						</div>
					</li>
				</ul>
				
			</div>
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h3>Concept Selection</h3>
				<h6>some instructions</h6>
				<form id="formSearch" class="form-horizontal" action="<%=basePath%>ohdsi/atlasresult">
					<div class="form-group" style="margin-top: 15px">

						<div class="col-sm-4" style="text-align: left;">
							<button type="submit" id="btn_query" class="btn btn-primary">Next
								Step</button>
						</div>
					</div>

				</form>
				<table id="tb_departments"></table>
			</div>

			<!-- Bootstrap core JavaScript
    ================================================== -->
			<!-- Placed at the end of the document so the pages load faster -->
			<script type="text/javascript">
  				var bPath = "<%=basePath%>";
				$(function() {

					//1.初始化Table
					var oTable = new TableInit();
					oTable.Init();
					//2.初始化Button的点击事件
					var oButtonInit = new ButtonInit();
					oButtonInit.Init();

				});

				var TableInit = function() {
					var oTableInit = new Object();
					//初始化Table
					oTableInit.Init = function() {
						$('#tb_departments')
								.bootstrapTable(
										{
											url : bPath + 'bill/queryallbill', //请求后台的URL（*）
											method : 'get', //请求方式（*）
											toolbar : '#toolbar', //工具按钮用哪个容器
											striped : true, //是否显示行间隔色
											cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
											pagination : true, //是否显示分页（*）
											sortable : true, //是否启用排序
											sortOrder : "asc", //排序方式
											queryParams : oTableInit.queryParams,//传递参数（*）
											sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
											pageNumber : 1, //初始化加载第一页，默认第一页
											pageSize : 10, //每页的记录行数（*）
											pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
											search : false, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
											strictSearch : true,
											showColumns : false, //是否显示所有的列
											showRefresh : false, //是否显示刷新按钮
											minimumCountColumns : 2, //最少允许的列数
											clickToSelect : true, //是否启用点击选中行
											height : 500, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
											uniqueId : "ID", //每一行的唯一标识，一般为主键列
											showToggle : false, //是否显示详细视图和列表视图的切换按钮
											cardView : false, //是否显示详细视图
											detailView : false, //是否显示父子表
											columns : [
													{
														checkbox : true
													},
													{
														field : 'concept_id',
														title : 'concept_id'
													},
													{
														field : 'criteria',
														title : 'Criteria'
													},
													
													{
														title : 'details',
														field : '#',
														align : 'center',
														formatter : function(
																value, row,
																index) {
															if (row.ispaid == '1') {
																return '<span style="color:green">是</span>';
															} else {
																return '<span style="color:red">否</span>';
															}
														}
													}

											]
										});
					};

					//得到查询的参数
					oTableInit.queryParams = function(params) {
						var temp = { //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
							limit : params.limit, //页面大小
							offset : params.offset, //页码
							searchText : params.search
						//statu: $("#txt_search_statu").val()
						};
						return temp;
					};
					return oTableInit;
				};

				var ButtonInit = function() {
					var oInit = new Object();
					var postdata = {};

					oInit.Init = function() {
						//初始化页面上面的按钮事件
					};

					return oInit;
				};
			</script>
</body>
</html>
