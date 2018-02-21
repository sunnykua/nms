package com.via.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.via.model.*;
import com.google.gson.Gson;

/**
 * Servlet implementation class DataCenter
 */
@WebServlet("/Account")
public class Account extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Account() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String type = request.getParameter("type");
		//add new account
		String check_username = request.getParameter("add_username");
		String add_password = request.getParameter("add_password");
		String add_level = request.getParameter("add_level");
		String add_name = request.getParameter("add_name");
		String add_email = request.getParameter("add_email");
		String add_phoneNumber = request.getParameter("add_phoneNumber");
		String is_manage = "false";
		String is_remote = "false";
		//update jsp page user name
		String view_user = request.getParameter("view_user");
		//update account
		String update_username = request.getParameter("update_username");
		String bef_password = request.getParameter("bef_password");
		String update_password = request.getParameter("update_password");
		String update_name = request.getParameter("update_name");
		String update_email = request.getParameter("update_email");
		String update_phoneNumber = request.getParameter("update_phoneNumber");
		
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		
		if(type.equals("check_username")){
			String check_complete = network.checkUserName(check_username.toLowerCase());
			
			String json_data = new Gson().toJson(check_complete);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (type.equals("add_account")) {
			String pwd;
			
			try {
		        MessageDigest digest = MessageDigest.getInstance("SHA-1");
		        digest.update(add_password.getBytes(), 0, add_password.length());
		        pwd = new BigInteger(1, digest.digest()).toString(16);
		        
		        String insert_complete = network.addNewAccount(check_username.toLowerCase(), pwd, add_level, add_name, add_email, add_phoneNumber, is_manage, is_remote);
				
				String json_data = new Gson().toJson(insert_complete);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				try {
					response.getWriter().write(json_data);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
	        } catch (NoSuchAlgorithmException e) {
	        	System.out.println("Password failed when adding new account.");
	        }
		}
		if (type.equals("view_account")) {
			String[] view_account_complete = network.viewAccount(view_user.toLowerCase());

			if (view_account_complete != null) {
				//System.out.println(view_account_complete[0]);
				//System.out.println(view_account_complete[1]);
				//System.out.println(view_account_complete[2]);
			}

			String json_data = new Gson().toJson(view_account_complete);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (type.equals("update_pwd")) {
			try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-1");
	            
	            String pwd;
		        digest.update(bef_password.getBytes(), 0, bef_password.length());
		        pwd = new BigInteger(1, digest.digest()).toString(16);
		        System.out.println("pwd in hex: " + pwd);
		        
		        String pwd1;
		        digest.update(update_password.getBytes(), 0, update_password.length());
				pwd1 = new BigInteger(1, digest.digest()).toString(16);
				System.out.println("pwd in hex: " + pwd1);

				String Update_pwd_complete = network.updatePassword(update_username.toLowerCase(), pwd, pwd1);

				if(Update_pwd_complete=="Update Success"){
					Update_pwd_complete="Success";
				}else if(Update_pwd_complete=="Password Error"){
					Update_pwd_complete="Pwd Error";
				}

				String json_data = new Gson().toJson(Update_pwd_complete);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				try {
					response.getWriter().write(json_data);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			} catch (NoSuchAlgorithmException e) {
				 System.out.println("Password failed when updating password.");
	        }
		}
		if (type.equals("update_name")) {
			String update_name_complete = network.updateName(update_username.toLowerCase(), update_name);

			if(update_name_complete=="Update Success"){
				update_name_complete="Success";
			}

			String json_data = new Gson().toJson(update_name_complete);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (type.equals("update_email")) {
			String update_email_complete = network.updateEmail(update_username.toLowerCase(), update_email);

			if(update_email_complete=="Update Success"){
				update_email_complete="Success";
			}

			String json_data = new Gson().toJson(update_email_complete);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (type.equals("update_phoneNumber")) {
			String update_phoneNumber_complete = network.updatePhoneNumber(update_username.toLowerCase(), update_phoneNumber);

			if(update_phoneNumber_complete=="Update Success"){
				update_phoneNumber_complete="Success";
			}

			String json_data = new Gson().toJson(update_phoneNumber_complete);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (type.equals("view_alluser_account")) {
			Object userCheck = request.getSession().getAttribute("userLevel");
		    String[][] view_alluser_account_complete = network.viewAllUserAccount(userCheck);
		    ArrayList<String[]> list = new ArrayList<String[]>();

		    if (view_alluser_account_complete != null) {
		        for(int i=0;i<view_alluser_account_complete.length;i++){
		            list.add(view_alluser_account_complete[i]);
		            //System.out.println(Arrays.toString(list.get(list.size()-1)));
		        }
		    }

		    String json_data = new Gson().toJson(list);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    try {
		        response.getWriter().write(json_data);
		    } catch (IOException e) {
		        System.out.println(e.getMessage());
		    }
		}
		if (type.equals("account_delete")) {
			String[] deleteArray = request.getParameterValues("checkdelete[]");
			//System.out.println(Arrays.toString(deleteArray));
			network.removeAccount(deleteArray);
			
			
			for(int i=0;i<deleteArray.length;i++){
				//logger.info("Delete Account: " + deleteArray[i] + ". User: " + );
			}
		}
		if(type.equals("remoteAccountSetItems")){
			List<JAccount> table = network.remoteAccountSetItems();
    		if(table==null){
    			//System.out.println("no account items");
    		}else
    			request.setAttribute("remoteAccountSetItems", table);
		}
	    if (type.equals("accountItems")) {
			List<JAccount> table = network.accountItems();
	    		if(table==null){
	    			System.out.println("no account items");
	    		}else
	    			request.setAttribute("accountItems", table);
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
