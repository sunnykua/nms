package com.via.model;

import java.util.Date;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import com.via.database.*;

public class JLogin {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private JDbAccount dbAccount;
	
	public JLogin(JDbAccount dbAccount) {
		this.dbAccount = dbAccount;
	}
	
	// ====================================================================================================

	public String login(final String username, final String password) {
		String output = null;
		String level = null;
		String[] result = dbAccount.getAccountInfo(username);
		
		if (result != null && result[3].equals("true")) {
			output="Eusr";
		}
		else if (result != null && result[0].equalsIgnoreCase(username) && result[1].equals(password)) {
			level = result[2];

			if(level.equals("admin")){
				output = "1";
			}
			else if(level.equals("user")){
				output = "2";
			}
			else{
				output = "0";
			}
			dbAccount.updateLoginDate(username, sdf.format(new Date()));
		}
		else if (result == null) {
			output="Eusr"; // represent username is error.
		}
		else {
			output="Epwd"; // represent pwd is error.
		}

		return output;
	}
	
	public String logout(final String username) {
		String output = "Logout Success.";
		
		dbAccount.updateLogoutDate(username, sdf.format(new Date()));
		
		return output;
	}
	
	public int checkIsManage(final String username) {
		int output = 1;
		String[] result = dbAccount.getAccountInfo(username);
		
		if(result != null && result[4].equals("true")){
			output = 0;
		}
		else {
			output = 1;
		}
		
		
		return output;
	}
	
	public String sessionId(final String username, String session_id) {
		dbAccount.updateSessionId(username, session_id);
		
		return "Update SessionId Success. ";
	}
	
	public String checkSessionId(final String username, String session_id) {
		String output = null;
		String[] oldSessionId = dbAccount.getSessionId(username);
		
		if(oldSessionId == null || oldSessionId[0].equals(session_id)){
			output = "Success";
		}
		else if(!oldSessionId[0].equals(session_id)){
			output = "Fail";
		}
		
		
		return output;
	}
	
	public String checkUsername(final String username) {

		return dbAccount.isAccountExisted(username) ? "Repeat" : "No Repeat";
	}
	
	public String newAccount(final String username, final String password, final String level, final String name, final String email, final String phone_number, final String is_manage, final String is_remote) {
		if (dbAccount.isAccountExisted(username)) {
			return "Repeat";
		}
		
		if (dbAccount.addAccount(username, password, level, name, email, phone_number, is_manage, is_remote)) {
			return "Success";
		}
		else {
			return "Failed.";
		}
	}
	
	public final String[] viewAccount(final String username) {
		return dbAccount.getAccountView(username);
	}
	
	public final String updatePassword(final String username, final String bef_password , final String update_password) {
		String[] result = dbAccount.getAccountInfo(username);

		if (result != null && result[0].equalsIgnoreCase(username) && result[1].equals(bef_password)) {
			return dbAccount.updatePassword(username, update_password) ? "Update Success" : "Update Failed.";
		}
		else {
			return "Password Error";
		}
	}
	
	public final String updateName(final String username, final String name) {
		return dbAccount.updateName(username, name) ? "Update Success" : "Update Failed.";			// Doesn't need to check password?
	}
	
	public final String updateEmail(final String username, final String email) {
		return dbAccount.updateEmail(username, email) ? "Update Success" : "Update Failed.";			// Doesn't need to check password?
	}
	
	public final String updatePhoneNumber(final String username, final String phoneNumber) {
		return dbAccount.updatePhoneNumber(username, phoneNumber) ? "Update Success" : "Update Failed.";			// Doesn't need to check password?
	}

	public final String[][] viewAllUserAccount(Object userCheck) {
        return dbAccount.getViewAllUserAccount(userCheck);
    }
	
	public String accountRecovery(final String email) {
		if (dbAccount.getAccountRecovery(email)) {
			
			
		int i;
		int[] A = new int[8];

		for (i = 0; i < 8; i++) {

			if (i < 3) { // 前 3 放數字
				A[i] = (int) ((Math.random() * 10) + 48);
			} else if (i < 6) { // 中間 3 位放大寫英文
				A[i] = (int) (((Math.random() * 26) + 65));
			} else { // 後 2 位放小寫英文
				A[i] = ((int) ((Math.random() * 26) + 97));
			}
		}
			    
		      String randompwd = String.valueOf((char)A[1])+String.valueOf((char)A[2])+String.valueOf((char)A[3])
		    		  +String.valueOf((char)A[4])+String.valueOf((char)A[5])+String.valueOf((char)A[6])+String.valueOf((char)A[7]);
		      System.out.println("pwd="+randompwd);
		      
		      try {
			        MessageDigest MD5 = MessageDigest.getInstance("MD5");
			        MD5.update(randompwd.getBytes(), 0, randompwd.length());
			        String pwd1 = new BigInteger(1, MD5.digest()).toString(16);
			        try {
				        MessageDigest SHA1 = MessageDigest.getInstance("SHA-1");
				        SHA1.update(pwd1.getBytes(), 0, pwd1.length());
				        String pwd2 = new BigInteger(1, SHA1.digest()).toString(16);
				        
				        MessageDigest SHA2 = MessageDigest.getInstance("SHA-1");
						SHA2.update(pwd2.getBytes(), 0, pwd2.length());
						String newpwd = new BigInteger(1, SHA2.digest()).toString(16);
				        
						dbAccount.pwdRecovery(newpwd, email);
						
						System.out.println(newpwd);
						
						String text = "Your new password:"+randompwd;
						
						JAlarm.doEmailSend(text, email);
						
			      } catch (NoSuchAlgorithmException e) {
			        	System.out.println("Password failed when adding new account.");
			      }
			        
		      } catch (NoSuchAlgorithmException e) {
		        	System.out.println("Password failed when adding new account.");
		      }
			    
			return "right email";
		}
		
		/*if (database.addAccount(tableName, email)) {
			return "Success";
		}*/
		else {
			return "search failed";
		}
	}
}
