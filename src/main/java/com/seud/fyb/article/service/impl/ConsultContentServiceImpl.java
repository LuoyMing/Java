package com.seud.fyb.article.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seud.fyb.article.dao.ICfgDictDAO;
import com.seud.fyb.article.dao.IConsultClassifyDAO;
import com.seud.fyb.article.dao.IConsultContentDAO;
import com.seud.fyb.article.dao.IConsultGroupDAO;
import com.seud.fyb.article.service.IAuditOperationService;
import com.seud.fyb.article.service.IConsultContentService;
import com.seud.fyb.article.utils.StringBuilderUtils;
import com.seud.fyb.feignclient.article.model.entity.ArticleAuditOperationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleBeforehandContentEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleClassifyEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupContentCorrelationEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleGroupEO;
import com.seud.fyb.feignclient.article.model.entity.ArticleKellegEO;
import com.seud.fyb.feignclient.article.model.entity.ArticlePublishContentEO;
import com.seud.fyb.feignclient.article.model.enums.AuditProcessStateEnum;
import com.seud.fyb.feignclient.article.model.enums.BooleanTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.ContentAuditTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.ContentEditStateEnum;
import com.seud.fyb.feignclient.article.model.enums.GeneralizeLabelEnum;
import com.seud.fyb.feignclient.article.model.enums.OperationTypeEnum;
import com.seud.fyb.feignclient.article.model.enums.PublishStateEnum;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.feignclient.workflow.model.enums.FlowItemsTypeEnum;
import com.seud.fyb.framework.bean.FileClassify;
import com.seud.fyb.framework.bean.FileOut;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.cache.redis.Prefix;
import com.seud.fyb.framework.cache.redis.RedisUtil;
import com.seud.fyb.framework.exception.RootException;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.framework.utils.DateUtils;
import com.seud.fyb.framework.utils.FileHelper;
import com.seud.fyb.framework.utils.UUIDGenerator;
import com.wfw.common.utils.StringUtils;

/**
 * @ClassName: ConsultClassifyServiceImpl
 * @Description: 文章管理
 * @author luoyiming
 * @date 2017年3月9日 下午1:57:02
 * 
 */
@Service("consultContentService")
public class ConsultContentServiceImpl implements IConsultContentService {

	private static final Logger logger = LoggerFactory.getLogger(AuditOperationServiceImpl.class);

	private static final String AUDIT_PERSON_TYPE = "audit_person_type_group_id";

	@Resource(name = "consultGroupDAO")
	private IConsultGroupDAO consultGroupDAO;

	@Resource(name = "consultClassifyDAO")
	private IConsultClassifyDAO consultClassifyDAO;

	@Resource(name = "consultContentDAO")
	private IConsultContentDAO consultContentDAO;

	@Resource(name = "auditOperationService")
	private IAuditOperationService auditOperationService;

	@Autowired
	protected Environment env;

	@Autowired
	private RedisUtil redisUtil;

	@Resource(name = "cfgDictDAO")
	private ICfgDictDAO cfgDictDAO;

