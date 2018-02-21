package com.via.controller;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.via.model.*;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;
import com.via.system.Config;

/**
 * Servlet implementation class Topology
 */
@WebServlet("/Topology")
public class Topology extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Topology() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	JNetwork network = (JNetwork) getServletContext().getAttribute("network");
    	String action = request.getParameter("action");
    	
    	if (action == null) {
    	    System.out.println("no action.");
    	}
    	else if (action.equals("tree_view")) {
    	    System.out.println("tree_view");
    	    String selectIp = request.getParameter("select_ip");
    	    String displayType = request.getParameter("display_type");
    	    if (selectIp == null || selectIp.isEmpty()) selectIp = Config.getTopologyTopDevice();
    	    if (displayType == null || displayType.isEmpty()) displayType = "ip_name";
    	    System.out.println("select_ip: " + selectIp + ", display_type: " + displayType);
    	    
    	    Object userLevel = request.getSession().getAttribute("userLevel");
    	    String userCheck = String.valueOf(userLevel);
    	    
    	    ArrayList<String[]> topologyTree = network.getTopologyTree(selectIp, displayType, userCheck);
    	    request.setAttribute("topologyTree", topologyTree);
    	    String treeIpSelect = "";
	    	    for(String[] treeIp :topologyTree){
	    	    	treeIpSelect+=treeIp[3]+",";
	    	    }
		    request.setAttribute("treeIpSelect", treeIpSelect.split(","));
		    if(!topologyTree.isEmpty()){
		    	request.setAttribute("treeScript", topologyTree.get(0)[1]);
		    }

    	    /*request.setAttribute("treeHTML", topologyTree[0]);
    	    request.setAttribute("treeScript", topologyTree[1]);
    	    request.setAttribute("treeRoot", topologyTree[2]);
    	    request.setAttribute("treeIpSelect", topologyTree[3].split(","));*/
    	}
    	else if (action.equals("remote_tree_view")) {
    		Map<String, ArrayList<String[]>> remoteTopologyTree = network.getRemoteTopologyTree();
    		
    		if(remoteTopologyTree != null){
    			request.setAttribute("remoteTopologyTree", remoteTopologyTree);
    		}
    	}
    	else if (action.equals("trace_path")) {
    	    System.out.println("trace_path");
    	    String[] data = request.getParameterValues("scanip[]");        //get selected ip array
    	    List<String[][]> pathList = null;
    	    
    	    if (data != null && data.length == 2) {
    	        String startAddr = data[0];
    	        String endAddr = data[1];
    	        System.out.println("startAddr: " + startAddr + ", endAddr: " + endAddr);
    	        pathList = network.getTopologyPath(startAddr, endAddr);
    	    }
    	    
    	    if (pathList == null) {
    	        pathList = Collections.emptyList();
    	    }
//    	    for (String[][] pair : pathList)
//    	        System.out.println("Trace Path: " + Arrays.toString(pair));
    	    
    	    String json_data = new Gson().toJson(pathList);                //send list to jsp
    	    response.setContentType("application/json");
    	    response.setCharacterEncoding("UTF-8");
    	    try {
    	        response.getWriter().write(json_data);
    	    } catch (IOException e) {
    	        System.out.println(e.getMessage());
    	    }
    	} else if (action.equals("path_status")) {
			System.out.println("path_status");

			String message = "";

			String src = request.getParameter("src");
			//System.out.println(src);
			String dst = request.getParameter("dst");
			//System.out.println(dst);

			String startAddr = "";

			if (src == null || src.equals("")) {
				for (Map.Entry<String, List<String>> entry : JSystem.networkInterfaceMap.entrySet()) {
					String infName = entry.getKey();
					if (infName.startsWith("eth") || infName.startsWith("net")) { // TODO find out if wireless always use 'net' in interface name
						for (String addr : entry.getValue()) {
							if (JTools.validIpv4Addr(addr)) { // will only get the first valid address
								for (JDevice device : network.getDeviceList()) {
									if (addr.equals(device.getPublicIp())) {
										startAddr = addr;
									}
								}
							}
						}
					}
				}

				//System.out.println(startAddr);
				if (startAddr.equals("")) {
					message = "Please add NMS to device list and check topology map.";
				}
			} else {
				startAddr = src;
			}

			List<String[][]> pathList = null;
			Map<String, List<String>> PathStatus = new LinkedHashMap<String, List<String>>();

			int srcCount = 0;
			int dstCount = 0;
			if (!(dst == null) || !dst.equals("")) {
				if (!dst.equals("") && !startAddr.equals("") && !startAddr.equals(dst)) {
					String endAddr = dst;
					System.out.println("startAddr: " + startAddr + ", endAddr: " + endAddr);

					pathList = network.getTopologyPath(startAddr, endAddr);

					if (pathList != null) {

						String tmpIP = "";
						String tmpPort = "";

						for (String[][] path : pathList) {
							for (int i = 0; i < path.length; i++) {
								if (path[i][0].equals(startAddr)) {
									srcCount++;
								}

								if (path[i][0].equals(endAddr)) {
									dstCount++;
								}

								List<String> tmpList = new ArrayList<String>();

								tmpList.add(path[i][0]);
								tmpList.add(path[i][2]);
								tmpList.add(Integer.toString(network.findDeviceByIp(path[i][0]).isAlive()));

								if (tmpIP.equals(path[i][0])) {
									tmpList.add(tmpPort);
									tmpList.add(path[i][1]);

									PathStatus.put(path[i][0], tmpList);

									tmpIP = "";
									tmpPort = "";
								} else {
									tmpIP = path[i][0];
									tmpPort = path[i][1];

									tmpList.add(path[i][1]);
									tmpList.add("");

									PathStatus.put(path[i][0], tmpList);
								}
							}
						}
					}
				}
			}

			ArrayList<String[]> path = new ArrayList<String[]>();

			for (Map.Entry<String, List<String>> entry : PathStatus.entrySet()) {
				//System.out.println(entry.getKey() + ", " + entry.getValue());

				String[] tmp = new String[entry.getValue().size()];
				tmp = entry.getValue().toArray(tmp);

				path.add(tmp);
			}

			JNode node = null;
			if (path.size() > 0) {
				JNode tmpnode = null;
				for (int i = path.size() - 1; i >= 0; i--) {
					node = new JNode(path.get(i)[0]);

					if (path.get(i)[1].equals("l2switch")) {
						node.setType("L2 Switch");
						node.setIcon("images/switch_layer2.png");
					} else if (path.get(i)[1].equals("l3switch")) {
						node.setType("L3 Switch");
						node.setIcon("images/switch_layer3.png");
					} else if (path.get(i)[1].equals("wlanAC")) {
						node.setType("AC");
						node.setIcon("images/wlan_ac.png");
					} else if (path.get(i)[1].equals("wlanAP")) {
						node.setType("AP");
						node.setIcon("images/wlan_ap.png");
					} else if (path.get(i)[1].equals("firewall")) {
						node.setType("Firewall");
						node.setIcon("images/firewall.png");
					} else if (path.get(i)[1].equals("server")) {
						node.setType("Server");
						node.setIcon("images/server.png");
					} else if (path.get(i)[1].equals("pc")) {
						node.setType("PC");
						node.setIcon("images/desktop.png");
					} else if (path.get(i)[1].equals("internet")) {
						node.setType("Internet");
						node.setIcon("images/cloud.png");
					} else if (path.get(i)[1].equals("NMS")) {
						node.setType("NMS");
						node.setIcon("images/nms.png");
					} else if (path.get(i)[1].equals("MGVChiefServer")) {
						node.setType("MGV Chief Server");
						node.setIcon("images/mgv_chief_server.png");
					} else if (path.get(i)[1].equals("MGVCommandServer")) {
						node.setType("MGV Command Server");
						node.setIcon("images/mgv_command_server.png");
					} else if (path.get(i)[1].equals("MGVPlayer")) {
						node.setType("MGV Player");
						node.setIcon("images/mgv_player.png");
					}

					node.setInport(path.get(i)[3]);
					node.setOutport(path.get(i)[4]);
					if (path.get(i)[1].indexOf("MGV") != -1) {
						node.setInport("1");
						node.setOutport("1");
					}
					if (i == 0) {
						node.setInport("");
					}
					if (i == (path.size() - 1)) {
						node.setOutport("");
					}

					if (path.get(i)[2].equals("0")) {
						node.setStrokecolor("#FF0000");
					} else if (path.get(i)[2].equals("1")) {
						node.setStrokecolor("#00FF00");
					} else if (path.get(i)[2].equals("2")) {
						node.setStrokecolor("#FFCC00");
					}

					if (tmpnode != null) {
						node.addChildren(tmpnode);
					}

					tmpnode = node;
				}
			}

			if (node == null) {
				node = new JNode(message);
			}

			//System.out.println(srcCount);
			//System.out.println(dstCount);
			if (message.equals("") && (path.size() == 0/* || srcCount == 0 || dstCount == 0*/)) {
				node = new JNode("Please check topology map.");
			}

			String json_data = new Gson().toJson(node);
			request.setAttribute("json_data", json_data);
		} else if (action.equals("refresh")) {
    		System.out.println("refresh");
    		boolean isOk = false;
    		//if(network.isTopologyRunning() == false){
    			isOk = network.updateTopologyInfo();
    		//}
    		
    		try {
    	        response.getWriter().write(isOk ? "Success" : "Failed");
    	    } catch (IOException e) {
    	        System.out.println(e.getMessage());
    	    }
    	}
    	else if (action.equals("top_device_set")) {
    		String selectIp = request.getParameter("select_ip");
    		System.out.println("selectIp="+selectIp);
    		Config.setTopologyTopDevice((selectIp == null ? "" : selectIp));
    	}
    	else if (action.equals("top_device_get")) {
    		String topDevice = Config.getTopologyTopDevice();
			request.setAttribute("topDevice", topDevice);
    	}
    	else if (action.equals("nms_tree_view")) {
    		String ip = request.getParameter("ip");
    		if (!ip.equals("")) {
	    		RemoteInterface Service = RemoteService.creatClient(ip, 3);
	    		if(Service != null) {
	    			ArrayList<String[]> topologyTree = new ArrayList<String[]>();
	    			try {
	    				topologyTree = Service.getTopology();
	    			} catch (RemoteException e) {
	    				e.printStackTrace();
	    			}
	    			
	    			if(topologyTree.size() > 0) {
	    				request.setAttribute("topologyTree", topologyTree);
	    			}
	    		}
    		}
    		
    	}
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
