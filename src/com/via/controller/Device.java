package com.via.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.csvreader.CsvReader;
import com.google.gson.Gson;
import com.via.database.JModule;
import com.via.model.JEntry;
import com.via.model.JGroup;
import com.via.model.JProfile;
import com.via.model.ReqServlet;
import com.via.model.JDevice;
import com.via.model.JNetwork;
import com.via.model.JPoeSchedule;
import com.via.model.JSystem;
import com.via.model.JTools;
import com.via.model.JTrapStatusItem;
import com.via.model.Mail;
import com.via.model.MailConfig;
import com.via.model.SmtpConfig;
import com.via.system.Config;
import com.via.topology.JGroupTopology;

/**
 * Servlet implementation class Device
 */
@MultipartConfig
@WebServlet("/Device")
public class Device extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Device() {
        super();
    }
    
	JNetwork network;

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String method = request.getParameter("method");
    	network = (JNetwork) getServletContext().getAttribute("network");
    	JDevice device = new JDevice();
    	if (method.equals("check")) {
    		
    		String[][] deviceInfoArray = network.checkDevices(JTools.parseSnmpAddress(request.getParameterValues("ipaddrstring[]")),device.getReadCommunity());
    		String responseText = "";
    		
    		if (deviceInfoArray != null) {
    			for (String[] deviceInfo : deviceInfoArray) {
    				if (deviceInfo.length == 4)
    					responseText += deviceInfo[0] + "," + deviceInfo[1] + "," + deviceInfo[2] + "," + deviceInfo[3] + ",";	// snmpAddress, sysDescr, phyAddr, localIp
    			}
    		}
    		
    		try {
				response.getWriter().write(responseText);
			} catch (IOException e) {
				System.out.println("Response candidates to AJAX failed.");
			}
    	}
    	else if (method.equals("nms_add")) {
    		
    		String ip = request.getParameter("ip");
    		String alias = request.getParameter("alias");
    		
    		if (ip == null) return;
    		
    		boolean isOk = false;
    		
    		isOk = network.addNmsDevice(ip, alias);
	        network.portAddInsert(ip);
	        
	        try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
                System.out.println("Response failed when adding device.");
            }
    		
    	}
    	else if (method.equals("deviceList_get")) {
    		String ip = request.getParameter("ip");
    		//System.out.println("ip= "+ip);
    		if (!ip.equals("")) {
	    		List<JDevice> deviceTable = network.getRemoteDeviceTable(ip);
	    		request.setAttribute("deviceTable", deviceTable);
    		}
    	}
    	else if (method.equals("nms_remoteAccountSet")) {
    		String ip = request.getParameter("ip");
    		String userItems = request.getParameter("userItems");
    		boolean isOk = network.setRemoteAccountItems(ip, userItems);
    		
    		try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
                
            }
    	}
    	else if (method.equals("device_add")) {
    		
    		String snmpVersion = request.getParameter("version");
            String snmpAddr = request.getParameter("ip");
            String type = request.getParameter("type");                 // it is ObjectID
            
            String community = request.getParameter("community");
            String snmpTimeout = request.getParameter("snmpTimeout");
            
            String securityName = request.getParameter("securityName");
            String securityLevel = request.getParameter("securityLevel");
            String authProtocol = request.getParameter("authProtocol");
            String authPassword = request.getParameter("authPassword");
            String privProtocol = request.getParameter("privProtocol");
            String privPassword = request.getParameter("privPassword");
            
            
            boolean isManaged = false;
            if (snmpVersion == null) return;
            if (snmpVersion.equals("-1") && !type.isEmpty()) isManaged = false;
            else if (!snmpVersion.equals("-1")) isManaged = true;
            else return;
            /*String[] ipAddrArray = request.getParameterValues("ipaddrstring[]");
            if (ipAddrArray == null || ipAddrArray.length == 0) {
                System.out.println("input ipaddrstring[] is empty.");
                return;
            }
            if (ipAddrArray.length != 3) {
                System.out.println("input ipaddrstring can't extract ip, community, and snmptimeout.");
                return;
            }*/
            String ipAddr = JTools.parseSnmpIp(snmpAddr);

    		String responseText = "";
            if (network.isDeviceNumberReachLimitation()) {
                responseText = "Failed, device number has reached license limitation.";
    		}
    		else {
    		    boolean isOk = false;
    		    if (isManaged) {
    		        System.out.println("try to add managed device: " + snmpAddr + ", community: " + community + ", snmpTimeout: " + snmpTimeout);
    		        if(snmpVersion.equals("1") || snmpVersion.equals("2")){
	    		        isOk = network.addManagedDevice(snmpAddr, community, Integer.parseInt(snmpTimeout), Integer.parseInt(snmpVersion),
	    		        		"", 0, "", "", "", "");
	    		        network.portAddInsert(ipAddr);
    		        }
    		        else if(snmpVersion.equals("3")){
    		        	isOk = network.addManagedDevice(snmpAddr, "", Integer.parseInt(snmpTimeout), Integer.parseInt(snmpVersion),
	    		        		securityName, Integer.parseInt(securityLevel), authProtocol, authPassword, privProtocol, privPassword);
	    		        network.portAddInsert(ipAddr);
    		        }
    		        else if(snmpVersion.equals("0")){
    		        	System.out.println("try to add ping only device: " + snmpAddr + ", type: " + type);
        		        isOk = network.addPingManagedDevice(snmpAddr, type);
        		        network.portAddInsert(ipAddr);
    		        }
    		    }
    		    else {
    		        System.out.println("try to add non-managed device: " + snmpAddr + ", type: " + type);
    		        isOk = network.addNonManagedDevice(snmpAddr, type);
    		        network.portAddInsert(ipAddr);
    		    }
    		    if (isOk) responseText = "Success";
    		    else responseText = "Failed";
    		}
            
            try {
                response.getWriter().write(responseText);
            }
            catch (IOException e) {
                System.out.println("Response failed when adding device.");
            }
    	}
    	else if (method.equals("nms_delete")) {

    		boolean isOk = network.removeNmsDevices(request.getParameterValues("checkdelete[]"));
    		
    		try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("device_delete")) {

    		boolean isOk = network.removeDevices(request.getParameterValues("checkdelete[]"),"device");
    		
    		try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("group_delete")) {

    		boolean isOk = network.removeGroupDevices(request.getParameterValues("checkdelete[]"));
    		
    		try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("device_scan")) {
            String snmpVersion = request.getParameter("version");
            String type = request.getParameter("type");
            String startIp = request.getParameter("startip");
            String endIp = request.getParameter("endip");
            String community = request.getParameter("community");
            String snmpTimeout = request.getParameter("snmpTimeout");
            
            String securityName = request.getParameter("securityName");
            String securityLevel = request.getParameter("securityLevel");
            String authProtocol = request.getParameter("authProtocol");
            String authPassword = request.getParameter("authPassword");
            String privProtocol = request.getParameter("privProtocol");
            String privPassword = request.getParameter("privPassword");

            System.out.println("startIp="+startIp+",endIp="+endIp+",community="+community+",snmptimeout="+snmpTimeout+",snmpVersion="+snmpVersion);
            
            String result ="";
            
            if(snmpVersion.equals("1") || snmpVersion.equals("2")){
            	result = network.startScanIpThread(startIp, endIp, community, Integer.parseInt(snmpTimeout), Integer.parseInt(snmpVersion), "") ? "true" : "false";
            }
            else {
            	result = network.startScanIpThread(startIp, endIp, "public", 1000, Integer.parseInt(snmpVersion), type) ? "true" : "false";
            }
            String ipAddr1 = JTools.parseSnmpIp(startIp);
            String ipAddr2 = JTools.parseSnmpIp(endIp);
            network.portScanInsert(ipAddr1, ipAddr2);
            try {
                response.getWriter().write(result);
            }
            catch (IOException e) {
                System.out.println("Response failed.");
            }
        }
    	else if (method.equals("device_scan_status")) {
    	    
    	    try {
    	        String scanStatus = network.getScanIpStatus();
    	        //System.out.println("Scan status: " + scanStatus);
                response.getWriter().write(scanStatus);
            }
            catch (IOException e) {
                System.out.println("Response failed.");
            }
    	}
    	else if (method.equals("group_view")) {
    		List<JGroup> result = network.viewGroup();
    		
    		request.setAttribute("groupList", result);
    	}
    	else if (method.equals("group_add")) {
    		
    		String groupName = request.getParameter("groupName");
    		
    		if (groupName == null) return;
    		
    		String result = "fail";
    		
    		result = network.addGroup(groupName);
	        
	        try {
                response.getWriter().write(result);
            }
            catch (IOException e) {
                System.out.println("Response failed when adding group.");
            }
    		
    	}
    	else if (method.equals("groupDevice")) {
    		String name ="";
			try {
				name = new String(request.getParameter("name").getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    		//System.out.println("name= "+name);
    		
    		JGroupTopology groupTopology = new JGroupTopology();
    		List<JEntry> result = groupTopology.findGroupDevice(name, network.getDeviceList());
    		
    		String json_data = new Gson().toJson(result);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
    		
    		try {
                response.getWriter().write(json_data);
            }
            catch (IOException e) {
                System.out.println("Response failed when get group topology.");
            }
    	}
    	else if (method.equals("groupTopology")) {
    		JGroupTopology groupTopology = new JGroupTopology();
    		JEntry result = groupTopology.findGroupTree();
    		
    		String json_data = new Gson().toJson(result);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
    		
    		try {
                response.getWriter().write(json_data);
            }
            catch (IOException e) {
                System.out.println("Response failed when get group topology.");
            }
    	}
    	else if (method.equals("groupNameUpdate")) {
    		String groupName = request.getParameter("groupName");
    		String bgroupName = request.getParameter("bgroupName");
    		
    		if (groupName == null) return;
    		
    		String result = "fail";
    		
    		result = network.updateGroup(groupName, bgroupName);
	        
	        try {
                response.getWriter().write(result);
            }
            catch (IOException e) {
                System.out.println("Response failed when adding group.");
            }
    		
    	}
    	else if (method.equals("groupParent")) {
    		String groupName = request.getParameter("groupName");
    		String parent = request.getParameter("parent");
    		
    		if (groupName == null) return;
    		
    		String result = "fail";
    		
    		result = network.updateParent(groupName, parent);
	        
	        try {
                response.getWriter().write(result);
            }
            catch (IOException e) {
                System.out.println("Response failed when adding group.");
            }
    		
    	}
    	else if (method.equals("groupRoot")) {
    		String groupName = request.getParameter("groupName");
    		String root = request.getParameter("root");
    		
    		if (groupName == null) return;
    		
    		String result = "fail";
    		
    		result = network.updateRoot(groupName, root);
	        
	        try {
                response.getWriter().write(result);
            }
            catch (IOException e) {
                System.out.println("Response failed when adding group.");
            }
    		
    	}
    	else if (method.equals("device_info")) {
    		String ip = request.getParameter("ip");
    		//System.out.println("ip=" + ip);
    		
    		String[] info = network.getDeviceSystemInfo(ip);
    		
    		request.setAttribute("sysDescr", info[0]);
    		request.setAttribute("sysObjectId", info[1]);
    		request.setAttribute("sysUpTime", info[2]);
    		request.setAttribute("sysContact", info[3]);
    		request.setAttribute("sysName", info[4]);
    		request.setAttribute("sysLocation", info[5]);
    	}
    	else if (method.equals("remote_set")) {
    		String[] inputData = request.getParameterValues("select_port_ip[]");
    		boolean isAllOK = true;
    		
    		if (inputData != null && inputData.length > 1) {
    		    String deviceIp = inputData[0];
    		    
    		    //int count = 0;
    		    System.out.println("device " + deviceIp + ":");
    		    for (int i = 1; i < inputData.length; i++) {
    		        String[] dataSet = inputData[i].split("/");
    		        if (dataSet.length != 3) continue;
    		        int portId;
    		        try {
    		            portId = Integer.parseInt(dataSet[0]);
    		        }
    		        catch (NumberFormatException e) {
    		            System.out.println("port id is parsing wrong at " + dataSet[0]);
    		            isAllOK = false;
    		            continue;
    		        }
    		        boolean isManual = dataSet[1].equals("auto") ? false : true;
    		        String remoteIp = dataSet[2].equals("none") ? "" : dataSet[2];
    		        //boolean isMonitored = dataSet[3].equals("1") ? true : false;
    		        //boolean isPoePower = dataSet[4].equals("1") ? true : false;
    		        //String poeScheduleName = dataSet[5].equals("none") ? "" : dataSet[5];
    		        
    		        /*if(dataSet[4].equals("0"))
    		        	count++;*/

    		        isAllOK &= network.setDeviceRemote(deviceIp, portId, isManual, remoteIp/*, isMonitored, isPoePower, poeScheduleName*/);
    		        System.out.println("portId:" + portId + ", isManu:" + isManual + ", remIp:" + remoteIp);
    		    }
    		    /*if(count == (inputData.length-1)){
    		    	network.setPoePowerStatus(deviceIp);
    		    }*/
    		}
    		else {
    		    System.out.println("input data is wrong.");
    		    isAllOK = false;
    		}
    		
    		try {
                response.getWriter().write(isAllOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("poeSchedule_set")) {
    		String[] inputData = request.getParameterValues("poeArray[]");
    		boolean isAllOK = true;
    		
    		if (inputData != null && inputData.length > 1) {
    		    String deviceIp = inputData[0];
    		    
    		    int count = 0;
    		    System.out.println("device " + deviceIp + ":");
    		    for (int i = 1; i < inputData.length; i++) {
    		        String[] dataSet = inputData[i].split("/");
    		        if (dataSet.length != 3) continue;
    		        int portId;
    		        try {
    		            portId = Integer.parseInt(dataSet[0]);
    		        }
    		        catch (NumberFormatException e) {
    		            System.out.println("port id is parsing wrong at " + dataSet[0]);
    		            isAllOK = false;
    		            continue;
    		        }
    		        boolean isPoePower = dataSet[1].equals("1") ? true : false;
    		        String poeScheduleName = dataSet[2].equals("none") ? "" : dataSet[2];
    		        
    		        if(dataSet[1].equals("0"))
    		        	count++;

    		        isAllOK &= network.setPoeSchedule(deviceIp, portId, isPoePower, poeScheduleName);
    		        System.out.println("portId:" + portId + ", isPPow:" + isPoePower + ", pScheName:" + poeScheduleName);
    		    }
    		    if(count == (inputData.length-1)){
    		    	network.setPoePowerStatus(deviceIp);
    		    }
    		}
    		else {
    		    System.out.println("input data is wrong.");
    		    isAllOK = false;
    		}
    		
    		try {
                response.getWriter().write(isAllOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("monitor_set")) {
    		String[] inputData = request.getParameterValues("monitorArray[]");
    		boolean isAllOK = true;
    		
    		if (inputData != null && inputData.length > 1) {
    		    String deviceIp = inputData[0];
    		    
    		    System.out.println("device " + deviceIp + ":");
    		    for (int i = 1; i < inputData.length; i++) {
    		        String[] dataSet = inputData[i].split("/");
    		        if (dataSet.length != 2) continue;
    		        int portId;
    		        try {
    		            portId = Integer.parseInt(dataSet[0]);
    		        }
    		        catch (NumberFormatException e) {
    		            System.out.println("port id is parsing wrong at " + dataSet[0]);
    		            isAllOK = false;
    		            continue;
    		        }
    		        boolean isMonitored = dataSet[1].equals("1") ? true : false;
    		        

    		        isAllOK &= network.setMonitor(deviceIp, portId, isMonitored);
    		        System.out.println("portId:" + portId + ", isMo:" + isMonitored);
    		    }
    		}
    		else {
    		    System.out.println("input data is wrong.");
    		    isAllOK = false;
    		}
    		
    		try {
                response.getWriter().write(isAllOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("changeModel")) {
    		String snmpAddr = request.getParameter("ip");
            String type = request.getParameter("type");
            
            String [] ipList = new String[1];
            ipList[0] = snmpAddr;
            
            boolean isOk = false;
            boolean result = network.removeDevices(ipList,"device");
            
            if(result){
            	isOk = network.addPingManagedDevice(snmpAddr, type);
            }
            
            try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
            

    		//network.updateDeviceAliasName(data[0],data[1]);
    	}
    	else if (method.equals("nmsAliasUpdate")) {
    		String ip = request.getParameter("ip");
    		String aliasName = request.getParameter("aliasName");

    		boolean isOK = network.updateNmsAliasName(ip, aliasName);
    		
    		try {
                response.getWriter().write(isOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("aliasUpdate")) {
    		String ip = request.getParameter("ip");
    		String aliasName = request.getParameter("aliasName");

    		boolean isOK = network.updateDeviceAliasName(ip, aliasName);
    		
    		try {
                response.getWriter().write(isOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("nmsRefresh")) {
    		boolean isOK = network.nmsRefresh(request.getParameterValues("selected[]"));
    		
    		try {
                response.getWriter().write(isOK ? "Finish" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("deviceRefresh")) {
    		boolean isOK = network.devicesRefresh(request.getParameterValues("selected[]"));
    		
    		try {
                response.getWriter().write(isOK ? "Finish" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("deviceMacRefresh")) {
    		String data = request.getParameter("ip");

    		String isOK = network.deviceMacRefresh(data);
    		
    		try {
                response.getWriter().write(isOK == "success" ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("devGroup_set")) {
    		boolean isOK = network.setDevGroup(request.getParameterValues("selected[]"), request.getParameter("groupName"));
    		
    		try {
                response.getWriter().write(isOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("devFil_set")) {
    		boolean isOK = network.setDevPro(request.getParameterValues("selected[]"), request.getParameter("profile"));
    		
    		try {
                response.getWriter().write(isOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("devFilter_set")) {
    		String[] inputData = request.getParameterValues("devFilterArray[]");
    		boolean isAllOK = true;
    		
    		if (inputData != null && inputData.length > 1) {
    		    String deviceIp = inputData[0];
    		    System.out.println("device " + deviceIp + ":");
    		    
    		    String[] dataSet = inputData[1].split("/");
    		    boolean isMailFilter = dataSet[0].equals("1") ? true : false;
    		    String profileName = dataSet[1].equals("none") ? "" : dataSet[1];
    		    isAllOK &= network.setDevProfile(deviceIp, isMailFilter, profileName);
    		    System.out.println("isMFPow:" + isMailFilter + ", pProfileName:" + profileName);
    		    
    		}
    		else {
    		    System.out.println("input data is wrong.");
    		    isAllOK = false;
    		}
    		
    		try {
                response.getWriter().write(isAllOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("snmpUpdate")) {
    		String snmpAddr = request.getParameter("ip");
    		String snmpVersion = request.getParameter("version");
    		String readCommunity = request.getParameter("readCommunity");
    		String writeCommunity = request.getParameter("writeCommunity");
            String snmpTimeout = request.getParameter("snmpTimeout");
            
            String securityName = request.getParameter("securityName");
            String securityLevel = request.getParameter("securityLevel");
            String authProtocol = request.getParameter("authProtocol");
            String authPassword = request.getParameter("authPassword");
            String privProtocol = request.getParameter("privProtocol");
            String privPassword = request.getParameter("privPassword");
            
            boolean isOk = true;

            if(snmpVersion.equals("2") || snmpVersion.equals("1")){
            	isOk = network.updateSnmpV2(snmpAddr, Integer.parseInt(snmpVersion), Integer.parseInt(snmpTimeout), readCommunity, writeCommunity);
            }
            else if(snmpVersion.equals("3")){
            	if(securityLevel.equals("1")){
            		isOk = network.updateSnmpV3(snmpAddr, Integer.parseInt(snmpVersion), Integer.parseInt(snmpTimeout),
                			securityName, Integer.parseInt(securityLevel), null, null, null, null);
            	}
            	else if(securityLevel.equals("2")){
            		isOk = network.updateSnmpV3(snmpAddr, Integer.parseInt(snmpVersion), Integer.parseInt(snmpTimeout),
                			securityName, Integer.parseInt(securityLevel), authProtocol, authPassword, null, null);
            	}
            	else {
            	isOk = network.updateSnmpV3(snmpAddr, Integer.parseInt(snmpVersion), Integer.parseInt(snmpTimeout),
            			securityName, Integer.parseInt(securityLevel), authProtocol, authPassword, privProtocol, privPassword);
            	}
            }
            
            try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    		
    	}
    	else if (method.equals("nonManaged")) {
    		List<JModule> table = network.nonManagedModule();
    		if(table==null){
    			System.out.println("no nonManaged device");
    		}else
			request.setAttribute("nonManagedModule", table);
    	}
    	else if (method.equals("pingManaged")) {
    		List<JModule> table = network.pingManagedModule();
    		if(table==null){
    			System.out.println("no pingManaged device");
    		}else
			request.setAttribute("pingManagedModule", table);
    	}
    	else if (method.equals("poe_items")) {
    		List<JPoeSchedule> table = network.poeScheduleItems();
    		if(table==null){
    			//System.out.println("no poe items");
    		}else
			request.setAttribute("poeItems", table);
    	}
    	else if (method.equals("profile_items")) {
    		List<JProfile> table = network.profileItems();
    		if(table==null){
    			System.out.println("no profile items");
    		}else
			request.setAttribute("profileItems", table);
    	}
    	else if (method.equals("trap_view")) {
    		String ip = request.getParameter("ip");
    		List<JTrapStatusItem> items = network.getTrapStatus(ip);
    		request.setAttribute("trapItems", items);
    	}
    	else if (method.equals("trap_set")) {
    		String[] inputData = request.getParameterValues("trapArray[]");
    		System.out.println("inputData="+Arrays.toString(inputData));
    		
    		network.setTrapStatus(inputData[0], inputData[1], inputData[2], inputData[3], inputData[4]);
    	}
    	else if (method.equals("trap_delete")) {
    		network.removeTrap(request.getParameter("ip"), request.getParameterValues("checkdelete[]"));
    	}
    	else if (method.equals("portAlias_set")) {
    		String[] inputData = request.getParameterValues("aliasArray[]");
    		boolean isAllOK = true;
    		
    		if (inputData != null) {
    		    String deviceIp = inputData[0];
    		    
    		    System.out.println("device " + deviceIp + ":");
    		    for (int i = 1; i < inputData.length; i++) {
    		        String[] dataSet = inputData[i].split("/");
    		        //if (dataSet.length != 2) continue;
    		        int portId;
    		        try {
    		            portId = Integer.parseInt(dataSet[0]);
    		        }
    		        catch (NumberFormatException e) {
    		            System.out.println("port id is parsing wrong at " + dataSet[0]);
    		            isAllOK = false;
    		            continue;
    		        }
    		        
    		        String portAliasName = dataSet[1].equals(" ") ? portAliasName = "" : dataSet[1];
    		        

    		        isAllOK &= network.setInfAliasName(deviceIp, portId, portAliasName);
    		        System.out.println("portId:" + portId + ", pAliasName:" + portAliasName);
    		    }
    		}
    		else {
    		    System.out.println("input data is wrong.");
    		    isAllOK = false;
    		}
    		
    		try {
                response.getWriter().write(isAllOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("mailFilter_set")) {
    		String[] inputData = request.getParameterValues("mailFilterArray[]");
    		boolean isAllOK = true;
    		
    		if (inputData != null && inputData.length > 1) {
    		    String deviceIp = inputData[0];
    		    
    		    System.out.println("device " + deviceIp + ":");
    		    for (int i = 1; i < inputData.length; i++) {
    		        String[] dataSet = inputData[i].split("/");
    		        if (dataSet.length != 3) continue;
    		        int portId;
    		        try {
    		            portId = Integer.parseInt(dataSet[0]);
    		        }
    		        catch (NumberFormatException e) {
    		            System.out.println("port id is parsing wrong at " + dataSet[0]);
    		            isAllOK = false;
    		            continue;
    		        }
    		        boolean isMailFilter = dataSet[1].equals("1") ? true : false;
    		        String profileName = dataSet[2].equals("none") ? "" : dataSet[2];
    		        
    		        isAllOK &= network.setInfProfile(deviceIp, portId, isMailFilter, profileName);
    		        System.out.println("portId:" + portId + ", isMFPow:" + isMailFilter + ", pProfileName:" + profileName);
    		    }
    		}
    		else {
    		    System.out.println("input data is wrong.");
    		    isAllOK = false;
    		}
    		
    		try {
                response.getWriter().write(isAllOK ? "Success" : "Failed");
            }
            catch (IOException e) {
            }
    	}
    	else if (method.equals("acTrap_view")) {
    		String acPhyAddr = request.getParameter("mac");
    		
    		if(acPhyAddr != null){
    			String[][] result = JNetwork.viewNotify(acPhyAddr);
    			String[][] result1 = JNetwork.viewNotifyAc(acPhyAddr);
    			if(result != null && result1 != null){
    				request.setAttribute("apJoinMailCheck", result[0][0]);
    	    		request.setAttribute("apJoinMail", result[0][1]);
    	    		request.setAttribute("apJoinSmsCheck", result[0][2]);
    	    		request.setAttribute("apJoinSms", result[0][3]);
    	    		request.setAttribute("apLeaveMailCheck", result[0][4]);
    	    		request.setAttribute("apLeaveMail", result[0][5]);
    	    		request.setAttribute("apLeaveSmsCheck", result[0][6]);
    	    		request.setAttribute("apLeaveSms", result[0][7]);
    	    		request.setAttribute("acWarmStartMailCheck", result1[0][0]);
    	    		request.setAttribute("acWarmStartMail", result1[0][1]);
    	    		request.setAttribute("acWarmStartSmsCheck", result1[0][2]);
    	    		request.setAttribute("acWarmStartSms", result1[0][3]);
    	    		request.setAttribute("acColdStartMailCheck", result1[0][4]);
    	    		request.setAttribute("acColdStartMail", result1[0][5]);
    	    		request.setAttribute("acColdStartSmsCheck", result1[0][6]);
    	    		request.setAttribute("acColdStartSms", result1[0][7]);
    			}
    			else{
    				System.out.println("Get data is failed.");
    			}
    			
    		}
    		else {
    			System.out.println("The ac ip is null. Get data is failed.");
    		}
    		
    		
    	}
    	else if (method.equals("acTrap_set")) {
    		String acIp = request.getParameter("acIp");
    		String acPhyAddr = request.getParameter("acPhyAddr");
    		
    		String apJoinMailCheck = request.getParameter("apJoinMailCheck");
    		String apJoinMail = request.getParameter("apJoinMail");
    		String apJoinSmsCheck = request.getParameter("apJoinSmsCheck");
    		String apJoinSms = request.getParameter("apJoinSms");
    		
    		String apLeaveMailCheck = request.getParameter("apLeaveMailCheck");
    		String apLeaveMail = request.getParameter("apLeaveMail");
    		String apLeaveSmsCheck = request.getParameter("apLeaveSmsCheck");
    		String apLeaveSms = request.getParameter("apLeaveSms");
    		
    		String acWarmStartMailCheck = request.getParameter("acWarmStartMailCheck");
    		String acWarmStartMail = request.getParameter("acWarmStartMail");
    		String acWarmStartSmsCheck = request.getParameter("acWarmStartSmsCheck");
    		String acWarmStartSms = request.getParameter("acWarmStartSms");
    		
    		String acColdStartMailCheck = request.getParameter("acColdStartMailCheck");
    		String acColdStartMail = request.getParameter("acColdStartMail");
    		String acColdStartSmsCheck = request.getParameter("acColdStartSmsCheck");
    		String acColdStartSms = request.getParameter("acColdStartSms");
    		
    		
    		System.out.println("acPhyAddr="+acPhyAddr+" acIp="+acIp+
    				" apJoinMailCheck="+apJoinMailCheck+" apJoinMail="+apJoinMail+" apJoinSmsCheck="+apJoinSmsCheck+" apJoinSms="+apJoinSms+
    				" apLeaveMailCheck="+apLeaveMailCheck+" apLeaveMail="+apLeaveMail+" apLeaveSmsCheck="+apLeaveSmsCheck+" apLeaveSms="+apLeaveSms+
    				" acWarmStartMailCheck="+acWarmStartMailCheck+" acWarmStartMail="+acWarmStartMail+" acWarmStartSmsCheck="+acWarmStartSmsCheck+" acWarmStartSms="+acWarmStartSms+
    				" acColdStartMailCheck="+acColdStartMailCheck+" acColdStartMail="+acColdStartMail+" acColdStartSmsCheck="+acColdStartSmsCheck+" acColdStartSms="+acColdStartSms);
    		
    		if(acIp != null){
    			boolean result = JNetwork.checkNotify(acPhyAddr, acIp, apJoinMailCheck.equals("1") ? "1" : "0", apJoinMail, 
    					apJoinSmsCheck.equals("1") ? "1" : "0", apJoinSms, 
    					apLeaveMailCheck.equals("1") ? "1" : "0", apLeaveMail,
    					apLeaveSmsCheck.equals("1") ? "1" : "0", apLeaveSms,
    					acWarmStartMailCheck.equals("1") ? "1" : "0", acWarmStartMail,
    					acWarmStartSmsCheck.equals("1") ? "1" : "0", acWarmStartSms,
    	    			acColdStartMailCheck.equals("1") ? "1" : "0", acColdStartMail,
    	    	    	acColdStartSmsCheck.equals("1") ? "1" : "0", acColdStartSms);
                try {
                    response.getWriter().write(result ? "Success" : "Failed");
                }
                catch (IOException e) {
                }
    		}
    		else {
    			System.out.println("The ac ip is null. Add/Update data is failed.");
    		}
    		
    		
    	}
    	else if (method.equals("single_ip_set")) {
            String ip = request.getParameter("ip");
            System.out.println("IP="+ip);
            List<JDevice> deviceList = network.getDeviceList();
            String oid ="";
            String isViaSwitch = "";
            
            for(JDevice deviceItem : deviceList){
            	if(deviceItem.getPublicIp().equals(ip)){
            		oid = deviceItem.getSysObjectId();
            	}
            }
            
            if(oid.equals("1.3.6.1.4.1.3742.10.5103.11") || oid.equals("1.3.6.1.4.1.259.10.1.5")){
            	isViaSwitch = "y";
            }
            else {
            	isViaSwitch = "n";
            }
            
            /*String singleIpInUse = (String) getServletContext().getAttribute("singleIpInUse");
            if (singleIpInUse != null && singleIpInUse.equals("yes")) {
                System.out.println("Single IP is in used, exit.");
                try {
                    response.sendRedirect("default.jsp");
                }
                catch (IOException e) {
                }
                return;
            }*/
            if (ip == null || ip.isEmpty()) {
                System.out.println("Input Ip used for single IP is not correct.");
                return;
            }
            
            try {
                String singleIpScript = JSystem.projectSpace + "/set_single_ip_script";
                String sed1 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    sed1 = "/bin/sed -i \"11c send \\\"iptables -t nat -A PREROUTING -p tcp -d "+JSystem.ethAddress+"/32 --dport "+Config.getRemoteWebPort()+" -j DNAT --to-destination "+ip+":80\r\\\"\" " + singleIpScript;
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    sed1 = "/bin/sed -i \"3c iptables -t nat -A PREROUTING -p tcp -d "+JSystem.ethAddress+"/32 --dport "+Config.getRemoteWebPort()+" -j DNAT --to-destination "+ip+":80\" " + singleIpScript;
                }
                String[] cmd_set_interfaces = { "/bin/sh", "-c", sed1 };
                String line = "";
                Process Process_set_interfaces = Runtime.getRuntime().exec(cmd_set_interfaces);
                BufferedReader BufferedReader_set_interfaces = new BufferedReader(new InputStreamReader(Process_set_interfaces.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces.readLine()) != null) {
                }
                BufferedReader_set_interfaces.close();
                Process_set_interfaces.waitFor();
                
                String sed2 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    sed2 = "/bin/sed -i \"13c send \\\"iptables -t nat -A POSTROUTING -p tcp -d "+ip+" --dport 80 -j SNAT --to-source "+JSystem.ethAddress+"\r\\\"\" " + singleIpScript;
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    sed2 = "/bin/sed -i \"4c iptables -t nat -A POSTROUTING -p tcp -d "+ip+" --dport 80 -j SNAT --to-source "+JSystem.ethAddress+"\" " + singleIpScript;
                }
                String[] cmd_set_interfaces2 = { "/bin/sh", "-c", sed2 };
                Process Process_set_interfaces2 = Runtime.getRuntime().exec(cmd_set_interfaces2);
                BufferedReader BufferedReader_set_interfaces2 = new BufferedReader(new InputStreamReader(Process_set_interfaces2.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces2.readLine()) != null) {
                }
                BufferedReader_set_interfaces2.close();
                Process_set_interfaces2.waitFor();
                
                String sed3 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                }
                else {
                    sed3 = "/bin/sed -i \"6c iptables -A FORWARD -p tcp -d "+ip+" --dport 80 -j ACCEPT\" " + singleIpScript;
                }
                String[] cmd_set_interfaces3 = { "/bin/sh", "-c", sed3 };
                Process Process_set_interfaces3 = Runtime.getRuntime().exec(cmd_set_interfaces3);
                BufferedReader BufferedReader_set_interfaces3 = new BufferedReader(new InputStreamReader(Process_set_interfaces3.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces3.readLine()) != null) {
                }
                BufferedReader_set_interfaces3.close();
                Process_set_interfaces3.waitFor();
                
                String sed4 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                }
                else {
                    sed4 = "/bin/sed -i \"7c iptables -A FORWARD -p tcp -s "+ip+" -j ACCEPT\" " + singleIpScript;
                }
                String[] cmd_set_interfaces4 = { "/bin/sh", "-c", sed4 };
                Process Process_set_interfaces4 = Runtime.getRuntime().exec(cmd_set_interfaces4);
                BufferedReader BufferedReader_set_interfaces4 = new BufferedReader(new InputStreamReader(Process_set_interfaces4.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces4.readLine()) != null) {
                }
                BufferedReader_set_interfaces4.close();
                Process_set_interfaces4.waitFor();
                
                String[] cmd_restart_interfaces = { "/bin/sh", "-c", singleIpScript };
                Process Process_restart_interfaces = Runtime.getRuntime().exec(cmd_restart_interfaces);
                BufferedReader BufferedReader_restart_interfaces = new BufferedReader(new InputStreamReader(Process_restart_interfaces.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_restart_interfaces.readLine()) != null) {
                }
                BufferedReader_restart_interfaces.close();
                Process_restart_interfaces.waitFor();
                //getServletContext().setAttribute("singleIpInUse", "yes");
                
                String url = request.getHeader("referer");
                String[] splitUrl = url.split("/"); // First split [http, localhost or ip:8080...] 
                String[] extranetIP = splitUrl[2].split(":"); // Second split [localhost or ip, 8080....]
                System.out.println("spiltIP="+extranetIP[0]);
                
                String result = "Extranet" + "," + isViaSwitch + "," + extranetIP[0] + ":" + Config.getRemoteWebPort() + "," + extranetIP[0];
                response.getWriter().write(result);
            }
            catch (Exception e) {
                try {
                	String result = "LAN" + "," + isViaSwitch;
					response.getWriter().write(result);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
    	}
    	else if (method.equals("single_ip_delete")) {
            if (!System.getProperty("os.name").startsWith("Windows")){
            try {
                String singleIpScript = JSystem.projectSpace + "/set_single_ip_script";
                String sed1 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    sed1 = "/bin/sed -i \"11c send \\\"iptables -t nat -L\r\\\"\" " + singleIpScript;
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    sed1 = "/bin/sed -i \"3c echo placeholder for iptables nat\" " + singleIpScript;
                }
                String[] cmd_set_interfaces = { "/bin/sh", "-c", sed1 };
                String line = "";
                Process Process_set_interfaces = Runtime.getRuntime().exec(cmd_set_interfaces);
                BufferedReader BufferedReader_set_interfaces = new BufferedReader(new InputStreamReader(Process_set_interfaces.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces.readLine()) != null) {
                }
                BufferedReader_set_interfaces.close();
                Process_set_interfaces.waitFor();
                
                String sed2 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    sed2 = "/bin/sed -i \"13c send \\\"iptables -t nat -L\r\\\"\" " + singleIpScript;
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    sed2 = "/bin/sed -i \"4c echo placeholder for iptables nat\" " + singleIpScript;
                }
                String[] cmd_set_interfaces2 = { "/bin/sh", "-c", sed2 };
                Process Process_set_interfaces2 = Runtime.getRuntime().exec(cmd_set_interfaces2);
                BufferedReader BufferedReader_set_interfaces2 = new BufferedReader(new InputStreamReader(Process_set_interfaces2.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces2.readLine()) != null) {
                }
                BufferedReader_set_interfaces2.close();
                Process_set_interfaces2.waitFor();
                
                String sed3 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    sed3 = "/bin/sed -i \"6c echo placeholder for iptables filter\" " + singleIpScript;
                }
                String[] cmd_set_interfaces3 = { "/bin/sh", "-c", sed3 };
                Process Process_set_interfaces3 = Runtime.getRuntime().exec(cmd_set_interfaces3);
                BufferedReader BufferedReader_set_interfaces3 = new BufferedReader(new InputStreamReader(Process_set_interfaces3.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces3.readLine()) != null) {
                }
                BufferedReader_set_interfaces3.close();
                Process_set_interfaces3.waitFor();
                
                String sed4 = "";
                if (JSystem.osDistribution.equals("Ubuntu")) {
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    sed4 = "/bin/sed -i \"7c echo placeholder for iptables filter\" " + singleIpScript;
                }
                String[] cmd_set_interfaces4 = { "/bin/sh", "-c", sed4 };
                Process Process_set_interfaces4 = Runtime.getRuntime().exec(cmd_set_interfaces4);
                BufferedReader BufferedReader_set_interfaces4 = new BufferedReader(new InputStreamReader(Process_set_interfaces4.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_set_interfaces4.readLine()) != null) {
                }
                BufferedReader_set_interfaces4.close();
                Process_set_interfaces4.waitFor();
                
                String[] cmd_restart_interfaces = { "/bin/sh", "-c", singleIpScript };
                Process Process_restart_interfaces = Runtime.getRuntime().exec(cmd_restart_interfaces);
                BufferedReader BufferedReader_restart_interfaces = new BufferedReader(new InputStreamReader(Process_restart_interfaces.getInputStream(), "UTF-8"));
                while ((line = BufferedReader_restart_interfaces.readLine()) != null) {
                }
                BufferedReader_restart_interfaces.close();
                Process_restart_interfaces.waitFor();
                //getServletContext().setAttribute("singleIpInUse", "yes");
                
            }
            catch (Exception e) {
				e.printStackTrace();
				
            }
            
            }
            else {
            	System.out.println("The windows can't run sinple ip delete function.");
            }
    	}
    	else if (method.equals("devicelist_export")) {
			System.out.println("method=devicelist_export");

			boolean result = Config.exportDeviceListToFile();
			if (result) {
				System.out.println("Export devicelist to " + Config.getExportDeviceListFileAbsolutePath());
			} else {
				System.out.println("Export devicelist FAILED.");
			}

			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("isDone", result);
			map.put("file", Config.getExportDeviceListFileRelativePath());

			sendOut(method, response, map, true);
		}
    	else if (method.equals("devicelist_import")) {
			System.out.println("method=devicelist_import");

			Map<String, Object> result = new LinkedHashMap<String, Object>();

			if (network.isScanIpRunning()) {
				System.out.println("Scan has already in running.\nPlease wait until standby.");

				result.put("isDone", new Boolean(false));
				result.put("text", "Scan has already in running.\nPlease wait until standby.");
			} else {
				result = importSettings(request);
			}

			sendOut(method, response, result, true);
		}
    	else {
    		System.out.println("method???");
    	}
    }
	
	private void sendOut(String method, HttpServletResponse response, Object obj, boolean isJson) {
	    String output = "";
	    
	    if (isJson) {
	        output = new Gson().toJson(obj);
	        response.setContentType("application/json");
	    }
	    else {
	        output = (String) obj;
	    }
	    
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(output);
        }
        catch (IOException e) {
            System.out.println("method: " + method + " send out failed.");
        }
	}
	
	private Map<String, Object> importSettings(HttpServletRequest request) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("fileName", "");
		result.put("fileSize", new Long(0));
		result.put("isDone", new Boolean(false));
		result.put("text", "");
		
		File uploadedFile;
        try {
            uploadedFile = receiveFile(request);
            result.put("fileName", uploadedFile.getName());
            result.put("fileSize", new Long(uploadedFile.length()));
        }
        catch (Exception e) {
        	result.put("text", "Upload failed.");
        	System.out.println("Upload failed. " + e.getMessage());
        	return result;
        }
        
        if (ImportFromDeviceListFile(uploadedFile.getAbsolutePath())) {
			System.out.println("Import device list success.");

			result.put("isDone", new Boolean(true));
			result.put("text", "Import device list success.");
			
			uploadedFile.delete();
		} else {
			System.out.println("Import device list failed.");

			result.put("isDone", new Boolean(false));
			result.put("text", "Import device list failed.");
			
			uploadedFile.delete();
		}

		return result;
	}
	
	private File receiveFile(HttpServletRequest request) throws Exception {
	    Part part = null;
	    try {
            part = request.getPart("file");     // uploading process will hang on this statement
            if (part == null) {
                System.out.println("upload file can not be got.");
                throw new Exception("File transfered error!");
            }
        }
        catch (IllegalStateException | IOException e) {
            System.out.println("upload file expcetion when getPart");
            throw new Exception("File transfered error!");
        }
        
        File file = new File(JSystem.projectSpace + File.separator + getFileName(part));
        try {
            part.write(file.getAbsolutePath());
        }
        catch (IOException e) {
            System.out.println("upload file save failed.");
            throw new Exception("File transfered error!");
        }
        System.out.println("Uploaded file name: " + file.getName() + ", size: " + file.length() + ", saved to " + file.getAbsolutePath());
        
        return file;
	}
	
	private String getFileName(Part part) {
	    String header = part.getHeader("Content-Disposition");
	    String fileName = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));
	    return fileName;
	}
	
	private boolean ImportFromDeviceListFile(String sourceFile) {
		//System.out.println(sourceFile);

		boolean result = false;

		CsvReader products = null;
		try {
			products = new CsvReader(new InputStreamReader(new FileInputStream(sourceFile), "UTF-8"));

			List<List<String>> Columns = new ArrayList<List<String>>();

			products.readHeaders();
			
			List<String> rowNames = new ArrayList<String>();
			if(products.readRecord()){
				rowNames = Arrays.asList(products.getValues());
			}

			while (products.readRecord()) {
				List<String> Column = new ArrayList<String>();
				Column = Arrays.asList(products.getValues());
				List<String> newColumn = new ArrayList<String>(Column);
				newColumn.add("2000");
				newColumn.add("");
				Columns.add(newColumn);
			}
			products.close();

			//System.out.println(rowNames.size());
			if (Columns.size() == 0 || rowNames.size() < 5 || !(rowNames.get(0).equals("* IP Address") && rowNames.get(1).equals("Alias Name") && rowNames.get(2).equals("SNMP Port") && rowNames.get(3).equals("SNMP Version") && rowNames.get(4).equals("SNMP Community"))) {
				System.out.println("Checking import file: Wrong file content.");
				
				result = false;
			} else {
				/*for (int i = 0; i < Columns.size(); i++) {
					System.out.println(Columns.get(i).get(0) + "," + Columns.get(i).get(1) + "," + Columns.get(i).get(2) + "," + Columns.get(i).get(3) + "," + Columns.get(i).get(4) + "," + Columns.get(i).get(5) + "," + Columns.get(i).get(6));
				}*/
				
				result = network.startScanIpThread(Columns);		
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
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
