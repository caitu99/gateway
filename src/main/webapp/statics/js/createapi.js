$(document).ready(function(){


   // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      //console.log(theme);
      var env = getEnv();
      if("Monitor" == theme){
         window.location.href = "monitor?env=" + env;
      } else if("Release" == theme) {
        window.location.href = "release?env=" + env;
      } else if("Home" == theme) {
        window.location.href = "apilist?env=" + env;
      } else if("LogView" == theme) {
        window.location.href = "pipelog?env=" + env;
      } else if("Users" == theme) {
        window.location.href = "user?env=" + env;
      } else if("Manual" == theme) {
        window.location.href = "manual?env=" + env;
      }else if("Cache" == theme) {
        window.location.href = "cachemanage?env=" + env;
      } else if("Instance" == theme) {
        window.location.href = "instancedetail?env=" + env;
      } else if("RecoverApi" == theme) {
        window.location.href = "recoverapi?env=" + env;
      }

    });

    // 之前激活按钮的绑定事件
    $('a[data-toggle="tab"]').on('hide.bs.tab', function (e) {
      var theme = $(e.target).html(); // 之前激活按钮显示的关键字
      console.log(theme);
    });

    // 跳转到帮助页面
    $(".help").on("click", function(e){
        e.preventDefault();
        var env = getEnv();
        location.href = "manual?env=" + env;
    });

    $(".console").on("click", function(e) {
        e.preventDefault();
        var content = $(this).html();
        $("#consoleText").html(content);
    });

    // DataTable关闭一些默认配置
    $.extend($.fn.dataTable.defaults, {
        searching: false,
        ordering: false
    });

    //  给API配置DIV绑定dataTable插件
    var carmenApiTable = $("#carmenApi").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
        //info: false
        //info: false
    });

    $("#apiInterfaceConfig").validate();

    $("#methodconfigform").validate();

    $(".apiTypeLi").on("click", function () {
        var apiType = $(this).attr("flag");
        if("JAVA" == apiType) {
            $("#lastLi").css("display", "block");
            $("ul.nav-wizard li").css("padding", "0 30px 0 40px");
        } else {
            $("#lastLi").css("display", "none");
            $("ul.nav-wizard li").css("padding", "0 40px 0 50px");
        }
    });

    initApiTypeTab();
    function initApiTypeTab() {
        var apiType = $("#initApiType").val();
        if(2 == apiType) { //PHP
            $("#lastLi").css("display", "none");
            $("ul.nav-wizard li").css("padding", "0 40px 0 50px");
        } else if(1 == apiType) {
            $("#lastLi").css("display", "block");
            $("ul.nav-wizard li").css("padding", "0 30px 0 40px");
        }
    }
    $(".hasMapping").on("click", function () {
        //  获取前面填写的namespace等
        var apiNamespace = $("#namespace").val();
        var apiName = $("#name").val();
        var apiVersion = $("#version").val();
        var methodNamespace = $("#namespaceMethodParam").val();
        var methodName = $("#nameMethodParam").val();
        var methodVersion = $("#versionMethodParam").val();
        $.post("checkapimethodmapping", {"apiNamespace":apiNamespace, "apiName":apiName, "apiVersion":apiVersion, "namespace":methodNamespace, "name":methodName, "version":methodVersion, "env":getEnv()}, function (d) {
            if("success" == d) {
                $("#apiMethodMappingInfo").html("<p>已存在映射关系，请修改API继续配置</p>");
                $("#apiMethodMappingInfo").css("display","block");
                $("#apiMethodMappingInfo").removeClass("alert-success");
                $("#apiMethodMappingInfo").addClass("alert-danger");
            } else {
                // 参数映射检测成功之后才能继续
                $(".nextStep").css("display", "block");
                $(".hasMapping").css("display", "none");

                $("#apiMethodMappingInfo").html("<p>当前不存在映射关系，请继续配置</p>");
                $("#apiMethodMappingInfo").css("display","block");

                $("#apiMethodMappingInfo").removeClass("alert-danger");
                $("#apiMethodMappingInfo").addClass("alert-success");
            }
        },"json");
    });

    // 给检测API是否存在按钮绑定事件
    $(".hasApi").on("click", function () {
        var apiNamespace = $("#namespace").val();
        var apiName = $("#name").val();
        var apiVersion = $("#version").val();
        if("" == apiNamespace || "" == apiName || "" == apiVersion) {
            $("#apiInfo").html("<p>请把参数填写完整</p>");
            $("#apiInfo").css("display","block");

            $("#apiInfo").removeClass("alert-success");
            $("#apiInfo").addClass("alert-danger");
            return;
        }
        $.post("getapi", {"namespace":apiNamespace, "name":apiName, "version":apiVersion, "env": getEnv()}, function (d) {
            if("fail" == d) {
                $("#apiInfo").html("<p>当前不存在此API，请继续配置</p>");
                $("#apiInfo").css("display","block");

                $("#apiInfo").removeClass("alert-danger");
                $("#apiInfo").addClass("alert-success");
                $(".hasApi").css("display", "none"); // 打开API检测按钮&关闭下一步按钮
                $(".nextStep").css("display", "block");
            } else {
                $("#apiInfo").html("<p>此API已存在，请修改后继续配置</p>");
                $("#apiInfo").css("display","block");

                $("#apiInfo").removeClass("alert-success");
                $("#apiInfo").addClass("alert-danger");
            }
        }, "json");
    });

    // 给检测方法是否存在按钮绑定事件
    $(".hasMethod").on("click", function () {
        var methodNamespace = $("#namespaceMethodParam").val();
        var methodName = $("#nameMethodParam").val();
        var methodVersion = $("#versionMethodParam").val();
        if("" == methodNamespace || "" == methodName || "" == methodVersion) {
            $("#apiMethodInfo").html("<p>请把参数填写完整</p>");
            $("#apiMethodInfo").css("display","block");

            $("#apiMethodInfo").removeClass("alert-success");
            $("#apiMethodInfo").addClass("alert-danger");
            return;
        }

        $.post("getapimethod", {"namespace":methodNamespace, "name":methodName, "version":methodVersion, "env":getEnv()}, function (d) {
            if("fail" == d) {
                $("#apiMethodInfo").html("<p>当前不存在此API，请继续配置</p>");
                $("#apiMethodInfo").css("display","block");

                $("#apiMethodInfo").removeClass("alert-danger");
                $("#apiMethodInfo").addClass("alert-success");
                $(".hasMethod").css("display", "none"); // 打开API检测按钮&关闭下一步按钮
                $(".nextStep").css("display", "block");
            } else {
                $("#apiMethodInfo").html("<p>此API已存在，请修改后继续配置</p>");
                $("#apiMethodInfo").css("display","block");

                $("#apiMethodInfo").removeClass("alert-success");
                $("#apiMethodInfo").addClass("alert-danger");
            }
        }, "json");

    });

    // 给上一步按钮绑定事件
    $(".preStep").on("click", function () {
        var step = $("#configStep").val();
        if( step > 0 ) { // 步骤从0开始
            step = parseInt(step) - 1;
            var apiTypeValue = getAPIType();
            if(4 == step && 2 == apiTypeValue) { //如果是配置PHP接口，那么跳过结构配置
                step = parseInt(step) - 1;
            }
            $("#configStep").val(step);
        }
        if(0 == step) { // 第0步，隐藏“上一步”按钮
            $("li[step=2]").removeClass("active");
            $("li[step=1]").addClass("active");
            $("#apiInfo").css("display","none");
            $(".hasApi").css("display", "none"); // 打开API检测按钮&关闭下一步按钮
            $(".nextStep").css("display", "block");
            $(".apiconfig").css("margin-left","350px");
            $(".apiconfig").html("选择API类型");
            $(".preStep").css("display", "none");
            $(".apiType").css("display", "block"); // 显示API类型选择界面
            $(".apiconfig1").css("display", "none"); //  隐藏API配置界面
        } else if(1 == step) {
            $("li[step=3]").removeClass("active");
            $("li[step=2]").addClass("active");
            $("#apiInfo").html("");
            $("#apiInfo").css("display","none");
            var edit = $("#edit").val();
            if(1 != edit) { // 新建任务场景
                $(".hasApi").css("display", "block"); // 打开API检测按钮&关闭下一步按钮
                $(".nextStep").css("display", "none");
            } else { //编辑任务场景
                $(".hasApi").css("display", "none"); // 打开API检测按钮&关闭下一步按钮
                $(".nextStep").css("display", "block");
            }


            $(".apiconfig").html("API接口信息配置");
            $(".apiconfig1").css("display", "block"); //  显示API配置界面
            $(".apiconfig2").css("display", "none"); // 隐藏API参数配置界面
        } else if(2 == step) {
            $("li[step=4]").removeClass("active");
            $("li[step=3]").addClass("active");
            $("#apiMethodInfo").css("display","none");
            $(".apiconfig").html("API参数配置");
            $(".apiconfig2").css("display", "block"); // 显示API参数配置界面
            $(".methodconfig").css("display", "none"); // 隐藏方法&方法参数配置界面
            $(".nextStep").css("display", "block");
            $(".hasMethod").css("display", "none");
        } else if(3 == step) {
            $("li[step=5]").removeClass("active");
            $("li[step=4]").addClass("active");
            $(".apiconfig").html("内部方法&方法参数配置");
            $(".methodconfig").css("display", "block"); // 隐藏方法&方法参数配置界面
            $(".structureconfig").css("display", "none"); // 显示结构配置界面
            $(".methodmappingconfig").css("display", "none"); // 显示方法映射的配置界面
            var edit = $("#edit").val();
            if(1 == edit) { // 编辑场景
                $(".nextStep").css("display", "block");
                $(".hasMethod").css("display", "none");
            } else { // 新增场景
                $(".nextStep").css("display", "none");
                $(".hasMethod").css("display", "block");
            }

            $(".hasMapping").css("display", "none");
            $("#apiMethodInfo").css("display","none");
        } else if(4 == step) {
            $("li[step=6]").removeClass("active");
            $("li[step=5]").addClass("active");
            $("#apiMethodMappingInfo").css("display","none"); // 关闭上一步的提示
            // 关闭检测按钮，打开下一步按钮
             $(".hasMapping").css("display", "none");
            $(".nextStep").css("display", "block");


            $(".apiconfig").html("方法参数中的结构配置");
            $(".structureconfig").css("display", "block"); // 隐藏结构配置界面
            $(".methodmappingconfig").css("display", "none"); // 显示方法映射的配置界面
        } else if(5 == step) { // 第5步，显示被隐藏的“下一步”按钮
            if("2" == getAPIType()) {
                $("li[step=6]").removeClass("active");
                $("li[step=5]").addClass("active");
            } else {
                $("li[step=7]").removeClass("active");
                $("li[step=6]").addClass("active");
            }

            $(".finalSave").css("display", "none");
            var edit = $("#edit").val();
            if(1 == edit) { // 编辑场景
                $(".nextStep").css("display", "block");
                $(".hasMapping").css("display", "none");
            } else { // 新增场景
                $(".nextStep").css("display", "none");
                $(".hasMapping").css("display", "block");
            }
            $(".apiconfig").html("方法映射配置");
            $(".methodmappingconfig").css("display", "block"); // 隐藏方法映射的配置界面
            $(".parammappingconfig").css("display", "none"); //  显示参数映射配置界面

            $("#apiParamMappingInfo").css("display", 'none');
        }
    });

    function getAPIType() {
        // 获取环境变量的值
        var apiType = $('[name="apiType"]:checked').val();
        return apiType;
    }

    // 给下一步按钮绑定事件
    $(".nextStep").on("click", function () {
        var step = $("#configStep").val();
        if(1 == step) {
            var flag = $("#apiInterfaceConfig").valid(); // 检测API的配置是否填写完整
            if(1 == flag) {
                //console.log("yes");
                $("#apiInfo").css("display","none");
            } else {
                $("label.error").css("color", "#FF0000");
                $("#apiInfo").html("<p>参数配置不完整，请检查~~</p>");
                $("#apiInfo").removeClass("alert-success");
                $("#apiInfo").addClass("alert-danger");
                $("#apiInfo").css("display","block");
                return;
            }
        }

        if(3 == step) {
            // 检测方法名是否填写
            var methodFlag = $("#methodconfigform").valid();
            if(1== methodFlag) {
                //console.log("yes");
            } else {
                //console.log("no");
                return;
            }
            // 检测方法参数是否填写
            //var methodParamContent = carmenMethodParamTable.$('input, select').serialize();
        }

        if( step < 6 ) { // 步骤上限为6
            step = parseInt(step) + 1;
            var apiTypeValue = getAPIType();
            if(4 == step && 2 == apiTypeValue) { //如果是配置PHP接口，那么跳过结构配置
                step = parseInt(step) + 1;
            }
            $("#configStep").val(step);
        }



        if(1 == step) { // 第一步，显示被隐藏的“上一步“按钮

            var serviceType = getAPIType();
            if(1 == serviceType) {
                $("#apiUrl").html("注册中心");
                $("#appNameContent").css("display", "block");
            } else {
                $("#apiUrl").html("url");
                $("#appNameContent").css("display", "none");
            }
            $("li[step=1]").removeClass("active");
            $("li[step=2]").addClass("active");

            $(".apiconfig").css("margin-left","260px");
            $(".apiconfig").html("API接口信息配置");
            var edit = $("#edit").val();
            if(1 != edit) { // 新建任务场景
                $(".hasApi").css("display", "block"); // 打开API检测按钮&关闭下一步按钮
                $(".nextStep").css("display", "none");
            } else { // 编辑任务场景
                $(".hasApi").css("display", "none"); // 打开API检测按钮&关闭下一步按钮
                $(".nextStep").css("display", "block");
            }


            $(".preStep").css("display", "block");
            $(".apiType").css("display", "none"); // 隐藏API类型选择界面
            $(".apiconfig1").css("display", "block"); //  显示API配置界面
        } else if (2 == step) {
            $("li[step=2]").removeClass("active");
            $("li[step=3]").addClass("active");
            $(".apiconfig").html("API参数配置");
            $(".apiconfig1").css("display", "none");  //  隐藏API配置界面
            $(".apiconfig2").css("display", "block"); // 显示API参数配置界面
            var apiType = getAPIType();
            if(2 == apiType) {
                $("#batchAdd").css("display", "block");
            } else {
                $("#batchAdd").css("display", "none");
            }
        } else if (3 == step) {
            $("li[step=3]").removeClass("active");
            $("li[step=4]").addClass("active");
            $(".apiconfig").html("内部方法&方法参数配置");
            var apiType = getAPIType();
            if(2 == apiType) {
                $("#batchAddMethod").css("display", "block");
            } else {
                $("#batchAddMethod").css("display", "none");
            }
            $(".apiconfig2").css("display", "none"); // 隐藏API参数配置界面
            $(".methodconfig").css("display", "block"); // 显示方法&方法参数配置界面
            var edit = $("#edit").val();
            if(1 == edit) { // 编辑场景
                $(".nextStep").css("display", "block");
                $(".hasMethod").css("display", "none");
            } else { // 新增场景
                $(".nextStep").css("display", "none");
                $(".hasMethod").css("display", "block");
            }
        } else if (4 == step) {
            $("li[step=4]").removeClass("active");
            $("li[step=5]").addClass("active");
            $(".apiconfig").html("方法参数中的结构配置");

            $(".methodconfig").css("display", "none"); // 隐藏方法&方法参数配置界面
            $(".structureconfig").css("display", "block"); // 显示结构配置界面

        } else if(5 == step) {
            if("2" == getAPIType()) { //如果是PHP配置
                $("li[step=4]").removeClass("active");
                $("li[step=5]").addClass("active");
            } else {
                $("li[step=5]").removeClass("active");
                $("li[step=6]").addClass("active");
            }
            $(".apiconfig").html("方法映射配置");
            // 参数映射检测成功之后才能继续
            var edit = $("#edit").val();
            if(1 == edit) { // 编辑场景
                $(".nextStep").css("display", "block");
                $(".hasMapping").css("display", "none");
            } else { // 新增场景
                $(".nextStep").css("display", "none");
                $(".hasMapping").css("display", "block");
            }


            $(".methodconfig").css("display", "none"); // 隐藏方法&方法参数配置界面
            $(".structureconfig").css("display", "none"); // 隐藏结构配置界面

            //  获取前面填写的namespace等
            var apiNamespace = $("#namespace").val();
            var apiName = $("#name").val();
            var apiVersion = $("#version").val();
            var methodNamespace = $("#namespaceMethodParam").val();
            var methodName = $("#nameMethodParam").val();
            var methodVersion = $("#versionMethodParam").val();
            var apiService = apiNamespace + "/" + apiVersion + "/" + apiName;
            var innerService = methodNamespace + "/" + methodVersion + "/" + methodName;
            $("#namespaceMethodMappingApi").html(apiService);
            //$("#nameMethodMappingApi").html(apiName);
            //$("#versionMethodMappingApi").html(apiVersion);
            $("#namespaceMethodMapping").html(innerService);
            //$("#nameMethodMapping").html(methodName);
            //$("#versionMethodMapping").html(methodVersion);

            $(".methodmappingconfig").css("display", "block"); // 显示方法映射的配置界面
        } else if(6 == step) { // 第6步，隐藏“下一步”按钮
            if("2" == getAPIType()) { //如果是PHP配置
                $("li[step=5]").removeClass("active");
                $("li[step=6]").addClass("active");
                $("#fieldTitle").html("参数名");
                $("#fieldType").html("参数类型");
            } else {
                $("li[step=6]").removeClass("active");
                $("li[step=7]").addClass("active");
                $("#fieldTitle").html("属性名");
                $("#fieldType").html("属性类型");
            }



            $("#apiMethodMappingInfo").css("display","none"); // 关闭上一步的提示
            $(".nextStep").css("display", "none");
            $(".finalSave").css("display", "block");
            $(".apiconfig").html("API与内部方法的参数映射配置");
            $(".methodmappingconfig").css("display", "none"); // 隐藏方法映射的配置界面

            //显示映射配置界面之前，先获取之前填写的API参数和内部方法参数
            var apiParamNames = getEditedApiParams();
            var methodParamNames = getEditedMethodParams();
            $("#apiParamNames").val(apiParamNames);
            $("#methodParamNames").val(methodParamNames);
            appendSelect(apiParamNames);
            appendMethodSelect(methodParamNames);

            $(".parammappingconfig").css("display", "block"); //  显示参数映射配置界面
        }
    });

    // 为已有的select补充完整
    function appendSelect(apiParamNames) {
        if("" == apiParamNames || "undefined" == typeof(apiParamNames) ) {
            return;
        }
        var apiParams = apiParamNames.split(",");
        $(".apiParamsSelect").each(function(index){
             var currentParam = $(this).val();
             var existElements = new Array();
             $(this).children().each(function(index){
                var value = $(this).val();
                existElements.push(value);
             });
             for(var i=0; i<apiParams.length; i++) {
                var tmpParam = decodeURI(apiParams[i]);
                if(-1 != $.inArray(tmpParam, existElements) || "" == tmpParam){ // 过滤掉已有的值
                    continue;
                }
                $(this).append("<option value='" + tmpParam + "'>" + tmpParam + "</option>");
             }
        });
    }

    function appendMethodSelect(methodParamNames) {
        if("" == methodParamNames || "undefined" == typeof(methodParamNames)) {
            return;
        }
        var methodParams =methodParamNames.split(",");
        $(".methodParamSelect").each(function(index){
            var currentParam = $(this).val();
            var existElements = new Array();
            $(this).children().each(function(index){
               var value = $(this).val();
               existElements.push(value);
            });
            for(var i=0; i<methodParams.length; i++) {
                var tmpParam = decodeURI(methodParams[i]);
                if(-1 != $.inArray(tmpParam, existElements)  || "" == tmpParam){ // 过滤掉已有的值
                    continue;
                }
                $(this).append("<option value='" + tmpParam + "'>" + tmpParam + "</option>");
            }
        });
    }

    // 获取已经填写的API参数
    function getEditedApiParams() {
        // 2. 解析API参数，结果转为json数组字符串
        var apiParamContent = carmenParamDivTable.$('input, select').serialize();
        var totalResult = "";
        if("" != apiParamContent) {
            var updateArray = new Array();
            var elements = apiParamContent.split("&");
            for (var i=0; i<elements.length/8; i++ ){
                var column = elements[i*8].split("=")[1];
                updateArray.push(column);
            }

            var parseUpdateResult = updateArray.join(",");
            return parseUpdateResult;
        }
    }

    // 获取已经填写的内部方法参数
    function getEditedMethodParams() {
        // 4. 解析方法的配置
        var methodParamContent = carmenMethodParamTable.$('input, select').serialize();
        var methodTotalResult = ""; // 解析方法参数
        if("" != methodParamContent) {
            var updateArray = new Array();
            var elements = methodParamContent.split("&");
            for (var i=0; i<elements.length/5; i++ ){
                var column = elements[i*5].split("=")[1];
                updateArray.push(column);
            }
            var parseUpdateResult = updateArray.join(",");
            return parseUpdateResult;
        }
    }


    //********************************************** API参数配置 ******************************************
    var carmenParamDivTable = $("#carmenParam").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    // API参数配置，绑定新增行事件
    $("#addRow_carmenParam").on("click", function () {
        $("#apiParamInfo").html("");
        $("#apiParamInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';

        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" >';
        //var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" >';
        var paramTypeElement = '<select style="max-width:150px;max-height: 25px;"  name="apiParamType" class="form-control apiParamType"><option value="Number" >Number</option><option value="String" >String</option><option value="Price" >Price</option><option value="Boolean" >Boolean</option><option value="Date" >Date</option><option value="byte[]" >byte[]</option></select>';
        var describleElement = '<input style="max-width:100px;" type="text"  name="describle" >';
        var isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
        var ruleElement = '<input style="max-width:140px;" type="text"  name="rule" >';
        var timestamp = Math.round(new Date().getTime()/1000);
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="0" selected>是</option><option value="1" >否</option></select><input type="hidden" name="currentId" value="0"><input type="hidden" name="currentSequence" value="' + timestamp + '">';
        carmenParamDivTable.row.add( [
           paramNameElement,paramTypeElement,describleElement,isRequiredElement,ruleElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });

    $('[data-toggle="tooltip"]').tooltip();
    //********************************************** 方法&方法参数配置 ******************************************
    var carmenMethodParamTable = $("#carmenMethodParam").DataTable({
        paging: false,
        //ordering: true,
        language: {"infoEmpty": "","info": ""}
    });

    $("#addRow_carmenMethodParam").on("click", function () {
        $("#apiMethodParamsInfo").html("");
        $("#apiMethodParamsInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step4" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" >';
        var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" >';
        //var paramTypeElement = '<select style="max-width:150px;max-height: 25px;"  name="apiParamType" class="form-control apiParamType"><option value="Number" >Number</option><option value="String" >String</option><option value="Price" >Price</option><option value="Boolean" >Boolean</option><option value="Date" >Date</option><option value="byte[]" >byte[]</option></select>';
        var sequenceElement = '<input style="max-width:160px;" type="text"  name="sequence" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="0" selected>是</option><option value="1" >否</option></select><input type="hidden" name="currentId" value="0">';

        carmenMethodParamTable.row.add( [
           paramNameElement,paramTypeElement,sequenceElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });

    //********************************************** 结构配置 ******************************************
    var carmenStructureTable = $("#carmenStructure").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    $("#addRow_carmenStructure").on("click", function () {
        $("#apiStructureInfo").html("");
        $("#apiStructureInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step5" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var classNameElement = '<input style="max-width:100px;" type="text"  name="apiParamName" >';
        var fieldNameElement = '<input style="max-width:100px;" type="text"  name="apiParamType" >';
        var fieldTypeElement = '<input style="max-width:100px;" type="text"  name="sequence" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select><input type="hidden" name="currentId" value="0">';

        carmenStructureTable.row.add( [
           classNameElement,fieldNameElement,fieldTypeElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });

    //********************************************* 7. 参数映射 *********************************************
    var carmenParamMappingTable = $("#carmenParamMapping").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    $("#addRow_carmenParamMapping").on("click", function () {

        $("#apiParamMappingInfo").html("");
        $("#apiParamMappingInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step7" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var fieldNameElement = '<select style="max-width:150px;max-height: 25px;display:none;" id="innerParam" name="innerParam" class="form-control" ><option value="user_id">user_id</option><option value="client_id" >client_id</option><option value="client_name" >client_name</option><option value="client_secret" >client_secret</option><option value="client_source" >client_source</option><option value="client_num" >client_num</option><option value="client_type" >client_type</option><option value="access_token" >access_token</option><option value="request_ip" >request_ip</option></select><input style="max-width:160px;" type="text"  name="fieldName" >';
        var fieldTypeElement = '<input style="max-width:160px;" type="text"  name="fieldType" >';
        //var apiParamNameElement = '<input style="max-width:100px;" type="text"  name="apiParamName" >';
        var apiParamNameElement = '<select style="max-width:150px;max-height: 25px;" name="innerParam" class="form-control" ><option value="default">default</option></select>';
        var apiParams = $("#apiParamNames").val();
        if("" != apiParams) {
            apiParamNameElement = '<select style="max-width:150px;max-height: 25px;" name="innerParam" class="form-control" >';
            var columns = apiParams.split(",");
            for(var i=0; i<columns.length; i++) {
                if("" == columns[i]) {
                    continue;
                }
                apiParamNameElement += "<option>" + decodeURI(columns[i]) + "</option>";
            }
            apiParamNameElement += "</select>";
        }
        apiParamNameElement += '<input style="max-width:160px;display:none;" type="text"  name="apiParamName" >';


        var timestamp = Math.round(new Date().getTime()/1000);
        //var methodParamRefElement = '<input style="max-width:100px;" type="text"  name="methodParamRef" ><input type="hidden" name="currentId" value="0"><input type="hidden" name="currentSequence" value="' + timestamp + '" >';
        var methodParamRefElement = '<select style="max-width:150px;max-height: 25px;" name="methodParamRef" class="form-control" ><option value="default">default</option></select>';
        var methodParams = $("#methodParamNames").val();
        if("" != methodParams) {
            methodParamRefElement = '<select style="max-width:150px;max-height: 25px;" name="methodParamRef" class="form-control" >';
            var columns = methodParams.split(",");
            for(var i=0; i<columns.length; i++) {
                if("" == columns[i]) {
                    continue;
                }
                methodParamRefElement += "<option>" + decodeURI(columns[i]) + "</option>";
            }
            methodParamRefElement += '</select>';
        }
        methodParamRefElement += '<input style="max-width:160px;display:none;" type="text"  name="methodParamRef" >';
        methodParamRefElement += '<input type="hidden" name="currentId" value="0"><input type="hidden" name="currentSequence" value="' + timestamp + '" >';
        var dateFromElement = '<select style="min-width:50px;max-height: 25px;" id="dataFrom"  name="dateFrom" class="form-control" ><option value="1" selected>API参数</option><option value="2" >内部参数</option><option value="3" >自定义参数</option></select>'
        carmenParamMappingTable.row.add( [
           dateFromElement, fieldNameElement, fieldTypeElement, apiParamNameElement, methodParamRefElement, operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });


    // 保存所有的配置信息
    $(".save").on("click", function () {
        // 0. 检测是否填写
        var paramMappingContent = carmenParamMappingTable.$('input, select').serialize();
        $("#apiParamMappingInfo").css("display", 'none');
        if("" == paramMappingContent) {
            $("#apiParamMappingInfo").html('<p>请先配置参数映射</p>');
            $("#apiParamMappingInfo").css("display", 'block');
            return;
        }


        // 1. 解析API配置的结果，转为json字符串
        var apiContent = $("#apiInterfaceConfig").serialize();
        //console.log("apiContent: " + apiContent);
        var parseApiResult = parseApi(apiContent);

        // 2. 解析API参数，结果转为json数组字符串
        var apiParamContent = carmenParamDivTable.$('input, select').serialize();
        //console.log("apiParamContent: " + apiParamContent);
        var totalResult = "";
        var apiParamUpdate = "";
        var apiParamAdd = "";
        if("" != apiParamContent) {
            totalResult = parseApiParam(apiParamContent); // 解析API参数，结果转为json数组字符串
            apiParamUpdate = totalResult.split(";")[0];
            apiParamAdd = totalResult.split(";")[1];
        }

        // 3. 解析方法的配置
        var methodContent = $("#methodconfigform").serialize();
        //console.log("methodContent: " + methodContent);
        var methodResult = parseMethod(methodContent); // 解析方法配置

        // 4. 解析方法的配置
        var methodParamContent = carmenMethodParamTable.$('input, select').serialize();
        //console.log("methodParamContent: " + methodParamContent);
        var methodTotalResult = ""; // 解析方法参数
        var methodParamUpdate = "";
        var methodParamAdd = "";
        if("" != methodParamContent) {
            methodTotalResult = parseMethodParam(methodParamContent); // 解析方法参数
            methodParamUpdate = methodTotalResult.split(";")[0];
            methodParamAdd = methodTotalResult.split(";")[1];
        }

        // 5. 解析结构
        var structureContent = carmenStructureTable.$('input, select').serialize();
        //console.log("structureContent: " + structureContent);
        var structureResult = ""; // 解析结构参数
        var structureUpdate = "";
        var structureAdd = "";
        if("" != structureContent) {
            structureResult = parseStructure(structureContent); // 解析结构参数
            structureUpdate = structureResult.split(";")[0];
            structureAdd = structureResult.split(";")[1];
        }

        // 6. 解析参数映射
        var paramMappingContent = carmenParamMappingTable.$('input, select').serialize();
        //console.log("paramMappingContent: " + paramMappingContent);
        //var paramMappingContent = ""; // 解析参数映射
        var paramMappingUpdate = "";
        var paramMappingAdd = "";
        if("" != paramMappingContent) {
            paramMappingContent = parseParamMapping(paramMappingContent); // 解析参数映射
            paramMappingUpdate = paramMappingContent.split(";")[0];
            paramMappingAdd = paramMappingContent.split(";")[1];
        }

        // 7. 解析方法映射
        var methodMappingId = $(".methodmappingconfig").attr("methodMapping");
        if("" == methodMappingId) {
            methodMappingId = 0;
        }


        $("#updateStatus").html("正在保存...");
        $("#myHideModal").modal("show");
        var flag = $(this).attr("flag"); // 1代表保存， 2代表保存并继续

        $.post("saveResult", {   "parseApiResult":parseApiResult,
                                 "apiParamUpdate":apiParamUpdate,
                                 "apiParamAdd":apiParamAdd,
                                 "methodResult":methodResult,
                                 "methodParamUpdate":methodParamUpdate,
                                 "methodParamAdd":methodParamAdd,
                                 "structureUpdate":structureUpdate,
                                 "structureAdd":structureAdd,
                                 "paramMappingUpdate":paramMappingUpdate,
                                 "paramMappingAdd":paramMappingAdd,
                                 "methodMappingId":methodMappingId,
                                 "env":getEnv()}, function (d) {
                                    if("success" == d) {
                                        var env = getEnv();
                                        if(1 == flag) { // 保存按钮跳转到首页
                                            window.location.href = "apilist?env=" + env;
                                        } else if(2 == flag) { // 保存并继续跳转到新建api页面
                                            window.location.href = "createapi?env=" + env;
                                        }

                                        $("#updateStatus").html("保存成功");
                                        $("#myHideModal").delay(6000);
                                        $("#myHideModal").modal("hide");
                                    }else {
                                        $("#updateStatus").html("保存失败!");
                                        $("#myHideModal").delay(6000);
                                        $("#myHideModal").modal("hide");
                                        $("#apiParamMappingInfo").html('<p>更新失败<br/>' + d + '</p>');
                                        $("#apiParamMappingInfo").css("display", 'block');
                                    }

                                 });
    });


    //  解析API接口配置的内容
    function parseApi(content) {
        var cells = content.split("&");

        var updateObject = new Object();
        updateObject.namespace = cells[0].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.name = cells[1].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.version = cells[2].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.appName = cells[3].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.addressUrl = cells[4].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.enableLog = cells[5].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.enableFreq = cells[6].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.enableInnerOuter = cells[7].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.apiType = getAPIType()
        updateObject.requestType = cells[8].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.id = cells[9].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.apiGroup = cells[10].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.sessionFlag = cells[11].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.apiDesc = cells[12].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.apiScenarios = cells[13].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.env = getEnv();
        updateObject.creator = $("#userName").val();

        var parseResult = JSON.stringify(updateObject);
        return parseResult;
    }

    //  解析API参数的配置内容
    function parseApiParam(content) {
        var updateArray = new Array();
        var addArray = new Array();
        var elements = content.split("&");
        for (var i=0; i<elements.length/8; i++ ){
            var updateObject = new Object();
            updateObject.paramName = elements[i*8].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.paramType = elements[i*8+1].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.describle = elements[i*8+2].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.isRequired = elements[i*8+3].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.rule = elements[i*8+4].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.isStructure = elements[i*8+5].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.env = getEnv();
            //updateObject.apiId = apiId; // 此处的id要等API查询出来
            updateObject.creator = $("#userName").val();
            var currentId = elements[i*8+6].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            if(0 != currentId && "" != currentId) {
                updateObject.id = currentId;
                updateArray.push(updateObject);
            } else {
                addArray.push(updateObject);
            }
            updateObject.sequence = elements[i*8+7].split("=")[1];
        }
        var parseUpdateResult = JSON.stringify(updateArray);
        var parseAddResult = JSON.stringify(addArray);
        var totalResult = parseUpdateResult + ";" + parseAddResult;
        return totalResult;
    }

    // 解析方法的配置
    function parseMethod(content) {
        var elements = content.split("&");
        var updateObject = new Object();
        updateObject.name = elements[0].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.method = elements[1].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.version = elements[2].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.id = elements[3].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        updateObject.creator = $("#userName").val();
        updateObject.env = getEnv();

        var parseMethodResult = JSON.stringify(updateObject);
        return parseMethodResult;
    }

    // 解析方法参数的配置
    function parseMethodParam(content) {
        var updateArray = new Array();
        var addArray = new Array();
        var elements = content.split("&");
        for (var i=0; i<elements.length/5; i++ ){
            var updateObject = new Object();
            updateObject.paramName = elements[i*5].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.paramType = elements[i*5+1].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.sequence = elements[i*5+2].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.isStructure = elements[i*5+3].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.env = getEnv();
            //updateObject.serviceMethodId = apiId; //  这个在JAVA里插入的时候从返回值中获取
            updateObject.creator = $("#userName").val();
            var currentId = elements[i*5+4].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            if(0 != currentId && "" != currentId) {
                updateObject.id = currentId;
                updateArray.push(updateObject);
            } else {
                addArray.push(updateObject);
            }
        }
        var parseUpdateResult = JSON.stringify(updateArray);
        var parseAddResult = JSON.stringify(addArray);
        var totalResult = parseUpdateResult + ";" + parseAddResult;
        return totalResult;
    }

    // 解析结构
    function parseStructure(content){
        var updateArray = new Array();
        var addArray = new Array();
        //var apiId = jsonObject.id;
        //var content = carmenStructureTable.$('input, select').serialize();
        //console.log("content: " + content);
        var elements = content.split("&");
        for (var i=0; i<elements.length/5; i++ ){
            var updateObject = new Object();
            updateObject.className = elements[i*5].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.fieldName = elements[i*5+1].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.fieldType = elements[i*5+2].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.isStructure = elements[i*5+3].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            updateObject.env = getEnv();
            //updateObject.serviceMethodId = apiId; //这个值从方法插入的返回结果获取
            updateObject.creator = $("#userName").val();
            var currentId = elements[i*5+4].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            if(0 != currentId && ""!=currentId) {
                updateObject.id = currentId;
                updateArray.push(updateObject);
            } else {
                addArray.push(updateObject);
            }
        }

        var parseUpdateResult = JSON.stringify(updateArray);
        var parseAddResult = JSON.stringify(addArray);
        var totalResult = parseUpdateResult + ";" + parseAddResult;
        return totalResult;
    }

    // 解析参数映射
    function parseParamMapping(content) {
        var updateArray = new Array();
        var addArray = new Array();
        var elements = content.split("&");
        for (var i=0; i<elements.length/10; i++ ){
            var updateObject = new Object();
            updateObject.dataFrom = elements[i*10].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            if(2 == updateObject.dataFrom) { //内部参数
                updateObject.fieldName = elements[i*10+1].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            } else {
                updateObject.fieldName = elements[i*10+2].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            }

            updateObject.fieldType = elements[i*10+3].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            if(1 == updateObject.dataFrom) { //API参数
                updateObject.apiParamName = elements[i*10+4].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            } else {
                updateObject.apiParamName = elements[i*10+5].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            }
            if(1 == updateObject.dataFrom) { //API参数
                updateObject.methodParamRef = elements[i*10+6].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            } else {
                updateObject.methodParamRef = elements[i*10+7].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            }

            updateObject.env = getEnv();
            // 以下这些值可以从JAVA中获取，不用再冗余设置
            //updateObject.serviceMethodId = $("#apiParamMappingParamMethodId").val();
            //updateObject.apiNamespace = apiNamespace;
            //updateObject.apiName = apiName;
            //updateObject.version = apiVersion;
            updateObject.creator = $("#userName").val();
            var currentId = elements[i*10+8].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
            if(0 != currentId && ""!= currentId) {
                updateObject.id = currentId;
                updateArray.push(updateObject);
            } else {
                addArray.push(updateObject);
            }
            updateObject.sequence = elements[i*10+9].split("=")[1].replace(/(^\+*)|(\+*$)/g, '');
        }
        var parseUpdateResult = JSON.stringify(updateArray);
        var parseAddResult = JSON.stringify(addArray);
        var totalResult = parseUpdateResult + ";" + parseAddResult;
        return totalResult;
    }


    // 获取环境变量的值
    function getEnv() {
        var env = $("#env").serialize();
        var envValue = env.split("=")[1];
        //console.log("value: " + envValue);
        return envValue;
    }


    // 绑定删除行事件，这么写是因为要给还未添加的DOM元素绑定事件
    $('body').on('click', '.deleteCurrentRow', function(e) {
        e.preventDefault();
        var flag = $(this).attr("flag");
       var tmp = $(this).closest('tr')[0];
       var content = carmenApiTable.$('input,select').serialize();
       //console.log("content" + content);
       if("step1" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id && "" != id) { // 需要从数据库中删除
                $.post("deleteapi",{"id":id}, function (d) {
                    console.log("apiResult: " + d);
                },"json");
                carmenApiTable.row(tmp).remove().draw(false);
            } else {
                carmenApiTable.row(tmp).remove().draw(false);
            }
       }  else if ("step2" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id && "" != id) {
                $.post("deleteapiparam", {"id":id}, function (d) {
                    console.log("apiParamResult: " + d);
                },"json");
                carmenParamDivTable.row(tmp).remove().draw(false);
            } else { // 简单地从页面删除
                carmenParamDivTable.row(tmp).remove().draw(false);
            }

       } else if ("step3" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id && "" != id) {
                $.post("deleteapimethod", {"id":id}, function () {
                    console.log("apiMethodResult: " + d);
                },"json");
                carmenMethodTable.row(tmp).remove().draw(false);
            } else {
                carmenMethodTable.row(tmp).remove().draw(false);
            }
       } else if ("step4" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id && "" != id) {
                $.post("deleteapimethodparam", {"id":id}, function(d) {
                    console.log("apiMethodParam: " + d);
                },"json");
                carmenMethodParamTable.row(tmp).remove().draw(false);
            } else {
                carmenMethodParamTable.row(tmp).remove().draw(false);
            }

       } else if ("step5" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id && "" != id) {
                $.post("deleteapistructure", {"id":id}, function (d) {
                    console.log("deleteApiStructure " + d);
                },"json");
                carmenStructureTable.row(tmp).remove().draw(false);
            } else {
                carmenStructureTable.row(tmp).remove().draw(false);
            }

       } else if ("step6" == flag) {
            carmenMethodMappingTable.row(tmp).remove().draw(false);
       } else if ("step7" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id && "" != id) {
                $.post("deleteapiparammapping", {"id":id}, function(d) {
                    console.log("apiParamMapping: " + d);
                },"json");
                carmenParamMappingTable.row(tmp).remove().draw(false);
            } else {
                carmenParamMappingTable.row(tmp).remove().draw(false);
            }
       }
    });

    // 预览事件
    $("#preview").on("click", function (e) {
        e.preventDefault();
        $("#apiInfo").css("display", "none");
        $("#apiParamInfo").css("display", "none");
        $("#apiMethodInfo").css("display", "none");
        $("#apiMethodParamsInfo").css("display", "none");
        $("#apiStructureInfo").css("display", "none");
        $("#apiMethodMappingInfo").css("display", "none");
        $("#apiParamMappingInfo").css("display", "none");
        $("#batchAdd").css("display", "none");
        $("#batchAddMethod").css("display", "none");

        var flag = $(this).attr("flag");  // 1代表预览，2代表关闭预览

        if(1 == flag) { // 当前是预览
            $(this).attr("flag", "2");
            $(this).html("关闭预览");
            $(".save").css("display", "none");
            $(".preStep").css("display", "none");

            $(".apiconfig").html("预览所有配置");
            $(".apiconfig").css("margin-left", "360px");

            $(".cut").css("display", "block");

            $("#apiType").css("display", "block");
            $(".apiconfig1").css("display", "block");
            $(".apiconfig2").css("display", "block");
            $(".methodconfig").css("display", "block");
            var apiType = getAPIType();
            if(2 == apiType) { // 当前是PHP配置
                $(".structureconfig").css("display", "none");
                $("#previewStep5").css("display", "none");
                $("#previewStep6").html("5");
                $("#previewStep7").html("6");

            } else { // JAVA配置
                $(".structureconfig").css("display", "block");
                $("#previewStep5").css("display", "block");
                $("#previewStep6").html("6");
                $("#previewStep7").html("7");
            }

            $(".methodmappingconfig").css("display", "block");
            $(".parammappingconfig").css("display", "block");

            $("#addRow_carmenParam").css("display", "none");
            $("#addRow_carmenMethodParam").css("display", "none");
            $("#addRow_carmenStructure").css("display", "none");
            $("#addRow_carmenParamMapping").css("display", "none");
        } else {
            $(this).attr("flag", "1");
            $(this).html("预览");
            $(".save").css("display", "block");
            $(".preStep").css("display", "block");

            $(".apiconfig").html("API与内部方法的参数映射配置");
            $(".apiconfig").css("margin-left", "260px");

            $(".cut").css("display", "none");

            $("#apiType").css("display", "none");
            $(".apiconfig1").css("display", "none");
            $(".apiconfig2").css("display", "none");
            $(".methodconfig").css("display", "none");
            $(".structureconfig").css("display", "none");
            $(".methodmappingconfig").css("display", "none");
            $(".parammappingconfig").css("display", "block");

            $("#addRow_carmenParam").css("display", "block");
            $("#addRow_carmenMethodParam").css("display", "block");
            $("#addRow_carmenStructure").css("display", "block");
            $("#addRow_carmenParamMapping").css("display", "block");
            var apiType = getAPIType();
            if(2 == apiType) {
                $("#batchAdd").css("display", "block");
                $("#batchAddMethod").css("display", "block");
            }


        }


    });


     $(".apiTypeLi").on("change", function () {
        var value = $(this).val();
        if(2 == value) { // PHP配置，去除url选项的校验
            $("#appName").removeClass("required");
        } else if (1 == value) { // JAVA配置，添加url选项的校验
            $("#appName").removeClass("required");
            $("#appName").addClass("required");
        }
     });


    $("body").on("change", "#dataFrom", function() {
        var content = $(this).val();
        //console.log("content: " + content);
        if(1 == content) { // API参数
            // 参数名的select和input的切换
            $($(this).parent().next().children()[0]).css("display", "none");
            $($(this).parent().next().children()[1]).css("display", "block");

            // api参数名的select与input的切换
            $($(this).parent().next().next().next().children()[0]).css("display", "block");
            $($(this).parent().next().next().next().children()[1]).css("display", "none");

            // api参数名的select与input的切换
            $($(this).parent().next().next().next().next().children()[0]).css("display", "block");
            $($(this).parent().next().next().next().next().children()[1]).css("display", "none");

        } else if(2 == content) { // 内部参数
            // 参数名的select和input的切换
            $($(this).parent().next().children()[0]).css("display", "block");
            $($(this).parent().next().children()[1]).css("display", "none");


            // api参数名的select与input的切换
            $($(this).parent().next().next().next().children()[0]).css("display", "none");
            $($(this).parent().next().next().next().children()[1]).css("display", "block");

            // api参数名的select与input的切换
            $($(this).parent().next().next().next().next().children()[0]).css("display", "none");
            $($(this).parent().next().next().next().next().children()[1]).css("display", "block");

        } else if(3 == content) { // 自定义参数
            // 参数名的select和input的切换
            $($(this).parent().next().children()[0]).css("display", "none");
            $($(this).parent().next().children()[1]).css("display", "block");

            // api参数名的select与input的切换
            $($(this).parent().next().next().next().children()[0]).css("display", "none");
            $($(this).parent().next().next().next().children()[1]).css("display", "block");


            // api参数名的select与input的切换
            $($(this).parent().next().next().next().next().children()[0]).css("display", "none");
            $($(this).parent().next().next().next().next().children()[1]).css("display", "block");

        }
    });

    initWidth();
    function initWidth() {
        var width = window.innerWidth;
        var apiType = getAPIType();
        if(1 == apiType) { // JAVA服务
             if(width < 1209) {
                $("ul.nav-wizard li").css("padding", "0 30px 0 40px");
             }
        } else if(2 == apiType) { // PHP服务
             if(width < 1209) {

             }
        }
    }

    window.onresize = function(){
        var width = window.innerWidth;
        var apiType = getAPIType();
        if(1 == apiType) { // JAVA服务
             if(width < 1209) {
                $("ul.nav-wizard li").css("padding", "0 30px 0 40px");
             }
        } else if(2 == apiType) { // PHP服务
             if(width < 1209) {

             }
        }
    }

    // 批量增加API参数
    $("#batchAdd").on("click", function(){
        $("#myPHPBatchModal").modal("show");
    });

    $("#sureBatchAdd").on("click", function(){
        var content = $("#phpCode").val();
        if("" == content || "undefined" == typeof(content)) {
            return;
        }
        var result = content.replace(/ => /g, ":");
        result = result.replace(/\[/g, "{");
        result = result.replace(/\]/g, "}");
        result = result.replace(/,$/, "");
        result = "{" + result + "}";
        var jsonStr = eval("(" + result + ")");
        var params = jsonStr.params;
        for(var key in params) {
            var values = params[key];
            addBatchRow(key, values.type, values.desc, values.required);
        }
        $("#myPHPBatchModal").modal("hide");
    });

    function addBatchRow(paramName, type, desc, required) {

        $("#apiParamInfo").html("");
        $("#apiParamInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';

        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value="' + paramName + '" >';
        var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" value="' + type + '" >';
        var describleElement = '<input style="max-width:100px;" type="text"  name="describle" value="' + desc + '" >';
        if(required) { // 必须填写的参数
            var isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
        } else {
            var isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        }

        var ruleElement = '<input style="max-width:140px;" type="text"  name="rule" >';
        var timestamp = Math.round(new Date().getTime()/1000);
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="0" selected>是</option><option value="1" >否</option></select><input type="hidden" name="currentId" value="0"><input type="hidden" name="currentSequence" value="' + timestamp + '">';
        carmenParamDivTable.row.add( [
           paramNameElement,paramTypeElement,describleElement,isRequiredElement,ruleElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }

    // 批量增加方法参数
    $("#batchAddMethod").on("click", function(){
        $("#myPHPBatchMethodModal").modal("show");
    });

    $("#sureBatchAddMethod").on("click", function(){
            var content = $("#phpMethodCode").val();
            if("" == content || "undefined" == typeof(content)) {
                return;
            }
            var result = content.replace(/=>/g, ":");
            result = result.replace(/^\$[a-zA-Z]+ =/g, "'params':");
            result = result.replace(/\[/g, "{");
            result = result.replace(/\]/g, "}");
            result = result.replace(/,$/, "");
            result = result.replace(/;$/, "");
            result = result.replace(/->/g, "");
            result = result.replace(/\$.*,?/g, "'test',");
            result = "{" + result + "}";
            var jsonStr = eval("(" + result + ")");
            var params = jsonStr.params;
            var count = 0;
            for(var key in params) {
                var values = params[key];
                addBatchMethodRow(key, count);
                count = count + 1;
            }
            $("#myPHPBatchMethodModal").modal("hide");
        });
    function addBatchMethodRow(key, count) {
        $("#apiMethodParamsInfo").html("");
        $("#apiMethodParamsInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step4" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value="' + key + '" />';
        var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" />';
        var sequenceElement = '<input style="max-width:160px;" type="text"  name="sequence" value="' + count + '" />';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="0" selected>是</option><option value="1" >否</option></select><input type="hidden" name="currentId" value="0">';

        carmenMethodParamTable.row.add( [
           paramNameElement,paramTypeElement,sequenceElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }
});