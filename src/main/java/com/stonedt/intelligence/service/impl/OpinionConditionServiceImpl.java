package com.stonedt.intelligence.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stonedt.intelligence.dao.OpinionConditionDao;
import com.stonedt.intelligence.entity.OpinionCondition;
import com.stonedt.intelligence.service.OpinionConditionService;

/**
 *
 */
@Service
public class OpinionConditionServiceImpl implements OpinionConditionService {

    @Autowired
    private OpinionConditionDao opinionConditionDao;

    @Override
    public OpinionCondition getOpinionConditionByProjectId(Long projectId) {
        try {
            OpinionCondition opinionConditionByProjectId = opinionConditionDao.getOpinionConditionByProjectId(projectId);
            if (null == opinionConditionByProjectId) {
                return new OpinionCondition();
            }
            return opinionConditionByProjectId;
        } catch (Exception e) {
            e.printStackTrace();
            return new OpinionCondition();
        }
    }

    @Override
    public Map<String, Object> updateOpinionCondition(OpinionCondition opinionCondition) {
        Map<String, Object> result = new HashMap<String, Object>();
        String times = opinionCondition.getTimes() + " 00:00:00";
        String timee = opinionCondition.getTimee() + " 23:59:59";
        opinionCondition.setTimes(times);
        opinionCondition.setTimee(timee);
        int updateOpinionCondition = opinionConditionDao.updateOpinionCondition(opinionCondition);
        if (updateOpinionCondition > 0) {
            result.put("status", true);
            result.put("message", "偏好设置修改成功");
        } else {
            result.put("status", false);
            result.put("message", "偏好设置修改失败");
        }
        return result;
    }

    @Override
    public Integer addOpinionConditionById(Map<String, Object> map) {
        Integer updateOpinionCondition = opinionConditionDao.addOpinionConditionById(map);
        return updateOpinionCondition;
    }

    @Override
    public Integer updateOpinionConditionByMap(Map<String, Object> map) {
        Integer updateOpinionCondition = opinionConditionDao.updateOpinionConditionByMap(map);
        return updateOpinionCondition;
    }

    @Override
    public Integer updateOpinionConditionById(Map<String, Object> map) {
        Integer updateOpinionCondition = opinionConditionDao.updateOpinionConditionById(map);
        return updateOpinionCondition;
    }
}
