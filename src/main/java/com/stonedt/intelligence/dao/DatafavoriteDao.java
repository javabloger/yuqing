package com.stonedt.intelligence.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.DatafavoriteEntity;

@Mapper
public interface DatafavoriteDao {  

	int adddata(DatafavoriteEntity favorite);

	List<DatafavoriteEntity> getdatafavoriteByUser(@Param("user_id")Long user_id);
	

	void updatedata(DatafavoriteEntity favorite);

	DatafavoriteEntity selectdata(Map<String, Object> map);

}
