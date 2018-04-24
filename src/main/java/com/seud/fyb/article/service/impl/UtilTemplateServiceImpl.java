package com.seud.fyb.article.service.impl;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seud.fyb.article.dao.IConsultUtilTemplateDAO;
import com.seud.fyb.article.service.IUtilTemplateService;
import com.seud.fyb.feignclient.article.model.entity.ArticleFileManageEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleUtilTemplateEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleKellegEnum;
import com.seud.fyb.feignclient.article.model.enums.TemplateTypeEnum;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.UUIDGenerator;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultClassifyServiceImpl
 * @Description: 模板管理
 * @author luoyiming
 * @date 2017年3月9日 下午1:57:02
 * 
 */
@Service("utilTemplateService")
public class UtilTemplateServiceImpl implements IUtilTemplateService {

	private final static String templateKeyCode = "template_code_no";

	@Resource(name = "consultUtilTemplateDAO")
	private IConsultUtilTemplateDAO consultUtilTemplateDAO;

	/**
	 * 获取模板列表 分页
	 * 
	 * @param keyword
	 *            模糊搜索关键字
	 * @param type
	 * @return
	 */
	@Override
	public PageInfo<ArticleUtilTemplateEO> listPageByKeyword(String keyword, String type, Integer pageSize,
			Integer pageNo) {
		if(null==pageSize){
			pageSize = 10;
		}
		if(null==pageNo){
			pageNo = 1;
		}
		try{
			return consultUtilTemplateDAO.queryListPageByKeyword(keyword, type, pageSize, pageNo);
		} catch (Exception e) {
			e.printStackTrace();
			PageInfo<ArticleUtilTemplateEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticleUtilTemplateEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	/**
	 * (non-Javadoc) 保存模板
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean saveTemplate(String title, String content, String type, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(title)) {
			throw new SeudRuntimeException("001", "请输入模板标题");
		}
		if (!TemplateTypeEnum.inkey(type)) {
			throw new SeudRuntimeException("001", "模板类型异常");
		}
		try{
			// 创建模板信息
			ArticleUtilTemplateEO templateEO = new ArticleUtilTemplateEO();
			templateEO.setId(UUIDGenerator.getUUID());
			templateEO.setTemplateCode(getTemplateCodeNo());
			templateEO.setTitle(title);
			templateEO.setType(type);
			templateEO.setContent(content);
			templateEO.setCreaterId(sysUserInfo.getUserId());
			templateEO.setCreater(sysUserInfo.getFullName());
			templateEO.setCreateTime(new Date());
			consultUtilTemplateDAO.save(templateEO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SeudRuntimeException("001", "保存模板数据失败");
		}
	}

	/**
	 * (non-Javadoc) 修改模板
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean updateTemplate(String id, String title, String content, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(id)) {
			throw new SeudRuntimeException("001", "模板不合法");
		}
		if (StringUtils.isEmpty(title)) {
			throw new SeudRuntimeException("001", "请输入模板名称");
		}
		try{
			ArticleUtilTemplateEO templateEO = consultUtilTemplateDAO.findByPrimaryKey(ArticleUtilTemplateEO.class, id);
			if (null == templateEO) {
				throw new SeudRuntimeException("001", "模板未找到");
			}
			// 变更模板的类型与审核状态
			templateEO.setTitle(title);
			templateEO.setContent(content);
			templateEO.setUpdaterId(sysUserInfo.getUserId());
			templateEO.setUpdater(sysUserInfo.getFullName());
			templateEO.setUpdateTime(new Date());
			consultUtilTemplateDAO.update(templateEO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SeudRuntimeException("001", "修改模板数据失败");
		}
	}

	/**
	 * (non-Javadoc) 删除模板
	 */
	@Override
	public boolean deleteTemplate(String id) {
		try{
			return consultUtilTemplateDAO.deleteByPrimaryKey(ArticleUtilTemplateEO.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SeudRuntimeException("001", "删除模板数据失败");
		}
	}

	/**
	 * 模板编号计数器 编号为6位,不足的补零
	 * 
	 * @return
	 */
	private String getTemplateCodeNo() {
		ArticleKellegEO dictEO = consultUtilTemplateDAO.findByPrimaryKey(ArticleKellegEO.class, templateKeyCode);
		if (null == dictEO) {
			dictEO = new ArticleKellegEO();
			dictEO.setKeyCode(templateKeyCode);
			dictEO.setRelevanceData("0");
			dictEO.setType(ArticleKellegEnum.Counter.toString());
			dictEO.setRelativeDescription("模板编号"+ArticleKellegEnum.Counter.name);
			consultUtilTemplateDAO.save(dictEO);
		}
		int codeNo = 0;
		try {
			codeNo = Integer.parseInt(dictEO.getRelevanceData());
		} catch (Exception e) {
		}
		codeNo++;
		dictEO.setRelevanceData(String.valueOf(codeNo));
		consultUtilTemplateDAO.update(dictEO);
		String code = dictEO.getRelevanceData();
		for (int i = code.length(); i < 6; i++) {
			code = "0" + code;
		}
		return code;
	}

	@Override
	public PageInfo<ArticleFileManageEO> listPageByFileManage(String type, Integer pageSize, Integer pageNo) {
		try{
			return consultUtilTemplateDAO.listPageByFileManage(type,pageSize,pageNo);
		} catch (Exception e) {
			e.printStackTrace();
			PageInfo<ArticleFileManageEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticleFileManageEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	@Override
	public boolean saveFileManage(ArticleFileManageEO eo) {
		try{
			if(null==eo)return true;
			eo.setCreateTime(new Date());
			eo.setId(UUIDGenerator.getUUID());
			consultUtilTemplateDAO.save(eo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public ArticleUtilTemplateEO findOneByIdTemplate(String id) {
		if(StringUtils.isEmpty(id)){
			throw new SeudRuntimeException("001", "模板id未找到");
		}
		return consultUtilTemplateDAO.findByPrimaryKey(ArticleUtilTemplateEO.class, id);
	}
}
