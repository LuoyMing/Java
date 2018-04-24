package com.seud.fyb.article.flow.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.seud.fyb.article.flow.service.IArticleBusiService;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.enums.BooleanTypeEnum;
import com.seud.fyb.feignclient.workflow.WorkflowClient;
import com.seud.fyb.feignclient.workflow.model.ProcessExecuteInfoEO;
import com.seud.fyb.feignclient.workflow.model.enums.FlowItemsTypeEnum;
import com.seud.fyb.workflow.business.service.IFlowItemsRecordService;
import com.seud.fyb.workflow.business.service.IWorkFLowExecuteHandler;
import com.wfw.common.utils.ContextUtils;

@Service("CommonArticleServiceImpl")
public class CommonArticleServiceImpl  implements IArticleBusiService{
	
	
	@Autowired
	private WorkflowClient workflowClient;
	
	private static Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
	//1立即生效 2定时生效
	@Autowired
	private IFlowItemsRecordService flowItemsRecordService;
	@Override
	public Map<String,Object> start(Map<String,Object> params){
		FlowItemsTypeEnum flowItemsTypeEnum = FlowItemsTypeEnum.getByFlowId(params.get("processDefinitionKey").toString());
		Map<String,Object> busiMap = new HashMap<String, Object>();
		String audititemsId = String.valueOf(params.get("processInstanceId")); 
		String businessKey = params.get("businessKey").toString();
		String applyUserId=String.valueOf(params.get("applyUserId")); 
		String startRole=""; 
		String flowInstanceId = String.valueOf(params.get("processInstanceId")); 
		String cause = "";
		try{
			flowItemsRecordService.addFlowAduitItemsInfo(audititemsId,
					flowItemsTypeEnum.getFlowId(),
					flowItemsTypeEnum.getFlowName(),
					flowItemsTypeEnum.getEffectiveType(),
					businessKey,
				applyUserId, startRole, flowInstanceId, cause);
		}catch(Exception e){
			logger.error(flowItemsTypeEnum.getFlowName()+" start执行失败！");
			throw new RuntimeException("执行失败");
		}
		return busiMap;
	}
	
	@Override
	public Map<String,Object> audit(Map<String,Object> params){
		FlowItemsTypeEnum flowItemsTypeEnum = FlowItemsTypeEnum.getByFlowId(params.get("processDefinitionKey").toString());
		Map<String,Object> busiMap = new HashMap<String, Object>();
		boolean isAgree=Boolean.parseBoolean(String.valueOf(params.get("isAgree")));
		
			String audititemsId=String.valueOf(params.get("processInstanceId"));
			//更新审事项信息表
			if(!flowItemsRecordService.updateFlowAduitItmesInfo(audititemsId, isAgree, flowItemsTypeEnum.getEffectiveType())){
				logger.error(flowItemsTypeEnum.getFlowName()+" audit执行失败！");
				throw new RuntimeException("执行失败");
			}else{
				if(isAgree&&!effective(params,flowItemsTypeEnum)){
					throw new RuntimeException("执行生效失败");
				}
				
				ArticleAuditOperationEO aaoEo = null;
				try {
					ProcessExecuteInfoEO executeInfoEO = new ProcessExecuteInfoEO();
					executeInfoEO.setProcessInstanceId(audititemsId);
					String aaoEoJsonText = workflowClient.queryNewestEffectiveInfoInfoByProcessInstancdId(executeInfoEO).getData();
					aaoEo = JSON.parseObject(aaoEoJsonText, ArticleAuditOperationEO.class);
					aaoEo.setIsPassAudit(BooleanTypeEnum.Y.toString());
					busiMap.put("changeInfo", JSON.toJSONString(aaoEo));
					busiMap.put("effectiveInfo", JSON.toJSONString(aaoEo));
				}catch (Exception e) {
					throw new RuntimeException("执行失败");
				}
			}
		return busiMap;
	}
	
	private boolean effective(Map<String,Object> resultMap,FlowItemsTypeEnum flowItemsTypeEnum){
		if(flowItemsTypeEnum.getEffectiveType()==1){
			IWorkFLowExecuteHandler workFLowExecuteHandler = ContextUtils.getBean(flowItemsTypeEnum.getHandlerName());
			return workFLowExecuteHandler.approvedfinishedUserChange(resultMap);
		}
		return  true;
	}

}
