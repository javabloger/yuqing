# 开源舆情 【数据处理】部分技术架构说明文档
（待续，未完...）

## 简述

经历了很多版本的迭代升级，期间采用过机器学习、深度学习、tensorflow 和 PaddlePaddle，经历大量的开发测试与项目实战经验。


## 开源技术栈
(开源技术清单)


## 总体技术架构

(思维导图)


## 数据总线

  通过SmarterAPI与Elasticsearch对接，将内部整套数据处理流程完成后对外输出。


## 数据去重
-   URL去重
   采用了redis集群


-   内容去重
   采用Elasticsearch内部的查询将文章标题一样的内容检索过滤掉。

## 数据清洗
自动提取字段，标题、正文、时间、作者、来源 等。


## 数据标记

### 实体识别
   HaNLP

### 情感分析    
   百度飞桨

### 高频词分词
-   自研算法

### 文本分类

-  由清华大学自然语言处理实验室推出的 [THUCTC(THU Chinese Text Classification)](http://http://thuctc.thunlp.org/)

### 相似文章

  将文章通过“海明距离”算法生成加密串码存储在clickhouse集群中，通过clickhouse距离查询方法实现，文章相似度聚类。

### 事件分类

  自研算法

### 行业分类

   自研算法

## 数据存储

分为多个部分存储，MySQL、redis、kafak、Elasticsearch、Mongodb、clickhouse

## 数据运维
###  数据清理
   
需要定期对  Elasticsearch、Mongodb 中存储的数据删除，同时还要将Mongodb中的表删除，否则磁盘空间容量不会减少。

###  数据备份




