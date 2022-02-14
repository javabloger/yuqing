package com.stonedt.intelligence.service;

import java.util.List;
import java.util.Map;

import com.stonedt.intelligence.entity.DatafavoriteEntity;
import com.stonedt.intelligence.entity.User;

public interface DisplayBoardService {

	List<Map<String, Object>> searchDisplayBiardByUser(User user);

	List<DatafavoriteEntity> getCollectionByuser(Long user_id);
	

}
