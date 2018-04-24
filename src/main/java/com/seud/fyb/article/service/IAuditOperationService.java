package com.seud.fyb.article.service;

import java.util.List;
import java.util.Map;

import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;

/**
 * @ClassName: AuditOperationService
 * @Description: 审核操作管理
 * @author luoyiming
 * @date 2017年3月8日 下午6:25:02
 */
public interface IAuditOperationService {

	/**
	 * 通过记录id查询审核数据
	 * @param opObjId
	 * @return
	 */
	List<ArticleAuditOperationEO> listByOpObjId(String opObjId);

	/**
	 * 获取审核列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param opType
	 *            业务类型
	 * @param pageSize
	 *            条数
	 * @param pageNo
	 *            页数
	 * @return
	 */
	PageInfo<ArticleAuditOperationEO> listPageByKeyword(String keyword, String opType, Integer pageSize,
			Integer pageNo);

	/**
	 * 判断是否已经重复提交审核（已提交审核的业务数据不允许出现业务操作。若未进入执行中状态的业务数据，允许变更快照数据）
	 * 
	 * @param opObjId
	 *            业务实例id
	 * @param afterDataSnapshot
	 *            快照数据(更新已存在的快照数据)
	 * @return
	 */
	boolean canAuditSubmit(String opObjId, String afterDataSnapshot);

	/**
	 * 审核提交（加入工作流）
	 * 
	 * @param aaoEO
	 *            审核所需数据
	 * @return
	 */
	boolean auditSubmit(ArticleAuditOperationEO aaoEO);

	/**
	 * 审核处理中 (即时修改业务数据)
	 * 
	 * @param id
	 *            审核事项id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return 当前审核业务数据
	 */
	Map<String, Object> auditInHand(String id, SysUserInfo sysUserInfo);
	
	/**
	 * 分组审核处理中 (即时修改业务数据)
	 * 
	 * @param id
	 *            审核事项id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return 当前审核业务数据
	 */
	Map<String, Object> groupAuditInHand(String id, SysUserInfo sysUserInfo);
	
	

	/**
	 * 审核通过 (即时完成业务操作)
	 * 
	 * @param id
	 *            审核事项id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean auditPass(String id, SysUserInfo sysUserInfo);

	/**
	 * 审核驳回(即时修改业务数据)
	 * 
	 * @param id
	 *            审核事项id
	 * @param remark
	 *            驳回原因
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	boolean auditReject(String id, String remark, SysUserInfo sysUserInfo);

}
