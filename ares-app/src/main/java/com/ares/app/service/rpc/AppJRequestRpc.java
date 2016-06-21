/**
 *
 * Copyright 2016 
 * All right  reserved	
 * Created on 2016年6月21日 
 */
package com.ares.app.service.rpc;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import com.ares.framework.rpc.JsonServiceRpc;
import com.ares.framework.rpc.context.RpcContext;
import com.ares.framework.rpc.json.JsonRpcRequest;

/**
 *@author wesley E-mail:wiqi.zhong@gmail.com
 *
 */

@Controller
public class AppJRequestRpc extends JsonServiceRpc {

	@Inject
	private Provider<RpcContext> contextProvider;

	@Override
	public void postProcess() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void checkSession(JsonRpcRequest req) {
		// TODO Auto-generated method stub
		contextProvider.get().setSk(req.getSk());		
	}
}
