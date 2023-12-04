package tm.mc.groupingsystem.common.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "spring.jwt") //与配置文件中的数据关联起来(这个注解会自动匹配jwt开头的配置)
public class JwtProperties {
    /**
     * Request Headers ： Authorization
     */
    @Value("${spring.jwt.header}")
    private String header;

    /**
     * Base64对该令牌进行编码
     */
    @Value("${spring.jwt.base64Secret}")
    private String base64Secret;

    /**
     * 令牌过期时间 此处单位/毫秒
     */
    @Value("${spring.jwt.tokenValidityInSeconds}")
    private Long tokenValidityInSeconds;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBase64Secret() {
        return base64Secret;
    }

    public void setBase64Secret(String base64Secret) {
        this.base64Secret = base64Secret;
    }

    public Long getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    public void setTokenValidityInSeconds(Long tokenValidityInSeconds) {
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    @Override
    public String toString() {
        return "JwtProperties{" +
                "header='" + header + '\'' +
                ", base64Secret='" + base64Secret + '\'' +
                ", tokenValidityInSeconds=" + tokenValidityInSeconds +
                '}';
    }
}
