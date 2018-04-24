package com.seud.fyb.article.service;

import java.util.List;

import com.seud.fyb.feignclient.article.model.bean.ArticleGroupBean;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupTypeEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;

/**
 * @ClassName: ArticleGroupService
 * @Description: 分组管理
 * @author luoyiming
 * @date 2017年3月8日 下午5:47:58
 * 
 */
public interface IConsultGroupService {

	/**
	 * 获取分组列表
	 * @param groupType 
	 * 
	 * @param parentId
	 * @return
	 */
	List<ArticleGroupBean> listAll();

	/**
	 * 通过根节点id获取分组列表
	 * 
	 * @param parentId
	 * @return
	 */
	List<ArticleGroupEO> listByRootGroup(String parentId);

	/**
	 * 通过根节点id获取分组列表 分页
	 * 
	 * @param parentId
	 * @return
	 */
	PageInfo<ArticleGroupEO> listPageByKeyword(String keyword, Integer pageSize, Integer pageNo);

	/**
	 * 保存分组（待审核）
	 * 
	 * @param id
	 *            分组id
	 * @param name
	 *            分组名称
	 * @param remark
	 *            描述信息
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean saveGroupToAudit(String parentId, String name, String remark, SysUserInfo sysUserInfo);

	/**
	 * 保存分组（审核通过）
	 * 
	 * @param aaoEo
	 *            审核业务数据
	 * @return
	 */
	boolean saveGroupAuditPass(ArticleAuditOperationEO aaoEo);

	/**
	 * 修改分组（待审核）
	 * 
	 * @param id
	 *            分组id
	 * @param name
	 *            分组名称
	 * @param remark
	 *            描述信息
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean updateGroupToAudit(String id, String name, String remark, SysUserInfo sysUserInfo);

	/**
	 * 修改分组（审核通过）
	 * 
	 * @param aaoEo
	 *            审核业务数据
	 * @return
	 */
	boolean updateGroupAuditPass(ArticleAuditOperationEO aaoEo);

	/**
	 * 删除分组（待审核）
	 * 
	 * @param id
	 *            分组id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean deleteGroupToAudit(String id, SysUserInfo sysUserInfo);

	/**
	 * 删除分组（审核通过）
	 * 
	 * @param aaoEo
	 *            审核业务数据
	 * @return
	 */
	boolean deleteGroupAuditPass(ArticleAuditOperationEO aaoEo);

	/**置顶
	 * @param groupId
	 * @param contentId
	 * @return
	 */
	boolean stickGroupContentCorrelation(String groupId, String contentId);

	/**移除
	 * @param groupId
	 * @param contentId
	 * @return
	 */
	boolean deleteGroupContentCorrelation(String groupId, String contentId);

	/**置顶
	 * @param id
	 * @return
	 */
	Boolean stickGroup(String id);

	/**
	 * 查询分组类型
	 * @return
	 */
	List<ArticleGroupTypeEO> queryGroupType();
	
	/**
	 * 保存分组类型
	 * @param eo
	 * @return
	 */
	boolean saveGroupType(ArticleGroupTypeEO eo);
	
}
