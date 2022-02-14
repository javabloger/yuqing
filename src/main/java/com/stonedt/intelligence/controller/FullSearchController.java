package com.stonedt.intelligence.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.aop.SystemControllerLog;
import com.stonedt.intelligence.entity.FullPolymerization;
import com.stonedt.intelligence.entity.FullType;
import com.stonedt.intelligence.entity.FullWord;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.FullSearchService;
import com.stonedt.intelligence.util.DateUtil;
import com.stonedt.intelligence.util.UserUtil;
import com.stonedt.intelligence.vo.FullSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全文搜索控制器
 */
@Controller
@RequestMapping(value = "/fullsearch")
public class FullSearchController {

    @Autowired
    private FullSearchService fullSearchService;

    @Autowired
    UserUtil userUtil;

    /**
     * 全文搜索页面
     */
    @SystemControllerLog(module = "全文搜索", submodule = "全文搜索", type = "全文搜索页面", operation = "search")
    @GetMapping(value = "")
    public String search(Integer full_poly, @RequestParam(defaultValue = "1") Integer full_type, Model model) {
        model.addAttribute("fulltype", full_type);
        model.addAttribute("full_poly", full_poly);
        model.addAttribute("menu", "full_search");
        return "search/full_search";
    }

    /**
     * 全文搜索结果页面
     */
    @GetMapping(value = "/result")
    @SystemControllerLog(module = "全文搜索", submodule = "搜索结果", type = "查询", operation = "result")
    public String result(@RequestParam(value = "menuStyle", required = false, defaultValue = "1") Integer menuStyle,
                         @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
                         Integer full_poly, String fulltype, String searchword, 
                         String sourcename, Integer page, Model model, 
                         HttpServletRequest request) {
    	model.addAttribute("searchWord", searchword);
        model.addAttribute("page", page);
        model.addAttribute("source_name", sourcename);
        model.addAttribute("fulltype", fulltype);
        model.addAttribute("full_poly", full_poly);
        model.addAttribute("menuStyle", menuStyle);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("menu", "full_search");

        //将搜索的词存到数据库
        User user = userUtil.getuser(request);
        String user_id = String.valueOf(user.getUser_id());
        String create_time = DateUtil.getNowTime();

        if (!searchword.equals("")) {
            FullWord fullWord = new FullWord();
            fullWord.setUser_id(Long.valueOf(user_id));
            fullWord.setCreate_time(create_time);
            fullWord.setSearch_word(searchword);
            boolean result = fullSearchService.saveFullWord(fullWord);
        }
        return "search/search_result";
    }
    
    /**
     * 律师详情页
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "律师详情", type = "查询", operation = "lawyerDetail")
    @GetMapping(value = "/lawyerDetail/{articleid}")
    public ModelAndView lawyerDetail(@PathVariable() String articleid, String fulltype,
            ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
    	mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
    	mv.setViewName("lawyer/lawyerDetail");
    	return mv;
    }
    
    /**
     * 律师列表
     * @param lawyerListParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "律师列表", type = "查询", operation = "lawyerList")
    @GetMapping(value = "/lawyerList")
    public @ResponseBody
    JSONObject lawyerList(FullSearchParam lawyerListParam) {
    	JSONObject lawyerList=fullSearchService.lawyerList(lawyerListParam);
    	return lawyerList;
    }
    
    /**
     * 律师详情数据
     * @param article_public_id
     * @return
     */
    @PostMapping(value = "/lawyerDetailData")
    public @ResponseBody
    JSONObject lawyerDetailData(String article_public_id) {
    	JSONObject lawyerDetailData=fullSearchService.lawyerDetailData(article_public_id);
    	return lawyerDetailData;
    }
    
