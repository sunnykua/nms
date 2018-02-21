package com.via.model;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ReqServlet {
	
	public void redirect(String timeSelect) {
		
        HttpURLConnection connection = null;
        
        try {
             String message = URLEncoder.encode("ABC", "UTF-8");
             System.out.println("Test.java: redirect(): message=" + message);
             
             String osUrl = "";
             if(JSystem.osDistribution.equals("CentOS")){
            	 osUrl = "http://localhost/nms/ConvertToHtml?timeSelect=";
             }
             else
             {
            	 osUrl = "http://localhost/ConvertToHtml?timeSelect=";
             }
             
             URL url = new URL(osUrl + timeSelect);
             System.out.println("Test.java: redirect(): url=" + url);
             
             connection = (HttpURLConnection) url.openConnection();
             System.out.println("Test.java: redirect(): connection=" + connection);
             
             connection.setDoOutput(true);
             System.out.println("Test.java: redirect(): after connection.setDoOutput()");
             
             connection.setRequestMethod("POST");
             System.out.println("Test.java: redirect(): after connection.setRequestMethod()");
             
             OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
             System.out.println("Test.java: redirect(): writer=" + writer);
             
             writer.write("message=" + message);
             System.out.println("Test.java: redirect(): after writer.write()");
             
             writer.flush();
             System.out.println("Test.java: redirect(): after writer.flush()");
             
             writer.close();
             System.out.println("Test.java: redirect(): after writer.close()");

             System.out.println("Test.java: redirect(): connection.getResponseCode()=" + connection.getResponseCode());
             System.out.println("Test.java: redirect(): HttpURLConnection.HTTP_OK=" + HttpURLConnection.HTTP_OK);
             
             // if there is a response code AND that response code is 200 OK, do stuff in the first if block
           if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
               // OK

           // otherwise, if any other status code is returned, or no status code is returned, do stuff in the else block
           } else {
               // Server returned HTTP error code.
           }
           
        } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
             
        } catch (MalformedURLException e) {
             e.printStackTrace();
             
        } catch (IOException e) {
             e.printStackTrace();
             
        } finally {
             
             if (connection != null)
                  connection.disconnect();
        }
        
        
   }
}
