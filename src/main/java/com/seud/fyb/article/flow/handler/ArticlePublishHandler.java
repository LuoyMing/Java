package com.seud.fyb.article.flow.handler;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.seud.fyb.article.service.IConsultContentService;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.workflow.WorkflowClient;
import com.seud.fyb.feignclient.workflow.model.ProcessExecuteInfoEO;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.workflow.business.service.IWorkFLowExecuteHandler;

@Service("articlePublishHandler")
public class ArticlePublishHandler implements IWorkFLowExecuteHandler {
	@Resource(name = "consultContentService")
	private IConsultContentService consultContentService;
	
	@Autowired
	private WorkflowClient workflowClient;
	
	@Override
	public boolean approvedfinishedUserChange(Map<String, Object> execVriables) {
		// TODO Auto-generated method stub
		String id = execVriables.get("processInstanceId").toString();
		ArticleAuditOperationEO aaoEo = null; 
		try {
			ProcessExecuteInfoEO executeInfoEO = new ProcessExecuteInfoEO();
			executeInfoEO.setProcessInstanceId(id);
			String aaoEoJsonText = workflowClient.queryNewestEffectiveInfoInfoByProcessInstancdId(executeInfoEO).getData();
			aaoEo = JSON.parseObject(aaoEoJsonText, ArticleAuditOperationEO.class);
			return consultContentService.publishContentAuditPass(aaoEo);
//			aaoEo = auditOperationDAO.findByPrimaryKey(ArticleAuditOperationEO.class, id);
		} catch (Exception e) {
			throw new SeudRuntimeException("001", "审核操作:业务数据获取失败，请联系管理员");
		}
	}

}
