package com.stonedt.intelligence.quartz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.service.ProjectService;
import com.stonedt.intelligence.util.ProjectUtil;
import com.stonedt.intelligence.util.ProjectWordUtil;
import com.stonedt.intelligence.util.SnowflakeUtil;

/**
 * 声量监测数据定时任务
 *
 * @date 2020年3月27日 下午5:13:52
 */
@Component
public class VolumeSchedule {

    // 定时任务开关
    @Value("${schedule.volume.open}")
    private Integer schedule_volume_open;

    @Autowired
    private VolumeDataRequest volumeDataRequest;

    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ProjectService projectService;

    private final int[] timePeriod = {1, 2, 3, 4};

    /**
     * 定时任务逻辑
     */
//	@Scheduled(fixedRate = 10000000)
//    @Scheduled(cron = "0 0 0/12 * * ?")
    @Scheduled(cron = "0 0 22 * * ? ")
    public void start() {
        // 定时任务开启
        if (schedule_volume_open == 1) {
            try {
                System.err.println("声量监测 启动");
                List<Project> listAllProject = projectUtil.listAllProject();
                for (int z = 0; z < listAllProject.size(); z++) {
                    try {
                        Project project = listAllProject.get(z);
                        Long projectId = project.getProjectId();
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
                        }
                        Long volume_monitor_id = SnowflakeUtil.getId();
                        // 循环四个时间周期
                        for (int i = 0; i < timePeriod.length; i++) {
                            try {
                                Map<String, Object> jsonObject = new HashMap<String, Object>();
                                int time_period = timePeriod[i];
                                jsonObject.put("time_period", time_period);
                                String create_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        .format(LocalDateTime.now());
                                jsonObject.put("create_time", create_time);
                                jsonObject.put("volume_monitor_id", volume_monitor_id);
                                jsonObject.put("project_id", projectId);
                                // 获取周期时间
                                Map<String, String> time = volumeDataRequest.time(time_period);
                                // 1、关键词情感分析数据统计分布
                                String keywordEmotionStatistical = volumeDataRequest.keywordEmotionStatistical(keyword, highKeyword, stopword,
                                        time.get("start"), time.get("end"), projectType);
                                jsonObject.put("keyword_emotion_statistical", keywordEmotionStatistical);
                                // 2、关键词数据来源分布
                                String keyword_source_distribution = volumeDataRequest.keywordmediaexposure(keyword, highKeyword, stopword, time.get("start"),
                                        time.get("end"), projectType);
                                jsonObject.put("keyword_source_distribution", keyword_source_distribution);
                                // 3、关键词情感分析数据走势
                                String keyword_emotion_trend = volumeDataRequest.keywordsentimentFlagChart(highKeyword, stopword, time.get("start"),
                                        time.get("end"), time_period, projectType);
                                jsonObject.put("keyword_emotion_trend", keyword_emotion_trend);
                                // 4、关键词资讯数量排名
                                String keyword_news_rank = volumeDataRequest.keywordranking(keyword, highKeyword, stopword, time.get("start"),
                                        time.get("end"), projectType);
                                jsonObject.put("keyword_news_rank", keyword_news_rank);
                                // 5、关键词高频分布统计
                                String highword_cloud = volumeDataRequest.wordCloud(highKeyword, stopword, time.get("start"), time.get("end"), "", projectType);
                                jsonObject.put("highword_cloud", highword_cloud);
                                // 6、关键词曝光度环比排行
                                String keyword_exposure_rank = volumeDataRequest.keywordExposure(keyword, highKeyword, stopword, time.get("start"),
                                        time.get("end"), projectType);
                                jsonObject.put("keyword_exposure_rank", keyword_exposure_rank);
                                // 7、自媒体渠道声量排行
                                String media_user_volume_rank = volumeDataRequest.selfMediaRanking(highKeyword, stopword, time.get("start"),
                                        time.get("end"), projectType);
                                jsonObject.put("media_user_volume_rank", media_user_volume_rank);
                                int timingProject = projectService.timingProject(jsonObject);
                                if (timingProject == 1) {
                                    System.out.println("插入数据！");
                                } else if (timingProject == 2) {
                                    System.out.println("修改数据！");
                                } else {
                                    System.out.println("插入结果-----" + timingProject);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.err.println("声量监测   结束");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
