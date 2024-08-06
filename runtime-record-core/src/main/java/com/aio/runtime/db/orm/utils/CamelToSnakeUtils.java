package com.aio.runtime.db.orm.utils;

/**
 * @author lzm
 * @desc 驼峰转桥接工具类
 * @date 2024/08/06
 */
public class CamelToSnakeUtils {
    public static String convertCamelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase; // 处理空字符串或 null
        }
        // 使用正则表达式替换大写字母，并在前面添加下划线
        String result = camelCase.replaceAll("([a-z])([A-Z])", "$1_$2");
        return result.toLowerCase(); // 转换为小写
    }


}
