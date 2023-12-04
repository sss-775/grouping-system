package tm.mc.groupingsystem.common.security;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tm.mc.groupingsystem.common.pojo.Result;
import tm.mc.groupingsystem.common.util.JwtTokenUtil;
import tm.mc.groupingsystem.entity.UserAuth;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 自定义认证成功处理器
 */

@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //生成token
        final String realToken = jwtTokenUtil.generateToken(authentication.getName(), ((UserAuth)authentication.getPrincipal()).getUserId());
        HashMap<String, Object> map = new HashMap<>();
        redisTemplate.opsForValue().set(authentication.getName() + ":token", realToken, 1, TimeUnit.DAYS);
        map.put("token", realToken);
        map.put("role", ((UserAuth)authentication.getPrincipal()).getIRole());


        //将生成的authentication放入容器中，生成安全的上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String json = JSON.toJSONString(Result.success(map));
        httpServletResponse.setContentType("text/json;charset=utf-8");
        httpServletResponse.getWriter().write(json);
    }
}

