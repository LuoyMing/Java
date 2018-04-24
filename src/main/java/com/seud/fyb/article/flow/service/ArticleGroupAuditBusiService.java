package com.seud.fyb.article.flow.service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.seud.fyb.article.dao.IConsultGroupDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleGroupEnum;
import com.seud.fyb.feignclient.article.model.enums.AuditProcessStateEnum;
import com.seud.fyb.feignclient.article.model.enums.BooleanTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.GroupAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.OperationTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.PublishStateEnum;
import com.seud.fyb.feignclient.workflow.model.enums.FlowItemsTypeEnum;
import com.seud.fyb.framework.annotation.ValidatorCase;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.GsonUtils;
import com.seud.fyb.framework.utils.StringUtils;
import com.seud.fyb.workflow.business.service.IFlowItemsRecordService;
import com.seud.fyb.workflow.business.service.IWorkFLowExecuteHandler;
import com.wfw.common.utils.ContextUtils;
import com.wfw.common.utils.UUIDGenerator;

/**
 * @ClassName: ArticleGroupAuditBusiService
 * @Description: 文章分组工作流业务执行类
 * @author luoyiming
 */
@Service("articleGroupAuditAuditBusiService")
public class ArticleGroupAuditBusiService{
	
	private static Logger logger = LoggerFactory.getLogger(ArticleGroupAuditBusiService.class);
	private static final String flowId = FlowItemsTypeEnum.ArticleGroupAudit.getFlowId();//"ArticleGroupAudit";
	private static final String flowName = FlowItemsTypeEnum.ArticleGroupAudit.getFlowName();
	//1立即生效 2定时生效
	private static int effectiveType = FlowItemsTypeEnum.ArticleGroupAudit.getEffectiveType();//EffectiveTypeEnum.IMMEDIATE.getValue();
	
	@Autowired
	private IConsultGroupDAO consultGroupDAO;
	@Autowired
	private IFlowItemsRecordService flowItemsRecordService;
	
