package tm.mc.groupingsystem.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tm.mc.groupingsystem.common.exception.CustomException;
import tm.mc.groupingsystem.common.pojo.JwtProperties;
import tm.mc.groupingsystem.common.pojo.Result;
import tm.mc.groupingsystem.common.util.JwtTokenUtil;
import tm.mc.groupingsystem.common.util.ToolUtil;
import tm.mc.groupingsystem.entity.UserInfo;
import tm.mc.groupingsystem.service.UserInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${spring.profiles.active}")
    private String env;


    @PostMapping("/register")
    public Result addUser(@Valid @RequestBody UserInfo userInfo) throws CustomException, ParseException {
//        UserInfo old = userInfoService.getUserInfo(userInfo.getUsername());
//        if (old != null) {
//            return Result.customError(20000, "当前账号已被注册");
//        }
        userInfo.setPassword(userInfo.getUsername());
        System.out.println(passwordEncoder.encode(userInfo.getUsername()));
//        userInfoService.insertUser(userInfo);
        log.info(userInfo.getUsername() + "用户注册成功");
        return Result.created();
    }

    @GetMapping("/completed")
    public Result isCompleted(HttpServletRequest request) throws CustomException, IllegalAccessException {
        String authToken = request.getHeader(jwtProperties.getHeader());
        String stuId = jwtTokenUtil.getUsernameFromToken(authToken);
        log.info(stuId + "发起请求，token为" + authToken);
        UserInfo userInfo = userInfoService.getUserInfo(stuId);
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("isCompleted", toolUtil.isCompleted(userInfo, new ArrayList<>()));
        return Result.success(map);
    }

//    @PostMapping("/update")
//    public Result updateInfo(HttpServletRequest request, @Valid @RequestBody UserInfo userInfo) throws CustomException {
//        String authToken = request.getHeader(jwtProperties.getHeader());
//        Long userId = jwtTokenUtil.getIdFromToken(authToken);
//        log.info(userId + "发起请求，token为" + authToken);
//        userInfo.setId(userId);
//        if (userInfo.getSno() != null) {
//            userInfo.setEducation(toolUtil.getEducation(userInfo.getSno()));
//        }
//        userInfoService.updateStudentInfo(userInfo);
//        userInfo = userInfoService.getUserInfoById(userId);
//        if (toolUtil.isCompleted(userInfo, Arrays.asList("role"))) {
//            userInfo.setRole("student");
//            userInfoService.updateStudentRole(userId);
//        }
//        log.info(userId + "发起请求成功");
//        return Result.success();
//    }

    @GetMapping("/info")
    public Result getInfo(HttpServletRequest request) throws CustomException {
        String authToken = request.getHeader(jwtProperties.getHeader());
        String username = jwtTokenUtil.getUsernameFromToken(authToken);
        log.info(username + "发起请求，token为" + authToken);
        return Result.success(userInfoService.getUserInfo(username));
    }
}
