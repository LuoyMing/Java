package com.seud.fyb.article.dao.impl;

import java.util.Hashtable;
import java.util.List;

import org.springframework.stereotype.Component;

import com.seud.fyb.article.dao.IConsultClassifyDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleClassifyEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.DaoImpl;
import com.wfw.common.utils.StringUtils;

@Component("consultClassifyDAO")
public class ConsultClassifyDAOImpl extends DaoImpl implements IConsultClassifyDAO {
	
	private static final String SORT_CODE = " order by t.create_time desc ";

	@Override
	public List<ArticleClassifyEO> queryListAll() {
		String sql = " select * from article_consult_classify t " + SORT_CODE;
		return super.execSqlQuery(ArticleClassifyEO.class, sql);
	}
	
	@Override
	public List<ArticleClassifyEO> queryListByRootClassify(String parentId) {
		String sql = " select * from article_consult_classify t where parent_id=? " + SORT_CODE;
		return super.execSqlQuery(ArticleClassifyEO.class, sql,parentId);
	}

	@Override
	public PageInfo<ArticleClassifyEO> queryListPageByKeyword(String keyword,Integer pageSize, Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from article_consult_classify t ");
		Hashtable<String, Object> param = new Hashtable<>();
		if(StringUtils.isNotEmpty(keyword)) {
			param.put("name",  "%" + keyword + "%");
			sqlb.append(" where t.name like :name ");
		}
		sqlb.append(SORT_CODE);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticleClassifyEO.class, pageSize, pageNo, param);
	}
}
