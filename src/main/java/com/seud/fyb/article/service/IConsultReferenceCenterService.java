package com.seud.fyb.article.service;

import java.util.List;

import com.seud.fyb.feignclient.article.model.bean.ProtocolBean;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.framework.bean.PageInfo;

/**
 * @ClassName: ArticleUtilTemplateService
 * @Description: 咨询中心展示设置管理
 * @author luoyiming
 * @date 2017年3月8日 下午5:48:07
 * 
 */
public interface IConsultReferenceCenterService {

	/**
	 * 获取顶部轮播数据
	 * @param appType 
	 * 
	 * @param parentId
	 * @return
	 */
	String getTopTemplateJson(String appType);
	
	/**
	 * 资讯中心版面数据
	 * @param appType 
	 * 
	 * @param parentId
	 * @return
	 */
	String getTemplateJson(String appType);

	/**保存配置中心数据
	 * @param templateJson
	 * @param isApp
	 * @return
	 */
	boolean saveTemplateJson(String templateJson, String isApp);

	/**
	 * 获取协议内容
	 * @param appType
	 * @param isApp 
	 * @return
	 */
	ProtocolBean getProtocol(String specialCode, String isApp);

	/**保存首页轮播图数据
	 * @param templateJson
	 * @param isApp
	 * @return
	 */
	boolean saveTopTemplateJson(String templateJson, String isApp);
	
	PageInfo<ArticleKellegEO> listPageByKeywordKelleg(String keyword,Integer pageSize,Integer pageNo);

	boolean saveKelleg(ArticleKellegEO articleKellegEO);

	
}
