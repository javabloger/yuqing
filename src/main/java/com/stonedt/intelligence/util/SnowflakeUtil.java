package com.stonedt.intelligence.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
//雪花算法生成唯一id

public class SnowflakeUtil {
	public static Long getId() {
		Snowflake snowflake = IdUtil.createSnowflake(1, 1);
		long id = snowflake.nextId();
		return id;
	}
}
