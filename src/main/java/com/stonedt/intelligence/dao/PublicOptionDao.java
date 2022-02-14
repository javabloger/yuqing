package com.stonedt.intelligence.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.PublicoptionDetailEntity;
import com.stonedt.intelligence.entity.PublicoptionEntity;
@Mapper
public interface PublicOptionDao {

	List<PublicoptionEntity> getlist(@Param("user_id")Long user_id, @Param("searchkeyword")String searchkeyword);

	PublicoptionEntity getdatabyid(Map<String, Object> mapParam);
	
	PublicoptionEntity getdatabyid2(Integer id);

	int updatedatabyid(PublicoptionEntity publicoption);

	int addpublicoptiondata(PublicoptionEntity publicoption);


	Integer DeleteupinfoByIds(Map<String, Object> delParam);

	List<PublicoptionDetailEntity> getdetail(Map<String, Object> mapParam);

	List<PublicoptionEntity> getUnfinishedPublicoptionevent();

	void updateStatusbyid(@Param("id")Integer id, @Param("status")Integer status);

	void savepublicoptionDetail(Map<String, Object> map);

	List<PublicoptionEntity> getpublicoptionreportlist(Long user_id, String searchkeyword);

	String getBackAnalysisById(Integer id);

	String getEventContextById(Integer id);

	String getEventTraceById(Integer id);

	String getHotAnalysisById(Integer id);

	String getNetizensAnalysisById(Integer id);

	String getStatisticsById(Integer id);

	String getPropagationAnalysisById(Integer id);

	String getThematicAnalysisById(Integer id);

	String getUnscrambleContentById(Integer id);

	List<Map<String, Object>> getpublicoptionnetizensAnalysisData(@Param("user_id")Long user_id);
	
}
