package com.stonedt.intelligence.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 *	方案关键词处理
 * @date  2020年4月30日 上午10:10:12
 */
public class ProjectWordUtil {
	
//	public static void main(String[] args) {
//		Set<String> commononprojectRelatedWord = CommononprojectRelatedWord("我爱中华人民任正非共和国,北京","有限公司|集团|集团公司|集团有限|李克强|习近平|王岐山|南京|北京|上海|宿迁|我爱中国|南京|宿迁|无锡|徐州|南京",null,null,null);
//		System.out.println(commononprojectRelatedWord);
//		
////		String commononprojectKeyWord = CommononprojectKeyWord("(任正非|马云|刘强东|马化腾|史玉柱)+北京");
////		System.out.println("commononprojectKeyWord:"+commononprojectKeyWord);
//		
//		
//	}
	
	/**
	 * 	提取普通方案文章的涉及词
	 */
	public static Set<String> CommononprojectRelatedWord(String text, String subject, String regional, String character, 
			String event) {
		
		Set<String> result = new HashSet<>();
		if (StringUtils.isBlank(text) || StringUtils.isBlank(subject)) {
			return result;
		}
		StringBuffer stringBuffer = new StringBuffer();
		subject = subject.replaceAll("\\(", "").replaceAll("\\)", "");
        String regex = "[\\s\\|+]+";
        String[] arr = subject.split(regex);
        for(int i = 0; i < arr.length; i++){
        	stringBuffer.append(",").append(arr[i]);
        }
		String[] split = stringBuffer.toString().split(",");
		for (int i = 0; i < split.length; i++) {
			if(!"".equals(split[i])) {
			if (result.size() > 4) {
				break;
			}
			Pattern compile = Pattern.compile(split[i], Pattern.CASE_INSENSITIVE);
			Matcher matcher = compile.matcher(text);
			if (matcher.find()) {
				result.add(split[i]);
			}
		}
		}
		return result;
	}
	
	
	
	
	/**
	 * 	提取方案文章的涉及词
	 */
	public static Set<String> projectRelatedWord(String text, String subject, String regional, String character, 
			String event) {
		
		Set<String> result = new HashSet<>();
		if (StringUtils.isBlank(text) || StringUtils.isBlank(subject)) {
			return result;
		}
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(subject);
		if (StringUtils.isNotBlank(regional) && !"null".equals(regional)) {
			stringBuffer.append(",").append(regional);
		}
		if (StringUtils.isNotBlank(character) && !"null".equals(character)) {
			stringBuffer.append(",").append(character);
		}
		if (StringUtils.isNotBlank(event) && !"null".equals(event)) {
			stringBuffer.append(",").append(event);
		}
		String[] split = stringBuffer.toString().split(",");
		for (int i = 0; i < split.length; i++) {
			if (result.size() > 4) {
				break;
			}
//			if (text.contains(split[i])) {
//				result.add(split[i]);
//			}
			Pattern compile = Pattern.compile(split[i], Pattern.CASE_INSENSITIVE);
			Matcher matcher = compile.matcher(text);
			if (matcher.find()) {
				result.add(split[i]);
			}
		}
		return result;
	}
	
	/**
	 * 	实现高亮
	 */
	public static String highlightcontent(String text, String subject, String regional, String character, 
			String event) {
		
		Set<String> result = new HashSet<>();
		if (StringUtils.isBlank(text) || StringUtils.isBlank(subject)) {
		}
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(subject);
		if (StringUtils.isNotBlank(regional) && !"null".equals(regional)) {
			stringBuffer.append(",").append(regional);
		}
		if (StringUtils.isNotBlank(character) && !"null".equals(character)) {
			stringBuffer.append(",").append(character);
		}
		if (StringUtils.isNotBlank(event) && !"null".equals(event)) {
			stringBuffer.append(",").append(event);
		}
		String[] split = stringBuffer.toString().split(",");
		for (int i = 0; i < split.length; i++) {
			if (result.size() > 4) {
				break;
			}
//			if (text.contains(split[i])) {
//				result.add(split[i]);
//			}
			Pattern compile = Pattern.compile(split[i], Pattern.CASE_INSENSITIVE);
			Matcher matcher = compile.matcher(text);
			if (matcher.find()) {
				result.add(split[i]);
			}
		}
		
		
		Set<String> set = new HashSet<String>();  
		Iterator<String> it = set.iterator();  
		while (it.hasNext()) {  
		  String str = it.next();  
		  text = text.replaceAll(str, "<b class=\"key\" style=\"color:red\">"+str+"</b>");
		}  
		
		
		
		
		return text;
	}
	
	
	
	
	
	
	
	/**
	 * 	拼接高级方案的关键词
	 */
	public static String highProjectKeyword(String subject, String regional, String character, String event) {
		StringBuffer keyword = new StringBuffer();
		if(StringUtils.isBlank(subject)) {
			return keyword.toString();
		}
		keyword.append("(");
		keyword.append("(").append(subject.replaceAll(",", " OR ")).append(")");
		if (StringUtils.isNotBlank(regional) && !"null".equals(regional)) {
			keyword.append(" AND ").append("(").append(regional.replaceAll(",", " OR ")).append(")");
		}
		if (StringUtils.isNotBlank(character) && !"null".equals(character)) {
			keyword.append(" AND ").append("(").append(character.replaceAll(",", " OR ")).append(")");
		}
		if (StringUtils.isNotBlank(event) && !"null".equals(event)) {
			keyword.append(" AND ").append("(").append(event.replaceAll(",", " OR ")).append(")");
		}
		keyword.append(")");
		return keyword.toString();
	}
	
	/**
	 * 	拼接高级方案的屏蔽词
	 */
	public static String highProjectStopword(String stopword) {
		StringBuffer keyword = new StringBuffer();
		if(StringUtils.isBlank(stopword) || "null".equals(stopword)) {
			return keyword.toString();
		}
		keyword.append("(").append(stopword.replaceAll(",", " OR ")).append(")");
		return keyword.toString();
	}

	public static String QuickProjectKeyword(String subject_word) {
		StringBuffer keyword = new StringBuffer();
		keyword.append("(").append(subject_word.replaceAll("\\|", " OR ").replaceAll("\\+"," AND ")).append(")");
		return keyword.toString();
	}

	/**
	 * 普通方案提取关键词
	 * @param string
	 * @return
	 */
	public static String CommononprojectKeyWord(String subject) {
		
		StringBuffer stringBuffer = new StringBuffer();
		subject = subject.replaceAll("\\(", "").replaceAll("\\)", "");
        String regex = "[\\s\\|+]+";
        String[] arr = subject.split(regex);
        for(int i = 0; i < arr.length; i++){
        	stringBuffer.append(",").append(arr[i]);
        }
        String string = stringBuffer.toString();
        if(string.startsWith(",")) {
        	string= string.substring(1, string.length());
        }
		return string;
	}
	public static void main(String[] args) {
		String commononprojectKeyWord = CommononprojectKeyWord("(((李克强|习近平)+南京))");
		System.out.println(commononprojectKeyWord);
	}

}