    /**
     * 被执行人
     */
    @SystemControllerLog(module = "全文搜索", submodule = "被执行人列表", type = "查询", operation = "executionPersonList")
    @GetMapping(value = "/executionPersonList")
    public @ResponseBody
    JSONObject executionPersonList(FullSearchParam executionPersonListParam) {
    	JSONObject executionPersonList=fullSearchService.executionPersonList(executionPersonListParam);
    	return executionPersonList;
    }
    
    /**
     * 被执行人详情视图
     */
    @SystemControllerLog(module = "全文搜索", submodule = "被执行人详情", type = "查询", operation = "executionPersonDetail")
    @GetMapping(value = "/executionPersonDetail/{articleid}")
    public ModelAndView executionPersonDetail(@PathVariable() String articleid, String fulltype,
            ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
    	mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
    	mv.setViewName("executionPerson/executionPersonDetail");
    	return mv;
    }
    
    /**
     * 被执行人详情数据
     */
    @PostMapping(value = "/executionPersonDetailData")
    public @ResponseBody
    JSONObject executionPersonDetailData(String article_public_id) {
    	JSONObject executionPersonDetailData=fullSearchService.executionPersonDetailData(article_public_id);
    	return executionPersonDetailData;
    }
    
    /**
     * 专家人才列表
     */
    @SystemControllerLog(module = "全文搜索", submodule = "专家人才列表", type = "查询", operation = "professorList")
    @GetMapping(value = "/professorList")
    public @ResponseBody
    JSONObject professorList(FullSearchParam professorListParam) {
    	JSONObject professorList=fullSearchService.professorList(professorListParam);
    	return professorList;
    }
    
    /**
     * 专家人才详情视图
     */
    @SystemControllerLog(module = "全文搜索", submodule = "专家人才详情", type = "查询", operation = "professorDetail")
    @GetMapping(value = "/professorDetail/{articleid}")
    public ModelAndView professorDetail(@PathVariable() String articleid, String fulltype,
            ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
    	mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
    	mv.setViewName("professor/professorDetail");
    	return mv;
    }
    
    /**
     * 专家人才详情数据
     */
    @PostMapping(value = "/professorDetailData")
    public @ResponseBody
    JSONObject professorDetailData(String article_public_id) {
    	JSONObject professorDetailData=fullSearchService.professorDetailData(article_public_id);
    	return professorDetailData;
    }
    
    /**
     * 医生列表
     */
    @SystemControllerLog(module = "全文搜索", submodule = "医生列表", type = "查询", operation = "doctorList")
    @GetMapping(value = "/doctorList")
    public @ResponseBody
    JSONObject doctorList(FullSearchParam doctorListParam) {
    	JSONObject doctorList=fullSearchService.doctorList(doctorListParam);
    	return doctorList;
    }
    
    /**
     * 医生详情视图
     */
    @SystemControllerLog(module = "全文搜索", submodule = "医生详情", type = "查询", operation = "doctorDetail")
    @GetMapping(value = "/doctorDetail/{articleid}")
    public ModelAndView doctorDetail(@PathVariable() String articleid, String fulltype,
            ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
    	mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
    	mv.setViewName("doctor/doctorDetail");
    	return mv;
    }
    
    /**
     * 医生详情数据
     */
    @PostMapping(value = "/doctorDetailData")
    public @ResponseBody
    JSONObject doctorDetailData(String article_public_id) {
    	JSONObject doctorDetailData=fullSearchService.doctorDetailData(article_public_id);
    	return doctorDetailData;
    }

    /**
     * 资讯数据列表
     */
    @SystemControllerLog(module = "全文搜索", submodule = "资讯列表", type = "查询", operation = "informationList")
    @GetMapping(value = "/informationList")
    public @ResponseBody
    JSONObject informationList(FullSearchParam informationListParam) {


    	JSONObject informationList = fullSearchService.informationList(informationListParam);

        return informationList;
    }



