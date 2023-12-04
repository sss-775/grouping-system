package tm.mc.groupingsystem.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserAuth extends User {

    private Long userId;

    private Integer iRole;

    public UserAuth(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getIRole() {
        return iRole;
    }

    public void setIRole(Integer iRole) {
        this.iRole = iRole;
    }
}
