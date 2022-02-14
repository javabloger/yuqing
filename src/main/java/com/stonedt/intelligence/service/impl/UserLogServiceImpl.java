package com.stonedt.intelligence.service.impl;


import com.stonedt.intelligence.dao.UserLogDao;
import com.stonedt.intelligence.entity.SysLog;
import com.stonedt.intelligence.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * description: UserLogServiceImpl <br>
 * date: 2020/5/9 14:36 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
@Service
public class UserLogServiceImpl implements UserLogService {
    @Autowired
    UserLogDao userLogDao;

    /**
     * @param sysLog 日志的信息
     * @return java.lang.Integer
     * @description: 保存用户日志 <br>
     * @version: 1.0 <br>
     * @date: 2020/5/9 14:38 <br>
     * @author: huajiancheng <br>
     */
    @Override
    public Integer saveUserLog(SysLog sysLog) {
        return userLogDao.saveUserLog(sysLog);
    }

    /**
     * @param map 账户的信息
     * @return java.lang.Integer
     * @description: 创建一个新的账户 <br>
     * @version: 1.0 <br>
     * @date: 2020/5/9 14:41 <br>
     * @author: huajiancheng <br>
     */
    @Override
    public Integer crateUserAcount(Map<String, Object> map) {
        return null;
    }

    /**
     * @param [map]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @description: 根据id获取用户的组织信息 <br>
     * @version: 1.0 <br>
     * @date: 2020/5/11 10:08 <br>
     * @author: huajiancheng <br>
     */

    @Override
    public Map<String, Object> getUserOrganizationById(Map<String, Object> map) {
        Map<String, Object> responseMap = userLogDao.getUserOrganizationById(map);
        return responseMap;
    }

    @Override
    public Map<String, Object> getMoudleByName(String module_name) {
        Map<String, Object> responseMap = userLogDao.getMoudleByName(module_name);
        return responseMap;
    }

    @Override
    public Map<String, Object> getSubMoudleByName(String module_name) {
        Map<String, Object> responseMap = userLogDao.getSubMoudleByName(module_name);
        return responseMap;
    }
}
