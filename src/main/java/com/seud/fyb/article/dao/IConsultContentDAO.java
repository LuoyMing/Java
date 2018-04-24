package com.seud.fyb.article.dao;

import java.util.List;

import com.seud.fyb.feignclient.article.model.entity.ArticleBeforehandContentEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.IDao;

public interface IConsultContentDAO extends IDao {

	PageInfo<ArticlePublishContentEO> queryListPageByKeyword(String keyword,String groupId,String appType, Integer pageSize, Integer pageNo);
	
	PageInfo<ArticlePublishContentEO> queryListPageByGroup(String groupId,String appType, Integer pageSize, Integer pageNo);
	
	PageInfo<ArticleBeforehandContentEO> queryListPageByKeywordBeforehand(String keyword,String classifyId, String relevanceBreedCode, Integer pageSize, Integer pageNo);

	List<ArticlePublishContentEO> listInPublish(String inPublish, String appType);

	List<ArticlePublishContentEO> listTopGroupOne(String groupIds, String appType,int size);
	
	List<ArticleBeforehandContentEO> listByGroupId(String groupId);
	
	List<ArticlePublishContentEO> getOneSpecialFieldPublish(String specialField);
}
