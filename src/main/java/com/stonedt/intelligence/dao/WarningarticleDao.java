package com.stonedt.intelligence.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.stonedt.intelligence.entity.AnalysisQuartzDo;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface WarningarticleDao {

	List<Map<String, Object>> selectWAlsitByUser(@Param("user_id")Long user_id);
	
}
