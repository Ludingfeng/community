/**
 * 提交一级评论
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#content").val();
    submitComment(questionId, content, 1);
}

/**
 * 提交二级评论
 */
function comment(e) {
    var commendId = e.getAttribute("data-id");
    var content = $("#input-" + commendId).val();
    submitComment(commendId, content, 2);
}

/**
 * 提交一级二级评论方法封装
 */
function submitComment(targetId, content, type) {
    if (!content) {
        alert("评论不能为空！");
        return;
    }
    $.ajax({
        url: "/comment",
        data: JSON.stringify({
            parentId: targetId,
            description: content,
            type: type
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

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var flag = $(e).attr("aria-expanded");
    var comments = $("#comment-" + id);
    // 二级评论展开式传递comment.id的值,用于后台获取数据
    if (flag == "false") {
        var id = e.getAttribute("data-id");
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length == 1) {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.description
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

function showSelectTag() {
    $("#select-tag").show();
}

function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if (previous.indexOf(value) == -1) {
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }
}