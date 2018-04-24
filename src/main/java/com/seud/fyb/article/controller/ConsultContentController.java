
package com.seud.fyb.article.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.article.service.IConsultContentService;
import com.seud.fyb.feignclient.article.model.bean.ArticleSaveContentBean;
import com.seud.fyb.feignclient.article.model.entity.ArticleBeforehandContentEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleGroupEnum;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.BaseController;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;

/**
 * @ClassName: ConsultClassifyController
 * @Description: 文章内容管理
 * @author luoyiming
 * @date 2017年3月14日 上午9:46:39
 * 
 */
@RestController("consultContent")
public class ConsultContentController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ConsultContentController.class);

	@Resource(name = "consultContentService")
	private IConsultContentService consultContentService;

	/**
	 * 获取单条记录
	 * 
	 * @return
	 */
	@RequestMapping("/findOneByIdPublish")
	public ResponseBodyInfo<ArticlePublishContentEO> findOneByIdPublish(String id) {
		ArticlePublishContentEO result = null;
		try {
			result = consultContentService.findOneByIdPublish(id);
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
	@RequestMapping("/findOneByIdContent")
	public ResponseBodyInfo<ArticleBeforehandContentEO> findOneByIdContent(String id) {
		ArticleBeforehandContentEO result = null;
		try {
			result = consultContentService.findOneById(id);
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
	 * 预发布数据 模糊搜索获取分类列表
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listPageByKeywordBeforehand")
	public ResponseBodyInfo<PageInfo<ArticleBeforehandContentEO>> listPageByKeywordBeforehand(String keyword,
			String classifyId, String relevanceBreedCode, Integer pageSize, Integer pageNo) {
		PageInfo<ArticleBeforehandContentEO> result = null;
		try {
			result = consultContentService.listPageByKeywordBeforehand(keyword, classifyId, relevanceBreedCode,
					pageSize, pageNo);
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
	 * 发布数据 标题模糊搜索获取分类列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listPageByKeywordPublish")
	public ResponseBodyInfo<PageInfo<ArticlePublishContentEO>> listPageByKeywordPublish(String keyword, String groupId,
			String isAPP, Integer pageSize, Integer pageNo) {
		PageInfo<ArticlePublishContentEO> result = null;
		try {
			// long start = System.currentTimeMillis();
			// System.out.println("start:"+start);
			result = consultContentService.listPageByKeyword(keyword, groupId, isAPP, pageSize, pageNo);
			// long end = System.currentTimeMillis();
			// System.out.println("end:"+end);
			// System.out.println("use:"+(end-start));
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
	 * 发布数据 通过分类id获取分类列表 分页
	 * 
	 * @param classifyId
	 *            分类id
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listPageByClassifyIdPublish")
	public ResponseBodyInfo<PageInfo<ArticlePublishContentEO>> listPageByClassifyIdPublish(String classifyId,
			String isAPP, Integer pageSize, Integer pageNo) {
		PageInfo<ArticlePublishContentEO> result = null;
		try {
			result = consultContentService.listPageByClassifyId(classifyId, pageSize, pageNo);
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
	 * 发布数据 通过分组id获取分类列表 分页
	 * 
	 * @param groupId
	 *            分组id
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listPageByGroupIdPublish")
	public ResponseBodyInfo<PageInfo<ArticlePublishContentEO>> listPageByGroupIdPublish(String groupId, String isAPP,
			Integer pageSize, Integer pageNo) {
		PageInfo<ArticlePublishContentEO> result = null;
		try {
			result = consultContentService.listPageByGroupId(groupId, isAPP, pageSize, pageNo);
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
	 * 针对特殊应用场景支持
	 * 
	 * @param specialCode
	 *            特殊编号
	 * @param type
	 *            1.通过groupId查询 2.通过keyword查询
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/listPageBySpecialCodePublish")
	public ResponseBodyInfo<PageInfo<ArticlePublishContentEO>> listPageBySpecialCodePublish(String specialCode,
			String type, String isAPP, Integer pageSize, Integer pageNo) {
		PageInfo<ArticlePublishContentEO> result = null;
		try {
			result = consultContentService.listPageBySpecialCodePublish(specialCode, type, isAPP, pageSize, pageNo);
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
	 * 针对特殊应用场景支持
	 * 
	 * @param specialCode
	 *            特殊编号
	 * @param type
	 *            1.通过groupId查询 2.通过keyword查询
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/listPageBySpecialCodePublishWithGroup")
	public ResponseBodyInfo<PageInfo<ArticlePublishContentEO>> listPageBySpecialCodePublishWithGroup(String keyword,
			String specialCode, String isAPP, Integer pageSize, Integer pageNo) {
		PageInfo<ArticlePublishContentEO> result = null;
		try {
			result = consultContentService.listPageBySpecialCodePublishWithGroup(keyword,specialCode, isAPP, pageSize, pageNo);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	@RequestMapping("/getOneSpecialCodePublish")
	public ResponseBodyInfo<ArticlePublishContentEO> getOneSpecialCodePublish(String specialCode) {
		ArticlePublishContentEO result = null;
		try {
			result = consultContentService.getOneSpecialCodePublish(specialCode);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
	@RequestMapping("/getOneSpecialFieldPublish")
	public ResponseBodyInfo<ArticlePublishContentEO> getOneSpecialFieldPublish(String specialField) {
		ArticlePublishContentEO result = null;
		try {
			result = consultContentService.getOneSpecialFieldPublish(specialField);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	@RequestMapping("/getSpecialCodeOne")
	public ResponseBodyInfo<ArticleKellegEO> getSpecialCodeOne(String specialCode) {
		ArticleKellegEO result = null;
		try {
			result = consultContentService.getSpecialCodeOne(specialCode);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	@RequestMapping("/findKellegByRelevanceData")
	public ResponseBodyInfo<ArticleKellegEO> findKellegByRelevanceData(String relevanceData) {
		ArticleKellegEO result = null;
		try {
			result = consultContentService.findKellegByRelevanceData(relevanceData);
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
	 * 文章发布（待审核，完成一次内容保存）
	 * 
	 * @param contentId
	 *            文章id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/publishContentToAudit")
	public ResponseBodyInfo<Boolean> publishContentToAudit(String contentId, @RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			result = consultContentService.publishContentToAudit(contentId, sysUserInfo);
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
	 * 文章删除（待审核）
	 * 
	 * @param params
	 *            内容参数
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/removeContentToAudit")
	public ResponseBodyInfo<Boolean> removeContentToAudit(String contentId, @RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			result = consultContentService.removeContentToAudit(contentId, sysUserInfo);
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
	 * 保存文章内容
	 * 
	 * @param params
	 *            内容参数
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/saveContent")
	public ResponseBodyInfo<Boolean> saveContent(@RequestBody ArticleSaveContentBean bean) {
		Boolean result = null;
		try {
			result = consultContentService.saveContent(bean.getContentEO(), bean.getSysUserInfo());
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
	 * 修改文章内容
	 * 
	 * @param params
	 *            内容参数
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/updateContent")
	public ResponseBodyInfo<Boolean> updateContent(@RequestBody ArticleSaveContentBean bean) {
		Boolean result = null;
		try {
			result = consultContentService.saveContent(bean.getContentEO(), bean.getSysUserInfo());
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
	 * 显示分组设置
	 * 
	 * @param params
	 *            内容参数
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/updateShowGroup")
	public ResponseBodyInfo<Boolean> updateShowGroup(@RequestBody ArticleSaveContentBean bean) {
		Boolean result = null;
		try {
			result = consultContentService.updateShowGroup(bean.getContentEO(), bean.getSysUserInfo());
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
	 * 预发布数据 模糊搜索获取分类列表
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listTopGroupOne")
	public ResponseBodyInfo<List<ArticlePublishContentEO>> listTopGroupOne(String isAPP) {
		List<ArticlePublishContentEO> result = null;
		try {
			result = consultContentService.listTopGroupOne(isAPP, ArticleGroupEnum.InformationCenter.toString(), 2);
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
	 * 预发布数据 模糊搜索获取分类列表
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listByGroupAndSize")
	public ResponseBodyInfo<List<ArticlePublishContentEO>> listByGroupAndSize(String isAPP, String parentGroup,
			Integer size) {
		List<ArticlePublishContentEO> result = null;
		try {
			if (!ArticleGroupEnum.inkey(parentGroup)) {
				parentGroup = ArticleGroupEnum.InformationCenter.toString();
			}
			if (null == size || size <= 0) {
				size = 1;
			}
			result = consultContentService.listTopGroupOne(isAPP, parentGroup, size);
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
	 * 保存文章内容生成静态文件，返回静态文件地址
	 * @param userCode
	 * @param content
	 * @return
	 */
	@RequestMapping("/saveOnlyContent")
	public ResponseBodyInfo<String> saveOnlyContent(String userCode,String content){
		String contentUrl = null;
		try {
			contentUrl = consultContentService.saveOnlyContent(userCode, content);
			if (contentUrl!=null&&contentUrl.contains("https://")) {
				contentUrl = contentUrl.replaceAll("https://", "http://");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(contentUrl);
	}
}
