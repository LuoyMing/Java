package com.seud.fyb.article.dao;

import java.util.List;

import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.IDao;

public interface IAuditOperationDAO extends IDao {
	
	List<ArticleAuditOperationEO> findByOpObjId(String opObjId);
	
	PageInfo<ArticleAuditOperationEO> queryListPageByKeyword(String keyword,String opType ,Integer pageSize, Integer pageNo);
	
	ArticleAuditOperationEO queryById(String id);
	
	void saveAuditOperationEO(ArticleAuditOperationEO aaoEo);

	void updateAuditOperationEO(ArticleAuditOperationEO aaoEo);

	List<ArticleAuditOperationEO> findByOpObjIdWithProcessState(String opObjId, String processState);

}
