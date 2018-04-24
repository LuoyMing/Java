package com.seud.fyb.article.dao.impl;

import java.util.Hashtable;

import org.springframework.stereotype.Component;

import com.seud.fyb.article.dao.IConsultUtilTemplateDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleFileManageEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleUtilTemplateEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.DaoImpl;
import com.wfw.common.utils.StringUtils;

@Component("consultUtilTemplateDAO")
public class ConsultUtilTemplateDAOImpl extends DaoImpl implements IConsultUtilTemplateDAO {
	
	private static final String SORT_CODE = " order by t.create_time desc ";

	@Override
	public PageInfo<ArticleUtilTemplateEO> queryListPageByKeyword(String keyword,String type, Integer pageSize, Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from article_util_template t ");
		Hashtable<String, Object> param = new Hashtable<>();
		sqlb.append(" where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			param.put("keyword",  "%" + keyword + "%");
			sqlb.append(" and ( t.title like :keyword or t.template_code like :keyword )");
		}
		if(StringUtils.isNotEmpty(type)) {
			param.put("type",  type);
			sqlb.append(" and t.type = :type");
		}
		sqlb.append(SORT_CODE);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticleUtilTemplateEO.class, pageSize, pageNo, param);
	}

	@Override
	public PageInfo<ArticleFileManageEO> listPageByFileManage(String type, Integer pageSize, Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from article_file_manage t");
		Hashtable<String, Object> param = new Hashtable<>();
		if(StringUtils.isNotEmpty(type)) {
			param.put("type",  type);
			sqlb.append(" where t.type = :type ");
		}
		sqlb.append(SORT_CODE);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticleFileManageEO.class, pageSize, pageNo, param);
	}


}
