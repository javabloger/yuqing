package com.stonedt.intelligence.service;

import com.stonedt.intelligence.entity.SysLog;

import java.util.Map;

/**
 * description: UserLogService <br>
 * date: 2020/5/9 14:36 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
public interface UserLogService {
    /**
     * @param [map]
     * @return java.lang.Integer
     * @description: 保存用户操作日志 <br>
     * @version: 1.0 <br>
     * @date: 2020/5/9 14:40 <br>
     * @author: huajiancheng <br>
     */

    Integer saveUserLog(SysLog sysLog);


    Integer crateUserAcount(Map<String, Object> map);

    Map<String, Object> getUserOrganizationById(Map<String, Object> map);

    Map<String, Object> getMoudleByName(String module_name);

    Map<String, Object> getSubMoudleByName(String module_name);


}
