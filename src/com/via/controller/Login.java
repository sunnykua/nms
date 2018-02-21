package com.via.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;







import org.apache.log4j.Logger;

import com.via.model.*;
import com.via.system.Config;
import com.google.gson.Gson;

/**
 * Servlet implementation class DataCenter
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String type = request.getParameter("type");
    	//login
    	String username = request.getParameter("username");
		String password = request.getParameter("password");
		String stay = request.getParameter("ifchecked");
		//account recovery
		String account_recovery = request.getParameter("account_recovery");
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		Logger logger = Logger.getLogger(Login.class);
		
		JUserInfo userInfo = JUserInfo.getInstance();         //get user list.
		CheckSessionBinding bind = new CheckSessionBinding(); //check session exist.
		int userLoginNum = Config.getLoginUserMax();
		
		if (type.equals("login")) {
			
			String userdata = "";
			String decryptPassword = null;
			int remoteLevel = 1;
			
			try {
				//Create MessageDigest object for MD5 or SHA-1
				MessageDigest digest = MessageDigest.getInstance("SHA-1");
				//Update input string in message digest
				digest.update(password.getBytes(), 0, password.length());
				//Converts message digest value in base 16 (hex) 
				decryptPassword = new BigInteger(1, digest.digest()).toString(16);
				//System.out.println("pwd in hex: " + decryptPassword);
			} catch (NoSuchAlgorithmException e) {
				userdata = "password failed.";
				//System.out.println("Password descrypt failed.");
			}

			if (decryptPassword != null && stay.equals("Y")) {
				userdata = network.login(username, decryptPassword);
				remoteLevel = network.checkIsManage(username);
				if(userdata=="0"){
					System.out.println("Level: Super");
					Cookie cookie1 = new Cookie("stay", "N");
				    response.addCookie(cookie1);
				    request.getSession().setAttribute("username", username);
					request.getSession().setAttribute("userLevel", 0);
					request.getSession().setAttribute("remoteLevel", remoteLevel);
					request.getSession().setMaxInactiveInterval(30*60);
					request.getSession().removeAttribute("URI");
				}
				else if(userdata=="1"){
					System.out.println("Level: Admin");

					Cookie cookie1 = new Cookie("stay", "Y");
					cookie1.setMaxAge(7*24*60*60);
				    response.addCookie(cookie1);
					request.getSession().setAttribute("username", username);
					request.getSession().setAttribute("userLevel", 1);
					request.getSession().setAttribute("remoteLevel", remoteLevel);
					request.getSession().setMaxInactiveInterval(7*24*60*60);
					request.getSession().removeAttribute("URI");
					logger.info("Account " + username + " login. IP address: " + request.getRemoteAddr());
				}
				else if(userdata=="2"){
					System.out.println("Level: User");
					
					Cookie cookie1 = new Cookie("stay", "Y");
					cookie1.setMaxAge(7*24*60*60);
				    response.addCookie(cookie1);
					request.getSession().setAttribute("username", username);
					request.getSession().setAttribute("userLevel", 2);
					request.getSession().setAttribute("remoteLevel", remoteLevel);
					request.getSession().setMaxInactiveInterval(7*24*60*60);
					request.getSession().removeAttribute("URI");
					logger.info("Account " + username + " login. IP address: " + request.getRemoteAddr());
				}
			}else if(decryptPassword != null && stay.equals("N")){
				userdata = network.login(username, decryptPassword);
				network.sessionId(username, request.getSession().getId());
				remoteLevel = network.checkIsManage(username);

				if(userdata=="0"){
					System.out.println("Level: Super");
					Cookie cookie1 = new Cookie("stay", "N");
					response.addCookie(cookie1);
				    request.getSession().setAttribute("username", username);
					request.getSession().setAttribute("userLevel", 0);
					request.getSession().setAttribute("remoteLevel", remoteLevel);
					request.getSession().setMaxInactiveInterval(12*60);
					request.getSession().removeAttribute("URI");
				}
				else if(userdata=="1"){
					System.out.println("Level: Admin");
					
					String[] listArray = userInfo.getListArray();
					//ArrayList<String[]> arraylist = userInfo.getArrayList();
					boolean isSameUsr = false;
					/*for(int i=0;i<arraylist.size();i++){
						if(arraylist.get(i)[0].equals(username)){
							isSameUsr = true;
						}
					}*/
					for(int i=0; i<listArray.length; i++){
						if(listArray[i].equalsIgnoreCase(username)){
							isSameUsr = true;
							userLoginNum = userLoginNum + 1;
						}
					}
					
					boolean accountRepeat = false;
					int count = 0;
					for(int i=1; i<listArray.length; i++){
						if(listArray.length > 1 && listArray[i].equals(listArray[i-1])){
							accountRepeat = true;
							count++;
						}
					}
					
					if(accountRepeat){
						userLoginNum = userLoginNum + count;
					}
					
					if(userInfo.getListSize() >= userLoginNum && isSameUsr == false){
						userdata = "Full";
					}
					else {
						Cookie cookie1 = new Cookie("stay", "N");
					    response.addCookie(cookie1);
						request.getSession().setAttribute("username", username);
						request.getSession().setAttribute("userLevel", 1);
						request.getSession().setAttribute("remoteLevel", remoteLevel);
						request.getSession().setMaxInactiveInterval(12*60);
						request.getSession().removeAttribute("URI");
						
						bind.setUsername(username);
						userInfo.addUser(username);   //add to list.
						//userInfo.addUserArray(username, request.getSession().getId());
						request.getSession().setAttribute("user", bind);   //It's binding.
	
						logger.info("Account " + username + " login. IP address: " + request.getRemoteAddr());
					}
				}
				else if(userdata=="2"){
					System.out.println("Level: User");

					String[] listArray = userInfo.getListArray();
					//ArrayList<String[]> arraylist = userInfo.getArrayList();
					boolean isSameUsr = false;
					/*for(int i=0;i<arraylist.size();i++){
						if(arraylist.get(i)[0].equals(username)){
							isSameUsr = true;
						}
					}*/
					for(int i=0; i<listArray.length; i++){
						if(listArray[i].equalsIgnoreCase(username)){
							isSameUsr = true;
							userLoginNum = userLoginNum + 1;
						}
					}
					
					boolean accountRepeat = false;
					int count = 0;
					for(int i=1; i<listArray.length; i++){
						if(listArray.length > 1 && listArray[i].equals(listArray[i-1])){
							accountRepeat = true;
							count++;
						}
					}
					
					if(accountRepeat){
						userLoginNum = userLoginNum + count;
					}
					
					if(userInfo.getListSize() >= userLoginNum && isSameUsr == false){
						userdata = "Full";
					}
					else {
						Cookie cookie1 = new Cookie("stay", "N");
					    response.addCookie(cookie1);
						request.getSession().setAttribute("username", username);
						request.getSession().setAttribute("userLevel", 2);
						request.getSession().setAttribute("remoteLevel", remoteLevel);
						request.getSession().setMaxInactiveInterval(12*60);
						request.getSession().removeAttribute("URI");
						
						bind.setUsername(username);
						userInfo.addUser(username);
						request.getSession().setAttribute("user", bind);
						logger.info("Account " + username + " login. IP address: " + request.getRemoteAddr());
					}
				}
			}

			String json_data = new Gson().toJson(userdata);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if(type.equals("logout")) {
			if(request.getSession().getAttribute("username") != null){
				String user = request.getSession().getAttribute("username").toString();
				network.logout(user);
			
				Object userCheck = request.getSession().getAttribute("userLevel");
				
				userInfo.removeUser(username);
				//userInfo.removeUserArray(user, request.getSession().getId());
			
				if(!userCheck.equals(0)){
					logger.info("Account " + user + " logout. IP address: " + request.getRemoteAddr());
				}
			}
			String getURI = request.getHeader("referer");
			Cookie cookies1 = new Cookie("stay", "");
			cookies1.setMaxAge(0);
		    response.addCookie(cookies1);
			request.getSession().removeAttribute("username");
			request.getSession().removeAttribute("userLevel");
			request.getSession().removeAttribute("user");
		    //request.getSession().invalidate();
			request.getSession().setAttribute("URI", getURI);
			
			String data = "Success";
			String json_data = new Gson().toJson(data);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json_data);
		}
		if (type.equals("recovery_pwd")) {
			System.out.println("account_recovery=" + account_recovery);
			String emailInput = network.accountRecovery(account_recovery);

				if(emailInput=="right email"){
					System.out.println("right email");
				}
				else if(emailInput=="search failed"){
					System.out.println("search failed");
				}
			String json_data = new Gson().toJson(emailInput);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (type.equals("remote_match")) {
			String ip = request.getParameter("ip");
			String[] result = network.rmiRemoteLogin(ip);
			System.out.println("center code= " + result[0]);
			String returnResult = "";
			if(result[0] != null){
				for(int i=0; i<result.length; i++){
					returnResult += result[i] + ",";
				}
			}
			System.out.println("returnResult= "+returnResult);
			try {
				response.getWriter().write(result[0] != null ? returnResult : "failed" );
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (type.equals("remote_login")) {
			String code = request.getParameter("code");
			System.out.println("local code= "+code);
			Calendar calendar = Calendar.getInstance();
			long centerTime = calendar.getTimeInMillis();
			String[] remoteInfo = JRemoteAuth.getRemoteInfo(centerTime);
			
			String userdata = "";
			String remoteUsername = remoteInfo[1];
			String decryptPassword = remoteInfo[2];
			if(code != null && code.equals(remoteInfo[0])){
				
				userdata = network.authlogin(remoteUsername, decryptPassword);
				network.sessionId(remoteUsername, request.getSession().getId());

				if(userdata=="0"){
					//System.out.println("Level: Super");
				}
				else if(userdata=="1"){
					System.out.println("Level: Admin");
					
					Cookie cookie1 = new Cookie("stay", "N");
					response.addCookie(cookie1);
					request.getSession().setAttribute("username", remoteUsername);
					request.getSession().setAttribute("userLevel", 1);
					request.getSession().setMaxInactiveInterval(12*60);
					request.getSession().removeAttribute("URI");
				}
				else if(userdata=="2"){
					//System.out.println("Level: User");

					Cookie cookie1 = new Cookie("stay", "N");
					response.addCookie(cookie1);
					request.getSession().setAttribute("username", remoteUsername);
					request.getSession().setAttribute("userLevel", 1);
					request.getSession().setMaxInactiveInterval(12*60);
					request.getSession().removeAttribute("URI");
				}
			}
			JRemoteAuth.removeRemoteInfo();
			try {
				response.getWriter().write(userdata);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}
