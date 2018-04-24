
package com.seud.fyb.article.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.seud.fyb.article.service.IConsultContentService;
import com.seud.fyb.article.service.IConsultReferenceCenterService;
import com.seud.fyb.feignclient.article.model.bean.CarouselBean;
import com.seud.fyb.feignclient.article.model.bean.ProtocolBean;
import com.seud.fyb.feignclient.article.model.bean.TemplateModuleBean;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleGroupEnum;
import com.seud.fyb.framework.annotation.Column;
import com.seud.fyb.framework.bean.BaseController;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultClassifyController
 * @Description: 咨询中心模板设置
 * @author luoyiming
 * @date 2017年3月14日 上午9:46:39
 * 
 */
@RestController("consultReferenceCenter")
public class ConsultReferenceCenterController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ConsultReferenceCenterController.class);

	@Resource(name = "consultReferenceCenterService")
	private IConsultReferenceCenterService consultReferenceCenterService;
	
	@Resource(name = "consultContentService")
	private IConsultContentService consultContentService;

	/**
	 * 获取顶部轮播数据
	 * 
	 * @return
	 */
	@RequestMapping("/getProtocol")
	public ResponseBodyInfo<ProtocolBean> getProtocol(String specialCode,String isAPP) {
		ProtocolBean result = null;
		try {
			result = consultReferenceCenterService.getProtocol(specialCode,isAPP);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
	/**
	 * 获取顶部轮播数据
	 * 
	 * @return
	 */
	@RequestMapping("/getTopTemplateJson")
	public ResponseBodyInfo<List<CarouselBean>> getTopTemplateJson(String isAPP) {
		List<CarouselBean> result = null;
		try {
			String json = consultReferenceCenterService.getTopTemplateJson(isAPP);
			if(StringUtils.isNotEmpty(json)){
				//解析为数据对象
				result = JSON.parseArray(json, CarouselBean.class);
			}else{
				result = new ArrayList<>();
			}
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
	/**
	 * 通过根节点id获取分组列表
	 * 
	 * @return
	 */
	@RequestMapping("/getTemplateJson")
	public ResponseBodyInfo<TemplateModuleBean> getTemplateJson(String isAPP) {
		TemplateModuleBean result = null;
		try {
			 String json = consultReferenceCenterService.getTemplateJson(isAPP);
			 if(StringUtils.isNotEmpty(json)){
				 result = JSON.parseObject(json, TemplateModuleBean.class);
			 }else{
				 result = new TemplateModuleBean();
			 }
			 if(StringUtils.isNotEmpty(isAPP)&&"true".equals(isAPP)){
				 result.setModules(consultContentService.listTopGroupOne(isAPP, ArticleGroupEnum.InformationCenter.toString(), 2));
			 }else{
				 //提取发布表中最新文章数据
				 if(0!=result.getModules().size()){
					 String inPublish = "'";
					 for (ArticlePublishContentEO publishContentEO : result.getModules()) {
						 inPublish += publishContentEO.getId() + "','";
					 }
					 inPublish = inPublish.substring(0,inPublish.length()-2);
					 List<ArticlePublishContentEO> publishList = consultContentService.listInPublish(inPublish,isAPP);
					 //移除已删除的发布文章
					 List<ArticlePublishContentEO> removeList = new ArrayList<>();
					 for (ArticlePublishContentEO publishContentEO : result.getModules()) {
						 boolean isin =false;
						 for (ArticlePublishContentEO contentEO : publishList) {
							 if(contentEO.getId().equals(publishContentEO.getId())&&publishContentEO.getGroupId().equals(contentEO.getGroupId())){
								 publishContentEO.setContentUrl(contentEO.getContentUrl());
								 publishContentEO.setTitleImgUrl(contentEO.getTitleImgUrl());
								 publishContentEO.setTitle(contentEO.getTitle());
								 publishContentEO.setShowType(contentEO.getShowType());
								 publishContentEO.setContentSource(contentEO.getContentSource());
								 publishContentEO.setContentAuthor(contentEO.getContentAuthor());
								 publishContentEO.setContentDigest(contentEO.getContentDigest());
								 publishContentEO.setContentUrl(contentEO.getContentUrl());
								 isin = true;
								 break;
							 }
						 }
						 if(!isin){
							 removeList.add(publishContentEO);
						 }
					 }
					 result.getModules().removeAll(removeList);
				 }
			 }
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	/**
	 * 保存模板
	 * 
	 * @param templateJson
	 *            模板json串
	 * @return
	 */
	@RequestMapping("/saveTemplateJson")
	public ResponseBodyInfo<Boolean> saveTemplateJson(@RequestBody String templateJson,String isAPP) {
		Boolean result = null;
		try {
			result = consultReferenceCenterService.saveTemplateJson(templateJson,isAPP);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
	/**
	 * 保存首页轮播模板
	 * 
	 * @param templateJson
	 *            模板json串
	 * @return
	 */
	@RequestMapping("/saveTopTemplateJson")
	public ResponseBodyInfo<Boolean> saveTopTemplateJson(@RequestBody String templateJson,String isAPP) {
		Boolean result = null;
		try {
			result = consultReferenceCenterService.saveTopTemplateJson(templateJson,isAPP);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
	/**
	 * 获取码表列表
	 * @param keyword
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/listPageByKeywordKelleg")
	public ResponseBodyInfo<PageInfo<ArticleKellegEO>> listPageByKeywordKelleg(String keyword, Integer pageSize,
			Integer pageNo){
		PageInfo<ArticleKellegEO> result = null;
		try {
			result = consultReferenceCenterService.listPageByKeywordKelleg(keyword, pageSize, pageNo);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
	/**
	 * 保存码表绑定关系
	 * @param articleKellegEO
	 * @return
	 */
	@RequestMapping("/saveKelleg")
	public ResponseBodyInfo<Boolean> saveKelleg(@RequestBody ArticleKellegEO articleKellegEO) {
		Boolean result = null;
		try {
			result = consultReferenceCenterService.saveKelleg(articleKellegEO);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
}
