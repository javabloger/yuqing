package com.stonedt.intelligence.aspect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.UserLogService;
import com.stonedt.intelligence.util.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 用户操作日志 <br>
 * date: 2020/5/9 12:31 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
@Aspect
@Component
public class UserLogAspect {
    @Autowired
    UserLogService userLogService;
    @Autowired
    UserUtil userUtil;
    @Value("${kafuka.url}")
    private String kafuka_url;

    // 切点
    @Pointcut("@annotation(com.stonedt.intelligence.aspect.SystemControllerLog)")
    public void controllerAspect() {
    }


    @Around("controllerAspect()")
    public Object around(ProceedingJoinPoint point) {
        Object result = null;
        long times = System.currentTimeMillis();
        try {
            // 执行方法
            result = point.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        long timee = System.currentTimeMillis();

        // 保存日志
        saveLog(point, times, timee);
        return result;
    }

    /**
     * @param joinPoint JoinPoint 对象
     * @param times     开始时间
     * @param timee     结束时间
     * @return void
     * @description: 保存用户的操作日志 <br>
     * @version: 1.0 <br>
     * @date: 2020/5/9 17:41 <br>
     * @author: huajiancheng <br>
     */
    private void saveLog(ProceedingJoinPoint joinPoint, long timestamps, long timestampe) {
        JSONObject responseJson = new JSONObject();
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            SystemControllerLog logAnnotation = method.getAnnotation(SystemControllerLog.class);
            String submodule = "";
            String operation = "";
            if (logAnnotation != null) {
                // 注解上的描述
                submodule = logAnnotation.submodule();
                operation = logAnnotation.operation();
            }
            // 请求的方法名
            String class_name = joinPoint.getTarget().getClass().getName();
            String method_name = signature.getName();
            // 请求的方法参数值
            Object[] args = joinPoint.getArgs();
            // 请求的方法参数名称
            LocalVariableTableParameterNameDiscoverer tableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = tableParameterNameDiscoverer.getParameterNames(method);
            JSONArray paramsArray = new JSONArray();  // 参数
            if (args != null && paramNames != null) {
                for (int i = 0; i < args.length; i++) {
                    JSONObject paramsJson = new JSONObject();
                    String paramKey = paramNames[i];
                    if (!paramKey.equals("mv") && !paramKey.equals("request") && !paramKey.equals("session")) {
                        String paramValue = String.valueOf(args[i]);
                        if (!paramValue.equals("null")) {
                            paramsJson.put(paramKey, paramValue);
                            paramsArray.add(paramsJson);
                        }
                    }
                }
            }


            // 获取request
            HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
            User user = userUtil.getuser(request);
            Long user_id = user.getUser_id();
            String organization_id = user.getOrganization_id();
            Integer status = user.getStatus();
            String username = user.getUsername();
            String create_time = DateUtil.getNowTime();

            Map<String, Object> organizeMap = new HashMap<String, Object>();
            organizeMap.put("organization_id", organization_id);
            Map<String, Object> organizeResponseMap = userLogService.getUserOrganizationById(organizeMap);
            String organization_name = String.valueOf(organizeResponseMap.get("organization_name"));
            String term_of_validity = String.valueOf(organizeResponseMap.get("term_of_validity"));
            term_of_validity = term_of_validity.substring(0,19);
            String article_public_id = MD5Util.getMD5(user_id + create_time + method_name + method_name + paramsArray.toJSONString());

            String times = DateUtil.stampToDate(String.valueOf(timestamps));
            String timee = DateUtil.stampToDate(String.valueOf(timestampe));

//            Map<String, Object> moudleMap = userLogService.getMoudleByName(module_name);
            Map<String, Object> moudleMap = userLogService.getSubMoudleByName(submodule);
            String module_name = String.valueOf(moudleMap.get("module_name"));
            String submodule_name = String.valueOf(moudleMap.get("submodule_name"));
            String submodule_id = String.valueOf(moudleMap.get("submodule_id"));
            String module_id = String.valueOf(moudleMap.get("module_id"));

            responseJson.put("user_id", user_id);
            responseJson.put("article_public_id", article_public_id);
            responseJson.put("organization_id", organization_id);
            responseJson.put("create_time", create_time);
            responseJson.put("username", username);
            responseJson.put("status", status);
            responseJson.put("parameters", paramsArray.toJSONString());
            responseJson.put("organization_name", organization_name);
            responseJson.put("class_name", class_name);
            responseJson.put("method_name", method_name);
            responseJson.put("times", times);
            responseJson.put("timee", timee);
            responseJson.put("module_id", module_id);
            responseJson.put("module_name", module_name);
            responseJson.put("operation", operation);
            responseJson.put("submodule_id", submodule_id);
            responseJson.put("submodule_name", submodule_name);
            responseJson.put("term_of_validity", term_of_validity);

            responseJson.put("es_index", "stonedt_portaluserlog");
            responseJson.put("es_type", "infor");
            responseJson.put("hbase_table", "stonedt_portaluserlog");
            String result = MyHttpRequestUtil.doPostKafka("proStonedtData", responseJson.toJSONString(), kafuka_url);
            if (result.equals("200")) {
                System.out.println("发送成功！");
            }
//            SysLog sysLog = JSON.toJavaObject(responseJson, SysLog.class);
            // 保存系统日志
//            userLogService.saveUserLog(sysLog);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}