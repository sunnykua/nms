package com.via.model;

import java.util.Date;

public class ShortMessageInfo {
	private int id;
	private Date time;
	private String provider;
	private String recipient;
	private String text;
	private int timeout;
	private String encoding;
	private String username;
	private String password;
	private String reason;

	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final Date getTime() {
		return time;
	}
	public final void setTime(Date time) {
		this.time = time;
	}
	public final String getProvider() {
		return provider;
	}
	public final void setProvider(String provider) {
		this.provider = provider;
	}
	public final String getRecipient() {
		return recipient;
	}
	public final void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public final String getText() {
		return text;
	}
	public final void setText(String text) {
		this.text = text;
	}
	public final int getTimeout() {
		return timeout;
	}
	public final void setTimeout(int timeout) {
		this.timeout = timeout;
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
	public final String getReason() {
		return reason;
	}
	public final void setReason(String reason) {
		this.reason = reason;
	}
}
