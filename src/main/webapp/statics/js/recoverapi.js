$(document).ready(function(){

    // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var group = $("#currentGroup").val();
      var env = $("#env").val();
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
     } else if("Release" == theme) {
        window.location.href = "release?env=" + env;
     } else if("RecoverApi" == theme) {
        window.location.href = "recoverapi?env=" + env;
     }

    });

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

    $("#recoverDeleteApi").on("click", function(){
        var ids = getReleasingApis();
        if("" == ids) {
            return;
        }
        $.post("executeapis", {"ids":ids}, function(d){
            console.log("d:" + d);
            location.reload();
        });
    });

    // 获取选中API的名字列表
    function getReleasingApis() {
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