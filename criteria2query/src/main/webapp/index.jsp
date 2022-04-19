<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!--!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"-->
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Criteria2Query</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="keywords" content="Clinical trial,NLP">
    <meta name="author" content="Yilu Fang, Yingcheng Sun, Hao Liu, Chi Yuan, Patrick Ryan, Yixuan Guo, Chunhua Weng">
    <meta name="robots" content="index,follow">

    <!-- Site CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"
          integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu" crossorigin="anonymous">
    <link
            href="./css/font-awesome.min.css"
            rel="stylesheet">
    <link href="./css/site.min.css?v5"
          rel="stylesheet">
    <style>
        .job-hot {
            position: absolute;
            color: #d9534f;
            right: 0;
            top: 15px;
        }

        .foot-wrap {
            background-color: #f5f5f5;
        }

        .eliback {
            color: white;
            background-color: #1E90FF;
            position: relative;
        }

        .navback {
            background-color: #428bca;
            color: white;
        }

        .gradient {
            background: #428bca;
            background: -moz-linear-gradient(top, #428bca 0%, #4169E1 100%);
            background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #428bca),
            color-stop(100%, #4169E1));
            background: -webkit-linear-gradient(top, #428bca 0%, #4169E1 100%);
            background: -o-linear-gradient(top, #428bca 0%, #4169E1 100%);
            background: -ms-linear-gradient(top, #428bca 0%, #4169E1 100%);
            background: linear-gradient(to bottom, #428bca 0%, #4169E1 100%);
            filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#428bca',
            endColorstr='#4169E1', GradientType=0);
        }

        .modal-backdrop {
            position: fixed;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            z-index: 0;
            background-color: #000;
        }

        #mytooltip {
            border: 2px solid #1C6EA4;
            border-radius: 5px;
            background: #FFFFFF;
            top: auto;
            bottom: auto;
            width: 600px;
            height: fit-content;
            -moz-transform: scale(0.8);
            -webkit-transform: scale(0.8);
            -o-transform: scale(0.8);
            -ms-transform: scale(0.8);
            transform: scale(0.8);
        }

        highlight {
            border: 2px dashed #1C6EA4
        }

        #ent_select {
            font-size: 18px;
        }

        .height-change {
            font-size: 18px;
            height: 50px;
            min-height: auto !important;
        }

        .form-group .ui.dropdown .menu > .item {
            font-size: 18px;
        }

        .form-group .ui.search > .results .result .title {
            font-size: 18px;
        }

        .form-group .ui.search > .results .result .description {
            font-size: 18px;
        }

        .form-group .ui.search > .results .result .price {
            font-size: 16px;
        }

        .form-group .ui.search > .results {
            font-size: 18px;
        }

        .form-group .ui.selection.dropdown .menu {
            max-height: 23rem;
        }

        #initialeventtable td {
            line-height: 2.3;
        }

        #intable td {
            line-height: 2.3;
        }

        #extable td {
            line-height: 2.3;
        }

        .tooltip {
            font-size: 16px;
        }


    </style>

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <!-- Custom styles for this template -->
    <link href="./css/dashboard.css" rel="stylesheet">
    <link href="./css/ner.css" rel="stylesheet">

    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="./css/site.min.css?v5"
          rel="stylesheet">


    <link href="https://unpkg.com/bootstrap-table@1.17.1/dist/bootstrap-table.min.css" rel="stylesheet">
    <link rel="stylesheet" href="//apps.bdimg.com/libs/jqueryui/1.10.4/css/jquery-ui.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <script src="//apps.bdimg.com/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
            integrity="sha512-Ua/7Woz9L5O0cwB/aYexmgoaD7lw3dWe9FvXejVdgqu71gRog3oJgjSWQR55fwWx+WKuk8cl7UwA1RS6QCadFA=="
            crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"
            integrity="sha384-aJ21OjlMXNL5UyIl/XNwTMqvzeRMZH2w8c5cRVpzpU8Y5bApTppSuUkhZXN0VxHd"
            crossorigin="anonymous"></script>



    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]>
    <script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="./js/ie-emulation-modes-warning.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="./js/ie10-viewport-bug-workaround.js"></script>
    <script src="./js/bootstrap-table.min.js"></script>
    <script type="text/javascript"
            src="./js/jquery.blockUI.js"></script>


    <!--Export result-->
    <script src="https://unpkg.com/bootstrap-table@1.17.1/dist/bootstrap-table.min.js"></script>
    <script src="https://unpkg.com/bootstrap-table@1.17.1/dist/extensions/export/bootstrap-table-export.min.js"></script>
    <script src="https://unpkg.com/tableexport.jquery.plugin/tableExport.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.15.4/extensions/print/bootstrap-table-print.js"></script>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/components/transition.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/components/transition.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.js"
            integrity="sha512-dqw6X88iGgZlTsONxZK9ePmJEFrmHwpuMrsUChjAw1mRUhUITE5QU9pkcSox+ynfLhL15Sv2al5A0LVyDCmtUw=="
            crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/components/dropdown.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/components/dropdown.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.2/FileSaver.js"
            integrity="sha512-Y36f1QBUtewxhuL8VzWzj6xgtHm4CTgYSdvW21mA6YZBduo6VjvGj79BKUhTqDU4xI9NpVMvCvOxByTsKlh1iQ=="
            crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.1/font/bootstrap-icons.css">

    <style>a {
        TEXT-DECORATION: none
    }</style>
    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-118282453-1"></script>
    <script>
        window.dataLayer = window.dataLayer || [];

        function gtag() {
            dataLayer.push(arguments);
        }

        gtag('js', new Date());
        gtag('config', 'UA-118282453-1');
    </script>


    <script type="text/javascript">

        function formReset() {
            document.getElementById("myForm").reset()
        }
    </script>

</head>
<body>

<div class="navbar navbar-inverse navbar-fixed-top navback">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand hidden-sm" href="./"
               style="color: white"
               onclick="_hmt.push(['_trackEvent', 'navbar', 'click', 'navbar-mainpage'])">Criteria2Query</a>
        </div>
        <div class="navbar-collapse collapse" role="navigation">
            <ul class="nav navbar-nav">

            </ul>
            <ul class="nav navbar-nav navbar-right hidden-sm">
                <li><a style="color: white" data-toggle="modal"
                       data-target="#myModal">Support</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="gradient masthead">
    <div class="container">
        <h1 style="font-size: 300%">Criteria2Query</h1>
        <h3>A system for automatically transforming clinical research
            eligibility criteria to OMOP Common Data Model-based executable
            cohort queries.</h3>
        <p class="masthead-button-links">

            <a class="btn btn-lg btn-info" id="feedback" data-toggle="modal"
               data-target="#myModal" role="button" style="font-size: 17px">&nbsp;&nbsp;FeedBack&nbsp;&nbsp;</a>
            <button id="playVideo" class="btn btn-lg btn-warning" style="font-size: 17px">
                <i class='bi bi-youtube'></i>
                Demonstration and Tutorial Video

            </button>
        </p>
    </div>
</div>


