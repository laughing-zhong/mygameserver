package com.ares.app.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserNotRewardedException extends RuntimeException {

	private static final long serialVersionUID = 1L;


}
