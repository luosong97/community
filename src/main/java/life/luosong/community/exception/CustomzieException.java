package life.luosong.community.exception;

public class CustomzieException extends RuntimeException {
    private String message;
    private Integer code;

    public CustomzieException(ICustomizeErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMesaage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
