package com.ldf.community.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001,"回复的问题被删除或不存在！"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论进行回复！"),
    NO_LOGIN(2003,"当前操作需要登录，是否登录？"),
    SYS_ERROR(2004,"服务崩溃了！"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在！"),
    COMMENT_NOT_FOUND(2006,"回复的评论被删除或不存在！"),
    COMMENT_IS_EMPTY(2007,"请输入评论内容！"),
    NOTIFICATION_NOT_FOUND(2008,"读取的消息不存在或消息已被读取！"),
    NOTIFICATION_READ_FAIL(2009,"读取消息错误！"),
    FILE_UPLOAD_FAIL(2010,"图片上传失败！"),
    REGISTER_FAIL(2011,"注册失败！未知错误！"),
    ;

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }


}
