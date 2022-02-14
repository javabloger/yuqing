package com.stonedt.intelligence.util;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class RedisUtil {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	

	/**
	 * 默认过期时长，单位：秒
	 */
	public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

	/**
	 * 不设置过期时长
	 */
	public static final long NOT_EXPIRE = -1;

	public boolean existsKey(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
	 *
	 * @param oldKey
	 * @param newKey
	 */
	public void renameKey(String oldKey, String newKey) {
		redisTemplate.rename(oldKey, newKey);
	}

	/**
	 * newKey不存在时才重命名
	 *
	 * @param oldKey
	 * @param newKey
	 * @return 修改成功返回true
	 */
	public boolean renameKeyNotExist(String oldKey, String newKey) {
		return redisTemplate.renameIfAbsent(oldKey, newKey);
	}

	/**
	 * 删除key
	 *
	 * @param key
	 */
	public void deleteKey(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 删除多个key
	 *
	 * @param keys
	 */
	public void deleteKey(String... keys) {
		Set<String> kSet = Stream.of(keys).map(k -> k).collect(Collectors.toSet());
		redisTemplate.delete(kSet);
	}

	/**
	 * 删除Key的集合
	 *
	 * @param keys
	 */
	public void deleteKey(Collection<String> keys) {
		Set<String> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
		redisTemplate.delete(kSet);
	}

	/**
	 * 设置key的生命周期
	 *
	 * @param key
	 * @param time
	 * @param timeUnit
	 */
	public void expireKey(String key, long time, TimeUnit timeUnit) {
		redisTemplate.expire(key, time, timeUnit);
	}

	/**
	 * 指定key在指定的日期过期
	 *
	 * @param key
	 * @param date
	 */
	public void expireKeyAt(String key, Date date) {
		redisTemplate.expireAt(key, date);
	}

	/**
	 * 查询key的生命周期
	 *
	 * @param key
	 * @param timeUnit
	 * @return
	 */
	public long getKeyExpire(String key, TimeUnit timeUnit) {
		return redisTemplate.getExpire(key, timeUnit);
	}

	/**
	 * 将key设置为永久有效
	 *
	 * @param key
	 */
	public void persistKey(String key) {
		redisTemplate.persist(key);
	}

	
	/**
	 * 写入缓存
	 */
	public boolean set(final String key, String value) {
		boolean result = false;
		try {
			redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 写入缓存-筛选项
	 */
	public boolean filteritemset(final String key, String value) {
		boolean result = false;
		try {
			redisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	
	
	/**
	 * 根据key获取vlue
	 *
	 * @param key
	 */
	public String getKey(String key) {
		String value = redisTemplate.opsForValue().get(key);
		return value;

	}


	public List<String> sortPageList(String key, String subKey, String by, boolean isDesc, boolean isAlpha, int off, int num) throws Exception {
		SortQueryBuilder<String> builder = SortQueryBuilder.sort(key);
		builder.by(subKey + "*->" + by);
		builder.get("#");
		builder.alphabetical(isAlpha);
		if (isDesc)
			builder.order(SortParameters.Order.DESC);
		builder.limit(off, num);
		List<String> cks = redisTemplate.sort(builder.build());

		List<T> result = new ArrayList<T>();
		for (String ck : cks) {
			//得到项目对象 by(subKey+ck);
		}
		return cks;

	}


	public Set<String> sortPageList(String key, Integer pageNum, Integer count){

		Set<String> strings = redisTemplate.opsForZSet().reverseRange(key, pageNum * count, pageNum * count + count - 1);
		return  strings;
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @param weight
	 */
	public void addZSet(String key, String value, long weight){
		redisTemplate.opsForZSet().add(key,value,weight);
	}


}