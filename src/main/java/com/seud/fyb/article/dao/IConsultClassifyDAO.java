package com.seud.fyb.article.dao;

import java.util.List;

import com.seud.fyb.feignclient.article.model.entity.ArticleClassifyEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.IDao;

public interface IConsultClassifyDAO extends IDao {
	
	List<ArticleClassifyEO> queryListAll();
	
	List<ArticleClassifyEO> queryListByRootClassify(String parentId);
	
	PageInfo<ArticleClassifyEO> queryListPageByKeyword(String keyword,Integer pageSize, Integer pageNo);
	
}
