package com.example.demo.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Aspect
@Component
public class PrincipalCheckAspect {

	@Around("execution(com.example.demo.controller.MfaController.*(..))")
	public Object checkPrincipal(ProceedingJoinPoint joinPoint) throws Throwable {

		ServletRequestAttributes attrs =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (attrs == null) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Request context non disponibile"
			);
		}

		HttpServletRequest request = attrs.getRequest();

		Principal principal = request.getUserPrincipal();

		if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Principal non valorizzato"
			);
		}

		return joinPoint.proceed();
	}
}
