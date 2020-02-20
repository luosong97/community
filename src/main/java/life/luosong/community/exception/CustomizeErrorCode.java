package life.luosong.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND(2001,"你找的问题不存在了!"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论进行回复!"),
    NO_LOGIN(2003,"当前操作需要登录，请登录后重试"),
    SYS_ERROR(2004,"服务冒烟了，要不然你稍后再试试!!!"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"你操作的评论不存在了"),
    COMMENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"兄弟你这是在读别人信息呢？"),
    NOTIFICATION_NOT_FOUND(2009,"消息莫非是不翼而飞了？"),
    FILE_UPLOAD_FAIL(2010,"图片上传失败")
    ;

    @Override
    public String getMesaage() {
        return mesaage;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    private String mesaage;
    private Integer code;

    CustomizeErrorCode(Integer code, String mesaage) {
        this.mesaage = mesaage;
        this.code = code;
    }
}
