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
    <title>EliIE APP | CUMC-DBMI </title>
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width">
    <link rel="icon" href="http://www.columbia.edu/sites/all/themes/base/columbia2/images/favicon-crown.png">







        <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
        
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
        <link rel="stylesheet" href="/static/css/styles.css">
        <style>
        .btn{
            margin : 5px;
        }
        .well{
            overflow: scroll;
        }
        </style>
    


    
    

    
        <script src="//code.jquery.com/jquery.min.js"></script>
        <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
        <script>
            $( function() {
                $( "#accordion" ).accordion({
                  collapsible: true
                });
              } );
        </script>
        <script src="/static/js/script.js"></script>
        
        <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    

    
    
    
</head>
<body> 
    <header class="banner">
        <nav class="navbar navbar-inverse">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" rel="home" href="/" title="Freesources">
                        EliIE APP
                    </a>
                </div>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="/EliIE">EliIE</a></li>
                    <li><a href="/json-transform">EliIEx</a></li>
                </ul>
            </div>
        </nav>
    </header>

 


    



	
  <div class="container">
  <h2>EliIEx</h2>
  <p> - a tool that transforms <a href="https://github.com/Tian312/EliIE" target="_blanket"> EliIE </a> xml result format into OHDSI json format</p>

  <form id = "xmlinputform" class="form-group" action="" method="POST">
  <input type='hidden' name='csrfmiddlewaretoken' value='nvfQeOw5NcKvTS0dGXsFVeRhjLXtmO6Xk1nKFsebaE5jOoLTqWHtjp5p0DAMFSpF' />
  <div class="form-group"><label class="control-label" for="xmlinput">Xmlinput</label><textarea class="form-control span6" cols="40" id="xmlinput" name="xmlinput" placeholder="Xmlinput" rows="12" title="" required>
&lt;root&gt;
	&lt;sent&gt;
		&lt;text&gt;female , older than 13&lt;/text&gt;
		&lt;entity class=&quot;Condition&quot; index=&quot;T1&quot; negated=&quot;N&quot; relation=&quot;T2:has_value&quot; start=&quot;0&quot;&gt; female &lt;/entity&gt;
		 &lt;attribute class=&quot;Measurement&quot; index=&quot;T2&quot; start=&quot;2&quot;&gt; older than 13 &lt;/attribute&gt;
		

	&lt;/sent&gt;
&lt;/root&gt;</textarea></div>
  <button type="submit" class="btn btn-primary pull-right">Start Transform</button>
  </form>
  </div>
<footer class="footer" style="background-color: white">
    <div class="container">
    <hr>
    <h4 class="text-center">2016 <script>new Date().getFullYear()>2010&&document.write("- "+new Date().getFullYear())</script> &copy Yixuan Guo</h4>
        <div class="text-center center-block">
            <p class="txt-railway">This is a web application developed for transforming eligibility criteria free text to OHDSI recepted JSON format,<br> a graduation project by Yixuan Guo at <a href="https://www.dbmi.columbia.edu/" target="_blanket"> DMBI</a> from <a href="www.columbia.edu">Columbia University</a>.
            <br><strong> Author: <a href="http://www.columbia.edu/~yg2430" target="_blanket">Yixuan Guo</a>, <a href="http://www.tiankangnlp.com/" target="_blanket">Tian Kang</a> (EliIE author), <a href="http://people.dbmi.columbia.edu/~chw7007/" target="_blanket">Chunhua Weng</a>.</strong></p>
                <a href="https://github.com/YixuanAshleyGuo/OHDSImatcher" target="_blanket"><i id="social-gh" class="fa fa-github-square fa-3x social"></i></a>
                 <a href="mailto:cw2384@cumc.columbia.edu"><i id="social-em" class="fa fa-envelope-square fa-3x social"></i></a>
        </div>
    <hr>
</footer>
  <script type="text/javascript">
  onLoadEvent()
  </script>
</body>
</html>