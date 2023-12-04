package tm.mc.groupingsystem.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tm.mc.groupingsystem.common.exception.CustomException;
import tm.mc.groupingsystem.entity.UserInfo;
import tm.mc.groupingsystem.mapper.UserInfoMapper;
import tm.mc.groupingsystem.service.UserInfoService;

/**
 * @author sss
 * @className UserInfoServiceImpl
 * @packageName tm.mc.groupingsystem.service.Impl
 * @description TODO
 * @project grouping-system
 * @Version 1.0
 * @since 2023/12/4 14:01
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserInfo getUserInfo(String username) throws CustomException {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        try {
//            return userInfoMapper.selectOne(wrapper);
            UserInfo userInfo = new UserInfo();
            userInfo.setId(1L);
            userInfo.setUsername("123456");
            userInfo.setPassword("$2a$10$sMgCT.iEeHryxE5ctn0yjOmQqhuZp3qOuVC/vmCd00NMdDzbUHvvG");
            return userInfo;
        } catch (Exception e) {
            throw new CustomException(20000, "出现多条记录，请联系管理员");
        }
    }

    @Override
    public UserInfo getUserInfoById(Long id) throws CustomException {
        return userInfoMapper.selectById(id);
    }

    @Override
    public void insertUser(UserInfo userInfo) throws CustomException {
        // 加密密码
        userInfo.setPassword(passwordEncoder.encode(userInfo.getUsername()));
        try {
            userInfoMapper.insert(userInfo);
        } catch (Exception e) {
            throw new CustomException(20000, "当前账号已经注册，请勿重复注册");
        }
    }
}