    /**
     * 资讯数据列表
     */
    @SystemControllerLog(module = "全文搜索", submodule = "资讯列表", type = "查询", operation = "informationList1")
    @PostMapping(value = "/informationListpost")
    public @ResponseBody
    JSONObject informationList1(@RequestBody FullSearchParam informationListParam) {


        informationListParam.setMatchType(1);
        informationListParam.setSortType(1);
        informationListParam.setMergeType(0);

        JSONObject informationList = fullSearchService.informationListSearch(informationListParam);

        return informationList;
    }


    /**
     * 热点数据列表
     *
     * @throws UnsupportedEncodingException
     */
    @SystemControllerLog(module = "全文搜索", submodule = "热点数据列表", type = "查询", operation = "hotList")
    @GetMapping(value = "/hotList")
    public @ResponseBody
    JSONObject hotList(FullSearchParam searchParam) throws UnsupportedEncodingException {
        return fullSearchService.hotList(searchParam);
    }

    /**
     * 投诉列表
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "投诉列表", type = "查询", operation = "complaintList")
    @GetMapping(value = "/complaintList")
    public @ResponseBody
    JSONObject complaintList(FullSearchParam searchParam) {
        return fullSearchService.complaintList(searchParam);
    }

    /**
     * 公告列表
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "公告列表", type = "查询", operation = "announcementList")
    @GetMapping(value = "/announcementList")
    public @ResponseBody
    JSONObject announcementList(FullSearchParam searchParam) {
        return fullSearchService.announcementList(searchParam);
    }

    /**
     * 公告rtype
     *
     * @param searchParam
     * @return
     */
    @GetMapping(value = "/announcementrtype")
    public @ResponseBody
    JSONArray announcementRtype(FullSearchParam searchParam) {
        return fullSearchService.announcementRtype(searchParam);
    }

    /**
     * 研报列表
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "研报列表", type = "查询", operation = "reportList")
    @GetMapping(value = "/reportList")
    public @ResponseBody
    JSONObject reportList(FullSearchParam searchParam) {
        return fullSearchService.reportList(searchParam);
    }

    /**
     * 研报分类
     *
     * @param searchParam
     * @return
     */
    @GetMapping(value = "/reportIndustry")
    public @ResponseBody
    JSONArray reportIndustry(FullSearchParam searchParam) {
        return fullSearchService.reportIndustry(searchParam);
    }
    
    /**
     * 招标列表
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "招标列表", type = "查询", operation = "biddingList")
    @GetMapping(value = "/biddingList")
    public @ResponseBody
    JSONObject bidding(FullSearchParam searchParam) {
        return fullSearchService.biddingList(searchParam);
    }

    /**
     * 招标详情
     *
     * @param articleid
     * @param fulltype
     * @param mv
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "招标详情", type = "查询", operation = "biddingdetail")
    @GetMapping(value = "biddingdetail/{articleid}")
    public ModelAndView biddingDetail(@PathVariable() String articleid, String fulltype,
                                      ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
        JSONObject bidding = fullSearchService.biddingDetail(articleid);

        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("bidding", bidding);
        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_bidding_detail");
        return mv;
    }

    /**
     * 招聘列表
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "招聘列表", type = "查询", operation = "inviteList")
    @GetMapping(value = "/inviteList")
    public @ResponseBody
    JSONObject inviteList(FullSearchParam searchParam) {
        JSONObject inviteList = fullSearchService.inviteList(searchParam);
        return inviteList;
    }

    /**
     * 招聘详情
     *
     * @param record_id
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "招聘详情", type = "查询", operation = "inviteDetails")
    @GetMapping(value = "inviteDetails/{record_id}")
    public ModelAndView inviteDetails(@PathVariable() String record_id, String fulltype,
                                      ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
        JSONObject invite = fullSearchService.getInviteDetail(record_id);
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("invite", invite);
        mv.addObject("menu", "full_search");
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_invite_detail");
        return mv;
    }

    /**
     * 跳转公告详情页面
     *
     * @param articleid
     * @param mv
     * @param request
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "招聘详情", type = "查询", operation = "reportdetail")
    @GetMapping(value = "reportdetail/{articleid}/{type}")
    public ModelAndView reportDetail(@PathVariable() String articleid, @PathVariable() String type, String fulltype,
                                     ModelAndView mv, String menuStyle, String fullpoly, String searchWord,
                                     HttpServletRequest request) {
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
        mv.addObject("type", type);
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_report_detail");
        return mv;
    }

    /**
     * 公告、研报详情
     *
     * @param request
     * @param type
     * @param article_public_id
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "公告、研报详情", type = "查询", operation = "reportdetail")
    @GetMapping(value = "getresearch-report-detail")
    public @ResponseBody
    String getReportDetail(HttpServletRequest request, String type, String article_public_id) {
        return fullSearchService.getReportDetail(type, article_public_id);
    }

    /**
     * 工商列表
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "工商列表", type = "查询", operation = "companyList")
    @GetMapping(value = "companyList")
    public @ResponseBody
    JSONObject companyList(FullSearchParam searchParam) {
        return fullSearchService.companyList(searchParam);
    }

    /**
     * 工商行业分类
     *
     * @param searchParam
     * @return
     */
   /* @SystemControllerLog(module = "全文搜索", submodule = "工商行业分类", type = "查询", operation = "companyIndustry")*/
    @GetMapping(value = "companyIndustry")
    public @ResponseBody
    JSONArray companyIndustry(FullSearchParam searchParam) {
        return fullSearchService.companyIndustry(searchParam);
    }

