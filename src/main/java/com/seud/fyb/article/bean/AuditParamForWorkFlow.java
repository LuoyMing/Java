package com.seud.fyb.article.bean;

import java.io.Serializable;

import com.wfw.common.utils.StringUtils;


public class AuditParamForWorkFlow implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String taskId; // 任务ID
	
	private String applyUserId; // 审核人id
	
	private String userId;	// 申请人id

	private String flowId; //流程id

	private String businessKey; // 业务编号

	private String variablyBusiApprove; // 参数：审核结果

	private String variablyApplyUserId;// 参数：审核人id

	private String variablyApplyUserName;// 参数：操作人名称
	
	private String variablyCertificateBatchId;// 参数：产品批次号

	private String variablyIssuerNeedId;// 参数：发行商ID

	private String variablyTaskName;// 参数：任务名称

	private String variablyIsAddByOldBatch;// 参数：原品增发
	
	private String variablyOver;// 参数：工作流直接结束
	
	private String variablyToIssuer;// 参数：直接驳回到发行商
	
	private String variablyModifyPosition;// 参数：产品资料修改位置

	private String variablyApplyPublicityId;// 参数：公示ID
	
	private String variablyListingNo;// 参数：挂牌编号
	
	private String variablyApplyType;// 参数：业务类型
	
	private String variablyBppsId;// 参数：产品配售模板编号
	
	private String variablyBppsModifyId;// 参数：配售模板修改记录ID
	
	private String variablyBprlId;// 参数：产品配售转线上记录ID
	
	private String variablyBpflId;// 参数：产品配售冻结到发售方账户记录ID
	
	private String variablyBprplId;// 参数：产品重新配售记录ID
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (StringUtils.isNotEmpty(userId)) {
			result.append("{\"userId\":\"" + userId);
		}
		if (StringUtils.isNotEmpty(applyUserId)) {
			result.append("{\"applyUserId\":\"" + applyUserId);
		}
		if (StringUtils.isNotEmpty(taskId)) {
			result.append("\",\"taskId\":\"" + taskId);
		}
		if (StringUtils.isNotEmpty(flowId)) {
			result.append("\",\"flowId\":\"" + flowId);
		}
		if (StringUtils.isNotEmpty(businessKey)) {
			result.append("\",\"businessKey\":\"" + businessKey);
		}
		result.append("\",\"variables\":{");
		if (StringUtils.isNotEmpty(variablyApplyUserId)) {
			result.append("\"applyUserId\":\"" + variablyApplyUserId);
		}
		if (StringUtils.isNotEmpty(variablyApplyUserName)) {
			result.append("\",\"applyUserName\":\"" + variablyApplyUserName);
		}
		if (StringUtils.isNotEmpty(variablyCertificateBatchId)) {
			result.append("\",\"certificateBatchId\":\"" + variablyCertificateBatchId);
		}
		if (StringUtils.isNotEmpty(variablyBusiApprove)) {
			result.append("\",\"busiApprove\":\"" + variablyBusiApprove);
		}
		if (StringUtils.isNotEmpty(variablyIssuerNeedId)) {
			result.append("\",\"issuerNeedId\":\"" + variablyIssuerNeedId);
		}
		if (StringUtils.isNotEmpty(variablyTaskName)) {
			result.append("\",\"taskName\":\"" + variablyTaskName);
		}
		if (StringUtils.isNotEmpty(variablyIsAddByOldBatch)) {
			result.append("\",\"isAddByOldBatch\":\"" + variablyIsAddByOldBatch);
		}
		if (StringUtils.isNotEmpty(variablyOver)) {
			result.append("\",\"over\":\"" + variablyOver);
		}
		if (StringUtils.isNotEmpty(variablyToIssuer)) {
			result.append("\",\"toIssuer\":\"" + variablyToIssuer);
		}
		if (StringUtils.isNotEmpty(variablyModifyPosition)) {
			result.append("\",\"modifyPosition\":\"" + variablyModifyPosition);
		}
		
		if (StringUtils.isNotEmpty(variablyIsAddByOldBatch)) {
			result.append("\",\"applyPublicityId\":\"" + variablyApplyPublicityId);
		}
		if (StringUtils.isNotEmpty(variablyListingNo)) {
			result.append("\",\"listingNo\":\"" + variablyListingNo);
		}
		if (StringUtils.isNotEmpty(variablyApplyType)) {
			result.append("\",\"applyType\":\"" + variablyApplyType);
		}
		if (StringUtils.isNotEmpty(variablyBppsId)) {
			result.append("\",\"bppsId\":\"" + variablyBppsId);
		}
		if (StringUtils.isNotEmpty(variablyBppsModifyId)) {
			result.append("\",\"bppsModifyId\":\"" + variablyBppsModifyId);
		}
		if (StringUtils.isNotEmpty(variablyBprlId)) {
			result.append("\",\"bprlId\":\"" + variablyBprlId);
		}
		if (StringUtils.isNotEmpty(variablyBpflId)) {
			result.append("\",\"bpflId\":\"" + variablyBpflId);
		}
		if (StringUtils.isNotEmpty(variablyBprplId)) {
			result.append("\",\"bprplId\":\"" + variablyBprplId);
		}
		result.append("\"}");
		result.append("}");
		return result.toString();
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getVariablyApplyUserId() {
		return variablyApplyUserId;
	}

	public void setVariablyApplyUserId(String variablyApplyUserId) {
		this.variablyApplyUserId = variablyApplyUserId;
	}

	public String getVariablyCertificateBatchId() {
		return variablyCertificateBatchId;
	}

	public void setVariablyCertificateBatchId(String variablyCertificateBatchId) {
		this.variablyCertificateBatchId = variablyCertificateBatchId;
	}

	public String getVariablyIssuerNeedId() {
		return variablyIssuerNeedId;
	}

	public void setVariablyIssuerNeedId(String variablyIssuerNeedId) {
		this.variablyIssuerNeedId = variablyIssuerNeedId;
	}

	public String getVariablyTaskName() {
		return variablyTaskName;
	}

	public void setVariablyTaskName(String variablyTaskName) {
		this.variablyTaskName = variablyTaskName;
	}

	public String getVariablyApplyUserName() {
		return variablyApplyUserName;
	}

	public void setVariablyApplyUserName(String variablyApplyUserName) {
		this.variablyApplyUserName = variablyApplyUserName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVariablyBusiApprove() {
		return variablyBusiApprove;
	}

	public void setVariablyBusiApprove(String variablyBusiApprove) {
		this.variablyBusiApprove = variablyBusiApprove;
	}

	public String getVariablyIsAddByOldBatch() {
		return variablyIsAddByOldBatch;
	}

	public void setVariablyIsAddByOldBatch(String variablyIsAddByOldBatch) {
		this.variablyIsAddByOldBatch = variablyIsAddByOldBatch;
	}

	public String getVariablyOver() {
		return variablyOver;
	}

	public void setVariablyOver(String variablyOver) {
		this.variablyOver = variablyOver;
	}

	public String getVariablyToIssuer() {
		return variablyToIssuer;
	}

	public void setVariablyToIssuer(String variablyToIssuer) {
		this.variablyToIssuer = variablyToIssuer;
	}

	public String getVariablyModifyPosition() {
		return variablyModifyPosition;
	}

	public void setVariablyModifyPosition(String variablyModifyPosition) {
		this.variablyModifyPosition = variablyModifyPosition;
	}

	public String getVariablyApplyPublicityId() {
		return variablyApplyPublicityId;
	}

	public void setVariablyApplyPublicityId(String variablyApplyPublicityId) {
		this.variablyApplyPublicityId = variablyApplyPublicityId;
	}

	public String getVariablyListingNo() {
		return variablyListingNo;
	}

	public void setVariablyListingNo(String variablyListingNo) {
		this.variablyListingNo = variablyListingNo;
	}

	public String getVariablyApplyType() {
		return variablyApplyType;
	}

	public void setVariablyApplyType(String variablyApplyType) {
		this.variablyApplyType = variablyApplyType;
	}

	public String getVariablyBppsId() {
		return variablyBppsId;
	}

	public void setVariablyBppsId(String variablyBppsId) {
		this.variablyBppsId = variablyBppsId;
	}

	public String getVariablyBppsModifyId() {
		return variablyBppsModifyId;
	}

	public void setVariablyBppsModifyId(String variablyBppsModifyId) {
		this.variablyBppsModifyId = variablyBppsModifyId;
	}

	public String getVariablyBprlId() {
		return variablyBprlId;
	}

	public void setVariablyBprlId(String variablyBprlId) {
		this.variablyBprlId = variablyBprlId;
	}

	public String getVariablyBpflId() {
		return variablyBpflId;
	}

	public void setVariablyBpflId(String variablyBpflId) {
		this.variablyBpflId = variablyBpflId;
	}

	public String getVariablyBprplId() {
		return variablyBprplId;
	}

	public void setVariablyBprplId(String variablyBprplId) {
		this.variablyBprplId = variablyBprplId;
	}

	public String getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}


	
	
}
