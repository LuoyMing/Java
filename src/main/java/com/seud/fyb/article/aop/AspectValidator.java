package com.seud.fyb.article.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.seud.fyb.framework.utils.AspectValidatorHelper;

/**
 * 
  * @ClassName: AspectValidator
  * @Description: 业务方法切面验证器
  * @author luoyiming
  * @date 2016-10-28 下午4:42:40
  *
 */
@Aspect
@Component
public class AspectValidator {

	@Around("execution (* com.seud.fyb.article.service..*.*(..))")  
    public Object validatorAround(ProceedingJoinPoint joinPoint) throws Throwable{
		return AspectValidatorHelper.validatorAround(joinPoint);
	}
}
