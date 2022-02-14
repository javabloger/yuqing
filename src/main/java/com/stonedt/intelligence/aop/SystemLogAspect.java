package com.stonedt.intelligence.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.entity.SysLog;
import com.stonedt.intelligence.entity.SystemLogEntity;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.SystemLogService;
import com.stonedt.intelligence.service.UserService;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;

/**
 * @date  2019年11月27日 下午12:03:01
 */
@Aspect
@Component
@SuppressWarnings("all")
@Slf4j

public class SystemLogAspect {
	@Autowired
	private SystemLogService systemLogService;
	
	@Autowired
	private UserService userService;
	
	
	
	
	
	
	
	
	
	
	
    @Pointcut("@annotation(com.stonedt.intelligence.aop.SystemControllerLog)")
    public void controllerAspect(){
    	System.out.println("12121212121221");
    }
 
    /**
     * @Description  前置通知 ,用于拦截Controller层记录用户的操作
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint){
    	System.out.println("进入拦截器----------");
    	ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    	HttpServletRequest request = servletRequestAttributes.getRequest();
    	SysLog sysLog = new SysLog();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        System.out.println("拦截到了" + joinPoint.getSignature().getName() +"方法...");
        
        String loginIp = getIpAddr(request);
        
        String username = "";
        Integer id = null ;
        String signatrue_name = joinPoint.getSignature().getName();
        if(signatrue_name.equals("login")) {
        	//String queryString = request.getQueryString();
        	String telephone = request.getParameter("telephone");
        	User selectUserByTelephone = userService.selectUserByTelephone(telephone);
        	if(selectUserByTelephone!=null) {
        		username = selectUserByTelephone.getUsername();
            	id = selectUserByTelephone.getId();
            	System.out.println("username:"+username);
        	}
        	
        }else {
        	 User use = (User)request.getSession().getAttribute("User");
             username = use.getUsername();
             id = use.getId();
        }
        
        
        
       
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String remark = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
       
        SystemLogEntity systemlog = new SystemLogEntity();
        log.info("浏览器：{}", userAgent.getBrowser().toString());
        systemlog.setUser_browser(userAgent.getBrowser().toString());
        log.info("浏览器版本：{}", userAgent.getBrowserVersion());
        
        systemlog.setUser_browser_version(userAgent.getBrowserVersion().getVersion());
        log.info("操作系统: {}", userAgent.getOperatingSystem().toString());
        systemlog.setOperatingSystem(userAgent.getOperatingSystem().toString());
        systemlog.setUser_id(id);
        systemlog.setUsername(username);
        systemlog.setLoginip(loginIp);
        try {
			SysLog controllerMethodDescription = getControllerMethodDescription(joinPoint);
			String module = controllerMethodDescription.getModule_name();
			System.out.println("module:"+module);
			systemlog.setModule(module);
			String type = controllerMethodDescription.getType();
			systemlog.setType(type);

			//子模块
			String submodule_name = controllerMethodDescription.getSubmodule_name();
			systemlog.setSubmodule(submodule_name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        systemLogService.addData(systemlog);
        
        
        
        
        
        
    }
    
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		try {
			ipAddress = request.getHeader("x-forwarded-for");
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
					// 根据网卡取本机配置的IP
					InetAddress inet = null;
					try {
						inet = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					ipAddress = inet.getHostAddress();
				}
			}
			// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
				if (ipAddress.indexOf(",") > 0) {
					ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
				}
			}
		} catch (Exception e) {
			ipAddress = "";
			e.printStackTrace();
		}
		return ipAddress;
    }

    /**
     * @Description  获取注解中对方法的描述信息 用于Controller层注解
     */
    public static SysLog getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
    	SysLog sysLog = new SysLog();
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();//目标方法名
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        for (Method method:methods) {
            if (method.getName().equals(methodName)){
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length){
                    String module = method.getAnnotation(SystemControllerLog.class).module();
                    String type = method.getAnnotation(SystemControllerLog.class).type();

                    String submodule = method.getAnnotation(SystemControllerLog.class).submodule();

                    sysLog.setModule_name(module);
                    sysLog.setType(type);

                    sysLog.setSubmodule_name(submodule);
                    break;
                }
            }
        }
        return sysLog;
    }
}
