package com.stonedt.intelligence.entity;

import lombok.Data;
@Data
public class DatafavoriteEntity {
	private int id;
	private String title;
	private String article_public_id;
	private String publish_time;
	private Long user_id;
	private String favoritetime;
	private int status;
	private int emotionalIndex;
	private Long projectid;
	private Long groupid;
	private String source_name;
	
}
