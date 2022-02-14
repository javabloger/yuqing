package com.stonedt.intelligence.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 *	数学计算工具类
 */
public class MyMathUtil {
	
	public static void main(String[] args) {
		System.err.println(calculatedRatioNoPercentSign(152, 3832));
	}
	
	/**
	 *	计算占比 a/b 无百分号
	 */
	public static double calculatedRatioNoPercentSign(Integer a, Integer b) {
		double result = 0;
		if (b == 0 || a == 0) {
			return result;
		}else {
			BigDecimal bigDecimala = new BigDecimal(a);
			BigDecimal bigDecimalb = new BigDecimal(b);
		    BigDecimal divide = bigDecimala.divide(bigDecimalb, 4, BigDecimal.ROUND_HALF_UP);
			BigDecimal multiply = divide.multiply(new BigDecimal(100));
		    result = multiply.doubleValue();
		}
	    return result;
	}
	
	/**
	 *	计算占比 a/b 有百分号
	 */
	public static String calculatedRatioWithPercentSign(Integer a, Integer b) {
		String result = "";
		if (b == 0 || a == 0) {
			result = "0.00%";
		}else {
			BigDecimal bigDecimala = new BigDecimal(a);
			BigDecimal bigDecimalb = new BigDecimal(b);
		    BigDecimal divide = bigDecimala.divide(bigDecimalb, 4, BigDecimal.ROUND_HALF_UP);
		    NumberFormat numberFormat = NumberFormat.getPercentInstance();
		    numberFormat.setMaximumFractionDigits(2);
		    result = numberFormat.format(divide.doubleValue());
		}
	    return result;
	}

}
