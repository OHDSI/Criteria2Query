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
//实现搜索输入框的输入提示js类
function oSearchSuggest(searchFuc){
	var input = $('#gover_search_key');
	var suggestWrap = $('#gov_search_suggest');
	var key = "";
	var init = function(){
		input.bind('keyup',sendKeyWord);
		input.bind('blur',function(){setTimeout(hideSuggest,100);})
	}
	var hideSuggest = function(){
		suggestWrap.hide();
	}
	
	//发送请求，根据关键字到后台查询
	var sendKeyWord = function(event){
		
		//键盘选择下拉项
		if(suggestWrap.css('display')=='block'&&event.keyCode == 38||event.keyCode == 40){
			var current = suggestWrap.find('li.hover');
			if(event.keyCode == 38){
				if(current.length>0){
					var prevLi = current.removeClass('hover').prev();
					if(prevLi.length>0){
						prevLi.addClass('hover');
						input.val(prevLi.html());
					}
				}else{
					var last = suggestWrap.find('li:last');
					last.addClass('hover');
					input.val(last.html());
				}
				
			}else if(event.keyCode == 40){
				if(current.length>0){
					var nextLi = current.removeClass('hover').next();
					if(nextLi.length>0){
						nextLi.addClass('hover');
						input.val(nextLi.html());
					}
				}else{
					var first = suggestWrap.find('li:first');
					first.addClass('hover');
					input.val(first.html());
				}
			}
			
		//输入字符
		}else{ 
			var valText = $.trim(input.val());
			if(valText ==''||valText==key){
				return;
			}
			searchFuc(valText);
			key = valText;
		}			
		
	}
	//请求返回后，执行数据展示
	this.dataDisplay = function(data){
		if(data.length<=0){
            suggestWrap.hide();
			return;
		}
		
		//往搜索框下拉建议显示栏中添加条目并显示
		var li;
		var tmpFrag = document.createDocumentFragment();
		suggestWrap.find('ul').html('');
		for(var i=0; i<data.length; i++){
			li = document.createElement('LI');
			li.innerHTML = data[i];
			tmpFrag.appendChild(li);
		}
		suggestWrap.find('ul').append(tmpFrag);
		suggestWrap.show();
		
		//为下拉选项绑定鼠标事件
		suggestWrap.find('li').hover(function(){
				suggestWrap.find('li').removeClass('hover');
				$(this).addClass('hover');
		
			},function(){
				$(this).removeClass('hover');
		}).bind('click',function(){
			input.val(this.innerHTML);
			suggestWrap.hide();
		});
	}
	init();
};

//实例化输入提示的JS,参数为进行查询操作时要调用的函数名
var searchSuggest =  new oSearchSuggest(sendKeyWordToBack);

//这是一个模似函数，实现向后台发送ajax查询请求，并返回一个查询结果数据，传递给前台的JS,再由前台JS来展示数据。本函数由程序员进行修改实现查询的请求
//参数为一个字符串，是搜索输入框中当前的内容
function sendKeyWordToBack(keyword){
	  var obj = {
			    "keyword" : keyword
			 };
			 $.ajax({
					   type: "POST",
					   url: basePath + "search/pico",
					   async:false,
					   data : obj,
					   dataType: "json",
					   success: function(data){
						 //var json = eval("("+data+")");
						 var key=data['candidate'].split(",");
						 var aData = [];
						 for(var i=0;i<key.length;i++){
								//以下为根据输入返回搜索结果的模拟效果代码,实际数据由后台返回
							if(key[i]!=""){
								  aData.push(key[i]);
							}
						 }
						//将返回的数据传递给实现搜索输入框的输入提示js类
						 searchSuggest.dataDisplay(aData);
					   }
		 });	
			 
				/* //以下为根据输入返回搜索结果的模拟效果代码,实际数据由后台返回
				var aData = [];
				aData.push(keyword+'返回数据1');
				aData.push(keyword+'返回数据2');
				aData.push(keyword+'不是有的人天生吃素的');
				aData.push(keyword+'不是有的人天生吃素的');
				aData.push(keyword+'2012是真的');
				aData.push(keyword+'2012是假的');
				//将返回的数据传递给实现搜索输入框的输入提示js类
				searchSuggest.dataDisplay(aData); */
	
}

</script>
</body>
</html>


