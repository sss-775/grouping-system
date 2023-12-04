package tm.mc.groupingsystem.common.exception;

/**
 * @className CustomException
 * @description 自定义异常处理类
 */
public class CustomException extends Exception {
    private final Integer serviceCode;

    public CustomException(Integer serviceCode, String message) {
        super(message);
        this.serviceCode = serviceCode;
    }

    public Integer getServiceCode() {
        return this.serviceCode;
    }
}