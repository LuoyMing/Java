package com.seud.fyb.article.bean;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 执行任务参数封装器
 * 
 * @author Administrator
 *
 */
public class FlowParamsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> params;

	private Map<String, Object> variables;

	private Map<String, Object> localVariables;

	public FlowParamsBean() {
		this.params = new HashMap<String, Object>();
		this.variables = new HashMap<String, Object>();
		this.localVariables = new HashMap<String, Object>();
	}
	
	public static FlowParamsBean getInstance() {
		return new FlowParamsBean();
	}

	public FlowParamsBean addParams(String key, Object value) {
		if ("variables".equals(key) || "localVariables".equals(key)) {
			throw new IllegalArgumentException("不合法的key: variables和localVariables为保留变量 ");
		}
		params.put(key, value);
		return this;
	}

	public FlowParamsBean addVariables(String key, Object value) {
		if (!params.containsKey("variables")) {
			params.put("variables", variables);
		}
		variables.put(key, value);
		return this;
	}

	public FlowParamsBean addlocalVariables(String key, Object value) {
		if (!params.containsKey("localVariables")) {
			params.put("localVariables", localVariables);
		}
		localVariables.put(key, value);
		return this;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public FlowParamsBean addParams(Map<String, Object> params) {
		this.params.putAll(params);
		return this;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public FlowParamsBean addVariables(Map<String, Object> variables) {
		this.variables.putAll(variables);
		return this;
	}

	public Map<String, Object> getLocalVariables() {
		return localVariables;
	}

	public FlowParamsBean addLocalVariables(Map<String, Object> localVariables) {
		this.localVariables.putAll(localVariables);
		return this;
	}

	public String toJSONString() {
		try {
			return JSONObject.toJSONString(params);
		} finally {
			// 释放
			params.clear();
			variables.clear();
			localVariables.clear();
		}
	}

}
