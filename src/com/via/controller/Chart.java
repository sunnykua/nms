package com.via.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JNetwork;
/**
 * Servlet implementation class PortStatistics
 */
@WebServlet("/Chart")
public class Chart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Chart() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	JNetwork network = (JNetwork) getServletContext().getAttribute("network");
    	String chart = request.getParameter("chart");
		String[] data = request.getParameterValues("portselect[]");
		String ip = request.getParameter("ip");
		String port = request.getParameter("port");
		String action = request.getParameter("action");
		String updateInterval = request.getParameter("updateinterval");
		String output = "0";
		String acip = request.getParameter("acip");
		String apip = request.getParameter("apip");
		String apssidindex = request.getParameter("apssidindex");
		String apssidname = request.getParameter("apssidname");
		
		if (chart == null) {
			System.out.println("chart parameter is missing.");
		}
		else if (chart.equals("chart1")) {
			output = "0,0,0";

    	 	if (action != null && action.equals("clear")) {
    	 		System.out.println("clear chart1.");
    	 		request.getSession().removeAttribute("chart1Data");
    	 	}
    	 	else if (data != null && data.length == 2) {
    	 		String devIp = data[0];
    	 		String devPort = data[1];
    	 		//System.out.println("devIp: " + devIp + ", devPort: " + devPort);
    	 		boolean toDiscard = false;
    	 		
    	 		int[] currData = network.chart1(devIp, Integer.parseInt(devPort));		// be careful
    	 		int diffUnicast, diffMulticast, diffBroadcast;
    	 		float rateUnicast = 0, rateMulticast = 0, rateBroadcast = 0;
    	 		int[] prevData = (int[]) request.getSession().getAttribute("chart1Data");
    	 		int interval = 0;

    	 		if (prevData == null) {
    	 			System.out.println("The first time to read for Chart.");
    	 			diffUnicast = diffMulticast = diffBroadcast = 0;
    	 			rateUnicast = rateMulticast = rateBroadcast = 0;
    	 		}
    	 		else {
    	 			diffUnicast = currData[0] - prevData[0];
    	 			diffMulticast = currData[1] - prevData[1];
    	 			diffBroadcast = currData[2] - prevData[2];
    	 			if (diffUnicast < 0 || diffMulticast < 0 || diffBroadcast < 0) {
    	 				System.out.println("diff is less than 0.");
    	 				toDiscard = true;
    	 				diffUnicast = diffMulticast = diffBroadcast = 0;
    	 			}
    	 			
    	 			try {
						interval = Integer.parseInt(updateInterval);
						
						if (interval > 0) {
							rateUnicast = (float)diffUnicast / (float)interval;
							rateMulticast = (float)diffMulticast / (float)interval;
							rateBroadcast = (float)diffBroadcast / (float)interval;
						}
					} catch (NumberFormatException e) {
						
					}
    	 			
    	 		}

    	 		//output = String.format("%d,%d,%d", diffUnicast, diffMulticast, diffBroadcast);
    	 		output = String.format("%f,%f,%f", rateUnicast, rateMulticast, rateBroadcast);

    	 		if (!toDiscard) {
    	 			request.getSession().setAttribute("chart1Data", currData);
    	 		}
    	 		//System.out.println(devIp + ", " + devPort + " : " + currData[0] + " " + currData[1] + " " + currData[2]);
    	 		System.out.println(String.format("devIp:%s devPort:%s upInterval:%d u:%d(%d, %.3fbps) m:%d(%d, %.3fbps) b:%d(%d, %.3fbps)", devIp, devPort, interval,
    	 				currData[0], diffUnicast, rateUnicast, currData[1], diffMulticast, rateMulticast, currData[2], diffBroadcast, rateBroadcast));
    	 	}
    	 	else {
    	 		System.out.println("data array is null or size is wrong.");
    	 	}
    	}
		else if (chart.equals("chart2")) {
			output = "0,0";
			
			if (action != null && action.equals("clear")) {
    	 		System.out.println("clear chart2.");
    	 		request.getSession().removeAttribute("chart2Data");
    	 	}
			else if (data != null && data.length == 2) {
				String devIp = data[0];
    	 		String devPort = data[1];
    	 		//System.out.println("devIp: " + devIp + ", devPort: " + devPort);
    	 		boolean toDiscard = false;

    	 		int[] currData = network.chart2(devIp, Integer.parseInt(devPort));		// be careful
    	 		int diffRx, diffTx;
    	 		int[] prevData = (int[]) request.getSession().getAttribute("chart2Data");

    	 		if (prevData == null) {
    	 			System.out.println("The first time to read for Chart.");
    	 			diffRx = diffTx = 0;
    	 		}
    	 		else {
    	 			diffRx = currData[0] - prevData[0];
    	 			diffTx = currData[1] - prevData[1];

    	 			if (diffRx < 0 || diffTx < 0) {
    	 				System.out.println("diff is less than 0.");
    	 				toDiscard = true;
    	 				diffRx = diffTx = 0;
    	 			}
    	 		}

    	 		output = String.format("%d,%d", diffRx, diffTx);

    	 		if (!toDiscard) {
    	 			request.getSession().setAttribute("chart2Data", currData);
    	 		}
    	 		//System.out.println(devIp + ", " + devPort + " : " + currData[0] + " " + currData[1] + " " + currData[2]);
    	 		System.out.println(String.format("devIp:%s devPort:%s rx:%d(%d) tx:%d(%d)", devIp, devPort, currData[0], diffRx, currData[1], diffTx));
			}
			else {
    	 		System.out.println("data array is null or size is wrong.");
    	 	}
		}
		else if (chart.equals("rx_tx_octet_bps")) {
		    output = "0,0";
		    String sessionName = chart;
		    
		    if ((action == null || action.isEmpty()) || (ip == null || ip.isEmpty()) || (port == null || port.isEmpty())) {
		        System.out.println("some data missing, action:" + action + ", ip: " + ip + ", port: " + port);
		    }
		    else if (action.equals("clear")) {
		        String deviceId = ip + "," + port;
		        @SuppressWarnings("unchecked")
		        Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName);     // store rx and tx octet (32 bits, unsigned)

		        if (dataMap != null) {
		            dataMap.remove(deviceId);
		            System.out.println("rx_tx_octet_bps: data cleared, " + deviceId);
		        }
		        else {
		            dataMap = new LinkedHashMap<String, long[]>();     // if it does not ever request, create an empty map
		            System.out.println("rx_tx_octet_bps: create the map, " + deviceId);
		        }

		        request.getSession().setAttribute(sessionName, dataMap);
		    }
		    else if (action.equals("read")) {
		        String deviceId = ip + "," + port;
		        int portid = 0;
		        int interval = 0;
		        float rxKbps = 0, txKbps = 0;
		        try {
		            portid = Integer.parseInt(port);
		            interval = Integer.parseInt(updateInterval);
		        }
		        catch (NumberFormatException e) {
		            //System.out.println("rx_tx_octet_bps: port or interval parse to int failed, " + deviceId);   // may duplicated print
		        }
		        
		        if (portid > 0 && interval > 0) {
		            long[] currData = network.getRxTxOctet(ip, portid);          // get CURRENT data
		            
		            if (currData != null && currData.length == 2) {
		                @SuppressWarnings("unchecked")
		                Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName);
		                
		                if (dataMap != null) {
		                    long[] prevData = dataMap.get(deviceId);              // get PREVIOUS data

		                    if (prevData != null) {                               // null may be has not ever read for this device and port, or, is cleared.
		                        long diffRx = currData[0] - prevData[0];
		                        if (diffRx < 0) diffRx = ((long)Integer.MAX_VALUE * 2) - prevData[0] + currData[0];     // if diff is minus, use current data as diff
		                        rxKbps = (float) diffRx * 8 / (float) interval / 1024;
		                        
		                        long diffTx = currData[1] - prevData[1];
		                        if (diffTx < 0) diffTx = ((long)Integer.MAX_VALUE * 2) - prevData[1] + currData[1];
		                        txKbps = (float) diffTx * 8 / (float) interval / 1024;
		                    }
		                    else {
		                        System.out.println("rx_tx_octet_bps: no data for " + deviceId);
		                    }
		                }
		                else {
		                    dataMap = new LinkedHashMap<String, long[]>();        // has not ever read this chart
		                    System.out.println("rx_tx_octet_bps: has not ever read this chart, " + deviceId);
		                }

		                dataMap.put(deviceId, currData);                                    // replace old data
		                request.getSession().setAttribute(sessionName, dataMap);
		                //System.out.println("rx_tx_octet_bps: data calculate success, " + deviceId + ", rxKbps:" + rxKbps + ", txKbps: " + txKbps);
		            }
		            else {
		                // if current data fails to read, nothing will be stored and return 0 kbps
		                System.out.println("rx_tx_octet_bps: current data read error, " + deviceId);
		            }
		        }
		        else {
		            System.out.println("rx_tx_octet_bps: portid or interval is wrong, " + deviceId + ", int:" + interval);
		        }
		        
		        output = String.format("%f,%f", rxKbps, txKbps);
		    }
		}
		else if (chart.equals("aplist_rx_tx_octet_bps")) {
		    output = "0,0";
		    String sessionName = chart;
		    
		    if ((action == null || action.isEmpty()) || (acip == null || acip.isEmpty()) || (apip == null || apip.isEmpty())) {
		        System.out.println("some data missing, action:" + action + ", acip: " + acip + ", apip: " + apip);
		    }
		    else if (action.equals("clear")) {
		        String aplistId = acip + "," + apip;
		        @SuppressWarnings("unchecked")
		        Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName);     // store rx and tx octet (32 bits, unsigned)

		        if (dataMap != null) {
		            dataMap.remove(aplistId);
		            System.out.println("aplist_rx_tx_octet_bps: data cleared, " + aplistId);
		        }
		        else {
		            dataMap = new LinkedHashMap<String, long[]>();     // if it does not ever request, create an empty map
		            System.out.println("aplist_rx_tx_octet_bps: create the map, " + aplistId);
		        }

		        request.getSession().setAttribute(sessionName, dataMap);
		    }
		    else if (action.equals("read")) {
		        String aplistId = acip + "," + apip;
		        int interval = 0;
		        float rxKbps = 0, txKbps = 0;
		        try {
		            interval = Integer.parseInt(updateInterval);
		        }
		        catch (NumberFormatException e) {
		            System.out.println("aplist_rx_tx_octet_bps: port or interval parse to int failed, " + aplistId);   // may duplicated print
		        }
		        
		        if (interval > 0) {
		            long[] currData = network.getAplistRxTxOctet(acip, apip);          // get CURRENT data
		            
		            if (currData != null && currData.length == 2) {
		                @SuppressWarnings("unchecked")
		                Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName);
		                
		                if (dataMap != null) {
		                    long[] prevData = dataMap.get(aplistId);              // get PREVIOUS data

		                    if (prevData != null) {                               // null may be has not ever read for this device and port, or, is cleared.
		                        long diffRx = currData[0] - prevData[0];
		                        if (diffRx < 0) diffRx = ((long)Integer.MAX_VALUE * 2) - prevData[0] + currData[0];     // if diff is minus, use current data as diff
		                        rxKbps = (float) diffRx * 8 / (float) interval / 1024;
		                        
		                        long diffTx = currData[1] - prevData[1];
		                        if (diffTx < 0) diffTx = ((long)Integer.MAX_VALUE * 2) - prevData[1] + currData[1];
		                        txKbps = (float) diffTx * 8 / (float) interval / 1024;
//				    	 		System.out.println(String.format("aplistId:%s  curr_rx:%d curr_tx:%d diff_rx:%d diff_tx:%d rxKbps:%f txKbps:%f ", aplistId, currData[0], currData[1], diffRx, diffTx, rxKbps, txKbps));

		                    }
		                    else {
		                        System.out.println("aplist_rx_tx_octet_bps: no data for " + aplistId);
		                    }
		                }
		                else {
		                    dataMap = new LinkedHashMap<String, long[]>();        // has not ever read this chart
		                    System.out.println("aplist_rx_tx_octet_bps: has not ever read this chart, " + aplistId);
		                }

		                dataMap.put(aplistId, currData);                                    // replace old data
		                request.getSession().setAttribute(sessionName, dataMap);
		            }
		            else {
		                // if current data fails to read, nothing will be stored and return 0 kbps
		                System.out.println("aplist_rx_tx_octet_bps: current data read error, " + aplistId);
		            }
		        }
		        else {
		            System.out.println("aplist_rx_tx_octet_bps: portid or interval is wrong, " + aplistId + ", int:" + interval);
		        }
		        
		        output = String.format("%f,%f", rxKbps, txKbps);

		    }
		}
		else if (chart.equals("apssidlist_rx_tx_octet_bps")) {
		    output = "0,0";
		    String sessionName = chart;
		    
		    if ((action == null || action.isEmpty()) || (acip == null || acip.isEmpty()) || (apip == null || apip.isEmpty()) || (apssidindex == null || apssidindex.isEmpty()) || (apssidname == null || apssidname.isEmpty()) ) {
		        System.out.println("some data missing, action:" + action + ", acip: " + acip + ", apip: " + apip + ", apssidindex: " + apssidindex  + ", apssidname: " + apssidname);
		    }
		    else if (action.equals("clear")) {
		        String apssidlistId = acip + "," + apip + "," + apssidindex + "," + apssidname;
		        @SuppressWarnings("unchecked")
		        Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName);     // store rx and tx octet (32 bits, unsigned)

		        if (dataMap != null) {
		            dataMap.remove(apssidlistId);
		            System.out.println("apssidlist_rx_tx_octet_bps: data cleared, " + apssidlistId);
		        }
		        else {
		            dataMap = new LinkedHashMap<String, long[]>();     // if it does not ever request, create an empty map
		            System.out.println("apssidlist_rx_tx_octet_bps: create the map, " + apssidlistId);
		        }

		        request.getSession().setAttribute(sessionName, dataMap);
		    }
		    else if (action.equals("read")) {
		        String apssidlistId = acip + "," + apip + "," + apssidindex + "," + apssidname;
		        int ssidindexid = 0;
		        int interval = 0;
		        float rxKbps = 0, txKbps = 0;
		        try {
		            ssidindexid = Integer.parseInt(apssidindex);
		            interval = Integer.parseInt(updateInterval);
		        }
		        catch (NumberFormatException e) {
		            System.out.println("apssidlist_rx_tx_octet_bps: apssidindex or interval parse to int failed, " + apssidlistId);   // may duplicated print
		        }
		        
		        if (interval > 0) {
		            long[] currData = network.getApSsidlistRxTxOctet(acip, apip, ssidindexid);          // get CURRENT data
		            
		            if (currData != null && currData.length == 2) {
		                @SuppressWarnings("unchecked")
		                Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName);
		                
		                if (dataMap != null) {
		                    long[] prevData = dataMap.get(apssidlistId);              // get PREVIOUS data

		                    if (prevData != null) {                               // null may be has not ever read for this device and apssidindex, or, is cleared.
		                        long diffRx = currData[0] - prevData[0];
		                        if (diffRx < 0) diffRx = ((long)Integer.MAX_VALUE * 2) - prevData[0] + currData[0];     // if diff is minus, use current data as diff
		                        rxKbps = (float) diffRx * 8 / (float) interval / 1024;
		                        
		                        long diffTx = currData[1] - prevData[1];
		                        if (diffTx < 0) diffTx = ((long)Integer.MAX_VALUE * 2) - prevData[1] + currData[1];
		                        txKbps = (float) diffTx * 8 / (float) interval / 1024;
//				    	 		System.out.println(String.format("apssidlistId:%s  curr_rx:%d curr_tx:%d diff_rx:%d diff_tx:%d rxKbps:%f txKbps:%f ", apssidlistId, currData[0], currData[1], diffRx, diffTx, rxKbps, txKbps));
		                        
		                    }
		                    else {
		                        System.out.println("apssidlist_rx_tx_octet_bps: no data for " + apssidlistId);
		                    }
		                }
		                else {
		                    dataMap = new LinkedHashMap<String, long[]>();        // has not ever read this chart
		                    System.out.println("apssidlist_rx_tx_octet_bps: has not ever read this chart, " + apssidlistId);
		                }

		                dataMap.put(apssidlistId, currData);                                    // replace old data
		                request.getSession().setAttribute(sessionName, dataMap);
		                //System.out.println("apssidlist_rx_tx_octet_bps: data calculate success, " + apssidlistId + ", rxKbps:" + rxKbps + ", txKbps: " + txKbps);
		            }
		            else {
		                // if current data fails to read, nothing will be stored and return 0 kbps
		                System.out.println("apssidlist_rx_tx_octet_bps: current data read error, " + apssidlistId);
		            }
		        }
		        else {
		            System.out.println("apssidlist_rx_tx_octet_bps: ssidindexid or interval is wrong, " + apssidlistId + ", int:" + interval);
		        }
		        
		        output = String.format("%f,%f", rxKbps, txKbps);
		    }
		}
		else if (chart.equals("rx_tx_utilization")) {
		    output = "0,0";
			if ((action == null || action.isEmpty()) ) {
		        System.out.println("rx_tx_utilization some data missing, action:" + action );
		    }
		    else if (action.equals("clear")) {
//		        System.out.println("rx_tx_utilization data, action mode : " + action);
		    	
		        @SuppressWarnings("unchecked")
		        Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute("rx_tx_utilization");     // store rx and tx octet (32 bits, unsigned)
		        dataMap = new LinkedHashMap<String, long[]>();     // if it does not ever request, create an empty map
		        System.out.println("rx_tx_utilization: create the map. ");
		        request.getSession().setAttribute("rx_tx_utilization", dataMap);
		    	
//    	 		System.out.println("clear rx_tx_utilization.");
//    	 		request.getSession().removeAttribute("rx_tx_utilization");
		    }
		    else if (action.equals("read")) {
		        String deviceId = ip + "," + port;
		        int portid = 0;
		        int interval = 0;
		        float rxUtilization = 0, txUtilization = 0;
		        try {
		            portid = Integer.parseInt(port);
		            interval = Integer.parseInt(updateInterval);
		        }
		        catch (NumberFormatException e) {
		            //System.out.println("rx_tx_utilization: port or interval parse to int failed, " + deviceId);   // may duplicated print
		        }
		        
		        if (portid > 0 && interval > 0) {
		            long[] currRxTxOctet = network.getRxTxOctet(ip, portid);          // get CURRENT RxTxOctet
		            long[] currRxTxPacket = network.getRxTxPacket(ip, portid);          // get CURRENT RxTxPacket
		            long[] currData = {currRxTxOctet[1],currRxTxOctet[0],currRxTxPacket[2],currRxTxPacket[1],currRxTxPacket[0]};  
		            // {TxOctet, RxOctet,Speed, TxPacket,RxPacket}


		            if (currData != null && currData.length == 5) {
		                @SuppressWarnings("unchecked")
		                Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute("rx_tx_utilization");
		                
		                if (dataMap != null) {
		                    long[] prevData = dataMap.get(deviceId);              // get PREVIOUS data


		                    if (prevData != null) {                               // null may be has not ever read for this device and port, or, is cleared.
		                        long diffTxOctet  = currData[4] - prevData[4];
		                        long diffRxOctet  = currData[3] - prevData[3];
		                        long diffTxPacket = currData[1] - prevData[1];
		                        long diffRxPacket = currData[0] - prevData[0];
		                        long speed = currData[2];		                        
		                        if (speed == 0) speed = 1000000000;
		                        if (diffTxOctet < 0) diffTxOctet = ((long)Integer.MAX_VALUE * 2 ) - prevData[4] + currData[4];     // if diff is minus, use current data as diff
		                        if (diffRxOctet < 0) diffRxOctet = ((long)Integer.MAX_VALUE * 2 ) - prevData[3] + currData[3];     // if diff is minus, use current data as diff
		                        if (diffTxPacket < 0) diffTxPacket = ((long)Integer.MAX_VALUE * 2 ) - prevData[1] + currData[1];     // if diff is minus, use current data as diff
		                        if (diffRxPacket < 0) diffRxPacket = ((long)Integer.MAX_VALUE * 2 ) - prevData[0] + currData[0];     // if diff is minus, use current data as diff

		                        rxUtilization = (float) Math.ceil((float) (diffRxOctet * 8) /  (float) (interval*speed) * 100);
		                        if (rxUtilization >= 100) rxUtilization = 100;
		                        txUtilization = (float) Math.ceil((float) (diffTxOctet * 8) /  (float) (interval*speed) * 100);
		                        if (txUtilization >= 100) txUtilization = 100;

		                    }
		                    else {
		                        System.out.println("rx_tx_utilization: no data for " + deviceId);
		                    }
		                }
		                else {
		                    dataMap = new LinkedHashMap<String, long[]>();        // has not ever read this chart
		                    System.out.println("rx_tx_utilization: has not ever read this chart, " + deviceId);
		                }

		                dataMap.put(deviceId, currData);                                    // replace old data
		                request.getSession().setAttribute("rx_tx_utilization", dataMap);
		                //System.out.println("rx_tx_utilization: data calculate success, " + deviceId + ", rxKbps:" + rxKbps + ", txKbps: " + txKbps);
		            }
		            else {
		                // if current data fails to read, nothing will be stored and return 0 kbps
		                System.out.println("rx_tx_utilization: current data read error, " + deviceId);
		            }
		        }
		        else {
		            System.out.println("rx_tx_utilization: portid or interval is wrong, " + deviceId + ", int:" + interval);
		        }
		        
		        output = String.format("%f,%f", rxUtilization, txUtilization);
		    }
		}
		else if (chart.equals("rx_all_octet_bps") || chart.equals("tx_all_octet_bps")) {
		    String sessionName = chart;
		    
		    if ((action == null || action.isEmpty()) || (ip == null || ip.isEmpty())) {
                System.out.println(chart + ": some data is missing, action:" + action + ", ip: " + ip);
            }
		    else if (action.equals("clear")) {
                String deviceId = ip;
                @SuppressWarnings("unchecked")
                Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName);     // store rx OR tx octet (32 bits, unsigned)

                if (dataMap != null) {
                    dataMap.remove(deviceId);
                    System.out.println(chart + ": data cleared, " + deviceId);
                }
                else {
                    dataMap = new LinkedHashMap<String, long[]>();     // if it does not ever request, create an empty map
                    System.out.println(chart + ": create the map, " + deviceId);
                }

                request.getSession().setAttribute(sessionName, dataMap);
            }
		    else if (action.equals("read")) {
                String deviceId = ip;
                int interval = 0;
                float[] rateKbps = null;
                try {
                    interval = Integer.parseInt(updateInterval);
                }
                catch (NumberFormatException e) {
                    //System.out.println("rx_tx_octet_bps: port or interval parse to int failed, " + deviceId);   // may duplicated print
                }
                
                if (interval > 0) {
                    long[] currData = network.getRxTxOctetAll(ip, chart.equals("rx_all_octet_bps") ? "rx" : "tx");       // get CURRENT data for all ports
                    //System.out.println("currData: " + Arrays.toString(currData));
                    
                    if (currData != null && currData.length > 0) {
                        @SuppressWarnings("unchecked")
                        Map<String, long[]> dataMap = (Map<String, long[]>) request.getSession().getAttribute(sessionName); // the "Value" store all rx/tx of all ports
                        
                        if (dataMap != null) {
                            long[] prevData = dataMap.get(deviceId);              // get PREVIOUS data for all ports

                            if (prevData != null && prevData.length == currData.length) {       // null may be has not ever read for this device and port, or, is cleared.
                                //System.out.println("prevData: " + Arrays.toString(prevData));
                                rateKbps = new float[prevData.length];
                                
                                for (int i = 0; i < prevData.length; i++) {
                                    long diff = currData[i] - prevData[i] >= 0 ? currData[i] - prevData[i] : currData[i]; // if diff is minus, use current data as diff
                                    rateKbps[i] = (float) diff * 8 / (float) interval / 1000;
                                }
                            }
                            else {
                                System.out.println(chart + ": no data for " + deviceId);
                            }
                        }
                        else {
                            dataMap = new LinkedHashMap<String, long[]>();        // has not ever read this chart
                            System.out.println(chart + ": has not ever read this chart, " + deviceId);
                        }

                        dataMap.put(deviceId, currData);                                    // replace old data
                        request.getSession().setAttribute(sessionName, dataMap);
                        //System.out.println(chart + ": data calculate success, " + deviceId + ", Kbps:" + Arrays.toString(rateKbps));
                    }
                    else {
                        // if current data fails to read, nothing will be stored and return 0 kbps
                        System.out.println(chart + ": current data read error, " + deviceId);
                    }
                }
                else {
                    System.out.println(chart + ": interval is wrong, " + deviceId + ", int:" + interval);
                }
                
                if (rateKbps != null && rateKbps.length > 0) {
                    output = String.valueOf(rateKbps[0]);
                    for (int i = 1; i < rateKbps.length; i++)
                        output += "," + String.valueOf(rateKbps[i]);
                }
                else {
                    output = "0";
                }
            }
		}
		else {
			System.out.println("chart type is not correct.");
		}

		response.getWriter().write(output);
		
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
