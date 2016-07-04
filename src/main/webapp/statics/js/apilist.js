$(document).ready(function(){

   // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var env = getEnv();
      var group = $("#currentGroup").val();
      if("Monitor" == theme){
         window.location.href = "monitor?env=" + env;
      } else if("Release" == theme) {
        window.location.href = "release?env=" + env;
      } else if("Config" == theme) {
        window.location.href = "createapi?env=" + env + "&group=" + group;
      } else if("LogView" == theme) {
        window.location.href = "pipelog?env=" + env;
      } else if("Users" == theme) {
        window.location.href = "user?env=" + env;
      } else if("Manual" == theme) {
        window.location.href = "manual?env=" + env;
      } else if("Cache" == theme) {
        window.location.href = "cachemanage?env=" + env;
      } else if("Instance" == theme) {
        window.location.href = "instancedetail?env=" + env;
      } else if("RecoverApi" == theme) {
        window.location.href = "recoverapi?env=" + env;
      }

    });

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

    $("#apiResourceConfig").validate();
    $("#apiResourceGroupConfig").validate();

    // 之前激活按钮的绑定事件
    $('a[data-toggle="tab"]').on('hide.bs.tab', function (e) {
      var theme = $(e.target).html(); // 之前激活按钮显示的关键字
      console.log(theme);
    });

    $("#newCreateTask").click("click", function () {
        var group = $("#currentGroup").val();
        window.location.href = "createapi?env=" + getEnv() + "&group=" + group;
    });

    $(".envRadio").on("change", function (e) {
        var result = $(e.target).val();
        $(".apiElement").remove();
        $.post("apilistbyenv", { "env": getEnv() }, function(d){
            var results = d; // 返回一个列表对象
            for(var item in results) {
                var operate = "";
                if(1 == results[item].testFlag) { // 如果测试通过
                    operate += '<li class="list-group-item list-group-item-success apiElement"><span style="margin: 0 6px 0 -10px;" data-toggle="tooltip" data-placement="top" title="测试已通过" class="glyphicon glyphicon-ok-circle"></span><a href="#" class="apidetail" apiId="' + results[item].id + '" >' + results[item].namespace + '.' + results[item].name + '.' + results[item].version + '</a><a href="#" data-toggle="tooltip" data-placement="top" title="删除" style="float:right" ><span apiID="' + results[item].id + '" class="glyphicon glyphicon-trash deleteIcon"></span></a>';
                    operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="监控数据" namespace="' + results[item].namespace  + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id  + '" class="glyphicon glyphicon-eye-open apimonitor"></span></a>';
                    if(1==results[item].validFlag) {
                        operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="禁用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="1" class="glyphicon glyphicon-remove-circle disableIcon"></span></a>';
                    } else {
                        operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="启用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="0" class="glyphicon glyphicon-ok-circle disableIcon"></span></a>';
                    }
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="修改白名单" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="'+ results[item].id + '" class="glyphicon glyphicon-cog modifyWhiteList"></span></a>';
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="新增资源" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-plus-sign addResource"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="测试接口" style="float:right"><span apiId="'  + results[item].id + '"namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" class="glyphicon glyphicon-wrench testApi"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="编辑" style="float:right" ><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-edit editIcon"></span></a></li>';
                } else {
                    operate += '<li class="list-group-item  apiElement"><a href="#" class="apidetail" apiId="' + results[item].id + '" >' + results[item].namespace + '.' + results[item].name + '.' + results[item].version + '</a><a href="#" data-toggle="tooltip" data-placement="top" title="删除" style="float:right" ><span apiID="' + results[item].id + '" class="glyphicon glyphicon-trash deleteIcon"></span></a>';
                    operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="监控数据" namespace="' + results[item].namespace  + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id  + '" class="glyphicon glyphicon-eye-open apimonitor"></span></a>';
                    if(1==results[item].validFlag) {
                        operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="禁用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="1" class="glyphicon glyphicon-remove-circle disableIcon"></span></a>';
                    } else {
                       operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="启用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="0" class="glyphicon glyphicon-ok-circle disableIcon"></span></a>';
                    }
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="修改白名单" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="'+ results[item].id + '" class="glyphicon glyphicon-cog modifyWhiteList"></span></a>';
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="新增资源" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-plus-sign addResource"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="测试接口" style="float:right"><span apiId="' + results[item].id + '"namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" class="glyphicon glyphicon-wrench testApi"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="编辑" style="float:right" ><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-edit editIcon"></span></a></li>';
                }
                //var operate = "<a href='#' data-toggle='tooltip' data-placement='top' title='编辑' style='float:right'><span class='glyphicon glyphicon-edit editIcon'></span></a><a href='#' data-toggle='tooltip' data-placement='top' title='删除' style='float:right'><span class='glyphicon glyphicon-trash deleteIcon'></span></a>" + "</li>";
                //var insertObject = "<li class='list-group-item apiElement'><a href='#'>" + results[item].namespace + "." + results[item].name + "." + results[item].version + "</a>";
                var  insertObject = "";
                var element = insertObject + operate;
                $("#listAPI").append(element);
            }
            $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件
        });


    });

    // 获取环境变量的值
    function getEnv() {
        var env = $("#env").serialize();
        var envValue = env.split("=")[1];
        console.log("value: " + envValue);
        return envValue;
    }

    $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件


    // 给API的编辑接口绑定事件
    $('body').on('click', '.editIcon', function(e) {
        e.preventDefault();
        var namespace = $(this).attr("namespace");
        var name = $(this).attr("name");
        var version = $(this).attr("version");
        var apiId = $(this).attr("apiId");
        var group = $("#currentGroup").val();
        var content = "createapi?edit=1&group=" + group + "&env=" + getEnv() + "&apiId=" + apiId;
        window.location.href = "createapi?edit=1&group=" + group + "&env=" + getEnv() + "&apiId=" + apiId;
    });

    // 绑定删除事件
    $('body').on('click', '.deleteIcon', function(e) {
        e.preventDefault();
        var apiId = $(this).attr("apiID");
        console.log("apiId: " + apiId);
        if("" != apiId && 0 != apiId) {
            $("#myDeleteModal").modal('show');
            $("#deleteApiId").val(apiId); // 把id存在隐藏域里面
        }
    });

    // 给clientId绑定删除事件
    $('body').on('click', '.deleteClientId', function(e) {
        e.preventDefault();
        var tmp = $(this).closest('tr')[0];
        var currentId = $(this).parent().attr("apiIdValue");
        currentId = parseInt(currentId);
        if(0 != currentId) {
            $.post("deleteclienttable", {"id":currentId}, function(d){
                console.log("result: " + d);
                if("success" == d) {
                    $("#clientIdInfo").html("<p>删除成功</p>");
                    $("#clientIdInfo").css("display","block");
                    $("#clientIdInfo").removeClass("alert-danger");
                    $("#clientIdInfo").addClass("alert-success");
                } else {
                    $("#clientIdInfo").html("<p>删除失败</p>");
                    $("#clientIdInfo").css("display","block");
                    $("#clientIdInfo").removeClass("alert-success");
                    $("#clientIdInfo").addClass("alert-danger");
                }
            });
        }

        oAuthClientTable.row(tmp).remove().draw(false);


    });

    // 确定删除
    $('body').on('click', '#sureDelete', function(e) {
        var apiId = $("#deleteApiId").val();
        $.post("deleteapi",{"id":apiId}, function (d) {
            console.log("apiResult: " + d);
            if("success" == d) {
                $("#myDeleteModal").modal('hide');
            } else {
                $("#deleteApiTip").html("删除失败，请联系管理员~~");
                $("#myDeleteModal").delay(6000);
                $("#myDeleteModal").modal('hide');
            }
            //location.reload();
            window.location.href = "apilist?env=" + getEnv();
        },"json");
    });

    // 测试API
    $('body').on('click', '.testApi', function(e) {
        e.preventDefault();
        var apiId = $(this).attr("apiId");
        $("#currentTestApiId").val(apiId);
        var namespace = $(this).attr("namespace");
        var name = $(this).attr("name");
        var version = $(this).attr("version");
        var env = getEnv();
        $("#currentTestApiNameSpace").val(namespace);
        $("#currentTestApiName").val(name);
        $.post("getapiparam", {"namespace":namespace, "name":name, "version":version, "env":env}, function (d) { // 获取API的参数，以便直接展现在页面上，避免用户输入
            if("fail" != d) {
                // 插入accessToken
                var paramName = '<input style="max-width:160px;" type="text"  name="paramName" value="access_token" >';
                var accessToken = getCookie("accessToken");
                if("" == accessToken || null == accessToken) { // 没有access token存在
                    var value = '<input style="min-width:260px;" type="text" id="access_token"  name="value" >';
                } else {
                    var value = '<input style="min-width:260px;" type="text" id="access_token"  name="value" value="' + accessToken + '" >';
                }
                //var value = '<input style="max-width:160px;" type="text" id="access_token"  name="value" >';
                var operate = '<a href="#" class="needTip getAccessToken" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="获取Access_Token" style="margin-left:5px;"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>';
                testParamTable.row.add( [
                   paramName,value,operate
                ] ).draw();
                // 插入method参数
                var methodValue = $("#currentTestApiNameSpace").val() + "." + $("#currentTestApiName").val();
                var paramName = '<input style="max-width:160px;" type="text"  name="paramName" value="method" >';
                var value = '<input style="min-width:260px;" type="text"  name="value" value="' + methodValue +  '" >';
                var operate = '<a href="#" class="needTip deleteCurrentParam" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
                testParamTable.row.add( [
                   paramName,value,operate
                ] ).draw();
                for (var i=0; i< d.length; i++) {
                    var paramName = '<input style="max-width:160px;" type="text"  name="paramName" value="' + d[i].paramName + '" >';
                    var value = '<input style="min-width:260px;" type="text"  name="value" >';
                    var operate = '<a href="#" class="needTip deleteCurrentParam" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
                    testParamTable.row.add( [
                       paramName,value,operate
                    ] ).draw();
                }

                $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件
                $("#testApiParam").css("display", "block");
                $("#myTestModal").modal("show");
            }

        });

    });

    // 增加参数
    $(".addParams").on("click", function () {
        $("#testApiParam").css("display", "block");
        var paramName = '<input style="max-width:160px;" type="text"  name="paramName" >';
        var value = '<input style="min-width:260px;" type="text"  name="value" >';
        var operate = '<a href="#" class="needTip deleteCurrentParam" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
        testParamTable.row.add( [
           paramName,value,operate
        ] ).draw();
    });

    // 绑定删除事件
    $('body').on('click', '.deleteCurrentParam', function(e) {
        e.preventDefault();
        var tmp = $(this).closest('tr');
        testParamTable.row(tmp).remove().draw(false);
        var table = document.getElementById("testApiParam");
        var rows = table.rows.length;
        var tr = testParamTable.row().data();
        if('undefined' == typeof(tr)) {
            $("#testApiParam").css("display", "none");
        }
    });

    // 绑定获取access token事件
    $('body').on("click", ".getAccessToken", function(e) {
        e.preventDefault();
        $("#apiAccessTokenInfo").css("display","none");
        delCookie("accessToken");
        var accessToken = getCookie("accessToken");
        if("" == accessToken || null == accessToken) { // 没有access token存在
            $("#myTokenModal").modal('show');
        } else {
            $("#access_token").val(accessToken);
        }

    });

    // DataTable关闭一些默认配置
    $.extend($.fn.dataTable.defaults, {
        searching: false,
        ordering: false
    });

    // 给测试table绑定DataTable插件
    var testParamTable = $("#testApiParam").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    var oAuthClientTable = $("#clientIdParam").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
    });

    // 测试API
    $("#sureTest").on("click", function () {

        var apiRequest = $(".testApiParam").serialize();
        var requestUrl = $("#testUrl").val();
        var requestType = $("#requestType option:selected").val();

        var params = testParamTable.$('input, select').serialize();
        var paramObject = "";
        if("" != params) {
            params = params.split("&");
            var len = params.length;
            for (var i=0; i<len; i++) {
                var key = params[i].split("=")[1];
                var value = params[i+1].split("=")[1];
                i = i+1;
                if("" == paramObject) { //首个参数键值对不需要&符号
                    paramObject = key + "=" + value;
                } else {
                    paramObject = paramObject + "&" + key + "=" + value;
                }
            }
        }
        $("#apiTestInfo").html('<p><i class="icon-spinner icon-spin icon-2x"></i><span>测试中，请稍后...</span></p>');
        $("#apiTestInfo").removeClass("alert-danger");
        $("#apiTestInfo").addClass("alert-success");
        $("#apiTestInfo").css("display", "block");
        $.post("testapi", {"requestUrl":requestUrl, "requestType":requestType, "params": paramObject}, function (d) {
            if("" == d) {
                $("#apiTestInfo").html('<p>无法获取服务器，请联系管理员-叮咚</p>');
                $("#apiTestInfo").removeClass("alert-success");
                $("#apiTestInfo").addClass("alert-danger");
                $("#apiTestInfo").css("display", "block");
                $("#testResult").val("0");
                return;
            }
            var result = d;
            var content = '<p style="word-wrap: break-word;word-break: normal;">' + JSON.stringify(d) + '</p>';
            $("#testResultContent").val(content);
            re = /^fail/;
            if(!re.test(result) && "undefined" == typeof(result.error_response)) {
                updateTestStatus(1); // 1代表测试通过
            } else {
                updateTestStatus(0); // 0代表测试失败
                if(re.test(result)) {
                    $("#apiTestInfo").html('<p>' + result + '</p>');
                } else {
                    $("#apiTestInfo").html(content);
                }

                $("#apiTestInfo").removeClass("alert-success");
                $("#apiTestInfo").addClass("alert-danger");
                $("#apiTestInfo").css("display", "block");
                $("#testResult").val("0");
            }
        }, "json");
    });

    function updateTestStatus(status) {
        var apiId = $("#currentTestApiId").val();
        if(0 != apiId) { // 更新API接口中的test_flag字段
            var updateapi = new Object();
            updateapi.id = apiId;
            updateapi.testFlag = status;
            updateapi.creator = $("#user").val();
            $.post("updateapi", {"updateObject": JSON.stringify(updateapi)}, function (e) {
                if("success" == e && 1 == status) { //1代表测试通过
                    $("#apiTestInfo").html("<p>API测试通过</p>" + $("#testResultContent").val());
                    $("#apiTestInfo").removeClass("alert-danger");
                    $("#apiTestInfo").addClass("alert-success");
                    $("#apiTestInfo").css("display", "block");
                    $("#testResult").val("1");
                } else if ("fail" == e && 1 == status) { //1代表测试通过
                    $("#apiTestInfo").html("<p>API测试正确，但是更新DBgit状态失败，请联系管理员-叮咚</p>");
                    $("#apiTestInfo").removeClass("alert-success");
                    $("#apiTestInfo").addClass("alert-danger");
                    $("#apiTestInfo").css("display", "block");
                    $("#testResult").val("0");
                }
            }, "json");
        }
    }

    $("#myTestModal").on("hidden.bs.modal", function () {
        var testResult = $("#testResult").val();
        //location.reload();
        window.location.href = "apilist?env=" + getEnv();
    });

    // 给修改resource表的按钮绑定事件
    $('body').on('click', '.addResource', function(e) {
        e.preventDefault();
        document.getElementById("apiResourceConfig").reset(); // 重置表单
        document.getElementById("apiResourceGroupConfig").reset(); // 重置表单
        $("#clientIdInfo").css("display","none"); //隐藏提示
        $("#apiResourceInfo").css("display", "none");
        $("#resourceId").val("0");
        var namespace = $(this).attr("namespace");
        var name = $(this).attr("name");
        var version = $(this).attr("version");
        var apiId = $(this).attr("apiId");
        var uri = namespace + "." + name;
        $("#resourceCurrentUri").val(uri);
        $("#resourceCurrentVersion").val(version);
        $("#resourceCurrentApiId").val(apiId);
        $.post("queryresource", {"uri":uri, "version":version, "apiId":apiId}, function (d) {

            var version = $("#resourceCurrentVersion").val();
            var apiId = $("#resourceCurrentApiId").val();
            var uri = $("#resourceCurrentUri").val();

            if("undefined" != typeof(d.status) && "fail" == d.status) { // DB中还没有resource数据
                $("#uri").val(uri);
                $("#resourceVersion").val(version);
                $("#apiId").val(apiId);

                // 删除option
                var objSelect=document.getElementById("groupAlias");
                objSelect.options.length=0;
                var groupAliasList = d.groupAlias;
                var currentGroup = $("#currentGroup").val();
                for (var i=0; i<groupAliasList.length; i++) {
                    objSelect.options.add(new Option(groupAliasList[i].name, groupAliasList[i].alias));
                    if(currentGroup != "all" && groupAliasList[i].alias == currentGroup) {
                        objSelect.options[i].selected = "selected";
                    }
                }

                // 给oauth client下拉框塞数据
                var clientIds = new Array();
                var clientNames = new Array();
                var openOauthClientses = d.openOauthClientses;
                var clientLen = openOauthClientses.length;
                //var clientSelect = document.getElementById("clientId");
                //clientSelect.options.length=0;
                for (var i=0; i< clientLen; i++) {
                    clientIds.push(openOauthClientses[i].id);
                    clientNames.push(openOauthClientses[i].clientName);
                    //clientSelect.options.add(new Option(clientList[i].clientName,clientList[i].id));
                }

                clientIdsStr = clientIds.join(",");
                clientNamesStr = clientNames.join(",");
                $("#clientIdsStr").val(clientIdsStr);
                $("#clientNamesStr").val(clientNamesStr);

                // 塞oauth client 塞数据
                var oAuths = d.listValueByApi;
                var oAuthsLen = oAuths.length;
                oAuthClientTable.clear().draw(); //清除已有数据

                for (var i=0; i<oAuthsLen; i++) {
                    var clientValue = '<input style="max-width:160px;" type="text"  name="clientValue" value="' + oAuths[i].value + '" >';
                    if(1 == oAuths[i].type) {
                        var clientType = '<select style="min-width:50px;max-height: 25px;"  name="clientType" class="form-control clientType" ><option value="1" selected>分钟</option><option value="2" >小时</option></select>';
                    } else {
                        var clientType = '<select style="min-width:50px;max-height: 25px;"  name="clientType" class="form-control clientType" ><option value="1" >分钟</option><option value="2" selected>小时</option></select>';
                    }

                    var clientId = '<select style="min-width:50px;max-height: 25px;"  name="clientId" class="form-control clientId" ></select>';
                    var operate = '<a href="#" class="needTip deleteCurrentRow" flag="client" apiIdValue="' + oAuths[i].id + '" data-toggle="tooltip"  data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash deleteClientId" aria-hidden="true"></span></a><input type="hidden" name="id" value="' + oAuths[i].id + '" />';
                    oAuthClientTable.row.add( [
                                            clientValue,clientType,clientId,operate
                                         ] ).draw();
                }

                var updateClientIds = document.getElementsByClassName("clientId");
                for(var i=0; i< updateClientIds.length; i++) {
                    var currentClientRow = updateClientIds[i];
                    currentClientRow.options.length=0;
                    for (var j=0; j<clientLen; j++) {

                        currentClientRow.options.add(new Option(clientNames[j],clientIds[j]));
                        if(oAuths[i].clientId == clientIds[j]) {
                            currentClientRow.options[j].selected = "selected";
                        }
                    }

                }


                $("#myResourceModal").modal("show");
            } else { // DB中已经存在resource数据
                var openResource = d.openResource;

                var description = openResource.description;
                var groupAlias = openResource.groupAlias;
                var id = openResource.id;

                // 删除option
                var objSelect=document.getElementById("groupAlias");
                objSelect.options.length=0;
                var groupAliasList = d.groupAlias;
                for (var i=0; i<groupAliasList.length; i++) {
                    objSelect.options.add(new Option(groupAliasList[i].name, groupAliasList[i].alias));
                    if(groupAlias == groupAliasList[i].alias) {
                        objSelect.options[i].selected='selected';
                    }
                }

                $("#uri").val(uri);
                $("#resourceVersion").val(version);
                $("#apiId").val(apiId);
                $("#description").val(description);
                //$("#groupAlias").val(groupAlias);
                $("#resourceId").val(id);
                $("#apiResourceInfo").css("display", "none");


                // 给oauth client下拉框塞数据
                var clientIds = new Array();
                var clientNames = new Array();
                var openOauthClientses = d.openOauthClientses;
                var clientLen = openOauthClientses.length;
                //var clientSelect = document.getElementById("clientId");
                //clientSelect.options.length=0;
                for (var i=0; i< clientLen; i++) {
                    clientIds.push(openOauthClientses[i].id);
                    clientNames.push(openOauthClientses[i].clientName);
                    //clientSelect.options.add(new Option(clientList[i].clientName,clientList[i].id));
                }

                clientIdsStr = clientIds.join(",");
                clientNamesStr = clientNames.join(",");
                $("#clientIdsStr").val(clientIdsStr);
                $("#clientNamesStr").val(clientNamesStr);
                // 塞oauth client 塞数据
                var oAuths = d.listValueByApi;
                var oAuthsLen = oAuths.length;
                oAuthClientTable.clear().draw(); //清楚已有数据

                for (var i=0; i<oAuthsLen; i++) {
                    var clientValue = '<input style="max-width:160px;" type="text"  name="clientValue" value="' + oAuths[i].value + '" >';
                    if(1 == oAuths[i].type) { // 分钟
                        var clientType = '<select style="min-width:50px;max-height: 25px;"  name="clientType" class="form-control clientType" ><option value="1" selected>分钟</option><option value="2" >小时</option></select>';
                    } else if(2 == oAuths[i].type){ //小时
                        var clientType = '<select style="min-width:50px;max-height: 25px;"  name="clientType" class="form-control clientType" ><option value="1" >分钟</option><option value="2" selected>小时</option></select>';
                    }

                    var clientId = '<select style="min-width:50px;max-height: 25px;"  name="clientId" class="form-control clientId" ></select>';
                    var operate = '<a href="#" class="needTip deleteCurrentRow" flag="client" apiIdValue="' + oAuths[i].id + '" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash deleteClientId" aria-hidden="true"></span></a><input type="hidden" name="id" value="' + oAuths[i].id + '" />';
                    oAuthClientTable.row.add( [
                                            clientValue,clientType,clientId,operate
                                         ] ).draw();
                }

                var updateClientIds = document.getElementsByClassName("clientId");
                for(var i=0; i< updateClientIds.length; i++) {
                    var currentClientRow = updateClientIds[i];
                    currentClientRow.options.length=0;
                    for (var j=0; j<clientLen; j++) {

                        currentClientRow.options.add(new Option(clientNames[j],clientIds[j]));
                        if(oAuths[i].clientId == clientIds[j]) {
                            currentClientRow.options[j].selected = "selected";
                        }
                    }

                }


                $("#myResourceModal").modal("show");
            }
        }, "json");


    });

    // 给clientID新增行
    $("#addRowFrequency").on("click", function(e){
        $("#clientIdInfo").css("display","none"); //隐藏提示
        var randomId = new Date().getTime();
        var clientValue = '<input style="max-width:160px;" type="text"  name="clientValue" value="" >';
        var clientType = '<select style="min-width:50px;max-height: 25px;"  name="clientType" class="form-control clientType" ><option value="1" selected>分钟</option><option value="2" >小时</option></select>';
        var clientId = '<select style="min-width:50px;max-height: 25px;" id="' + randomId + '"  name="clientId" class="form-control clientId" ></select>';
        var operate = '<a href="#" class="needTip deleteCurrentRow" flag="client" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash deleteClientId" aria-hidden="true"></span></a><input type="hidden" name="id" value="0" />';
        oAuthClientTable.row.add( [
                                clientValue,clientType,clientId,operate
                             ] ).draw();

        var clientIdsStr = $("#clientIdsStr").val();
        var clientNamesStr = $("#clientNamesStr").val();
        clientIdsStr = clientIdsStr.split(",");
        clientNamesStr = clientNamesStr.split(",");
        var selectElement = document.getElementById(randomId);
        for(var i=0; i<clientIdsStr.length; i++) {
            selectElement.options.add(new Option(clientNamesStr[i],clientIdsStr[i]));
        }
    });

    // 修改资源表：resource & resourceGroup
    $("#sureModifyResource").on("click", function () {

        var saveFlag = $(this).attr("flag");
        if("resource" == saveFlag) { // 修改resource table
            var flag = $("#apiResourceConfig").valid();
            if(false == flag) { // 用户信息没有填写完整
                return;
            }
            modifyResourceTable();
        } else if("resourceGroup" == saveFlag){ // 修改resource group table
            var flag = $("#apiResourceGroupConfig").valid();
            if(false == flag) { // 用户信息没有填写完整
                return;
            }
            modifyResourceGroupTable();
        } else if("oAuthClient" == saveFlag) {
            modifyClientIdTable();
        }

    });

    // 修改client id表
    function modifyClientIdTable() {
        var content = oAuthClientTable.$('input, select').serialize();

        var updateArray = new Array();
        var addArray = new Array();
        var elements = content.split("&");
        for (var i=0; i<elements.length/4; i++ ){
            var updateObject = new Object();

            updateObject.value = elements[i*4].split("=")[1];
            updateObject.type = elements[i*4+1].split("=")[1];
            updateObject.clientId = elements[i*4+2].split("=")[1];
            updateObject.id = elements[i*4+3].split("=")[1];
            updateObject.apiRef = $("#resourceCurrentApiId").val();
            var currentId = elements[i*4+3].split("=")[1];

            if(0 != currentId && "" != currentId) {
                updateObject.id = currentId;
                updateArray.push(updateObject);
            } else {
                addArray.push(updateObject);
            }
        }
        var parseUpdateResult = JSON.stringify(updateArray);
        var parseAddResult = JSON.stringify(addArray);
        $.post("updateclienttable", {"updateArray":parseUpdateResult, "addArray":parseAddResult}, function(d){
            if("success" == d) {
                $("#clientIdInfo").html("<p>更新成功</p>");
                $("#clientIdInfo").css("display","block");
                $("#clientIdInfo").removeClass("alert-danger");
                $("#clientIdInfo").addClass("alert-success");
            } else {
                $("#clientIdInfo").html("<p>更新失败" + d + "</p>");
                $("#clientIdInfo").css("display","block");
                $("#clientIdInfo").removeClass("alert-success");
                $("#clientIdInfo").addClass("alert-danger");
            }
        });
    }

    // 修改resource表
    function modifyResourceTable() {

        var formContent = $("#apiResourceConfig").serialize();
        var fields = formContent.split("&");
        var update = new Object();
        update.uri = fields[0].split("=")[1];
        update.description = fields[1].split("=")[1];
        update.groupAlias = fields[2].split("=")[1];
        update.version = fields[3].split("=")[1];
        update.refApiId = fields[4].split("=")[1];
        update.id = fields[5].split("=")[1];

        $.post("updateresource", {"update":JSON.stringify(update)}, function (d) {
            re=/^fail/;
            if(re.test(d)) {
                $("#apiResourceInfo").html('<p>更新失败，' + d + '</p>');
                $("#apiResourceInfo").removeClass("alert-success");
                $("#apiResourceInfo").addClass("alert-danger");
                $("#apiResourceInfo").css("display", "block");
            } else {
                $("#apiResourceInfo").html('<p>恭喜你，更新成功.</p>');
                $("#apiResourceInfo").removeClass("alert-danger");
                $("#apiResourceInfo").addClass("alert-success");
                $("#apiResourceInfo").css("display", "block");
            }
        }, "json");
    }

    // 修改resource group table
    function modifyResourceGroupTable() {
        var formContent = $("#apiResourceGroupConfig").serialize();
        var fields = formContent.split("&");
        var insert = new Object();
        insert.name = fields[0].split("=")[1];
        insert.alias = fields[1].split("=")[1];
        insert.level = fields[2].split("=")[1];
        $.post("insertresourcegroup", {"insert":JSON.stringify(insert)}, function (d) {
            re=/^fail/;
            if(re.test(d)) {
                $("#apiResourceInfo").html('<p>更新失败，' + d + '</p>');
                $("#apiResourceInfo").removeClass("alert-success");
                $("#apiResourceInfo").addClass("alert-danger");
                $("#apiResourceInfo").css("display", "block");
            } else {
                $("#apiResourceInfo").html('<p>恭喜你，更新成功.</p>');
                $("#apiResourceInfo").removeClass("alert-danger");
                $("#apiResourceInfo").addClass("alert-success");
                $("#apiResourceInfo").css("display", "block");
            }
        });
    }

    // 禁用或者启用API
    $('body').on('click', '.disableIcon', function(e) {
        e.preventDefault();
        var apiId = $(this).attr("apiID");
        var namespace = $(this).attr("namespace");
        var name = $(this).attr("name");
        var version = $(this).attr("version");
        var timestamp = (new Date()).valueOf();
        $(this).attr("timestamp", timestamp);
        $("#resourceTimeStamp").val(timestamp);
        var update = new Object();
        update.id=apiId;
        update.namespace = namespace;
        update.name = name;
        update.version = version;
        update.env = getEnv();
        var flag = $(this).attr("flag");
        if(1 == flag){
            update.validFlag = 0;
        } else {
            update.validFlag = 1;
        }
        update.modifier = $("#user").val();

        $.post("disableapi", {"update": JSON.stringify(update)}, function (d) {
            if("success" == d) {
                var currentTimestamp = $("#resourceTimeStamp").val();
                var flag = $(".disableIcon[timestamp="+ currentTimestamp +"]").attr("flag");
                if(1 == flag) { // 1表示当前是禁用状态
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").removeClass("glyphicon-remove-circle");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").addClass("glyphicon-ok-circle");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").attr("flag", "0");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").attr("title", "启用");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").attr("data-original-title", "启用");

                } else {
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").removeClass("glyphicon-ok-circle");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").addClass("glyphicon-remove-circle");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").attr("flag", "1");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").attr("title", "禁用");
                    $(".disableIcon[timestamp="+ currentTimestamp +"]").attr("data-original-title", "禁用");
                }
                $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件


            }
        });
    });

    // 给资源配置的tab页绑定js插件
    $("#resourceConfigTab a").click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    });


    // 给配置资源时 切换相应的标签页内容
    $('a[data-toggle="resourceTab"]').on("shown.bs.tab", function (e) {
        var content = $(e.target).html(); // 当前点击标签页的文本内容

        if("Resource" == content) { // 配置resource表
            $("#apiResourceInfo").css("display", "none");
            $("#apiResourceConfig").css("display", "block");
            $("#apiResourceGroupConfig").css("display", "none");
            $("#apiClientIdConfig").css("display", "none");
            $("#sureModifyResource").attr("flag", "resource");
            $("#addRowFrequency").css("display", "none");
        } else if("ResourceGroup" == content) { // 配置resource group表
            $("#apiResourceInfo").css("display", "none");
            $("#apiResourceConfig").css("display", "none");
            $("#apiClientIdConfig").css("display", "none");
            $("#apiResourceGroupConfig").css("display", "block");
            $("#sureModifyResource").attr("flag", "resourceGroup");
            $("#addRowFrequency").css("display", "none");
        } else if("OAuth Client" == content) { // 配置oAuth client table
            $("#apiResourceInfo").css("display", "none");
            $("#apiResourceConfig").css("display", "none");
            $("#apiResourceGroupConfig").css("display", "none");
            $("#apiClientIdConfig").css("display", "block");
            $("#sureModifyResource").attr("flag", "oAuthClient");

            $("#addRowFrequency").css("display", "block");
        }

    });

