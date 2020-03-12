function post() {
    var questionId = $("#question_id").val();
    var content = $("#content").val();
    if (!content) {
        alert("评论不能为空！");
        return;
    }
    $.ajax({
        url: "/comment",
        data: JSON.stringify({
            parentId: questionId,
            description: content,
            type: 1
        }),
        type: "post",
        dataType: "json",
        contentType: "application/json",
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var conf = confirm(response.message);
                    if (conf) {
                        window.open("https://github.com/login/oauth/authorize?client_id=ffe0e41894e56ceedee9&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", "true");
                    }
                } else {
                    alert(response.message);
                }
            }
        }
    });
}