<div class="container projects">
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog"
         aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                            aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">Comments &
                        Suggestions</h4>
                </div>
                <p align="center">
                    <iframe src="https://docs.google.com/forms/d/e/1FAIpQLSfIRr_9jSkyHRoEDCJKdPpMIxUTDKUrB2meJ2ILA4BI3Hp6Sw/viewform?embedded=true"
                            width="500" height="600" frameborder="0" marginheight="0" marginwidth="0">Loading…</iframe>
                </p>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalCategoryExamples" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
         aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Categories of Medical Terms </h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <picture>
                        <img src="./img/categoryExample.png" class="img-fluid img-thumbnail">
                    </picture>
                    <span>
                        * Terms under these four categories don’t have nor need to be mapped a concept due to their semantics.
                    </span>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="projects-header">
        </div>

        <div class="col-sm-12 col-md-12 col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseNCT"><span
                                class="glyphicon glyphicon-download-alt"></span>
                            Criteria Resource
                        </a>
                    </h4>
                </div>
                <div id="collapseNCT" class="panel-collapse collapse in">
                    <div class="panel-body">
                        <div class="form-group col-sm-12 col-md-12 col-lg-12">
                            <form class="form-inline">
                                <div class="form-group">
                                    <label>Please input a NCTID :</label>
                                    <input class="input-sm" type="text" id="nctid" name="nctid"
                                           placeholder="e.g., NCT01640873">
                                    <a class="btn btn-primary" id="fetchct" role="button"
                                       style="font-size: 16px;">Extract Criteria</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-12 col-md-12 col-lg-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion"
                           href="#collapseTwo"><span class="glyphicon glyphicon-ok"></span>
                            Inclusion Criteria
                        </a>
                    </h4>
                </div>
                <div id="collapseTwo" class="panel-collapse collapse in">
                    <div class="panel-body">
                        <div class="form-group">
                            <span class="help-block">Please input criteria line by line.
                            </span>
                            <textarea class="form-control" rows="12" id="incriteria"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-12 col-md-12 col-lg-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion"
                           href="#collapseThree"><span class="glyphicon glyphicon-remove"></span>
                            Exclusion Criteria
                        </a>
                    </h4>
                </div>
                <div id="collapseThree" class="panel-collapse collapse in">
                    <div class="panel-body">
                        <div class="form-group">
                            <span class="help-block">Please input criteria line by line.
                            </span>
                            <textarea class="form-control" rows="12" id="excriteria"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-12 col-md-12 col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion"
                           href="#collapseOne"
                           title="The cohort entry event defines the time when people enter the cohort. A cohort entry event can be any event such as drug exposures, conditions, procedures, measurements and visits."><span
                                class="glyphicon glyphicon-dashboard"></span>
                            Cohort Entry Events (Optional)
                        </a>
                    </h4>
                </div>
                <div id="collapseOne" class="panel-collapse collapse" style="height: 0px;">

                    <div class="panel-body">
                        <div class="form-group">
								<span class="help-block"><strong>Initial event
										cohort:</strong> Initial events determine whether a person enter a cohort, which generates the initial event cohort.
                                    Inclusion and exclusion criteria will then be applied to this initial event cohort, which generates the final cohort.<br>
                                    <strong>Initial event:</strong>
                                    Initial events are recorded time-stamped observations for the
									persons, such as drug exposures, conditions, procedures,
									measurements and visits. All events have a start date and end
									date, though some events may have a start date and end date
									with the same value (such as procedures or measurements). The
									event index date is set to be equal to the event start date.<br>
                                For more information, please visit
                                    <a target="_blank"
                                       href="https://ohdsi.github.io/TheBookOfOhdsi/Cohorts.html">the Book of OHDSI
                                    </a>. </span>
                            <textarea class="form-control" rows="5" id="initialevent"></textarea>
                        </div>
                        <div class="form-group">
                            <span>observation occurring between </span>
                            <input class="input-sm" type="text" id="startdatepicker">
                            <span>and</span>
                            <input class="input-sm" type="text" id="enddatepicker">
                        </div>
                        <div class="form-group">
                            <span>with continuous observation of at least</span>
                            <select name="obstart" id="obstart">
                                <option value="0">0</option>
                                <option value="1">1</option>
                                <option value="7">7</option>
                                <option value="14">14</option>
                                <option value="21">21</option>
                                <option value="30">30</option>
                                <option value="60">60</option>
                                <option value="90">90</option>
                                <option value="120">120</option>
                                <option value="180">180</option>
                                <option value="365">365</option>
                                <option value="548">548</option>
                                <option value="730">730</option>
                                <option value="1095">1095</option>
                            </select>
                            <span>days before and </span>
                            <select name="obend" id="obend">
                                <option value="0">0</option>
                                <option value="1">1</option>
                                <option value="7">7</option>
                                <option value="14">14</option>
                                <option value="21">21</option>
                                <option value="30">30</option>
                                <option value="60">60</option>
                                <option value="90">90</option>
                                <option value="120">120</option>
                                <option value="180">180</option>
                                <option value="365">365</option>
                                <option value="548">548</option>
                                <option value="730">730</option>
                                <option value="1095">1095</option>
                            </select>
                            <span>days after event index date </span>
                        </div>
                        <div class="form-group">
                            <span>Limit initial events to:</span>
                            <select name="limitto" id="limitto">
                                <option value="All">all events</option>
                                <option value="First">earliest event</option>
                                <option value="Last">latest event</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%--        <div class="col-sm-12 col-md-12 col-lg-12">--%>
        <%--            <div class="panel panel-default">--%>
        <%--                <div class="panel-heading">--%>
        <%--                    <h4 class="panel-title">--%>
        <%--                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseConfig" aria-expanded="false"--%>
        <%--                           class="collapsed"><span class="glyphicon glyphicon-cog"></span>--%>
        <%--                            Configuration--%>
        <%--                        </a>--%>
        <%--                    </h4>--%>
        <%--                </div>--%>
        <%--                <div id="collapseConfig" class="panel-collapse collapse" aria-expanded="false" style="height: 0px;">--%>
        <%--                    <div class="panel-body">--%>
        <%--                        <div class="form-group col-sm-12 col-md-12 col-lg-12">--%>
        <%--                            <form class="form-inline">--%>
        <%--                                <div class="form-group col-sm-4 col-md-4 col-lg-4">--%>
        <%--                                    <div class="checkbox">--%>
        <%--                                        <label>--%>
        <%--                                            <input id="ml" type="checkbox" checked disabled> Machine Learning-based--%>
        <%--                                            Model (CRF model)--%>
        <%--                                        </label>--%>
        <%--                                    </div>--%>
        <%--                                </div>--%>
        <%--                                <div class="form-group col-sm-4 col-md-4 col-lg-4">--%>
        <%--                                    <div class="checkbox">--%>
        <%--                                        <label> <input id="rule" type="checkbox" checked disabled> Rule-based Model--%>
        <%--                                            (OHDSI Usagi)--%>
        <%--                                        </label>--%>
        <%--                                    </div>--%>
        <%--                                </div>--%>
        <%--                                <div class="form-group col-sm-4 col-md-4 col-lg-4">--%>
        <%--                                    <div class="checkbox">--%>
        <%--                                        <label> <input id="abbr" type="checkbox" checked> Abbreviation Extension (UMLS)--%>
        <%--                                        </label>--%>
        <%--                                    </div>--%>
        <%--                                </div>--%>
        <%--                            </form>--%>
        <%--                        </div>--%>
        <%--                        <!-- <div class="form-group col-sm-12 col-md-12 col-lg-12">--%>
        <%--                            <form class="form-inline">--%>
        <%--                            <div class="form-group col-sm-4 col-md-4 col-lg-4">--%>
        <%--                                    <div class="checkbox">--%>
        <%--                                        <label> <input id="recon" type="checkbox"> Coordination Ellipsis Reconstruction--%>
        <%--                                        </label>--%>
        <%--                                    </div>--%>
        <%--                            </div>--%>
        <%--                            </form>--%>
        <%--                        </div> -->--%>
        <%--                    </div>--%>
        <%--                </div>--%>
        <%--            </div>--%>
        <%--        </div>--%>
        <div class="col-sm-12 col-md-12 col-lg-12">
            <div class="masthead-button-links">
                <a role="button" class="btn btn-success" id="start" style="font-size: 17px;">&nbsp;&nbsp;Generate
                    Cohort&nbsp;&nbsp;</a>
                <a role="button" class="btn btn-info" id="reset" style="font-size: 17px;">&nbsp;&nbsp;&nbsp;Reset&nbsp;&nbsp;&nbsp;</a>

            </div>
        </div>
        <div id="sentenceParsingResult" class="col-sm-12 col-md-12 col-lg-12" style="display:none; margin-top:10px">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <span class="glyphicon glyphicon-list"></span>
                        Criteria Parsing Result
                    </h4>
                </div>
                <div class="panel-body">

                    <div class="help-block"><b>Please note:</b> <br>
                        1. Only those criteria with a check on the first column are used to query for potential patients
                        you see in your cohort result.
                        You can select or unselect a criterion by checking/unchecking the box to modify the query.<br>
                        2. Medical terms recognized by the system are highlighted in different colored tags.
                        The color indicates the category of a medical term
                        <a class="badge" id="categoryExamples" data-toggle="modal"
                           data-target="#modalCategoryExamples">(click to see examples)</a>
                        .You can edit the parsing results by adding, updating, deleting, or clearing all tags.<br>
                        3. Negation cue indicates the scope within a sentence where the phrase needs to be negated. <br>
                        4. The system will analyze the sentence structure and automatically link concepts to their
                        corresponding
                        <mark data-entity="temporal">temporal terms</mark>
                        and the
                        <mark data-entity="measurement">measurement</mark>
                        to their corresponding
                        <mark data-entity="value">values</mark>
                        .


                    </div>
                    <div class="ui four top attached steps">
                        <div class="step">
                            <i class="plus square icon"></i>
                            <div class="content">
                                <div class="title">Add a Tag</div>
                                <div class="description">
                                    Select the text you want and the annotation box will pop up.
                                </div>
                            </div>
                        </div>
                        <div class="step">
                            <i class="edit icon"></i>
                            <div class="content">
                                <div class="title">Update a Tag</div>
                                <div class="description">
                                    Move the mouse to the tag, <br>
                                    <b style="font-style: italic;">Method 1:</b><br>
                                    Right click the mouse and select the "update".<br>
                                    <b style="font-style: italic;">Method 2:</b><br>
                                    <b>Window System:</b> Press down Alt key and click the mouse<br>
                                    <b>Mac System:</b> Press down Option key and click the mouse
                                </div>
                            </div>
                        </div>
                        <div class="step">
                            <i class="trash alternate icon"></i>
                            <div class="content">
                                <div class="title">Delete a Tag</div>
                                <div class="description">
                                    Move the mouse to the tag, <br>
                                    <b style="font-style: italic;">Method 1:</b><br>
                                    Right click the mouse and select the "delete".<br>
                                    <b style="font-style: italic;">Method 2:</b><br>
                                    <b>Window System:</b> Press down Ctrl key and click the mouse<br>
                                    <b>Mac System:</b> Press down Command key and click the mouse
                                </div>
                            </div>
                        </div>
                        <div class="step">
                            <i class="erase alternate icon"></i>
                            <div class="content">
                                <div class="title">Delete all Tags in a Paragraph</div>
                                <div class="description">
                                    Click the button in the last column to delete all tags in a paragraph
                                    if none of them are wanted. You can then add new tags to this paragraph.
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--div id="initialTableToolbar"></div-->
                    <table id="initialeventtable"
                           class="table table-bordered col-sm-12 col-md-12 col-lg-12"
                           data-header-style="parsingResultTableHeaderStyle"
                           style="table-layout:fixed;">
                    </table>
                    <br/>
                    <!--div id="inTableToolbar"></div-->
                    <table id="intable"
                           class="table table-bordered col-sm-12 col-md-12 col-lg-12"
                           data-header-style="parsingResultTableHeaderStyle"
                           style="table-layout:fixed;">
                    </table>
                    <br/>
                    <!--div id="exTableToolbar"></div-->
                    <table id="extable"
                           class="table table-bordered col-sm-12 col-md-12 col-lg-12"
                           data-header-style="parsingResultTableHeaderStyle"
                           style="table-layout:fixed;">
                    </table>

                </div>
            </div>
        </div>

        <div class="col-sm-12 col-md-12 col-lg-12" id="buttons_group" style="display: none">
            <!--a class="btn btn-success" id="auto" style="display:none" role="button">&nbsp;&nbsp;Identify Patients in ATLAS&nbsp;&nbsp;</a-->
            <div style="display:inline-block; ">
                <a role="button" id="processData" class="btn btn-success" style="font-size: 17px;">
                    Generate Cohort Again
                </a>
            </div>


            <div class="dropdown" style="display: inline-block">
                <button class="btn btn-primary dropdown-toggle" type="button" id="downloadQuery"
                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                        style="display:inline-block; font-size: 17px; ">
                    Download Cohort Query
                    <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" style="width: 100%" aria-labelledby="downloadQuery">
                    <li><a id="downloadJSONfile" href="#" style="font-size: 17px;">JSON File</a></li>
                    <li class="divider"></li>
                    <li class="dropdown-header" style="font-size: 17px;font-weight: bold;color: #778899">SQL Query</li>
                    <li><a id="downloadPostgreSQL" class="item sql" href="#" style="font-size: 17px;">PostgreSQL</a></li>
                    <li><a id="downloadMSSQL" class="item sql" href="#" style="font-size: 17px;">MSSQL_Server</a></li>
                </ul>
            </div>
        </div>

        <div id="resultSection" class="col-sm-12 col-md-12 col-lg-12" style="display:none;margin-top:10px">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <span class="glyphicon glyphicon-grain"></span>
                        Cohort/Patient Output
                    </h4>
                </div>
                <div class="panel-body">
                    <h4>Data Source:</h4>
                    <select id='data_select' class='ui fluid selection dropdown change_dataset'>
                        <option value="SynPUF1K" selected>SynPUF 1K dataset</option>
                        <option value="SynPUF5pct">SynPUF 5% dataset
                            (* Querying this database is time-consuming due to large data volume. The expected time of
                            query execution is approximately 30 seconds to 4 minutes per criterion.)
                        </option>
                    </select>
                    <p id="resultTips" style="color: #0d71bb;text-align: center; ">The patient table is empty,
                        which may due to the large number of criteria or concepts.</p>
                    <div id="resultTableToolbar" style="margin-top:10px"></div>
                    <table id="result"
                           class="table table-bordered col-sm-12 col-md-12 col-lg-12" style="table-layout:fixed;">
                    </table>
                </div>
            </div>
        </div>


    </div>

    <div class="modal" id="modalNumberPatients" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
         aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">

                    <h4>
                        Number of patients returned: <h3 id="numPatients"></h3>
                    </h4>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</div>


