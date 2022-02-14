package com.stonedt.intelligence.service;
/**
 *
 * @date  2020年4月17日 下午6:11:54
 */

import java.util.List;
import java.util.Map;

public interface ArticleService {
	
	Map<String, Object> articleDetail(String articleId, Long projectId,String relatedword,String publish_time);

	List<Map<String, Object>> relatedArticles(String keywords);

}
