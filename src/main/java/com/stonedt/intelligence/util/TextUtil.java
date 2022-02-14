package com.stonedt.intelligence.util;


import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

//import java.util.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 处理字符串
 */
public class TextUtil {

    public static void main(String[] args) {
        System.err.println(removeParenthesesAndContents("世界网(aa-nn)"));
    }

    /**
     * 去除括号及括号里的内容
     */
    public static String removeParenthesesAndContents(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String patternBracket = "\\(.*?\\)";
        Pattern pattern = Pattern.compile(patternBracket);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            text = matcher.replaceAll("");
        }
        return text;
    }

    /**
     * 处理空值返回
     */
    public static String nullAsStringEmpty(String arg) {
        if (StringUtils.isBlank(arg)) {
            return "";
        } else {
            return arg;
        }
    }

    // 去除 <#..> ${...} 类似标签
    public static String removeFreemarker(String text) {
        String regex1 = "<[^>]*>";
        String regex2 = "\\$\\{.*?\\}.";
        String regex3 = "\\$\\{.*?\\}";
        String str = "";
        if (StringUtils.isNotEmpty(text)) {
            str = text.replaceAll(regex1, "").replaceAll(regex2, "").replaceAll(regex3, "");
        }
        return str;
    }

    //调用jieba之前去除不要的字符
    public static String removeSpecialCharacters(String content) {
        String regEx = "[`~!@#$%^&*()+=|{}':;'\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        content = m.replaceAll("").trim();
        return content;
    }

    //是否包含数字或字母
    public static boolean validNumbersAndLetters(String keyword) {
        Pattern p = Pattern.compile("[a-zA-Z0-9]");
        Matcher m = p.matcher(keyword);
        if (m.find()) {
            return true;
        }
        return false;
    }

    //数据源
    public static String dataSourceClassification(String key) {
        String result = "";
        switch (key) {
            case "1":
                result = "微信";
                break;
            case "2":
                result = "微博";
                break;
            case "3":
                result = "政务";
                break;
            case "4":
                result = "论坛";
                break;
            case "5":
                result = "新闻";
                break;
            case "6":
                result = "报刊";
                break;
            case "7":
                result = "客户端";
                break;
            case "8":
                result = "网站";
                break;
            case "9":
                result = "外媒";
                break;
            case "10":
                result = "视频";
                break;
            case "11":
                result = "博客";
                break;
            default:
                break;
        }
        return result;
    }

    //数据源
    public static String dataSourceClassificationEng(String key) {
        String result = "";
        switch (key) {
            case "1":
                result = "wechat";
                break;
            case "2":
                result = "weibo";
                break;
            case "3":
                result = "gov";
                break;
            case "4":
                result = "bbs";
                break;
            case "5":
                result = "news";
                break;
            case "6":
                result = "newspaper";
                break;
            case "7":
                result = "app";
                break;
            case "8":
                result = "web";
                break;
            case "9":
                result = "foreign_media";
                break;
            case "10":
                result = "video";
                break;
            case "11":
                result = "blog";
                break;
            default:
                break;
        }
        return result;
    }


    //处理文章的关键词，展示num个关键词
    @SuppressWarnings("unchecked")
    public static String paraseKeywords(String key_words, Integer num) {
        String key_word = "";
        if (key_words != null && key_words.contains(":")) {
            Map<String, Integer> parseMap = JSON.parseObject(key_words, Map.class);
            Map<String, Integer> sortDescend = SortMap.sortDescend(parseMap);
            int a = 1;
            for (String key : sortDescend.keySet()) {
                if (a > num) {
                    break;
                }
                key_word += key + ",";
                a++;
            }
        }
        if (key_word != "") {
            key_word = key_word.substring(0, key_word.length() - 1);
        }
        return key_word;
    }