<!-- /.container -->
<footer class="footer foot-wrap">
    <div class="container">
        <div class="row footer-top">
            <div class="col-sm-12 col-lg-12">
                <p>
                    <strong>Criteria2query v2.4</strong>
                </p>
                <p>This website was developed by Yilu Fang, Yingcheng Sun, Hao Liu, Chi Yuan, Patrick Ryan, Yixuan Guo,
                    Chunhua Weng</p>
            </div>
        </div>
    </div>
</footer>

<script type="text/javascript">
    $('.ui.checkbox').checkbox();
    $(document).ajaxStop($.unblockUI);
    var datasetName = null;
    var conceptResult = null;
    var selectedNode = null;
    var $intable = $('#intable');
    var $extable = $('#extable');
    var $initialeventtable = $('#initialeventtable');
    var basePath = "./";

    var withoutConceptCategories = ["demographic", "negation_cue", "temporal", "value"];
    $(function () {
        $("#startdatepicker").datepicker({dateFormat: 'yy-mm-dd'});
        $("#enddatepicker").datepicker({dateFormat: 'yy-mm-dd'});
        $("#total").hide();
        $("#format").click(function () {
            formatdata();
        });
        $("#reset").click(function () {
            //$("#abbr").attr("checked",false);
            top.location = 'index.jsp';
        });
        //Click the "Extract Criteria" button and extract both the inclusion and exclusion criteria. If the value is Null, an alert will show up.
        $("#fetchct").click(function () {
            if ($("#nctid").val() == '') {
                alert('Please input a nctid!');
                return;
            }
            getCTinfo();
        });
        //Press the 'Enter' key after entering the NCTID and extract both the inclusion and exclusion criteria.
        //An alert will show up if the user doesn't enter anything in the input field before pressing the 'Enter' key.
        $('#nctid').bind('keypress', function (event) {
            if (event.keyCode == "13") {
                if ($("#nctid").val() == '') {
                    alert('Please input a nctid!');
                    return;
                }
                getCTinfo();

            }
        });
        $("#starttoinput").click(function () {
            var t = $(window).scrollTop();
            $('body,html').animate({
                'scrollTop': t + 450
            }, 200)
        })
        $("#mapping").click(function () {
            mapping();
        });
        $("#downloadfile").click(function () {
            downloadfile();
        });

        // $("#auto").click(function () {
        //      autoparse();
        //  });
        $("#test").click(function () {
            testsys();
        });
        $("#intable").on("mouseup", "tbody tr .criterion", function (e) {
            addTag(e);
        });
        $("#extable").on("mouseup", "tbody tr .criterion", function (e) {
            addTag(e);
        });
        $("#initialeventtable").on("mouseup", "tbody tr .criterion", function (e) {
            addTag(e);
        });
        $("#intable").on("click", "tbody tr .criterion", function (e) {
            //prevent whole text selection triggered by triple click from happening
            if (e.detail >= 2) {
                clearSelection();
            }
        });
        $("#extable").on("click", "tbody tr .criterion", function (e) {
            //prevent whole text selection triggered by triple click from happening
            if (e.detail >= 2) {
                clearSelection();
            }
        });
        $("#initialeventtable").on("click", "tbody tr .criterion", function (e) {
            //prevent whole text selection triggered by triple click from happening
            if (e.detail >= 2) {
                clearSelection();
            }
        });
        $("#start").click(function () {
            parse("SynPUF 1K dataset");
            $('.ui.dropdown.change_dataset').dropdown('set selected', 'SynPUF1K');

        });

        $("#processData").click(function () {
            processData("SynPUF 1K dataset");
            $('.ui.dropdown.change_dataset').dropdown('set selected', 'SynPUF1K');

        });

        $("#downloadJSONfile").click(function () {
            downloadJSONfile();
        });
        $(".item.sql").click(function () {
            downloadSQLfile($(this).text());
        });

        //change dataset
        $('.ui.dropdown.change_dataset')
            .dropdown({
                action: 'activate',
                onChange: function (value, text, $selectedItem) {
                    changeDataset(text);

                }
            })
        ;
        $('#playVideo').click(function () {
            window.open('https://youtu.be/LJsWgE0EZ-o', '_blank');
        });
        $(document).click(function () {
            $("#contextMenu").remove();
        });


    })
    function parsingResultTableHeaderStyle(column) {
        return {
            criterion: {
                css: {'font-size': '17px',
                    'font-weight': 900}
            }
        }[column.field]
    }

    function downloadfile() {
        openNewWin(basePath + "ie/download", "download");
    }

    function openNewWin(url, title) {
        window.open(url);
    }



    //Format both the inclusion and exclusion criteria.
    function formatdata() {
        var inc = $("#incriteria").val();
        var exc = $("#excriteria").val();
        $.blockUI({
            message: '<h3><img src="' + basePath + '/img/squares.gif" /> Formatting...</h3>',
            css: {
                border: '1px solid khaki'
            }
        });
        $.ajax({
            type: 'POST',
            url: basePath + "nlpmethod/formatdata",
            data: {
                'inc': inc,
                'exc': exc
            },
            dataType: "json",
            success: function (data) {
                $("#incriteria").val(data["afterinc"]);
                $("#excriteria").val(data["afterexc"]);
            }
        })
    }

    //Extract both the inclusion criteria and exclusion criteria with the given NCTID.
    //If success, the inclusion and exclusion criteria will be returned to the text editable boxes respectively.
    //If an extraction failure occurs, an alert will show up.
    function getCTinfo() {
        var nctid = $("#nctid").val();
        $.blockUI({
            message: '<h3><img src="' + basePath + '/img/squares.gif" /> Data Loading...</h3>',
            css: {
                border: '1px solid khaki'
            }
        });
        $.ajax({
            type: 'POST',
            url: basePath + "ie/getct",
            data: {
                'nctid': nctid
            },
            dataType: "json",
            success: function (data) {
                /* alert("Success!"); */
                $("#incriteria").val(data["inc"]);
                $("#excriteria").val(data["exc"]);

            },
            error: function () {
                $(document).ajaxStop($.unblockUI);
                alert("Please check your NCTID");
            }

        });
    }

    function autoparse() {
        var inc = $("#incriteria").val();
        var exc = $("#excriteria").val();
        var initialevent = $("#initialevent").val();
        var rule = $("#rule").is(':checked');
        var ml = $("#ml").is(':checked');
        var abb = $("#abbr").is(':checked');
        var recon = $("#recon").is(':checked');
        var obstart = $("#startdatepicker").val();
        var obend = $("#enddatepicker").val();
        var daysbefore = $("#obstart").val();
        var daysafter = $("#obend").val();
        var limitto = $("#limitto").val();
        $.blockUI({
            message: '<h3><img src="' + basePath + '/img/squares.gif" /> Information Extracting...</h3>',
            css: {
                border: '1px solid khaki'
            }
        });
        $.ajax({
            type: 'POST',
            url: basePath + "main/autoparse",
            data: {
                'nctid': $('#nctid').val(),
                'inc': inc,
                'exc': exc,
                'initialevent': initialevent,
                'rule': rule,
                'ml': ml,
                'abb': abb,
                'recon': recon,
                'obstart': obstart,
                'obend': obend,
                'daysbefore': daysbefore,
                'daysafter': daysafter,
                'limitto': limitto
            },
            dataType: "json",
            timeout: 300000,
            success: function (data) {
                window.location.href = basePath + "main/gojson";
            },
            error: function (e) {
                alert("Oppps....");
            }
        })
    }

    function testsys() {
        var inc = $("#incriteria").val();
        var exc = $("#excriteria").val();
        var initialevent = $("#initialevent").val();
        var rule = $("#rule").is(':checked');
        var ml = $("#ml").is(':checked');
        var abb = $("#abbr").is(':checked');

        $.blockUI({
            message: '<h3><img src="' + basePath + '/img/squares.gif" /> Information Extracting...</h3>',
            css: {
                border: '1px solid khaki'
            }
        });
        $.ajax({
            type: 'POST',
            url: basePath + "main/testsys",//nlpmethod/parsebycdm
            data: {
                'inc': inc,
                'exc': exc,
                'initialevent': initialevent,
                'rule': rule,
                'ml': ml,
                'abb': abb

            },
            dataType: "json",
            success: function (data) {
                window.location.href = basePath + "main/gojson";
            },
            error: function (e) {
                alert("Oppps....");
            }
        })
    }

    function parse(dataset) {
        var inc = $("#incriteria").val().trim().replace(/^\s+|\s+$/g, '').trim();
        var exc = $("#excriteria").val().trim().replace(/^\s+|\s+$/g, '').trim();
        var initialevent = $("#initialevent").val().trim().replace(/^\s+|\s+$/g, '').trim();
        var rule = $("#rule").is(':checked');
        var ml = $("#ml").is(':checked');
        var abb = $("#abbr").is(':checked');
        var recon = $("#recon").is(':checked');
        var obstart = $("#startdatepicker").val();
        var obend = $("#enddatepicker").val();
        var daysbefore = $("#obstart").val();
        var daysafter = $("#obend").val();
        var limitto = $("#limitto").val();

        $.blockUI({
            message: '<h5><img src="' + basePath + '/img/squares.gif" />Generating query based on your input...</h5>',
            // message: '<div id="progressbar" val=0></div>',
            css: {
                border: '1px solid khaki'
            }
        });


        $.ajax({
            type: 'POST',
            //url: basePath + "ie/parse",//nlpmethod/parsebycdm
            url: basePath + "main/runPipeline",
            data: {
                'nctid': $('#nctid').val(),
                'dataset': dataset,
                'inc': inc,
                'exc': exc,
                'initialevent': initialevent,
                'rule': rule,
                'ml': ml,
                'abb': abb,
                'recon': recon,
                'obstart': obstart,
                'obend': obend,
                'daysbefore': daysbefore,
                'daysafter': daysafter,
                'limitto': limitto
            },
            dataType: "json",

            //async: false,
            //contentType: "json",

            success: function (data) {
                $('#initialeventtable').bootstrapTable('destroy');
                $('#initialeventtable').bootstrapTable(
                    {
                        formatNoMatches: function () {
                            return '';
                        },
                        cache: false,
                        data: data["display_initial_event"],
                        columns: [
                            {
                                title: 'Select',
                                width: '5%',
                                checkbox: 'true',
                                align: 'center',
                                valign: 'middle',
                                field: 'ehrstatus',
                                formatter: function (
                                    value, row, index) {
                                    return value;
                                }
                            },
                            {
                                field: 'id',
                                title: '#',
                                width: '5%',
                                disable: "true"
                            },
                            {
                                field: 'criterion',
                                width: '80%',
                                title: 'Initial Events:',
                                class: 'criterion'
                            },
                            {
                                field: 'clear',
                                width: "10.5%",
                                title: 'Delete All Tags',
                                align: 'center',
                                valign: 'middle',
                                events: {
                                    'click .clear': function (e, value, row, index) {
                                        groups = row.criterion.match(/<mark.*?>*?<\/mark>/g);
                                        for (i = 0; i < groups.length; i++) {
                                            entity = groups[i].match(/data-entity="(.*?)"/)[1];
                                            if (withoutConceptCategories.includes(entity)) {
                                                term = groups[i].match(/">(.*?)<\/mark>/)[1];
                                            } else {
                                                items = groups[i].match(/concept-id="(.*?)">(.*?)<b><i>\[(.*?)\]<\/i><\/b>/);
                                                conceptID = items[1];
                                                term = items[2];
                                                conceptName = items[3];
                                            }
                                        }
                                        new_text = row.criterion.replace(/<[/]*mark.*?>/g, "");
                                        new_text = new_text.replace(/<b>.*?<[/]b>/g, "");
                                        $('#initialeventtable').bootstrapTable('updateCell', {
                                            index: index,
                                            field: 'criterion',
                                            value: new_text
                                        });
                                        $('#initialeventtable').bootstrapTable('uncheck', index);
                                        deleteAndUpdateTag();
                                    }
                                },
                                formatter: function (value, row, index) {
                                    return '<button class="clear btn btn-default" data-toggle="tooltip" title="Delete all tags">' +
                                        '<i class="erase alternate icon" aria-hidden="true"></i></button>'
                                    // '<span class="glyphicon glyphicon-erase" aria-hidden="true"></span></button> '
                                }

                            }

                        ]
                    });
                $('#intable').bootstrapTable('destroy');
                $('#intable').bootstrapTable(
                    {
                        formatNoMatches: function () {
                            return '';
                        },
                        style: "word-break:break-all; word-wrap:break-all;",
                        cache: false,
                        data: data["display_include"],
                        columns: [
                            {
                                title: 'Select',
                                width: '5%',
                                checkbox: 'true',
                                align: 'center',
                                valign: 'middle',
                                field: 'ehrstatus',
                                formatter: function (
                                    value, row, index) {
                                    return value;
                                }
                            },
                            {
                                field: 'id',
                                title: '#',
                                width: '5%',
                                disable: true
                            },
                            {
                                field: 'criterion',
                                width: '80%',
                                class: 'criterion',
                                title: 'Inclusion Criteria:'
                            },
                            {
                                field: 'clear',
                                width: "10.5%",
                                title: 'Delete All Tags',
                                align: 'center',
                                valign: 'middle',
                                events: {
                                    'click .clear': function (e, value, row, index) {
                                        groups = row.criterion.match(/<mark.*?>*?<\/mark>/g);
                                        for (i = 0; i < groups.length; i++) {
                                            entity = groups[i].match(/data-entity="(.*?)"/)[1];
                                            if (withoutConceptCategories.includes(entity)) {
                                                term = groups[i].match(/">(.*?)<\/mark>/)[1];
                                            } else {
                                                items = groups[i].match(/concept-id="(.*?)">(.*?)<b><i>\[(.*?)\]<\/i><\/b>/);
                                                conceptID = items[1];
                                                term = items[2];
                                                conceptName = items[3];
                                            }
                                        }
                                        new_text = row.criterion.replace(/<[/]*mark.*?>/g, "");
                                        new_text = new_text.replace(/<b>.*?<[/]b>/g, "");
                                        $('#intable').bootstrapTable('updateCell', {
                                            index: index,
                                            field: 'criterion',
                                            value: new_text
                                        });
                                        $('#intable').bootstrapTable('uncheck', index);
                                        deleteAndUpdateTag();

                                    }
                                },
                                formatter: function (value, row, index) {
                                    return '<button class="clear btn btn-default" data-toggle="tooltip" title="Delete all tags">' +
                                        '<i class="erase alternate icon" aria-hidden="true"></i></button>'
                                }

                            }

                        ]
                    });
                $('#extable').bootstrapTable('destroy');
                $('#extable').bootstrapTable(
                    {
                        formatNoMatches: function () {
                            return '';
                        },
                        cache: false,
                        data: data["display_exclude"],
                        columns: [
                            {
                                title: 'Select',
                                width: '5%',
                                checkbox: 'true',
                                align: 'center',
                                valign: 'middle',
                                field: 'ehrstatus',
                                formatter: function (
                                    value, row, index) {
                                    return value;
                                }
                            },
                            {
                                field: 'id',
                                title: '#',
                                width: '5%',
                                disable: true
                            },
                            {
                                field: 'criterion',
                                width: '80%',
                                class: 'criterion',
                                title: 'Exclusion Criteria:'
                            },
                            {
                                field: 'clear',
                                width: "10.5%",
                                title: 'Delete All Tags',
                                align: 'center',
                                valign: 'middle',
                                events: {
                                    'click .clear': function (e, value, row, index) {
                                        groups = row.criterion.match(/<mark.*?>*?<\/mark>/g);
                                        for (i = 0; i < groups.length; i++) {
                                            entity = groups[i].match(/data-entity="(.*?)"/)[1];
                                            if (withoutConceptCategories.includes(entity)) {
                                                term = groups[i].match(/">(.*?)<\/mark>/)[1];
                                            } else {
                                                items = groups[i].match(/concept-id="(.*?)">(.*?)<b><i>\[(.*?)\]<\/i><\/b>/);
                                                conceptID = items[1];
                                                term = items[2];
                                                conceptName = items[3];
                                            }
                                        }
                                        new_text = row.criterion.replace(/<[/]*mark.*?>/g, "");
                                        new_text = new_text.replace(/<b>.*?<[/]b>/g, "");
                                        $('#extable').bootstrapTable('updateCell', {
                                            index: index,
                                            field: 'criterion',
                                            value: new_text
                                        });
                                        $('#extable').bootstrapTable('uncheck', index);
                                        deleteAndUpdateTag();
                                    }
                                },
                                formatter: function (value, row, index) {
                                    return '<button class="clear btn btn-default" data-toggle="tooltip" title="Delete all tags">' +
                                        '<i class="erase alternate icon" aria-hidden="true"></i></button>'
                                    // '<span class="glyphicon glyphicon-erase" aria-hidden="true"></span></button> '
                                }

                            }
                        ]
                    });
                $('#result').bootstrapTable('destroy');
                $('#result').bootstrapTable({
                    contentType: "application/x-www-form-urlencoded",
                    //height:800,
                    //buttonsOrder: ['columns', 'toggle'],
                    buttonsToolbar: "#resultTableToolbar",
                    buttonsClass: 'primary',
                    // search: true,
                    iconSize: 'lg',
                    // sortable: true,
                    showColumns: true,
                    showPaginationSwitch: true,
                    showPrint: true,
                    pagination: true,
                    pageNumber: 1,
                    queryParamsType: 'limit',
                    queryParams: queryParams,

                    // serverSort: false,
                    // silentSort: false,
                    sidePagination: "server",
                    showExport: true,
                    exportDataType: "all",
                    exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'excel'],
                    exportOptions: {
                        fileName: "Criteria2Query_Cohort_Data",
                    },
                    showToggle: true,
                    pageList: [10, 25, 50, 100, 500, 'All'],
                    method: 'post',
                    url: basePath + "main/queryAgencyPage",
                    //data: data['queryResult'],
                    columns: [
                        {
                            // sortOrder: 'desc',
                            field: 'person_id',
                            title: 'person_id',
                            width: '20%'
                        },
                        {
                            // sortOrder: 'asc',
                            field: 'gender',
                            title: 'gender',
                            width: '25%'
                        },
                        {
                            // sortOrder: 'asc',
                            field: 'age',
                            title: 'age',
                            width: '25%'
                        },
                        {
                            // sortOrder: 'asc',
                            field: 'race',
                            title: 'race',
                            width: '30%'
                        }
                    ],

                    responseHandler: function (res) {
                        showTips(res.total);
                        $('#numPatients').text(res.total);

                        if (res.total===1){
                            if (res.rows[0]["person_id"]===("Please connect to a database. For now, you can only download the JSON file and SQL script.")){
                                $('#numPatients').text("Please connect to a database.");
                            }
                        }

                        return {
                            rows: res.rows,
                            total: res.total
                        }
                    }


                });


                var t = $(window).scrollTop();
                $('body,html').animate({
                    'scrollTop': t + 1000
                }, 200)
                $("#sentenceParsingResult").show();
                $("#mapping").show();
                $("#auto").show();
                $("#buttons_group").show();
                $("#resultSection").show();
                deleteAndUpdateTag();
                $('#modalNumberPatients').modal('show');

            },
            error: function (e) {
                console.log(e);
                alert('Parsing Error...');
            }
        });
    }


    function mapping() {
        window.location.href = basePath + "nlpmethod/conceptset";
    }


    //Add options to the category/domain dropdown selection
    var dynamic_select = function (select_ele) {
        select_ele.empty();
        select_ele.append("<option value=''>" + "" + "</option>");
        select_ele.append("<option value='" + "demographic" + "'>" + "* demographic" + "</option>");
        select_ele.append("<option value='" + "temporal" + "'>" + "* temporal" + "</option>");
        select_ele.append("<option value='" + "value" + "'>" + "* value" + "</option>");
        select_ele.append("<option value='" + "negation_cue" + "'>" + "* negation_cue" + "</option>");
        select_ele.append("<option value='" + "drug" + "'>" + "drug" + "</option>");
        select_ele.append("<option value='" + "condition" + "'>" + "condition" + "</option>");
        select_ele.append("<option value='" + "observation" + "'>" + "observation" + "</option>");
        select_ele.append("<option value='" + "measurement" + "'>" + "measurement" + "</option>");
        select_ele.append("<option value='" + "procedure" + "'>" + "procedure" + "</option>");
        select_ele.append("<option value='" + "device" + "'>" + "device" + "</option>");


    }

    function constructTooltip() {
        //Construct the tooltip for tag addition and update.
        var tooltip = "<div id='mytooltip' class='mytooltip' role='tooltip' style='background-color: white;'>" +

            "<button type='submit' class='btn btn-warning' style='margin:10px;' " +
            "data-toggle='tooltip' data-placement='right' " +
            "title='The dialog box is for two tasks: concept searching and category selection. It can be used to add or update a tag for a medical term with its category and mapped concepts.'>" +
            "  <i  class=' fa-lg bi bi-info-square-fill'></i>" +
            "</button>" +

            "<div  class='form-group' style=' border-width: medium;  text-align:center; align: center; width:95%; margin: 0 auto;'>" +
            "<h1 id='termText' for='name' style='position: relative;margin-top:5%;'>...</h1>" +

            "<p for='name' style='font-size:20px; text-align: justify; position: relative; color: #1C6EA4; font-weight: 700; margin-top: 5%;'>Concept Name: <br>" +
            "<a style='color: black; text-align: left; font-size:16px; font-weight: 500;'>1. Terms with category 'Temporal', 'Value', 'Negation_cue', 'Demographic' don't have matched concepts. Please directly select a domain below.</a> <br>" +
            "<a style='color: black; text-align: left; font-size:16px; font-weight: 500;'>2. The concept search function is supported by </a> " +
            "<a style='color: black; text-align: left; font-size:16px; font-weight: 500; text-decoration: underline' href='https://athena.ohdsi.org/search-terms/start' target=\"_blank\">ATHENA</a>" +
            "<a style='color: black; text-align: left; font-size:16px; font-weight: 500;' >.</a> <br>" +
            "<a style='color: black; text-align: left; font-size:16px; font-weight: 500;'>3. If you do not find an appropriate option, please try the mode All Concepts.</a>" +
            "</p >" +


            "<div class='ui form'>" +
            "<div class='inline fields'>" +
            "<div class='field'>" +
            "<div class='ui radio checkbox'>" +
            "<input id='standard_concept_radio' type='radio' name='concept types' checked='checked' style='width: 10px; height: 10px' class='hidden'>" +
            "<label data-toggle='tooltip' data-placement='bottom' title='Standard Concepts are those concepts that are used to define the meaning of a clinical entity uniquely across all databases and independent from the coding system used in the sources' " +
            "style='font-size: 19px; '>Standard Concepts Only (Recommended)</label>" +
            "</div>" +
            "</div>" +
            "<div class='field'>" +
            "<div class='ui radio checkbox'>" +
            "<input id='all_concept_radio' type='radio' name='concept types' style='width: 10px; height: 10px' class='hidden'>" +
            "<label data-toggle='tooltip' data-placement='bottom' style='font-size: 19px; ' title='All Concepts include Standard and Non-standard Concepts. Non-standard Concepts that have the equivalent meaning to a Standard Concept have a mapping to the Standard Concept in the Standardized Vocabularies. We will directly recommend their mapped Standard Concepts. '>All Concepts</label>" +
            "</div> </div> </div> </div>" +

            "<p id='searchTip' style='color: #CB4335 ; text-align: left; font-size:18px; display: none'>Please search a concept.</p>" +
            "<div id='conceptNameSearchPopUp' class='ui fluid search height-change' style=' border-width: medium; align: center;' >" +
            "<div id='conceptNameSearchBox' class='ui icon input large' style=' width:100%'>" +
            "<input id='conceptName' style='font-size: 18px;' class='prompt' type='text' placeholder='Search concept name...'>" +
            "<i class='search icon'></i>" +
            "</div>" +
            "</div>" +
            "<p for='name' style='font-size:20px; text-align:left; position: relative; color: #1C6EA4; font-weight: 700;margin-top:3%;'>Category/Domain:</p>" +
            "<p id='domainTip' style='color: #CB4335 ; text-align: left; font-size:18px;display: none'>Please select a domain.</p>" +
            "<select size = '10' id= 'ent_select' class='ui fluid selection dropdown height-change domain'>" +
            "<option>1</option>" +
            "</select>" +
            "<a role='button' type='submit' id='tip_submit' class='btn btn-primary'  style='font-size:20px; margin:auto; margin-top:5%; margin-bottom: 5% ' >Apply</a>" +
            "<a role='button' id='cancel_submit' class='btn btn-default'  style='font-size:20px; margin:auto;margin-left:5%; margin-top:5%; margin-bottom: 5% ' >Cancel</a>" +
            "</div>" + "</div>";
        return tooltip;
    }

    function addTag(e) {
        //function for adding a tag.
        conceptResult = null;
        var r = "";
        $("#mytooltip").remove();
        // //remove all the tags with name "highlight".
        var hList = document.getElementsByTagName("highlight");
        for (var i = 0; i < hList.length; i++) {
            var node = hList[i];
            var pa = node.parentNode;
            var textnode = document.createTextNode(decodeHtml(node.innerHTML));
            pa.insertBefore(textnode, node);
            pa.removeChild(node);
        }
        var flag = false;
        //highlight the text
        if (document.selection) {
            var selection = document.selection.createRange();
            r = selection.text;
            if (r != null && r != " " && r != "") {
                selection.select();
                selection.pasteHTML("<highlight>" + selection.text + "<highlight/>");
                flag = true;
            }
        } else if (window.getSelection().focusNode.parentNode.nodeName == 'TD') {
            var selection = window.getSelection();//Get the highlighted text.
            r = selection.toString();
            if (r != null && r != " " && r != "") {
                var range = selection.getRangeAt(0);

                var fragment = range.cloneContents();
                var div = document.createElement('div');
                div.appendChild(fragment.cloneNode(true));
                var html = div.innerHTML;

                if (!html.includes("</mark>")) {
                    var span = document.createElement("highlight");
                    range.surroundContents(span);
                    flag = true;
                }
            }
        }


        if (flag == true) {
            var tooltip = constructTooltip();//Construct a tooltip.
            $("body").append(tooltip);
            $('[data-toggle="tooltip"]').tooltip();
            if (e.pageX + document.getElementById('mytooltip').clientWidth + 10 > document.body.clientWidth) {
                x = document.body.clientWidth - 10 - document.getElementById('mytooltip').clientWidth;
            } else {
                x = e.pageX - 10;
            }
            $("#mytooltip").css({
                "top": e.pageY + "px",
                "left": x + "px",
                "position": "absolute"
            }).show("fast");
            var select_one = $('#ent_select');
            dynamic_select(select_one);//Update the options of the tag category selection.
            $('.ui.dropdown.domain').dropdown();
            $('.ui.checkbox').checkbox();
            $("#termText").text(r.trim());


            $('.ui.checkbox.standard_concept').checkbox('check');

            $('.ui.search').search('set value', r.trim());
            $('#conceptName').css({'color': 'grey', 'font-style': 'italic'});
            $('.ui.search')
                .search({
                    minCharacters: 3,
                    cache: false,
                    type: 'category',
                    maxResults: 8,

                    apiSettings: {
                        beforeSend: function (settings) {
                            // if ($('.ui.checkbox.standard_concept').checkbox('is checked')) {
                            if ($('#standard_concept_radio').prop('checked')) {
                                settings.url = 'https://athena.ohdsi.org/api/v1/concepts?pageSize=15&standardConcept=Standard&domain%5B0%5D=Condition&domain%5B1%5D=Device&domain%5B2%5D=Drug&domain%5B3%5D=Measurement&domain%5B4%5D=Observation&domain%5B5%5D=Procedure&page=1&query={query}&boosts=';
                            } else {
                                settings.url = 'https://athena.ohdsi.org/api/v1/concepts?pageSize=15&domain%5B0%5D=Condition&domain%5B1%5D=Device&domain%5B2%5D=Drug&domain%5B3%5D=Measurement&domain%5B4%5D=Observation&domain%5B5%5D=Procedure&page=1&query={query}&boosts=';

                            }
                            return settings;
                        },

                        onResponse: function (athenaResponse) {
                            var
                                response = {
                                    results: {}
                                }
                            ;
                            // translate ATHENA API response to work with search
                            $.each(athenaResponse.content, function (index, item) {
                                var
                                    domain = item.domain || 'Unknown',
                                    maxResults = 8
                                ;
                                if (index >= maxResults) {
                                    return false;
                                }
                                //create new language category
                                if (response.results[domain] === undefined) {
                                    response.results[domain] = {
                                        name: domain,
                                        results: []
                                    };
                                }
                                // add result to category
                                var flag = true;
                                if (item.standardConcept != "Standard") {
                                    var targetConceptName = "", targetConceptId = "";
                                    $.ajax({
                                        type: 'GET',
                                        url: 'https://athena.ohdsi.org/api/v1/concepts/' + item.id + '/relationships?std=false',
                                        dataType: 'JSON',
                                        async: false,
                                        success: function (data) {
                                            var itms = data['items'];
                                            for (let i = 0; i < itms.length; i++) {
                                                var itm = itms[i]
                                                if (itm['relationshipName'] === "Non-standard to Standard map (OMOP)") {
                                                    targetConceptId = itm['relationships'][0]['targetConceptId'];
                                                    targetConceptName = itm['relationships'][0]['targetConceptName'];
                                                    break;
                                                }
                                            }
                                        }
                                    })
                                    if (targetConceptName != "") {
                                        var targetDomain = "";
                                        $.ajax({
                                            type: 'GET',
                                            async: false,
                                            url: 'https://athena.ohdsi.org/api/v1/concepts?pageSize=1&standardConcept=Standard&domain%5B0%5D=Condition&domain%5B1%5D=Device&domain%5B2%5D=Drug&domain%5B3%5D=Measurement&domain%5B4%5D=Observation&domain%5B5%5D=Procedure&page=1&query=' + targetConceptId + '&boosts=',
                                            dataType: 'JSON',
                                            success: function (data) {
                                                for (let i = 0; i < data.content.length; i++) {
                                                    if (data.content[i]['id'] == targetConceptId) {
                                                        targetDomain = data.content[i]['domain']
                                                        break
                                                    }
                                                }
                                            },
                                            error: function (e) {
                                                console.log(e);
                                            }

                                        })
                                        if (targetDomain != "") {
                                            flag = false;
                                            response.results[domain].results.push({
                                                title: targetConceptName,
                                                description: targetConceptId + " [mapped from non-standard concept: " + item.name + " (" + item.id + ")]",
                                                price: "Standard",
                                                domain: targetDomain,
                                                name: targetConceptName,
                                                id: targetConceptId
                                            });

                                        }
                                    }
                                }
                                if (flag) {
                                    response.results[domain].results.push({
                                        title: item.name,
                                        description: item.id,
                                        price: item.standardConcept,
                                        domain: item.domain,
                                        name: item.name,
                                        id: item.id
                                    });
                                }
                            });
                            return response;
                        }
                    },
                    onSelect: function (result) {
                        conceptResult = result;
                        $('.ui.dropdown.domain').dropdown('set selected', conceptResult.domain.toLowerCase());
                        $("#searchTip").hide();
                    }
                });

            $("#cancel_submit").click(function () {
                $("#mytooltip").remove();
                //Delete tags with name "highlight".
                var hList = document.getElementsByTagName("highlight");
                for (var i = 0; i < hList.length; i++) {
                    var node = hList[i];
                    var pa = node.parentNode;
                    var textnode = document.createTextNode(node.innerHTML);
                    pa.insertBefore(textnode, node);
                    pa.removeChild(node);
                }
            })

            $(".ui.dropdown.domain").on("change", function () {
                var ent_selected = $("#ent_select").dropdown("get value");
                if (["demographic", "temporal", "value", "negation_cue"].indexOf(ent_selected) > -1) {
                    $("#conceptName").css("background-color", "#D7DBDD");
                    $("#searchTip").hide();
                } else {
                    $("#conceptName").css("background-color", "#ffffff");
                }
                $('#conceptName').css({'color': 'black', 'font-style': 'normal'});
                $("#domainTip").hide();
            });

            $("#tip_submit").click(function () {
                var ent_selected = $("#ent_select").dropdown("get value");
                if (ent_selected === null) {
                    $("#domainTip").show();
                    $("#searchTip").show();
                } else {
                    if (["demographic", "temporal", "value", "negation_cue"].indexOf(ent_selected) === -1) {
                        addTagwithConcept(ent_selected);
                    } else {
                        addTagwithoutConcept(ent_selected);
                    }
                }
                //clear selection
                var sel = window.getSelection ? window.getSelection() : document.selection;
                if (sel) {
                    if (sel.removeAllRanges) {
                        sel.removeAllRanges();
                    } else if (sel.empty) {
                        sel.empty();
                    }
                }
            });

        }
    }


    //Add the tag without a concept. Only those terms with domain "Condition", "Observation",
    //"Measurement", "Drug", "Procedure", "Device" can be mapped to a concept. Otherwise, they don't have a matched concept.
    function addTagwithoutConcept(domain) {
        var ent_selected = domain;
        var hList = document.getElementsByTagName("highlight");
        node = hList[0];
        var term = node.firstChild.textContent;
        node.setAttribute('data-entity', ent_selected);
        var newNode = document.createElement("mark");
        newNode.setAttribute('data-entity', ent_selected);
        newNode.innerHTML = node.innerHTML;
        var pa = node.parentNode;
        pa.insertBefore(newNode, node);
        pa.removeChild(node);
        var index = pa.parentNode.getAttribute('data-index');
        $(pa.parentNode.parentNode.parentNode).bootstrapTable('check', index);
        $(pa.parentNode.parentNode.parentNode).bootstrapTable('updateCell',
            {index: index, field: 'criterion', value: pa.innerHTML});
        deleteAndUpdateTag();//Add the delete and update tag function to the new term.
        $("#mytooltip").remove();
        //Delete tags with name "highlight".
        for (var i = 0; i < hList.length; i++) {
            var node = hList[i];
            var pa = node.parentNode;
            var textnode = document.createTextNode(node.innerHTML);
            pa.insertBefore(textnode, node);
            pa.removeChild(node);
        }

    }

    //Add the tag with a concept. Only those terms with domain "Condition", "Observation",
    //"Measurement", "Drug", "Procedure", "Device" can be mapped to a concept.
    function addTagwithConcept(ent_selected) {
        var name_selected = $('.ui.search').search('get value');
        var hList = document.getElementsByTagName("highlight");
        if (!(conceptResult === null) && conceptResult.name === name_selected) {
            //If the user selects a concept from the concept list, and selects a domain,
            var hList = document.getElementsByTagName("highlight");
            node = hList[0];
            var term = node.firstChild.textContent;
            var pa = node.parentNode;
            var node_i = document.createElement("i");
            var textnode = document.createTextNode("[" + name_selected + "]");
            node_i.appendChild(textnode);
            var node_b = document.createElement("b");
            node_b.appendChild(node_i);
            node.innerHTML = node.innerHTML;
            node.appendChild(node_b);
            var newNode = document.createElement("mark");
            newNode.setAttribute('data-entity', ent_selected);
            //set attributes of the mapped concept
            newNode.setAttribute('concept-id', conceptResult.id);
            newNode.innerHTML = node.innerHTML;
            pa.insertBefore(newNode, node);
            pa.removeChild(node);
            var index = pa.parentNode.getAttribute('data-index');
            $(pa.parentNode.parentNode.parentNode).bootstrapTable('check', index);
            $(pa.parentNode.parentNode.parentNode).bootstrapTable('updateCell',
                {index: index, field: 'criterion', value: pa.innerHTML});
            deleteAndUpdateTag();//Add the delete and update tag function to the new term.
            $("#mytooltip").remove();
            //Delete tags with name "highlight".
            for (var i = 0; i < hList.length; i++) {
                var node = hList[i];
                var pa = node.parentNode;
                var textnode = document.createTextNode(node.innerHTML);
                pa.insertBefore(textnode, node);
                pa.removeChild(node);
            }
        } else {
            $("#searchTip").show();
        }

    };

    function constructContextMenu() {
        var buttonGroup = "<div id='contextMenu' class='btn-group-vertical'>" +
            "<button type='button' class='btn btn-default updateButton' style='font-weight:bold;'><i class='edit icon'></i>Update</button>" +
            "<button type='button' class='btn btn-default deleteButton' style='font-weight:bold;'><i class='trash alternate icon'></i>Delete</button>" +
            "<button type='button' class='btn btn-default cancelButton'>Cancel</button>" +
            "</div>"
        return buttonGroup;
    }

    function removeTag() {
        var pa = selectedNode.parentNode;
        var term = selectedNode.firstChild.textContent
        var oldDomain = selectedNode.getAttribute("data-entity");
        var oldID = selectedNode.getAttribute("concept-id")
        var oldConcept = "";
        if (selectedNode.lastChild.nodeName.toLowerCase() == 'b') {
            oldConcept = selectedNode.lastChild.textContent.slice(1, -1);
            selectedNode.removeChild(selectedNode.lastChild);
        }
        selectedNode.before(decodeHtml(selectedNode.innerHTML));
        pa.removeChild(selectedNode);
        var index = pa.parentNode.getAttribute('data-index');
        $(pa.parentNode.parentNode.parentNode).bootstrapTable('updateCell',
            {index: index, field: 'criterion', value: pa.innerHTML});
    }

    function updateTag(e) {
        conceptResult = null;
        if (document.getElementById("selected_term")) {
            document.getElementById("selected_term").removeAttribute("id");
        }
        var node = selectedNode;
        node.setAttribute("id", "selected_term");
        $("#mytooltip").remove();
        var tooltip = constructTooltip(); //Construct the tooltip.
        $("body").append(tooltip);
        $('[data-toggle="tooltip"]').tooltip();
        if (e.pageX + document.getElementById('mytooltip').clientWidth + 10 > document.body.clientWidth) {
            x = document.body.clientWidth - 10 - document.getElementById('mytooltip').clientWidth;
        } else {
            x = e.pageX - 10;
        }
        $("#mytooltip").css({
            "top": e.pageY + "px",
            "left": x + "px",
            "position": "absolute"
        }).show("fast");
        var select_one = $('#ent_select');
        dynamic_select(select_one); //Add the options to the category selection.
        $('.ui.dropdown.domain').dropdown();
        $('.ui.checkbox').checkbox();
        $("#termText").text(node.firstChild.textContent);

        $('.ui.dropdown.domain').dropdown('set selected', node.getAttribute("data-entity"));
        $('.ui.checkbox.standard_concept').checkbox('check');

        if (node.getElementsByTagName("i").length > 0) {
            var concept_name = node.getElementsByTagName("i")[0].innerText.slice(1, -1); //Remove the brackets
        }

        $('.ui.search').search('set value', concept_name);

        $('.ui.search')
            .search({
                minCharacters: 3,
                cache: false,
                type: 'category',
                maxResults: 8,
                apiSettings: {
                    beforeSend: function (settings) {
                        if ($('#standard_concept_radio').prop('checked')) {
                            settings.url = 'https://athena.ohdsi.org/api/v1/concepts?pageSize=15&standardConcept=Standard&domain%5B0%5D=Condition&domain%5B1%5D=Device&domain%5B2%5D=Drug&domain%5B3%5D=Measurement&domain%5B4%5D=Observation&domain%5B5%5D=Procedure&page=1&query={query}&boosts=';
                        } else {
                            settings.url = 'https://athena.ohdsi.org/api/v1/concepts?pageSize=15&domain%5B0%5D=Condition&domain%5B1%5D=Device&domain%5B2%5D=Drug&domain%5B3%5D=Measurement&domain%5B4%5D=Observation&domain%5B5%5D=Procedure&page=1&query={query}&boosts=';

                        }
                        return settings;
                    },

                    onResponse: function (athenaResponse) {
                        var
                            response = {
                                results: {}
                            }
                        ;
                        // translate ATHENA API response to work with search
                        $.each(athenaResponse.content, function (index, item) {
                            var
                                domain = item.domain || 'Unknown',
                                maxResults = 8
                            ;
                            if (index >= maxResults) {
                                return false;
                            }
                            //create new language category
                            if (response.results[domain] === undefined) {
                                response.results[domain] = {
                                    name: domain,
                                    results: []
                                };
                            }
                            // add result to category
                            var flag = true;
                            if (item.standardConcept != "Standard") {
                                var targetConceptName = "", targetConceptId = "";
                                $.ajax({
                                    type: 'GET',
                                    url: 'https://athena.ohdsi.org/api/v1/concepts/' + item.id + '/relationships?std=false',
                                    dataType: 'JSON',
                                    async: false,
                                    success: function (data) {
                                        var itms = data['items'];
                                        for (let i = 0; i < itms.length; i++) {
                                            var itm = itms[i]
                                            if (itm['relationshipName'] === "Non-standard to Standard map (OMOP)") {
                                                targetConceptId = itm['relationships'][0]['targetConceptId'];
                                                targetConceptName = itm['relationships'][0]['targetConceptName'];
                                                break;
                                            }
                                        }
                                    }
                                })
                                if (targetConceptName != "") {
                                    var targetDomain = "";
                                    $.ajax({
                                        type: 'GET',
                                        async: false,
                                        url: 'https://athena.ohdsi.org/api/v1/concepts?pageSize=1&standardConcept=Standard&domain%5B0%5D=Condition&domain%5B1%5D=Device&domain%5B2%5D=Drug&domain%5B3%5D=Measurement&domain%5B4%5D=Observation&domain%5B5%5D=Procedure&page=1&query=' + targetConceptId + '&boosts=',
                                        dataType: 'JSON',
                                        success: function (data) {
                                            for (let i = 0; i < data.content.length; i++) {
                                                if (data.content[i]['id'] == targetConceptId) {
                                                    targetDomain = data.content[i]['domain']
                                                    break
                                                }
                                            }
                                        },
                                        error: function (e) {
                                            console.log(e);
                                        }

                                    })
                                    if (targetDomain != "") {
                                        flag = false;
                                        response.results[domain].results.push({
                                            title: targetConceptName,
                                            description: targetConceptId + " [mapped from non-standard concept: " + item.name + " (" + item.id + ")]",
                                            price: "Standard",
                                            domain: targetDomain,
                                            name: targetConceptName,
                                            id: targetConceptId
                                        });

                                    }
                                }
                            }
                            if (flag) {
                                response.results[domain].results.push({
                                    title: item.name,
                                    description: item.id,
                                    price: item.standardConcept,
                                    domain: item.domain,
                                    name: item.name,
                                    id: item.id
                                });
                            }
                        });

                        return response;
                    }
                },

                onSelect: function (result) {
                    conceptResult = result;
                    var domain = result.domain;
                    $('.ui.dropdown.domain').dropdown('set selected', domain.toLowerCase());
                    $("#searchTip").hide();
                }
            });

        $("#cancel_submit").click(function () {
            $("#mytooltip").remove();

        });
        $(".ui.dropdown.domain").on("change", function () {
            var ent_selected = $("#ent_select").dropdown("get value");
            if (["demographic", "temporal", "value", "negation_cue"].indexOf(ent_selected) > -1) {
                $("#conceptName").css("background-color", "#D7DBDD");
            } else {
                $("#conceptName").css("background-color", "#ffffff");

            }
        });

        $("#tip_submit").click(function () {
            //Update a term with a new domain which has a matched concept.
            var ent_selected = $("#ent_select").dropdown("get value");
            var name_selected = $('.ui.search').search('get value');
            var node = document.getElementById("selected_term");
            if (ent_selected != "" && ["demographic", "temporal", "value", "negation_cue"].indexOf(ent_selected) === -1) {
                //If the user select a term with domain not in the four types above,
                if (!(conceptResult === null)) {
                    //If the user select a concept from the concept list,
                    if (conceptResult.name === name_selected) {
                        //After select a concept, the user doesn't change the concept name,
                        var term = node.firstChild.textContent;
                        var oldDomain = node.getAttribute('data-entity');
                        var oldID = node.getAttribute('concept-id');
                        var oldConcept = "";
                        if (node.getElementsByTagName("i").length > 0) {
                            var oldConcept = node.getElementsByTagName("i")[0].innerText.slice(1, -1); //Remove the brackets
                        }
                        var pa = node.parentNode;
                        var node_i = document.createElement('i');
                        var textnode = document.createTextNode('[' + name_selected + ']');
                        node_i.appendChild(textnode);
                        var node_b = document.createElement('b');
                        node_b.appendChild(node_i);
                        var newNode = document.createElement('mark');
                        newNode.innerText = node.firstChild.textContent;
                        newNode.appendChild(node_b);
                        newNode.setAttribute('data-entity', ent_selected);
                        newNode.setAttribute('concept-id', conceptResult.id);
                        pa.insertBefore(newNode, node);
                        pa.removeChild(node);
                        var index = pa.parentNode.getAttribute('data-index');
                        $(pa.parentNode.parentNode.parentNode).bootstrapTable('updateCell',
                            {index: index, field: 'criterion', value: pa.innerHTML});
                        $("#mytooltip").remove();
                    } else {
                        //After select a concept, the user changes the concept name,
                        $("#searchTip").show();
                    }
                } else {
                    //If the user doesn't select a concept from the concept list,
                    var oldConcept = "";
                    if (node.getElementsByTagName("i").length > 0) {
                        var concept_name = node.getElementsByTagName("i")[0].innerText.slice(1, -1); //Remove the brackets
                        oldConcept = concept_name;
                    }
                    if (!(concept_name === null) && (concept_name === name_selected)) {
                        //If the term was mapped to a concept, and the user now just change the domain of the concept,
                        var term = node.firstChild.textContent;
                        var oldDomain = node.getAttribute('data-entity');
                        var oldID = node.getAttribute('concept-id')
                        var pa = node.parentNode;
                        var node_i = document.createElement('i');
                        var textnode = document.createTextNode('[' + name_selected + ']');
                        node_i.appendChild(textnode);
                        var node_b = document.createElement('b');
                        node_b.appendChild(node_i);
                        var newNode = document.createElement('mark');
                        newNode.innerText = node.firstChild.textContent;
                        newNode.appendChild(node_b);
                        newNode.setAttribute('data-entity', ent_selected);
                        newNode.setAttribute('concept-id', node.getAttribute('concept-id'));
                        pa.insertBefore(newNode, node);
                        pa.removeChild(node);
                        var index = pa.parentNode.getAttribute('data-index');
                        $(pa.parentNode.parentNode.parentNode).bootstrapTable('updateCell',
                            {index: index, field: 'criterion', value: pa.innerHTML});
                        $("#mytooltip").remove();
                    } else {
                        $("#searchTip").show()
                    }
                }
            } else {
                //If the user select a term with domain in the four types above,
                var node = document.getElementById("selected_term");
                var term = node.firstChild.textContent;
                var oldDomain = node.getAttribute('data-entity');
                var oldID = node.getAttribute('concept-id')
                var oldConcept = "";
                if (node.getElementsByTagName("i").length > 0) {
                    var oldConcept = node.getElementsByTagName("i")[0].innerText.slice(1, -1); //Remove the brackets
                }
                var pa = node.parentNode;
                var newNode = document.createElement('mark');
                newNode.innerText = node.firstChild.textContent;
                newNode.setAttribute('data-entity', ent_selected);
                pa.insertBefore(newNode, node);
                pa.removeChild(node);
                var index = pa.parentNode.getAttribute('data-index');
                $(pa.parentNode.parentNode.parentNode).bootstrapTable('updateCell',
                    {index: index, field: 'criterion', value: pa.innerHTML});
                $("#mytooltip").remove();
            }
            deleteAndUpdateTag();
        });
    }

    //Delete and update the category tag for the selected term.
    function deleteAndUpdateTag() {
        var mList = document.getElementsByTagName("mark");

        for (var i = 0; i < mList.length; i++) {

            mList[i].oncontextmenu = function (e) {
                $("#contextMenu").remove();
                selectedNode = this;
                var contextMenu = constructContextMenu();
                $("body").append(contextMenu);
                if (e.pageX + document.getElementById('contextMenu').clientWidth + 10 > document.body.clientWidth) {
                    x = document.body.clientWidth + 10 - document.getElementById('contextMenu').clientWidth;
                } else {
                    x = e.pageX + 10;
                }
                $("#contextMenu").css({
                    "top": e.pageY + "px",
                    "left": x + "px",
                    "position": "absolute"
                }).show("fast");
                $(".deleteButton").click(function (e) {
                    $("#contextMenu").remove();
                    removeTag();
                    deleteAndUpdateTag();
                });
                $(".updateButton").click(function () {
                    $("#contextMenu").remove();
                    updateTag(e);
                    deleteAndUpdateTag();
                })
                $(".cancelButton").click(function () {
                    $("#contextMenu").remove();
                })
                return false;
            }

            mList[i].onclick = function (e) { //Add onclick function to the item with <mark> tag.
                $("#contextMenu").remove();
                selectedNode = this;
                if (e.ctrlKey || e.metaKey) {//delete key
                    removeTag();
                    deleteAndUpdateTag();
                } else if (e.altKey) {//Update a tag.
                    updateTag(e);
                    deleteAndUpdateTag();
                }
            }
        }
    };


    //Continue processing the text with latest terms. If success, the website will turn to a new web page.
    function processData(dataset) {
        $("#mytooltip").remove();
        $("#contextMenu").remove();
        //remove all the tags with name "highlight".
        var hList = document.getElementsByTagName("highlight");
        for (var i = 0; i < hList.length; i++) {
            var node = hList[i];
            var pa = node.parentNode;
            var textnode = document.createTextNode(decodeHtml(node.innerHTML));
            pa.insertBefore(textnode, node);
            pa.removeChild(node);
        }
        if (document.getElementById("selected_term")) {
            document.getElementById("selected_term").removeAttribute("id");
        }

        var exc = formulateJSONArray($("#extable").bootstrapTable("getSelections"));
        var inc = formulateJSONArray($("#intable").bootstrapTable("getSelections"));
        var initialevent = formulateJSONArray($("#initialeventtable").bootstrapTable("getSelections"));
        var abb = $("#abbr").is(':checked');
        var recon = $("#recon").is(':checked');
        var obstart = $("#startdatepicker").val();
        var obend = $("#enddatepicker").val();
        var daysbefore = $("#obstart").val();
        var daysafter = $("#obend").val();
        var limitto = $("#limitto").val();
        $.blockUI({
            message: '<h5><img src="' + basePath + '/img/squares.gif" /> Processing...</h5>',
            css: {
                border: '1px solid khaki'
            }
        });

        $.ajax({
            type: 'POST',
            url: basePath + "main/continueParsing",
            data: {
                'dataset': dataset,
                'nctid': $('#nctid').val(),
                'time': Date.now(),
                'exc': JSON.stringify(exc),
                'inc': JSON.stringify(inc),
                'initialEvent': JSON.stringify(initialevent),
                'abb': abb,
                'recon': recon,
                'obstart': obstart,
                'obend': obend,
                'daysbefore': daysbefore,
                'daysafter': daysafter,
                'limitto': limitto
            },
            timeout: 300000,
            dataType: "json",

            success: function (data) {
                //window.location.href = basePath + "main/gojson";
                $('#result').bootstrapTable('destroy');
                $('#result').bootstrapTable({
                    contentType: "application/x-www-form-urlencoded",
                    //height:800,
                    //buttonsOrder: ['columns', 'toggle'],
                    buttonsToolbar: "#resultTableToolbar",
                    buttonsClass: 'primary',
                    //search: true,
                    iconSize: 'lg',
                    showColumns: true,
                    showPaginationSwitch: true,
                    showPrint: true,
                    pagination: true,
                    pageNumber: 1,
                    queryParamsType: 'limit',
                    queryParams: queryParams,
                    sidePagination: "server",
                    showExport: true,
                    exportDataType: "all",
                    exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'excel'],
                    exportOptions: {
                        fileName: "Criteria2Query_Cohort_Data",
                    },
                    showToggle: true,
                    pageList: [10, 25, 50, 100, 500, 'All'],
                    method: 'post',
                    url: basePath + "main/queryAgencyPage",
                    //data: data['queryResult'],
                    columns: [
                        {
                            sortOrder: 'asc',
                            field: 'person_id',
                            title: 'person_id',
                            width: '20%'
                        },
                        {
                            sortOrder: 'asc',
                            field: 'gender',
                            title: 'gender',
                            width: '20%'
                        },
                        {
                            sortOrder: 'asc',
                            field: 'age',
                            title: 'age',
                            width: '30%'
                        },
                        {
                            sortOrder: 'asc',
                            field: 'race',
                            title: 'race',
                            width: '30%'
                        }
                    ],
                    responseHandler: function (res) {
                        showTips(res.total);
                        $('#numPatients').text(res.total);
                        if (res.total===1){
                            if (res.rows[0]["person_id"]===("Please connect to a database. For now, you can only download the JSON file and SQL script.")){
                                $('#numPatients').text("Please connect to a database.");
                            }
                        }
                        return {
                            rows: res.rows,
                            total: res.total
                        }
                    }
                });
                $('#modalNumberPatients').modal('show');

            },
            error: function (e) {
                alert("Oppps....");
            }
        })

    };


    function queryParams(params) {
        if (!params.hasOwnProperty("offset")) {
            params.offset = 0;
            params.limit = -1;
        }
        return {
            offset: params.offset,
            limit: params.limit
        }
    }

    //Get text in a table in HTML format.
    function getText(tab) {
        var rows = tab.getElementsByClassName("criterion");
        var text = "";
        for (var i = 1; i < rows.length; i++) {
            text = text + "\n" + rows[i].innerHTML;
        }
        return text;
    };

    function formulateJSONArray(selectedRows) {
        var jsonArray = [];
        for (row of selectedRows) {
            var data = {};
            data['id'] = row['id'];
            var paragraph = row['criterion'];
            var regex = new RegExp("(<mark.*?>.*?)(\\.)(<b><i>.*?</mark>)", "g");
            paragraph = paragraph.replaceAll(regex, "$1$3$2");
            regex = new RegExp("(<mark.*?<b><i>)(.*?)(</i></b></mark>)", "g");
            paragraph = paragraph.replaceAll(regex, replacer);
            data['criterion'] = paragraph;
            jsonArray.push(data);
        }
        return jsonArray;
    };

    function replacer(match, p1, p2, p3, offset, string) {
        p2 = p2.replaceAll(".", "");
        return [p1, p2, p3].join("");
    }

    //Prevent whole text selection triggered by triple click.
    function clearSelection() {
        if (document.selection && document.selection.empty) {
            document.selection.empty();
        } else if (window.getSelection) {
            var sel = window.getSelection();
            sel.removeAllRanges();
        }
    }

    function showTips(total) {
        if (total === 0) {
            $("#resultTips").show();
        } else {
            $("#resultTips").hide();
        }
    }

    function downloadJSONfile() {
        openNewWin(basePath + "main/downloadJSON", "download");
    }

    function downloadSQLfile(dialect) {
        openNewWin(basePath + "main/downloadSQL?sqlDialect=" + dialect, "download");
    }

    function decodeHtml(str) {
        var map =
            {
                '&amp;': '&',
                '&lt;': '<',
                '&gt;': '>',
                '&quot;': '"',
                '&#039;': "'"
            };
        return str.replace(/&amp;|&lt;|&gt;|&quot;|&#039;/g, function (m) {
            return map[m];
        });
    }


    function changeDataset(dataset) {
        $.blockUI({
            message: '<h3><img src="' + basePath + '/img/squares.gif" /> Processing...</h3>',
            css: {
                border: '1px solid khaki'
            }
        });
        $.ajax({
            type: 'POST',
            url: basePath + "main/changeDataset",
            data: {
                'dataset': dataset
            },

            success: function () {
                $('#result').bootstrapTable('destroy');
                $('#result').bootstrapTable({
                    contentType: "application/x-www-form-urlencoded",
                    //height:800,
                    //buttonsOrder: ['columns', 'toggle'],
                    buttonsToolbar: "#resultTableToolbar",
                    buttonsClass: 'primary',
                    iconSize: 'lg',
                    //search: true,
                    showColumns: true,
                    showPaginationSwitch: true,
                    showPrint: true,
                    pagination: true,
                    pageNumber: 1,
                    queryParamsType: 'limit',
                    queryParams: queryParams,
                    sidePagination: "server",
                    showExport: true,
                    exportDataType: "all",
                    exportTypes: ['json', 'xml', 'csv', 'txt', 'sql', 'excel'],
                    exportOptions: {
                        fileName: "Criteria2Query_Cohort_Data",
                    },
                    showToggle: true,
                    pageList: [10, 25, 50, 100, 500, 'All'],
                    method: 'post',
                    url: basePath + "main/queryAgencyPage",
                    //data: data['queryResult'],
                    columns: [
                        {
                            sortOrder: 'asc',
                            field: 'person_id',
                            title: 'person_id',
                            width: '20%'
                        },
                        {
                            sortOrder: 'asc',
                            field: 'gender',
                            title: 'gender',
                            width: '20%'
                        },
                        {
                            sortOrder: 'asc',
                            field: 'age',
                            title: 'age',
                            width: '30%'
                        },
                        {
                            sortOrder: 'asc',
                            field: 'race',
                            title: 'race',
                            width: '30%'
                        }
                    ],
                    responseHandler: function (res) {
                        showTips(res.total);
                        $('#numPatients').text(res.total);
                        if (res.total===1){
                            if (res.rows[0]["person_id"]===("Please connect to a database. For now, you can only download the JSON file and SQL script.")){
                                $('#numPatients').text("Please connect to a database.");
                            }
                        }
                        return {
                            rows: res.rows,
                            total: res.total
                        }
                    }
                });
                $('#modalNumberPatients').modal('show');

            },
            error: function (xhr, textStatus, errorThrown) {
                console.log('error:' + textStatus);
                console.log(xhr.responseText);
                console.log(errorThrown);
            }
        });

    }


</script>

</body>

</html>
