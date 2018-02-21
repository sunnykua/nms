package com.via.model;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.via.database.MailQueue;

public class Mail {
	
	/**
	 * Don't send to multiple recipients in one mail.
	 */
	public static boolean send(final String from, final String recipient, final String subject, final String text,
			final String smtpHost, final String smtpPort, final int smtpTimeout, final String username, final String password, final boolean toQueue, final String filePath, final String fileName, final String content) {
		MailInfo mailInfo = new MailInfo();
		mailInfo.setId(0);
		mailInfo.setTime(new Date());			// Only used for add_time in MailQueue, not for sending time
		mailInfo.setFrom(from);
		mailInfo.setTo(recipient);
		mailInfo.setCc("");
		mailInfo.setBcc("");
		mailInfo.setSubject(subject);
		mailInfo.setText(text);
		mailInfo.setSmtpHost(smtpHost);
		mailInfo.setSmtpPort(smtpPort);
		mailInfo.setSmtpTimeout(smtpTimeout);
		mailInfo.setUsername(username);
		mailInfo.setPassword(password);
		mailInfo.setFilePath(filePath);
		mailInfo.setFileName(fileName);
		mailInfo.setContent(content);

		return send(mailInfo, toQueue);
	}
	
	/**
	 * Don't send to multiple recipients in one mail.
	 */
	public static boolean send(MailInfo mailInfo, final boolean toQueue) {
		try {
			doSend(mailInfo);
			
			//System.out.println("JMail: Send mail to " + mailInfo.getTo() + " success.");
			return true;
		}
		catch (Exception e) {
			mailInfo.setReason(e.getMessage());
			if (toQueue) MailQueue.add(mailInfo);
			
			//System.out.println("JMail: Send mail to " + mailInfo.getTo() + " FAILED.\n\treason: " + e.getMessage());
			return false;
		}
	}
	
	private synchronized static void doSend(final MailInfo mailInfo) throws Exception {
		Date currentTime = new Date();
		Properties  props = new Properties();
		props.put("mail.smtp.host", mailInfo.getSmtpHost());
		props.put("mail.smtp.timeout", String.valueOf(mailInfo.getSmtpTimeout()));				// Socket read timeout
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", mailInfo.getSmtpPort());
		props.setProperty("mail.smtp.socketFactory.port", mailInfo.getSmtpPort());
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.connectiontimeout", String.valueOf(mailInfo.getSmtpTimeout()));

		Session session = Session.getInstance(props, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailInfo.getUsername(), mailInfo.getPassword());
			}
		});
		
		Message message = new MimeMessage(session);
		try {
			InternetAddress ia;
			if (mailInfo.getFrom().indexOf("<") > -1) {							// TODO better solution is that passing label and address separately
				String label = mailInfo.getFrom().substring(0, mailInfo.getFrom().indexOf("<"));
				String address = mailInfo.getFrom().substring(mailInfo.getFrom().indexOf("<") + 1, mailInfo.getFrom().indexOf(">"));
				ia = new InternetAddress(address, label, "UTF-8");
			}
			else {
				ia = new InternetAddress(mailInfo.getFrom());
			}
			message.setFrom(ia);
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailInfo.getTo()));
			message.setSubject(MimeUtility.encodeText(mailInfo.getSubject(), "utf-8", "B"));
			message.setSentDate(currentTime);				// Sending time always uses current time
			if(mailInfo.getContent() != null && !mailInfo.getContent().isEmpty()){
				message.setContent(mailInfo.getContent(),"text/html; charset=UTF-8");
			}
			else {
				message.setText(mailInfo.getText());
			}
		}
		catch (MessagingException e) {													// Create Message failed. This mail is unavailable.
			//System.out.println("Prepare mail for " + mailInfo.getTo() + " failed.");
			throw new Exception(e.getMessage());
		}
		
		//send attachment file
		/*if(mailInfo.getFilePath() != null || !mailInfo.getFilePath().isEmpty()){ //Is the attachment path exist?
			System.out.println("mailInfo.getFilePath()= "+ mailInfo.getFilePath());
			Multipart multipart = new MimeMultipart();
	        BodyPart msg = new MimeBodyPart();
	        msg.setContent("text", "text/html; charset=utf-8");
	        multipart.addBodyPart(msg);
	        
	        MimeBodyPart filePart = new MimeBodyPart();
	        filePart.attachFile("C:\\users\\jameshung\\2015-03-29.html"); //get file path
	        filePart.setHeader("Content-Type",  "application/octet-stream; charset=\"utf-8\"");
	        filePart.setFileName(MimeUtility.encodeText(mailInfo.getFileName(), "UTF-8", "B")); //set file name
	        multipart.addBodyPart(filePart);
	        message.setContent(multipart);
		}*/
        
		try {
			Transport.send(message);
		}
		catch (MessagingException e) {													// Send mail failed. Does it means timeout?
			//System.out.println("Send mail to " + mailInfo.getTo() + " failed.");
			System.out.println(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}
}
