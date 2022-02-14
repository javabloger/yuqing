package com.stonedt.intelligence.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;

public class CheckoutUtil {
		 
	    // 与测试账号接口配置信息中的Token要一致
	    private static String token = "999";
	 
	    /**
	     * 验证签名
	     * @param signature
	     * @param timestamp
	     * @param nonce
	     * @return
	     */
	    public static boolean checkSignature(String signature, String timestamp, String nonce) {
	        String[] arr = new String[] { token, timestamp, nonce };
	         Arrays.sort(arr);// 将token、timestamp、nonce三个参数进行字典序排序
	        StringBuilder content = new StringBuilder();
	        for (int i = 0; i < arr.length; i++) {
	            content.append(arr[i]);
	        }
	        MessageDigest md = null;
	        String tmpStr = null;
	 
	        try {
	            md = MessageDigest.getInstance("SHA-1");
	            // 将三个参数字符串拼接成一个字符串进行sha1加密
	            byte[] digest = md.digest(content.toString().getBytes());
	            tmpStr = byteToHex(digest );
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
	        return tmpStr != null ? tmpStr.equals(signature) : false;
	    }
	 
	    //十六进制字节数组转为字符串
	    private static String byteToHex(final byte[] hash) {
	        Formatter formatter = new Formatter();
	        for (byte b : hash) {
	            formatter.format("%02x", b);
	        }
	        String result = formatter.toString();
	        formatter.close();
	        return result;
	    }


}
