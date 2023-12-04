package tm.mc.groupingsystem.common.pojo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Result {
    private boolean success;
    private Integer serviceCode;
    private String message;
    private Object data;

    public Result(boolean success, int serviceCode, String message, Object data) {
        this.success = success;
        this.serviceCode = serviceCode;
        this.message = message;
        this.data = data;
    }


    public static Result success(String message, Object data) {
        return new Result(true, 20000, message, data);
    }

    public static Result success(String message) {
        return new Result(true, 20000, message, null);
    }

    public static Result success() {
        return new Result(true, 20000, "操作成功", null);
    }

    public static Result success(Object data) {
        return new Result(true, 20000, "操作成功", data);
    }

    /**
     * 提交成功
     */
    public static Result created(String message) {
        return new Result(true, 20001, message, null);
    }

    public static Result created() {
        return new Result(true, 20001, "提交成功", null);
    }

    /**
     * 校验异常
     */
    public static Result notValid(String message) {
        return new Result(false, 40000, message, null);
    }

    public static Result notValid() {
        return new Result(false, 40000, "字段校验异常", null);
    }

    /**
     * 没有权限访问
     */
    public static Result forbidden(String message) {
        return new Result(false, 40003, message, null);
    }

    public static Result forbidden() {
        return new Result(false, 40003, "没有权限访问", null);
    }

    /**
     * 未找到资源
     */
    public static Result notFound(String message) {
        return new Result(false, 40004, message, null);
    }

    public static Result notFound() {
        return new Result(false, 40004, "未找到资源", null);
    }

    /**
     * 不支持xxx请求
     */
    public static Result methodNotAllowed(String message) {
        return new Result(false, 40005, message, null);
    }

    /**
     * 服务器错误
     */
    public static Result internalServerError(String message) {
        return new Result(false, 50000, message, null);
    }

    public static Result internalServerError() {
        return new Result(false, 50000, "服务器错误", null);
    }

    public static Result customError(Integer serviceCode, String message) {
        return new Result(false, serviceCode, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(Integer serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", serviceCode=" + serviceCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