//	//获取涉及词
//	public static String getRelatedWords(String keyword, String searchText, String content) {
//		if(StringUtils.isEmpty(keyword)) {
//			return "";
//		}
//		String[] split = keyword.split(",");
//		String result = "";
//		int a = 1;
//		for (int i = 0; i < split.length; i++) {
//			if (a > 4) {
//				break;
//			}
//			String reg = "";
//			for (int j = 0; j < split[i].length(); j++) {
//				char charAt = split[i].charAt(j);
//				reg += "<b class='key' style='color:red'>"+charAt+"</b>";
//			}
//			Pattern pt = Pattern.compile(split[i]);
//			Pattern pt2 = Pattern.compile(reg);
//			Matcher m1 = pt.matcher(content);
//			Matcher m2 = pt2.matcher(content);
//			boolean find1 = m1.find();
//			boolean find2 = m2.find();
//			if (find1 || find2) {
//				 result += split[i] + ",";
//				 a++;
//			}
//		}
//		if (result != "") {
//			result = result.substring(0, result.length()-1);
//		}
//		StringBuilder stringBuilder = new StringBuilder();
//		if (StringUtils.isNotEmpty(searchText) && content.contains(searchText)) {
//			if (result != "") {
//				stringBuilder.append(searchText).append(",").append(result);
//			}else {
//				stringBuilder.append(searchText);
//			}
//		}else {
//			stringBuilder.append(result);
//		}
//		return stringBuilder.toString();
//	}
//	
//	//获取涉及词
//	public static String getRelatedWordsFull(String keyword, String content) {
//		String[] split = keyword.split(",");
//		String result = "";
//		for (int i = 0; i < split.length; i++) {
//			String reg = "";
//			for (int j = 0; j < split[i].length(); j++) {
//				char charAt = split[i].charAt(j);
//				reg += "<b class='key' style='color:red'>"+charAt+"</b>";
//			}
//			Pattern pt = Pattern.compile(reg);
//			Matcher m = pt.matcher(content);
//			boolean find = m.find();
//			if (find) {
//				 result += split[i] + ",";
//			}
//		}
//		if (result != "") {
//			result = result.substring(0, result.length()-1);
//		}
//		return result;
//	}

    //获取涉及词-数据监测
    public static String getRelatedWords(String keyword, String searchText, String content) {
        if (StringUtils.isEmpty(keyword) || StringUtils.isEmpty(content)) {
            return "";
        }
        content = content.replaceAll("<b class='key' style='color:red'>", "").replaceAll("</b>", "");
        String[] split = keyword.split(",");
        String result = "";
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("_")) {
                String[] split2 = split[i].split("_");
                int a = 0;
                for (int j = 0; j < split2.length; j++) {
                    if (content.contains(split2[j])) {
                        a++;
                    }
                }
                if (a == split2.length) {
                    if (!result.contains(split[i])) {
                        result += split[i] + ",";
                    }
                }
            } else {
                if (content.contains(split[i])) {
                    if (!result.contains(split[i])) {
                        result += split[i] + ",";
                    }
                }
            }
        }
