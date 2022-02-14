package com.stonedt.intelligence;

import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.dao.AnalysisQuartzDao;
import com.stonedt.intelligence.dao.ProjectTaskDao;
import com.stonedt.intelligence.entity.AnalysisQuartzDo;
import com.stonedt.intelligence.entity.ProjectTask;
import com.stonedt.intelligence.quartz.AnalysisDataRequest;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.ProjectWordUtil;
import com.stonedt.intelligence.util.SnowFlake;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StonedtPortalApplicationTests {


	@Value("${schedule.analysispt.open}")
	private Integer schedule_analysispt_open;
	@Value("${insertnewwords.url}")
	private String insert_new_words_url;


	@Autowired
	private AnalysisDataRequest analysisDataRequest;

//    @Autowired
//    private AnalysisService analysisService;

	@Autowired
	private AnalysisQuartzDao analysisQuartzDao;

	@Autowired
	private ProjectTaskDao projectTaskDao;

	private SnowFlake snowFlake = new SnowFlake();

	private final int[] timePeriod = {1, 2, 3, 4};

	@Test
	public void contextLoads() {

		name();


	}


	@Scheduled(cron = "0 0/1 * * * ?")
	public void name() {
		if (schedule_analysispt_open == 1) {
			try {
				// 定时业务逻辑
				System.out.println("AnalysisPTQuartz 启动");
				List<ProjectTask> listProjectTaskByAnalysisFlag = projectTaskDao.listProjectTaskByAnalysisFlag();
				for (int i = 0; i < listProjectTaskByAnalysisFlag.size(); i++) {
					try {
						ProjectTask projectTask = listProjectTaskByAnalysisFlag.get(i);
						String keyword = projectTask.getSubject_word();
						if (StringUtils.isNotBlank(keyword)) keyword = keyword.trim();
						String stopword = projectTask.getStop_word();
						if (StringUtils.isNotBlank(stopword)) stopword = stopword.trim();
						String characterWord = projectTask.getCharacter_word();
						if (StringUtils.isNotBlank(characterWord)) characterWord = characterWord.trim();
						String eventWord = projectTask.getEvent_word();
						if (StringUtils.isNotBlank(eventWord)) eventWord = eventWord.trim();
						String regionalWord = projectTask.getRegional_word();
						if (StringUtils.isNotBlank(regionalWord)) regionalWord = regionalWord.trim();
						Integer projectType = projectTask.getProject_type();
						String highKeyword = keyword;
						if (projectType == 2) {
							highKeyword = ProjectWordUtil.highProjectKeyword(keyword, regionalWord, characterWord, eventWord);
							stopword = ProjectWordUtil.highProjectStopword(stopword);
						}

						if (projectType == 1) {
							projectType = 2;
							highKeyword = ProjectWordUtil.QuickProjectKeyword(keyword);
							stopword = ProjectWordUtil.highProjectStopword(stopword);
							keyword = ProjectWordUtil.CommononprojectKeyWord(keyword);
						}

						Long projectId = projectTask.getProject_id();
						for (int j = 0; j < timePeriod.length; j++) {
							try {
								int time_period = timePeriod[j];
								Map<String, String> timeMap = analysisDataRequest.time(time_period);
								String times = timeMap.get("start");
								String timee = timeMap.get("end");

								AnalysisQuartzDo analysisQuartzDo = new AnalysisQuartzDo();
								analysisQuartzDo.setCreate_time(DateUtil.nowTime());
								analysisQuartzDo.setAnalysis_id(snowFlake.getId());
								analysisQuartzDo.setProject_id(projectId);
								analysisQuartzDo.setTime_period(time_period);

								// 数据概览
								String dataOverview = analysisDataRequest.dataOverview(projectId, highKeyword, times, timee, stopword, projectType);
								analysisQuartzDo.setData_overview(dataOverview);
								// 情感占比
								String emotional = analysisDataRequest.emotional(highKeyword, times, timee, stopword, projectType);
								analysisQuartzDo.setEmotional_proportion(emotional);
								// 方案命中主体词
								String fangan = analysisDataRequest.fangan(keyword, highKeyword, times, timee, stopword, projectType);
								analysisQuartzDo.setPlan_word_hit(fangan);
								// 关键词情感分析数据走势
								String keywordsentimentFlagChart = analysisDataRequest.keywordsentimentFlagChart(keyword, highKeyword, stopword, times, timee, time_period, projectType);
								analysisQuartzDo.setKeyword_emotion_trend(keywordsentimentFlagChart);
								// 热点事件排名
								String hotEventRanking = analysisDataRequest.hotEventRanking(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setHot_event_ranking(hotEventRanking);
								// 关键词高频分布统计
								String wordCloud = analysisDataRequest.wordCloud(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setHighword_cloud(wordCloud);
								// 高频词指数
								//String keyWordIndex = analysisDataRequest.keyWordIndex(time_period, keywordsentimentFlagChart, times, timee, stopword, projectType);
								String keyWordIndex = analysisDataRequest.keyWordIndex(time_period, keywordsentimentFlagChart, times, timee, stopword, projectType,wordCloud);
								analysisQuartzDo.setKeyword_index(keyWordIndex);
								// 媒体活跃度分析
								String mediaActivityAnalysis = analysisDataRequest.mediaActivityAnalysis(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setMedia_activity_analysis(mediaActivityAnalysis);
								// 热点地区排名
								String hotSpotRanking = analysisDataRequest.hotSpotRanking(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setHot_spot_ranking(hotSpotRanking);
								// 数据来源分布
								String dataSourceDistribution = analysisDataRequest.dataSourceDistribution(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setData_source_distribution(dataSourceDistribution);
								// 数据来源分析
								String dataSourceAnalysis = analysisDataRequest.dataSourceAnalysis(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setData_source_analysis(dataSourceAnalysis);
								// 关键词曝光度环比排行
								String keywordExposure = analysisDataRequest.keywordExposure(keyword, highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setKeyword_exposure_rank(keywordExposure);
								// 自媒体渠道声量排名
								String selfMediaRanking = analysisDataRequest.selfMediaRanking(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setSelfmedia_volume_rank(selfMediaRanking);


								//String dataNer = analysisDataRequest.dataNer(highKeyword, stopword, times, timee, projectType);
								//政策法规
								String dataPolicy = analysisDataRequest.dataPolicy(highKeyword, stopword, times, timee, projectType);

								//JSONObject nersjon = JSONObject.parseObject(dataNer);


								JSONObject nersjon = new JSONObject();
								nersjon.put("policy", JSONObject.parseObject(dataPolicy).get("policy"));


								//行业分布分析
								String industrialDistribution = analysisDataRequest.industrialDistribution(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setIndustrial_distribution(industrialDistribution);


								//事件统计
								String eventStudy = analysisDataRequest.eventStudy(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setEvent_statistics(eventStudy);

								//计算统计结果


								JSONObject nerdatasjon = analysisDataRequest.dataNerArticleData(highKeyword, stopword, times, timee, projectType,nersjon);



								analysisQuartzDo.setNer(nerdatasjon.toJSONString());

								//将统计出来的实体存储到分词词典中
//                                try {
//	                                JSONObject nersjon = JSONObject.parseObject(dataNer);
//
//	                                nersjon.put("policy", JSONObject.parseObject(dataPolicy).get("policy"));
//
//	                                analysisQuartzDo.setNer(nersjon.toJSONString());
//
//	                                Iterator iter = nersjon.entrySet().iterator();
//	                                String message = "";
//	                                while (iter.hasNext()) {
//	                                    Map.Entry entry = (Map.Entry) iter.next();
//	                                   JSONArray parseArray = JSONArray.parseArray(entry.getValue().toString());
//	                                   for (Object object : parseArray) {
//	                                	   JSONObject parseObject = JSONObject.parseObject(object.toString());
//	                                	   String datakeyword = parseObject.get("keyword").toString();
//	                                	   message += datakeyword+",";
//									}
//	                                }
//	                                RestTemplate template = new RestTemplate();
//	                                MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
//	                                paramMap.add("text", message);
//	                                String result = template.postForObject(insert_new_words_url, paramMap, String.class);
//                                } catch (Exception e) {
//                                	e.printStackTrace();
//								}

								//方案命中分类统计
								String datacategory = analysisDataRequest.dataCategory(highKeyword, stopword, times, timee, projectType);
								analysisQuartzDo.setCategory_rank(datacategory);
								Boolean updateAnalysisExceptPopularInformation = analysisQuartzDao.updateAnalysisExceptPopularInformation(analysisQuartzDo);
								if (updateAnalysisExceptPopularInformation) {
									System.err.println("监测分析数据更新");
									projectTaskDao.updateProjectTaskAnalysisFlag(projectId);
								}

//                                Analysis analysis = new Analysis();
//                                analysis.setAnalysisId(snowFlake.getId());
//                                analysis.setProjectId(projectId);
//                                analysis.setTimePeriod(time_period);

								//高频词
//                                List<Map<String, Object>> keyWordIndex = analysisDataRequest.keyWordIndex(highKeyword, times, timee, stopword, projectType);
//                                analysis.setKeywordIndex(JSON.toJSONString(keyWordIndex));
//
//                                //情感分析
//                                String emotional = analysisDataRequest.emotional(highKeyword, times, timee, stopword, projectType);
//                                analysis.setEmotionalProportion(emotional);
//
//                                //方案词命中
//                                List<Map<String, Object>> fangan = analysisDataRequest.fangan(keyword, highKeyword, times, timee, stopword, projectType);
//                                analysis.setPlanWordHit(JSON.toJSONString(fangan));
//
//                                //热门资讯
//                                JSONArray ziXun = analysisDataRequest.ZiXun(keyword, highKeyword, times, timee, stopword, projectType);
//                                analysis.setPopularInformation(JSON.toJSONString(ziXun));
//
//                                // 热门数据
//                                String hot = analysisDataRequest.getHot(highKeyword, times, timee, stopword, time_period, projectType);
//                                JSONObject hotjson = JSONObject.parseObject(hot);
//
//                                // 热门人物
//                                JSONArray perArray = JSONArray.parseArray(hotjson.getString("per"));
//                                String hotData = analysisDataRequest.hotData(perArray, highKeyword, stopword, times, timee, projectType);
//                                analysis.setHotPeople(hotData);
//
//                                //热门机构
//                                JSONArray orgArray = JSONArray.parseArray(hotjson.getString("org"));
//                                String hotData2 = analysisDataRequest.hotData(orgArray, highKeyword, stopword, times, timee, projectType);
//                                analysis.setHotCompany(hotData2);
//
//                                //地区
//                                JSONArray locArray = JSONArray.parseArray(hotjson.getString("loc"));
//                                String hotData3 = analysisDataRequest.hotData(locArray, highKeyword, stopword, times, timee, projectType);
//                                analysis.setHotSpot(hotData3);
//
//                                int insert = analysisService.insert(analysis);
//                                if (insert > 0) {
//                                    System.out.println("数据存储成功");
//                                    projectTaskDao.updateProjectTaskAnalysisFlag(projectId);
//                                } else {
//                                    System.out.println("数据存储失败");
//                                }
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("AnalysisPTQuartz 结束");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
