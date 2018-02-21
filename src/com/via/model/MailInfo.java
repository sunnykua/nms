package com.via.model;

import java.util.Date;

public class MailInfo {
	private int id;
	private Date time;
	private String from;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String text;
	private String smtpHost;
	private String smtpPort;
	private int smtpTimeout;
	private String username;
	private String password;
	private String reason;
	private String filePath;
	private String fileName;
	private String content;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public final String getFrom() {
		return from;
	}
	public final void setFrom(String from) {
		this.from = from;
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
	public final String getSubject() {
		return subject;
	}
	public final void setSubject(String subject) {
		this.subject = subject;
	}
	public final String getText() {
		return text;
	}
	public final void setText(String text) {
		this.text = text;
	}
	public final String getSmtpHost() {
		return smtpHost;
	}
	public final void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public final String getSmtpPort() {
		return smtpPort;
	}
	public final void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public final int getSmtpTimeout() {
		return smtpTimeout;
	}
	public final void setSmtpTimeout(int smtpTimeout) {
		this.smtpTimeout = smtpTimeout;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
