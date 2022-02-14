package com.stonedt.intelligence.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SynthesizeDao {

	void insertSynthesize(@Param("map")Map<String, Object> map);

}
