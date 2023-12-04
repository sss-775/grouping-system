package tm.mc.groupingsystem.service.Impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import tm.mc.groupingsystem.entity.UserAuth;
import tm.mc.groupingsystem.entity.UserInfo;
import tm.mc.groupingsystem.service.UserInfoService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserInfoService userInfoService;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String userName) {
        UserInfo userInfo = userInfoService.getUserInfo(userName);

        String role = userInfo.getRole();

        // 角色集合
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            String[] roles = role.split(",");
            // 角色必须以`ROLE_`开头，数据库中没有，则在这里加
            for (String r : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
            }
        }

        return new UserAuth(
                userInfo.getId(),
                userInfo.getUsername(),
                userInfo.getPassword(),
                authorities
        );
    }
}
