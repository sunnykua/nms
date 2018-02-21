package com.via.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import com.via.database.ShortMessageQueue;;

enum SMSProvider {
	SANZHU
}

enum SMSEncoding {
	BIG5
}

public class ShortMessage {
	
	/**
	 * Don't send to multiple recipients in one SMS.
	 */
	public static boolean send(final SMSProvider provider, final SMSEncoding encoding, final int timeout, final String username, final String password,
			final String recipient, final String text, final boolean toQueue) {
		ShortMessageInfo smsInfo = new ShortMessageInfo();
		smsInfo.setId(0);
		smsInfo.setTime(new Date());
		smsInfo.setProvider((provider == SMSProvider.SANZHU) ? "SANZHU" : "");		// TODO
		smsInfo.setEncoding((encoding == SMSEncoding.BIG5) ? "BIG5" : "");
		smsInfo.setTimeout(timeout);
		smsInfo.setUsername(username);
		smsInfo.setPassword(password);
		smsInfo.setText(text);
		smsInfo.setRecipient(recipient);

		return send(smsInfo, toQueue);
	}
	
	/**
	 * Don't send to multiple recipients in one SMS.
	 */
	public static boolean send(ShortMessageInfo smsInfo, final boolean toQueue) {
		try {
			doSend(smsInfo);
			
			//System.out.println("SMS: Send text to " + smsInfo.getRecipient() + " success.");
			return true;
		}
		catch (Exception e) {
			smsInfo.setReason(e.getMessage());
			if (toQueue) ShortMessageQueue.add(smsInfo);
			
			//System.out.println("SMS: Send text to " + smsInfo.getRecipient() + " FAILED.\n\t- reason: " + e.getMessage());
			return false;
		}
	}
	
	private static void doSend(final ShortMessageInfo smsInfo) throws Exception {
		String smsUrl;
		if (smsInfo.getProvider().equalsIgnoreCase("SANZHU")) {
			smsUrl = "http://smexpress.mitake.com.tw:9600/SmSendPost.asp";
		}
		else {
			throw new Exception("Unknown SMS provider.");
		}
		String urlString = String.format("%s?username=%s&password=%s&encoding=%s", smsUrl, smsInfo.getUsername(), smsInfo.getPassword(), smsInfo.getEncoding());
		//System.out.println("SMS url:\n" + urlString);
		HttpURLConnection huc = null;
		DataOutputStream out = null;
		BufferedReader buReader = null;
		String data = String.format("[%s]\r\nDestName=\r\ndstaddr=%s\r\nsmbody=%s\r\n", String.valueOf(101), smsInfo.getRecipient(), smsInfo.getText());
		//System.out.print("SMS data:\n" + data);
		System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(smsInfo.getTimeout()));
		System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(smsInfo.getTimeout()));
		
		try {
			huc = (HttpURLConnection)new URL(urlString).openConnection();
			huc.setInstanceFollowRedirects(true);
			huc.setDoInput(true);
			huc.setDoOutput(true);
			huc.setUseCaches(false);
			huc.setRequestMethod("POST");
			huc.setRequestProperty("Accept", "*/*");
			huc.setRequestProperty("Accept-Language", "zh-tw");		// orig: en-US
			huc.setRequestProperty("Content-Type", "text/html");
			huc.setRequestProperty("Accept-Encoding", "gzip, deflate");
			huc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
			
			out = new DataOutputStream(huc.getOutputStream());
			
			/*String smbody = "smbody=" + smsInfo.getText() + "\r\n";
		    String[] SMSList = List.split(",");
		    
		    for (int i = 0; i < SMSList.length; i++) {
            	String iNumber = "["+String.valueOf(101+i)+"]\r\n";
            	String idstaddr = "dstaddr="+(SMSList[i].equals("") ? "0900000000" : SMSList[i])+"\r\n";
            	
    			out.write(iNumber.getBytes(smsInfo.getEncoding()));
    			out.write("DestName=\r\n".getBytes(smsInfo.getEncoding()));
    			out.write(idstaddr.getBytes(smsInfo.getEncoding()));
    			out.write(smbody.getBytes(smsInfo.getEncoding()));
            }*/

			
			out.write(data.getBytes(smsInfo.getEncoding()));

			out.flush();
			out.close();

			buReader = new BufferedReader(new InputStreamReader(huc.getInputStream(), "Big5"));
			/*String sLine;
			System.out.println("SMS result:");
			while ((sLine = buReader.readLine()) != null)
				System.out.println(sLine);*/
		}
		catch (Exception e) {
            throw new Exception(e.getMessage());
		}
		finally {
			try {
				if (buReader != null) buReader.close();
			}
			catch (Exception e) {
			}
			finally {
				buReader = null;
			}
			
			try {
				if (out != null) out.close();
			}
			catch (Exception e) {
			}
			finally {
				out = null;
			}
			
			huc = null;
		}
	}
}
