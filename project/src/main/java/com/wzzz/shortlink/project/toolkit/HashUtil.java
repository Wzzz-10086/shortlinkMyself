package com.wzzz.shortlink.project.toolkit;

import cn.hutool.core.lang.hash.MurmurHash;

/**
 * HASH 工具类
 * 提供将字符串转换为Base62编码的功能
 */
public class HashUtil {

    // Base62编码使用的字符集，包含数字0-9，大写字母A-Z，小写字母a-z
    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    // 字符集的大小，即62
    private static final int SIZE = CHARS.length;

    /**
     * 将十进制数转换为Base62字符串
     * @param num 要转换的十进制数
     * @return Base62编码的字符串
     */
    private static String convertDecToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            // 取余数作为字符索引
            int i = (int) (num % SIZE);
            // 将对应字符添加到字符串中
            sb.append(CHARS[i]);
            // 继续处理商
            num /= SIZE;
        }
        // 反转字符串得到最终结果
        return sb.reverse().toString();
    }

    /**
     * 将字符串哈希并转换为Base62编码
     * @param str 要处理的字符串
     * @return 哈希后的Base62编码字符串
     */
    public static String hashToBase62(String str) {
        // 使用MurmurHash算法计算32位哈希值
        int i = MurmurHash.hash32(str);
        // 如果哈希值为负数，则将其转换为正数,使用Integer.MAX_VALUE减去负值
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;
        // 将数字转换为Base62编码
        return convertDecToBase62(num);
    }
}
