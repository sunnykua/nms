package com.via.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JNetwork;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	JNetwork network = (JNetwork) getServletContext().getAttribute("network");
    	
    	if(request.getSession().getAttribute("username") == null){
			//System.out.println("Sign out!");
			response.getWriter().write("SignOut");
		}
    	else if(request.getSession().getAttribute("username") != null){
	    	String checkSessionId = network.checkSessionId(request.getSession().getAttribute("username").toString(), request.getSession().getId());
	    	if(checkSessionId !=null && checkSessionId == "Success"){
	    		//System.out.println("Is same ID.");
	    	}
	    	else if(checkSessionId !=null && checkSessionId == "Fail"){
	    		network.logout(request.getSession().getAttribute("username").toString());
	    		request.getSession().removeAttribute("username");
	    		response.getWriter().write("SignOut");
	    	}
    	}
    }
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
