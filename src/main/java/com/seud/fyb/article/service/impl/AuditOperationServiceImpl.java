package com.seud.fyb.article.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.seud.fyb.article.bean.FlowParamsBean;
import com.seud.fyb.article.dao.IAuditOperationDAO;
import com.seud.fyb.article.dao.IConsultContentDAO;
import com.seud.fyb.article.dao.IConsultGroupDAO;
import com.seud.fyb.article.service.IAuditOperationService;
import com.seud.fyb.article.service.IConsultContentService;
import com.seud.fyb.article.service.IConsultGroupService;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditRecordEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleBeforehandContentEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.enums.AuditProcessStateEnum;
import com.seud.fyb.feignclient.article.model.enums.BooleanTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.ContentAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.ContentEditStateEnum;
import com.seud.fyb.feignclient.article.model.enums.GeneralizeLabelEnum;
import com.seud.fyb.feignclient.article.model.enums.GroupAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.OperationTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.ShowTypeEnum;
import com.seud.fyb.feignclient.background.BackgroundClient;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.feignclient.member.model.FlowAduitItemsInfoEO;
import com.seud.fyb.feignclient.operation.MessageClient;
import com.seud.fyb.feignclient.operation.model.MessageBean;
import com.seud.fyb.feignclient.operation.model.MessageUserBean;
import com.seud.fyb.feignclient.workflow.WorkflowClient;
import com.seud.fyb.feignclient.workflow.model.ProcessExecuteInfoEO;
import com.seud.fyb.feignclient.workflow.model.enums.VersionEnum;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.GsonUtils;
import com.seud.fyb.framework.utils.UUIDGenerator;
import com.seud.fyb.workflow.business.service.IFlowItemsRecordService;
import com.wfw.common.utils.StringUtils;



/**
 * @ClassName: ConsultClassifyServiceImpl
 * @Description: 分类管理
 * @author luoyiming
 * @date 2017年3月9日 下午1:57:02
 * 
 */
