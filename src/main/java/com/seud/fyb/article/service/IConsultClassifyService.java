package com.seud.fyb.article.service;

import java.util.List;

import com.seud.fyb.feignclient.article.model.entity.ArticleClassifyEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;

/**
 * @ClassName: ArticleClassifyService
 * @Description: 分类管理
 * @author luoyiming
 * @date 2017年3月8日 下午5:47:31
 * 
 */
public interface IConsultClassifyService {

	/**
	 * 获取分类列表
	 * 
	 * @return
	 */
	List<ArticleClassifyEO> listAll();

	/**
	 * 通过根节点id获取分类列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            条数
	 * @param pageNo
	 *            页数
	 * @return
	 */
	PageInfo<ArticleClassifyEO> listPageByKeyword(String keyword, Integer pageSize, Integer pageNo);

	/**
	 * 保存分类（待审核）
	 * 
	 * @param name
	 *            分类名称
	 * @param parentId
	 *            根节点分类id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean saveClassify(String name, SysUserInfo sysUserInfo);

	/**
	 * 修改分类（待审核）
	 * 
	 * @param id
	 *            分类id
	 * @param name
	 *            分类名称
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean updateClassify(String id, String name, SysUserInfo sysUserInfo);

	/**
	 * 删除分类（待审核）
	 * 
	 * @param id
	 *            分类id
	 * @return
	 */
	boolean deleteClassify(String id);

}
