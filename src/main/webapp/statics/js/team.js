$(document).ready(function() {
    $("#auth").click(function() {
        $("#message").html("");
        var kdt = new Object();
        kdt.kdtId = $("#kdt_id").val();

        if(kdt.kdtId == "") {
            $("#message").html("请选择授权的店铺");
            return;
        }

        $.post("/oauth/team/authorize", {"kdt_id": kdt.kdtId}, function(r) {
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