//		String[] split = keyword.split(",");
//		String result = "";
//		int a = 1;
//		for (int i = 0; i < split.length; i++) {
//			if (a > 4) {
//				break;
//			}
//			String reg = "";
//			for (int j = 0; j < split[i].length(); j++) {
//				char charAt = split[i].charAt(j);
//				reg += "<b class='key' style='color:red'>"+charAt+"</b>";
//			}
//			Pattern pt = Pattern.compile(split[i]);
//			Pattern pt2 = Pattern.compile(reg);
//			Matcher m1 = pt.matcher(content);
//			Matcher m2 = pt2.matcher(content);
//			boolean find1 = m1.find();
//			boolean find2 = m2.find();
//			if (find1 || find2) {
//				 result += split[i] + ",";
//				 a++;
//			}
//		}
        if (result != "") {
            result = result.substring(0, result.length() - 1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(searchText) && content.contains(searchText)) {
            stringBuilder.append(result);
            if (!result.contains(searchText)) {
                if (result != "") {
                    stringBuilder.append(",").append(searchText);
                } else {
                    stringBuilder.append(searchText);
                }
            }
        } else {
            stringBuilder.append(result);
        }
        return stringBuilder.toString();
    }

    //获取涉及词-全文搜索
    public static String getRelatedWordsFull(String keyword, String content) {
        String[] split = keyword.split(",");
        String result = "";
        for (int i = 0; i < split.length; i++) {
            String reg = "";
            for (int j = 0; j < split[i].length(); j++) {
                char charAt = split[i].charAt(j);
                reg += "<b class='key' style='color:red'>" + charAt + "</b>";
            }
            Pattern pt = Pattern.compile(reg);
            Matcher m = pt.matcher(content);
            boolean find = m.find();
            if (find) {
                result += split[i] + ",";
            }
        }
        if (result != "") {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    //请求es返回的字符串
    public static Map<String, Object> tool(String result) {//result：调es返回的数据
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> hashMap = new HashMap<String, Object>();
        Map<String, Object> loc = new HashMap<String, Object>();
        Map<String, Object> org = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(result)) {
            JSONObject parseObjects = JSONObject.parseObject(result);
            JSONArray jsonArray = parseObjects.getJSONObject("hits").getJSONArray("hits");
            for (int z = 0; z < jsonArray.size(); z++) {
                JSONObject parseObject = jsonArray.getJSONObject(z).getJSONObject("_source");
                if (parseObject.containsKey("ner")) {
                    JSONObject jsonObject = JSONObject.parseObject(parseObject.getString("ner"));
                    if (StringUtils.isEmpty(parseObject.getString("ner"))) {
                        continue;
                    }
                    Map<String, Object> map = JSONObject.parseObject(jsonObject.getString("per"), Map.class);
                    for (String key : map.keySet()) {
                        if (key == null || "".equals(key) || " ".equals(key)) {
                            continue;
                        }
                        if (hashMap.containsKey(key)) {
                            Integer count = (Integer) hashMap.get(key);
                            count += (Integer) map.get(key);
                            hashMap.put(key, count);
                        } else {
                            hashMap.put(key, map.get(key));
                        }
                    }
                } else {
//					System.err.println("ner为空!");
                }

                if (parseObject.containsKey("ner")) {
                    JSONObject jsonObject = JSONObject.parseObject(parseObject.getString("ner"));
                    if (StringUtils.isEmpty(parseObject.getString("ner"))) {
                        continue;
                    }
                    Map<String, Object> map = JSONObject.parseObject(jsonObject.getString("loc"), Map.class);
                    for (String key : map.keySet()) {
                        if (key == null || "".equals(key) || " ".equals(key)) {
                            continue;
                        }
                        if (loc.containsKey(key)) {
                            Integer count = (Integer) loc.get(key);
                            count += (Integer) map.get(key);
                            loc.put(key, count);
                        } else {
                            loc.put(key, map.get(key));
                        }
                    }
                } else {
//					System.err.println("loc为空!");
                }

                if (parseObject.containsKey("ner")) {
                    JSONObject jsonObject = JSONObject.parseObject(parseObject.getString("ner"));
                    if (StringUtils.isEmpty(parseObject.getString("ner"))) {
                        continue;
                    }
                    Map<String, Object> map = JSONObject.parseObject(jsonObject.getString("org"), Map.class);
                    for (String key : map.keySet()) {
                        if (key == null || "".equals(key) || " ".equals(key)) {
                            continue;
                        }
                        if (org.containsKey(key)) {
                            Integer count = (Integer) org.get(key);
                            count += (Integer) map.get(key);
                            org.put(key, count);
                        } else {
                            org.put(key, map.get(key));
                        }
                    }
                } else {
//					System.err.println("org为空!");
                }

            }
        } else {
            System.err.println("数据为空!");
        }
        resultMap.put("per", hashMap);
        resultMap.put("loc", loc);
        resultMap.put("org", org);
        return resultMap;
    }

    //将两个map放入方法
    public static String RingRatioCycletool(Map<String, Object> map1, Map<String, Object> map2) {//现在的map1   环比的map2
        Map<String, Integer> per = (Map<String, Integer>) map1.get("per");
        Map<String, Integer> loc = (Map<String, Integer>) map1.get("loc");
        Map<String, Integer> org = (Map<String, Integer>) map1.get("org");

        Map<String, Integer> per2 = (Map<String, Integer>) map2.get("per");
        Map<String, Integer> loc2 = (Map<String, Integer>) map2.get("loc");
        Map<String, Integer> org2 = (Map<String, Integer>) map2.get("org");


        List<Map<String, Object>> perList = new ArrayList<>();

        for (Entry<String, Integer> entrySet : per.entrySet()) {
            try {
                Map<String, Object> perMap = new HashMap<>();
                perMap.put("keyword", entrySet.getKey());
                perMap.put("count", entrySet.getValue());
                perList.add(perMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        perList = perList.stream()
                .sorted((mapa, mapb) -> (int) mapb.get("count") - (int) mapa.get("count")).limit(10)
                .collect(Collectors.toList());
        int total = perList.stream().mapToInt(a -> (int) a.get("count")).sum();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < perList.size(); i++) {
            try {
                Map<String, Object> hashMap = perList.get(i);
                Integer index = 0;//指数
                Integer trend = 3;//趋势
                Integer count1 = (Integer) hashMap.get("count");
                String keyword = String.valueOf(hashMap.get("keyword"));
                Integer count2 = (Integer) per2.get(keyword);
                if (per2.containsKey(keyword)) {
                    if (count1 > count2) {
                        trend = 3;
                    } else if (count1 < count2) {
                        trend = 1;
                    } else {
                        trend = 2;
                    }
                }
                hashMap.put("trend", trend);
                hashMap.put("index", MyMathUtil.calculatedRatioWithPercentSign(count1, total));
                list.add(hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // 地点计算指数
        List<Map<String, Object>> locList = new ArrayList<>();

        for (Entry<String, Integer> entrySet : loc.entrySet()) {
            try {
                Map<String, Object> locMap = new HashMap<>();
                locMap.put("keyword", entrySet.getKey());
                locMap.put("count", entrySet.getValue());
                locList.add(locMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        locList = locList.stream()
                .sorted((mapa, mapb) -> (int) mapb.get("count") - (int) mapa.get("count")).limit(10)
                .collect(Collectors.toList());

        int loctotal = locList.stream().mapToInt(a -> (int) a.get("count")).sum();

        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < locList.size(); i++) {
            try {
                Map<String, Object> hashMap = locList.get(i);
                Integer trend = 3;//趋势
                Integer count1 = (Integer) hashMap.get("count");
                String keyword = String.valueOf(hashMap.get("keyword"));
                Integer count2 = (Integer) loc2.get(keyword);
                if (loc2.containsKey(keyword)) {
                    if (count1 > count2) {
                        trend = 3;
                    } else if (count1 < count2) {
                        trend = 1;
                    } else {
                        trend = 2;
                    }
                }
                hashMap.put("trend", trend);
                hashMap.put("index", MyMathUtil.calculatedRatioWithPercentSign(count1, loctotal));
                list2.add(hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        List<Map<String, Object>> orgList = new ArrayList<>();

        for (Entry<String, Integer> entrySet : org.entrySet()) {
            try {
                Map<String, Object> orgMap = new HashMap<>();
                orgMap.put("keyword", entrySet.getKey());
                orgMap.put("count", entrySet.getValue());
                orgList.add(orgMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        orgList = orgList.stream()
                .sorted((mapa, mapb) -> (int) mapb.get("count") - (int) mapa.get("count")).limit(10)
                .collect(Collectors.toList());

        int orgtotal = orgList.stream().mapToInt(a -> (int) a.get("count")).sum();

        List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < orgList.size(); i++) {
            try {
                Map<String, Object> hashMap = orgList.get(i);
                Integer trend = 3;//趋势
                Integer count1 = (Integer) hashMap.get("count");
                String keyword = String.valueOf(hashMap.get("keyword"));
                Integer count2 = (Integer) org2.get(keyword);
                if (org2.containsKey(keyword)) {
                    if (count1 > count2) {
                        trend = 3;
                    } else if (count1 < count2) {
                        trend = 1;
                    } else {
                        trend = 2;
                    }
                }
                hashMap.put("trend", trend);
                hashMap.put("index", MyMathUtil.calculatedRatioWithPercentSign(count1, orgtotal));
                list3.add(hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        for (String key : per.keySet()) {
//            Map<String, Object> hashMap = new HashMap<String, Object>();
//            Integer index = (Integer) per.get(key);//指数
//            Integer trend = 3;//趋势1上升。2相等，3下降
//
//            if (per2.containsKey(key)) {
//                Integer count1 = (Integer) per.get(key);
//                Integer count2 = (Integer) per2.get(key);
//                index = count1 - count2;
//                if (count1 > count2) {
//                    trend = 3;
//                } else if (count1 < count2) {
//                    trend = 1;
//                } else {
//                    trend = 2;
//                }
//            } else {
//                index = (Integer) per.get(key);
//            }
//            Integer count = (Integer) per.get(key);//数量
//            hashMap.put("keyword", key);
//            hashMap.put("count", count);
//            hashMap.put("trend", trend);
//            hashMap.put("index", index);
//            list.add(hashMap);
//        }
//
//        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
//        for (String key : loc.keySet()) {
//            Map<String, Object> hashMap = new HashMap<String, Object>();
//            Integer index = 0;//指数
//            Integer trend = 3;//趋势
//            if (loc2.containsKey(key)) {
//                Integer count1 = (Integer) loc.get(key);
//                Integer count2 = (Integer) loc2.get(key);
//                index = count1 - count2;
//                if (count1 > count2) {
//                    trend = 3;
//                } else if (count1 < count2) {
//                    trend = 1;
//                } else {
//                    trend = 2;
//                }
//            } else {
//                index = (Integer) loc.get(key);
//            }
//            Integer count = (Integer) loc.get(key);//数量
//            hashMap.put("keyword", key);
//            hashMap.put("count", count);
//            hashMap.put("trend", trend);
//            hashMap.put("index", index);
//            list2.add(hashMap);
//        }
//
//        List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
//        for (String key : org.keySet()) {
//            Map<String, Object> hashMap = new HashMap<String, Object>();
//            Integer index = 0;//指数
//            Integer trend = 3;//趋势
//            if (org2.containsKey(key)) {
//                Integer count1 = (Integer) org.get(key);
//                Integer count2 = (Integer) org2.get(key);
//                index = count1 - count2;
//                if (count1 > count2) {
//                    trend = 3;
//                } else if (count1 < count2) {
//                    trend = 1;
//                } else {
//                    trend = 2;
//                }
//            } else {
//                index = (Integer) org.get(key);
//            }
//            Integer count = (Integer) org.get(key);//数量
//            hashMap.put("keyword", key);
//            hashMap.put("count", count);
//            hashMap.put("trend", trend);
//            hashMap.put("index", index);
//            list3.add(hashMap);
//        }
//
//        Collections.sort(list, new Comparator<Map<String, Object>>() {
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                return (int) o2.get("index") - (int) o1.get("index");
//            }
//        });
//
//        Collections.sort(list2, new Comparator<Map<String, Object>>() {
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                return (int) o2.get("index") - (int) o1.get("index");
//            }
//        });
//
//        Collections.sort(list3, new Comparator<Map<String, Object>>() {
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                return (int) o2.get("index") - (int) o1.get("index");
//            }
//        });
//
        JSONObject map = new JSONObject();
        map.put("per", list);
        map.put("loc", list2);
        map.put("org", list3);
        return map.toJSONString();
    }


    /**
     * 去除html标签
     *
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }
    
    
    /**
     * 将英文双引号 /" 转换成中文的
     * @param content
     * @return 
     */
	public static String processQuotationMarks(String content){
		String regex = "\\\\\\\"([^\"]*)\\\\\\\"";
    	Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        
        String reCT=content;
 
        while(matcher.find()){
            String itemMatch = "“" + matcher.group(1) + "”";
            reCT=reCT.replace("\\\""+matcher.group(1)+"\\\"", itemMatch);
        }
        return reCT.replaceAll("\\\\\\\"", "“");
    }

}
