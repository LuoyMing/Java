
package com.seud.fyb.article.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.article.service.IConsultClassifyService;
import com.seud.fyb.feignclient.article.model.entity.ArticleClassifyEO;
import com.seud.fyb.feignclient.background.model.SysUserInfo;
import com.seud.fyb.framework.bean.BaseController;
import com.seud.fyb.framework.bean.PageInfo;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;

/**
 * @ClassName: ConsultClassifyController
 * @Description: 分类管理
 * @author luoyiming
 * @date 2017年3月14日 上午9:46:39
 * 
 */
@RestController("consultClassify")
public class ConsultClassifyController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ConsultClassifyController.class);

	@Resource(name = "consultClassifyService")
	private IConsultClassifyService consultClassifyService;

	/**
	 * 获取分类列表
	 * 
	 * @return
	 */
	@RequestMapping("/listAllClassify")
	public ResponseBodyInfo<List<ArticleClassifyEO>> listAllClassify() {
		List<ArticleClassifyEO> result = null;
		try {
			result = consultClassifyService.listAll();
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	/**
	 * 模糊搜索分类列表 分页
	 * 
	 * @param keyword
	 *            查询字
	 * @param pageSize
	 *            条数
	 * @param pageNo
	 *            页数
	 * @return
	 */
	@RequestMapping("/listPageByKeywordClassify")
	public ResponseBodyInfo<PageInfo<ArticleClassifyEO>> listPageByKeywordClassify(String keyword, Integer pageSize,
			Integer pageNo) {
		PageInfo<ArticleClassifyEO> result = null;
		try {
			result = consultClassifyService.listPageByKeyword(keyword, pageSize, pageNo);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	/**
	 * 保存分类（待审核）
	 * 
	 * @param name
	 *            分类名称
	 * @param parentId
	 *            根节点分类id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/saveClassify")
	public ResponseBodyInfo<Boolean> saveClassify(String name, @RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			result = consultClassifyService.saveClassify(name, sysUserInfo);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	/**
	 * 修改分类（待审核）
	 * 
	 * @param id
	 *            分类id
	 * @param name
	 *            分类名称
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/updateClassify")
	public ResponseBodyInfo<Boolean> updateClassify(String id, String name, @RequestBody SysUserInfo sysUserInfo) {
		Boolean result = null;
		try {
			result = consultClassifyService.updateClassify(id, name, sysUserInfo);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}

	/**
	 * 删除分类（待审核）
	 * 
	 * @param id
	 *            分类id
	 * @param sysUserInfo
	 *            当前操作者
	 * @return
	 */
	@RequestMapping("/deleteClassify")
	public ResponseBodyInfo<Boolean> deleteClassify(String id) {
		Boolean result = null;
		try {
			result = consultClassifyService.deleteClassify(id);
		} catch (SeudRuntimeException e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultInfo.errorForObj("001", e.getMessage());
		}
		return ResultInfo.successForObj(result);
	}
}
