$(document).ready(function(){
    // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var env = $("#env").val();
      //console.log(theme);
      if("Monitor" == theme){
         window.location.href = "monitor?env=" + env;
      } else if("Config" == theme) {
        window.location.href = "createapi?env=" + env;
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
      }

    });
});