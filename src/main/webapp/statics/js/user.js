$(document).ready(function(){
   // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      var theme = $(e.target).html(); // 当前激活按钮显示的关键字
      var group = $("#currentGroup").val();
      var env = $("#env").val();
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

    // DataTable关闭一些默认配置
    $.extend($.fn.dataTable.defaults, {
        searching: false,
        ordering: false
    });

    //  给API配置DIV绑定dataTable插件
    var usersTable = $("#users").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
        //info: false
        //info: false
    });

    // 查找user
    $("#findUser").on("click", function() {
        var group = $("#userGroup").val();
        var userName = $("#username").val();
        console.log("aa:" + group + " " + userName);
        $.post("getusers", {"userGroup": group, "userName": userName}, function(d){
            console.log(d);
            usersTable.clear().draw(); //清除已有数据
            var userElement = "<span>" + d.userName +  "</span>";
            var group = "管理员";
            if(1 == d.userGroup) {
                group = "管理员";
            } else if(2 == d.userGroup) {
                group = "普通用户";
            }
            var userGroupElement = "<span>" + group + "</span>";
            var operate = '<a href="#" class="needTip deleteCurrentRow" userId="' + d.id + '"  data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
            usersTable.row.add( [
               userElement, userGroupElement, operate
            ] ).draw();
        }, "json");
    });

    // 删除user
    $("body").on("click", '.deleteCurrentRow', function(e){
        e.preventDefault();
        var id = $(this).attr("userId");
        $("#currentUserId").val(id);
        $("#deleteUserTip").html("确定要删除当前User么？");
        $("#myDeleteModal").modal("show");
    });

    // 确定删除user
    $("#sureDelete").on("click", function(e){
        e.preventDefault();
        var id = $("#currentUserId").val();
        $.post("deleteuser", {"id":id}, function(d) {
            console.log(d);
            if("fail" == d) {
                $("#deleteUserTip").html("Sorry, 删除失败~");
            } else {
                $("#deleteUserTip").html("恭喜，删除成功~");
            }
            location.reload();
        },"json");
    });

    $("#createUser").on("click", function(e) {
        $("#myAddUserModal").modal("show");
    });

    $("#sureAdd").on("click", function() {

        var flag = $("#addUserConfig").valid(); // 检测API的配置是否填写完整
        if(0 == flag) {
            return;
        }
        var userName = $("#User").val();
        var userGroup = $("#userGroup").val();

        $.post("adduser", {"userName": userName, "userGroup":userGroup}, function(d) {

            if("fail" == d) {
                console.log(d);
            } else {

            }
            location.reload();

        }, "json");
    });

    $("#addUserConfig").validate();
});