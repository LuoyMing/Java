package com.seud.fyb.article.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seud.fyb.framework.annotation.Method;
import com.seud.fyb.framework.bean.BaseController;
import com.seud.fyb.framework.bean.ResponseBodyInfo;
import com.seud.fyb.framework.bean.ResultInfo;
import com.seud.fyb.framework.exception.SeudRuntimeException;
import com.seud.fyb.workflow.business.service.impl.EffectServiceImpl;
import com.wfw.common.utils.StringUtils;

/**
 * @author hgp
 *清结算后参数生效控制类
 */
@RestController
public class EffectController  extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(EffectController.class);
		
	@Resource(name="effectService")
	private EffectServiceImpl effectService;
	
	@RequestMapping(value="paramEffect")
	@Method(desc="清结算后参数生效")	
	public ResponseBodyInfo paramEffect()
	{
		try{
			String result = this.effectService.effect("article");
			if(StringUtils.isNotEmpty(result))
			{
				return ResultInfo.errorForObj("9999", "参数生效失败,失败记录" + result);
			}
 		    return ResultInfo.successForObj();
		}catch (SeudRuntimeException e) {
			logger.error("paramtersEffect 失败",e);
			return ResultInfo.errorForObj(e.getErrorCode(), e.getMessage());

		}catch (Exception e) {
			logger.error("paramtersEffect 失败",e);
			return ResultInfo.errorForObj("9999", "参数生效失败!");
		}	
		
	}
}
