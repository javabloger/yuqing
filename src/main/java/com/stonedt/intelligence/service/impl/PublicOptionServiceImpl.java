package com.stonedt.intelligence.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stonedt.intelligence.constant.PublicoptionConstant;
import com.stonedt.intelligence.dao.PublicOptionDao;
import com.stonedt.intelligence.entity.PublicoptionDetailEntity;
import com.stonedt.intelligence.entity.PublicoptionEntity;
import com.stonedt.intelligence.service.PublicOptionService;
import com.stonedt.intelligence.util.ProjectWordUtil;
@Service
public class PublicOptionServiceImpl implements PublicOptionService{
	@Autowired
	private PublicOptionDao publicOptionDao;
	// es搜索地址
    @Value("${es.search.url}")
    private String es_search_url;
    @Value("${insertnewwords.url}")
	private String insert_new_words_url;

	@Override
	public List<PublicoptionEntity> getlist(Long user_id, String searchkeyword) {
		// TODO Auto-generated method stub
		return publicOptionDao.getlist(user_id,searchkeyword);
	}

	@Override
	public PublicoptionEntity getdatabyid(Map<String, Object> mapParam) {
		// TODO Auto-generated method stub
		return publicOptionDao.getdatabyid(mapParam);
	}
	
	@Override
	public PublicoptionEntity getdatabyid2(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getdatabyid2(id);
	}

