package com.seud.fyb.article.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.seud.fyb.article.dao.ICfgDictDAO;
import com.seud.fyb.article.service.IConsultContentService;
import com.seud.fyb.article.service.IConsultReferenceCenterService;
import com.seud.fyb.feignclient.article.model.bean.ProtocolBean;
import com.seud.fyb.feignclient.article.model.bean.TemplateModuleBean;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleKellegEnum;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.cache.redis.Prefix;
import com.seud.fyb.framework.cache.redis.RedisUtil;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.http.HttpHelper;
import com.seud.fyb.framework.utils.http.ResponseContent;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultReferenceCenterServiceImpl
 * @Description: 咨询中心展示设置管理
 * @author luoyiming
 * @date 2017年3月13日 下午5:54:13
 * 
 */
@Service("consultReferenceCenterService")
public class ConsultReferenceCenterServiceImpl implements IConsultReferenceCenterService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditOperationServiceImpl.class);
	
	public static final String TEMPLATE_SUFFIX = "_template_content";

	private static final String TEMPLATE_KEY = "consult_reference_template_json";
	private static final String APP_TEMPLATE_KEY = "consult_app_template_json";
	private static final String TOP_TEMPLATE_KEY = "top_consult_reference_template_json";
	private static final String TOP_APP_TEMPLATE_KEY = "top_consult_app_template_json";

	@Autowired
	private RedisUtil redisUtil;

	@Resource(name = "cfgDictDAO")
	private ICfgDictDAO cfgDictDAO;

	@Resource(name = "consultContentService")
	private IConsultContentService  consultContentService;
	
	@Override
	public String getTopTemplateJson(String appType) {
		try{
			String keyStr = TOP_TEMPLATE_KEY;
			if("true".equals(appType)){
				keyStr = TOP_APP_TEMPLATE_KEY;
			}
			String templateJson = null;
			try {
				templateJson = redisUtil.get(keyStr);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("资讯中心",e);
			}
			if (StringUtils.isEmpty(templateJson)) {
				ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, keyStr);
				if (null == dictEO) {
					templateJson = "";
				} else {
					templateJson = dictEO.getRelevanceData();
					try {
						redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, keyStr, templateJson);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("资讯中心",e);
					}
				}
			}
			return templateJson;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("资讯中心",e);
			throw new SeudRuntimeException("001","加载轮播数据异常");
		}
	}
	
	@Override
	public String getTemplateJson(String appType) {
		try{
			String keyStr = TEMPLATE_KEY;
			if("true".equals(appType)){
				keyStr = APP_TEMPLATE_KEY;
			}
			String templateJson = null;
			try {
				templateJson = redisUtil.get(keyStr);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("资讯中心",e);
			}
			if (StringUtils.isEmpty(templateJson)) {
				ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, keyStr);
				if (null == dictEO) {
					templateJson = "";
				} else {
					templateJson = dictEO.getRelevanceData();
					try {
						redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, keyStr, templateJson);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("资讯中心",e);
					}
				}
			}
			if(StringUtils.isEmpty(templateJson)){
				templateJson = "";
			}
			return templateJson;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("资讯中心",e);
			throw new SeudRuntimeException("001","加载资讯数据异常");
		}
	}

	/**
	 * (non-Javadoc) 保存资讯中心设置数据 1.保存到数据库 2.保存到redis
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean saveTemplateJson(String templateJson,String isApp) {
		try{
			ArticleKellegEO appDictEO = null;
			if(StringUtils.isNotEmpty(isApp)&&"true".equals(isApp)){
				appDictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, APP_TEMPLATE_KEY);
				if (null == appDictEO) {
					appDictEO = new ArticleKellegEO();
					appDictEO.setKeyCode(APP_TEMPLATE_KEY);
					appDictEO.setRelevanceData(templateJson);
					appDictEO.setType(ArticleKellegEnum.Deploy.toString());
					appDictEO.setRelativeDescription("app资讯中心"+ArticleKellegEnum.Deploy.name);
					cfgDictDAO.save(appDictEO);
				} else {
					appDictEO.setRelevanceData(templateJson);
					cfgDictDAO.update(appDictEO);
				}
			}else{
				ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, TEMPLATE_KEY);
				if (null == dictEO) {
					dictEO = new ArticleKellegEO();
					dictEO.setKeyCode(TEMPLATE_KEY);
					dictEO.setRelevanceData(templateJson);
					dictEO.setType(ArticleKellegEnum.Deploy.toString());
					dictEO.setRelativeDescription("pc资讯中心"+ArticleKellegEnum.Deploy.name);
					cfgDictDAO.save(dictEO);
				} else {
					dictEO.setRelevanceData(templateJson);
					cfgDictDAO.update(dictEO);
				}
			}
			if(StringUtils.isNotEmpty(isApp)&&"true".equals(isApp)){
				if (!redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, APP_TEMPLATE_KEY, templateJson)) {
					throw new SeudRuntimeException("001", "app咨询中心展示设置管理:数据存储失败。");
				}
			}else{
				if (!redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, TEMPLATE_KEY, templateJson)) {
					throw new SeudRuntimeException("001", "pc咨询中心展示设置管理:数据存储失败。");
				}
			}
			return true;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("资讯中心",e);
			throw new SeudRuntimeException("001","数据保存失败");
		}
	}
	
	
	/**
	 * 组装一份app其他模块数据  且保存（资讯中心数据）
	 * @param templateJson
	 */
	private void saveForAppJson(String templateJson) {
			String appJson = getTemplateJson("true");
			TemplateModuleBean appModuleBean = null;
			if(StringUtils.isNotEmpty(appJson)){
				appModuleBean = JSON.parseObject(appJson, TemplateModuleBean.class);
			}else{
				appModuleBean = new TemplateModuleBean();
			}
			TemplateModuleBean templateModuleBean = JSON.parseObject(templateJson, TemplateModuleBean.class);
			appModuleBean.setModules(templateModuleBean.getModules());
			saveTemplateJson(JSON.toJSONString(appModuleBean), "true");
	}

	/**保存首页轮播图数据
	 * @param templateJson
	 * @param isApp
	 * @return
	 */
	@Override
	public boolean saveTopTemplateJson(String templateJson,String isApp) {
		try{
			ArticleKellegEO appDictEO = null;
			if(StringUtils.isNotEmpty(isApp)&&"true".equals(isApp)){
				appDictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, TOP_APP_TEMPLATE_KEY);
				if (null == appDictEO) {
					appDictEO = new ArticleKellegEO();
					appDictEO.setKeyCode(TOP_APP_TEMPLATE_KEY);
					appDictEO.setRelevanceData(templateJson);
					appDictEO.setType(ArticleKellegEnum.Deploy.toString());
					appDictEO.setRelativeDescription("app首页轮播图"+ArticleKellegEnum.Deploy.name);
					cfgDictDAO.save(appDictEO);
				} else {
					appDictEO.setRelevanceData(templateJson);
					cfgDictDAO.update(appDictEO);
				}
			}else{
				ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, TOP_TEMPLATE_KEY);
				if (null == dictEO) {
					dictEO = new ArticleKellegEO();
					dictEO.setKeyCode(TOP_TEMPLATE_KEY);
					dictEO.setRelevanceData(templateJson);
					dictEO.setType(ArticleKellegEnum.Deploy.toString());
					dictEO.setRelativeDescription("pc首页轮播图"+ArticleKellegEnum.Deploy.name);
					cfgDictDAO.save(dictEO);
				} else {
					dictEO.setRelevanceData(templateJson);
					cfgDictDAO.update(dictEO);
				}
			}
			if(StringUtils.isNotEmpty(isApp)&&"true".equals(isApp)){
				if (!redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, TOP_APP_TEMPLATE_KEY, templateJson)) {
					throw new SeudRuntimeException("001", "app咨询中心展示设置管理:数据存储失败。");
				}
			}else{
				if (!redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, TOP_TEMPLATE_KEY, templateJson)) {
					throw new SeudRuntimeException("001", "pc咨询中心展示设置管理:数据存储失败。");
				}
			}
			return true;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("资讯中心",e);
			throw new SeudRuntimeException("001","数据保存失败");
		}
	}

	/**
	 * 获取协议内容
	 * @param appType
	 * @return
	 */
	@Override
	public ProtocolBean getProtocol(String specialCode, String isApp){
		if (StringUtils.isEmpty(specialCode)) {
			throw new SeudRuntimeException("001", "协议查询:关键字异常");
		}
		try{
			String templateJson = null;
			try {
				templateJson = redisUtil.get(specialCode);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("资讯中心",e);
			}
			if (StringUtils.isEmpty(templateJson)) {
				ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, specialCode);
				if (null == dictEO) {
					throw new SeudRuntimeException("001", "协议查询:未找到业务应用场景。");
				} else {
					templateJson = dictEO.getRelevanceData();
					try {
						redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, specialCode, templateJson, 86400000L);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("资讯中心",e);
					}
				}
			}
			if(StringUtils.isEmpty(templateJson)){
				throw new SeudRuntimeException("001", "协议查询:未配置指定协议");
			}
			//获取协议数据
			ArticlePublishContentEO contentEO = consultContentService.findOneByIdPublish(templateJson);
			if(null==contentEO){
				throw new SeudRuntimeException("001", "协议查询:未找到指定协议");
			}
			ProtocolBean result = new ProtocolBean();
			result.setContent_url(contentEO.getContentUrl());
			result.setTitle(contentEO.getTitle());
			return result;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("资讯中心",e);
			throw new SeudRuntimeException("001","协议查询获取数据失败");
		}
	}

	@Override
	public PageInfo<ArticleKellegEO> listPageByKeywordKelleg(String keyword, Integer pageSize,
			Integer pageNo) {
		if(null==pageSize){
			pageSize = 10;
		}
		if(null==pageNo){
			pageNo = 1;
		}
		try{
			PageInfo<ArticleKellegEO> result = cfgDictDAO.queryListPageByKeywordKelleg(keyword,pageSize,pageNo);
			if (null!=result&&null!=result.getResultsList()) {
				for (ArticleKellegEO eo : result.getResultsList()) {
					eo.setTypeStr(ArticleKellegEnum.getName(eo.getType()));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("资讯中心",e);
			PageInfo<ArticleKellegEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticleKellegEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	@Override
	public boolean saveKelleg(ArticleKellegEO articleKellegEO) {
		if(null==articleKellegEO||StringUtils.isEmpty(articleKellegEO.getKeyCode())){
			throw new SeudRuntimeException("001", "绑定保存:未找到绑定关键字");
		}
		if(StringUtils.isEmpty(articleKellegEO.getType())){
			articleKellegEO.setType(ArticleKellegEnum.Grouping.toString());
		}
		try{
			//若模板类型数据，则加载模板内容缓存
			if(ArticleKellegEnum.Template.toString().equals(articleKellegEO.getType())){
				saveTemplateFile(articleKellegEO);
			}
			ArticleKellegEO kellegEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, articleKellegEO.getKeyCode());
			ArticleKellegEO kellegEO1 = cfgDictDAO.findKellegByRelevanceData(articleKellegEO.getRelevanceData());
			if(null!=kellegEO || null!=kellegEO1){
//				throw new SeudRuntimeException("001","自定义id已经被使用，请替换");
				/*kellegEO.setRelativeDescription(articleKellegEO.getRelativeDescription());
				kellegEO.setRelevanceData(articleKellegEO.getRelevanceData());
				kellegEO.setType(articleKellegEO.getType());
				cfgDictDAO.update(kellegEO);*/
				if(kellegEO!=null){
					cfgDictDAO.delete(kellegEO);
				}
				if(kellegEO1!=null){
					cfgDictDAO.delete(kellegEO1);
				}
				cfgDictDAO.save(articleKellegEO);
			}else{
				cfgDictDAO.save(articleKellegEO);
			}
			return true;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("资讯中心",e);
			throw new SeudRuntimeException("001","绑定保存失败");
		}
	}

	private void saveTemplateFile(ArticleKellegEO articleKellegEO) {
		ResponseContent responseContent = HttpHelper.getUrlRespContent(articleKellegEO.getRelevanceData());
		String content = responseContent.getUTFContent();
		ArticleKellegEO kellegEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, articleKellegEO.getKeyCode()+TEMPLATE_SUFFIX);
		if(null==kellegEO){
			kellegEO = new ArticleKellegEO();
			kellegEO.setKeyCode(articleKellegEO.getKeyCode()+TEMPLATE_SUFFIX);
			kellegEO.setRelativeDescription("模板内容");
			kellegEO.setType(ArticleKellegEnum.Deploy.toString());
			kellegEO.setRelevanceData(content);
			cfgDictDAO.save(kellegEO);
		}else{
			kellegEO.setRelevanceData(content);
			cfgDictDAO.update(kellegEO);
		}
	}
}
