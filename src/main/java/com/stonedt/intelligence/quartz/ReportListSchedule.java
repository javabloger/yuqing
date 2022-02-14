package com.stonedt.intelligence.quartz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stonedt.intelligence.entity.Project;
import com.stonedt.intelligence.entity.ReportCustom;
import com.stonedt.intelligence.service.ReportCustomService;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.ProjectUtil;
import com.stonedt.intelligence.util.SnowFlake;

/**
 * 数据报告列表定时任务
 */
@Component
public class ReportListSchedule {

    // 日报定时任务开关
    @Value("${schedule.dayreport.open}")
    private Integer schedule_dayreport_open;

    // 周报定时任务开关
    @Value("${schedule.weekreport.open}")
    private Integer schedule_weekreport_open;

    // 月报定时任务开关
    @Value("${schedule.monthreport.open}")
    private Integer schedule_monthreport_open;

    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ReportCustomService reportCustomService;

    private SnowFlake snowFlake = new SnowFlake();

    /**
     * 日报列表
     */
//	@Scheduled(fixedRate = 10000000)
    @Scheduled(cron = "0 0 1 * * ?")
    public void dayreport() {
        // 定时业务逻辑
        if (schedule_dayreport_open == 1) {
            try {
                System.err.println("ReportListSchedule dayreport start");
                List<Project> listAllProject = projectUtil.listAllProject();
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
                for (int k = 0; k < listAllProject.size(); k++) {
                    try {
                        final int i = k;
                        newFixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Project project = listAllProject.get(i);
                                    Long projectId = project.getProjectId();
                                    String projectName = project.getProjectName();
//							Integer projectType = project.getProjectType();
//							if (projectType != 1) {
//								return;
//							}
                                    String subjectWord = project.getSubjectWord();
                                    String stopWord = project.getStopWord();
                                    Long userId = project.getUserId();
                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    LocalDate minusDays = LocalDate.now().minusDays(1);
                                    int year = minusDays.getYear();
                                    int monthValue = minusDays.getMonthValue();
                                    int dayOfMonth = minusDays.getDayOfMonth();
                                    String reportName = year + "年" + String.format("%02d", monthValue) + "月"
                                            + String.format("%02d", dayOfMonth) + "日舆情日报-[" + projectName + "]";
                                    String preDate = minusDays.format(dateTimeFormatter);
                                    ReportCustom reportCustom = new ReportCustom();
                                    String nowTime = DateUtil.nowTime();
                                    reportCustom.setCreate_time(nowTime);
                                    reportCustom.setDel_status(0);
                                    reportCustom.setReport_status(0);
                                    reportCustom.setReport_time(nowTime);
                                    reportCustom.setReport_topping(0);
                                    reportCustom.setReport_id(snowFlake.getId());
                                    reportCustom.setKeyword(subjectWord);
                                    reportCustom.setProject_id(projectId);
                                    reportCustom.setReport_endtime(preDate + " 23:59:59");
                                    reportCustom.setReport_starttime(preDate + " 00:00:00");
                                    reportCustom.setStopword(stopWord);
                                    reportCustom.setUser_id(userId);
                                    reportCustom.setReport_type(1);
                                    reportCustom.setReport_name(reportName);
                                    int saveReportCustom = reportCustomService.saveReportCustom(reportCustom);
                                    if (saveReportCustom > 0) {
                                        System.err.println(DateUtil.nowTime() + " : " + reportName);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                System.err.println("ReportListSchedule dayreport end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 周报列表
     */
//	@Scheduled(fixedRate = 10000000)
    @Scheduled(cron = "0 0 1 ? * MON")
    public void weekreport() {
        // 定时业务逻辑
        if (schedule_weekreport_open == 1) {
            try {
                System.err.println("ReportListSchedule weekreport start");
                List<Project> listAllProject = projectUtil.listAllProject();
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
                for (int k = 0; k < listAllProject.size(); k++) {
                    try {
                        final int i = k;
                        newFixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Project project = listAllProject.get(i);
                                    Long projectId = project.getProjectId();
                                    String projectName = project.getProjectName();
//							Integer projectType = project.getProjectType();
//							if (projectType != 1) {
//								return;
//							}
                                    String subjectWord = project.getSubjectWord();
                                    String stopWord = project.getStopWord();
                                    Long userId = project.getUserId();
                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    LocalDate minusWeeks = LocalDate.now().minusWeeks(1);
                                    TemporalField fieldISO = WeekFields.of(Locale.FRANCE).dayOfWeek();
                                    LocalDate monday = minusWeeks.with(fieldISO, 1);
                                    LocalDate sunday = minusWeeks.with(fieldISO, 7);
                                    int year = monday.getYear();
                                    WeekFields weekFields = WeekFields.ISO;
                                    int weekNumber = monday.get(weekFields.weekOfWeekBasedYear());
                                    String reportName = year + "年第" + weekNumber + "周舆情周报-[" + projectName + "]";
                                    ReportCustom reportCustom = new ReportCustom();
                                    String nowTime = DateUtil.nowTime();
                                    reportCustom.setCreate_time(nowTime);
                                    reportCustom.setDel_status(0);
                                    reportCustom.setReport_status(0);
                                    reportCustom.setReport_time(nowTime);
                                    reportCustom.setReport_topping(0);
                                    reportCustom.setReport_id(snowFlake.getId());
                                    reportCustom.setKeyword(subjectWord);
                                    reportCustom.setProject_id(projectId);
                                    reportCustom.setReport_endtime(sunday.format(dateTimeFormatter) + " 23:59:59");
                                    reportCustom.setReport_starttime(monday.format(dateTimeFormatter) + " 00:00:00");
                                    reportCustom.setStopword(stopWord);
                                    reportCustom.setUser_id(userId);
                                    reportCustom.setReport_type(2);
                                    reportCustom.setReport_name(reportName);
                                    int saveReportCustom = reportCustomService.saveReportCustom(reportCustom);
                                    if (saveReportCustom > 0) {
                                        System.err.println(DateUtil.nowTime() + " : " + reportName);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                System.err.println("ReportListSchedule weekreport end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 月报列表
     */
//	@Scheduled(fixedRate = 10000000)
    @Scheduled(cron = "0 0 1 1 * ?")
    public void monthreport() {
        // 定时业务逻辑
        if (schedule_monthreport_open == 1) {
            try {
                System.err.println("ReportListSchedule monthreport start");
                List<Project> listAllProject = projectUtil.listAllProject();
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
                for (int k = 0; k < listAllProject.size(); k++) {
                    final int i = k;
                    newFixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Project project = listAllProject.get(i);
                                Long projectId = project.getProjectId();
                                String projectName = project.getProjectName();
//							Integer projectType = project.getProjectType();
//							if (projectType != 1) {
//								return;
//							}
                                String subjectWord = project.getSubjectWord();
                                String stopWord = project.getStopWord();
                                Long userId = project.getUserId();
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate minusMonths = LocalDate.now().minusMonths(1);
                                LocalDate firstDay = minusMonths.with(TemporalAdjusters.firstDayOfMonth());
                                LocalDate lastDay = minusMonths.with(TemporalAdjusters.lastDayOfMonth());
                                int year = minusMonths.getYear();
                                int monthValue = minusMonths.getMonthValue();
                                String reportName = year + "年" + String.format("%02d", monthValue) + "月舆情月报-[" + projectName + "]";
                                ReportCustom reportCustom = new ReportCustom();
                                String nowTime = DateUtil.nowTime();
                                reportCustom.setCreate_time(nowTime);
                                reportCustom.setDel_status(0);
                                reportCustom.setReport_status(0);
                                reportCustom.setReport_time(nowTime);
                                reportCustom.setReport_topping(0);
                                reportCustom.setReport_id(snowFlake.getId());
                                reportCustom.setKeyword(subjectWord);
                                reportCustom.setProject_id(projectId);
                                reportCustom.setReport_endtime(lastDay.format(dateTimeFormatter) + " 23:59:59");
                                reportCustom.setReport_starttime(firstDay.format(dateTimeFormatter) + " 00:00:00");
                                reportCustom.setStopword(stopWord);
                                reportCustom.setUser_id(userId);
                                reportCustom.setReport_type(3);
                                reportCustom.setReport_name(reportName);
                                int saveReportCustom = reportCustomService.saveReportCustom(reportCustom);
                                if (saveReportCustom > 0) {
                                    System.err.println(DateUtil.nowTime() + " : " + reportName);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                newFixedThreadPool.shutdown();
                try {
                    newFixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("ReportListSchedule monthreport end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
