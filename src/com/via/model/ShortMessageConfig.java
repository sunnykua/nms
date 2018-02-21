package com.via.model;

public class ShortMessageConfig {
	private String provider;	
	private String encoding;
	private String username;
	private String password;
	private int timeout;
	
	public ShortMessageConfig() {
		this.provider = "";
		this.encoding = "";
		this.username = "";
		this.password = "";
		this.timeout = 0;
	}
	
	public final String getProvider() {
		return provider;
	}
	public final void setProvider(String provider) {
		this.provider = provider;
	}
	public final String getEncoding() {
		return encoding;
	}
	public final void setEncoding(String encoding) {
		this.encoding = encoding;
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
	public final int getTimeout() {
		return timeout;
	}
	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
