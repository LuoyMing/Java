package com.seud.fyb.article.flow.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.article.flow.service.IArticleBusiService;
import com.seud.fyb.article.flow.service.IArticleContentProduceBusiService;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;

@RestController
public class ArticlePublishContorller {
	@Autowired
	@Qualifier("CommonArticleServiceImpl")
	private IArticleBusiService CommonArticleServiceImpl;

	@RequestMapping("/startArticlePublish")
	public ResponseBodyInfo<Map<String,Object>> startArticlePublish(@RequestBody Map<String,Object> params){
		return ResultInfo.successForObj(CommonArticleServiceImpl.start(params));
	}

	@RequestMapping("/auditPublish")
	public ResponseBodyInfo<Map<String,Object>> auditPublish(@RequestBody Map<String,Object> params){
		return ResultInfo.successForObj(CommonArticleServiceImpl.audit(params));
	}
}
