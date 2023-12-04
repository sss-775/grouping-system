package tm.mc.groupingsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import tm.mc.groupingsystem.entity.UserInfo;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
