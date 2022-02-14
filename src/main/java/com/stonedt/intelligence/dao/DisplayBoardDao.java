package com.stonedt.intelligence.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface DisplayBoardDao {

	List<Map<String, Object>> searchDisplayBiardByUser(@Param("user_id")Long user_id);

}