	/**
	 * (non-Javadoc) 模糊分页搜索 预发布信息
	 */
	@Override
	public PageInfo<ArticleBeforehandContentEO> listPageByKeywordBeforehand(String keyword, String classifyId,
			String relevanceBreedCode, Integer pageSize, Integer pageNo) {
		if (null == pageSize) {
			pageSize = 10;
		}
		if (null == pageNo) {
			pageNo = 1;
		}
		try {
			PageInfo<ArticleBeforehandContentEO> result = consultContentDAO.queryListPageByKeywordBeforehand(keyword,
					classifyId, relevanceBreedCode, pageSize, pageNo);
			if (null != result && null != result.getResultsList() && result.getResultsList().size() > 0) {
				for (ArticleBeforehandContentEO contentEO : result.getResultsList()) {
					contentEO.setEditState(ContentEditStateEnum.getName(contentEO.getEditState()));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			PageInfo<ArticleBeforehandContentEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticleBeforehandContentEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	/**
	 * (non-Javadoc) 标题分页搜索 已发布信息
	 */
	@Override
	public PageInfo<ArticlePublishContentEO> listPageByKeyword(String keyword, String groupId, String appType,
			Integer pageSize, Integer pageNo) {
		if (null == pageSize) {
			pageSize = 10;
		}
		if (null == pageNo) {
			pageNo = 1;
		}
		try {
			// 排序处理广告
			PageInfo<ArticlePublishContentEO> result = consultContentDAO.queryListPageByKeyword(keyword, groupId,
					appType, pageSize, pageNo);
			return sortChange(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			PageInfo<ArticlePublishContentEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticlePublishContentEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	private PageInfo<ArticlePublishContentEO> sortChange(PageInfo<ArticlePublishContentEO> result) {
		if (null != result && null != result.getResultsList() && result.getResultsList().size() > 0) {
			List<ArticlePublishContentEO> adList = new ArrayList<>();
			for (ArticlePublishContentEO contentEO : result.getResultsList()) {
				if (GeneralizeLabelEnum.inkey(contentEO.getGeneralizeLabel()) && null != contentEO.getSort()) {
					adList.add(contentEO);
				}
			}
			result.getResultsList().removeAll(adList);
			// adList sort降序
			Collections.sort(adList, new Comparator<ArticlePublishContentEO>() {
				public int compare(ArticlePublishContentEO o1, ArticlePublishContentEO o2) {
					if (o1.getSort() > o2.getSort()) {
						return 1;
					}
					if (o1.getSort() == o2.getSort()) {
						if (o1.getTimeInterval().after(o2.getTimeInterval())) {
							return 1;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
			for (ArticlePublishContentEO adEO : adList) {
				if (0 == adEO.getSort()) {
					result.getResultsList().add(0, adEO);
				} else if (adEO.getSort() > 0) {
					if (result.getResultsList().size() > (adEO.getSort() - 1)) {
						result.getResultsList().add(adEO.getSort() - 1, adEO);
					} else {
						result.getResultsList().add(adEO);
					}
				}
			}
		}
		return result;
	}

	/**
	 * (non-Javadoc) 分类分页搜索 已发布信息
	 */
	@Override
	public PageInfo<ArticlePublishContentEO> listPageByClassifyId(String classifyId, Integer pageSize, Integer pageNo) {
		if (null == pageSize) {
			pageSize = 10;
		}
		if (null == pageNo) {
			pageNo = 1;
		}
		return null;
	}

	/**
	 * (non-Javadoc) 分组分页搜索 已发布信息
	 */
	@Override
	public PageInfo<ArticlePublishContentEO> listPageByGroupId(String groupId, String appType, Integer pageSize,
			Integer pageNo) {
		if (null == pageSize) {
			pageSize = 10;
		}
		if (null == pageNo) {
			pageNo = 1;
		}
		try {
			// 排序处理广告
			PageInfo<ArticlePublishContentEO> result = consultContentDAO.queryListPageByGroup(groupId, appType,
					pageSize, pageNo);
			return sortChange(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			PageInfo<ArticlePublishContentEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticlePublishContentEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	/**
	 * (non-Javadoc) 新增保存文章信息
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean saveContent(ArticleBeforehandContentEO contentEO, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(contentEO.getId())) {
			throw new SeudRuntimeException("001", "文章管理:id异常");
		}
		if (StringUtils.isEmpty(contentEO.getTitle())) {
			throw new SeudRuntimeException("001", "文章管理:标题不能为空");
		}
		if (null == contentEO.getTimeInterval()) {
			throw new SeudRuntimeException("001", "文章管理:发布时间不能为空");
		}
		if (StringUtils.isNotEmpty(contentEO.getGeneralizeLabel())) {
			if (null == contentEO.getSort() || 0 == contentEO.getSort()) {
				throw new SeudRuntimeException("001", "文章管理:请选择文章排序");
			}
			if (StringUtils.isEmpty(contentEO.getAdUrl())) {
				throw new SeudRuntimeException("001", "文章管理:悬浮链接地址不能为空");
			} else {
				if (contentEO.getAdUrl().contains("http://") && contentEO.getAdUrl().contains("https://")) {
					contentEO.setAdUrl("http://" + contentEO.getAdUrl());
				}
			}
		} else {
			if (StringUtils.isEmpty(contentEO.getContent())) {
				throw new SeudRuntimeException("001", "文章管理:文章内容不能为空");
			}
		}
		if (StringUtils.isEmpty(contentEO.getClassifyId())) {
			throw new SeudRuntimeException("001", "文章管理:请选择正确的分类");
		}
		if (StringUtils.isEmpty(contentEO.getGroupId())) {
			throw new SeudRuntimeException("001", "文章管理:请选择正确的分组");
		}
		try {
			// 判断当前记录能否修改
			if (!auditOperationService.canAuditSubmit(contentEO.getId(), null)) {
				throw new SeudRuntimeException("001", "文章发布:当前文章审核中,操作失败");
			}

			// 获取父级分组
			String sql = " select GROUP_CONCAT( DISTINCT parent_id) from article_consult_group where id in ('"
					+ contentEO.getGroupId().replaceAll(",", "','") + "') ";
			String parentGroupId = consultGroupDAO.queryForObj(String.class, sql);
			contentEO.setParentGroupId(parentGroupId);

			// 判断当前记录是否存在
			ArticleBeforehandContentEO editContentEO = consultContentDAO
					.findByPrimaryKey(ArticleBeforehandContentEO.class, contentEO.getId());
			if (null != editContentEO) {
				updateContent(contentEO, sysUserInfo);
				return true;
			}

			ArticleClassifyEO classifyEO = consultClassifyDAO.findByPrimaryKey(ArticleClassifyEO.class,
					contentEO.getClassifyId());
			if (null == classifyEO) {
				throw new SeudRuntimeException("001", "新增文章:分类数据错误");
			}
			// 匹配分类名称
			contentEO.setClassifyName(classifyEO.getName());

			// 匹配父级分组
			contentEO.setEditState(ContentEditStateEnum.Draft.toString());
			contentEO.setPublishState(PublishStateEnum.N.toString());
			contentEO.setCreaterId(sysUserInfo.getUserId());
			contentEO.setCreater(sysUserInfo.getFullName());
			contentEO.setCreateTime(new Date());
			contentEO.setUpdaterId(sysUserInfo.getUserId());
			contentEO.setUpdater(sysUserInfo.getFullName());
			contentEO.setUpdateTime(new Date());
			consultContentDAO.save(contentEO);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "新增文章:保存失败");
		}
		return true;
	}

	/**
	 * (non-Javadoc) 修改文章信息 当该文章业务存在审核状态处理中时,不允许做任何编辑
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean updateContent(ArticleBeforehandContentEO editContentEO, SysUserInfo sysUserInfo) {
		try {
			ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class,
					editContentEO.getId());
			if (null == contentEO) {
				throw new SeudRuntimeException("001", "修改文章信息:文章未找到");
			}

			// 分组分类修改
			if (!(StringUtils.isEmpty(editContentEO.getClassifyId()))) {
				contentEO.setClassifyId(editContentEO.getClassifyId());
				ArticleClassifyEO classifyEO = consultClassifyDAO.findByPrimaryKey(ArticleClassifyEO.class,
						editContentEO.getClassifyId());
				if (null != classifyEO) {
					contentEO.setClassifyName(classifyEO.getName());
				}
			}
			if (!(StringUtils.isEmpty(contentEO.getGroupId()) || StringUtils.isEmpty(contentEO.getParentGroupId()))) {
				contentEO.setGroupId(editContentEO.getGroupId());
				contentEO.setParentGroupId(editContentEO.getParentGroupId());
			}

			// 业务参数修改
			if (!StringUtils.isEmpty(editContentEO.getTitle())) {
				contentEO.setTitle(editContentEO.getTitle());
			}
			if (!StringUtils.isEmpty(editContentEO.getTitleImgUrl())) {
				contentEO.setTitleImgUrl(editContentEO.getTitleImgUrl());
			}
			if (!StringUtils.isEmpty(editContentEO.getShowType())) {
				contentEO.setShowType(editContentEO.getShowType());
			}
			if (null != editContentEO.getTimeInterval()) {
				contentEO.setTimeInterval(editContentEO.getTimeInterval());
			}
			if (!StringUtils.isEmpty(editContentEO.getContent())) {
				contentEO.setContent(editContentEO.getContent());
			}

			// 品种允许置空
			contentEO.setGeneralizeLabel(editContentEO.getGeneralizeLabel());
			contentEO.setAdUrl(editContentEO.getAdUrl());
			contentEO.setSort(editContentEO.getSort());
			contentEO.setContentAuthor(editContentEO.getContentAuthor());
			contentEO.setContentDigest(editContentEO.getContentDigest());
			contentEO.setContentSource(editContentEO.getContentSource());
			contentEO.setRelevanceBreedTransactionCode(editContentEO.getRelevanceBreedTransactionCode());
			contentEO.setRelevanceBreedCode(editContentEO.getRelevanceBreedCode());
			contentEO.setRelevanceBreed(editContentEO.getRelevanceBreed());
			contentEO.setUpdaterId(sysUserInfo.getUserId());
			contentEO.setUpdater(sysUserInfo.getFullName());
			contentEO.setUpdateTime(new Date());
			contentEO.setEditState(ContentEditStateEnum.Draft.toString());
			consultContentDAO.update(contentEO);
			return true;
		} catch (SeudRuntimeException e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "修改保存失败");
		}
	}

	/**
	 * (non-Javadoc) 修改文章信息 当该文章业务存在审核状态处理中时,不允许做任何编辑
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean updateShowGroup(ArticleBeforehandContentEO editContentEO, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(editContentEO.getId())) {
			throw new SeudRuntimeException("001", "文章管理:id异常");
		}
		try {
			// 判断当前记录能否修改
			if (!auditOperationService.canAuditSubmit(editContentEO.getId(), null)) {
				throw new SeudRuntimeException("001", "文章发布:当前文章审核中,操作失败");
			}

			ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class,
					editContentEO.getId());
			if (null == contentEO) {
				throw new SeudRuntimeException("001", "修改文章信息:文章未找到");
			}

			// 获取父级分组
			String sql = " select GROUP_CONCAT( DISTINCT parent_id) from article_consult_group where id in ('"
					+ contentEO.getGroupId().replaceAll(",", "','") + "') ";
			String parentGroupId = consultGroupDAO.queryForObj(String.class, sql);
			contentEO.setParentGroupId(parentGroupId);

			// 分组修改
			if (!(StringUtils.isEmpty(contentEO.getGroupId()) || StringUtils.isEmpty(contentEO.getParentGroupId()))) {
				contentEO.setGroupId(editContentEO.getGroupId());
				contentEO.setParentGroupId(editContentEO.getParentGroupId());
			}

			// 允许置空
			contentEO.setRelevanceBreedTransactionCode(editContentEO.getRelevanceBreedTransactionCode());
			contentEO.setRelevanceBreedCode(editContentEO.getRelevanceBreedCode());
			contentEO.setRelevanceBreed(editContentEO.getRelevanceBreed());
			contentEO.setUpdaterId(sysUserInfo.getUserId());
			contentEO.setUpdater(sysUserInfo.getFullName());
			contentEO.setUpdateTime(new Date());
			consultContentDAO.update(contentEO);
			return true;
		} catch (SeudRuntimeException e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "修改保存失败");
		}
	}

	/**
	 * (non-Javadoc) 文章发布（待审核） 变更状态数据 加入工作流
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean publishContentToAudit(String contentId, SysUserInfo sysUserInfo) {
		if (StringUtils.isEmpty(contentId)) {
			throw new SeudRuntimeException("001", "文章发布:文章信息id异常");
		}
		try {
			ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class,
					contentId);
			if (null == contentEO) {
				throw new SeudRuntimeException("001", "文章发布:文章未找到");
			}
			// 判断能否发布审核
			if (!auditOperationService.canAuditSubmit(contentEO.getId(), null)) {
				throw new SeudRuntimeException("001", "文章发布:当前文章审核中,操作失败");
			}

			contentEO.setEditState(ContentEditStateEnum.Audit.toString());
			consultContentDAO.update(contentEO);
			
			String[] strs = new String[2];
			strs[0] = "交易所公告";
			strs[1] = "产品公告";
			String flowId = getFlowId(contentEO.getGroupId(), strs);

			// 提交审核(文章不使用快照数据)
			ArticleAuditOperationEO aaoEo = new ArticleAuditOperationEO();
			aaoEo.setId(UUIDGenerator.getUUID());
			aaoEo.setMatterName(sysUserInfo.getFullName() + "(" + sysUserInfo.getUserId() + ")");
			aaoEo.setOpObjId(contentEO.getId());
			aaoEo.setAuditType(ContentAuditTypeEnum.Release.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.InHand.toString());
			aaoEo.setOpType(OperationTypeEnum.Content.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setFlowId(flowId);
			aaoEo.setInitiatorId(sysUserInfo.getUserId());
			aaoEo.setInitiator(sysUserInfo.getFullName());
			aaoEo.setInitiatorRole(sysUserInfo.getRoleName());
			aaoEo.setInitiatTime(new Date());
			return auditOperationService.auditSubmit(aaoEo);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "提交审核保存失败");
		}
	}

	private String getFlowId(String groupId, String[] strs) {
		for (String str : strs) {
			try {
				List<ArticleGroupEO> groupEOs = consultGroupDAO.findOneByName(str);
				for (ArticleGroupEO groupEO : groupEOs) {
					if (null != groupEO && groupId.contains(groupEO.getId())) {
						return FlowItemsTypeEnum.ArticlePublishNotice.getFlowId();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("文章", e);
			}
		}
		return FlowItemsTypeEnum.ArticlePublishNotNotice.getFlowId();
	}
	
	private String getRemoveFlowId(String groupId, String[] strs) {
		for (String str : strs) {
			try {
				List<ArticleGroupEO> groupEOs = consultGroupDAO.findOneByName(str);
				for (ArticleGroupEO groupEO : groupEOs) {
					if (null != groupEO && groupId.contains(groupEO.getId())) {
						return FlowItemsTypeEnum.ArticleRemoveNotice.getFlowId();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("文章", e);
			}
		}
		return FlowItemsTypeEnum.ArticleRemoveNotNotice.getFlowId();
	}

	/**
	 * (non-Javadoc) 文章发布（审核通过） 变更状态数据 添加数据到发布表 生成文章内容文件
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean publishContentAuditPass(ArticleAuditOperationEO aaoEo) {
		try {
			// 变更状态数据
			ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class,
					aaoEo.getOpObjId());
			if (null == contentEO) {
				throw new SeudRuntimeException("001", "文章发布:文章未找到");
			}
			contentEO.setPublishState(PublishStateEnum.Y.toString());
			contentEO.setEditState(ContentEditStateEnum.Normal.toString());
			consultContentDAO.update(contentEO);
			String fileName = contentEO.getId() + ".html";
			ArticlePublishContentEO publishContentEO = new ArticlePublishContentEO();
			org.springframework.beans.BeanUtils.copyProperties(contentEO, publishContentEO);

			if (StringUtils.isEmpty(contentEO.getGeneralizeLabel())) {
				// 生成文章内容文件
				String contentUrl = createContentFile(aaoEo.getInitiatorId(), fileName, contentEO);
				// 清除https:// 协议地址
				
				if (StringUtils.isEmpty(contentUrl)) {
					throw new SeudRuntimeException("001", "文章发布:文章内容文件生成失败");
				}
				if (contentUrl.contains("https://")) {
					contentUrl = contentUrl.replaceAll("https://", "http://");
				}
				publishContentEO.setContentUrl(contentUrl);
			}

			ArticlePublishContentEO oldPublishContentEO = consultContentDAO
					.findByPrimaryKey(ArticlePublishContentEO.class, contentEO.getId());
			try {
				// 添加数据到发布表
				if (null == oldPublishContentEO) {
					consultContentDAO.save(publishContentEO);
				} else {
					consultContentDAO.update(publishContentEO);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("文章", e);
				throw e;
			}

			// 添加文章与分组关系表数据
			if (StringUtils.isEmpty(contentEO.getGroupId())) {
				throw new SeudRuntimeException("001", "文章发布:文章未绑定分组");
			}
			String[] groupIds = contentEO.getGroupId().split(",");
			List<ArticleGroupContentCorrelationEO> list = consultGroupDAO.findByContentId(contentEO.getId());
			for (String groupId : groupIds) {
				boolean isIn = false;
				for (ArticleGroupContentCorrelationEO eo : list) {
					if (eo.getGroupId().equals(groupId)) {
						isIn = true;
						break;
					}
				}
				if (!isIn) {
					ArticleGroupEO groupEO = consultGroupDAO.findByPrimaryKey(ArticleGroupEO.class, groupId);
					if (null == groupEO || PublishStateEnum.N.toString().equals(groupEO.getPublishState())) {
						continue;
					}
					int sort = 0;
					try {
						sort = consultGroupDAO.findMaxGroupContentCorrelation(groupId);
					} catch (Exception e) {
					}
					ArticleGroupContentCorrelationEO cEO = new ArticleGroupContentCorrelationEO(groupEO.getParentId(),
							groupEO.getParentName(), groupEO.getId(), groupEO.getName(), contentEO.getId(), sort + 1);
					consultGroupDAO.save(cEO);
				}
			}
			for (ArticleGroupContentCorrelationEO eo : list) {
				boolean isIn = false;
				for (String groupId : groupIds) {
					if (eo.getGroupId().equals(groupId)) {
						isIn = true;
						break;
					}
				}
				if (!isIn) {
					try {
						consultGroupDAO.delete(eo);
					} catch (RootException e) {
						e.printStackTrace();
						logger.error("文章", e);
					}
				}
			}
			return true;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "发布文章数据失败");
		}
	}

	/**
	 * 创建内容文件
	 * 
	 * @param fileName
	 * @return
	 */
	private String createContentFile(String userCode, String fileName, ArticleBeforehandContentEO contentEO) {
		try {
			StringBuilder contentSB = new StringBuilder();
			String keyCode = "content_template_file_url" + ConsultReferenceCenterServiceImpl.TEMPLATE_SUFFIX;
			ArticleKellegEO tkellegEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, keyCode);
			if (null != tkellegEO && StringUtils.isNotEmpty(tkellegEO.getRelevanceData())) {
				contentSB.append(tkellegEO.getRelevanceData());
			}
			// 替换，变更模板内容
			if (0 == contentSB.length()) {
				contentSB.append(contentEO.getContent());
			} else {
				String timeInterval = null;
				if (null != contentEO.getTimeInterval()) {
					timeInterval = DateUtils.format(contentEO.getTimeInterval(), "yyyy-MM-dd HH:mm:ss");
				}
				StringBuilderUtils.replaceForLaber(contentSB, contentEO.getTitle(), timeInterval,
						contentEO.getContentSource(), contentEO.getContentAuthor(), contentEO.getContentDigest(),
						contentEO.getContent(), contentEO.getTitleImgUrl(),
						contentEO.getRelevanceBreedTransactionCode(), contentEO.getRelevanceBreedCode(),
						contentEO.getRelevanceBreed(), fileName);
			}
			// 文件需要先转base64字符串后转为byte[] 进行上传处理
			String serverUrl = env.getProperty("file.uploadServerUrl");
			String contentStr = new String(contentSB.toString().getBytes(), "UTF-8");
			ResponseBodyInfo<FileOut> result = FileHelper.upload(userCode, FileClassify.ARTICLE.getClassifyValue(),
					fileName, contentStr.getBytes(), serverUrl);
			FileOut fileOut = null;
			if (result != null && result.getData() != null) {
				fileOut = result.getData();
				StringBuffer fileStr = new StringBuffer();
				return fileStr.append(fileOut.getViewRootUrl()).append(fileOut.getFileDirectory())
						.append(fileOut.getFileName()).toString();
			}
			return null;
		} catch (Exception e) {
			throw new SeudRuntimeException("001", "文章发布:文章内容文件生成失败");
		}
	}

	/**
	 * (non-Javadoc) 文章删除（待审核） 1.判断当前文章是否发布 2.判断文章是否进入审核状态
	 * 1&2:文章为草稿状态,且未发布则可以直接删除 3变更状态数据 4加入工作流
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean removeContentToAudit(String contentId, SysUserInfo sysUserInfo) {
		try {
			// 判断
			if (StringUtils.isEmpty(contentId)) {
				throw new SeudRuntimeException("001", "文章删除:文章信息id异常");
			}
			ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class,
					contentId);
			if (null == contentEO) {
				throw new SeudRuntimeException("001", "文章删除:文章未找到");
			}
			// 判断能否删除审核
			if (!auditOperationService.canAuditSubmit(contentEO.getId(), null)) {
				throw new SeudRuntimeException("001", "文章删除:当前文章审核中,操作失败");
			}

			// 文章为草稿状态,且未发布 删除为逻辑删除
			if (PublishStateEnum.N.toString().equals(contentEO.getPublishState())) {
				contentEO.setDeleterId(sysUserInfo.getUserId());
				contentEO.setDeleter(sysUserInfo.getFullName());
				contentEO.setDeleteTime(new Date());
				contentEO.setDeleteState(BooleanTypeEnum.Y.toString());
				consultContentDAO.update(contentEO);
				return true;
			}
			contentEO.setEditState(ContentEditStateEnum.Audit.toString());
			contentEO.setDeleterId(sysUserInfo.getUserId());
			contentEO.setDeleter(sysUserInfo.getFullName());
			contentEO.setDeleteTime(new Date());
			consultContentDAO.update(contentEO);

			ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, AUDIT_PERSON_TYPE);
			/*String flowId = FlowItemsTypeEnum.ArticleRemoveNotNotice.getFlowId();// "ArticleContentOther";
			if (null != dictEO && StringUtils.isNotEmpty(dictEO.getRelevanceData())) {
				String[] groupIds = dictEO.getRelevanceData().split(",");
				for (String groupId : groupIds) {
					if (contentEO.getGroupId().contains(groupId)) {
						flowId = FlowItemsTypeEnum.ArticleRemoveNotice.getFlowId();
						break;
					}
				}
			}*/
			String[] strs = new String[2];
			strs[0] = "交易所公告";
			strs[1] = "产品公告";
			String flowId = getRemoveFlowId(contentEO.getGroupId(), strs);

			// 提交审核
			ArticleAuditOperationEO aaoEo = new ArticleAuditOperationEO();
			aaoEo.setId(UUIDGenerator.getUUID());
			aaoEo.setMatterName(sysUserInfo.getFullName() + "(" + sysUserInfo.getUserId() + ")");
			aaoEo.setOpObjId(contentEO.getId());
			aaoEo.setAuditType(ContentAuditTypeEnum.Remove.toString());
			aaoEo.setProcessState(AuditProcessStateEnum.InHand.toString());
			aaoEo.setOpType(OperationTypeEnum.Content.toString());
			aaoEo.setIsPassAudit(BooleanTypeEnum.N.toString());
			aaoEo.setFlowId(flowId);
			aaoEo.setInitiatorId(sysUserInfo.getUserId());
			aaoEo.setInitiator(sysUserInfo.getFullName());
			aaoEo.setInitiatorRole(sysUserInfo.getRoleName());
			aaoEo.setInitiatTime(new Date());
			return auditOperationService.auditSubmit(aaoEo);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "删除文章提交审核失败");
		}

	}

	/**
	 * (non-Javadoc) 文章删除（审核通过） 预发布表逻辑删除 发布表物理删除
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public boolean removeContentAuditPass(ArticleAuditOperationEO aaoEo) {
		try {
			// 预发布表逻辑删除
			ArticleBeforehandContentEO contentEO = consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class,
					aaoEo.getOpObjId());
			if (null == contentEO) {
				throw new SeudRuntimeException("001", "文章删除:文章未找到");
			}
			contentEO.setPublishState(PublishStateEnum.Y.toString());
			contentEO.setDeleteState(BooleanTypeEnum.Y.toString());
			consultContentDAO.update(contentEO);
			// 发布表物理删除
			return consultContentDAO.deleteByPrimaryKey(ArticlePublishContentEO.class, contentEO.getId());
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "删除文章数据失败");
		}
	}

	/**
	 * (non-Javadoc) 获取单条
	 */
	@Override
	public ArticleBeforehandContentEO findOneById(String id) {
		try {
			if (StringUtils.isEmpty(id)) {
				throw new SeudRuntimeException("001", "文章查询:id未找到");
			}
			return consultContentDAO.findByPrimaryKey(ArticleBeforehandContentEO.class, id);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new SeudRuntimeException("001", "文章查询异常,请联系管理员");
		}
	}

	/**
	 * (non-Javadoc) 获取单条
	 */
	@Override
	public ArticlePublishContentEO findOneByIdPublish(String id) {
		try {
			if (StringUtils.isEmpty(id)) {
				throw new SeudRuntimeException("001", "文章查询:id未找到");
			}
			return consultContentDAO.findByPrimaryKey(ArticlePublishContentEO.class, id);
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new SeudRuntimeException("001", "文章查询异常,请联系管理员");
		}
	}

	/**
	 * 针对特殊应用场景支持
	 * 
	 * @param specialCode
	 *            特殊编号
	 * @param type
	 *            1.通过groupId查询 2.通过keyword查询
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@Override
	public PageInfo<ArticlePublishContentEO> listPageBySpecialCodePublish(String specialCode, String type,
			String appType, Integer pageSize, Integer pageNo) {
		if (null == pageSize) {
			pageSize = 10;
		}
		if (null == pageNo) {
			pageNo = 1;
		}
		try {
			if (StringUtils.isEmpty(specialCode) || StringUtils.isEmpty(type)) {
				throw new SeudRuntimeException("001", "文章查询:关键字异常");
			}
			String templateJson = redisUtil.get(specialCode);
			if (StringUtils.isEmpty(templateJson)) {
				ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, specialCode);
				if (null == dictEO) {
					throw new SeudRuntimeException("001", "文章查询:未找到业务应用场景。");
				} else {
					templateJson = dictEO.getRelevanceData();
					try {
						redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, specialCode, templateJson, 86400000L);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("文章", e);
					}
				}
			}
			if ("1".equals(type)) {
				return listPageByGroupId(templateJson, appType, pageSize, pageNo);
			} else {
				return listPageByKeyword(templateJson, null, appType, pageSize, pageNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			PageInfo<ArticlePublishContentEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticlePublishContentEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	@Override
	public PageInfo<ArticlePublishContentEO> listPageBySpecialCodePublishWithGroup(String keyword, String specialCode,
			String isAPP, Integer pageSize, Integer pageNo) {
		if (null == pageSize) {
			pageSize = 10;
		}
		if (null == pageNo) {
			pageNo = 1;
		}
		try {
			if (StringUtils.isEmpty(specialCode)) {
				throw new SeudRuntimeException("001", "文章查询:关键字异常");
			}
			String templateJson = redisUtil.get(specialCode);
			if (StringUtils.isEmpty(templateJson)) {
				ArticleKellegEO dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, specialCode);
				if (null == dictEO) {
					throw new SeudRuntimeException("001", "文章查询:未找到业务应用场景。");
				} else {
					templateJson = dictEO.getRelevanceData();
					try {
						redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, specialCode, templateJson, 86400000L);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("文章", e);
					}
				}
			}
			return listPageByKeyword(keyword, templateJson, isAPP, pageSize, pageNo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			PageInfo<ArticlePublishContentEO> result = new PageInfo<>();
			result.setResultsList(new ArrayList<ArticlePublishContentEO>());
			result.setCurPageNO(pageNo);
			result.setPageSize(pageSize);
			result.setTotalPage(0);
			result.setTotalRecord(0);
			return result;
		}
	}

	@Override
	public ArticleKellegEO getSpecialCodeOne(String specialCode) {
		try {
			if (StringUtils.isEmpty(specialCode)) {
				throw new SeudRuntimeException("001", "获取单条:关键字异常");
			}
			String templateJson = redisUtil.get(specialCode);
			ArticleKellegEO dictEO = null;
			if (StringUtils.isEmpty(templateJson)) {
				dictEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, specialCode);
				if (null == dictEO) {
					throw new SeudRuntimeException("001", "获取单条:未找到关键字数据。");
				} else {
					templateJson = dictEO.getRelevanceData();
					try {
						redisUtil.set(Prefix.ACCESS_INTERFACE_PERMISSION_PREFIX, specialCode, templateJson, 86400000L);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("文章", e);
					}
				}
			} else {
				dictEO = new ArticleKellegEO();
				dictEO.setKeyCode(specialCode);
				dictEO.setRelevanceData(templateJson);
			}
			return dictEO;
		} catch (SeudRuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文章", e);
			throw new SeudRuntimeException("001", "读取数据异常，请联系管理员");
		}
	}

	@Override
	public List<ArticlePublishContentEO> listInPublish(String inPublish, String appType) {
		return consultContentDAO.listInPublish(inPublish, appType);
	}

	@Override
	public ArticleKellegEO findKellegByRelevanceData(String relevanceData) {
		return cfgDictDAO.findKellegByRelevanceData(relevanceData);
	}

	//通过自定义ID查询文章
	@Override
	public ArticlePublishContentEO getOneSpecialCodePublish(String specialCode) {
		ArticleKellegEO kellegEO = cfgDictDAO.findByPrimaryKey(ArticleKellegEO.class, specialCode);
		if (null != kellegEO && StringUtils.isNotEmpty(kellegEO.getRelevanceData())) {
			return consultContentDAO.findByPrimaryKey(ArticlePublishContentEO.class, kellegEO.getRelevanceData());
		} else {
			return null;
		}
	}

	@Override
	public List<ArticlePublishContentEO> listTopGroupOne(String appType, String parentGroup, int size) {
		List<ArticleGroupEO> list = consultGroupDAO.queryListByRootGroup(parentGroup);
		List<ArticlePublishContentEO> contentEOs = new ArrayList<>();
		for (ArticleGroupEO articleGroupEO : list) {
			List<ArticlePublishContentEO> contentEO = consultContentDAO.listTopGroupOne(articleGroupEO.getId(), appType,
					size);
			if (null != contentEO) {
				contentEOs.addAll(contentEO);
			}
		}
		return contentEOs;
	}

	//通过关联ID查询文章
	@Override
	public ArticlePublishContentEO getOneSpecialFieldPublish(String specialField) {
		List<ArticlePublishContentEO> list = consultContentDAO.getOneSpecialFieldPublish(specialField);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 新增文章内容返回静态文章内容地址
	 */
	@Override
	public String saveOnlyContent(String userCode, String content) {
		try {
			String id = UUIDGenerator.getUUID();
			String fileName = id + ".html";
			ArticleBeforehandContentEO contentEO = new ArticleBeforehandContentEO();
			contentEO.setId(id);
			contentEO.setContent(content);
			String contentUrl = createContentFile(userCode, fileName, contentEO);
			if (contentUrl != null && contentUrl.contains("https://")) {
				contentUrl = contentUrl.replaceAll("https://", "http://");
			}
			return contentUrl;
		} catch (Exception e) {
			throw new SeudRuntimeException("0001", "内容文件生成失败");
		}
	}
}
