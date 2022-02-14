package com.stonedt.intelligence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * description: 全文搜索控制器 <br>
 * date: 2020/4/13 10:53 <br>
 * author: xiaomi <br>
 * version: 1.0 <br>
 */
@Controller
@RequestMapping(value = "/search")
public class SearchController {
	
//	@Autowired
//	private FullSearchService fullSearchService;
    
    /**
     * @description: 跳转全文搜索页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 15:51 <br>
     * @author: huajiancheng <br>
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView 
     * */
    @GetMapping(value = "")
    public ModelAndView fullsearch(ModelAndView mv) {
        mv.setViewName("search/fullSearch");
        mv.addObject("menu", "search");
        return mv;
    }
    
    /**
     * 	12小时热点内容
     */
//    @PostMapping(value = "/twelveHotArticle")
//    @ResponseBody
//    public String twelveHotArticle() {
//    	List<Map<String, Object>> twelveHotArticle = fullSearchService.twelveHotArticle();
//		return JSON.toJSONString(twelveHotArticle);
//	}


}
