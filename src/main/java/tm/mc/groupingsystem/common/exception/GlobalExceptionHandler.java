package tm.mc.groupingsystem.common.exception;

import tm.mc.groupingsystem.common.pojo.Result;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @className GlobalExceptionHandler
 * @description 全局异常处理类
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 获取e.printStackTrace() 的具体信息，赋值给String 变量，并返回
     *
     * @param e Exception
     * @return e.printStackTrace() 中 的信息
     */
    public static String getStackTraceInfo(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            //将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        } catch (Exception ex) {
            return "printStackTrace()转换错误";
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }

    }



    /**
     * 处理自定义异常
     */
    @ResponseBody
    @ExceptionHandler(value = CustomException.class)
    public Object handleCustomException(CustomException e) {
        // 输出异常信息
        Integer serviceCode = e.getServiceCode();
        String errorResult = e.getMessage() != null ? e.getMessage() : "服务器错误";
        log.info(serviceCode + errorResult + "出现自定义异常信息" + errorResult);
        // 返回自定义异常信息
        return Result.customError(serviceCode, errorResult);
    }



    /**
     * 处理枚举异常
     */
    @ResponseBody
    @ExceptionHandler(value = InvalidDataAccessApiUsageException.class)
    public Object handleIllegalArgumentException(InvalidDataAccessApiUsageException e) {
        //输出异常信息
        String errorResult = e.getMessage();
        return Result.notValid(errorResult);
    }

    /**
     * 处理请求对象属性不满足校验规则的异常信息
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        //输出异常信息
        // 获取异常信息
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errorList = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorList.add(fieldError.getField() + fieldError.getDefaultMessage());
        }
        String errorResult = errorList.toString().replace("[", "").replace("]", "");
        log.info("出现校验异常信息" + errorResult);
        // 返回处理后的异常信息
        return Result.notValid(errorResult);
    }

    /**
     * 处理请求对象属性不满足校验规则的异常信息
     */
    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Object handleConstraintViolationException(ConstraintViolationException e) {
        log.info(e.getMessage());
        return Result.notValid(e.getMessage());
    }

    /**
     * 请求方式不支持
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        //输出异常信息
        log.info("出现请求方式不存在异常信息：" + e.getMethod());
        // 返回请求方式不支持
        return Result.methodNotAllowed("不支持'" + e.getMethod() + "'请求");
    }

    /**
     * 接口不存在
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException e) {
        //输出异常信息
        log.info("出现接口不存在异常信息：" + e.getRequestURL());
        // 返回xxx接口不存在
        return Result.notFound(e.getRequestURL() + " 接口不存在");
    }

    /**
     * 数据库异常
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SQLException.class)
    public Object handleSqlException(SQLException e) {
        //输出异常信息
        log.error("出现数据库异常信息：" + getStackTraceInfo(e));
        // 处理数据库链接异常（目前尚未定位该异常原因）
        return Result.internalServerError("数据库出现异常，请联系管理员");
    }


    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = MysqlDataTruncation.class)
    public Object handleDataTruncation(MysqlDataTruncation e) {
        //输出异常信息
        log.error("出现数据库异常信息：" + getStackTraceInfo(e));
        // 处理数据库链接异常（目前尚未定位该异常原因）
        return Result.internalServerError("当前文件名长度过长，请修改文件名或联系管理员");
    }

    /**
     * 全局异常处理
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public Object handleException(Exception e) {
        // 输出异常信息
        log.error("出现未知异常信息：" + getStackTraceInfo(e));
        // 不知道是什么错误，就返回服务器错误，或者抓自定义异常等，很乱，后面再优化
        if (e instanceof UndeclaredThrowableException) {
            String errorResult = e.getCause().getMessage() != null ? e.getCause().getMessage() : "服务器错误";
            log.info(errorResult);
        }
        // unique异常
        if (e instanceof DataIntegrityViolationException) {
            log.info("出现unique异常{}", e.getCause().getCause().getMessage());
        }
        return Result.internalServerError();
    }
}
