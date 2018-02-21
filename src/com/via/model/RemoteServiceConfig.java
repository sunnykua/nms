package com.via.model;

public class RemoteServiceConfig {
	private boolean enable;
	private String localAddress;
	private String serverAddress;
	
	public final boolean isEnable() {
		return enable;
	}
	public final void setEnable(boolean enable) {
		this.enable = enable;
	}
	public final String getLocalAddress() {
		return localAddress;
	}
	public final void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}
	public final String getServerAddress() {
		return serverAddress;
	}
	public final void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
}
