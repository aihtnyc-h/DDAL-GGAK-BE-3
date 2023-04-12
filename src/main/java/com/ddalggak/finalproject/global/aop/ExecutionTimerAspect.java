package com.ddalggak.finalproject.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ExecutionTimerAspect {

	@Pointcut("@annotation(ExecutionTimer)")
	public void timer() {
	}

	;

	@Around("timer()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		Object result = joinPoint.proceed();
		stopWatch.stop();

		long totalTimeMillis = stopWatch.getTotalTimeMillis();

		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		String methodName = methodSignature.getMethod().getName();

		log.info("실행 메서드 : {}, 실행 시간 = {}ms", methodName, totalTimeMillis);

		return result;
	}
}
