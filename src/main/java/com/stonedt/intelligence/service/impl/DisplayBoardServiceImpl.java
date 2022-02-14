package com.stonedt.intelligence.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stonedt.intelligence.dao.DatafavoriteDao;
import com.stonedt.intelligence.dao.DisplayBoardDao;
import com.stonedt.intelligence.entity.DatafavoriteEntity;
import com.stonedt.intelligence.entity.User;
import com.stonedt.intelligence.service.DisplayBoardService;

@Service
public class DisplayBoardServiceImpl implements DisplayBoardService {
	
	@Autowired
	DisplayBoardDao displayBoardDao;
	@Autowired
	DatafavoriteDao datafavoriteDao;

	@Override
	public List<Map<String, Object>> searchDisplayBiardByUser(User user) {
		return displayBoardDao.searchDisplayBiardByUser(user.getUser_id());
	}

	@Override
	public List<DatafavoriteEntity> getCollectionByuser(Long user_id) {
		return datafavoriteDao.getdatafavoriteByUser(user_id);
	}

	
	

	
	
}
