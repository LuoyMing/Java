
package com.seud.fyb.article.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.article.service.IUtilTemplateService;
import com.seud.fyb.article.utils.MassegeFormat;
import com.seud.fyb.feignclient.article.model.entity.ArticleFileManageEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleUtilTemplateEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.BaseController;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.RequestParamtersUtil;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultClassifyController
 * @Description: 公告模板
 * @author luoyiming
 * @date 2017年3月14日 上午9:46:39
 * 
 */
@RestController("utilTemplate")
public class UtilTemplateController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UtilTemplateController.class);

	@Resource(name = "utilTemplateService")
	private IUtilTemplateService utilTemplateService;

	/**
	 * 获取模板列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param type
	 *            模板类型
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listPageByKeywordUtilTemplate")
	public ResponseBodyInfo<PageInfo<ArticleUtilTemplateEO>> listPageByKeywordUtilTemplate(String keyword, String type,
			Integer pageSize, Integer pageNo) {
		PageInfo<ArticleUtilTemplateEO> result = null;
		try {
			result = utilTemplateService.listPageByKeyword(keyword, type, pageSize, pageNo);
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
	 * 获取模板列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param type
	 *            模板类型
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数 
	 * @return
	 */
	@RequestMapping("/listPageByKeywordUtilTemplateIsInContent")
	public ResponseBodyInfo<PageInfo<ArticleUtilTemplateEO>> listPageByKeywordUtilTemplateIsInContent(String keyword, String type,
			Boolean isInContent,Integer pageSize, Integer pageNo) {
		PageInfo<ArticleUtilTemplateEO> result = null;
		try {
			result = utilTemplateService.listPageByKeyword(keyword, type, pageSize, pageNo);
			if(null!=isInContent&&!isInContent){
				for (ArticleUtilTemplateEO templateEO : result.getResultsList()) {
					templateEO.setContent("");
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
	 * 获取单条记录
	 * 
	 * @return
	 */
	@RequestMapping("/findOneByIdTemplate")
	public ResponseBodyInfo<ArticleUtilTemplateEO> findOneByIdTemplate(String id) {
		ArticleUtilTemplateEO result = null;
		try {
			result = utilTemplateService.findOneByIdTemplate(id);
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
	 * @param id
	 *            模板id
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param type
	 *            类型
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/saveTemplate")
	public ResponseBodyInfo<Boolean> saveTemplate(String id, String title, String content, String type,
			@RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			if (StringUtils.isEmpty(id)) {
				result = utilTemplateService.saveTemplate(title, content, type, sysUserInfo);
			} else {
				result = utilTemplateService.updateTemplate(id, title, content, sysUserInfo);
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
	 * 删除模板
	 * 
	 * @param id
	 *            模板id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/deleteTemplate")
	public ResponseBodyInfo<Boolean> deleteTemplate(String id) {
		Boolean result = null;
		try {
			result = utilTemplateService.deleteTemplate(id);
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
	 * 获取富文本编辑器文件列表 分页
	 * 
	 * @param type
	 * @param pageSize 条数
	 * @param pageNo 起始位置
	 * @return
	 */
	@RequestMapping("/listPageByFileManage")
	public ResponseBodyInfo<PageInfo<ArticleFileManageEO>> listPageByFileManage(String type,
			Integer pageSize, Integer pageNo) {
		PageInfo<ArticleFileManageEO> result = null;
		try {
			result = utilTemplateService.listPageByFileManage(type, pageSize, pageNo);
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
	 * 保存文件记录
	 * 
	 * @param eo
	 * @return
	 */
	@RequestMapping("/saveFileManage")
	public ResponseBodyInfo<Boolean> saveFileManage(@RequestBody ArticleFileManageEO eo) {
		Boolean result = null;
		try {
				result = utilTemplateService.saveFileManage(eo);
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
	 * 文章模板内容填充(通用，1、传入参数必须带模板ID；2、参数名称必须与模板占位符名称一致)
	 * @return
	 */
	@RequestMapping("/replaceTemplate")
	public ResponseBodyInfo<ArticleUtilTemplateEO> replaceTemplate(HttpServletRequest request) {
		ArticleUtilTemplateEO template = null;
		Map<String, String> params = RequestParamtersUtil.getRequestParamsInFilter(request);
		try {
			template = utilTemplateService.findOneByIdTemplate(params.get("id"));
			if(template!=null){
				String content = template.getContent();
				String newContent = MassegeFormat.stringFormat(content, params);
				template.setContent(newContent);
			}else{
				return ResultInfo.errorForObj("001","未找到文章公告模板");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(template);
	}
	
	
	
	
	
	
	
}
