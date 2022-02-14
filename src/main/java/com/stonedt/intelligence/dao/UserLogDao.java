package com.stonedt.intelligence.dao;

import com.stonedt.intelligence.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * description: UserLogDao <br>
 * date: 2020/5/9 14:49 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
@Mapper
public interface UserLogDao {
    Integer saveUserLog(SysLog sysLog);

    Map<String, Object> getUserOrganizationById(@Param("map") Map<String, Object> map);

    Map<String, Object> getMoudleByName(@Param("module_name") String module_name);

    Map<String, Object> getSubMoudleByName(@Param("submodule_name") String module_name);
}
