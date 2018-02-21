package com.via.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.database.MailQueue;
import com.via.database.ShortMessageQueue;
import com.via.model.MailInfo;
import com.via.model.ShortMessageInfo;

/**
 * Servlet implementation class MailQueueViewer
 */
@WebServlet("/QueueViewer")
public class QueueViewer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueueViewer() {
        super();
        
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String type = (String) request.getParameter("type");
    	String action = (String) request.getParameter("action");
    	
    	if (type == null || action == null) {
    		System.out.println("something is null");
    	}
    	else if (type.equals("mail")) {
    		if (action.equals("read")) {
    			System.out.println("Mail read");
    			ArrayList<MailInfo> mailList = MailQueue.getAll();
    			request.setAttribute("mailQueueList", mailList);
    		}
    		else if (action.equals("delete")) {
    			System.out.println("Mail delete");
    			String[] selectedItems = request.getParameterValues("items[]");
    			String result = "Input error.";
    			if (selectedItems != null) {
    				int selectedCount = selectedItems.length;
    				int deletedCount = 0;
    				for (String itemId : selectedItems) {
    					if (MailQueue.remove(Integer.parseInt(itemId))) deletedCount++;
    				}
    				if (deletedCount == selectedCount) result = "Success";
    				else result = String.format("Caution: selected %d, but deleted %d mails.", selectedCount, deletedCount);
    			}
    			try {
    				response.getWriter().write(result);
    			}
    			catch (IOException e) {
    				System.out.println("Mail delete response failed.");
    			}
    		}
    	}
    	else if (type.equals("sms")) {
    		if (action.equals("read")) {
    			System.out.println("SMS read");
    			ArrayList<ShortMessageInfo> smsList = ShortMessageQueue.getAll();
    			request.setAttribute("smsQueueList", smsList);
    		}
    		else if (action.equals("delete")) {
    			System.out.println("SMS delete");
    			String[] selectedItems = request.getParameterValues("items[]");
    			String result = "Input error.";
    			if (selectedItems != null) {
    				int selectedCount = selectedItems.length;
    				int deletedCount = 0;
    				for (String itemId : selectedItems) {
    					if (ShortMessageQueue.remove(Integer.parseInt(itemId))) deletedCount++;
    				}
    				if (deletedCount == selectedCount) result = "Success";
    				else result = String.format("Caution: selected %d, but deleted %d SMS.", selectedCount, deletedCount);
    			}
    			try {
    				response.getWriter().write(result);
    			}
    			catch (IOException e) {
    				System.out.println("SMS delete response failed.");
    			}
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