    /**
     * 工商详情页面
     *
     * @param articleid
     * @param type
     * @param fulltype
     * @param mv
     * @param request
     * @return
     */
    
    @GetMapping(value = "companyDetail/{articleid}")
    public ModelAndView companyDetail(@PathVariable() String articleid, String fulltype,
                                      ModelAndView mv, String menuStyle, String fullpoly, String searchWord,
                                      HttpServletRequest request) {
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_company_detail");
        return mv;
    }

    /**
     * 工商详情
     *
     * @param article_public_id
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "工商详情", type = "查询", operation = "companyDetails")
    @GetMapping(value = "companyDetails")
    public @ResponseBody
    JSONObject companyDetails(String article_public_id) {
        return fullSearchService.companyDetails(article_public_id);
    }

    /**
     * 法律文书
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "法律文书", type = "查询", operation = "judgmentList")
    @GetMapping(value = "judgmentList")
    public @ResponseBody
    JSONObject judgmentList(FullSearchParam searchParam) {
        return fullSearchService.judgmentList(searchParam);
    }

    /**
     * 案件类型
     *
     * @param searchParam
     * @return
     */
    @GetMapping(value = "judgmentCaseType")
    public @ResponseBody
    JSONArray judgmentCaseType(FullSearchParam searchParam) {
        return fullSearchService.judgmentCaseType(searchParam);
    }

    /**
     * 法律文书详情
     *
     * @param articleid
     * @param fulltype
     * @param mv
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "法律文书详情", type = "查询", operation = "judgmentDetail")
    @GetMapping(value = "judgmentDetail/{articleid}")
    public ModelAndView judgmentDetail(@PathVariable() String articleid, String fulltype,
                                       ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
        JSONObject judgment = fullSearchService.judgmentDetail(articleid);
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("judgment", judgment);
        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_judgment_detail");
        return mv;
    }

    /**
     * 知识产权列表
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "知识产权列表", type = "查询", operation = "knowLedgeList")
    @GetMapping(value = "knowLedgeList")
    public @ResponseBody
    JSONObject knowLedgeList(FullSearchParam searchParam) {
        JSONObject knowLedgeList = fullSearchService.knowLedgeList(searchParam);
        return knowLedgeList;
    }

    /**
     * 知识产权 专利类型
     *
     * @param searchParam
     * @return
     */
    @GetMapping(value = "knowLedgeCaseType")
    public @ResponseBody
    JSONArray knowLedgeCaseType(FullSearchParam searchParam) {
        return fullSearchService.knowLedgeCaseType(searchParam);
    }
    @SystemControllerLog(module = "全文搜索", submodule = "知识产权详情", type = "查询", operation = "knowLedgeDetail")
    @GetMapping(value = "knowLedgeDetail/{articleid}")
    public ModelAndView knowLedgeDetail(@PathVariable() String articleid, String fulltype,
                                        ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
        JSONObject knowLedge = fullSearchService.knowLedgeDetail(articleid);
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("knowLedge", knowLedge);
        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_knowledge_detail");
        return mv;
    }

