$(document).ready(function () {
    // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var env = getEnv();
      var group = $("#currentGroup").val();
      if("Monitor" == theme){
         window.location.href = "monitor?env=" + env;
      } else if("Config" == theme) {
        window.location.href = "createapi?env=" + env + "&group=" + group;
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

    $(".envRadio").on("change", function (e) {
        var result = $(e.target).val();
        $(".apiElement").remove();
        $.post("apilistbyenv", { "env": getEnv() }, function(d){
            var results = d; // 返回一个列表对象
            for(var item in results) {
                if(2 == results[item].migrateFlag) {
                    var element = '<li class="list-group-item apiElement list-group-item-warning"><span style="margin: 0 6px 0 -10px;" data-toggle="tooltip" data-placement="top" title="正在发布" class="icon-spinner icon-spin releasing"></span>';
                    element = element + "<a href='#'>" + results[item].namespace + "." + results[item].name + "." + results[item].version + "</a>";
                    element = element + '<input style="float:right" class="select" flag="2" apiContent="' + results[item].namespace + "." + results[item].name + "." + results[item].version + '" apiId="' + results[item].id + '" type="checkbox" checked disabled></li>';
                } else if (3 == results[item].migrateFlag) {
                    var element = '<li class="list-group-item apiElement">';
                    element = element + "<a href='#'>" + results[item].namespace + "." + results[item].name + "." + results[item].version + "</a>";
                    element = element + '<input style="float:right" class="select" flag="3" apiContent="' + results[item].namespace + "." + results[item].name + "." + results[item].version + '" apiId="' + results[item].id + '" type="checkbox"  disabled></li>';
                } else {
                    var element = '<li class="list-group-item apiElement">';
                    element = element + "<a href='#'>" + results[item].namespace + "." + results[item].name + "." + results[item].version + "</a>";
                    if(1 == getEnv()) {
                        element = element + '<input style="float:right" class="select" flag="1" apiContent="' + results[item].namespace + "." + results[item].name + "." + results[item].version + '" apiId="' + results[item].id + '" type="checkbox" ></li>';
                    } else {
                        element = element + '</li>';
                    }

                }

//                var operate = '<input style="float:right" class="select" apiContent="' + results[item].namespace + "." + results[item].name + "." + results[item].version + '" apiId="' + results[item].id + '" type="checkbox"></li>';
//                var insertObject = "<li class='list-group-item apiElement'><a href='#'>" + results[item].namespace + "." + results[item].name + "." + results[item].version + "</a>";
//                var element = insertObject + operate;
                $("#listAPI").append(element);
            }
            $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件
        });
    });

    $(".releasing").tooltip(); // 绑定工具提示js插件

    // 获取环境变量的值
    function getEnv() {
        var env = $("#env").serialize();
        var envValue = env.split("=")[1];
        console.log("value: " + envValue);
        return envValue;
    }

    // 选择所有的单选框
    $("#selectAll").on("click", function() {
        var flag = $(this).attr("flag");
        if(1 == flag) { // 全选
            updateCheckbox(true);
            $(this).attr("flag", "0");
            $(this).html("全不选");
        } else { // 全不选
            updateCheckbox(false);
            $(this).attr("flag", "1");
            $(this).html("全选");
        }
    });

    function updateCheckbox(flag) {
        var elements = document.getElementsByClassName("select");
        for(var i=0; i<elements.length; i++) {
            elements[i].checked = flag;
        }
    }

    $("#releaseApi").on("click", function () {
         $("#apiReleaseInfo").css("display", "none");
         $("#apiRelease1Info").css("display", "none");
         $(".apiReleaseElement").remove();
         // 删除option
         var objSelect=document.getElementById("ToDev");
         objSelect.options.length=0;

         var env = getEnv();
         if(1 == env) {
            objSelect.options.add(new Option("test", "2"));
            objSelect.options.add(new Option("product", "3"));
         } else if(2 == env){
            $("#fromDev").html("test");
            objSelect.options.add(new Option("product", "3"));

         } else {
            $("#fromDev").html("product");
            objSelect.options.add(new Option("product不能发布", "0"));
         }


         // 插入列表
         var apiList = getApis();
         if("" == apiList) { // 隐藏API的List
            $(".releasingApi").css("display", "none");
         } else {
            var apis = apiList.split(",");
            $("#numbers").html(apis.length);
            var fromEnvFlag = getEnv();
            if(1 == fromEnvFlag) { // 当前环境是dev
                $("#fromEnv").html("dev");
            } else if(2 == fromEnvFlag) { // 当前环境是test
                $("#fromEnv").html("test");
            } else if(3 == fromEnvFlag) { // 当前环境是product
                $("#fromEnv").html("product");
            }
            var toEnvFlag = $('#ToDev option:selected').val();
            if(2 == toEnvFlag) { // 目标环境是test
                $("#toEnv").html("test");
            }
            else if (3 == toEnvFlag) { // 目标环境是product
                $("#toEnv").html("product");
            }

            for (var i=0; i<apis.length; i++) {
                var insertObject = "<li class='list-group-item apiReleaseElement'><a href='#'>" + apis[i] + "</a></li>";
                $("#releaseApiList").append(insertObject);
            }
            $(".releasingApi").css("display", "block");
         }
         $("#myReleaseModal").modal("show");


    });

    // 给目标环境的选择列表绑定事件
    $('#ToDev').on("change", function () {
        var toEnvFlag=$(this).children('option:selected').val();
        if(2 == toEnvFlag) {
            $("#toEnv").html("test");
        } else if (3 == toEnvFlag) {
            $("#toEnv").html("product");
        }
    });

    // 确定发布
    $("#sureRelease").on("click", function () {
        var ids = getIds();
        if("" == ids) {
            $("#apiRelease1Info").html("<p>Sorry，你没有勾选任何API，本次发布无效。</p>");
            $("#apiRelease1Info").css("display", "block");
            return;
        } else {
            var fromEnv = getEnv();
            var toEnv = $("#ToDev").children('option:selected').val();
            windowOpenInPost(ids, fromEnv, toEnv);
//            var idArray = ids.split(",");
//            location.reload();
            //$.post("download", {"ids":ids, "fromEnv":fromEnv, "toEnv":toEnv}, function(){});
        }
    });

    //*******************************************************************************
    // Description: This function is send a post request.
    // Author: dongsheng.dds
    // Created: 2015-08-10
    //*******************************************************************************
    function windowOpenInPost(ids, fromEnv, toEnv){
        var mapForm = document.createElement("form");
        mapForm.target = "_self";
        mapForm.method = "POST";
        mapForm.action = "download";

        var mapInput = document.createElement('input');
        mapInput.type = "text";
        mapInput.name = 'ids';
        mapInput.value = ids;

        var mapInput1 = document.createElement('input');
        mapInput1.type = "text";
        mapInput1.name = 'fromEnv';
        mapInput1.value = fromEnv;

        var mapInput2 = document.createElement('input');
        mapInput2.type = "text";
        mapInput2.name = 'toEnv';
        mapInput2.value = toEnv;


        mapForm.appendChild(mapInput);
        mapForm.appendChild(mapInput1);
        mapForm.appendChild(mapInput2);

        document.body.appendChild(mapForm);

        mapForm.submit();
    }

    $("#uploadApi").on("click", function () {
        $("#file").val("");
        $("#myUploadModal").modal("show");
    });

    $("#sureUpload").on("click", function() {
        var uploadFile = $("#inputFile").val();
        console.log("file: " + uploadFile);
    });

    // 获取选中的checkbox的id
    function getIds() {
        var elements = document.getElementsByClassName("select");
        var ids = new Array();
        for(var i=0; i<elements.length; i++) {
            if(elements[i].checked && !elements[i].disabled) {
                var id=$(elements[i]).attr("apiId");
                ids.push(id);
            }
        }
        return ids.toString();
    }

    // 获取选中API的名字列表
    function getApis() {
        var elements = document.getElementsByClassName("select");
        var apiList = new Array();
        for(var i=0; i<elements.length; i++) {
            if(elements[i].checked && !elements[i].disabled) {
                var api=$(elements[i]).attr("apiContent");
                apiList.push(api);
            }
        }
        return apiList.toString();
    }

    $("#sureReleasedApi").on("click", function () {
        $("#mySureReleaseModal").modal("show");
    });

    $("#sureIfRelease").on("click", function () {
        var isRelease = getIfReleaseFlag();
        if(1 == isRelease) { // 确认发布
            var ids = getReleasingApis();
            $.post("releaseapi", {"ids":ids, "flag":1}, function (d) {
                window.location.href = "release?env=" + getEnv();
            });
        } else if(2 == isRelease) { // 取消发布
            var ids = getReleasingApis();
            $.post("releaseapi", {"ids":ids, "flag":2}, function (d) {
                window.location.href = "release?env=" + getEnv();
            });
        }
    });

    // 获取环境变量的值
    function getIfReleaseFlag() {
        var env = $(".SureReleaseForm").serialize();
        var envValue = env.split("=")[1];
        return envValue;
    }

    // 获取选中API的名字列表
    function getReleasingApis() {
        var elements = document.getElementsByClassName("select");
        var apiList = new Array();
        for(var i=0; i<elements.length; i++) {
            if(elements[i].checked && elements[i].disabled) {
                var api=$(elements[i]).attr("apiid");
                apiList.push(api);
            }
        }
        return apiList.toString();
    }

    $("#myReleaseModal").on("hidden.bs.modal", function () {
        //location.reload();
        window.location.href = "release?env=" + getEnv();
    });

    $("#physicalDeleteApi").on("click", function(){
        var ids = getDeletedApis(); // 用发布的函数获取选中的API的id号
        var env = getEnv();
        $.post("batchdelete", {"ids":ids, "env":env}, function(d){
            console.log(d);
            location.reload();
        }, "json");
    });

    // 获取选中API的名字列表
    function getDeletedApis() {
        var elements = document.getElementsByClassName("select");
        var apiList = new Array();
        for(var i=0; i<elements.length; i++) {
            if(elements[i].checked) {
                var api=$(elements[i]).attr("apiid");
                apiList.push(api);
            }
        }
        return apiList.toString();
    }
});