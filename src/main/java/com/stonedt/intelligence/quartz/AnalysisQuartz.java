package com.stonedt.intelligence.quartz;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.dao.AnalysisQuartzDao;
import com.stonedt.intelligence.entity.AnalysisQuartzDo;
import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.ProjectUtil;
import com.stonedt.intelligence.util.ProjectWordUtil;
import com.stonedt.intelligence.util.SnowFlake;

/**
 * 监测分析定时任务
 */
@Component
public class AnalysisQuartz {

    // 定时任务开关
    @Value("${schedule.analysis.open}")
    private Integer schedule_analysis_open;
    
    @Value("${insertnewwords.url}")
    private String insert_new_words_url;

    @Autowired
    private AnalysisQuartzDao analysisQuartzDao;
    @Autowired
    private ProjectUtil projectUtil;

    @Autowired
    private AnalysisDataRequest analysisDataRequest;

    private SnowFlake snowFlake = new SnowFlake();

    private final int[] timePeriod = {1, 2, 3, 4};

    /**
     * 热门资讯
     */
//	@Scheduled(fixedRate = 10000000)
//    @Scheduled(cron = "0 0 0/1 * * ?")
//    @Scheduled(cron = "0 0/1 * * * ?")
    public void popularInformation() {
        if (schedule_analysis_open == 1) {
            try {
                System.err.println("热门资讯   启动");
                List<Project> listAllProject = projectUtil.listAllProject();
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(30);
                for (int k = 0; k < listAllProject.size(); k++) {
                    try {
                        final int i = k;
                        newFixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                Project project = listAllProject.get(i);
                                String keyword = project.getSubjectWord();
                                if (StringUtils.isNotBlank(keyword)) keyword = keyword.trim();
                                String stopword = project.getStopWord();
                                if (StringUtils.isNotBlank(stopword)) stopword = stopword.trim();
                                String characterWord = project.getCharacterWord();
                                if (StringUtils.isNotBlank(characterWord)) characterWord = characterWord.trim();
                                String eventWord = project.getEventWord();
                                if (StringUtils.isNotBlank(eventWord)) eventWord = eventWord.trim();
                                String regionalWord = project.getRegionalWord();
                                if (StringUtils.isNotBlank(regionalWord)) regionalWord = regionalWord.trim();
                                Integer projectType = project.getProjectType();
                                String highKeyword = keyword;
                                if (projectType == 2) {
                                    highKeyword = ProjectWordUtil.highProjectKeyword(keyword, regionalWord, characterWord, eventWord);
                                    stopword = ProjectWordUtil.highProjectStopword(stopword);
                                }
                                //简单方案切换
                                if (projectType == 1) {
                                	projectType = 2;
                                    highKeyword = ProjectWordUtil.QuickProjectKeyword(keyword);
                                    stopword = ProjectWordUtil.highProjectStopword(stopword);
                                }
                                Long projectId = project.getProjectId();
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

                                        //热门资讯
                                        JSONArray ziXun = analysisDataRequest.ZiXun(keyword, highKeyword, times, timee, stopword, projectType);
                                        analysisQuartzDo.setPopular_information(JSON.toJSONString(ziXun));

                                        Boolean updateAnalysisPopularInformation = analysisQuartzDao.updateAnalysisPopularInformation(analysisQuartzDo);
                                        if (updateAnalysisPopularInformation) {
                                            System.err.println("热门资讯更新");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                newFixedThreadPool.shutdown();
                try {
                    newFixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("热门资讯   结束");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 	新的监测分析定时任务
     */
    //@Scheduled(cron = "0 0 22 * * ?")
    @Scheduled(cron = "${schedule.analysispt.cron}")
    public void start() {
        if (schedule_analysis_open == 1) {
            try {
                // 定时业务逻辑
                System.err.println("AnalysisQuartz 启动");
                List<Project> listAllProject = projectUtil.listAllProject();
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(30);
                for (int k = 0; k < listAllProject.size(); k++) {
                    try {
                        final int i = k;
                        newFixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                Project project = listAllProject.get(i);
                                System.out.println("方案id："+project.getProjectId());
                                String keyword = project.getSubjectWord();
                                if (StringUtils.isNotBlank(keyword)) keyword = keyword.trim();
                                String stopword = project.getStopWord();
                                if (StringUtils.isNotBlank(stopword)) stopword = stopword.trim();
                                String characterWord = project.getCharacterWord();
                                if (StringUtils.isNotBlank(characterWord)) characterWord = characterWord.trim();
                                String eventWord = project.getEventWord();
                                if (StringUtils.isNotBlank(eventWord)) eventWord = eventWord.trim();
                                String regionalWord = project.getRegionalWord();
                                if (StringUtils.isNotBlank(regionalWord)) regionalWord = regionalWord.trim();
                                Integer projectType = project.getProjectType();
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
                                
                                Long projectId = project.getProjectId();
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
                                        String keyWordIndex = analysisDataRequest.keyWordIndex(time_period, highKeyword, times, timee, stopword, projectType,wordCloud);
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
                                        // 涉及地点排名
                                        // 涉及银行排名
                                        // 涉及机构排名
                                        // 涉及高校院所排名
                                        // 涉及政府部门排名
                                        // 涉及上市公司排名R
                                        // 涉及人物排名
                                        // 涉及医院排名
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
//                                        try {
//        	                                JSONObject nersjon = JSONObject.parseObject(dataNer);
//        	                                
//        	                                nersjon.put("policy", JSONObject.parseObject(dataPolicy).get("policy"));
//        	                                
//        	                                analysisQuartzDo.setNer(nersjon.toJSONString());
//        	                                
//        	                                Iterator iter = nersjon.entrySet().iterator();
//        	                                String message = "";
//        	                                while (iter.hasNext()) {
//        	                                    Map.Entry entry = (Map.Entry) iter.next();
//        	                                   JSONArray parseArray = JSONArray.parseArray(entry.getValue().toString());
//        	                                   for (Object object : parseArray) {
//        	                                	   JSONObject parseObject = JSONObject.parseObject(object.toString());
//        	                                	   String datakeyword = parseObject.get("keyword").toString();
//        	                                	   message += datakeyword+",";
//        									}
//        	                                }
//        	                                RestTemplate template = new RestTemplate();
//        	                                MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
//        	                                paramMap.add("text", message);
//        	                                String result = template.postForObject(insert_new_words_url, paramMap, String.class);
//                                        } catch (Exception e) {
//                                        	e.printStackTrace();
//        								}
                                        
                                        
                                        
                                        
                                        
                                        //方案命中分类统计
                                        String datacategory = analysisDataRequest.dataCategory(highKeyword, stopword, times, timee, projectType);
                                        analysisQuartzDo.setCategory_rank(datacategory);
                                        
                                        
                                        
                                        Boolean updateAnalysisExceptPopularInformation = analysisQuartzDao.updateAnalysisExceptPopularInformation(analysisQuartzDo);
                                        if (updateAnalysisExceptPopularInformation) {
                                            System.err.println("监测分析数据更新");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                newFixedThreadPool.shutdown();
                try {
                    newFixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("AnalysisQuartz 结束");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
