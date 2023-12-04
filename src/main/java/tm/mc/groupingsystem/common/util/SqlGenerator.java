package tm.mc.groupingsystem.common.util;

/**
 * @ClassName SqlGenator
 * @Description 根据实体类生成建库语句
 * @Date 2021/10/18 16:35
 * @Version 1.0
 **/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlGenerator {

    /**
     * 用来存储Java等属性类型与sql中属性类型的对照
     * </br>
     * 例如：java.lang.Integer 对应 integer
     */
    public static Map<String, String> map = new HashMap<>();

    public static void main(String[] args) {

        map.put("class java.lang.String", "varchar(255)");
        map.put("class java.lang.Integer", "int");
        map.put("int", "int");
        map.put("class java.lang.Long", "bigint");
        map.put("class java.lang.byte[]", "blob");
        map.put("class java.lang.Boolean", "tinyint");
        map.put("boolean", "tinyint(1)");
        map.put("class java.math.BigInteger", "bigint unsigned");
        map.put("class java.lang.Float", "float");
        map.put("class java.lang.Double", "double");
        map.put("class java.sql.Date", "datetime");
        map.put("class java.sql.Time", "time");
        map.put("class java.sql.Timestamp", "datetime");
        map.put("class java.util.Date", "datetime");
        map.put("class java.time.LocalDateTime", "datetime");
        map.put("class java.time.LocalDate", "date");
        map.put("class java.time.LocalTime", "time");
        map.put("class java.lang.Byte", "tinyint");
        map.put("class java.math.BigDecimal", "decimal");

        //实体类所在的package在磁盘上的绝对路径
        String packageName = "src/main/java/club/dev/shb_back/entity/";
        //生成的sql文件名
        String sqlName = "shb";
        //生成sql的文件夹
        String filePath = "src/main/resources/" + sqlName;
        //表命名前缀
        String tablePrefix = "";
        //项目中实体类的路径,（可以不用修改）
//        String prefix = "com.test.model.entity.";
        String prefix = packageName.substring(packageName.indexOf("java") + 5).replace("/", ".");
        //开始构造sql
        sqlConstruction(packageName, filePath, prefix, tablePrefix);
    }

    /**
     * 生成sql建库语句
     * @param tablePrefix 表命名前缀
     * @param packageName 实体类所在的package在磁盘上的绝对路径
     * @param filePath 生成sql的文件夹
     * @param prefix 项目中实体类的路径
     */
    private static void sqlConstruction(String packageName, String filePath, String prefix, String tablePrefix) {

        String className = "";

        StringBuffer sqls = new StringBuffer();
        //获取包下的所有类名称
        List<String> list = getAllClasses(packageName);
        for (String str : list) {
            if (!str.endsWith(".java")) continue;
            className = prefix + str.substring(0, str.lastIndexOf("."));
            String sql = generateSql(className, tablePrefix);
            sqls.append(sql);
        }
        StringToSql(sqls.toString(), filePath + ".sql");
    }

    /**
     * 根据实体类生成建表语句
     *
     * @param className 全类名
     * @param tablePrefix  表命名前缀
     * @return
     */
    public static String generateSql(String className, String tablePrefix) {
        try {
            Class<?> clz = Class.forName(className);
            className = clz.getSimpleName();
            // 表表名adminUser → tb_admin_user
            className = tablePrefix + getStandardFields(className);
            Field[] fields = clz.getDeclaredFields();
            StringBuffer column = new StringBuffer();
            String varchar = " CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,";
            for (Field f : fields) {
                if ("class java.lang.String".equals(f.getType().toString())){
                    column.append(" \n `" + getStandardFields(f.getName()) + "`" + " " + map.get(f.getType().toString())).append(varchar);
                }else {
                    column.append(" \n `" + getStandardFields(f.getName()) + "`" + " " + map.get(f.getType().toString())).append(",");
                }
            }
            //已单独指定id列的生成语句，去掉多余id的拼接
            String column1 = column.substring(column.indexOf(",") + 1);
            StringBuffer sql = new StringBuffer();
            sql.append("\n -- 表 " + className + "\n")
                    .append("\n DROP TABLE IF EXISTS `" + className + "`; ")
                    .append(" \n CREATE TABLE `" + className + "`  (")
                    .append(" \n `id` int(11) NOT NULL AUTO_INCREMENT,")
                    .append(" " + column1)
                    .append(" \n PRIMARY KEY (`id`) USING BTREE,")
                    .append("\n INDEX `id`(`id`) USING BTREE")
                    .append(" \n ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci; \n");
            return sql.toString();
        } catch (ClassNotFoundException e) {
            System.out.println("该类未找到！");
            return null;
        }

    }

    /**
     * 转换为标准等sql字段 例如 adminUser → admin_user
     *
     * @param str 转换为字符串的字段名
     * @return
     */
    public static String getStandardFields(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            //非首字母，在大写字母A到Z之间
            if (i != 0 && (c >= 'A' && c <= 'Z')) {
                sb.append("_");
                c = (char)((int)c+32);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 获取包下面等所有实体类名称，类似于获取 XXX.java
     *
     * @param packageName 全类名
     * @return
     */
    public static List<String> getAllClasses(String packageName) {
        List<String> classList = new ArrayList();
        String className = "";
        File f = new File(packageName);
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            // 遍历实体类下面等所有.java文件 获取其类名
            for (File file : files) {
                className = file.getName();
                classList.add(className);
            }
            return classList;
        } else {
            System.out.println("包路径未找到！");
            return null;
        }
    }

    /**
     * 将生成等String字符串 写进sql文件
     *
     * @param str  String字符串
     * @param path sql文件路径路径
     */
    public static void StringToSql(String str, String path) {
        byte[] sourceByte = str.getBytes();
        FileOutputStream os = null;
        if (null != sourceByte) {
            try {
                //文件路径（路径+文件名）
                File file = new File(path);
                //文件不存在则创建文件，先创建目录
                if (!file.exists()) {
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    file.createNewFile();
                }
                //文件输出流用于将数据写入文件
                os = new FileOutputStream(file);
                os.write(sourceByte);
                os.flush();
                System.out.println("生成成功!!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭文件输出流
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
