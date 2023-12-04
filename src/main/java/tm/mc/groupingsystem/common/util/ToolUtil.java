package tm.mc.groupingsystem.common.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tm.mc.groupingsystem.common.exception.CustomException;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class ToolUtil {

    // 验证属性是否完整 names为非必填项
    public boolean isCompleted(Object object, List<String> names) throws CustomException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object o = null;
            try {
                o = field.get(object);
            } catch (IllegalAccessException e) {
                log.warn("校验属性完整性错误" + e);
            }
            if (o == null && !names.contains(field.getName())) {
                log.info(field.getName() + "不能为空");
                return false;
            }
        }
        return true;
    }

    // 验证属性是否完整 requires为必填项
    public void isRequiredCompleted(Object object, List<String> requires) throws CustomException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object o = null;
            try {
                o = field.get(object);
            } catch (IllegalAccessException e) {
                log.warn("校验属性完整性错误" + e);
            }
            if (o == null && requires.contains(field.getName())) {
                throw new CustomException(20000, "信息需要完善" + field.getName() + "不能为空");
            }
        }
    }

    //生成6位数验证码
    public String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    // 时间校验工具类
    public Boolean isValidTime(String startTime, String endTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse(startTime);
        Date end = sdf.parse(endTime);
        Date cur = new Date();
        log.info("开始时间为：" + start + "结束时间为：" + end + "当前时间为：" + cur);
        return cur.after(start) && cur.before(end);
    }
}
