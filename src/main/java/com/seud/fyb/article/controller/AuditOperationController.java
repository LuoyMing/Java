
package com.seud.fyb.article.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.reflect.TypeToken;
import com.seud.fyb.article.service.IAuditOperationService;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditRecordEO;
import com.seud.fyb.feignclient.article.model.enums.AuditProcessStateEnum;
import com.seud.fyb.feignclient.article.model.enums.BooleanTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.ContentAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.GroupAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.OperationTypeEnum;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.feignclient.workflow.model.ProcessExecuteInfoEO;
import com.seud.fyb.feignclient.workflow.model.WorkFlowAduitItemsInfoBean;
import com.seud.fyb.feignclient.workflow.model.WorkFlowAduitRecordCondtion;
import com.seud.fyb.framework.bean.BaseController;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.GsonUtils;
import com.seud.fyb.workflow.business.service.IFlowItemsRecordService;

/**
 * @ClassName: AuditOperationController
 * @Description: 操作审核管理
 * @author luoyiming
 * @date 2017年3月14日 上午9:46:00
 * 
 */
@RestController("auditOperation")
public class AuditOperationController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(AuditOperationController.class);

	@Resource(name = "auditOperationService")
	private IAuditOperationService auditOperationService;
	@Autowired
	private IFlowItemsRecordService flowItemsRecordService;
	
	/**
	 * 通过记录id查询审核数据
	 * @param opObjId
	 * @return
	 */
	@RequestMapping("/listByOpObjId")
	public ResponseBodyInfo<List<ArticleAuditOperationEO>> listByOpObjId(String opObjId) {
		List<ArticleAuditOperationEO> result = new ArrayList<ArticleAuditOperationEO>();
		try {
			WorkFlowAduitRecordCondtion flowAduitRecordCondtion=new WorkFlowAduitRecordCondtion();
			flowAduitRecordCondtion.setCountPerPages(10);
			flowAduitRecordCondtion.setPageNumbers(1);
			flowAduitRecordCondtion.setRelationId(opObjId);
			flowAduitRecordCondtion.setIsChangeInfo("1");
			PageInfo<WorkFlowAduitItemsInfoBean> page=flowItemsRecordService.qryFlowItemsRecordList(flowAduitRecordCondtion);
			for (WorkFlowAduitItemsInfoBean workFlowAduitItemsInfo : page.getResultsList()) {
				String changeInfo=workFlowAduitItemsInfo.getChangeInfo();
				Type type = new TypeToken<ArticleAuditOperationEO>() {}.getType();
				ArticleAuditOperationEO aaoEo=GsonUtils.getGson().fromJson(changeInfo, type);
				
				List<ArticleAuditRecordEO> recordEOs =new ArrayList<ArticleAuditRecordEO>();
				
				if(workFlowAduitItemsInfo.getAuditList()!=null){
					for (ProcessExecuteInfoEO processExecuteInfo : workFlowAduitItemsInfo.getAuditList()) {
						if("submit".equals(processExecuteInfo.getBusiApprove())){
							continue;
						}
						ArticleAuditRecordEO articleAuditRecord=new ArticleAuditRecordEO();
						articleAuditRecord.setAuditTime(processExecuteInfo.getExecuteTime());
						articleAuditRecord.setAuditResult(BooleanTypeEnum.getName("true".equals(processExecuteInfo.getBusiApprove())||"submit".equals(processExecuteInfo.getBusiApprove())?"Y":"N"));
						articleAuditRecord.setCause(processExecuteInfo.getAuditRemark());
						articleAuditRecord.setAuditr(processExecuteInfo.getUserName());
						recordEOs.add(articleAuditRecord);
					}
				}
				aaoEo.setRecordEOs(recordEOs);
				if(workFlowAduitItemsInfo.getIsFinished()==1){
					aaoEo.setProcessState(AuditProcessStateEnum.getName("Finish"));
				}else {
					aaoEo.setProcessState(AuditProcessStateEnum.getName("InHand"));
				}
				result.add(aaoEo);
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
	 * 模糊分页查询
	 * 
	 * @param keyword
	 *            搜索字
	 * @param pageSize
	 *            分页条数
	 * @param pageNo
	 *            分页页数
	 * @return
	 */
	@RequestMapping("/listPageByKeywordAudit")
	public ResponseBodyInfo<PageInfo<ArticleAuditOperationEO>> listPageByKeywordAudit(String keyword, String opType,Integer pageSize,
			Integer pageNo) {
		PageInfo<ArticleAuditOperationEO> result;
		try {
			result = auditOperationService.listPageByKeyword(keyword, opType,pageSize, pageNo);
		 	List<ArticleAuditOperationEO> list = result.getResultsList();
		 	for (ArticleAuditOperationEO operationEO : list) {
		 		if (OperationTypeEnum.Content.toString().equals(operationEO.getOpType())) {
		 			operationEO.setAuditType(ContentAuditTypeEnum.getName(operationEO.getAuditType()));
				}else{
					operationEO.setAuditType(GroupAuditTypeEnum.getName(operationEO.getAuditType()));
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
	 * 审核处理中 (即时修改业务数据)
	 * 
	 * @param id
	 *            审核事项id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return 当前审核业务数据
	 */
	@RequestMapping("/auditInHand")
	public ResponseBodyInfo<Map<String, Object>> auditInHand(String id, @RequestBody SysUserInfo sysUserInfo) {
		Map<String, Object> result = null;
		try {
			result = auditOperationService.auditInHand(id, sysUserInfo);
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
	 * 审核处理中 (即时修改业务数据)
	 * 
	 * @param id
	 *            审核事项id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return 当前审核业务数据
	 */
	@RequestMapping("/groupAuditInHand")
	public ResponseBodyInfo<Map<String, Object>> groupAuditInHand(String taskId, @RequestBody SysUserInfo sysUserInfo) {
		Map<String, Object> result = null;
		try {
			result = auditOperationService.groupAuditInHand(taskId, sysUserInfo);
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
	 * 审核通过 (即时完成业务操作)
	 * 
	 * @param id
	 *            审核事项id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/auditPass")
	public ResponseBodyInfo<Boolean> auditPass(String id,String type,String remark, @RequestBody SysUserInfo sysUserInfo) {
		Boolean result;
		try {
			if(BooleanTypeEnum.Y.toString().equals(type)){
				result = auditOperationService.auditPass(id, sysUserInfo);
			}else{
				result = auditOperationService.auditReject(id, remark, sysUserInfo);
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
	@RequestMapping("/auditReject")
	public ResponseBodyInfo<Boolean> auditReject(String id, String remark, @RequestBody SysUserInfo sysUserInfo) {
		Boolean result;
		try {
			result = auditOperationService.auditReject(id, remark, sysUserInfo);
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
