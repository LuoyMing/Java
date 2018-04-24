package com.seud.fyb.article.flow.handler;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.seud.fyb.article.dao.IConsultGroupDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupContentCorrelationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.enums.BooleanTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.GroupAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.PublishStateEnum;
import com.seud.fyb.feignclient.workflow.WorkflowClient;
import com.seud.fyb.feignclient.workflow.model.ProcessExecuteInfoEO;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.GsonUtils;
import com.seud.fyb.workflow.business.handler.DefaultWorkFLowExecuteHandlerImpl;


/**
 * @ClassName: clearTimeParamWorkFLowExecuteHandlerImpl
 * @Description: 出入金时间工作流业务生效类
 * @author luoyiming
 */
@Service("articleGroupAuditWorkFLowExecuteHandlerImpl")
public class ArticleGroupAuditWorkFLowExecuteHandlerImpl extends DefaultWorkFLowExecuteHandlerImpl{
	private static Logger logger = LoggerFactory.getLogger(ArticleGroupAuditWorkFLowExecuteHandlerImpl.class);
	
	@Autowired
	private WorkflowClient workflowClient;
	
	@Resource(name = "consultGroupDAO")
	private IConsultGroupDAO consultGroupDAO;

	@Override
	public boolean approvedfinishedUserChange(Map<String, Object> execVriables) {
		String processInstanceId=String.valueOf(execVriables.get("processInstanceId"));
		try{
			//查询变更后数据
			ProcessExecuteInfoEO executeInfoEO =new ProcessExecuteInfoEO();
			executeInfoEO.setProcessInstanceId(processInstanceId);
			ResponseBodyInfo<String> resp=workflowClient.queryNewestChangeInfoInfoByProcessInstancdId(executeInfoEO);
			Type type = new TypeToken<ArticleAuditOperationEO>() {}.getType();
			ArticleAuditOperationEO aaoEo=GsonUtils.getGson().fromJson(resp.getData(), type);
			if(GroupAuditTypeEnum.Adding.toString().equals(aaoEo.getAuditType())){//添加
				try{
					ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, aaoEo.getOpObjId());
					groupEO.setPublishState(PublishStateEnum.Y.toString());
					//置顶
					int sort = 0;
					try {
						sort = consultGroupDAO.findMaxGroup();
					} catch (Exception e) {
					}
					groupEO.setSort(sort+1);
					consultGroupDAO.update(groupEO);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("分组",e);
					throw new SeudRuntimeException("001","保存分组数据失败，请联系管理员");
				}
			}else if(GroupAuditTypeEnum.Change.toString().equals(aaoEo.getAuditType())){//修改
				try {
					String afterDataSnapshot = aaoEo.getAfterDataSnapshot();
					ArticleGroupEO groupEO = JSON.parseObject(afterDataSnapshot, ArticleGroupEO.class);
					groupEO.setPublishState(PublishStateEnum.Y.toString());
					groupEO.setUpdateTime(new Date());
					consultGroupDAO.update(groupEO);
					
					//修改文章与分组中间表
					List<ArticleGroupContentCorrelationEO> correlationList = consultGroupDAO.findByGroupId(groupEO.getId());
					if(correlationList!=null&&correlationList.size()>0){
						for(ArticleGroupContentCorrelationEO correlation : correlationList){
							correlation.setGroupName(groupEO.getName());
							consultGroupDAO.update(correlation);
						}
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("分组",e);
					throw new SeudRuntimeException("001", "修改分组异常，请联系管理员");
				}
			}else if(GroupAuditTypeEnum.Remove.toString().equals(aaoEo.getAuditType())){//删除
				try{
					ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, aaoEo.getOpObjId());
					groupEO.setDeleteState(BooleanTypeEnum.Y.toString());
					consultGroupDAO.update(groupEO);
					//删除分组关系列表
					consultGroupDAO.deleteContentCorrelationByGroupId(groupEO.getId());
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("分组",e);
					throw new SeudRuntimeException("001","删除分组失败");
				}
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}
}
