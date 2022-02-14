package com.stonedt.intelligence.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * description: Map工具类 <br>
 * date: 2020/4/17 11:19 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
public class MapUtil {


    /**
     * @param [map]
     * @return 转换出的url
     * @description: 将map转换成url <br>
     * @version: 1.0 <br>
     * @date: 2020/4/17 11:20 <br>
     * @author: huajiancheng <br>
     */
    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }


    /**
     * 使用 Map按value进行排序
     * @param map
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (int i = list.size()-1; i > 1; i--) {
            result.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return result;
    }

    /**
     * 使用 Map按value进行倒序
     * @param map
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }
}
