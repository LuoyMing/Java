package com.seud.fyb.article.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.seud.fyb.article.dao.ICfgDictDAO;
import com.seud.fyb.article.dao.IConsultContentDAO;
import com.seud.fyb.article.dao.IConsultGroupDAO;
import com.seud.fyb.article.service.IAuditOperationService;
import com.seud.fyb.article.service.IConsultGroupService;
import com.seud.fyb.feignclient.article.model.bean.ArticleGroupBean;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleBeforehandContentEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupContentCorrelationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupTypeEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.enums.ArticleGroupEnum;
import com.seud.fyb.feignclient.article.model.enums.AuditProcessStateEnum;
import com.seud.fyb.feignclient.article.model.enums.BooleanTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.DeleteStateEnum;
import com.seud.fyb.feignclient.article.model.enums.GroupAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.OperationTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.PublishStateEnum;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.exception.RootException;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.UUIDGenerator;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultClassifyServiceImpl
 * @Description: 分组管理
 * @author luoyiming
 * @date 2017年3月9日 下午1:57:02
 * 
 */
@Service("consultGroupService")
public class ConsultGroupServiceImpl implements IConsultGroupService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditOperationServiceImpl.class);

	@Resource(name = "cfgDictDAO")
	private ICfgDictDAO cfgDictDAO;
	
	@Resource(name = "consultGroupDAO")
	private IConsultGroupDAO consultGroupDAO;

	@Resource(name = "auditOperationService")
	private IAuditOperationService auditOperationService;
	
	@Resource(name = "consultContentDAO")
	private IConsultContentDAO consultContentDAO;

	@Override
	public List<ArticleGroupBean> listAll() {
		List<ArticleGroupBean> beans = new ArrayList<>();
		List<ArticleGroupEO> list = null;
		try{
			list = consultGroupDAO.queryListAll(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
		}
		if(null==list){
			list = new ArrayList<>();
		}
		List<ArticleGroupTypeEO> groupTypeList = consultGroupDAO.queryGroupType();
		for (ArticleGroupTypeEO groupType : groupTypeList) {
			ArticleGroupBean bean = new ArticleGroupBean();
			bean.setId(groupType.getParentId());
			bean.setName(groupType.getParentName());
			bean.setGroupEOs(new ArrayList<ArticleGroupEO>());
			for (ArticleGroupEO articleGroupEO : list) {
				if (groupType.getParentId().equals(articleGroupEO.getParentId())) {
					bean.getGroupEOs().add(articleGroupEO);
				}
			}
			beans.add(bean);
		}
		return beans;
	}

	@Override
	public List<ArticleGroupEO> listByRootGroup(String parentId) {
		List<ArticleGroupEO> list = null;
		try {
			list = consultGroupDAO.queryListByRootGroup(parentId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
		}
		if(null==list){
			list = new ArrayList<>();
		}
		return consultGroupDAO.queryListByRootGroup(parentId);
	}

	/**
	 * (non-Javadoc) 通过根节点id获取分组列表 分页
	 */
	@Override
	public PageInfo<ArticleGroupEO> listPageByKeyword(String keyword, Integer pageSize, Integer pageNo) {
		if(null==pageSize){
			pageSize = 10;
		}
		if(null==pageNo){
			pageNo = 1;
		}
		try {
			return consultGroupDAO.queryListPageByKeyword(keyword, pageSize, pageNo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			PageInfo<ArticleGroupEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticleGroupEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	/**
	 * (non-Javadoc) 保存分组（待审核） 创建分组 加入工作流
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean saveGroupToAudit(String parentId, String name, String remark, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(parentId)) {
			throw new SeudRuntimeException("001", "未选择分组类型");
		}
		if (StringUtils.isEmpty(name)) {
			throw new SeudRuntimeException("001", "请输入分组名称");
		}
		if(StringUtils.isNotEmpty(remark)){
			if (remark.length() > 190) {
				throw new SeudRuntimeException("001", "分组描述过长");
			}
		}
		
		name = name.trim();
		try {
			ArticleGroupTypeEO groupTypeEO = consultGroupDAO.findByPrimaryKey(ArticleGroupTypeEO.class, parentId);
			// 创建分组信息
			ArticleGroupEO groupEO = new ArticleGroupEO();
			groupEO.setId(UUIDGenerator.getUUID());
			groupEO.setName(name);
			groupEO.setParentId(parentId);
			groupEO.setParentName(groupTypeEO.getParentName());
			groupEO.setPublishState(PublishStateEnum.N.toString());
			groupEO.setRemark(remark);
			groupEO.setCreaterId(sysUserInfo.getUserId());
			groupEO.setCreater(sysUserInfo.getFullName());
			groupEO.setCreateTime(new Date());
			int sort = 0;
			try {
				sort = consultGroupDAO.findMaxGroup();
			} catch (Exception e) {
			}
			groupEO.setSort(sort+1);
			consultGroupDAO.save(groupEO);
	
			// 提交审核
			ArticleAuditOperationEO aaoEo = new ArticleAuditOperationEO();
			aaoEo.setId(UUIDGenerator.getUUID());
			aaoEo.setMatterName(sysUserInfo.getFullName() +"("+sysUserInfo.getUserId()+")");
			aaoEo.setOpObjId(groupEO.getId());
			aaoEo.setAuditType(GroupAuditTypeEnum.Adding.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.Wait.toString());
			aaoEo.setOpType(OperationTypeEnum.AGroup.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setFlowId("ArticleGroupAudit");
			aaoEo.setInitiatorId(sysUserInfo.getUserId());
			aaoEo.setInitiator(sysUserInfo.getFullName());
			aaoEo.setInitiatorRole(sysUserInfo.getRoleName());
			aaoEo.setInitiatTime(new Date());
			aaoEo.setBeforeDataSnapshot(null);
			aaoEo.setAfterDataSnapshot(JSON.toJSONString(groupEO));
			return auditOperationService.auditSubmit(aaoEo);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","分组数据保存失败，请联系管理员");
		}
	}

	/**
	 * (non-Javadoc) 保存分组（审核通过）
	 */
	@Override
	public boolean saveGroupAuditPass(ArticleAuditOperationEO aaoEo) {
		try{
			ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, aaoEo.getOpObjId());
			groupEO.setPublishState(PublishStateEnum.Y.toString());
			//置顶
			int sort = 0;
			try {
				sort = consultGroupDAO.findMaxGroup();
			} catch (Exception e) {
			}
			groupEO.setSort(sort+1);
			consultGroupDAO.update(groupEO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","保存分组数据失败，请联系管理员");
		}
	}

	/**
	 * (non-Javadoc) 修改分组（待审核）
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean updateGroupToAudit(String id, String name, String remark, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(id)) {
			throw new SeudRuntimeException("001", "分组不合法");
		}
		if (StringUtils.isEmpty(name)) {
			throw new SeudRuntimeException("001", "请输入分组名称");
		}
		if (remark.length() > 190) {
			throw new SeudRuntimeException("001", "分组描述过长");
		}
		try{
			ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, id);
			if (null == groupEO) {
				throw new SeudRuntimeException("001", "分组未找到");
			}
			ArticleGroupEO editGroupEO = new ArticleGroupEO();
			BeanUtils.copyProperties(groupEO, editGroupEO);
			editGroupEO.setName(name);
			editGroupEO.setRemark(remark);
			editGroupEO.setUpdater(sysUserInfo.getFullName());
			editGroupEO.setUpdaterId(sysUserInfo.getUserId());
			editGroupEO.setUpdateTime(new Date());
			String afterDataSnapshot = JSON.toJSONString(editGroupEO);
	
			// 当前分组能否变更(审核模块提供查询接口(且完成快照数据更新))
			if (!auditOperationService.canAuditSubmit(groupEO.getId(), afterDataSnapshot)) {
				return true;
			}
			// 提交审核
			ArticleAuditOperationEO aaoEo = new ArticleAuditOperationEO();
			aaoEo.setId(UUIDGenerator.getUUID());
			aaoEo.setMatterName(sysUserInfo.getFullName() +"("+sysUserInfo.getUserId()+")");
			aaoEo.setOpObjId(groupEO.getId());
			aaoEo.setAuditType(GroupAuditTypeEnum.Change.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.Wait.toString());
			aaoEo.setOpType(OperationTypeEnum.AGroup.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setFlowId("ArticleGroupAudit");
			aaoEo.setInitiatorId(sysUserInfo.getUserId());
			aaoEo.setInitiator(sysUserInfo.getFullName());
			aaoEo.setInitiatorRole(sysUserInfo.getRoleName());
			aaoEo.setInitiatTime(new Date());
			String beforeDataSnapshot = JSON.toJSONString(groupEO);
			aaoEo.setBeforeDataSnapshot(beforeDataSnapshot);
			aaoEo.setAfterDataSnapshot(afterDataSnapshot);
	
			return auditOperationService.auditSubmit(aaoEo);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","修改分组异常，请联系管理员");
		}
	}

	/**
	 * (non-Javadoc) 修改分组（审核通过）
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean updateGroupAuditPass(ArticleAuditOperationEO aaoEo) {
		try {
			String afterDataSnapshot = aaoEo.getAfterDataSnapshot();
			ArticleGroupEO groupEO = JSON.parseObject(afterDataSnapshot, ArticleGroupEO.class);
			groupEO.setPublishState(PublishStateEnum.Y.toString());
			groupEO.setUpdateTime(new Date());
			consultGroupDAO.update(groupEO);
			
			List<ArticleGroupContentCorrelationEO> correlationList = consultGroupDAO.findByGroupId(groupEO.getId());
			for(ArticleGroupContentCorrelationEO correlation : correlationList){
				correlation.setGroupName(groupEO.getName());
				consultGroupDAO.update(correlation);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001", "修改分组异常，请联系管理员");
		}
	}

	/**
	 * (non-Javadoc) 删除分组（待审核）
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean deleteGroupToAudit(String id, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(id)) {
			throw new SeudRuntimeException("001", "分组不合法");
		}
		ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, id);
		if (null == groupEO) {
			throw new SeudRuntimeException("001", "分组未找到");
		}
		String beforeDataSnapshot = JSON.toJSONString(groupEO);
		// 当前分组能否删除(审核模块提供查询接口(且完成快照数据更新))
		if (!auditOperationService.canAuditSubmit(groupEO.getId(), null)) {
			throw new SeudRuntimeException("001", "数据审核中，操作异常");
		}
		try{
			// 修改分组信息
			groupEO.setDeleterId(sysUserInfo.getUserId());
			groupEO.setDeleter(sysUserInfo.getFullName());
			groupEO.setDeleteTime(new Date());
			consultGroupDAO.update(groupEO);
			groupEO.setDeleteState(BooleanTypeEnum.Y.toString());
			String afterDataSnapshot = JSON.toJSONString(groupEO);
	
			// 提交审核
			ArticleAuditOperationEO aaoEo = new ArticleAuditOperationEO();
			aaoEo.setId(UUIDGenerator.getUUID());
			aaoEo.setMatterName(sysUserInfo.getFullName() +"("+sysUserInfo.getUserId()+")");
			aaoEo.setOpObjId(groupEO.getId());
			aaoEo.setAuditType(GroupAuditTypeEnum.Remove.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.InHand.toString());
			aaoEo.setOpType(OperationTypeEnum.AGroup.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setFlowId("ArticleGroupAudit");
			aaoEo.setInitiatorId(sysUserInfo.getUserId());
			aaoEo.setInitiator(sysUserInfo.getFullName());
			aaoEo.setInitiatorRole(sysUserInfo.getRoleName());
			aaoEo.setInitiatTime(new Date());
			aaoEo.setBeforeDataSnapshot(beforeDataSnapshot);
			aaoEo.setAfterDataSnapshot(afterDataSnapshot);
			return auditOperationService.auditSubmit(aaoEo);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","删除分组失败");
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean deleteGroupAuditPass(ArticleAuditOperationEO aaoEo) {
		try{
			ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, aaoEo.getOpObjId());
			groupEO.setDeleteState(BooleanTypeEnum.Y.toString());
			consultGroupDAO.update(groupEO);
			//删除分组关系列表
			consultGroupDAO.deleteContentCorrelationByGroupId(groupEO.getId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","删除分组失败");
		}
	}
	
	@Override
	public boolean stickGroupContentCorrelation(String groupId,String contentId){
		try {
			ArticleGroupContentCorrelationEO contentCorrelationEO = consultGroupDAO.findOne(groupId,contentId);
			if(null==contentCorrelationEO){
				throw new SeudRuntimeException("001","数据数据异常");
			}
			int sort = 0; 
			try {
				sort = consultGroupDAO.findMaxGroupContentCorrelation(groupId);
			} catch (Exception e) {
			}
			if(sort==contentCorrelationEO.getContentSort()){
				return true;
			}
			contentCorrelationEO.setContentSort(sort+1);
			consultGroupDAO.update(contentCorrelationEO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","置顶数据失败");
		}
	}
	
	@Override
	public boolean deleteGroupContentCorrelation(String groupId,String contentId){
		try{
			ArticleGroupContentCorrelationEO contentCorrelationEO = new ArticleGroupContentCorrelationEO();
			contentCorrelationEO.setContentId(contentId);
			contentCorrelationEO.setGroupId(groupId);
			try {
				consultGroupDAO.delete(contentCorrelationEO);
			} catch (RootException e) {
				e.printStackTrace();
				logger.error("分组",e);
				throw new SeudRuntimeException("001","移除数据失败");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","删除数据失败");
		}
	}

	/** (non-Javadoc)
	 * 置顶
	 */
	@Override
	public Boolean stickGroup(String id) {
		try{
			ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, id);
			int sort = 1;
			try {
				sort = consultGroupDAO.findMaxGroup();
			} catch (Exception e) {
			}
			if(null==groupEO){
				throw new SeudRuntimeException("001","未找到数据");
			}else{
				int nowsort = 0;
				try {
					nowsort = groupEO.getSort();
				} catch (Exception e) {
				}
				if(sort==nowsort){
					return true;
				}else{
					groupEO.setSort(sort+1);
					consultGroupDAO.update(groupEO);
				}
			}
			return true;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("分组",e);
			throw new SeudRuntimeException("001","置顶数据失败");
		}
	}

	/**
	 * 查询分组类型
	 */
	@Override
	public List<ArticleGroupTypeEO> queryGroupType() {
		return consultGroupDAO.queryGroupType();
	}

	/**
	 * 保存分组类型
	 */
	@Override
	public boolean saveGroupType(ArticleGroupTypeEO eo) {
		ArticleGroupTypeEO groupTypeEO = consultGroupDAO.findByPrimaryKey(ArticleGroupTypeEO.class, eo.getParentId());
		try{
			if(groupTypeEO == null){
				consultGroupDAO.save(eo);
			}else{
				consultGroupDAO.update(eo);
			}
			return true;
		} catch (SeudRuntimeException e) {
			throw new SeudRuntimeException("0001","分组类型保存失败");
		} catch (Exception e) {
			logger.error("分组类型",e);
			throw new SeudRuntimeException("0001","分组类型保存失败");
		}
	}

}
