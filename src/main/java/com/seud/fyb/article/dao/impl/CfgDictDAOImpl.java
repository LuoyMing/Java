package com.seud.fyb.article.dao.impl;

import java.util.Hashtable;

import org.springframework.stereotype.Component;

import com.seud.fyb.article.dao.ICfgDictDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleKellegEnum;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.DaoImpl;
import com.wfw.common.utils.StringUtils;

@Component("cfgDictDAO")
public class CfgDictDAOImpl extends DaoImpl implements ICfgDictDAO {

	@Override
	public PageInfo<ArticleKellegEO> queryListPageByKeywordKelleg(String keyword, Integer pageSize,
			Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from  article_kelleg t  ");
		Hashtable<String, Object> param = new Hashtable<>();
		sqlb.append(" where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			param.put("keyword",  "%" + keyword + "%");
			sqlb.append(" and (t.key_code like :keyword ");
			sqlb.append(" or t.relative_description like :keyword) ");
		}
		sqlb.append(" and t.type in ('"+ArticleKellegEnum.Grouping.toString()+"','"+ArticleKellegEnum.Protocol.toString()+"','"+ArticleKellegEnum.Template.toString()+"')");
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticleKellegEO.class, pageSize, pageNo, param);
	}

	@Override
	public ArticleKellegEO findKellegByRelevanceData(String relevanceData) {
		String sql = "select * from  article_kelleg t where 1=1 and t.relevance_data = ?";
		return super.execSqlQueryOne(ArticleKellegEO.class, sql,relevanceData);
	}

}
