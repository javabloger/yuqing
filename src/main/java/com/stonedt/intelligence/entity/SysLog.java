package com.stonedt.intelligence.entity;

import java.io.Serializable;

/**
 * description: SysLog <br>
 * date: 2020/5/9 17:05 <br>
 * author: huajiancheng <br>
 * version: 1.0 <br>
 */
public class SysLog implements Serializable {
    private String operation; // 请求名称
    private String type; // 注解上的操作类型
    private String article_public_id;
    private Long user_id;
    private String module_name;
    private String submodule_name; // 子模块名称
    private String method_name;
    private String times;
    private String timee;
    private String create_time;
    private String username;
    private String organization_id;
    private String organization_name;
    private String parameters;
    private String class_name;
    private Integer module_id;
    private Integer submodule_id;
    private Integer status;

    public SysLog() {
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getArticle_public_id() {
        return article_public_id;
    }

    public void setArticle_public_id(String article_public_id) {
        this.article_public_id = article_public_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getTimee() {
        return timee;
    }

    public void setTimee(String timee) {
        this.timee = timee;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(String organization_id) {
        this.organization_id = organization_id;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public Integer getModule_id() {
        return module_id;
    }

    public void setModule_id(Integer module_id) {
        this.module_id = module_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSubmodule_name() {
        return submodule_name;
    }

    public void setSubmodule_name(String submodule_name) {
        this.submodule_name = submodule_name;
    }

    public Integer getSubmodule_id() {
        return submodule_id;
    }

    public void setSubmodule_id(Integer submodule_id) {
        this.submodule_id = submodule_id;
    }
}
