package com.stonedt.intelligence.dao;

import org.apache.ibatis.annotations.Mapper;

import com.stonedt.intelligence.entity.SystemLogEntity;


@Mapper
public interface SystemLogDao {

	void addData(SystemLogEntity systemlog);

}
