package com.seud.fyb.article.service;

import com.seud.fyb.feignclient.article.model.entity.ArticleFileManageEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleUtilTemplateEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;

/**
 * @ClassName: ArticleUtilTemplateService
 * @Description: 公告模板管理
 * @author luoyiming
 * @date 2017年3月8日 下午5:48:07
 * 
 */
public interface IUtilTemplateService {

	/**
	 * 获取模板列表 分页
	 * 
	 * @param parentId
	 * @return
	 */
	PageInfo<ArticleUtilTemplateEO> listPageByKeyword(String keyword, String type, Integer pageSize, Integer pageNo);

	/**
	 * 保存模板
	 * 
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
	boolean saveTemplate(String title, String content, String type, SysUserInfo sysUserInfo);

	/**
	 * 修改模板
	 * 
	 * @param id
	 *            模板id
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean updateTemplate(String id, String title, String content, SysUserInfo sysUserInfo);

	/**
	 * 删除模板
	 * 
	 * @param id
	 *            模板id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean deleteTemplate(String id);
	
	/**
	 * 获取富文本编辑器文件列表 分页
	 * 
	 * @param type
	 * @param pageSize 条数
	 * @param pageNo 起始位置
	 * @return
	 */
	PageInfo<ArticleFileManageEO> listPageByFileManage(String type, Integer pageSize, Integer pageNo);
	
	/**
	 * 保存文件记录
	 * @param eo
	 * @return
	 */
	boolean saveFileManage(ArticleFileManageEO eo);

	ArticleUtilTemplateEO findOneByIdTemplate(String id);

}
