package com.via.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSMSHttpPost {
	private static String sURL = "http://smexpress.mitake.com.tw:9600/SmSendPost.asp";  // 三竹 Http 發送 URL
	private static long lTimeout = 30000;                                          // 逾時時間 (單位：毫秒)
	private static String sEncoding = "Big5";
//	private static String sEncoding = "UTF8";
//	private static String sUserName = "86870786";                                          // 使用者帳號
//	private static String sPassword = "22185452";                                          // 使用者密碼
	
	public static void doSMSSend(String sUserName, String sPassword, String sMobile1, String sMobile2, 
			String sMobile3, String sMobile4, String sMobile5, String sMessage) throws Exception {
		System.setProperty("sun.net.client.defaultReadTimeout", "" + lTimeout);
		System.setProperty("sun.net.client.defaultConnectTimeout", "" + lTimeout);
		
		String sURLString = sURL + "?username=" + sUserName + "&password=" + sPassword + "&encoding=" + sEncoding;
		HttpURLConnection huc = null;
		DataOutputStream out = null;
		BufferedReader buReader = null;
		
		try {
			huc = (HttpURLConnection)new URL(sURLString).openConnection();
			huc.setInstanceFollowRedirects(true);
			huc.setDoInput(true);
			huc.setDoOutput(true);
			huc.setUseCaches(false);
			huc.setRequestMethod("POST");
			
			huc.setRequestProperty("Accept", "*/*");
			huc.setRequestProperty("Accept-Language", "zh-tw");
//			huc.setRequestProperty("Accept-Language", "en-US");
			huc.setRequestProperty("Content-Type", "text/html");
			huc.setRequestProperty("Accept-Encoding", "gzip, deflate");
			huc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
			
			out = new DataOutputStream(huc.getOutputStream());
			
			String dstaddr1 = "dstaddr="+sMobile1+"\r\n";
			String dstaddr2 = "dstaddr="+sMobile2+"\r\n";
			String dstaddr3 = "dstaddr="+sMobile3+"\r\n";
			String dstaddr4 = "dstaddr="+sMobile4+"\r\n";
			String dstaddr5 = "dstaddr="+sMobile5+"\r\n";
			String smbody = "smbody="+sMessage+"\r\n";
//			String smbody = "smbody=VIA NMS SMS Message : Device IP = 172.16.66.7 Port3 Critical port received Link Up notice\r\n";
			
			out.write("[101]\r\n".getBytes(sEncoding));
			out.write("DestName=\r\n".getBytes(sEncoding));
			out.write(dstaddr1.getBytes(sEncoding));
			out.write(smbody.getBytes(sEncoding));
			
			out.write("[102]\r\n".getBytes(sEncoding));
			out.write("DestName=\r\n".getBytes(sEncoding));
			out.write(dstaddr2.getBytes(sEncoding));
			out.write(smbody.getBytes(sEncoding));
			
			out.write("[103]\r\n".getBytes(sEncoding));
			out.write("DestName=\r\n".getBytes(sEncoding));
			out.write(dstaddr3.getBytes(sEncoding));
			out.write(smbody.getBytes(sEncoding));
			
			out.write("[104]\r\n".getBytes(sEncoding));
			out.write("DestName=\r\n".getBytes(sEncoding));
			out.write(dstaddr4.getBytes(sEncoding));
			out.write(smbody.getBytes(sEncoding));
			
			out.write("[105]\r\n".getBytes(sEncoding));
			out.write("DestName=\r\n".getBytes(sEncoding));
			out.write(dstaddr5.getBytes(sEncoding));
			out.write(smbody.getBytes(sEncoding));
			
			out.flush();
			out.close();
			
			buReader = new BufferedReader(new InputStreamReader(huc.getInputStream(), "Big5"));
			String sLine;
			
			while ((sLine = buReader.readLine()) != null)
				System.out.println(sLine);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (buReader != null) buReader.close();
			} catch (Exception e) {
			} finally {
				buReader = null;
			}
			
			try {
				if (out != null) out.close();
			} catch (Exception e) {
			} finally {
				out = null;
			}
			
			huc = null;
		}
	}

}
