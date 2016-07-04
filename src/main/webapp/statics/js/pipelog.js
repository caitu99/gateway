$(document).ready(function(){
   $('#date-start').datetimepicker({ // 绑定时间选择器
        format: 'yyyy-mm-dd hh:ii',
        todayBtn: 1,
        todayHighlight: 1,
        weekStart: 1,
        startView: 2,
           forceParse: 0,
        pickerPosition: "bottom-left",
        //showMeridian: true,
        autoclose: true,
        //initialDate: "2015-01-01 12:12"
    }).on("changeDate", function(e){
           var currentDate = Math.floor(e.date.valueOf()/1000);
       });
    $('#date-end').datetimepicker({ // 绑定时间选择器
           format: 'yyyy-mm-dd hh:ii',
           todayBtn: 1,
           todayHighlight: 1,
           weekStart: 1,
           startView: 2,
           forceParse: 0,
           pickerPosition: "bottom-left",
           //showMeridian: true,
           autoclose: true
       }).on("changeDate", function(e){
           var currentDate = Math.floor(e.date.valueOf()/1000); //GMT时间，不符合北京时间

       });

   // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var env = $("#env").val();
      var group = $("#currentGroup").val();
      if("Monitor" == theme){
         window.location.href = "monitor?env=" + env;
      } else if("Release" == theme) {
        window.location.href = "release?env=" + env;
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
      } else if("Cache" == theme) {
         window.location.href = "cachemanage?env=" + env;
      } else if("Instance" == theme) {
         window.location.href = "instancedetail?env=" + env;
      } else if("RecoverApi" == theme) {
        window.location.href = "recoverapi?env=" + env;
      }

    });

    // 跳转到帮助页面
    $(".help").on("click", function(e){
        e.preventDefault();
        var env = $("#env").val();
        location.href = "manual?env=" + env;
    });

    $(".console").on("click", function(e) {
        e.preventDefault();
        var content = $(this).html();
        $("#consoleText").html(content);
    });

    defaultDisplay();

    //页面加载时执行一次默认查询
    function defaultDisplay() {

        // 当天零点
        var dateStart = new Date();
        var year = dateStart.getFullYear();
        var month = dateStart.getMonth();
        var date = dateStart.getDate();
        var hour = dateStart.getHours();
        var minutes = dateStart.getMinutes();
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        var tmp = new Date(year, month, date, 0, 0, 0);

        var endTime = Math.floor(dateStart.getTime() / 1000); // 获取毫秒时间
        var startTime = endTime - 10*60; // 过去10分钟
        var startObject = new Date(startTime*1000);
        var startYear = startObject.getFullYear();
        var startMonth = startObject.getMonth();
        var startDate = startObject.getDate();
        var startHour = startObject.getHours();
        var startMinutes = startObject.getMinutes();
        if (startMinutes < 10) {
            startMinutes = "0" + startMinutes;
        }

        var monthSt = parseInt(month) + 1;
        startMonth = parseInt(startMonth) + 1;
        var timeStr = startYear + "-" + startMonth + "-" + startDate + " " + startHour + ":" + startMinutes;
        $("#datetimepickerStart").val(timeStr);

        var endStr = year + "-" + monthSt + "-" + date + " " + hour + ":" + minutes;
        $("#datetimepickerEnd").val(endStr);


        $.post("querylog", {"startTime":startTime, "endTime":endTime}, function(d) {
            console.log("d: " + d);
            if("empty" == d) { // 没有数据直接提示并返回
                var element = "<i class='log'><p> Sorry, no data is found.</p></i>";
                $("#listLog").append(element);
                return;
            }
            var log = d.split(";");
            for (var i=0; i< log.length; i++) {
                console.log(i + ": " + log[i]);
                if(i%2 == 1) {
                    var element = "<i class='log'><p>" + log[i] + "</p></i>";
                } else {
                    var element = "<i class='list-group-item list-group-item-info log'><p>" + log[i] + "</p></i>";
                }

                $("#listLog").append(element);
            }

         });
    }

     // 按日期区间查询监控数据
     $("#statistic").on("click", function(){
         $(".log").remove();
         var startTime = $("#datetimepickerStart").val();
         var endTime = $("#datetimepickerEnd").val();
         if("" == startTime || "" == endTime) {
             return;
         }
         startTime = Math.floor(new Date(startTime).getTime()/1000);
         endTime = Math.floor(new Date(endTime).getTime()/1000);

         var api = $("#api").val();
         var id = $("#ID").val();
         $.post("querylog", {"startTime":startTime, "endTime":endTime, "api":api, "id":id}, function(d) {
            console.log("d: " + d);
            if("empty" == d) { // 没有数据直接提示并返回
                var element = "<i class='log'><p> Sorry, no data is found.</p></i>";
                $("#listLog").append(element);
                return;
            }
            var log = d.split(";");
            for (var i=0; i< log.length; i++) {
                console.log(i + ": " + log[i]);
                if(i%2 == 1) {
                    var element = "<i class='log'><p>" + log[i] + "</p></i>";
                } else {
                    var element = "<i class='list-group-item list-group-item-info log'><p>" + log[i] + "</p></i>";
                }

                $("#listLog").append(element);
            }

         });
     });

});