$(document).ready(function() {
    $("#login").click(function() {
        $("#message").html("");
        var user = new Object();
        user.username = $("#username").val();
        user.password = $("#password").val();
        user.captcha = $("#captcha").val();

        if(user.username == "" || user.password == "" || user.captcha == "") {
            $("#message").html("账号、密码和验证码不能为空");
            return;
        }

//        $.post("/oauth/login/authorize", {"username": user.username,
//            "password": user.password, "captcha": user.captcha}, function(r) {
//            console.log(r);
//            if(r == false) {
//                $("#message").html("账号、密码或验证码错误");
//            } else {
//                var c = window.location.href;
//                var p = c.split("?")[1];
//                window.location.href = "/oauth/team?" + p;
//            }
//        }, "json");
        
        $.post("/oauth/login/authorize", {"username": user.username,
           "password": user.password, "captcha": user.captcha}, function(r) {
            console.log(r);
            if(r.result == false) {
                $("#message").html("请选择授权店铺");
                if(r.to != "") {
                    window.location.href = r.to;
                }
            } else {
                window.location.href = r.to;
            }
        }, "json");
        
    });
});
