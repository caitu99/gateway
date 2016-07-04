$(document).ready(function(){

    // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      console.log(theme);
      if("Monitor" == theme){
         window.location.href="monitor?data=1";
      } else if("Home" == theme) {
        window.location.href="apilist";
      } else if("Config" == theme) {
        window.location.href="createapi";
      }
    });

    // 之前激活按钮的绑定事件
    $('a[data-toggle="tab"]').on('hide.bs.tab', function (e) {
      var theme = $(e.target).html(); // 之前激活按钮显示的关键字
      console.log(theme);
    });

    // DataTable关闭一些默认配置
    $.extend($.fn.dataTable.defaults, {
        searching: false,
        ordering: false
    });



    // 滚动监听事件
    $('body').scrollspy({ target: '#navExample' });

    //***************************************************** 1. API 配置 *********************************
    //  给API配置DIV绑定dataTable插件
    var carmenApiTable = $("#carmenApi").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
        //info: false
        //info: false
    });

    //  API配置添加行事件
    $("#addRow_carmenApi").on( 'click', function () {
        deleteApiInfo();
        var id = "0";
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step1" apiIdValue="0"  data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var appNameElement = '<input style="max-width:160px;" type="text"  name="apiAppName" >';
        var urlElement = '<input type="text"  name="apiAddressUrl">';
        var isLogElement = '<select style="min-width:50px;max-height: 25px;"  name="isLog" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        var isFreqElement = '<select style="min-width:50px;max-height: 25px;"  name="isFreq" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        var isInnerOuterElement = '<select style="min-width:50px;max-height: 25px;"  name="isInnerOuter" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        var apiTypeElement = '<select style="min-width:50px;max-height: 25px;"  name="apiType" class="form-control" ><option value="1" selected>JAVA</option><option value="0" >PHP</option></select>';
        var requestTypeElement = '<select style="min-width:50px;max-height: 25px;"  name="requestType" class="form-control" ><option value="1" selected>POST</option><option value="0" >GET</option></select><input type="hidden"  name="currentId" value="0">';

        carmenApiTable.row.add( [
           appNameElement,urlElement,isLogElement,isFreqElement,isInnerOuterElement,apiTypeElement,requestTypeElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
        //deleteRow();
    });
    // 删除提示
    function deleteApiInfo(){
        $("#apiInfo").css("display","none");
        $("#apiInfo").html("");
    }

    // 绑定查询事件
    $("#searchCarmenApi").on("click", function () {
        deleteApiInfo();
        carmenApiTable.clear(); // 清空表格中的数据
        var namespace = $("#namespace").val();
        var name = $("#name").val();
        var version = $("#version").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) {
            $("#apiInfo").html("<p>Sorry，请填写完整查询条件</p>");
            $("#apiInfo").css("display","block");
            return;
        }
        $.post("getapi", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) { // 返回异常
                console.log("yes i am in");
                $("#apiInfo").html("<p>Sorry，没有返回数据或者查询出现异常</p>");
                $("#apiInfo").css("display","block");

            } else {
                var jsonObject = eval('(' + d + ')'); // json字符串转成json对象
                apiAddRow(jsonObject);
            }

        });
        //setTimeout("deleteApiInfo()", 1000); // 1秒后删除提示信息


    });

    // 获取环境变量的值
    function getEnv() {
        var env = $("#env").serialize();
        var envValue = env.split("=")[1];
        console.log("value: " + envValue);
        return envValue;
    }



    //api配置增加行
    function apiAddRow(jsonObject) {
        var id = jsonObject.id;
        var appName = (typeof(jsonObject.appName)!="undefined") ? jsonObject.appName : "";
        var url = (typeof(jsonObject.addressUrl)!="undefined") ? jsonObject.addressUrl : "";
        var isLog = (typeof(jsonObject.enableLog)!="undefined") ? jsonObject.enableLog : "";
        var isFreq = (typeof(jsonObject.enableFreq)!="undefined") ? jsonObject.enableFreq : "";
        var isInnerOuter = (typeof(jsonObject.enableInnerOuter)!="undefined") ? jsonObject.enableInnerOuter : "";
        var apiType = (typeof(jsonObject.apiType)!="undefined") ? jsonObject.apiType : "";
        var requestType = (typeof(jsonObject.requestType)!="undefined") ? jsonObject.requestType : "";

        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step1" apiIdValue="' + id + '" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var appNameElement = '<input style="max-width:160px;" type="text"  name="apiAppName" value= "' + appName + '" >';
        var urlElement = '<input type="text"  name="apiAddressUrl" value="' + url + '">';
        var isLogElement = '<select style="min-width:50px;max-height: 25px;"  name="isLog" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        if(1 == isLog) {
            isLogElement = '<select style="min-width:50px;max-height: 25px;"  name="isLog" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
        }
        var isFreqElement = '<select style="min-width:50px;max-height: 25px;"  name="isFreq" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        if(1 == isFreq) {
            isFreqElement = '<select style="min-width:50px;max-height: 25px;"  name="isFreq" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
        }
        var isInnerOuterElement = '<select style="min-width:50px;max-height: 25px;"  name="isInnerOuter"  class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        if(1 == isInnerOuter) {
            isInnerOuterElement = '<select style="min-width:50px;max-height: 25px;"  name="isInnerOuter"  class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
        }
        var apiTypeElement = '<select style="min-width:50px;max-height: 25px;"  name="apiType" class="form-control" ><option value="1" selected>JAVA</option><option value="0" >PHP</option></select>';
        if(0 == apiType) {
            apiTypeElement = '<select style="min-width:50px;max-height: 25px;"  name="apiType" class="form-control" ><option value="1" >JAVA</option><option value="0" selected>PHP</option></select>';
        }
        var requestTypeElement = '<select style="min-width:50px;max-height: 25px;"  name="requestType" class="form-control" ><option value="1" selected>POST</option><option value="0" >GET</option></select><input type="hidden"  name="currentId" value="' + id + '">';
        if(0 == requestType) {
            requestTypeElement = '<select style="min-width:50px;max-height: 25px;"  name="requestType" class="form-control" ><option value="1" >POST</option><option value="0" selected>GET</option></select><input type="hidden"  name="currentId" value="' + id + '">';
        }
        carmenApiTable.row.add( [
           appNameElement,urlElement,isLogElement,isFreqElement,isInnerOuterElement,apiTypeElement,requestTypeElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }

    $("#updateCarmenApi").on("click", function () {

        var namespace = $("#namespace").val();
        var name = $("#name").val();
        var version = $("#version").val();
        if("" == namespace || "" == name || "" == version) {
            $("#apiInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiInfo").css("display","block");
            return;
        }
        var env = getEnv();
        var updateObject = new Object();
        updateObject.namespace = namespace;
        updateObject.name = name;
        updateObject.version = version;
        updateObject.env = env;
        updateObject.creator = $("#userName").val();
        var content = carmenApiTable.$('input, select').serialize();
        console.log("content: " + content);
        var cells = content.split("&");
        updateObject.appName = cells[0].split("=")[1];
        updateObject.addressUrl = decodeURI(cells[1].split("=")[1]);
        updateObject.enableLog = cells[2].split("=")[1];
        updateObject.enableFreq = cells[3].split("=")[1];
        updateObject.enableInnerOuter = cells[4].split("=")[1];
        updateObject.apiType = cells[5].split("=")[1];
        updateObject.requestType = cells[6].split("=")[1];
        updateObject.id = cells[7].split("=")[1];


        console.log(updateObject);
        $.post("updateapi",{"updateObject":JSON.stringify(updateObject)}, function (d) {
            console.log("result:" + d);
            $("#searchCarmenApi").click();
        });
    });

    //*************************************************** 2. API参数配置 **************************************
    var carmenParamDivTable = $("#carmenParam").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
        });
    $("#addRow_carmenParam").on("click", function () {
        $("#apiParamInfo").html("");
        $("#apiParamInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';

        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" >';
        var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" >';
        var isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
        var ruleElement = '<input style="max-width:160px;" type="text"  name="rule" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select><input type="hidden" name="currentId" value="0">';
        carmenParamDivTable.row.add( [
           paramNameElement,paramTypeElement,isRequiredElement,ruleElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });

    // 给api参数配置绑定事件
    $("#searchParam").on("click", function() {
        $("#apiParamInfo").html("");
        $("#apiParamInfo").css("display","none");
        carmenParamDivTable.clear(); // 清空表格中的数据
        var namespace = $("#namespaceParam").val();
        var name = $("#nameParam").val();
        var version = $("#versionParam").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiParamInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiParamInfo").css("display","block");
            return;
        }
        $.post("getapiparam", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) {
                $("#apiParamInfo").html("<p>Sorry，没有数据返回或者查询出现异常</p>");
                $("#apiParamInfo").css("display","block");
            } else{
                var listObject = eval('(' + d + ')'); // json字符串数组转成对象
                for(var i=0; i<listObject.length; i++) {
                    //jsonObject = eval('(' + listObject[i] + ')');
                    apiParamAddRow(listObject[i]);
                }
            }
        });
    });

    function apiParamAddRow(jsonObject){
        var id = jsonObject.id;
        var paramName = (typeof(jsonObject.paramName)!="undefined") ? jsonObject.paramName : "";
        var paramType = (typeof(jsonObject.paramType)!="undefined") ? jsonObject.paramType : "";
        var isRequired = (typeof(jsonObject.isRequired)!="undefined") ? jsonObject.isRequired : "";
        var rule = (typeof(jsonObject.rule)!="undefined") ? jsonObject.rule : "";
        var isStructure = (typeof(jsonObject.isStructure)!="undefined") ? jsonObject.isStructure : "";
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step2" apiIdValue="' + id + '" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';

        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value= "' + paramName + '" >';
        var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" value= "' + paramType + '" >';
        var isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
        if(0 == isRequired) {
            isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
        }
        var ruleElement = '<input style="max-width:160px;" type="text"  name="rule" value= "' + rule + '" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select><input type="hidden" name="currentId" value="' + id + '">';
        if(1 == isStructure) {
            isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select><input type="hidden" name="currentId" value="' + id + '">';
        }
        carmenParamDivTable.row.add( [
           paramNameElement, paramTypeElement, isRequiredElement, ruleElement, isStructureElement, operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }

    $("#updateCarmenParam").on("click", function () {
        var namespace = $("#namespaceParam").val();
        var name = $("#nameParam").val();
        var version = $("#versionParam").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiParamInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiParamInfo").css("display","block");
            return;
        }
        $.post("getapi", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) { // 返回异常
                $("#apiParamInfo").html("<p>Sorry，没有返回数据或者查询出现异常</p>");
                $("#apiParamInfo").css("display","block");
            } else {
                var updateArray = new Array();
                var addArray = new Array();
                var jsonObject = eval('(' + d + ')'); // json字符串转成json对象
                var apiId = jsonObject.id;
                var content = carmenParamDivTable.$('input, select').serialize();
                console.log("content: " + content);
                var elements = content.split("&");
                for (var i=0; i<elements.length/6; i++ ){
                    var updateObject = new Object();
                    updateObject.paramName = elements[i*6].split("=")[1];
                    updateObject.paramType = elements[i*6+1].split("=")[1];
                    updateObject.isRequired = elements[i*6+2].split("=")[1];
                    updateObject.rule = elements[i*6+3].split("=")[1];
                    updateObject.isStructure = elements[i*6+4].split("=")[1];
                    updateObject.env = getEnv();
                    updateObject.apiId = apiId;
                    updateObject.creator = $("#userName").val();
                    var currentId = elements[i*6+5].split("=")[1];
                    if(0 != currentId) {
                        updateObject.id = currentId;
                        updateArray.push(updateObject);
                    } else {
                        addArray.push(updateObject);
                    }

                }
                $.post("updateapiparam", { "updateArray": JSON.stringify(updateArray), "addArray":JSON.stringify(addArray)}, function (d) {
                    console.log("updateApiParamResult: " + d);
                    $("#searchParam").click();
                });
            }

        });

    });

    // ************************************************* 3. 方法配置 *******************************************
    var carmenMethodTable = $("#carmenMethod").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    $("#addRow_carmenMethod").on("click", function () {
        $("#apiMethodInfo").html("");
        $("#apiMethodInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step3" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var nameElement = '<input style="max-width:160px;" type="text"  name="apiAppName" >';
        var methodElement = '<input type="text"  name="apiAddressUrl" >';
        var versionElement = '<input type="text"  name="apiAddressUrl"><input type="hidden" name="currentId" value="0">';
        carmenMethodTable.row.add( [
           nameElement,methodElement,versionElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });

    $("#searchMethod").on("click", function () {
        $("#apiMethodInfo").html("");
        $("#apiMethodInfo").css("display","none");
        carmenMethodTable.clear(); // 清空表格中的数据
        var namespace = $("#namespaceMethod").val();
        var name = $("#nameMethod").val();
        var version = $("#versionMethod").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiMethodInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiMethodInfo").css("display","block");
            return;
        }
        $.post("getapimethod", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) {
                $("#apiMethodInfo").html("<p>Sorry，没有数据返回或者查询出现异常</p>");
                $("#apiMethodInfo").css("display","block");
            } else{
                var jsonObject = eval('(' + d + ')'); // json字符串数组转成对象
                apiMethodAddRow(jsonObject);
            }
        });
    });

    function apiMethodAddRow(jsonObject) {
        var id = jsonObject.id;
        var name = (typeof(jsonObject.name)!="undefined") ? jsonObject.name : "";
        var method = (typeof(jsonObject.method)!="undefined") ? jsonObject.method : "";
        var version = (typeof(jsonObject.version)!="undefined") ? jsonObject.version : "";
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step3" apiIdValue="' + id + '" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var nameElement = '<input style="max-width:160px;" type="text"  name="apiname" value= "' + name + '" >';
        var methodElement = '<input type="text"  name="apimethod" value="' + method + '">';
        var versionElement = '<input type="text"  name="apiversion" value="' + version + '"><input type="hidden" name="currentId" value="' + id + '">';

        carmenMethodTable.row.add([
           nameElement,methodElement,versionElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }

    $("#updateCarmenMethod").on("click", function () {
        var namespace = $("#namespaceMethod").val();
        var name = $("#nameMethod").val();
        var version = $("#versionMethod").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiMethodInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiMethodInfo").css("display","block");
            return;
        }
        var content = carmenMethodTable.$('input, select').serialize();
        console.log("content: " + content);
        var elements = content.split("&");
        var updateObject = new Object();
        updateObject.name = elements[0].split("=")[1];
        updateObject.method = elements[1].split("=")[1];
        updateObject.version = elements[2].split("=")[1];
        updateObject.id = elements[3].split("=")[1];
        updateObject.creator = $("#userName").val();
        updateObject.env = getEnv();
        $.post("updateapimethod", {"updateObject":JSON.stringify(updateObject)}, function (d) {
            console.log("apimethodResult: " + d);
            $("#searchMethod").click();
        });
    });

    //*********************************************** 4. 方法参数配置 ******************************************
    var carmenMethodParamTable = $("#carmenMethodParam").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    $("#addRow_carmenMethodParam").on("click", function () {
        $("#apiMethodParamsInfo").html("");
        $("#apiMethodParamsInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step4" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" >';
        var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" >';
        var sequenceElement = '<input style="max-width:160px;" type="text"  name="sequence" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select><input type="hidden" name="currentId" value="0">';

        carmenMethodParamTable.row.add( [
           paramNameElement,paramTypeElement,sequenceElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });

    $("#searchMethodParam").on("click", function () {
        $("#apiMethodParamsInfo").html("");
        $("#apiMethodParamsInfo").css("display","none");
        carmenMethodParamTable.clear(); // 清空表格中的数据
        var namespace = $("#namespaceMethodParam").val();
        var name = $("#nameMethodParam").val();
        var version = $("#versionMethodParam").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiMethodParamsInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiMethodParamsInfo").css("display","block");
            return;
        }
        $.post("getapimethodparam", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) {
                $("#apiMethodParamsInfo").html("<p>Sorry，没有数据返回或者查询出现异常</p>");
                $("#apiMethodParamsInfo").css("display","block");
            } else{
                var listObject = eval('(' + d + ')'); // json字符串数组转成对象
                for(var i=0; i<listObject.length; i++) {
                    apiMethodParamAddRow(listObject[i]);
                }
            }
        });
    });

    function apiMethodParamAddRow(jsonObject) {
        var id = jsonObject.id;
        var paramName = (typeof(jsonObject.paramName)!="undefined") ? jsonObject.paramName : "";
        var paramType = (typeof(jsonObject.paramType)!="undefined") ? jsonObject.paramType : "";
        var sequence = (typeof(jsonObject.sequence)!="undefined") ? jsonObject.sequence : "";
        var isStructure = (typeof(jsonObject.isStructure)!="undefined") ? jsonObject.isStructure : "";

        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step4" apiIdValue="' + id + '" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';

        var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value= "' + paramName + '" >';
        var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" value= "' + paramType + '" >';

        var sequenceElement = '<input style="max-width:160px;" type="text"  name="sequence" value= "' + sequence + '" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select><input type="hidden" name="currentId" value="' + id + '">';
        if(1 == isStructure) {
            isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select><input type="hidden" name="currentId" value="' + id + '">';
        }
        carmenMethodParamTable.row.add( [
           paramNameElement, paramTypeElement, sequenceElement, isStructureElement, operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }

    $("#updateCarmenMethodParam").on("click", function () {
        $("#apiMethodParamsInfo").html("");
        $("#apiMethodParamsInfo").css("display","none");
        var namespace = $("#namespaceMethodParam").val();
        var name = $("#nameMethodParam").val();
        var version = $("#versionMethodParam").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiMethodParamsInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiMethodParamsInfo").css("display","block");
            return;
        }
        $.post("getapimethod", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) { // 返回异常
                $("#apiMethodParamsInfo").html("<p>Sorry，没有返回数据或者查询出现异常</p>");
                $("#apiMethodParamsInfo").css("display","block");
            } else {
                var updateArray = new Array();
                var addArray = new Array();
                var jsonObject = eval('(' + d + ')'); // json字符串转成json对象
                var apiId = jsonObject.id;
                var content = carmenMethodParamTable.$('input, select').serialize();
                console.log("content: " + content);
                var elements = content.split("&");
                for (var i=0; i<elements.length/5; i++ ){
                    var updateObject = new Object();
                    updateObject.paramName = elements[i*5].split("=")[1];
                    updateObject.paramType = elements[i*5+1].split("=")[1];
                    updateObject.sequence = elements[i*5+2].split("=")[1];
                    updateObject.isStructure = elements[i*5+3].split("=")[1];
                    updateObject.env = getEnv();
                    updateObject.serviceMethodId = apiId;
                    updateObject.creator = $("#userName").val();
                    var currentId = elements[i*5+4].split("=")[1];
                    if(0 != currentId) {
                        updateObject.id = currentId;
                        updateArray.push(updateObject);
                    } else {
                        addArray.push(updateObject);
                    }

                }
                $.post("updateapimethodparam", { "updateArray": JSON.stringify(updateArray), "addArray":JSON.stringify(addArray)}, function (d) {
                    console.log("updateApiParamResult: " + d);
                    $("#searchMethodParam").click();
                });
            }

        });
    });

    //********************************************** 5. 结构配置 ********************************************
    var carmenStructureTable = $("#carmenStructure").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    $("#addRow_carmenStructure").on("click", function () {
        $("#apiStructureInfo").html("");
        $("#apiStructureInfo").css("display","none");
        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step5" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var classNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" >';
        var fieldNameElement = '<input style="max-width:160px;" type="text"  name="apiParamType" >';
        var fieldTypeElement = '<input style="max-width:160px;" type="text"  name="sequence" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select><input type="hidden" name="currentId" value="0">';

        carmenStructureTable.row.add( [
           classNameElement,fieldNameElement,fieldTypeElement,isStructureElement,operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });


    $("#searchStructure").on("click", function () {
        $("#apiStructureInfo").html("");
        $("#apiStructureInfo").css("display","none");
        carmenStructureTable.clear(); // 清空表格中的数据
        var namespace = $("#namespaceStructure").val();
        var name = $("#nameStructureParam").val();
        var version = $("#versionStructure").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiStructureInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiStructureInfo").css("display","block");
            return;
        }
        $.post("getapistructure", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) {
                $("#apiStructureInfo").html("<p>Sorry，没有数据返回或者查询出现异常</p>");
                $("#apiStructureInfo").css("display","block");
            } else{
                var listObject = eval('(' + d + ')'); // json字符串数组转成对象
                for(var i=0; i<listObject.length; i++) {
                    apiStructureAddRow(listObject[i]);
                }
            }
        });
    });

    function apiStructureAddRow(jsonObject) {
        var id = jsonObject.id;
        var className = (typeof(jsonObject.className)!="undefined") ? jsonObject.className : "";
        var fieldName = (typeof(jsonObject.fieldName)!="undefined") ? jsonObject.fieldName : "";
        var fieldType = (typeof(jsonObject.fieldType)!="undefined") ? jsonObject.fieldType : "";
        var isStructure = (typeof(jsonObject.isStructure)!="undefined") ? jsonObject.isStructure : "";

        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step5" apiIdValue="' + id + '" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';

        var classNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value= "' + className + '" >';
        var fieldNameElement = '<input style="max-width:160px;" type="text"  name="apiParamType" value= "' + fieldName + '" >';

        var fieldTypeElement = '<input style="max-width:160px;" type="text"  name="sequence" value= "' + fieldType + '" >';
        var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select><input type="hidden" name="currentId" value="' + id + '">';
        if(1 == isStructure) {
            isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select><input type="hidden" name="currentId" value="' + id + '">';
        }
        carmenStructureTable.row.add( [
           classNameElement, fieldNameElement, fieldTypeElement, isStructureElement, operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }

    $("#updateCarmenStructure").on("click", function () {
        $("#apiStructureInfo").html("");
        $("#apiStructureInfo").css("display","none");
        var namespace = $("#namespaceStructure").val();
        var name = $("#nameStructureParam").val();
        var version = $("#versionStructure").val();
        var env = getEnv();
        if("" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiStructureInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiStructureInfo").css("display","block");
            return;
        }
        $.post("getapimethod", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) {
            if("fail" == d) { // 返回异常
                $("#apiStructureInfo").html("<p>Sorry，没有返回数据或者查询出现异常</p>");
                $("#apiStructureInfo").css("display","block");
            } else {
                var updateArray = new Array();
                var addArray = new Array();
                var jsonObject = eval('(' + d + ')'); // json字符串转成json对象
                var apiId = jsonObject.id;
                var content = carmenStructureTable.$('input, select').serialize();
                console.log("content: " + content);
                var elements = content.split("&");
                for (var i=0; i<elements.length/5; i++ ){
                    var updateObject = new Object();
                    updateObject.className = elements[i*5].split("=")[1];
                    updateObject.fieldName = elements[i*5+1].split("=")[1];
                    updateObject.fieldType = elements[i*5+2].split("=")[1];
                    updateObject.isStructure = elements[i*5+3].split("=")[1];
                    updateObject.env = getEnv();
                    updateObject.serviceMethodId = apiId;
                    updateObject.creator = $("#userName").val();
                    var currentId = elements[i*5+4].split("=")[1];
                    if(0 != currentId) {
                        updateObject.id = currentId;
                        updateArray.push(updateObject);
                    } else {
                        addArray.push(updateObject);
                    }

                }
                $.post("updateapistructure", { "updateArray": JSON.stringify(updateArray), "addArray":JSON.stringify(addArray)}, function (d) {
                    console.log("updateApiParamResult: " + d);
                    $("#searchStructure").click();
                });
            }

        });
    });



    //********************************************** 6. 方法映射 *******************************************
    var carmenMethodMappingTable = $("#carmenMethodMapping").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

//    $("#addRow_carmenMethodMapping").on("click", function () {
//        $("#apiMethodMappingInfo").html("");
//        $("#apiMethodMappingInfo").css("display","none");
//        $("#delete_carmenMethodMapping").css("display", "none");
//        $("#add_carmenMethodMapping").css("display", "none");
//        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;" ><span class="glyphicon glyphicon-pencil methodStructure" aria-hidden="true" content=' + "test" + ' ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step6" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
//        var select = '<select style="min-width:50px;max-height: 25px;" class="form-control" ><option value="1" selected>是</option><option value="0">否</option></select>';
//        var select_1 = '<select style="min-width:70px;max-height: 25px;" class="form-control" ><option value="1" selected>是</option><option value="0">否</option></select>';
//        var select_2 = '<select style="min-width:70px;max-height: 25px;" class="form-control" ><option value="POST" selected>POST</option><option value="GET">GET</option></select>';
//
//        carmenMethodMappingTable.row.add( [
//           '<input style="max-width:160px;" type="text" id="row-1-age" name="row-1-age" value="61">','<input type="text" id="row-1-age" name="row-1-age" value="61">','<input type="text" id="row-1-age" name="row-1-age" value="61">',select_1,operate
//        ] ).draw();
//        $('[data-toggle="tooltip"]').tooltip();
//    })

    // 给参数映射查询绑定事件
    $("#searchMethodMapping").on("click", function () {
        $("#apiMethodMappingInfo").html("");
        $("#apiMethodMappingInfo").css("display","none");
        $("#delete_carmenMethodMapping").css("display", "none");
        $("#add_carmenMethodMapping").css("display", "none");
        carmenMethodMappingTable.clear(); // 清空表格中的数据
        var apiNamespace = $("#namespaceMethodMappingApi").val();
        var apiName = $("#nameMethodMappingApi").val();
        var apiVersion = $("#versionMethodMappingApi").val();
        var namespace = $("#namespaceMethodMapping").val();
        var name = $("#nameMethodMapping").val();
        var version = $("#versionMethodMapping").val();
        var env = getEnv();
        if( "" == apiNamespace || "" == apiName || "" == apiVersion || "" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiMethodMappingInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiMethodMappingInfo").css("display","block");
            return;
        }
        $.post("getapimethodmapping", {"apiNamespace":apiNamespace, "apiName":apiName, "apiVersion":apiVersion, "namespace":namespace, "name":name, "version":version, "env":env}, function (d) {

            if("fail" == d) {
                $("#apiMethodMappingInfo").html("<p>Sorry，没有数据返回或者查询出现异常</p>");
                $("#apiMethodMappingInfo").css("display","block");
            } else{
                var jsonObject = eval('(' + d + ')'); // json字符串数组转成对象
                var id = jsonObject.id;
                var apiMethodMappingId = jsonObject.apiId;
                var apiMethodMappingMethodId = jsonObject.methodId;
                var status = jsonObject.status;
                if("success" == status) { // 有映射
                    $("#apiMethodMappingInfo").html("<p>已经存在映射关系，你可以选择删除映射关系</p>");
                    $("#apiMethodMappingInfo").css("display","block");
                    $("#delete_carmenMethodMapping").css("display", "block");
                } else { //无映射
                    $("#apiMethodMappingInfo").html("<p>Sorry，没有找到映射关系,你可以添加此映射关系</p>");
                    $("#apiMethodMappingInfo").css("display","block");
                    $("#add_carmenMethodMapping").css("display","block");
                }
                $("#methodMappingId").val(id);
                $("#apiMethodMappingId").val(apiMethodMappingId);
                $("#apiMethodMappingMethodId").val(apiMethodMappingMethodId);
                //apiMethodMappingAddRow(jsonObject);
            }
        });
    });

    $("#delete_carmenMethodMapping").on("click", function () {
        var id = $("#methodMappingId").val();
        if(0 != id) {
            $.post("deletemethodmapping", {"id":id}, function (d) {
                console.log("deleteMapping " + d);
                $("#apiMethodMappingInfo").html("<p>Bingo, 删除成功~~</p>");
                $("#apiMethodMappingInfo").css("display","block");
                $("#searchMethodMapping").click();
            });
        }
    });

    $("#add_carmenMethodMapping").on("click", function () {
        $("#apiMethodMappingInfo").html("");
        $("#apiMethodMappingInfo").css("display","none");
        $("#delete_carmenMethodMapping").css("display", "none");
        $("#add_carmenMethodMapping").css("display", "none");
        var apiId = $("#apiMethodMappingId").val();
        var methodId = $("#apiMethodMappingMethodId").val();
        //var id = $("#methodMappingId").val();
        var insertObject = new Object();
        insertObject.apiId = apiId;
        insertObject.serviceMethodId = methodId;
        insertObject.env = getEnv();
        insertObject.creator = $("#userName").val();
        $.post("addmethodmapping", {"insertObject":JSON.stringify(insertObject)}, function (d) {
            console.log("insertMehtodMapping " + d);
            if("success" == d) {
                $("#apiMethodMappingInfo").html("<p>Bingo, 添加成功~~</p>");
                $("#apiMethodMappingInfo").css("display","block");
                $("#searchMethodMapping").click();
            } else {
                $("#apiMethodMappingInfo").html("<p>Sorry, 添加失败~~</p>");
                $("#apiMethodMappingInfo").css("display","block");
            }
        });
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
        var fieldNameElement = '<input style="max-width:160px;" type="text"  name="apiParamType" >';
        var fieldTypeElement = '<input style="max-width:160px;" type="text"  name="sequence" >';
        var apiParamNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" >';
        var methodParamRefElement = '<input style="max-width:160px;" type="text"  name="methodParamRef" >';
        var dateFromElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" selected>API参数</option><option value="2" >内部参数</option></select><input type="hidden" name="currentId" value="0">'
        carmenParamMappingTable.row.add( [
           fieldNameElement, fieldTypeElement, apiParamNameElement, methodParamRefElement, dateFromElement, operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    });


    $("#searchParamMapping").on("click", function () {
        $("#apiParamMappingInfo").html("");
        $("#apiParamMappingInfo").css("display","none");
        carmenParamMappingTable.clear(); // 清空表格中的数据

        var namespace = $("#namespaceParamMapping").val();
        var name = $("#nameParamMapping").val();
        var version = $("#versionParamMapping").val();
        var env = getEnv();
        if( "" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiParamMappingInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiParamMappingInfo").css("display","block");
            return;
        }

        $.post("getapimethod", { "namespace":namespace, "name":name, "version":version, "env":env }, function (d) {
            if("fail" == d) { // 返回异常
                $("#apiParamMappingInfo").html("<p>Sorry，没有找到api信息。</p>");
                $("#apiParamMappingInfo").css("display","block");
            } else {
                var jsonObject = eval('(' + d + ')'); // json字符串转成json对象
                var id = jsonObject.id;
                $("#apiParamMappingParamMethodId").val(id)
                $.post("getparammapping", {"methodId" : id , "env":getEnv()}, function (d) {
                    var listObject = eval('(' + d + ')'); // json字符串数组转成对象
                    if(0 == listObject.length) {
                        $("#apiParamMappingInfo").html("<p>Sorry，没有参数映射数据</p>");
                        $("#apiParamMappingInfo").css("display","block");
                        return;
                    }
                    for(var i=0; i<listObject.length; i++) {
                        apiParamMappingAddRow(listObject[i]);
                    }
                });
            }
        });
    });

    function apiParamMappingAddRow(jsonObject) {
        var id = jsonObject.id;
        var fieldName = (typeof(jsonObject.fieldName)!="undefined") ? jsonObject.fieldName : "";
        var fieldType = (typeof(jsonObject.fieldType)!="undefined") ? jsonObject.fieldType : "";
        var apiParamName = (typeof(jsonObject.apiParamName)!="undefined") ? jsonObject.apiParamName : "";
        var methodParamRef = (typeof(jsonObject.methodParamRef)!="undefined") ? jsonObject.methodParamRef : "";
        var dataFrom = (typeof(jsonObject.dataFrom)!="undefined") ? jsonObject.dataFrom : "";

        var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step7" apiIdValue="' + id + '" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        var fieldNameElement = '<input style="max-width:160px;" type="text"  name="apiParamType" value= "' + fieldName + '" >';
        var fieldTypeElement = '<input style="max-width:160px;" type="text"  name="sequence" value= "' + fieldType + '" >';
        var apiParamNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value= "' + apiParamName + '" >';
        var methodParamRefElement = '<input style="max-width:160px;" type="text"  name="methodParamRef" value= "' + methodParamRef + '" >';
        if(1 == dataFrom) {
            var dataFromElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" selected>API参数</option><option value="2" >内部参数</option></select><input type="hidden" name="currentId" value="' + id + '">';
        } else {
            var dataFromElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="1" >API参数</option><option value="2" selected>内部参数</option></select><input type="hidden" name="currentId" value="' + id + '">';
        }


        carmenParamMappingTable.row.add( [
           fieldNameElement, fieldTypeElement, apiParamNameElement, methodParamRefElement, dataFromElement, operate
        ] ).draw();
        $('[data-toggle="tooltip"]').tooltip();
    }


    $("#updateCarmenParamMapping").on("click", function () {
        $("#apiParamMappingInfo").html("");
        $("#apiParamMappingInfo").css("display","none");

        var namespace = $("#namespaceParamMapping").val();
        var name = $("#nameParamMapping").val();
        var version = $("#versionParamMapping").val();
        var env = getEnv();
        if( "" == namespace || "" == name || "" == version) { // 如果查询条件不完整，提示错误并返回。
            $("#apiParamMappingInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
            $("#apiParamMappingInfo").css("display","block");
            return;
        }

        $.post("getapiinfo", {"id":$("#apiParamMappingParamMethodId").val(), "env":getEnv()}, function (d) {

            if("fail" == d) {
                $("#apiParamMappingInfo").html("<p>Sorry，请把namespace, name, version填写完整再更新</p>");
                $("#apiParamMappingInfo").css("display","block");
                return;
            } else {

                var jsonObject = eval('(' + d + ')'); // json字符串数组转成对象
                var apiNamespace = (typeof(jsonObject.namespace)!="undefined") ? jsonObject.namespace : "";
                var apiName = (typeof(jsonObject.name)!="undefined") ? jsonObject.name : "";
                var apiVersion = (typeof(jsonObject.version)!="undefined") ? jsonObject.version : "";

                var updateArray = new Array();
                var addArray = new Array();

                var content = carmenParamMappingTable.$('input, select').serialize();
                console.log("content: " + content);
                var elements = content.split("&");
                for (var i=0; i<elements.length/6; i++ ){
                    var updateObject = new Object();
                    updateObject.fieldName = elements[i*6].split("=")[1];
                    updateObject.fieldType = elements[i*6+1].split("=")[1];
                    updateObject.apiParamName = elements[i*6+2].split("=")[1];
                    updateObject.methodParamRef = elements[i*6+3].split("=")[1];
                    updateObject.dataFrom = elements[i*6+4].split("=")[1];
                    updateObject.env = getEnv();
                    updateObject.serviceMethodId = $("#apiParamMappingParamMethodId").val();
                    updateObject.apiNamespace = apiNamespace;
                    updateObject.apiName = apiName;
                    updateObject.version = apiVersion;
                    updateObject.creator = $("#userName").val();
                    var currentId = elements[i*6+5].split("=")[1];
                    if(0 != currentId) {
                        updateObject.id = currentId;
                        updateArray.push(updateObject);
                    } else {
                        addArray.push(updateObject);
                    }

                }
                $.post("updateapiparammapping", { "updateArray": JSON.stringify(updateArray), "addArray":JSON.stringify(addArray)}, function (d) {
                    console.log("updateApiParamResult: " + d);
                    $("#searchParamMapping").click();
                });
            }
        });


    });




    // 绑定删除行事件，这么写是因为要给还未添加的DOM元素绑定事件
    $('body').on('click', '.deleteCurrentRow', function(e) {
        e.preventDefault();
        var flag = $(this).attr("flag");
        console.log("flag " + flag);
       console.log("i am in~~");
       var tmp = $(this).closest('tr')[0];
       console.log(tmp);
       var content = carmenApiTable.$('input,select').serialize();
       console.log("content" + content);
       if("step1" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id) { // 需要从数据库中删除
                $.post("deleteapi",{"id":id}, function (d) {
                    console.log("apiResult: " + d);
                })
                carmenApiTable.row(tmp).remove().draw(false);
            } else {
                carmenApiTable.row(tmp).remove().draw(false);
            }
       }  else if ("step2" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id) {
                $.post("deleteapiparam", {"id":id}, function (d) {
                    console.log("apiParamResult: " + d);
                });
                carmenParamDivTable.row(tmp).remove().draw(false);
            } else { // 简单地从页面删除
                carmenParamDivTable.row(tmp).remove().draw(false);
            }

       } else if ("step3" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id) {
                $.post("deleteapimethod", {"id":id}, function () {
                    console.log("apiMethodResult: " + d);
                });
                carmenMethodTable.row(tmp).remove().draw(false);
            } else {
                carmenMethodTable.row(tmp).remove().draw(false);
            }
       } else if ("step4" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id) {
                $.post("deleteapimethodparam", {"id":id}, function(d) {
                    console.log("apiMethodParam: " + d);
                });
                carmenMethodParamTable.row(tmp).remove().draw(false);
            } else {
                carmenMethodParamTable.row(tmp).remove().draw(false);
            }

       } else if ("step5" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id) {
                $.post("deleteapistructure", {"id":id}, function (d) {
                    console.log("deleteApiStructure " + d);
                });
                carmenStructureTable.row(tmp).remove().draw(false);
            } else {
                carmenStructureTable.row(tmp).remove().draw(false);
            }

       } else if ("step6" == flag) {
            carmenMethodMappingTable.row(tmp).remove().draw(false);
       } else if ("step7" == flag) {
            var id = $(this).attr("apiIdValue");
            if(0 != id) {
                $.post("deleteapiparammapping", {"id":id}, function(d) {
                    console.log("apiParamMapping: " + d);
                });
                carmenParamMappingTable.row(tmp).remove().draw(false);
            } else {
                carmenParamMappingTable.row(tmp).remove().draw(false);
            }
       }



    });



    $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件

    $(".affix").affix({
        offset: {
            top:0,
            bottom:function () {

               }
        }
    });
});