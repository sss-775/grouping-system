package tm.mc.groupingsystem.common.security;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tm.mc.groupingsystem.common.pojo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthenticationFilter that supports rest login(json login) and form login.
 * @author chenhuanming
 */
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //attempt Authentication when Content-Type is json
        if(request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                ||request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){

            //use jackson to deserialize json
            // 获取请求内容
            Map<String, String> loginData = new HashMap<>(2);

            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String username = loginData.get(getUsernameParameter());
            String password = loginData.get(getPasswordParameter());

            // 创建 Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, password);
            setDetails(request, authentication);

            // 执行身份验证
            try {
                return this.getAuthenticationManager().authenticate(authentication);
            } catch (InternalAuthenticationServiceException e) {
                log.error(e.getMessage());
                String json =  JSON.toJSONString(Result.customError(40004,"用户不存在"));
                // 指定响应格式是json
                response.setContentType("text/json;charset=utf-8");
                response.getWriter().write(json);
                return null;
            }
        }

        //transmit it to UsernamePasswordAuthenticationFilter
        else {
            return super.attemptAuthentication(request, response);
        }
    }
}
