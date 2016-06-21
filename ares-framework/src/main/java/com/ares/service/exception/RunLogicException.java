package com.ares.service.exception;

public class RunLogicException  extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3695156115567584160L;
	private String msg;
    private String webPage;
	public RunLogicException(String msg,String webPage){
		super(msg);
		this.msg = msg;
		this.webPage = webPage;
	}
	
	public String getMsg(){
		return msg;
	}
	public String getWebPage(){
		return webPage;
	}
}
