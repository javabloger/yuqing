package com.stonedt.intelligence.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.entity.DataMonitorVO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarUtil {

	public static JSONObject resultStringToJSONString(String response, String listKeyword, String searchText, int articlesize) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<DataMonitorVO> list = new ArrayList<DataMonitorVO>();
		JSONObject parseObject = JSON.parseObject(response);
		JSONArray jsonArray = parseObject.getJSONArray("data");
		int size = jsonArray.size();
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject jsonObject2 = jsonObject.getJSONObject("_source");
			DataMonitorVO dataMonitorVO = JSON.toJavaObject(jsonObject2, DataMonitorVO.class);
			JSONObject jsonObject3 = jsonObject.getJSONObject("highlight");
			if (jsonObject3.containsKey("content")) {
				JSONArray jsonArray2 = jsonObject3.getJSONArray("content");
				String content = jsonArray2.getString(0);
				//content = TextUtil.removeFreemarker(content);
				dataMonitorVO.setContent(content);
			}
			if (jsonObject3.containsKey("title")) {
				JSONArray jsonArray2 = jsonObject3.getJSONArray("title");
				String title = jsonArray2.getString(0);
				dataMonitorVO.setTitle(title);
				;
			}
			String title = dataMonitorVO.getTitle();
			if (StringUtils.isNotEmpty(title) && title.contains("_")) {
				dataMonitorVO.setTitle(title.split("_")[0]);
			}
			String text = title + jsonObject2.getString("content");
			String relatedWords = TextUtil.getRelatedWords(listKeyword, searchText, text);
			dataMonitorVO.setRelated_words(relatedWords);
			String key_words = dataMonitorVO.getKey_words();
			String replaceAll = key_words.replaceAll("___________", "").replaceAll("_____", "");
			String keywords = TextUtil.paraseKeywords(replaceAll, 5);
			dataMonitorVO.setKey_words(keywords);
			JSONObject jsonObject4 = jsonObject2.getJSONObject("extend_string_one");
			List<String> imglist = new ArrayList<>();
			String vedioUrl = "";
			String videoorientationurl = "";
			if (jsonObject4 != null) {
				if (jsonObject4.containsKey("vediourl")) {
					vedioUrl = jsonObject4.getString("vediourl");
				}
				if (jsonObject4.containsKey("videoorientationurl")) {
					videoorientationurl = jsonObject4.getString("videoorientationurl");
				}
				if (jsonObject4.containsKey("imglist")) {
					JSONArray jsonArray2 = jsonObject4.getJSONArray("imglist");
					for (int j = 0; j < jsonArray2.size(); j++) {
						imglist.add(jsonArray2.getJSONObject(j).getString("imgurl"));
					}
				}
			}
			dataMonitorVO.setVedioUrl(vedioUrl);
			dataMonitorVO.setVideoorientationurl(videoorientationurl);
			dataMonitorVO.setImglist(imglist);
			list.add(dataMonitorVO);
		}
		if (articlesize > 5000) {
			articlesize = 5000;
		}
		int page = articlesize / 10;
		if (articlesize % 10 != 0) {
			page += 1;
		}
		result.put("data", list);
		result.put("totalCount", articlesize);
		result.put("totalPage", page);
		return new JSONObject(result);
	}
}