	@Override
	public String updatabyid(Integer id, String eventname, String eventkeywords, String eventstarttime,
			String eventendtime, String eventstopwords) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		map.put("result", "success");
		try {
		PublicoptionEntity publicoption =new PublicoptionEntity();
		publicoption.setId(id);
		publicoption.setEventendtime(eventendtime+" 23:59:59");
		publicoption.setEventname(eventname);
		publicoption.setEventstarttime(eventstarttime+" 00:00:00");
		publicoption.setEventkeywords(eventkeywords);
		publicoption.setEventstopwords(eventstopwords);
		publicOptionDao.updatedatabyid(publicoption);
		} catch (Exception e) {
			map.put("status", 500);
			map.put("result", "fail");
		}
		return JSON.toJSONString(map);
	}

	@Override
	public String addpublicoptiondata(Long user_id,String eventname, String eventkeywords, String eventstarttime,
			String eventendtime, String eventstopwords) {
		
		try {
			 String message = ProjectWordUtil.CommononprojectKeyWord(eventkeywords);
		        RestTemplate template = new RestTemplate();
				MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
		        paramMap.add("text", message);
		        String result = template.postForObject(insert_new_words_url, paramMap, String.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
	       
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", 200);
			map.put("result", "success");
			try {
			PublicoptionEntity publicoption =new PublicoptionEntity();
			publicoption.setEventendtime(eventendtime+" 23:59:59");
			publicoption.setEventname(eventname);
			publicoption.setUser_id(user_id);
			publicoption.setEventstarttime(eventstarttime+" 00:00:00");
			publicoption.setEventkeywords(eventkeywords);
			publicoption.setEventstopwords(eventstopwords);
			publicOptionDao.addpublicoptiondata(publicoption);
			} catch (Exception e) {
				map.put("status", 500);
				map.put("result", "fail");
			}
			return JSON.toJSONString(map);
	}

	@Override
	public String DeleteupinfoByIds(String ids, HttpServletRequest request) {
		if(ids.indexOf("\"")!=-1) {
			/*System.out.println("ids:"+ids);*/
			ids = ids.substring(1, ids.length()-1);
			ids = ids.replaceAll("\"", "");
		}
		
		Map<String,Object> response = new HashMap<String,Object>();
		 List<String> uploadIds = new ArrayList<String>();
		 if (ids.contains(",")) {
			 uploadIds = Arrays.asList(ids.split(","));
	        } else {
	        	uploadIds = Arrays.asList(ids);
	        }
	        for (int i = 0; i < uploadIds.size(); i++) {
	            Integer id = Integer.parseInt(uploadIds.get(i).toString());
	            Map<String, Object> delParam = new HashMap<String, Object>();
	            delParam.put("del_status", 1);
	            delParam.put("id", id);
	            Integer count = publicOptionDao.DeleteupinfoByIds(delParam);
	            if (count > 0) {
	                response.put("delstatus", 200);
	                response.put("msg", "方案删除成功！");
	            } else {
	                response.put("delstatus", 500);
	                response.put("msg", "方案删除失败！");
	            }
	        }
	        return JSON.toJSONString(response);
	}

	@Override
	public PublicoptionDetailEntity getdetail(Map<String, Object> mapParam) {
		List<PublicoptionDetailEntity> list = publicOptionDao.getdetail(mapParam);
		return list.size() > 0?list.get(0):null;
	}

	@Override
	public List<PublicoptionEntity> getpublicoptionreportlist(Long user_id, String searchkeyword) {
		return publicOptionDao.getpublicoptionreportlist(user_id,searchkeyword);
	}

	@Override
	public String getBackAnalysisById(Integer id) {
		String backAnalysisStr = publicOptionDao.getBackAnalysisById(id);
		return backAnalysisStr;
	}

	@Override
	public String getEventContextById(Integer id) {
		return publicOptionDao.getEventContextById(id);
	}

	@Override
	public String getEventTraceById(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getEventTraceById(id);
	}

	@Override
	public String getHotAnalysisById(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getHotAnalysisById(id);
	}

	@Override
	public String getNetizensAnalysisById(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getNetizensAnalysisById(id);
	}

	@Override
	public String getStatisticsById(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getStatisticsById(id);
	}

	@Override
	public String getPropagationAnalysisById(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getPropagationAnalysisById(id);
	}

	@Override
	public String getThematicAnalysisById(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getThematicAnalysisById(id);
	}

	@Override
	public String getUnscrambleContentById(Integer id) {
		// TODO Auto-generated method stub
		return publicOptionDao.getUnscrambleContentById(id);
	}

	@Override
	public JSONObject loadInformation(PublicoptionEntity publicoptionEntity) {
		String url = es_search_url + PublicoptionConstant.es_api_search_list;
    	String eventstarttime = checkString(publicoptionEntity.getEventstarttime());
    	String eventendtime = checkString(publicoptionEntity.getEventendtime());
    	String eventkeywords = checkString(publicoptionEntity.getEventkeywords().replaceAll("\\+", "AND").replaceAll("\\|", "OR"));
    	String eventstopwords = checkString(publicoptionEntity.getEventstopwords());
    	String searchwords = "";
    	
    	String params = "times=" + eventstarttime + "&timee=" + eventendtime + "&keyword=" + eventkeywords + "&stopword=" + eventstopwords
                + "&searchkeyword=" + searchwords + "&origintype=0&searchType=0&size=10&page="+publicoptionEntity.getPage()+"&emotionalIndex="+publicoptionEntity.getEmotionalIndex()+"&projecttype=2";
    	String sendPost = getEsRequset(url, params,null,null);
    	JSONObject parseObject = JSONObject.parseObject(sendPost);
    	return parseObject;
	}
	
	public  String checkString(String string){
		if(null == string || "".equals(string.trim())){
			return "";
		}
		return string;
	}
	private  String getEsRequset(String url, String params,String checkMsg,String checkMsgKey) {
		int i =0;
		while (i<3) {
			i++;
			String sendPost = "";
			try {
				sendPost = sendPost(url,params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != checkMsg && !"".equals(checkMsg.trim())){
				if(JSONObject.parseObject(sendPost).get(checkMsgKey).equals(checkMsg)){
					continue;
				}
			}
			return sendPost;
		}
		return null;
	}
	/**
     * 发送post请求
     *
     * @date 2020年4月13日 下午4:02:23
     */
    public  String sendPost(String url, String params) {
        System.err.println(url + "?" + params);
        try {
            PrintWriter out = null;
            BufferedReader in = null;
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(params);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
