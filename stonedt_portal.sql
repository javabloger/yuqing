/*
 Navicat Premium Data Transfer

 Source Server         : s1.stonedt.com6201
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : s1.stonedt.com
 Source Database       : stonedt_portal

 Target Server Type    : MySQL
 Target Server Version : 50733
 File Encoding         : utf-8

 Date: 02/14/2022 17:00:35 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `data_favorite`
-- ----------------------------
DROP TABLE IF EXISTS `data_favorite`;
CREATE TABLE `data_favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `article_public_id` varchar(50) DEFAULT NULL COMMENT '文章唯一id',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `user_id` bigint(50) NOT NULL COMMENT '用户id',
  `favoritetime` datetime DEFAULT NULL COMMENT '收藏时间',
  `status` int(11) DEFAULT '1' COMMENT '状态1.正常 2.删除',
  `emotionalIndex` int(11) DEFAULT NULL COMMENT '情感 1正面 2中性 3负面',
  `projectid` bigint(20) DEFAULT NULL COMMENT '方案id',
  `groupid` bigint(20) DEFAULT NULL COMMENT '方案组id',
  `source_name` varchar(255) DEFAULT NULL COMMENT '来源网站',
  PRIMARY KEY (`id`,`user_id`),
  UNIQUE KEY `article_public_id` (`article_public_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COMMENT='文章收藏表';

-- ----------------------------
--  Table structure for `full_menu`
-- ----------------------------
DROP TABLE IF EXISTS `full_menu`;
CREATE TABLE `full_menu` (
  `only_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `id` int(11) NOT NULL COMMENT '唯一id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(1) DEFAULT NULL COMMENT '1一级分类2二级分类',
  `name` varchar(255) DEFAULT NULL COMMENT '分类名称',
  `value` varchar(255) DEFAULT NULL COMMENT '传值（一级分类为空）',
  `type_one_id` int(11) DEFAULT NULL COMMENT '所属一级分类id（一级分类为空）',
  `type_two_id` int(11) DEFAULT NULL COMMENT '所属二级分类id',
  `icon` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '一级分类图标',
  `is_show` tinyint(2) DEFAULT '0' COMMENT '是否展示，0展示，1不展示',
  `is_default` tinyint(2) DEFAULT '0' COMMENT '默认菜单列表，0是、1不是',
  PRIMARY KEY (`only_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `full_polymerization`
-- ----------------------------
DROP TABLE IF EXISTS `full_polymerization`;
CREATE TABLE `full_polymerization` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` tinyint(2) DEFAULT '0' COMMENT '聚合菜单分类 0为系统默认分类、1为用户分类',
  `name` varchar(255) DEFAULT NULL COMMENT '聚合菜单名称',
  `value` varchar(255) DEFAULT NULL COMMENT '一级菜单id，多个用,间隔',
  `icon` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '聚合菜单图标',
  `is_show` tinyint(2) DEFAULT '0' COMMENT '是否展示，0展示，1不展示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `full_type`
-- ----------------------------
DROP TABLE IF EXISTS `full_type`;
CREATE TABLE `full_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(1) DEFAULT NULL COMMENT '1一级分类2二级分类',
  `name` varchar(255) DEFAULT NULL COMMENT '分类名称',
  `value` varchar(255) DEFAULT NULL COMMENT '传值（一级分类为空）',
  `type_one_id` int(11) DEFAULT NULL COMMENT '所属一级分类id（一级分类为空）',
  `type_two_id` int(11) DEFAULT NULL COMMENT '所属二级分类id',
  `icon` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '一级分类图标',
  `is_show` tinyint(2) DEFAULT '0' COMMENT '是否展示，0展示，1不展示',
  `is_default` tinyint(2) DEFAULT '0' COMMENT '默认菜单列表，0是、1不是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `full_word`
-- ----------------------------
DROP TABLE IF EXISTS `full_word`;
CREATE TABLE `full_word` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `create_time` datetime DEFAULT NULL COMMENT '记录时间',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `search_word` varchar(255) DEFAULT NULL COMMENT '搜索词',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2093 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `module`
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `module_id` int(11) DEFAULT NULL COMMENT '模块id',
  `module_name` varchar(255) DEFAULT NULL COMMENT '模块名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `module_method`
-- ----------------------------
DROP TABLE IF EXISTS `module_method`;
CREATE TABLE `module_method` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `module_id` int(11) DEFAULT NULL COMMENT '模块id',
  `method_name` varchar(255) DEFAULT NULL COMMENT '方法名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `monitor_analysis`
-- ----------------------------
DROP TABLE IF EXISTS `monitor_analysis`;
CREATE TABLE `monitor_analysis` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `analysis_id` bigint(20) DEFAULT NULL COMMENT '监测分析公共id',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案id',
  `time_period` int(1) DEFAULT NULL COMMENT '时间周期',
  `data_overview` longtext COMMENT '数据概览',
  `emotional_proportion` longtext COMMENT '情感占比',
  `plan_word_hit` longtext COMMENT '方案命中主体词',
  `keyword_emotion_trend` longtext COMMENT '关键词情感分析走势',
  `hot_event_ranking` longtext COMMENT '热点事件排名',
  `highword_cloud` longtext COMMENT '关键词高频分布统计',
  `keyword_index` longtext COMMENT '高频词指数',
  `media_activity_analysis` longtext COMMENT '媒体活跃度分析',
  `hot_spot_ranking` longtext COMMENT '热点地区排名',
  `data_source_distribution` longtext COMMENT '数据来源分布',
  `data_source_analysis` longtext COMMENT '数据来源分析',
  `keyword_exposure_rank` longtext COMMENT '关键词曝光度环比排行',
  `selfmedia_volume_rank` longtext COMMENT '自媒体渠道声量排名',
  `popular_event` longtext COMMENT '热点事件json',
  `popular_information` longtext COMMENT '热门资讯数据json',
  `relative_news` longtext COMMENT '相关资讯json',
  `hot_company` longtext COMMENT '热点公司json',
  `hot_people` longtext COMMENT '热点人物json',
  `hot_spot` longtext COMMENT '热点地区json',
  `keyword_emotion_statistical` longtext COMMENT '关键词情感分析数据统计分布json',
  `ner` longtext COMMENT '实体',
  `category_rank` longtext COMMENT '分类趋势',
  `industrial_distribution` longtext COMMENT '行业分布',
  `event_statistics` longtext COMMENT '事件统计',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_id` (`project_id`,`time_period`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3602 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `monitor_analysis_copy1`
-- ----------------------------
DROP TABLE IF EXISTS `monitor_analysis_copy1`;
CREATE TABLE `monitor_analysis_copy1` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `analysis_id` bigint(20) DEFAULT NULL COMMENT '监测分析公共id',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案id',
  `time_period` int(1) DEFAULT NULL COMMENT '时间周期',
  `data_overview` longtext COMMENT '数据概览',
  `emotional_proportion` longtext COMMENT '情感占比',
  `plan_word_hit` longtext COMMENT '方案命中主体词',
  `keyword_emotion_trend` longtext COMMENT '关键词情感分析走势',
  `hot_event_ranking` longtext COMMENT '热点事件排名',
  `highword_cloud` longtext COMMENT '关键词高频分布统计',
  `keyword_index` longtext COMMENT '高频词指数',
  `media_activity_analysis` longtext COMMENT '媒体活跃度分析',
  `hot_spot_ranking` longtext COMMENT '热点地区排名',
  `data_source_distribution` longtext COMMENT '数据来源分布',
  `data_source_analysis` longtext COMMENT '数据来源分析',
  `keyword_exposure_rank` longtext COMMENT '关键词曝光度环比排行',
  `selfmedia_volume_rank` longtext COMMENT '自媒体渠道声量排名',
  `popular_event` longtext COMMENT '热点事件json',
  `popular_information` longtext COMMENT '热门资讯数据json',
  `relative_news` longtext COMMENT '相关资讯json',
  `hot_company` longtext COMMENT '热点公司json',
  `hot_people` longtext COMMENT '热点人物json',
  `hot_spot` longtext COMMENT '热点地区json',
  `keyword_emotion_statistical` longtext COMMENT '关键词情感分析数据统计分布json',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_id` (`project_id`,`time_period`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `opinion_condition`
-- ----------------------------
DROP TABLE IF EXISTS `opinion_condition`;
CREATE TABLE `opinion_condition` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `opinion_condition_id` bigint(20) DEFAULT NULL COMMENT '偏好设置公共id',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案id',
  `time` int(1) DEFAULT NULL COMMENT '时间范围(1:24小时，2:昨天，3:今天，4:3天，5：7天，6：15天，7：30天，8自定义)',
  `precise` int(1) DEFAULT NULL COMMENT '精准筛选（0：关闭：1打开）',
  `emotion` varchar(255) DEFAULT NULL COMMENT '情感属性（1：正面 2：中性 3：负面）\r\n可多选，英文逗号分隔',
  `similar` int(1) DEFAULT NULL COMMENT '相似文章(0:取消合并 1：合并文章)',
  `sort` int(1) DEFAULT NULL COMMENT '信息排序（1：时间降序 2：时间升序 3：相似数倒序）',
  `matchs` int(1) DEFAULT NULL COMMENT '匹配方式（1：全文 2：标题 3：正文）',
  `times` varchar(255) DEFAULT NULL COMMENT '自定义时间',
  `timee` varchar(255) DEFAULT NULL,
  `classify` varchar(255) DEFAULT '[0]' COMMENT '数据来源',
  `websitename` varchar(255) DEFAULT '' COMMENT '网站名称',
  `author` varchar(255) DEFAULT NULL COMMENT '作者名称',
  `organization` varchar(255) DEFAULT NULL COMMENT '涉及机构',
  `categorylable` varchar(255) DEFAULT NULL COMMENT '文章分类',
  `enterprisetype` varchar(255) DEFAULT NULL COMMENT '涉及企业',
  `hightechtype` varchar(255) DEFAULT NULL COMMENT '高科技型企业',
  `policylableflag` varchar(255) DEFAULT NULL COMMENT '涉及政策',
  `datasource_type` varchar(255) DEFAULT NULL COMMENT '数据品类',
  `eventIndex` varchar(255) DEFAULT NULL COMMENT '涉及事件',
  `industryIndex` varchar(255) DEFAULT NULL COMMENT '涉及行业',
  `province` varchar(255) DEFAULT NULL COMMENT '涉及省份',
  `city` varchar(255) DEFAULT NULL COMMENT '涉及城市',
  PRIMARY KEY (`id`),
  UNIQUE KEY `opinion_condition_id` (`opinion_condition_id`) USING BTREE,
  UNIQUE KEY `project_id` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=926 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `organization`
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `organization_id` bigint(20) DEFAULT NULL COMMENT '机构公共id',
  `organization_short` varchar(255) DEFAULT NULL COMMENT '机构简称',
  `organization_name` varchar(255) DEFAULT NULL COMMENT '机构名全称',
  `organization_type` int(1) DEFAULT '1' COMMENT '机构类型（1机构、2个人）',
  `term_of_validity` datetime DEFAULT NULL COMMENT '有效期',
  `status` int(1) DEFAULT '1' COMMENT '状态（1代表正常 2代表注销）',
  `organization_code` varchar(255) DEFAULT NULL COMMENT '组织代码',
  `logo_url` varchar(255) DEFAULT NULL COMMENT 'logo地址',
  `system_title` varchar(255) DEFAULT NULL COMMENT '系统名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `project`
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案公共id',
  `project_name` varchar(255) DEFAULT NULL COMMENT '方案名',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `project_type` int(1) DEFAULT '1' COMMENT '方案类型（普通1，高级2）',
  `project_description` varchar(255) DEFAULT NULL COMMENT '方案描述',
  `subject_word` longtext COMMENT '主体词',
  `character_word` longtext COMMENT '人物词',
  `event_word` longtext COMMENT '事件词',
  `regional_word` longtext COMMENT '地域词',
  `stop_word` longtext COMMENT '屏蔽词',
  `del_status` int(1) DEFAULT '0' COMMENT '软删除（0：否 1：是）',
  `group_id` bigint(20) DEFAULT NULL COMMENT '方案组id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=962 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `project_task`
-- ----------------------------
DROP TABLE IF EXISTS `project_task`;
CREATE TABLE `project_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `project_type` int(1) DEFAULT NULL,
  `subject_word` text,
  `regional_word` text,
  `character_word` text,
  `event_word` text,
  `stop_word` text,
  `analysis_flag` int(1) DEFAULT '0',
  `volume_flag` int(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_id` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=777 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `publicoption_detail`
-- ----------------------------
DROP TABLE IF EXISTS `publicoption_detail`;
CREATE TABLE `publicoption_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `publicoption_id` int(11) DEFAULT NULL COMMENT '研判报告id',
  `back_analysis` text COMMENT '溯源分析',
  `event_context` longtext COMMENT '事件脉络',
  `event_trace` longtext COMMENT '事件跟踪',
  `hot_analysis` longtext COMMENT '热点分析',
  `netizens_analysis` longtext COMMENT '重点网民分析',
  `statistics` longtext COMMENT '统计',
  `propagation_analysis` longtext COMMENT '传播分析',
  `thematic_analysis` longtext COMMENT '专题分析',
  `unscramble_content` text COMMENT '内容解读',
  `create_time` datetime DEFAULT NULL COMMENT '生成时间',
  `detail_status` int(1) DEFAULT '0' COMMENT '状态值（暂时没用）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `publicoption_id` (`publicoption_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COMMENT='舆情研判任务详情表';

-- ----------------------------
--  Table structure for `publicoptionevent`
-- ----------------------------
DROP TABLE IF EXISTS `publicoptionevent`;
CREATE TABLE `publicoptionevent` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `eventname` varchar(255) DEFAULT '' COMMENT '任务名称',
  `eventkeywords` varchar(255) DEFAULT '' COMMENT '任务关键词',
  `eventstarttime` datetime DEFAULT NULL COMMENT '任务开始时间',
  `eventendtime` datetime DEFAULT NULL COMMENT '任务结束时间',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `status` int(11) DEFAULT '2' COMMENT '1.创建失败2.正在创建3.创建成功',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `user_id` bigint(50) DEFAULT NULL COMMENT '用户id',
  `eventstopwords` varchar(255) DEFAULT NULL COMMENT '屏蔽词',
  `isdelete` int(11) DEFAULT '1' COMMENT '是否删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='舆情研判任务表';

-- ----------------------------
--  Table structure for `read_sign`
-- ----------------------------
DROP TABLE IF EXISTS `read_sign`;
CREATE TABLE `read_sign` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `article_id` varchar(255) DEFAULT NULL COMMENT '文章ID',
  `user_id` varchar(255) DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(11) DEFAULT NULL COMMENT '新增字段',
  `str` varchar(255) DEFAULT NULL COMMENT '新增字段',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`article_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COMMENT='已读标记表';

-- ----------------------------
--  Table structure for `report_custom`
-- ----------------------------
DROP TABLE IF EXISTS `report_custom`;
CREATE TABLE `report_custom` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `report_id` bigint(20) DEFAULT NULL COMMENT '报告公共id',
  `report_name` varchar(255) DEFAULT NULL COMMENT '报告名称',
  `report_type` int(1) DEFAULT NULL COMMENT '报告类型（1:日报，2:周报，3:月报）',
  `report_starttime` datetime DEFAULT NULL COMMENT '报告周期开始时间',
  `report_endtime` datetime DEFAULT NULL COMMENT '报告周期结束时间',
  `report_status` int(1) DEFAULT '1' COMMENT '报告状态（0：已生成任务，1：正在编制，2:编制成功3：编制失败）',
  `report_topping` int(1) DEFAULT '0' COMMENT '报告是否置顶(0:未置顶，1:置顶)，默认0',
  `report_time` datetime DEFAULT NULL COMMENT '报告编制时间',
  `del_status` int(1) DEFAULT '0' COMMENT '软删除（0:否，1：是）',
  `number_period` int(11) DEFAULT NULL COMMENT '期数',
  `processes` int(11) DEFAULT NULL COMMENT '生成进度',
  `module_sum` int(11) DEFAULT NULL COMMENT '模板组件数量',
  `template_id` bigint(20) DEFAULT NULL COMMENT '报告模板id',
  `template_info` varchar(255) DEFAULT NULL COMMENT '模板信息',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案id',
  `keyword` text COMMENT '报告关键词',
  `stopword` text COMMENT '报告屏蔽词',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_id` (`report_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=279535 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `report_detail`
-- ----------------------------
DROP TABLE IF EXISTS `report_detail`;
CREATE TABLE `report_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `report_id` bigint(20) DEFAULT NULL COMMENT '报告id',
  `data_overview` longtext COMMENT '1数据概览逻辑处理 2、3资讯和社交数据逻辑处理',
  `emotion_analysis` longtext COMMENT '4、情感分析',
  `hot_event_ranking` longtext COMMENT '5、热点事件排名 ',
  `media_activity_analysis` longtext COMMENT '10、媒体活跃度分析',
  `self_media_ranking` longtext COMMENT '13、自媒体热度排名',
  `high_word_index` longtext COMMENT '14、高频词指数',
  `hot_spot_ranking` longtext COMMENT '15、热点地区排名',
  `netizen_word_cloud` longtext COMMENT '11、网民高频词云',
  `media_cord_cloud` longtext COMMENT '12、媒体高频词云',
  `hot_people` longtext COMMENT '6、热点人物',
  `hot_spots` longtext COMMENT '7、热点地区 ',
  `topic_clustering` longtext COMMENT '8、主题观点聚类分析',
  `social_v_ranking` longtext COMMENT '9、社交网络大V热度排名',
  `highword_cloud` longtext COMMENT '关键词高频分布统计',
  `keyword_index` longtext COMMENT '高频词指数',
  `highword_cloud_index` longtext,
  `ner` longtext COMMENT '实体',
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_id` (`report_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6351 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `report_detail_copy1`
-- ----------------------------
DROP TABLE IF EXISTS `report_detail_copy1`;
CREATE TABLE `report_detail_copy1` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `report_id` bigint(20) DEFAULT NULL COMMENT '报告id',
  `data_overview` longtext COMMENT '1数据概览逻辑处理 2、3资讯和社交数据逻辑处理',
  `emotion_analysis` longtext COMMENT '4、情感分析',
  `hot_event_ranking` longtext COMMENT '5、热点事件排名 ',
  `media_activity_analysis` longtext COMMENT '10、媒体活跃度分析',
  `self_media_ranking` longtext COMMENT '13、自媒体热度排名',
  `high_word_index` longtext COMMENT '14、高频词指数',
  `hot_spot_ranking` longtext COMMENT '15、热点地区排名',
  `netizen_word_cloud` longtext COMMENT '11、网民高频词云',
  `media_cord_cloud` longtext COMMENT '12、媒体高频词云',
  `hot_people` longtext COMMENT '6、热点人物',
  `hot_spots` longtext COMMENT '7、热点地区 ',
  `topic_clustering` longtext COMMENT '8、主题观点聚类分析',
  `social_v_ranking` longtext COMMENT '9、社交网络大V热度排名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `report_id` (`report_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `solution_group`
-- ----------------------------
DROP TABLE IF EXISTS `solution_group`;
CREATE TABLE `solution_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `group_id` bigint(20) DEFAULT NULL COMMENT '方案组公共id',
  `group_name` varchar(255) DEFAULT NULL COMMENT '方案组名称',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `del_status` int(1) DEFAULT '0' COMMENT '软删除（0：否 1：是）',
  PRIMARY KEY (`id`),
  KEY `group_name` (`group_name`,`user_id`,`del_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=556 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `submodule`
-- ----------------------------
DROP TABLE IF EXISTS `submodule`;
CREATE TABLE `submodule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `submodule_id` int(11) DEFAULT NULL COMMENT '子模块id',
  `submodule_name` varchar(255) DEFAULT NULL COMMENT '子模块名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `module_id` int(11) DEFAULT NULL COMMENT '模块id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `synthesize`
-- ----------------------------
DROP TABLE IF EXISTS `synthesize`;
CREATE TABLE `synthesize` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `cteate_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `report_day` text COMMENT '日报',
  `report_week` text COMMENT '周报',
  `hot_weibo` longtext COMMENT '微博热点',
  `hot_all` longtext COMMENT '热点事件',
  `hot_search_terms` longtext COMMENT '热点搜索词',
  `leaders_PO` longtext COMMENT '领导人舆情',
  `today_PO_status` longtext COMMENT '今日舆情情况',
  `warning_PO` longtext COMMENT '预警舆情展示',
  `upload_PO` longtext COMMENT '个人信息报送',
  `project_PO_status` longtext COMMENT '专题展示',
  `online` longtext COMMENT '系统当前在线统计',
  `push_PO` longtext COMMENT ' 推送舆情',
  `reprint_PO` longtext COMMENT '转载查询',
  `collection_po` longtext COMMENT '收藏贴文',
  `hot_wechat` longtext COMMENT '微信热点',
  `hot_douyin` longtext COMMENT '抖音',
  `hot_bilibili` longtext COMMENT 'bilibili',
  `hot_tecentvedio` longtext COMMENT '腾讯视频',
  `hot_36kr` longtext,
  `hot_finaceData` longtext,
  `hot_policydata` longtext,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='综合看板表';

-- ----------------------------
--  Table structure for `systemlog`
-- ----------------------------
DROP TABLE IF EXISTS `systemlog`;
CREATE TABLE `systemlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `user_browser` varchar(255) DEFAULT NULL COMMENT '用户浏览器',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `user_browser_version` varchar(255) DEFAULT NULL COMMENT '用户浏览器版本',
  `operatingSystem` varchar(255) DEFAULT NULL COMMENT '操作系统',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `loginip` varchar(50) DEFAULT NULL COMMENT '登陆ip',
  `module` varchar(50) DEFAULT NULL COMMENT '模块',
  `submodule` varchar(50) DEFAULT NULL COMMENT '子模块',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13628 DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `telephone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `end_login_time` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `status` int(1) DEFAULT '1' COMMENT '状态（1代表正常 2代表注销）',
  `username` varchar(255) DEFAULT NULL COMMENT '用户姓名',
  `wechat_number` varchar(255) DEFAULT NULL COMMENT '微信号',
  `openid` varchar(255) DEFAULT NULL COMMENT 'openid',
  `login_count` int(11) DEFAULT '0' COMMENT '登录次数',
  `identity` int(1) DEFAULT NULL COMMENT '身份标识',
  `organization_id` bigint(20) DEFAULT NULL COMMENT '所属机构id',
  `user_type` int(1) DEFAULT NULL COMMENT '用户类型(0普通用户,1渠道商,2渠道专员,3管理员)',
  `user_level` int(1) DEFAULT NULL COMMENT '用户等级',
  `wechatflag` int(1) DEFAULT NULL COMMENT '微信绑定状态（1代表绑定 0代表捆绑）',
  `is_online` int(1) DEFAULT NULL COMMENT '是否在线',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_apply`
-- ----------------------------
DROP TABLE IF EXISTS `user_apply`;
CREATE TABLE `user_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openid` varchar(50) DEFAULT NULL COMMENT 'openid',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `telephone` varchar(255) DEFAULT NULL COMMENT '手机号码',
  `industry` varchar(255) DEFAULT NULL COMMENT '行业',
  `company` varchar(255) DEFAULT NULL COMMENT '公司',
  `applytime` datetime DEFAULT NULL,
  `dealstatus` int(1) DEFAULT '0' COMMENT '处理状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_log`
-- ----------------------------
DROP TABLE IF EXISTS `user_log`;
CREATE TABLE `user_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `article_public_id` varchar(50) DEFAULT NULL COMMENT '日志id',
  `method_name` varchar(50) DEFAULT NULL COMMENT '方法名称',
  `module_name` varchar(50) DEFAULT NULL COMMENT '模块名称',
  `submodule_name` varchar(255) DEFAULT NULL COMMENT '子模块名称',
  `times` datetime DEFAULT NULL COMMENT '开始时间',
  `timee` datetime DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名称',
  `organization_id` bigint(20) DEFAULT NULL COMMENT '组织id',
  `organization_name` varchar(255) DEFAULT NULL COMMENT '组织名称',
  `status` int(1) DEFAULT NULL COMMENT '用户状态',
  `parameters` text COMMENT '请求参数',
  `class_name` varchar(255) DEFAULT NULL COMMENT '类名',
  `module_id` int(11) DEFAULT NULL COMMENT '模块id',
  `submodule_id` int(11) DEFAULT NULL COMMENT '子模块id',
  `operation` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `volume_monitor`
-- ----------------------------
DROP TABLE IF EXISTS `volume_monitor`;
CREATE TABLE `volume_monitor` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `volume_monitor_id` bigint(20) DEFAULT NULL COMMENT '声量监测公共id',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案id',
  `time_period` int(1) DEFAULT NULL COMMENT '时间周期(1:24h 2:3d 3:7d 4:15d)',
  `keyword_emotion_statistical` longtext COMMENT '关键词情感分析数据统计分布',
  `keyword_source_distribution` longtext COMMENT '关键词数据来源分布',
  `keyword_news_rank` longtext COMMENT '关键词资讯数量排名',
  `topic_cluster_analysis` longtext COMMENT '主题观点聚类分析',
  `keyword_emotion_trend` longtext COMMENT '关键词情感分析数据走势',
  `highword_cloud` longtext COMMENT '关键词高频分布统计',
  `keyword_exposure_rank` longtext COMMENT '关键词曝光度环比排行',
  `keyword_correlation_news` longtext COMMENT '热点内容聚类分析排名',
  `user_portrait_label` longtext COMMENT '用户画像标签',
  `social_user_volume_rank` longtext COMMENT '社交网络用户声量排名',
  `media_user_volume_rank` longtext COMMENT '自媒体用户声量排名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_id` (`project_id`,`time_period`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=209516 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `warning_article`
-- ----------------------------
DROP TABLE IF EXISTS `warning_article`;
CREATE TABLE `warning_article` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '记录创建时间',
  `warning_article_id` bigint(20) DEFAULT NULL COMMENT '预警内容公共id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `popup_id` bigint(20) DEFAULT NULL COMMENT '预警弹窗公共id',
  `popup_content` varchar(255) DEFAULT NULL COMMENT '预警弹窗内容',
  `popup_time` datetime DEFAULT NULL COMMENT '预警弹窗时间',
  `article_id` varchar(200) DEFAULT NULL COMMENT '内容id',
  `article_time` datetime DEFAULT NULL COMMENT '内容时间',
  `article_title` varchar(255) DEFAULT NULL COMMENT '内容标题',
  `article_emotion` int(1) DEFAULT NULL COMMENT '内容情感(1:正面，2:中性，3:负面)',
  `status` int(1) DEFAULT '0' COMMENT '状态 ( 0:未弹窗，1:已弹窗 )',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案id',
  `read_status` int(1) DEFAULT NULL COMMENT '阅读状态 ( 0:未读，1:已读 )',
  `article_detail` text COMMENT '资讯补充字段json',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`project_id`,`article_id`) USING BTREE,
  UNIQUE KEY `popup_id` (`popup_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4023702 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `warning_setting`
-- ----------------------------
DROP TABLE IF EXISTS `warning_setting`;
CREATE TABLE `warning_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `warning_setting_id` bigint(20) DEFAULT NULL COMMENT '预警设置公共id',
  `project_id` bigint(20) DEFAULT NULL COMMENT '方案id',
  `warning_status` int(255) DEFAULT NULL COMMENT '预警开关 ( 0:关，1:开 )',
  `warning_name` varchar(255) DEFAULT NULL COMMENT '预警名称',
  `warning_word` varchar(255) DEFAULT NULL COMMENT '预警词(英文逗号分隔)',
  `warning_classify` varchar(255) DEFAULT NULL COMMENT '来源类型(1-11)(英文逗号分隔)',
  `warning_content` int(1) DEFAULT NULL COMMENT '预警内容(0:全部 1:敏感)',
  `warning_similar` int(1) DEFAULT NULL COMMENT '相似文章合并（0：取消合并 1：合并）',
  `warning_match` int(1) DEFAULT NULL COMMENT '匹配方式（1：全文 2：标题 3：正文）',
  `warning_deduplication` int(1) DEFAULT NULL COMMENT '预警去重（0：关闭 1：开启）',
  `warning_source` varchar(255) DEFAULT NULL COMMENT '预警来源json（[type]1:系统推送 2：邮箱推送 [email]:邮箱地址，可为空）',
  `warning_receive_time` varchar(255) DEFAULT NULL COMMENT '接收时间json [start]:开始时间 [end]:结束时间',
  `weekend_warning` int(1) DEFAULT NULL COMMENT '周末预警（0：关闭 1：开启）',
  `warning_interval` varchar(255) DEFAULT NULL COMMENT '预警间隔json（[type]1:实时预警 2：定时预警 [time]:时间，可为空）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_id` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=917 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `wechatqrcode`
-- ----------------------------
DROP TABLE IF EXISTS `wechatqrcode`;
CREATE TABLE `wechatqrcode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `telephone` varchar(50) DEFAULT NULL COMMENT '手机号码',
  `ticket` varchar(255) DEFAULT NULL COMMENT '二维码地址',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniquestr` (`telephone`)
) ENGINE=InnoDB AUTO_INCREMENT=32124 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
