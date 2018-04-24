package com.seud.fyb.article.flow.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.article.flow.service.ArticleGroupAuditBusiService;
import com.seud.fyb.framework.annotation.Method;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
@RestController
public class ArticleGroupAuditBusiController{
	
	@Autowired
	private ArticleGroupAuditBusiService ArticleGroupAuditAuditBusiService;
	
	@RequestMapping(value="startCallBack_ArticleGroupAudit")
	@Method(desc="启动流程业务方法")
	public ResponseBodyInfo<Map<String, Object>> startCallBack_ArticleGroupAudit(@RequestBody Map<String, Object> params) {
		try{
			Map<String, Object> returnParam=ArticleGroupAuditAuditBusiService.startCallBack(params);
			return ResultInfo.successForObj(returnParam);
		}catch(SeudRuntimeException e){
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		}
	}
	
	@RequestMapping(value="endCallBack_ArticleGroupAudit")
	@Method(desc="结束流程业务方法")
	public ResponseBodyInfo<Map<String, Object>> endCallBack_ArticleGroupAudit(@RequestBody Map<String, Object> params) {
		try{
			return ResultInfo.successForObj(ArticleGroupAuditAuditBusiService.endCallBack(params));
		}catch(SeudRuntimeException e){
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		}
	}
	
	@RequestMapping(value="firstCallBack_ArticleGroupAudit")
	@Method(desc="第一级审核业务方法")
	public ResponseBodyInfo<Map<String, Object>> firstCallBack_ArticleGroupAudit(@RequestBody Map<String, Object> params) {
		try{
			return ResultInfo.successForObj(ArticleGroupAuditAuditBusiService.firstCallBack(params));
		}catch(SeudRuntimeException e){
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		}
	}
}
