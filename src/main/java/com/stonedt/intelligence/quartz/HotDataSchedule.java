
package com.stonedt.intelligence.quartz;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.SearchConstant;
import com.stonedt.intelligence.dao.IFullSearchDao;
import com.stonedt.intelligence.entity.FullType;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.MD5Util;
import com.stonedt.intelligence.util.MyHttpRequestUtil;
import com.stonedt.intelligence.util.RedisUtil;
import com.stonedt.intelligence.vo.FullSearchParam;

/**
* <p>热门数据缓存</p>
* <p>Title: HotDataSchedule</p>  
* <p>Description: </p>  
* @author Mapeng 
* @date Jul 15, 2020  
*/
@Component
public class HotDataSchedule {

	@Value("${es.search.url}")
	private String es_search_url;
	// es热点地址
	@Value("${es.hot.search.url}")
	private String es_hot_search_url;
	@Value("${es.hot.open}")
	private Integer schedule_hot_open;
	
	
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private IFullSearchDao fullSearchDao;
	
	@Scheduled(cron = "0 12/30 * * * ?")
	public void name() {
		if (schedule_hot_open == 1) {
		for (int j = 1; j < 11; j++) {
			System.err.println("开始更新热门数据全部栏目到redis");
			List<FullType> fullSecond = fullSearchDao.listFullTypeBysecond(8);
			for (int i = 0; i < fullSecond.size(); i++) {
				FullType fullType = fullSecond.get(i);
				Integer pageNum = j;
				FullSearchParam searchParam = new FullSearchParam();
				searchParam.setClassify(fullType.getValue());
				searchParam.setSource_name("");
				searchParam.setPageNum(pageNum);
				searchParam.setPageSize(25);
				searchParam.setTimeType(2);
				searchParam.setSearchWord("");
				setRedis(searchParam);
			}
		}
		System.err.println("更新热门数据全部栏目到redis成功");
		for (int j = 1; j < 11; j++) {
			System.err.println("开始更新热门具体来源数据到redis");
			List<FullType> listFullTypeThree = fullSearchDao.listFullTypeThree();
			for (int i = 0; i < listFullTypeThree.size(); i++) {
				FullType fullType = listFullTypeThree.get(i);
				Integer pageNum = j;
				FullSearchParam searchParam = new FullSearchParam();
				searchParam.setClassify(fullType.getValue());
				searchParam.setSource_name(fullType.getName());
				searchParam.setPageNum(pageNum);
				searchParam.setPageSize(25);
				searchParam.setTimeType(2);
				searchParam.setSearchWord("");

				setRedis(searchParam);
			}
		}
		System.err.println("完毕");
		}
	}
	
	public void setRedis(FullSearchParam searchParam){
		String times = "", timee = "";
		String timetype = searchParam.getTimetype();
		timetype = "spider_time";
		String searchparam = "spider_time";
		JSONObject timeJson = new JSONObject();
		switch (searchParam.getTimeType()) {
			case 1:
				timeJson = DateUtil.dateRoll(new Date(), Calendar.HOUR, -24);
				times = timeJson.getString("times");
				timee = timeJson.getString("timee");
				break;
			case 2:
				timeJson = DateUtil.getDifferOneDayTimes(-365);
				times = timeJson.getString("times") + " 00:00:00";
				timee = timeJson.getString("timee") + " 23:59:59";
				break;
			case 3:
				timeJson = DateUtil.getDifferOneDayTimes(-1);
				times = timeJson.getString("times") + " 00:00:00";
				timee = timeJson.getString("times") + " 23:59:59";
				break;
			case 4:
				timeJson = DateUtil.getDifferOneDayTimes(-3);
				times = timeJson.getString("times") + " 00:00:00";
				timee = timeJson.getString("timee") + " 23:59:59";
				break;
			case 5:
				timeJson = DateUtil.getDifferOneDayTimes(-7);
				times = timeJson.getString("times") + " 00:00:00";
				timee = timeJson.getString("timee") + " 23:59:59";
				break;
			case 6:
				timeJson = DateUtil.getDifferOneDayTimes(-15);
				times = timeJson.getString("times") + " 00:00:00";
				timee = timeJson.getString("timee") + " 23:59:59";
				break;
			case 7:
				timeJson = DateUtil.getDifferOneDayTimes(-30);
				times = timeJson.getString("times") + " 00:00:00";
				timee = timeJson.getString("timee") + " 23:59:59";
				break;
			case 8:
				times = searchParam.getStartTime() + " 00:00:00";
				timee = searchParam.getEndTime() + " 23:59:59";
				break;
		}
		if(StringUtils.equals(searchParam.getSource_name(), "全部")) {
			searchParam.setSource_name("");
		}
		String params = "page="+searchParam.getPageNum()+ "&topic="+searchParam.getSearchWord() +
				"&searchparam=" + searchparam + "&searchType=0" +
				"&source_name="+searchParam.getSource_name() +
				"&classify=" + searchParam.getClassify() + "&size=" + searchParam.getPageSize() +
				"&times="+times+
				"&timee="+timee+
				"&timetype="+timetype;
		String url = es_hot_search_url + SearchConstant.ES_API_HOT;
		String esResponse = MyHttpRequestUtil.sendPostEsSearch(url, params);
		boolean set = redisUtil.set(MD5Util.getMD5(searchParam.getPageNum() + searchParam.getClassify() + searchParam.getSource_name() + searchParam.getSearchWord()), esResponse);
		if(set) {
			System.out.println("来源为："+searchParam.getSource_name()+"的第："+searchParam.getPageNum()+"页更新数据到redis成功");
		}else {
			System.out.println("来源为："+searchParam.getSource_name()+"的第："+searchParam.getPageNum()+"页更新数据到redis失败");
		}
	}
}