    /**
     * 投资融资
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "投资融资", type = "查询", operation = "investmentList")
    @GetMapping(value = "investmentList")
    public @ResponseBody
    JSONObject investmentList(FullSearchParam searchParam) {
        return fullSearchService.investmentList(searchParam);
    }

    /**
     * 投资融资 类型
     *
     * @param searchParam
     * @return
     */
    @GetMapping(value = "investmentType")
    public @ResponseBody
    JSONArray investmentType(FullSearchParam searchParam) {
        return fullSearchService.investmentType(searchParam);
    }

    /**
     * 投资融资 详情
     *
     * @param articleid
     * @param fulltype
     * @param mv
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "投资融资详情", type = "查询", operation = "investmentList")
    @GetMapping(value = "investmentDetail/{articleid}")
    public ModelAndView investmentDetail(@PathVariable() String articleid, String fulltype,
                                         ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
        JSONObject investment = fullSearchService.investmentDetail(articleid);
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("investment", investment);
        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_investment_detail");
        return mv;
    }

    /**
     * 问答数据--百度知道
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "百度知道", type = "查询", operation = "baiduKnowsList")
    @GetMapping(value = "baiduKnowsList")
    public @ResponseBody
    JSONObject baiduKnowsList(FullSearchParam searchParam) {
        JSONObject baiduKnowsList = fullSearchService.baiduKnowsList(searchParam);
        return baiduKnowsList;
    }

    /**
     * 学术数据--百度学术
     *
     * @param searchParam
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "百度学术", type = "查询", operation = "thesisnList")
    @GetMapping(value = "thesisnList")
    public @ResponseBody
    JSONObject thesisnList(FullSearchParam searchParam) {
        JSONObject thesisnList = fullSearchService.thesisnList(searchParam);
        return thesisnList;
    }

    /**
     * 学术数据--百度学术 详情
     *
     * @param articleid
     * @param fulltype
     * @param mv
     * @return
     */
    @SystemControllerLog(module = "全文搜索", submodule = "百度学术详情", type = "查询", operation = "thesisnDetail")
    @GetMapping(value = "thesisnDetail/{articleid}")
    public ModelAndView thesisnDetail(@PathVariable() String articleid, String fulltype,
                                      ModelAndView mv, String menuStyle, String fullpoly, String searchWord) {
        JSONObject thesisn = fullSearchService.thesisnDetail(articleid);
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);

