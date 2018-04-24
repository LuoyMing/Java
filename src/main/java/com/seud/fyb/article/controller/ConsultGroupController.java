
package com.seud.fyb.article.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.article.service.IConsultGroupService;
import com.seud.fyb.feignclient.article.model.bean.ArticleGroupBean;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupTypeEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleGroupEnum;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.BaseController;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultClassifyController
 * @Description: 分组管理
 * @author luoyiming
 * @date 2017年3月14日 上午9:46:39
 * 
 */
@RestController("consultGroup")
public class ConsultGroupController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ConsultGroupController.class);

	@Resource(name = "consultGroupService")
	private IConsultGroupService consultGroupService;

	/**
	 * 获取分组列表
	 * 
	 * 分组类型
	 * @param groupType
	 * @return
	 */
	@RequestMapping("/listAllGroup")
	public ResponseBodyInfo<List<ArticleGroupBean>> listAllGroup() {
		List<ArticleGroupBean> result = null;
		try {
			result = consultGroupService.listAll();
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
	 * @param parentId
	 *            根节点id
	 * @return
	 */
	@RequestMapping("/listByRootGroup")
	public ResponseBodyInfo<List<ArticleGroupEO>> listByRootGroup(String groupType) {
		List<ArticleGroupEO> result = null;
		try {
			if(StringUtils.isNotEmpty(groupType)){
				if(ArticleGroupEnum.inkey(groupType)){
					result = consultGroupService.listByRootGroup(groupType);
				}else{
					throw new SeudRuntimeException("001", "通过分组类型获取分组列表:分组类型不合法");
				}
			}else{
				throw new SeudRuntimeException("001", "通过分组类型获取分组列表:分组类型不合法");
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
	 * 通过根节点id获取分组列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listPageByKeywordGroup")
	public ResponseBodyInfo<PageInfo<ArticleGroupEO>> listPageByKeywordGroup(String keyword, Integer pageSize,
			Integer pageNo) {
		PageInfo<ArticleGroupEO> result = null;
		try {
			result = consultGroupService.listPageByKeyword(keyword, pageSize, pageNo);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
	
	/** 置顶
	 * @return
	 */
	@RequestMapping("/stickGroup")
	public @ResponseBody ResponseBodyInfo<Boolean> stickGroup(String id) {
		Boolean result = null;
		try {
			result = consultGroupService.stickGroup(id);
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
	@RequestMapping("/saveGroupToAudit")
	public ResponseBodyInfo<Boolean> saveGroupToAudit(String parentId, String name, String remark,
			@RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			result = consultGroupService.saveGroupToAudit(parentId, name, remark, sysUserInfo);
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
	@RequestMapping("/updateGroupToAudit")
	public ResponseBodyInfo<Boolean> updateGroupToAudit(String id, String name, String remark,
			@RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			result = consultGroupService.updateGroupToAudit(id, name, remark, sysUserInfo);
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
	 * 删除分组（待审核）
	 * 
	 * @param id
	 *            分组id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/deleteGroupToAudit")
	public ResponseBodyInfo<Boolean> deleteGroupToAudit(String id, @RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			result = consultGroupService.deleteGroupToAudit(id, sysUserInfo);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	/** 编辑文章分组关系
	 * @param groupId 
	 * @param contentId 
	 * @param type  编辑类型 1.置顶 2.移除
	 * @return
	 */
	@RequestMapping("/articleGroupContentCorrelationEdit")
	public ResponseBodyInfo<Boolean> articleGroupContentCorrelationEdit(String groupId,String contentId,String type) {
		Boolean result = null;
		try {
			if("1".equals(type)){
				result = consultGroupService.stickGroupContentCorrelation(groupId, contentId);
			}else if("2".equals(type)){
				result = consultGroupService.deleteGroupContentCorrelation(groupId, contentId);
			}else{
				result = false; 
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
	 * 查询分组类型
	 * @return
	 */
	@RequestMapping("/queryGroupType")
	public ResponseBodyInfo<List<ArticleGroupTypeEO>> queryGroupType(){
		List<ArticleGroupTypeEO> result = consultGroupService.queryGroupType();
		return ResultInfo.successForObj(result);
	}
	
	/**
	 * 保存分组类型
	 * type :1 新增else修改
	 * @return
	 */
	@RequestMapping("/saveGroupType")
	public ResponseBodyInfo<Boolean> saveGroupType(@RequestBody ArticleGroupTypeEO eo){
		Boolean result = null;
		try {
				result = consultGroupService.saveGroupType(eo);
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
