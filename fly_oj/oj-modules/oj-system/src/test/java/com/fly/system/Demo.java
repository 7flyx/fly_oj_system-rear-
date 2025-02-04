package com.fly.system;

public class Demo {

    // 定义罗马数字和对应的整数值
    private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    public static String intToRoman(int num) {
        StringBuilder roman = new StringBuilder();
        // 遍历VALUES数组
        for (int i = 0; i < VALUES.length; i++) {
            // 如果当前值小于等于num，则减去该值并追加对应的罗马数字
            while (num >= VALUES[i]) {
                num -= VALUES[i];
                roman.append(SYMBOLS[i]);
            }
        }
        return roman.toString();
    }

    public static void main(String[] args) {
        // 测试用例
        int[] testCases = {3, 4, 9, 58, 1994, 2023};
        for (int num : testCases) {
            System.out.println(num + " -> " + intToRoman(num));
        }
    }
}
