$(document).ready(function(){
    // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var env = $("#env").val();
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
      } else if("Release" == theme) {
        window.location.href = "release?env=" + env;
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

    //  给API配置DIV绑定dataTable插件
    var instanceDetailTable = $("#instanceTable").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
        //info: false
        //info: false
    });
});