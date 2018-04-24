package com.seud.fyb.article.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.seud.fyb.article.dao.IConsultClassifyDAO;
import com.seud.fyb.article.service.IConsultClassifyService;
import com.seud.fyb.feignclient.article.model.entity.ArticleClassifyEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.UUIDGenerator;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultClassifyServiceImpl
 * @Description: 分类管理
 * @author luoyiming
 * @date 2017年3月9日 下午1:57:02
 * 
 */
@Service("consultClassifyService")
public class ConsultClassifyServiceImpl implements IConsultClassifyService {
	
	

	@Resource(name = "consultClassifyDAO")
	private IConsultClassifyDAO consultClassifyDAO;

	@Override
	public List<ArticleClassifyEO> listAll() {
		try {
			return consultClassifyDAO.queryListAll();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * (non-Javadoc) 通过根节点id获取分类列表 分页
	 */
	@Override
	public PageInfo<ArticleClassifyEO> listPageByKeyword(String keyword, Integer pageSize, Integer pageNo) {
		if(null==pageSize){
			pageSize = 10;
		}
		if(null==pageNo){
			pageNo = 1;
		}
		try {
			return consultClassifyDAO.queryListPageByKeyword(keyword, pageSize, pageNo);
		} catch (Exception e) {
			e.printStackTrace();
			PageInfo<ArticleClassifyEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticleClassifyEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	/**
	 * (non-Javadoc) 保存分类（待审核） 创建分类 加入工作流
	 */
	@Override
	public boolean saveClassify(String name, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(name)) {
			throw new SeudRuntimeException("001", "请输入分类名称");
		}
		// 创建分类信息
		ArticleClassifyEO classifyEO = new ArticleClassifyEO();
		classifyEO.setId(UUIDGenerator.getUUID());
		classifyEO.setName(name);
		classifyEO.setCreaterId(sysUserInfo.getUserId());
		classifyEO.setCreater(sysUserInfo.getFullName());
		classifyEO.setCreateTime(new Date());
		consultClassifyDAO.save(classifyEO);
		return true;
	}

	/**
	 * (non-Javadoc) 修改分类
	 */
	@Override
	public boolean updateClassify(String id, String name, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(id)) {
			throw new SeudRuntimeException("001", "分类不合法");
		}
		if (StringUtils.isEmpty(name)) {
			throw new SeudRuntimeException("001", "请输入分类名称");
		}
		try {
			ArticleClassifyEO classifyEO = consultClassifyDAO.findByPrimaryKey(ArticleClassifyEO.class, id);
			if (null == classifyEO) {
				throw new SeudRuntimeException("001", "分类未找到");
			}
			// 变更分类的类型与审核状态
			classifyEO.setName(name);
			classifyEO.setUpdaterId(sysUserInfo.getUserId());
			classifyEO.setUpdater(sysUserInfo.getFullName());
			classifyEO.setUpdateTime(new Date());
			consultClassifyDAO.update(classifyEO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SeudRuntimeException("001", "分类修改失败，请联系管理员");
		}
	}

	/**
	 * (non-Javadoc) 删除分类（待审核）
	 */
	@Override
	public boolean deleteClassify(String id) {
		try {
			return consultClassifyDAO.deleteByPrimaryKey(ArticleClassifyEO.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SeudRuntimeException("001", "分类删除失败，请联系管理员");
		}
	}
}
