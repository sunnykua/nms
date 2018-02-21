package com.via.model;

public class MailConfig {
	private String subject;
	private String fromLable;
	private String to;
	private String cc;
	private String bcc;
	
	public MailConfig() {
		this.subject = "";
		this.fromLable = "";
		this.to = "";
		this.cc = "";
		this.bcc = "";
	}
	
	public final String getSubject() {
		return subject;
	}
	public final void setSubject(String subject) {
		this.subject = subject;
	}
	public final String getFromLable() {
		return fromLable;
	}
	public final void setFromLable(String fromLable) {
		this.fromLable = fromLable;
	}
	public final String getTo() {
		return to;
	}
	public final void setTo(String to) {
		this.to = to;
	}
	public final String getCc() {
		return cc;
	}
	public final void setCc(String cc) {
		this.cc = cc;
	}
	public final String getBcc() {
		return bcc;
	}
	public final void setBcc(String bcc) {
		this.bcc = bcc;
	}
}
