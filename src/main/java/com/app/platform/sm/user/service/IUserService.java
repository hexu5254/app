package com.app.platform.sm.user.service;

import com.app.platform.core.authentication.intf.IUser;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {

	IUser getById(Long id);

	void setLoginUser(IUser user, String loginType, HttpServletRequest request, boolean recordLog);
}
