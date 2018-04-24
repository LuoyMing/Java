package com.seud.fyb.article.dao;

import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.IDao;

public interface ICfgDictDAO extends IDao {

	PageInfo<ArticleKellegEO> queryListPageByKeywordKelleg(String keyword, Integer pageSize, Integer pageNo);

	ArticleKellegEO findKellegByRelevanceData(String relevanceData);

}
