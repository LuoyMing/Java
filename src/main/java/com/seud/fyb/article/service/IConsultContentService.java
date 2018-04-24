package com.seud.fyb.article.service;

import java.util.List;

import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleBeforehandContentEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;

/**
 * @ClassName: ArticleContentService
 * @Description: 文章内容管理
 * @author luoyiming
 * @date 2017年3月8日 下午5:47:47
 * 
 */
public interface IConsultContentService {

	/**
	 * 预发布数据 模糊搜索获取分类列表
	 * @param classifyId 
	 * @param relevanceBreedCode 
	 * 
	 * @param parentId
	 * @return
	 */
	PageInfo<ArticleBeforehandContentEO> listPageByKeywordBeforehand(String keyword, String classifyId, String relevanceBreedCode, Integer pageSize, Integer pageNo);

	/**
	 * 发布数据 标题模糊搜索获取分类列表 分页
	 * @param appType 
	 * 
	 * @param parentId
	 * @return
	 */
	PageInfo<ArticlePublishContentEO> listPageByKeyword(String keyword, String groupId,String appType, Integer pageSize, Integer pageNo);

	/**
	 * 发布数据 通过分类id获取分类列表 分页
	 * 
	 * @param parentId
	 * @return
	 */
	PageInfo<ArticlePublishContentEO> listPageByClassifyId(String classifyId, Integer pageSize, Integer pageNo);

	/**
	 * 发布数据 通过分组id获取分类列表 分页
	 * @param appType 
	 * 
	 * @param parentId
	 * @return
	 */
	PageInfo<ArticlePublishContentEO> listPageByGroupId(String groupId, String appType, Integer pageSize, Integer pageNo);

	/**
	 * 文章发布（待审核，完成一次内容保存）
	 * 
	 * @param contentId
	 *            文章id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean publishContentToAudit(String contentId, SysUserInfo sysUserInfo);

	/**
	 * 文章发布（审核通过）
	 * 
	 * @param aaoEo
	 *            审核业务数据
	 * @return
	 */
	boolean publishContentAuditPass(ArticleAuditOperationEO aaoEo);

	/**
	 * 文章删除（待审核）
	 * 
	 * @param params
	 *            内容参数
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean removeContentToAudit(String contentId, SysUserInfo sysUserInfo);

	/**
	 * 文章删除（审核通过）
	 * 
	 * @param aaoEo
	 *            审核业务数据
	 * @return
	 */
	boolean removeContentAuditPass(ArticleAuditOperationEO aaoEo);

	/**
	 * 保存文章内容
	 * 
	 * @param params
	 *            内容参数
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean saveContent(ArticleBeforehandContentEO contentEO, SysUserInfo sysUserInfo);

	/**
	 * 修改文章内容
	 * 
	 * @param params
	 *            内容参数
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean updateContent(ArticleBeforehandContentEO contentEO, SysUserInfo sysUserInfo);

	/**
	 * 获取单条
	 * @param id
	 * @return
	 */
	ArticleBeforehandContentEO findOneById(String id);
	
	/**
	 * 获取单条
	 * @param id
	 * @return
	 */
	ArticlePublishContentEO findOneByIdPublish(String id);

	/**
	 * 针对特殊应用场景支持
	 * @param specialCode 特殊编号
	 * @param type  1.通过groupId查询 2.通过keyword查询
	 * @param appType 
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	PageInfo<ArticlePublishContentEO> listPageBySpecialCodePublish(String specialCode, String type, String appType, Integer pageSize,
			Integer pageNo);
	
	ArticleKellegEO getSpecialCodeOne(String specialCode);

	boolean updateShowGroup(ArticleBeforehandContentEO editContentEO, SysUserInfo sysUserInfo);

	List<ArticlePublishContentEO> listInPublish(String inPublish, String isAPP);

	ArticleKellegEO findKellegByRelevanceData(String relevanceData);

	ArticlePublishContentEO getOneSpecialCodePublish(String specialCode);
	
	ArticlePublishContentEO getOneSpecialFieldPublish(String specialField);

	List<ArticlePublishContentEO> listTopGroupOne(String appType,	String parentGroup, int size);

	PageInfo<ArticlePublishContentEO> listPageBySpecialCodePublishWithGroup(String keyword, String specialCode,
			String isAPP, Integer pageSize, Integer pageNo);

	String saveOnlyContent(String userCode,String content);
}
