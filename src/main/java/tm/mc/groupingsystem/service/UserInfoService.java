package tm.mc.groupingsystem.service;

import tm.mc.groupingsystem.common.exception.CustomException;
import tm.mc.groupingsystem.entity.UserInfo;

/**
 * @author sss
 * @className UserInfoService
 * @packageName tm.mc.groupingsystem.service
 * @description TODO
 * @project grouping-system
 * @Version 1.0
 * @since 2023/12/4 14:00
 */
public interface UserInfoService {

    UserInfo getUserInfo(String username) throws CustomException;

    UserInfo getUserInfoById(Long id) throws CustomException;

    void insertUser(UserInfo userInfo) throws CustomException;
}
