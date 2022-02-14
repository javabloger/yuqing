package com.stonedt.intelligence.entity;

import java.util.List;

/**
 *
 * @author 	ZhaoHengxing
 * @date  2019年9月10日 下午2:43:03
 */
public class DataMonitorVO {
	
	private String article_public_id;
	private String title;
	private String publish_time;
	private String content;
	private String related_words;
	private String key_words;
	private String emotionalIndex;
	private String sourcewebsitename;
	private String similarvolume;
	private String source_url;
	private String vedioUrl;
	private List<String> imglist;
	private String videoorientationurl;
	
	public String getVideoorientationurl() {
		return videoorientationurl;
	}
	public void setVideoorientationurl(String videoorientationurl) {
		this.videoorientationurl = videoorientationurl;
	}
	public String getVedioUrl() {
		return vedioUrl;
	}
	public void setVedioUrl(String vedioUrl) {
		this.vedioUrl = vedioUrl;
	}
	public List<String> getImglist() {
		return imglist;
	}
	public void setImglist(List<String> imglist) {
		this.imglist = imglist;
	}
	public String getArticle_public_id() {
		return article_public_id;
	}
	public void setArticle_public_id(String article_public_id) {
		this.article_public_id = article_public_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublish_time() {
		return publish_time;
	}
	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRelated_words() {
		return related_words;
	}
	public void setRelated_words(String related_words) {
		this.related_words = related_words;
	}
	public String getKey_words() {
		return key_words;
	}
	public void setKey_words(String key_words) {
		this.key_words = key_words;
	}
	public String getEmotionalIndex() {
		return emotionalIndex;
	}
	public void setEmotionalIndex(String emotionalIndex) {
		this.emotionalIndex = emotionalIndex;
	}
	public String getSourcewebsitename() {
		return sourcewebsitename;
	}
	public void setSourcewebsitename(String sourcewebsitename) {
		this.sourcewebsitename = sourcewebsitename;
	}
	public String getSimilarvolume() {
		return similarvolume;
	}
	public void setSimilarvolume(String similarvolume) {
		this.similarvolume = similarvolume;
	}
	public String getSource_url() {
		return source_url;
	}
	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}
	
	
}
