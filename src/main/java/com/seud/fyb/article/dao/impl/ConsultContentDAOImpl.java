package com.seud.fyb.article.dao.impl;

import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.seud.fyb.article.dao.IConsultContentDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleBeforehandContentEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleGroupEnum;
import com.seud.fyb.feignclient.article.model.enums.DeleteStateEnum;
import com.seud.fyb.feignclient.article.model.enums.ShowTypeEnum;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.DaoImpl;

@Component("consultContentDAO")
public class ConsultContentDAOImpl extends DaoImpl implements IConsultContentDAO {
	
	private static final String SORT_CODE = " order by t.update_time desc,t.create_time desc ";

	@Override
	public PageInfo<ArticlePublishContentEO> queryListPageByKeyword(String keyword,String groupId,String appType, Integer pageSize, Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from  article_group_content_correlation t left join article_consult_publish_content t2 on t.content_id = t2.id ");
		Hashtable<String, Object> param = new Hashtable<>();
		sqlb.append(" where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			param.put("keyword",  "%" + keyword + "%");
			sqlb.append(" and (t2.title like :keyword ");
			sqlb.append(" or t2.relevance_breed_transaction_code like :keyword ");
			sqlb.append(" or t2.relevance_breed_code like :keyword ");
			sqlb.append(" or t2.relevance_breed like :keyword ");
			sqlb.append(" or t2.content_digest like :keyword) ");
		}
		String orderby = " order by t2.sort desc,t.content_sort desc ";
		if(StringUtils.isNotEmpty(groupId)) {
			param.put("groupId",  groupId);
			if(ArticleGroupEnum.inkey(groupId)){
				sqlb.append(" and t.parent_group_id = :groupId ");
				orderby = " order by t2.sort desc,t2.time_interval desc ";
			}else{
				sqlb.append(" and t.group_id = :groupId ");
			}
		}
		if(!"alltime".equals(appType)){
			sqlb.append(" and t2.time_interval < now() ");
		}
		if("true".equals(appType)){
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowApp.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' )");
		}else{
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowPc.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' )");
		}
		sqlb.append(orderby);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticlePublishContentEO.class, pageSize, pageNo, param);
	}

	@Override
	public PageInfo<ArticlePublishContentEO> queryListPageByGroup(String groupId, String appType,Integer pageSize, Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from  article_group_content_correlation t left join article_consult_publish_content t2 on t.content_id = t2.id ");
		Hashtable<String, Object> param = new Hashtable<>();
		sqlb.append(" where 1=1 ");
		
		String orderby = " order by t2.sort desc,t.content_sort desc ";
		if(StringUtils.isNotEmpty(groupId)) {
			param.put("groupId",  groupId);
			if(ArticleGroupEnum.inkey(groupId)){
				sqlb.append(" and t.parent_group_id = :groupId ");
				orderby = " order by t2.sort desc,t2.time_interval desc ";
			}else{
				sqlb.append(" and t.group_id = :groupId ");
			}
		}
		if(!"alltime".equals(appType)){
			sqlb.append(" and t2.time_interval < now() ");
		}
		if("true".equals(appType)){
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowApp.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' )");
		}else{
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowPc.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' )");
		}
		sqlb.append(orderby);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticlePublishContentEO.class, pageSize, pageNo, param);
	}

	@Override
	public PageInfo<ArticleBeforehandContentEO> queryListPageByKeywordBeforehand(String keyword,String classifyId, String relevanceBreedCode, Integer pageSize,
			Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select t.id,t.title,t.update_time,t.create_time,t.classify_name,t.edit_state,t.relevance_breed,t.group_id ");
		sqlb.append(" from article_consult_beforehand_content t where 1=1 ");
		
		Hashtable<String, Object> param = new Hashtable<>();
		if(StringUtils.isNotEmpty(keyword)) {
			param.put("keyword",  "%" + keyword + "%");
			sqlb.append(" and (t.title like :keyword ");
			sqlb.append(" or t.content_digest like :keyword) ");
		}
		if(StringUtils.isNotEmpty(relevanceBreedCode)) {
			param.put("relevanceBreedCode",  relevanceBreedCode);
			sqlb.append(" and t.relevance_breed_code = :relevanceBreedCode ");
		}
		if(StringUtils.isNotEmpty(classifyId)) {
			param.put("classifyId",  classifyId );
			sqlb.append(" and t.classify_id = :classifyId ");
		}
		param.put("delete_state",  DeleteStateEnum.Y.toString());
		sqlb.append(" and ( t.delete_state <> :delete_state or t.delete_state is null ) " );
		sqlb.append(SORT_CODE);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticleBeforehandContentEO.class, pageSize, pageNo, param);
	}

	@Override
	public List<ArticlePublishContentEO> listInPublish(String inPublish,String appType) {
		StringBuilder sqlb = new StringBuilder(" select * from  article_group_content_correlation t left join article_consult_publish_content t2 on t.content_id = t2.id where 1=1 and t2.id in ("+inPublish+") ");
		if(!"alltime".equals(appType)){
			sqlb.append(" and t2.time_interval < now() ");
		}
		if("true".equals(appType)){
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowApp.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' )");
		}else{
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowPc.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' )");
		}
		return super.execSqlQuery(ArticlePublishContentEO.class, sqlb.toString());
	}

	@Override
	public List<ArticlePublishContentEO> listTopGroupOne(String groupIds, String appType,int size) {
		StringBuilder sqlb = new StringBuilder(" select * from  article_group_content_correlation t left join article_consult_publish_content t2 on t.content_id = t2.id where 1=1 and t.group_id = ? and ( t2.sort = '' or t2.sort is null ) ");  
		if("true".equals(appType)){
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowApp.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' ) ");
		}else{
			sqlb.append(" and (t2.show_type = '"+ShowTypeEnum.ShowPc.toString()+"' or t2.show_type = '"+ShowTypeEnum.ShowAll.toString()+"' ) ");
		}
		sqlb.append(" order by t.content_sort desc limit "+size);
		return super.execSqlQuery(ArticlePublishContentEO.class, sqlb.toString(),groupIds);
	}

	@Override
	public List<ArticleBeforehandContentEO> listByGroupId(String groupId) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from article_consult_beforehand_content t ");
		sqlb.append(" where 1=1");
		sqlb.append(" and group_id like '%" + groupId + "%' ");
		sqlb.append(" and ( t.delete_state <> '"+DeleteStateEnum.Y.toString()+"' or t.delete_state is null ) " );
		return execSqlQuery(ArticleBeforehandContentEO.class, sqlb.toString());
	}

	@Override
	public List<ArticlePublishContentEO> getOneSpecialFieldPublish(String specialField) {
		String sql = "select * from article_consult_publish_content where special_field = ? ";
		return execSqlQuery(ArticlePublishContentEO.class,sql,specialField);
	}
}
