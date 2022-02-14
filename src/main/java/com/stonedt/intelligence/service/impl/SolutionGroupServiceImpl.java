package com.stonedt.intelligence.service.impl;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stonedt.intelligence.dao.SolutionGroupDao;
import com.stonedt.intelligence.entity.SolutionGroup;
import com.stonedt.intelligence.service.SolutionGroupService;

@Service
public class SolutionGroupServiceImpl implements SolutionGroupService {

    @Autowired
    private SolutionGroupDao solutionGroupDao;

    @Override
    public int addSolutionGroup(SolutionGroup sg) {
        return solutionGroupDao.addSolutionGroup(sg);
    }

    /**
     * @param [sg 方案组实体]
     * @return java.lang.Integer
     * @description: 修改方案组 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/15 20:55 <br>
     * @author: huajiancheng <br>
     */

    @Override
    public JSONObject editSolutionGroup(SolutionGroup sg) {
        JSONObject response = new JSONObject();
        Integer groupCount = solutionGroupDao.getGroupCount(sg);
        if (groupCount > 0) {
            response.put("code", 500);
            response.put("msg", "方案组名已存在！");
        } else {
            Integer count = solutionGroupDao.editSolutionGroup(sg);
            if (count > 0) {
                response.put("code", 200);
                response.put("msg", "方案组名修改成功！");
            } else {
                response.put("code", 500);
                response.put("msg", "方案组名修改失败！");
            }
        }
        return response;
    }

    @Override
    public String getGroupName(Long groupId) {
        return solutionGroupDao.getGroupName(groupId);
    }

	@Override
	public List<SolutionGroup> listSolutionGroupByUserId(Long userId) {
		return solutionGroupDao.listSolutionGroupByUserId(userId);
	}

	@Override
	public Map<String, Object> updateSolutionGroupStatus(Long groupId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean updateSolutionGroupStatus = solutionGroupDao.updateSolutionGroupStatus(groupId);
		if (updateSolutionGroupStatus) {
			result.put("state", true);
			result.put("message", "删除方案组成功！");
		}else {
			result.put("state", false);
			result.put("message", "删除方案组失败！");
		}
		return result;
	}

}
