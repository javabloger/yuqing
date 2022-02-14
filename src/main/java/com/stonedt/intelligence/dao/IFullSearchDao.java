package com.stonedt.intelligence.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.stonedt.intelligence.entity.FullPolymerization;
import com.stonedt.intelligence.entity.FullType;
import com.stonedt.intelligence.entity.FullWord;

/**
 *	全文搜索数据接口
 */
@Mapper
public interface IFullSearchDao {
	
	/**
	 * 	获取全文搜索一级分类列表 
	 */
	List<FullType> listFullTypeOne();
	
	/**
	 * 获取所有三级分类
	 * @return
	 */
	List<FullType> listFullTypeThree();
	
	/**
	 * 根据一级分类id获取全文搜索二级分类列表 
	 * @param firstFullVal
	 * @return
	 */
	List<FullType> listFullTypeBysecond(@Param("type_one_id")Integer type_one_id);
	
	/**
	 * 根据二级分类vid获取全文搜索三级分类列表
	 * @param type_two_id
	 * @return
	 */
	List<FullType> listFullTypeBythird(@Param("type_two_id")Integer type_two_id);
	
	/**
	 * 获取聚合分类
	 * @return
	 */
	List<FullPolymerization> listFullPolymerization();
	
	/**
	 * 获取全文搜索一级分类列表 通过id list
	 * @param idList
	 * @return
	 */
	List<FullType> listFullTypeOneByIdList(@Param("list")List<Integer> idList);
	/**
	 * 	获取用户搜索词的数量
	 */
	Integer getUserWordCount(@Param("userId")Long userId);
	
	/**
	 * 	保存用户搜索词记录
	 */
	Boolean saveFullWord(FullWord fullWord);
	
	String getBreadCrumbsByFullType(Integer fulltype);
	
	String getBreadCrumbsByPolyId(Integer polyid);

	String getBreadCrumbsByOnlyId(Integer onlyid);

	List<Map<String,Object>> getSearchWordById(@Param("map") Map<String,Object> map);
}
