package tm.mc.groupingsystem.common.filter;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tm.mc.groupingsystem.common.pojo.JwtProperties;
import tm.mc.groupingsystem.common.pojo.Result;
import tm.mc.groupingsystem.common.util.JwtTokenUtil;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (httpServletRequest.getMethod().equals("OPTIONS")) {
            log.info("浏览器的预请求的处理..");
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,OPTIONS,DELETE");
            httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,Authorization,token");
            return;
        }
        String authToken = httpServletRequest.getHeader(jwtProperties.getHeader());

        String stuId = jwtTokenUtil.getUsernameFromToken(authToken);
//        log.info("自定义过滤器获得用户名为:" + stuId);
        Long userid = jwtTokenUtil.getIdFromToken(authToken);
//        log.info("自定义过滤器获得用户id为:" + userid);
        if (stuId != null) {
            String token = (String) redisTemplate.opsForValue().get(stuId + ":token");
            if (token == null) {
                log.error("token失效，用户名为" + stuId);
                String json =  JSON.toJSONString(Result.customError(40001,"token失效"));
                // 指定响应格式是json
                httpServletResponse.setContentType("text/json;charset=utf-8");
                httpServletResponse.getWriter().write(json);
                return;
            }
        }

        //当token中的username不为空时进行验证token是否是有效的token
        if (stuId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //token中username不为空，并且Context中的认证为空，进行token验证
            UserDetails userDetails = null;
            try {
                userDetails = userDetailsService.loadUserByUsername(stuId);
            } catch (UsernameNotFoundException e) {
                log.error("用户不存在" + stuId);
                String json =  JSON.toJSONString(Result.customError(40004,"用户不存在"));
                // 指定响应格式是json
                httpServletResponse.setContentType("text/json;charset=utf-8");
                httpServletResponse.getWriter().write(json);
                return;
            }
            log.info("用户为 " + userDetails.getUsername() + "身份为 " + userDetails.getAuthorities());
            if (jwtTokenUtil.validateToken(authToken, userDetails)) { //如username不为空，并且能够在数据库中查到
                /**
                 * UsernamePasswordAuthenticationToken继承AbstractAuthenticationToken实现Authentication
                 * 所以当在页面中输入用户名和密码之后首先会进入到UsernamePasswordAuthenticationToken验证(Authentication)，
                 * 然后生成的Authentication会被交由AuthenticationManager来进行管理
                 * 而AuthenticationManager管理一系列的AuthenticationProvider，
                 * 而每一个Provider都会通UserDetailsService和UserDetail来返回一个
                 * 以UsernamePasswordAuthenticationToken实现的带用户名和密码以及权限的Authentication
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));


                //将authentication放入SecurityContextHolder中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}


