package com.hjy.resourcesparse.util;

public class ByteUtil {

    private final static String HEX = "0123456789ABCDEF";

    /**
     * 4字节转为int
     *
     * @param bytes
     * @return
     */
    public static int byte2int(byte[] bytes) {
        return (bytes[0] & 0xff) | (bytes[1] << 8 & 0xff00) | (bytes[2] << 16 & 0xff0000) | (bytes[3] << 24 & 0xff000000);
    }

    public static short byte2short(byte[] bytes) {
        short s0 = (short) (bytes[0] & 0xff);
        short s1 = (short) (bytes[1] & 0xff);
        return (short) (s0 | (s1 << 8));
    }

    public static byte[] copyByte(byte[] src, int start, int len){
        if(src == null){
            return null;
        }
        if(start > src.length){
            return null;
        }
        if((start+len) > src.length){
            return null;
        }
        if(start<0){
            return null;
        }
        if(len<=0){
            return null;
        }
        byte[] resultByte = new byte[len];
        for(int i=0;i<len;i++){
            resultByte[i] = src[i+start];
        }
        return resultByte;
    }

    public static String toHex(int t) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (t >> 24 & 0xff);
        bytes[1] = (byte) (t >> 16 & 0xff);
        bytes[2] = (byte) (t >> 8 & 0xff);
        bytes[3] = (byte) (t & 0xff);
        return toHex(bytes);
    }

    public static String toHex(byte b) {
        byte[] bytes = new byte[1];
        bytes[0] = b;
        return toHex(bytes);
    }

    public static String toHex(short s) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (s >> 8 & 0xff);
        bytes[1] = (byte) (s & 0xff);
        return toHex(bytes);
    }

    public static String toHex(String txt) {
        if(txt == null)
            return null;
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        if(hex == null)
            return null;
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        if(hexString == null)
            return null;
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return null;
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }


}
