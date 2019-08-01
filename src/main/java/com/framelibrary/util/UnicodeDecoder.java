package com.framelibrary.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.framelibrary.util.StringUtils.isEmpty;

/**
 * 字符集编码工具类
 *
 * @author wangwx
 */

public class UnicodeDecoder {

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 把中文转成Unicode码
     *
     * @param str
     * @return
     */
    public static String chineseToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            if (chr1 >= 19968 && chr1 <= 171941) {//汉字范围 \u4e00-\u9fa5 (中文)
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        unicodeToString(chineseToUnicode(strName));
        return true;

    }

    /**
     * encoded in utf-8
     * <p>
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException if an error occurs
     */
    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * encoded in utf-8, if exception, return defultReturn
     *
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }

    /**
     * 将utf-8的汉字转换成unicode格式汉字码
     *
     * @param string
     * @return
     */
    public static String stringToUnicode(String string) {

        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        String str = unicode.toString();

        return str.replaceAll("\\\\", "0x");
    }

    /**
     * 将unicode的汉字码转换成utf-8格式的汉字
     *
     * @param unicode
     * @return
     */
    public static String unicodeToString(String unicode) {

        String str = unicode.replace("0x", "\\");

        StringBuffer string = new StringBuffer();
        String[] hex = str.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return StringUtils.isEmpty(string.toString()) ? str : string.toString();
    }


}
