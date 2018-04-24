package com.seud.fyb.article.dao.impl;

import java.util.Hashtable;
import java.util.List;

import org.springframework.stereotype.Component;

import com.seud.fyb.article.dao.IConsultGroupDAO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupContentCorrelationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupTypeEO;
import com.seud.fyb.feignclient.article.model.enums.DeleteStateEnum;
import com.seud.fyb.feignclient.article.model.enums.PublishStateEnum;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.DaoImpl;
import com.wfw.common.utils.StringUtils;

@Component("consultGroupDAO")
public class ConsultGroupDAOImpl extends DaoImpl implements IConsultGroupDAO {
	
	private static final String ISSUE = " and t.publish_state = '"+PublishStateEnum.Y.toString()+"'  ";
	
	private static final String NOT_DELETE = " and ( t.delete_state <> '"+DeleteStateEnum.Y.toString()+"' or t.delete_state is null ) ";
	
	private static final String SORT_CODE = " order by t.sort desc ";
	
	@Override
	public List<ArticleGroupEO> queryListAll(String groupType) {
		String sql = " select * from article_consult_group t where 1=1 " + ISSUE + NOT_DELETE +SORT_CODE;
		return super.execSqlQuery(ArticleGroupEO.class, sql);
	}

	@Override
	public List<ArticleGroupEO> queryListByRootGroup(String parentId) {
		String sql = " select * from article_consult_group t where t.parent_id=? " +ISSUE + NOT_DELETE +SORT_CODE;
		return super.execSqlQuery(ArticleGroupEO.class, sql,parentId);
	}

	@Override
	public PageInfo<ArticleGroupEO> queryListPageByKeyword(String keyword, Integer pageSize, Integer pageNo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" select * from article_consult_group t ");
		Hashtable<String, Object> param = new Hashtable<>();
		sqlb.append(" where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			param.put("name",  "%" + keyword + "%");
			sqlb.append(" and t.name like :name ");
		}
		sqlb.append(NOT_DELETE);
		sqlb.append(SORT_CODE);
		return super.queryRecordByClassForPageInfoWithParam(sqlb.toString(), ArticleGroupEO.class, pageSize, pageNo, param);
	}

	@Override
	public ArticleGroupContentCorrelationEO findOne(String groupId, String contentId) {
		String sql = " select * from article_group_content_correlation t where t.group_id = '"+groupId+"' and t.content_id = '"+contentId+"'";
		return super.execSqlQueryOne(ArticleGroupContentCorrelationEO.class, sql);
	}

	@Override
	public Integer findMaxGroupContentCorrelation(String groupId) {
		String sql = " select max(content_sort) from article_group_content_correlation t where t.group_id = '"+groupId+"'";
		return super.queryForInt(sql);
	}

	@Override
	public void updateGroupContentCorrelation(ArticleGroupContentCorrelationEO contentCorrelationEO) {
		String sql = " update article_group_content_correlation t set t.content_sort = ? where t.group_id = ? and t.content_id = ?";
		super.execSqlUpdate(sql, contentCorrelationEO.getContentSort(),contentCorrelationEO.getGroupId(),contentCorrelationEO.getContentId());
	}

	@Override
	public List<ArticleGroupContentCorrelationEO> findByContentId(String id) {
		String sql = " select * from article_group_content_correlation t where t.content_id = '"+id+"'";
		return super.execSqlQuery(ArticleGroupContentCorrelationEO.class, sql);
	}

	@Override
	public List<ArticleGroupContentCorrelationEO> findByGroupId(String id) {
		String sql = " select * from article_group_content_correlation t where t.group_id = '"+id+"'";
		return super.execSqlQuery(ArticleGroupContentCorrelationEO.class, sql);
	}
	
	@Override
	public int findMaxGroup() {
		String sql = " select max(sort) from article_consult_group ";
		return super.queryForInt(sql);
	}

	@Override
	public List<ArticleGroupEO> findOneByName(String str) {
		String sql = " select * from article_consult_group t where t.name=? " + ISSUE + NOT_DELETE;
		
		return super.execSqlQuery(ArticleGroupEO.class, sql,str);
	}

	@Override
	public int countInGroupId(String inGroupStr) {
		String sql = " select count(*) from article_consult_group t where 1=1 and t.id in ("+inGroupStr+") "+ NOT_DELETE;
		return super.queryForInt(sql);
	}

	@Override
	public void deleteContentCorrelationByGroupId(String id) {
		String sql = "  delete from article_group_content_correlation  where  group_id = ?";
		super.execSqlUpdate(sql, id);
	}

	/**
	 * 查询分组类型
	 */
	@Override
	public List<ArticleGroupTypeEO> queryGroupType() {
		String sql = " select * from article_group_type order by sort ";
		return super.execSqlQuery(ArticleGroupTypeEO.class, sql);
	}

}