@Service("auditOperationService")
public class AuditOperationServiceImpl implements IAuditOperationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditOperationServiceImpl.class);
	
	@Resource(name = "consultGroupDAO")
	private IConsultGroupDAO consultGroupDAO;

	@Resource(name = "consultContentDAO")
	private IConsultContentDAO consultContentDAO;

	@Resource(name = "auditOperationDAO")
	private IAuditOperationDAO auditOperationDAO;

	@Autowired
	protected WorkflowClient workflowClient;

	@Resource(name = "consultContentService")
	private IConsultContentService consultContentService;

	@Resource(name = "consultGroupService")
	private IConsultGroupService consultGroupService;
	
	@Autowired
	private IFlowItemsRecordService flowItemsRecordService;
	
	@Autowired
	private WorkflowClient workflow;
	
	@Autowired
	private MessageClient messageSender;
	
	@Autowired
	private BackgroundClient backgroundClient;

	/**
	 * 通过记录id查询审核数据
	 * @param opObjId
	 * @return
	 */
	public List<ArticleAuditOperationEO> listByOpObjId(String opObjId) {
		try {
			return auditOperationDAO.findByOpObjId(opObjId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			return new ArrayList<>();
		}
	}
	
	/**
	 * (non-Javadoc) 通过根节点id获取分类列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            条数
	 * @param pageNo
	 *            页数
	 * @return
	 */
	@Override
	public PageInfo<ArticleAuditOperationEO> listPageByKeyword(String keyword,String opType ,Integer pageSize, Integer pageNo) {
		if(null==pageSize){
			pageSize = 10;
		}
		if(null==pageNo){
			pageNo = 1;
		}
		try {
			PageInfo<ArticleAuditOperationEO> result = auditOperationDAO.queryListPageByKeyword(keyword,opType ,pageSize, pageNo);
			if(null!=result&&null!=result.getResultsList()){
				for (ArticleAuditOperationEO eo : result.getResultsList()) {
					eo.setProcessState(AuditProcessStateEnum.getName(eo.getProcessState()));
					eo.setIsPassAudit(BooleanTypeEnum.getName(eo.getIsPassAudit()));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			PageInfo<ArticleAuditOperationEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticleAuditOperationEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	
	/**
	 * 判断可否提交审核（已提交审核的业务数据不允许出现业务操作。若未进入执行中状态的业务数据，允许变更快照数据）
	 * 
	 * @param opObjId
	 *            业务实例id
	 * @param afterDataSnapshot
	 *            快照数据(更新已存在的快照数据)
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean canAuditSubmit(String opObjId, String afterDataSnapshot) {
		FlowAduitItemsInfoEO flowAduitItemsInfoEO = new FlowAduitItemsInfoEO();
		flowAduitItemsInfoEO.setRelationId(opObjId);
		 List<FlowAduitItemsInfoEO>  flowAduitItemsInfolist = flowItemsRecordService.getFlowAduitItemsInfo(flowAduitItemsInfoEO);
		 if(!flowAduitItemsInfolist.isEmpty()){
			 return false;
		 }
		return true;
		/*List<ArticleAuditOperationEO> list = null;
		try {
			list = auditOperationDAO.findByOpObjIdWithProcessState(opObjId,
					AuditProcessStateEnum.Finish.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			throw new SeudRuntimeException("001", "判断可否提交审核:获取审核数据异常，请联系管理员");
		}
		if (null == list || list.size() == 0) {
			return true;
		}
		ArticleAuditOperationEO operationEO = list.get(0);
		if (AuditProcessStateEnum.Wait.toString().equals(list.get(0).getProcessState())) {
			if (!StringUtils.isEmpty(afterDataSnapshot)) {
				// 变更快照数据
				operationEO.setAfterDataSnapshot(afterDataSnapshot);
				try {
					auditOperationDAO.update(operationEO);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("审核",e);
					throw new SeudRuntimeException("001", "提交审核:变更数据存储失败");
				}
			}
			return false;
		} else {
			throw new SeudRuntimeException("001", "该记录正在进行审核");
		}*/
	}
	
	
	/**
	 * (non-Javadoc) 审核提交（启动工作流） 存储审核数据 加入工作流
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean auditSubmit(ArticleAuditOperationEO aaoEo) {
		String flowInstanceId = null;
		if (StringUtils.isEmpty(aaoEo.getInitiatorId()) 
				|| StringUtils.isEmpty(aaoEo.getFlowId())
				|| StringUtils.isEmpty(aaoEo.getId())) {
			throw new SeudRuntimeException("001", aaoEo.getMatterName() + "添加工作流失败:关键参数异常");
		}
		try {
			aaoEo.setMatterName(aaoEo.getMatterName().trim().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", ""));
			FlowParamsBean flowParams = FlowParamsBean.getInstance().addParams("applyUserId", aaoEo.getInitiatorId())
					.addParams("flowId", aaoEo.getFlowId()).addParams("businessKey", aaoEo.getOpObjId())
					.addVariables("audititemsId", aaoEo.getId()).addVariables("applyUserId", aaoEo.getInitiatorId())
					.addVariables("applyUserName", aaoEo.getInitiator())
					.addVariables("taskName", aaoEo.getMatterName())
					.addVariables("executerUserId",aaoEo.getInitiatorId())
					.addVariables("executerUserName", aaoEo.getInitiator())
					.addVariables("version", VersionEnum.getNewestVersion())
					.addVariables("effectiveInfo", JSON.toJSONString(aaoEo))
					.addVariables("changeInfo", JSON.toJSONString(aaoEo));
			
			// 启动工作流
			ResponseBodyInfo<Map<String, Object>> responseBody = this.workflowClient
					.startWorkflow(flowParams.toJSONString());
			if (!responseBody.isOk()) {
				throw new SeudRuntimeException("001", aaoEo.getMatterName() + "添加工作流失败:" + responseBody.getRetMsg());
			}
			// 获取流程实例ID
			flowInstanceId = String.valueOf(responseBody.getData().get("processInstanceId"));
//			aaoEo.setFlowInstanceId(flowInstanceId);
			// 保存审核数据
//			auditOperationDAO.saveAuditOperationEO(aaoEo);
			return true;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			if (StringUtils.isNotEmpty(flowInstanceId)) {
				// 删除流程实例
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("processInstanceId", flowInstanceId);
				paramsMap.put("deleteReason", aaoEo.getMatterName() + "过程发生异常");
				workflowClient.delWorkflowInst(JSON.toJSONString(paramsMap));
			}
			throw new SeudRuntimeException("001", "审核记录提交失败");
		}
	}

	/**
	 * (non-Javadoc) 审核处理中 变更审核状态为审核中(主要应用于当审核信息被打开之后，该信息不能进行任何变更处理)
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Map<String, Object> auditInHand(String processInstanceId, SysUserInfo sysUserInfo) {
		Map<String, Object> map = new HashMap<>();
		ArticleAuditOperationEO aaoEo = null;
		try {
			ProcessExecuteInfoEO executeInfoEO = new ProcessExecuteInfoEO();
			executeInfoEO.setProcessInstanceId(processInstanceId);
			String aaoEoJsonText = workflowClient.queryNewestEffectiveInfoInfoByProcessInstancdId(executeInfoEO).getData();
			aaoEo = JSON.parseObject(aaoEoJsonText, ArticleAuditOperationEO.class);
			aaoEo.setFlowInstanceId(processInstanceId);
//			aaoEo = auditOperationDAO.findByPrimaryKey(ArticleAuditOperationEO.class, id);
			if (AuditProcessStateEnum.No.toString().equals(aaoEo.getProcessState())
					|| AuditProcessStateEnum.Wait.toString().equals(aaoEo.getProcessState())) {
				aaoEo.setProcessState(AuditProcessStateEnum.InHand.toString());
				auditOperationDAO.update(aaoEo);
			}
			String operation = null;
			if (OperationTypeEnum.Content.toString().equals(aaoEo.getOpType())) {
				operation = ContentAuditTypeEnum.getName(aaoEo.getAuditType());
			}else{
				operation = GroupAuditTypeEnum.getName(aaoEo.getAuditType());
			}
			aaoEo.setMatterName(OperationTypeEnum.getName(aaoEo.getOpType())+operation);
			map.put("audit", aaoEo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			throw new SeudRuntimeException("001", "审核处理中:变更审核状态失败");
		}
		try {
			//补充业务数据
			if(OperationTypeEnum.Content.toString().equals(aaoEo.getOpType())){
				ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class, aaoEo.getOpObjId());
				if(null==contentEO){
					throw new SeudRuntimeException("001","审核数据异常，请告知管理员");
				}
				contentEO.setShowType(ShowTypeEnum.getName(contentEO.getShowType()));
				contentEO.setGeneralizeLabel(GeneralizeLabelEnum.getName(contentEO.getGeneralizeLabel()));
				String groupName = null;
				if(StringUtils.isNotEmpty(contentEO.getGroupId())){
					//获取分组名称
					String sql = " select GROUP_CONCAT( DISTINCT name) from article_consult_group where id in ('"+contentEO.getGroupId().replaceAll(",", "','")+"')";
					groupName = consultGroupDAO.queryForObj(String.class, sql);
				}
				map.put("groupName", groupName);
				map.put("data", contentEO);
			}else{
				ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, aaoEo.getOpObjId());
				if(null!=groupEO){
					map.put("parentGroupName", groupEO.getParentName());
				}
				if(StringUtils.isNotEmpty(aaoEo.getBeforeDataSnapshot())){
					ArticleGroupEO oldGroupEO =  JSON.parseObject(aaoEo.getBeforeDataSnapshot(), ArticleGroupEO.class);
					map.put("oldGroupName", oldGroupEO.getName());
					map.put("oldGroupRemark", oldGroupEO.getRemark());
				}
				if(StringUtils.isNotEmpty(aaoEo.getAfterDataSnapshot())){
					ArticleGroupEO oldGroupEO =  JSON.parseObject(aaoEo.getAfterDataSnapshot(), ArticleGroupEO.class);
					map.put("groupName", oldGroupEO.getName());
					map.put("groupRemark", oldGroupEO.getRemark());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
		}
		return map;
	}
	
	/**
	 * (non-Javadoc) 审核处理中 变更审核状态为审核中(主要应用于当审核信息被打开之后，该信息不能进行任何变更处理)
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Map<String, Object> groupAuditInHand(String taskId, SysUserInfo sysUserInfo) {
		Map<String, Object> map = new HashMap<>();
		ArticleAuditOperationEO aaoEo = null;
		try {
			map.put("taskId", taskId);
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("taskId", taskId);
			ResponseBodyInfo<Map<String,Object>> rp=workflow.queryTask(JSON.toJSONString(params));
			Map<String,Object> data=rp.getData();
			String processInstanceId=String.valueOf(data.get("processInstanceId"));
			ProcessExecuteInfoEO executeInfoEO=new ProcessExecuteInfoEO();
			executeInfoEO.setProcessInstanceId(processInstanceId);
			ResponseBodyInfo<String> resp=workflow.queryNewestChangeInfoInfoByProcessInstancdId(executeInfoEO);
			Type type = new TypeToken<ArticleAuditOperationEO>() {}.getType();
			aaoEo=GsonUtils.getGson().fromJson(resp.getData(), type);
	/*		if (AuditProcessStateEnum.No.toString().equals(aaoEo.getProcessState())
					|| AuditProcessStateEnum.Wait.toString().equals(aaoEo.getProcessState())) {
				aaoEo.setProcessState(AuditProcessStateEnum.InHand.toString());
				auditOperationDAO.update(aaoEo);
			}*/
			String operation = null;
			if (OperationTypeEnum.Content.toString().equals(aaoEo.getOpType())) {
				operation = ContentAuditTypeEnum.getName(aaoEo.getAuditType());
			}else{
				operation = GroupAuditTypeEnum.getName(aaoEo.getAuditType());
			}
			aaoEo.setMatterName(OperationTypeEnum.getName(aaoEo.getOpType())+operation);
			map.put("audit", aaoEo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			throw new SeudRuntimeException("001", "审核处理中:变更审核状态失败");
		}
		try {
			//补充业务数据
			if(OperationTypeEnum.Content.toString().equals(aaoEo.getOpType())){
				ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class, aaoEo.getOpObjId());
				if(null==contentEO){
					throw new SeudRuntimeException("001","审核数据异常，请告知管理员");
				}
				contentEO.setShowType(ShowTypeEnum.getName(contentEO.getShowType()));
				contentEO.setGeneralizeLabel(GeneralizeLabelEnum.getName(contentEO.getGeneralizeLabel()));
				String groupName = null;
				if(StringUtils.isNotEmpty(contentEO.getGroupId())){
					//获取分组名称
					String sql = " select GROUP_CONCAT( DISTINCT name) from article_consult_group where id in ('"+contentEO.getGroupId().replaceAll(",", "','")+"')";
					groupName = consultGroupDAO.queryForObj(String.class, sql);
				}
				map.put("groupName", groupName);
				map.put("data", contentEO);
			}else{
				ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, aaoEo.getOpObjId());
				if(null!=groupEO){
					map.put("parentGroupName", groupEO.getParentName());
				}
				if(StringUtils.isNotEmpty(aaoEo.getBeforeDataSnapshot())){
					ArticleGroupEO oldGroupEO =  JSON.parseObject(aaoEo.getBeforeDataSnapshot(), ArticleGroupEO.class);
					map.put("oldGroupName", oldGroupEO.getName());
					map.put("oldGroupRemark", oldGroupEO.getRemark());
				}
				if(StringUtils.isNotEmpty(aaoEo.getAfterDataSnapshot())){
					ArticleGroupEO oldGroupEO =  JSON.parseObject(aaoEo.getAfterDataSnapshot(), ArticleGroupEO.class);
					map.put("groupName", oldGroupEO.getName());
					map.put("groupRemark", oldGroupEO.getRemark());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
		}
		return map;
	}

	/**
	 * (non-Javadoc) 审核通过（执行工作流任务） 判断当前工作流节点。 若当前节点为最后执行节点则具体业务执行
	 * 根据当前业务数据所需要执行的类型 若当前节点非最后执行节点则不做任何具体业务操作 仅仅增加审核记录 提交工作流 变更审核数据
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean auditPass(String id, SysUserInfo sysUserInfo) {
		ArticleAuditOperationEO aaoEo = null; 
		try {
			ProcessExecuteInfoEO executeInfoEO = new ProcessExecuteInfoEO();
			executeInfoEO.setProcessInstanceId(id);
			String aaoEoJsonText = workflowClient.queryNewestEffectiveInfoInfoByProcessInstancdId(executeInfoEO).getData();
			aaoEo = JSON.parseObject(aaoEoJsonText, ArticleAuditOperationEO.class);
//			aaoEo = auditOperationDAO.findByPrimaryKey(ArticleAuditOperationEO.class, id);
		} catch (Exception e) {
			throw new SeudRuntimeException("001", "审核操作:业务数据获取失败，请联系管理员");
		}
		if(null==aaoEo){
			throw new SeudRuntimeException("001", "审核操作:未找到指定业务数据");
		}
		String excutenode = "";
		String taskId = null;
		try {
			// 获取当前工作流节点
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("processInstanceId", id);
			params.put("pageSize", 10);
			params.put("pageNo", 1);
			ResponseBodyInfo<PageInfo<Map>> workflowResult = this.workflowClient
					.queryTaskList(GsonUtils.object2Json(params));
			PageInfo<Map> resultMap = workflowResult.getData();
			if (null == resultMap) {
				throw new SeudRuntimeException("001", "审核操作:未找到流程实例信息,操作失败");
			}
			if (null != resultMap.getResultsList() && 0 != resultMap.getResultsList().size()) {
				Map<String, Object> map = resultMap.getResultsList().get(0);
				excutenode = (String) map.get("taskDefinitionKey");
				taskId = (String) map.get("taskId");
			}
			if (null == excutenode) {
				excutenode = "";
			}
			if (null == taskId) {
				throw new SeudRuntimeException("001", "审核操作:未找到任务id,操作失败");
			}
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new SeudRuntimeException("001", "审核操作:提取工作流数据失败，请联系管理员");
		}
		
		// 当前节点执行节点为最后执行节点
		/*if ("OperatingCompanyVicePresidentAudit".equals(excutenode)||
				"ExchangeSaleAuditCommissionerAudit".equals(excutenode)||
				"OperatingCompanyVicePresidentAudit".equals(excutenode)) {
			aaoEo.setProcessState(AuditProcessStateEnum.Finish.toString());
			aaoEo.setFinishTime(new Date());
			aaoEo.setIsPassAudit(BooleanTypeEnum.Y.toString());
			// 文章审核数据处理
			if (OperationTypeEnum.Content.toString().equals(aaoEo.getOpType())) {
				if (ContentAuditTypeEnum.Release.toString().equals(aaoEo.getAuditType())) {
					consultContentService.publishContentAuditPass(aaoEo);
				} else if (ContentAuditTypeEnum.Remove.toString().equals(aaoEo.getAuditType())) {
					consultContentService.removeContentAuditPass(aaoEo);
				} else {
					throw new SeudRuntimeException("001", "审核操作:当前业务类型异常,操作失败");
				}
				// 分组审核数据处理
			} else if (OperationTypeEnum.AGroup.toString().equals(aaoEo.getOpType())) {
				if (GroupAuditTypeEnum.Adding.toString().equals(aaoEo.getAuditType())) {
					consultGroupService.saveGroupAuditPass(aaoEo);
				} else if (GroupAuditTypeEnum.Change.toString().equals(aaoEo.getAuditType())) {
					consultGroupService.updateGroupAuditPass(aaoEo);
				} else if (GroupAuditTypeEnum.Remove.toString().equals(aaoEo.getAuditType())) {
					consultGroupService.deleteGroupAuditPass(aaoEo);
				} else {
					throw new SeudRuntimeException("001", "审核操作:当前业务类型异常,操作失败");
				}
			} else {
				throw new SeudRuntimeException("001", "审核类型不在受理范围内");
			}
		}*/
		
		ArticleAuditRecordEO recordEO = new ArticleAuditRecordEO();
		recordEO.setId(UUIDGenerator.getUUID());
		recordEO.setBoId(aaoEo.getId());
		recordEO.setAuditTask(aaoEo.getMatterName());
		recordEO.setAuditResult(BooleanTypeEnum.Y.toString());
		recordEO.setAuditTime(new Date());
		recordEO.setAuditrId(sysUserInfo.getUserId());
		recordEO.setAuditr(sysUserInfo.getFullName());
		recordEO.setAuditRole(sysUserInfo.getRoleName());
		if (null == aaoEo.getRecordEOs()) {
			aaoEo.setRecordEOs(new ArrayList<ArticleAuditRecordEO>());
		}
		aaoEo.getRecordEOs().add(recordEO);
		try {
			auditOperationDAO.updateAuditOperationEO(aaoEo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			throw new RuntimeException("审核操作:业务数据存储失败，请联系管理员");
		}
		
		// 提交工作流
		FlowParamsBean flowParams = FlowParamsBean.getInstance().addParams("userId", sysUserInfo.getUserId())
				.addParams("taskId", taskId)
				.addVariables("isAgree", true)
				.addVariables("userName", sysUserInfo.getShorthand())
				.addVariables("busiApprove", "true")
				.addVariables("taskName", "文章管理-")
				.addlocalVariables("result", true);
		if (!workflowClient.executeTask(flowParams.toJSONString()).isOk()) {
			throw new RuntimeException("审核操作:提交失败");
		}
		return true;
	}

	/**
	 * (non-Javadoc) 审核驳回 变更业务数据 提交工作流 变更审核数据
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean auditReject(String id, String remark, SysUserInfo sysUserInfo) {
		ArticleAuditOperationEO aaoEo = null;
		try{
			
			ProcessExecuteInfoEO executeInfoEO = new ProcessExecuteInfoEO();
			executeInfoEO.setProcessInstanceId(id);
			String aaoEoJsonText = workflowClient.queryNewestEffectiveInfoInfoByProcessInstancdId(executeInfoEO).getData();
			aaoEo = JSON.parseObject(aaoEoJsonText, ArticleAuditOperationEO.class);
//			aaoEo = auditOperationDAO.findByPrimaryKey(ArticleAuditOperationEO.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			throw new RuntimeException("审核操作:业务数据提取失败，请联系管理员");
		}
		if(null==aaoEo){
			throw new RuntimeException("审核操作:业务数据未找到");
		}
		
		try{
			// 文章审核数据处理
			if (OperationTypeEnum.Content.toString().equals(aaoEo.getOpType())) {
				ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class,
						aaoEo.getOpObjId());
				contentEO.setEditState(ContentEditStateEnum.Draft.toString());
				contentEO = consultContentDAO.update(contentEO);
				// 分组审核数据处理
			} else if (OperationTypeEnum.AGroup.toString().equals(aaoEo.getOpType())) {
				ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, aaoEo.getOpObjId());
				groupEO = consultGroupDAO.update(groupEO);
			} else {
				throw new SeudRuntimeException("001", "审核类型不在受理范围内");
			}
			// 保存审核数据
			aaoEo.setProcessState(AuditProcessStateEnum.Finish.toString());
			aaoEo.setFinishTime(new Date());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			ArticleAuditRecordEO recordEO = new ArticleAuditRecordEO();
			recordEO.setId(UUIDGenerator.getUUID());
			recordEO.setBoId(aaoEo.getId());
			recordEO.setAuditTask(aaoEo.getMatterName());
			recordEO.setAuditResult(BooleanTypeEnum.N.toString());
			recordEO.setCause(remark);
			recordEO.setAuditTime(new Date());
			recordEO.setAuditrId(sysUserInfo.getUserId());
			recordEO.setAuditr(sysUserInfo.getFullName());
			recordEO.setAuditRole(sysUserInfo.getRoleName());
			if (null == aaoEo.getRecordEOs()) {
				aaoEo.setRecordEOs(new ArrayList<ArticleAuditRecordEO>());
			}
			aaoEo.getRecordEOs().add(recordEO);
//			auditOperationDAO.updateAuditOperationEO(aaoEo);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			throw new RuntimeException("审核操作:业务数据存储失败，请联系管理员");
		}
		
		// 获取工作流  任务id
		// 获取当前工作流节点
		String taskId = null;
		try{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("processInstanceId", id);
			params.put("pageSize", 10);
			params.put("pageNo", 1);
			ResponseBodyInfo<PageInfo<Map>> workflowResult = this.workflowClient
					.queryTaskList(GsonUtils.object2Json(params));
			PageInfo<Map> resultMap = workflowResult.getData();
			if (null == resultMap) {
				throw new SeudRuntimeException("001", "审核驳回:未找到流程实例信息,操作失败");
			}
			if (null != resultMap.getResultsList() && 0 != resultMap.getResultsList().size()) {
				Map<String, Object> map = resultMap.getResultsList().get(0);
				taskId = (String) map.get("taskId");
			}
			if (null == taskId) {
				throw new SeudRuntimeException("001", "审核驳回:未找到任务id,操作失败");
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("审核",e);
			throw new RuntimeException("审核操作:业务数据存储失败，请联系管理员");
		}
		
		// 提交工作流
		FlowParamsBean flowParams = FlowParamsBean.getInstance().addParams("userId", sysUserInfo.getUserId())
				.addParams("taskId", taskId)
				.addVariables("isAgree", false)
				.addVariables("userName", sysUserInfo.getShorthand())
				.addVariables("busiApprove", "false")
				.addVariables("taskName", "文章审核")
				.addVariables("auditRemark", remark)
				.addlocalVariables("cause", remark)
				.addlocalVariables("result", false);
		if (!workflowClient.executeTask(flowParams.toJSONString()).isOk()) {
			throw new RuntimeException("审核驳回:审核驳回提交失败");
		}
		sendTaskRejectMessage("文章发布审核",remark,aaoEo.getInitiatorId());
		return true;
	}
	
	/**
	 * 发送任务提醒信息
	 * @param systemName
	 * @param message
	 * @param userCode
	 */
	public void sendTaskRejectMessage(String systemName,String message,String userCode)	{
	        // 平台消息
	        MessageBean messageBean = new MessageBean();
	   try{
	        messageBean.setBusinessCode("210");
	        ResponseBodyInfo<List<MessageUserBean>> result2 = backgroundClient.getMessageUserBeansByUserCodes(userCode);
	        if(null==result2||!result2.isOk()){
	        	logger.error("sendTaskRejectMessage error,userCode="+userCode);
	        	return;
	        }
	        messageBean.setMessageUserBeans(result2.getData());
	        messageBean.setTargetUserType(4);
	        messageBean.addClientChannel();
	        messageBean.addPlaceholder("system_name", systemName);
	        messageBean.addPlaceholder("message_content", message);
	        messageSender.sendMessage(messageBean);
		}catch(Exception e){
			logger.error("sendTaskRejectMessage error,messageBean="+JSON.toJSONString(messageBean),e);
		}
	}


}
