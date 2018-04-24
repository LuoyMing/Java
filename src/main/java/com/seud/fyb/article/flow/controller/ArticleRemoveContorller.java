package com.seud.fyb.article.flow.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.article.flow.service.IArticleBusiService;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;

@RestController
public class ArticleRemoveContorller {
	@Autowired
	@Qualifier("CommonArticleServiceImpl")
	private IArticleBusiService articleRemoveBusiService;

	@RequestMapping("/startArticleRemove")
	public ResponseBodyInfo<Map<String,Object>> startArticleRemove(@RequestBody Map<String,Object> params){
		return ResultInfo.successForObj(articleRemoveBusiService.start(params));
	}

	@RequestMapping("/auditRemove")
	public ResponseBodyInfo<Map<String,Object>> auditRemove(@RequestBody Map<String,Object> params){
		return ResultInfo.successForObj(articleRemoveBusiService.audit(params));
	}
}
