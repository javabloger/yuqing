package com.stonedt.intelligence.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stonedt.intelligence.dao.SystemLogDao;
import com.stonedt.intelligence.entity.SystemLogEntity;
import com.stonedt.intelligence.service.SystemLogService;

@Service
public class SystemLogServiceImpl implements SystemLogService{

	@Autowired
	private SystemLogDao systemLogDao;
	@Override
	public void addData(SystemLogEntity systemlog) {
		systemLogDao.addData(systemlog);
		
	}
	

}
