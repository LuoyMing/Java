package com.seud.fyb.article.dao;

import java.util.List;

import com.seud.fyb.feignclient.article.model.entity.ArticleGroupContentCorrelationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupTypeEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.IDao;

public interface IConsultGroupDAO extends IDao {
	
	List<ArticleGroupEO> queryListAll(String groupType);

	List<ArticleGroupEO> queryListByRootGroup(String parentId);
	
	PageInfo<ArticleGroupEO> queryListPageByKeyword(String keyword,Integer pageSize, Integer pageNo);

	ArticleGroupContentCorrelationEO findOne(String groupId, String contentId);

	Integer findMaxGroupContentCorrelation(String groupId);

	void updateGroupContentCorrelation(ArticleGroupContentCorrelationEO contentCorrelationEO);

	List<ArticleGroupContentCorrelationEO> findByContentId(String id);
	
	List<ArticleGroupContentCorrelationEO> findByGroupId(String id);

	int findMaxGroup();

	List<ArticleGroupEO> findOneByName(String str);

	int countInGroupId(String inGroupStr);

	void deleteContentCorrelationByGroupId(String id);
	
	List<ArticleGroupTypeEO> queryGroupType();
}
