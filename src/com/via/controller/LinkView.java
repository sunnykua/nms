package com.via.controller;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;
import com.via.model.JLinkView.JLinkViewItem;

import org.apache.log4j.Logger;


/**
 * Servlet implementation class LinkStatus
 */
@WebServlet("/LinkView")
public class LinkView extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LinkView() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String ip = request.getParameter("ip");
		
	    //Logger logger = Logger.getLogger(LinkView.class);

		if (ip != null) {
			//System.out.println("link view - ip: " + ip);
	        //logger.debug("Device IP = " + ip + " Link View Start");   
			List<JLinkViewItem> linkViewItems = network.linkView(ip);
			request.setAttribute("linkView", linkViewItems);
		}

		//double endTime = System.currentTimeMillis();
		//System.out.println("Using Time: " + (endTime - startTime) / 1000 + " sec.");
        //logger.debug(" Link View Using Time: " + (endTime - startTime) / 1000 + " sec.");   

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}
}
