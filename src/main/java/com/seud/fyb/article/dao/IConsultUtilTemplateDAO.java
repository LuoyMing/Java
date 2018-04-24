package com.seud.fyb.article.dao;

import com.seud.fyb.feignclient.article.model.entity.ArticleFileManageEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleUtilTemplateEO;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.dao.IDao;

public interface IConsultUtilTemplateDAO extends IDao {

	PageInfo<ArticleUtilTemplateEO> queryListPageByKeyword(String keyword, String type, Integer pageSize,
			Integer pageNo);

	PageInfo<ArticleFileManageEO> listPageByFileManage(String type, Integer pageSize, Integer pageNo);
}
