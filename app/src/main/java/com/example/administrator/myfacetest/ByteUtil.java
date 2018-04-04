package com.example.administrator.myfacetest;

import java.io.ByteArrayOutputStream;

public class ByteUtil {
    /**
     * 将short 转化成字节
     */
    public static byte[] shortToByte(short shortValue) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            b[1 - i] = (byte) (shortValue >> 8 * (1 - i) & 0xFF);
        }
        return b;
    }

    /**
     * 将Byte 转换成 short
     */
    public static short byteToShort(byte[] b) {
        short shortValue = 0;
        for (int i = 0; i < b.length; i++) {
            shortValue += (b[i] & 0xFF) << (8 * (1 - i));
        }
        return shortValue;
    }


    /**
     * 将byte数组转换为16位int
     *
     * @param res byte[]
     * @return int
     */
    public static int byte2int(byte[] res) {
        // res = InversionByte(res);
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets;
        if (res.length == 1) {
            targets = (res[0] & 0xff); // | 表示安位或
        } else {
            targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
        }
        return targets;
    }

    /**
     * 求CKS
     *
     * @param packet
     * @param length
     * @return
     */
    public static int checkSum(byte[] packet, int length) {
        short result = 0;
        for (int i = 0; i < length - 1; i++) {
            int a = (int) packet[i];
            a = a & 0xff;
            result += a;
        }
        return result;
    }

    /**
     * 将字节数组转化成字符
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 十六进制转二进制
     *
     * @param hexString
     * @return
     */
    public static String hex2binary(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    /**
     * 从字节数组到十六进制字符串转换
     */
    public static String toHexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    /**
     * 从十六进制字符串到字节数组转换
     */
    public static byte[] toBytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    /**
     * int转16进制
     *
     * @param integer
     * @return
     */
    public static String intToHex(int integer) {
        StringBuffer buf = new StringBuffer(2);
        if ((integer & 0xff) < 0x10) {
            buf.append("0");
        }
        buf.append(Long.toString(integer & 0xff, 16));
        return buf.toString();
    }

    /**
     * 16进制转10进制
     */
    public static Integer hexToInt(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    /**
     * 16进制转换成字符串
     */
    private static String hexString = "0123456789ABCDEF";
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    /**
     * ascii码转16进制
     */
    public static String convertStringToHex(String str){
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }
        return hex.toString();
    }
}