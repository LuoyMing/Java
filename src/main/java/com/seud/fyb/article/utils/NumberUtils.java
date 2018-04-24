package com.seud.fyb.article.utils;

import java.math.BigDecimal;

public class NumberUtils {

    /**
     * 相加
     *
     * @param num1
     * @param num2
     * @return double
     * @throws
     * @Title: add
     */
    public static double add(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1 == null ? "0" : num1);
        BigDecimal b2 = new BigDecimal(num2 == null ? "0" : num2);
        return b1.add(b2).doubleValue();
    }
    
    /**
     * 相减
     *
     * @param num1
     * @param num2
     * @return double
     * @throws
     * @Title: add
     */
    public static double subtract(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1 == null ? "0" : num1);
        BigDecimal b2 = new BigDecimal(num2 == null ? "0" : num2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 相乘
     *
     * @param num1
     * @param num2
     * @return double
     * @throws
     * @Title: mult
     */
    public static double multiply(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 大数据相除，精度默认为2
     *
     * @param num1
     * @param num2
     * @return
     */
    public static double divide(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.divide(b2, 2).doubleValue();
    }

    /**
     * 相除
     *
     * @param num1
     * @param num2
     * @param scale 精度
     * @return
     */
    public static double divide(String num1, String num2, int scale) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
