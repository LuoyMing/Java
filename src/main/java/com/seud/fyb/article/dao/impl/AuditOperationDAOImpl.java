package com.seud.fyb.article.dao.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.stereotype.Component;

import com.seud.fyb.article.dao.IAuditOperationDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditRecordEO;
import com.seud.fyb.feignclient.article.model.enums.DeleteStateEnum;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.DaoImpl;
import com.wfw.common.utils.StringUtils;

@Component("auditOperationDAO")
public class AuditOperationDAOImpl extends DaoImpl implements IAuditOperationDAO {
	
	private static final String NOT_DELETE = " and ( t.delete_state <> '"+DeleteStateEnum.Y.toString()+"' or t.delete_state is null ) ";
	
	private static final String SORT_CODE = " order by initiat_time desc ";
	
	
	@Override
	public List<ArticleAuditOperationEO> findByOpObjId(String opObjId) {
		String sql = "select * from article_audit_business_operation where op_obj_id = ? " + SORT_CODE +"limit 0,40";
		List<ArticleAuditOperationEO> list = super.execSqlQuery(ArticleAuditOperationEO.class, sql, opObjId);
		if(null!=list&&list.size()>0){
			StringBuffer instr = new StringBuffer(" (");
			for (ArticleAuditOperationEO eo : list) {
				instr.append("'");
				instr.append(eo.getId());
				instr.append("',");
			}
			instr.deleteCharAt(instr.length()-1);
			instr.append(")");
			String rsql = "select * from article_audit_record where bo_id in "+instr.toString();
			List<ArticleAuditRecordEO> rlist = super.execSqlQuery(ArticleAuditRecordEO.class, rsql);
			for (ArticleAuditOperationEO eo : list) {
				eo.setRecordEOs(new ArrayList<ArticleAuditRecordEO>());
				for (ArticleAuditRecordEO reo : rlist) {
					if(eo.getId().equals(reo.getBoId())){
						eo.getRecordEOs().add(reo);
					}
				}
			}
			return list;
		}
		return list;
	}

	@Override
	public List<ArticleAuditOperationEO> findByOpObjIdWithProcessState(String opObjId, String processState) {
		String sql = "select * from article_audit_business_operation where op_obj_id = ? and process_state != ?";
		return super.execSqlQuery(ArticleAuditOperationEO.class, sql, opObjId,processState);
	}
	
	@Override
	public PageInfo<ArticleAuditOperationEO> queryListPageByKeyword(String keyword ,String opType ,Integer pageSize, Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from article_audit_business_operation t ");
		sqlb.append(" where 1=1 ");
		Hashtable<String, Object> param = new Hashtable<>();
		
		if(StringUtils.isNotEmpty(keyword)) {
			param.put("after_data_snapshot",  "%" + keyword + "%");
			sqlb.append(" and  t.after_data_snapshot like :after_data_snapshot ");
		}
		if(StringUtils.isNotEmpty(opType)) {
			param.put("op_type",  opType);
			sqlb.append(" and  t.op_type = :op_type ");
		}
		sqlb.append(SORT_CODE);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticleAuditOperationEO.class, pageSize, pageNo, param);
	}
	
	@Override
	public ArticleAuditOperationEO queryById(String id) {
		ArticleAuditOperationEO eo = super.findByPrimaryKey(ArticleAuditOperationEO.class, id);
		String rsql = "select * from article_audit_record where bo_id = ? ";
		List<ArticleAuditRecordEO> rlist = super.execSqlQuery(ArticleAuditRecordEO.class, rsql,id);
		eo.setRecordEOs(rlist);
		return eo;
	}

	@Override
	public void saveAuditOperationEO(ArticleAuditOperationEO aaoEo) {
		super.save(aaoEo);
		List<ArticleAuditRecordEO>rlist = aaoEo.getRecordEOs();
		if(null!=rlist){
			for (ArticleAuditRecordEO aEO : rlist) {
				super.save(aEO);
			}
		}
	}

	@Override
	public void updateAuditOperationEO(ArticleAuditOperationEO aaoEo) {
		super.update(aaoEo);
		if(null==aaoEo.getRecordEOs()){
			return;
		}
		String rsql = "select * from article_audit_record where bo_id = ? ";
		List<ArticleAuditRecordEO> rlist = super.execSqlQuery(ArticleAuditRecordEO.class, rsql,aaoEo.getId());
		for (ArticleAuditRecordEO recordEO : aaoEo.getRecordEOs()) {
			boolean isin = false;
			for (ArticleAuditRecordEO oRecordEO : rlist) {
				if(oRecordEO.getId().equals(recordEO.getId())){
					isin = true;
				}
			}
			if(!isin){
				super.save(recordEO);
			}
		}
	}
}