        mv.addObject("thesisn", thesisn);
        mv.addObject("articleid", articleid);
        mv.addObject("menu", "full_search");
        mv.addObject("fulltype", fulltype);
        mv.setViewName("search/search_thesisn_detail");
        return mv;
    }


    /**
     * @param [articleid, groupid, projectid, menu, mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @description: 跳转 资讯文章详情页面 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/13 14:32 <br>
     * @author: huajiancheng <br>
     */
    @SystemControllerLog(module = "全文搜索", submodule = "资讯文章详情", type = "查询", operation = "thesisnDetail")
    @GetMapping(value = "/detail/{articleid}")
    public ModelAndView skiparticle(@PathVariable() String articleid,
                                    String groupid, String projectid, String relatedWord,
                                    String menu, String page, ModelAndView mv,
                                    String menuStyle, String fulltype,
                                    String fullpoly, String searchWord,String publish_time,
                                    HttpServletRequest request) {
        if (StringUtils.isBlank(groupid)) groupid = "";
        if (StringUtils.isBlank(projectid)) projectid = "";
        if (StringUtils.isBlank(articleid)) articleid = "";
        if (StringUtils.isBlank(relatedWord)) relatedWord = "";
        if (StringUtils.isBlank(publish_time)) publish_time = "";
        if (StringUtils.isBlank(menu)) menu = "full_search";
        mv.addObject("menuStyle", menuStyle);
        mv.addObject("full_poly", fullpoly);
        mv.addObject("fulltype", fulltype);
        mv.addObject("searchWord", searchWord);
        mv.addObject("publish_time", publish_time);
        mv.addObject("articleid", articleid);
        mv.addObject("groupid", groupid);
        mv.addObject("projectid", projectid);
        mv.addObject("relatedword", relatedWord);
        mv.addObject("menu", "full_search");
        mv.setViewName("search/search_detail");
        return mv;
    }

    /**
     * 获取一级菜单
     *
     * @return
     */
    @GetMapping("listFullTypeByFirst")
    public @ResponseBody
    List<FullType> listFullTypeByFirst() {
        List<FullType> listFullTypeByFirst = fullSearchService.listFullTypeByFirst();
        return listFullTypeByFirst;
    }

    /**
     * 获取二级菜单
     *
     * @param type_one_id
     * @return
     */
    @GetMapping("listFullTypeBySecond")
    public @ResponseBody
    List<FullType> listFullTypeBysecond(Integer type_one_id) {
        List<FullType> listFullTypeBysecond = fullSearchService.listFullTypeBysecond(type_one_id);
        return listFullTypeBysecond;
    }

    /**
     * 获取三级菜单
     *
     * @param type_one_id
     * @return
     */
    @GetMapping("listFullTypeByThird")
    public @ResponseBody
    List<FullType> listFullTypeBythird(Integer type_two_id) {
        List<FullType> listFullTypeBythird = fullSearchService.listFullTypeBythird(type_two_id);
        return listFullTypeBythird;
    }

    /**
     * 获取聚合分类
     *
     * @return
     */
    @GetMapping("listFullPolymerization")
    public @ResponseBody
    List<FullPolymerization> listFullPolymerization() {
        return fullSearchService.listFullPolymerization();
    }

    /**
     * @return
     */
    @GetMapping("getBreadCrumbs")
    public @ResponseBody
    JSONObject getBreadCrumbs(Integer menuStyle, Integer fulltype, Integer onlyid, Integer polyid) {
        return fullSearchService.getBreadCrumbs(menuStyle, fulltype, onlyid, polyid);
    }

    /**
     * 获取全文搜索一级分类列表 通过id list
     *
     * @param id
     * @return
     */
    @GetMapping("listFullTypeOneByIdList")
    public @ResponseBody
    List<FullType> listFullTypeOneByIdList(String id) {
        String[] split = id.split(",");
        List<Integer> list = new ArrayList<Integer>();
        for (String string : split) {
            list.add(Integer.parseInt(string));
        }
        return fullSearchService.listFullTypeOneByIdList(list);
    }


    /**
     * @param [request]
     * @return com.alibaba.fastjson.JSONObject
     * @description: 获取用户输入的关键词 <br>
     * @version: 1.0 <br>
     * @date: 2020/7/13 14:05 <br>
     * @author: huajiancheng <br>
     */

    @PostMapping(value = "/search")
    @ResponseBody
    public JSONObject getSearchWordById(HttpServletRequest request) {
        JSONObject response = new JSONObject();
        JSONObject paramJson = new JSONObject();
        User user = userUtil.getuser(request);
        String user_id = String.valueOf(user.getUser_id());
        paramJson.put("user_id", user_id);
        response = fullSearchService.getSearchWordById(paramJson);
        return response;
    }
}