	@Transactional
	@ValidatorCase(validatorId="DefaultServiceValidator")
	public Map<String, Object> startCallBack(Map<String, Object> params) {
		logger.info("ArticleGroupAudit startCallBack... ,params:"+GsonUtils.object2Json(params));
		String applyUserId=String.valueOf(params.get("applyUserId")); 
		String applyUserName=String.valueOf(params.get("applyUserName")); 
		String startRole=String.valueOf(params.get("startRole")); 
		
		String groupAuditType=String.valueOf(params.get("groupAuditType"));
		ArticleAuditOperationEO aaoEo = new ArticleAuditOperationEO();
		if(String.valueOf(GroupAuditTypeEnum.Adding).equals(groupAuditType)){//添加分组操作
			String parentId=String.valueOf(params.get("parentId")); 
			String name=String.valueOf(params.get("name")); 
			String remark=String.valueOf(params.get("remark")); 
			ArticleGroupEO groupEO = new ArticleGroupEO();
			groupEO.setId(UUIDGenerator.getUUID());
			groupEO.setName(name);
			groupEO.setParentId(parentId);
			groupEO.setParentName(ArticleGroupEnum.getName(parentId));
			groupEO.setPublishState(PublishStateEnum.N.toString());
			groupEO.setRemark(remark);
			groupEO.setCreaterId(applyUserId);
			groupEO.setCreater(applyUserName);
			groupEO.setCreateTime(new Date());
			int sort = 0;
			try {
				sort = consultGroupDAO.findMaxGroup();
			} catch (Exception e) {
			}
			groupEO.setSort(sort+1);
			consultGroupDAO.save(groupEO);
			aaoEo.setOpObjId(groupEO.getId());
			aaoEo.setAuditType(GroupAuditTypeEnum.Adding.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.Wait.toString());
			aaoEo.setOpType(OperationTypeEnum.AGroup.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setInitiatTime(new Date());
			aaoEo.setBeforeDataSnapshot(null);
			aaoEo.setAfterDataSnapshot(JSON.toJSONString(groupEO));
			
		}else if(String.valueOf(GroupAuditTypeEnum.Change).equals(groupAuditType)){//添加分组操作
			String id=String.valueOf(params.get("id")); 
			String name=String.valueOf(params.get("name")); 
			String remark=String.valueOf(params.get("remark")); 
			ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, id);
			if (null == groupEO) {
				throw new SeudRuntimeException("001", "分组未找到");
			}
			ArticleGroupEO editGroupEO = new ArticleGroupEO();
			BeanUtils.copyProperties(groupEO, editGroupEO);
			editGroupEO.setName(name);
			editGroupEO.setRemark(remark);
			editGroupEO.setUpdater(applyUserId);
			editGroupEO.setUpdaterId(applyUserName);
			editGroupEO.setUpdateTime(new Date());
			String afterDataSnapshot = JSON.toJSONString(editGroupEO);
			String beforeDataSnapshot = JSON.toJSONString(groupEO);
			aaoEo.setOpObjId(groupEO.getId());
			aaoEo.setAuditType(GroupAuditTypeEnum.Change.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.Wait.toString());
			aaoEo.setOpType(OperationTypeEnum.AGroup.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setInitiatTime(new Date());
			aaoEo.setBeforeDataSnapshot(beforeDataSnapshot);
			aaoEo.setAfterDataSnapshot(afterDataSnapshot);
		}else if(String.valueOf(GroupAuditTypeEnum.Remove).equals(groupAuditType)){//添加分组操作
			String id=String.valueOf(params.get("id")); 
			ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, id);
			if (null == groupEO) {
				throw new SeudRuntimeException("001", "分组未找到");
			}
			String beforeDataSnapshot = JSON.toJSONString(groupEO);
			// 修改分组信息
			groupEO.setDeleterId(applyUserId);
			groupEO.setDeleter(applyUserName);
			groupEO.setDeleteTime(new Date());
			consultGroupDAO.update(groupEO);
			groupEO.setDeleteState(BooleanTypeEnum.Y.toString());
			String afterDataSnapshot = JSON.toJSONString(groupEO);
			aaoEo.setOpObjId(groupEO.getId());
			aaoEo.setAuditType(GroupAuditTypeEnum.Remove.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.Wait.toString());
			aaoEo.setOpType(OperationTypeEnum.AGroup.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setInitiatTime(new Date());
			aaoEo.setBeforeDataSnapshot(beforeDataSnapshot);
			aaoEo.setAfterDataSnapshot(afterDataSnapshot);
		}
		if(StringUtils.isEmpty(startRole)){
			startRole = "tradeUser";
		}
		String flowInstanceId=String.valueOf(params.get("processInstanceId")); 
		params.put("changeInfo",GsonUtils.object2Json(aaoEo));
		params.put("effectiveInfo", GsonUtils.object2Json(aaoEo));
		//添加业务表数据
		String audititemsId=UUIDGenerator.getUUID();
		try{
		flowItemsRecordService.addFlowAduitItemsInfo(audititemsId,flowId, flowName,effectiveType,aaoEo.getOpObjId(),
				applyUserId, startRole, flowInstanceId, "");
		}catch(Exception e){
			logger.error("ArticleGroupAuditAuditBusiService,addFlowAduitItemsInfo执行失败！");
			throw new RuntimeException("执行失败");
		}
		params.put("audititemsId", audititemsId);
		return params;
	}
	public Map<String, Object> firstCallBack(Map<String, Object> params) {
		boolean isAgree=Boolean.parseBoolean(String.valueOf(params.get("isAgree")));
		params.put("operApprove", isAgree?"true":"false");
		String audititemsId=String.valueOf(params.get("audititemsId"));
		//更新审事项信息表
		if(!flowItemsRecordService.updateFlowAduitItmesInfo(audititemsId, isAgree, effectiveType)
				){
			logger.error("ArticleGroupAuditAuditBusiService,updateMembAduitItmesInfo执行失败！");
			throw new RuntimeException("执行失败");
		}
		if(isAgree){
			effective(params);
		}
		return params;
	}
	
	private boolean effective(Map<String,Object> resultMap){
		String processInstanceId = resultMap.get("processInstanceId").toString();
		if(effectiveType==1){
			Map<String, Object> execVriables =new HashMap<String,Object >();
			execVriables.put("processInstanceId", processInstanceId);
			IWorkFLowExecuteHandler workFLowExecuteHandler = ContextUtils.getBean(FlowItemsTypeEnum.ArticleGroupAudit.getHandlerName());
			workFLowExecuteHandler.approvedfinishedUserChange(execVriables);
		}
		return  true;
	}
	public Map<String, Object> endCallBack(Map<String, Object> params) {
		return null;
	}
}