//    $(".modifyWhiteList").on("click", function () {
//        $("#apiModifyWhiteListInfo").css("display", "none");
//        var namespace = $(this).attr("namespace");
//        var name = $(this).attr("name");
//        $("#modifyNamespace").val(namespace);
//        $("#modifyName").val(name);
//        $("#myWhiteListModal").modal("show");
//    });

    $("body").on("click", ".modifyWhiteList", function (e) {
        $("#apiModifyWhiteListInfo").css("display", "none");
        var namespace = $(this).attr("namespace");
        var name = $(this).attr("name");
        $("#modifyNamespace").val(namespace);
        $("#modifyName").val(name);
        $("#myWhiteListModal").modal("show");
    });

    $("#sureModify").on("click", function () {
        var flag = getWhiteListFlag();
        var namespace = $("#modifyNamespace").val();
        var name = $("#modifyName").val();
        var api = namespace + "." + name;
        var operate = "add";
        if(2 == flag) {
            operate = "delete";
        }
        $.post("modifywhitelist", {"operate":operate, "api": api}, function (d) {
            if( "invalid" == d) { // API已经在白名单中
                $("#apiModifyWhiteListInfo").html('<p>此API已经在白名单中</p>');
                $("#apiModifyWhiteListInfo").removeClass("alert-success");
                $("#apiModifyWhiteListInfo").addClass("alert-danger");
                $("#apiModifyWhiteListInfo").css("display", "block");
            } else if ("addsuccess" == d) {
                $("#apiModifyWhiteListInfo").html('<p>添加成功</p>');
                $("#apiModifyWhiteListInfo").removeClass("alert-danger");
                $("#apiModifyWhiteListInfo").addClass("alert-success");
                $("#apiModifyWhiteListInfo").css("display", "block");
            } else if("addfail" == d) {
                $("#apiModifyWhiteListInfo").html('<p>添加失败</p>');
                $("#apiModifyWhiteListInfo").removeClass("alert-success");
                $("#apiModifyWhiteListInfo").addClass("alert-danger");
                $("#apiModifyWhiteListInfo").css("display", "block");
            } else if ("delsuccess" == d) {
                $("#apiModifyWhiteListInfo").html('<p>删除成功</p>');
                $("#apiModifyWhiteListInfo").removeClass("alert-danger");
                $("#apiModifyWhiteListInfo").addClass("alert-success");
                $("#apiModifyWhiteListInfo").css("display", "block");
            } else if("delfail" == d) {
                $("#apiModifyWhiteListInfo").html('<p>删除失败</p>');
                $("#apiModifyWhiteListInfo").removeClass("alert-success");
                $("#apiModifyWhiteListInfo").addClass("alert-danger");
                $("#apiModifyWhiteListInfo").css("display", "block");
            }else if("NoRow" == d) {
                $("#apiModifyWhiteListInfo").html('<p>此API不在白名单中</p>');
                $("#apiModifyWhiteListInfo").removeClass("alert-success");
                $("#apiModifyWhiteListInfo").addClass("alert-danger");
                $("#apiModifyWhiteListInfo").css("display", "block");
            }
        }, "json");
    });

    // 获取环境变量的值
    function getWhiteListFlag() {
        var env = $(".modifyWhiteListForm").serialize();
        var envValue = env.split("=")[1];
        return envValue;
    }

    $("#testMonitorTask").on("click", function () {
        var etime =  Math.ceil(new Date().getTime()/1000);
        var stime = etime - 60;
//        stime = 1439395200;
//        etime = 1439481600;

        var metric = new Array();
        metric.push("callCount");
        metric.push("maxResponseTime");
        metric.push("failure");
        var element = metric.join(",");
        var api = "test";
        $.post("querymonitor", {"stime":stime, "etime":etime, "metric":element, "api":api}, function (d) {

            d = eval('(' + d + ')');

            for(var i=0; i<d.length; i++) {
                var metric = d[i].metric;
                var dps = d[i].dps;
                var flag = $.isEmptyObject(dps); // dps是否为空对象
                if (flag == false) { // json非空
                    for(var key in dps) {
                        console.log("key: " + key);
                        console.log("value: " + dps[key]);
                    }
                } else {
                    console.log("flag: " + flag);
                }
                console.log("metric: " + metric);
                console.log("dps: " + dps);
            }




            var flag = $.isEmptyObject(d);
            if (flag == false) { // json非空
                console.log("flag: " + flag);
                for(var key in d) {
                    console.log("key: " + key);
                    console.log("value: " + d[key]);
                }
            } else {
                console.log("flag: " + flag);
            }
        }, "json");
    });

    // 获取API详情
    $("body").on("click", ".apidetail", function(e) {
        e.preventDefault();
        var apiId = $(this).attr("apiId");
        var env = getEnv();
        var mapForm = document.createElement("form");
        mapForm.target = "_self";
        mapForm.method = "POST";
        mapForm.action = "apidetail";

        var mapInput = document.createElement('input');
        mapInput.type = "text";
        mapInput.name = 'id';
        mapInput.value = apiId;

        var mapInput1 = document.createElement('input');
        mapInput1.type = "text";
        mapInput1.name = 'env';
        mapInput1.value = env;

        mapForm.appendChild(mapInput);
        mapForm.appendChild(mapInput1);

        document.body.appendChild(mapForm);

        mapForm.submit();

    });

    // 通过API链接到监控数据
    $("body").on("click", ".apimonitor", function(e){
        e.preventDefault();
        var namespace = $(this).attr("namespace");
        var name = $(this).attr("name");
        var version = $(this).attr("version");
        var env = getEnv();
        var mapForm = document.createElement("form");
        mapForm.target = "_self";
        mapForm.method = "POST";
        mapForm.action = "monitor";

        var mapInput = document.createElement('input');
        mapInput.type = "text";
        mapInput.name = 'namespace';
        mapInput.value = namespace;

        var mapInput1 = document.createElement('input');
        mapInput1.type = "text";
        mapInput1.name = 'name';
        mapInput1.value = name;

        var mapInput2 = document.createElement('input');
        mapInput2.type = "text";
        mapInput2.name = 'version';
        mapInput2.value = version;

        mapForm.appendChild(mapInput);
        mapForm.appendChild(mapInput1);
        mapForm.appendChild(mapInput2);
        document.body.appendChild(mapForm);

        mapForm.submit();
    });

    $("#accessToken").validate(); // 绑定表单验证
    $("#sureGetToken").on("click", function(){
        var userName = $("#userName").val();
        var password = $("#password").val();
        var flag = $("#accessToken").valid();
        if(false == flag) {
            return;
        }
        var username = userName; // 用户名
        var password = password; // 密码
        $.post("getaccesstoken", {"username":username, "password":password}, function(d) {
            var accessToken = d.access_token;
            if("undefined" == typeof(accessToken)) {
                var errorDescription = d.error_description;
                if("undefined" == errorDescription) {
                    errorDescription = "error,请再试一次。"
                }
                $("#access_token").val("");
                $("#apiAccessTokenInfo").html("<p>" + errorDescription + "</p>");
                $("#apiAccessTokenInfo").removeClass("alert-success");
                $("#apiAccessTokenInfo").addClass("alert-danger");
                $("#apiAccessTokenInfo").css("display","block");
            } else {
                document.cookie = "accessToken=" + accessToken;
                $("#access_token").val(accessToken);
                $("#apiAccessTokenInfo").html("<p>" + "成功获取Access_token: " +  accessToken + " 请关闭并继续" + "</p>");
                $("#apiAccessTokenInfo").removeClass("alert-danger");
                $("#apiAccessTokenInfo").addClass("alert-success");
                $("#apiAccessTokenInfo").css("display","block");
                $("#myTokenModal").modal('hide');
            }

        }, "json");

    });

    function getCookie(c_name)
    {
        if (document.cookie.length>0)
          {
          c_start=document.cookie.indexOf(c_name + "=")
          if (c_start!=-1)
            {
            c_start=c_start + c_name.length+1
            c_end=document.cookie.indexOf(";",c_start)
            if (c_end==-1) c_end=document.cookie.length
            return unescape(document.cookie.substring(c_start,c_end))
            }
          }
        return ""
    }

    //删除cookies
    function delCookie(name)
    {
        var exp = new Date();
        exp.setTime(exp.getTime() - 1);
        var cval = getCookie(name);
        if(cval != null) {
            document.cookie= name + "="+cval+";expires="+exp.toGMTString();
        }
    }

    $("#navbarLeft").affix();

    $("body").on("click", ".affixGroup", function(e){
        e.preventDefault();
        $("#navbarLeft li").each(function(index){
            $(this).removeClass("active");
            $(this).removeClass("list-group-item-info");
        });
        $(this).addClass("active");
        var group = $(this).attr("group");
        var env = getEnv();
        $(".apiElement").remove(); // 清除之前所有的API列表
        $("#currentGroup").val(group); // 设置当前的group,以便新建的时候去的取到当前的group
        $.post("getapibygroup", {"group": group, "env":env}, function(d){
            //console.log("d: " + d);
            var results = d; // 返回一个列表对象
            for(var item in results) {
                var operate = "";
                if(1 == results[item].testFlag) { // 如果测试通过
                    operate += '<li class="list-group-item list-group-item-success apiElement"><span style="margin: 0 6px 0 -10px;" data-toggle="tooltip" data-placement="top" title="测试已通过" class="glyphicon glyphicon-ok-circle"></span><a href="#" class="apidetail" apiId="' + results[item].id + '" >' + results[item].namespace + '.' + results[item].name + '.' + results[item].version + '</a><a href="#" data-toggle="tooltip" data-placement="top" title="删除" style="float:right" ><span apiID="' + results[item].id + '" class="glyphicon glyphicon-trash deleteIcon"></span></a>';
                    operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="监控数据" namespace="' + results[item].namespace  + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id  + '" class="glyphicon glyphicon-eye-open apimonitor"></span></a>';
                    if(1==results[item].validFlag) {
                        operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="禁用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="1" class="glyphicon glyphicon-remove-circle disableIcon"></span></a>';
                    } else {
                        operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="启用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="0" class="glyphicon glyphicon-ok-circle disableIcon"></span></a>';
                    }
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="修改白名单" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="'+ results[item].id + '" class="glyphicon glyphicon-cog modifyWhiteList"></span></a>';
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="新增资源" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-plus-sign addResource"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="测试接口" style="float:right"><span apiId="'  + results[item].id + '"namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" class="glyphicon glyphicon-wrench testApi"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="编辑" style="float:right" ><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-edit editIcon"></span></a></li>';
                } else {
                    operate += '<li class="list-group-item  apiElement"><a href="#" class="apidetail" apiId="' + results[item].id + '" >' + results[item].namespace + '.' + results[item].name + '.' + results[item].version + '</a><a href="#" data-toggle="tooltip" data-placement="top" title="删除" style="float:right" ><span apiID="' + results[item].id + '" class="glyphicon glyphicon-trash deleteIcon"></span></a>';
                    operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="监控数据" namespace="' + results[item].namespace  + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id  + '" class="glyphicon glyphicon-eye-open apimonitor"></span></a>';
                    if(1==results[item].validFlag) {
                        operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="禁用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="1" class="glyphicon glyphicon-remove-circle disableIcon"></span></a>';
                    } else {
                       operate += '<a href="#" style="float:right" ><span data-toggle="tooltip" data-placement="top" title="启用" namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiID="' + results[item].id + '" flag="0" class="glyphicon glyphicon-ok-circle disableIcon"></span></a>';
                    }
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="修改白名单" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="'+ results[item].id + '" class="glyphicon glyphicon-cog modifyWhiteList"></span></a>';
                    operate += '<a href="#" data-toggle="tooltip" data-placement="top" title="新增资源" style="float:right"><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-plus-sign addResource"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="测试接口" style="float:right"><span apiId="' + results[item].id + '"namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" class="glyphicon glyphicon-wrench testApi"></span></a><a href="#" data-toggle="tooltip" data-placement="top" title="编辑" style="float:right" ><span namespace="' + results[item].namespace + '" name="' + results[item].name + '" version="' + results[item].version + '" apiId="' + results[item].id + '" class="glyphicon glyphicon-edit editIcon"></span></a></li>';
                }
                //var operate = "<a href='#' data-toggle='tooltip' data-placement='top' title='编辑' style='float:right'><span class='glyphicon glyphicon-edit editIcon'></span></a><a href='#' data-toggle='tooltip' data-placement='top' title='删除' style='float:right'><span class='glyphicon glyphicon-trash deleteIcon'></span></a>" + "</li>";
                //var insertObject = "<li class='list-group-item apiElement'><a href='#'>" + results[item].namespace + "." + results[item].name + "." + results[item].version + "</a>";
                var  insertObject = "";
                var element = insertObject + operate;
                $("#listAPI").append(element);
            }
            $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件
        });
    });




});