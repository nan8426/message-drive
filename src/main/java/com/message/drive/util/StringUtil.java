package com.message.drive.util;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

/**
 * @Describe 字符串工具类
 * @Author 袁江南
 * @Date 2019/11/8 19:59 message-drive
 **/
public class StringUtil {

    /**
     * 验证码默认长度
     */
    private static final int SMS_CODE_DEFAULT_SIZE = 4;

    private static final char[] CHARACTER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private StringUtil() {

    }

    /**
     * 随机一个GUID
     *
     * @return
     */
    public static String randomGUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    /**
     * 随机验证码
     *
     * @param codeSize 验证码长度
     * @return
     */
    public static String randomSmsCode(Integer codeSize) {
        // 处理长度
        int smsCodeSize = codeSize;
        if (codeSize == null || codeSize < 4) {
            smsCodeSize = SMS_CODE_DEFAULT_SIZE;
        }

        // 随机验证码
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < smsCodeSize; i++) {
            builder.append(CHARACTER[random.nextInt(9)]);
        }

        return builder.toString();
    }

    /**
     * 手机号脱敏
     */

    public static String subStringUserMobile(String userMobile) {
        String newMobile = "";
        if (userMobile == null || userMobile.length() != 11) {
            return newMobile;
        } else {
            newMobile = userMobile.substring(0, 3) + "****" + userMobile.substring(7);
        }
        return newMobile;
    }


    /**
     * 校验身份证号是否合规
     *
     * @param cardNo 身份证号
     * @return
     */
    public static boolean isCardNoLastLetterRight(String cardNo) {
        //身份证前17位的系数
        int[] w = {7, 9, 10, 5, 8, 4, 2, 1, 6,
                3, 7, 9, 10, 5, 8, 4, 2};

        if (cardNo == null) {
            return false;
        }

        char[] c = cardNo.toCharArray();
        if (c.length != 18) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < w.length; i++) {
            sum += (c[i] - '0') * w[i];
        }

        //身份证最后一位可能情况数组分别对应余数0-10
        char[] verifyCode = "10X98765432".toCharArray();
        if (verifyCode.length <= sum % 11) {
            return false;
        }

        char ch = verifyCode[sum % 11];
        return c[17] == ch;
    }

    /**
     * 将app版本号拼接成6位数字的字符串
     *
     * @param appVersion
     * @return
     */
    public static Integer getAppVersionValue(String appVersion) {
        if (appVersion == null || "".equals(appVersion)) {
            return null;
        }
        String[] versionSplit = appVersion.split("\\.");
        if (versionSplit == null || versionSplit.length < 1) {
            return null;
        }

        DecimalFormat decimalFormat = new DecimalFormat("00");
        StringBuilder version = new StringBuilder();
        for (String item : versionSplit) {
            String itemFormat = decimalFormat.format(Long.parseLong(item));
            version.append(itemFormat);
        }

        return Integer.parseInt(version.toString());
    }

}
