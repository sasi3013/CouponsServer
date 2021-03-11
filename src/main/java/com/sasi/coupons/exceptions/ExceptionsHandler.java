package com.sasi.coupons.exceptions;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sasi.coupons.dto.ErrorDto;
import com.sasi.coupons.enums.ErrorType;

@RestControllerAdvice
public class ExceptionsHandler {

	// Response - Object in Spring
	@ExceptionHandler
	@ResponseBody
	// Variable name is throwable in order to remember that it handles Exception and Error
	public ErrorDto toResponse(Throwable throwable, HttpServletResponse response) {

		// ErrorDto errorDto;
		if (throwable instanceof ApplicationException) {

			ApplicationException appException = (ApplicationException) throwable;

			ErrorType errorType = appException.getErrorType();
			int errorNumber = errorType.getErrorNumber();
			String errorMessage = errorType.getErrorMessage();
			String errorName = errorType.toString();
			response.setStatus(errorNumber);

			ErrorDto errorDto = new ErrorDto(errorNumber, errorName, errorMessage);
			if (appException.getErrorType().isPrintStackTrace()) {
				appException.printStackTrace();
			}

			return errorDto;
		}

		response.setStatus(600);

		String errorMessage = throwable.getMessage();
		ErrorDto errorDto = new ErrorDto(601, "General error", errorMessage);
		throwable.printStackTrace();

		return errorDto;
	}

}
