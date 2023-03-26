package com.moli.config.util;

public class GenerateRandom {
    public static void main(String[] args) {
        System.out.println(name(10));
    }
    /**
     * 生成随机数
     * @param length
     * @return
     */
    public static String number(int length) {
        String[] pswdStr = {"0123456789"};
        int pswdLen = length;
        String pswd = " ";
        char[] chs = new char[pswdLen];
        for (int i = 0; i < pswdStr.length; i++) {

            int idx = (int) (Math.random() * pswdStr[i].length());
            chs[i] = pswdStr[i].charAt(idx);

        }


        for (int i = pswdStr.length; i < pswdLen; i++) {

            int arrIdx = (int) (Math.random() * pswdStr.length);
            int strIdx = (int) (Math.random() * pswdStr[arrIdx].length());

            chs[i] = pswdStr[arrIdx].charAt(strIdx);
        }

        for (int i = 0; i < 1000; i++) {
            int idx1 = (int) (Math.random() * chs.length);
            int idx2 = (int) (Math.random() * chs.length);

            if (idx1 == idx2) {
                continue;
            }

            char tempChar = chs[idx1];
            chs[idx1] = chs[idx2];
            chs[idx2] = tempChar;
        }

        pswd = new String(chs);
        return pswd;
    }

    // 生成复杂码函数
    public static String name(int length) {
        String[] pswdStr = {"qwertyuiopasdfghjklzxcvbnm",
                "QWERTYUIOPASDFGHJKLZXCVBNM", "0123456789"};
        int pswdLen = length;
        String pswd = " ";
        char[] chs = new char[pswdLen];
        for (int i = 0; i < pswdStr.length; i++) {

            int idx = (int) (Math.random() * pswdStr[i].length());
            chs[i] = pswdStr[i].charAt(idx);

        }

        for (int i = pswdStr.length; i < pswdLen; i++) {

            int arrIdx = (int) (Math.random() * pswdStr.length);
            int strIdx = (int) (Math.random() * pswdStr[arrIdx].length());

            chs[i] = pswdStr[arrIdx].charAt(strIdx);
        }

        for (int i = 0; i < 1000; i++) {
            int idx1 = (int) (Math.random() * chs.length);
            int idx2 = (int) (Math.random() * chs.length);

            if (idx1 == idx2) {
                continue;
            }

            char tempChar = chs[idx1];
            chs[idx1] = chs[idx2];
            chs[idx2] = tempChar;
        }

        pswd = new String(chs);
        return pswd;
    }

    // 生成复杂码函数
    public static String name() {
        String[] pswdStr = {"qwertyuiopasdfghjklzxcvbnm",
                "QWERTYUIOPASDFGHJKLZXCVBNM", "0123456789"};
        int pswdLen = 10;
        String pswd = " ";
        char[] chs = new char[pswdLen];
        for (int i = 0; i < pswdStr.length; i++) {

            int idx = (int) (Math.random() * pswdStr[i].length());
            chs[i] = pswdStr[i].charAt(idx);

        }

        for (int i = pswdStr.length; i < pswdLen; i++) {

            int arrIdx = (int) (Math.random() * pswdStr.length);
            int strIdx = (int) (Math.random() * pswdStr[arrIdx].length());

            chs[i] = pswdStr[arrIdx].charAt(strIdx);
        }

        for (int i = 0; i < 1000; i++) {
            int idx1 = (int) (Math.random() * chs.length);
            int idx2 = (int) (Math.random() * chs.length);

            if (idx1 == idx2) {
                continue;
            }

            char tempChar = chs[idx1];
            chs[idx1] = chs[idx2];
            chs[idx2] = tempChar;
        }

        pswd = new String(chs);

        return pswd;
    }
}
