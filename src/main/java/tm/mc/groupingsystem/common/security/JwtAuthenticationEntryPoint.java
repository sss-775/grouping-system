package tm.mc.groupingsystem.common.security;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tm.mc.groupingsystem.common.pojo.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理类
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        // 使用fastjson
        String json =  JSON.toJSONString(Result.customError(40001,"token失效"));
        // 指定响应格式是json
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(json);
    }
}

