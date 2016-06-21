package com.ares.web.controller;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import com.ares.app.constdata.Const;
import com.ares.framework.rpc.WebRequestRpc;
import com.ares.framework.rpc.context.RpcContext;
import com.ares.service.exception.RunLogicException;

@Controller
public class AppWebRequetRpc extends WebRequestRpc {

	@Inject
	private Provider<RpcContext> contextProvider;

	@Override
	public void checkSession(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			throw new RunLogicException("please login first", "login");
		}
		// System.out.println(req.getServerName() +" port  ="+
		// req.getServerPort());
		// System.out.println(req.getLocalName());
		// System.out.println("content path = " + req.getPathInfo());
		// System.out.println("content path = " + req.getPathTranslated());
		RpcContext context = contextProvider.get();
		context.setAccountID((String) session.getAttribute(Const.ACOUNT_ID));
		context.setUserID((String) session.getAttribute(Const.USER_ID));
		context.setContentPath(req.getPathInfo());
		context.setServerName(req.getServerName());
		context.setPort(req.getServerPort());
	}

	@Override
	public void postProcess() {
		// TODO Auto-generated method stub

	}

}
