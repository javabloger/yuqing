package com.stonedt.intelligence.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.stonedt.intelligence.entity.VolumeMonitor;

/** 

* @author 作者 ZouFangHao: 

* @version 创建时间：2020年4月15日 下午5:29:15 

* 类说明 

*/
@Mapper
public interface VolumeMonitorDao {
	
	VolumeMonitor getproject(Map<String, Object> map);
	
	Map<String, Object> getprojectName(Map<String, Object> map);
}
