package com.via.model;

public class SmtpConfig {
	private String host;
	private String port;
	private int timeout;
	private String username;
	private String password;
	
	public SmtpConfig() {
		this.host = "";
		this.port = "";
		this.timeout = 0;
		this.username = "";
		this.password = "";
	}
	
	public final String getHost() {
		return host;
	}
	public final void setHost(String host) {
		this.host = host;
	}
	public final String getPort() {
		return port;
	}
	public final void setPort(String port) {
		this.port = port;
	}
	public final int getTimeout() {
		return timeout;
	}
	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public final String getUsername() {
		return username;
	}
	public final void setUsername(String username) {
		this.username = username;
	}
	public final String getPassword() {
		return password;
	}
	public final void setPassword(String password) {
		this.password = password;
	}
}
