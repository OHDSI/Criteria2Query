<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Search PICO</title>
<meta name="description" content=" ">
<meta name="keywords" content=" ">
<link href="../css/search.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/impress.js"></script>
</head>

<body>
	<div class="gover_search">
	
    	<div class="gover_search_form clearfix">
            <span class="search_t"><strong>Search for trials</strong></span>
            <input type="text" class="input_search_key" id="gover_search_key" placeholder="please input some key words" />
            <button type="submit" class="">Search</button>
            <div class="search_suggest" id="gov_search_suggest">
                <ul>
                </ul>
            </div>
        </div>
        
    </div>
<script type="text/javascript">
var basePath = "http://localhost:8080/criteria2query/";

</script>
</body>
</html>


