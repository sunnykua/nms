package com.via.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class SystemInfo
 */
@WebServlet("/SystemInfo")
public class SystemInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SystemInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String chart = request.getParameter("chart");
		String[] chartIp = request.getParameterValues("portselect[]");
		String ip = request.getParameter("ip");
		String[] memoryData = request.getParameterValues("memoryData[]");
		String[] diskChartIp = request.getParameterValues("diskChartData[]");
		//String ip = "61.220.40.158";
		
		Logger logger = Logger.getLogger(SystemInfo.class);

		if (chart == null) {
			System.out.println("chart parameter is missing.");
		}
		else if (chart.equals("hostView")) {
			if (ip != null) {
				System.out.println("System Information - ip: " + ip);
		        logger.debug("Device IP = " + ip + " System Information Start");   

				JSystemInfoTable table = network.systemInfo(ip);
				request.setAttribute("HostInfor", table);
			}
			else {
		        logger.debug("System Information - Read All Device Start");   
				List<JSystemInfoTable> tables = network.systemInfo();
				request.setAttribute("staticvlanList", tables);
			}
		}else if (chart.equals("hostCpu")) {
		
			String cpu = network.hostCpu(chartIp[0]);
			
			String[] cupSplit = cpu.split(",");
						
//			int cup1 = Integer.parseInt(cupSplit[0]);
//			int cup2 = Integer.parseInt(cupSplit[1]);
//			int cup3 = Integer.parseInt(cupSplit[2]);
//			int cup4 = Integer.parseInt(cupSplit[3]);			
//			int tempResult = (cup1 + cup2 + cup3 + cup4)/4;
			
			double cupSplit_total = 0;
			for (int i = 0; i <= cupSplit.length-1; i++) {
				cupSplit_total += Double.parseDouble(cupSplit[i]);
			}
			double tempResult = cupSplit_total/(cupSplit.length);
			String result = Integer.toString((int)tempResult);
			
//			String result = cpu;

			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}else if (chart.equals("hostMemory")) {
		
			String memory = network.hostMemory(chartIp[0]);
			
			String[] memorySplit = memory.split(",");
			
			double physical_size = Double.parseDouble(memorySplit[0]);
			double physical_used = Double.parseDouble(memorySplit[1]);
			
			double tempResult = physical_used / physical_size *100 ;
			
			String result = String.valueOf((int)tempResult);
			
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if (chart.equals("hostMemoryView")) {
		
			String memory = network.hostMemory(memoryData[0]);
			
			String[] memorySplit = memory.split(",");
			
			double tempTotal = Double.parseDouble(memorySplit[0]);
			double tempCached = Double.parseDouble(memorySplit[6]);
			double tempUsed = Double.parseDouble(memorySplit[1]);
			
			double total = tempTotal/1024;
			double cached = tempCached/1024;
			double used = tempUsed/1024;
			double free = total - used;
			
			String result = String.valueOf((int)total)+ ","+ String.valueOf((int)cached)+ ","+ String.valueOf((int)used)+ ","+ String.valueOf((int)free);
			
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if (chart.equals("hostDisk")) {
			String disk = network.hostDisk(diskChartIp[0]);
			String[] diskSplit = disk.split(",");
			
			double disk_size_total = 0;
			for (int i = 0; i <= diskSplit.length-1; i=i+2) {
				disk_size_total += Double.parseDouble(diskSplit[i]);
			}
			double disk_used_total = 0;
			for (int i = 1; i <= diskSplit.length-1; i=i+2) {
				disk_used_total += Double.parseDouble(diskSplit[i]);
			}
			
			double diskSize = disk_size_total/1024/1024/1024;
			double diskUsed = disk_used_total/1024/1024/1024;
			double diskFree = diskSize - diskUsed;
			
			double tempResult = disk_used_total / disk_size_total *100 ;
			
			String result = (String.valueOf(tempResult)).substring(0, (String.valueOf(tempResult)).indexOf(".") + 3)
					+ ","+ (String.valueOf((double)diskSize)).substring(0, (String.valueOf((double)diskSize)).indexOf(".") + 3)
					+ ","+ (String.valueOf((double)diskUsed)).substring(0, (String.valueOf((double)diskUsed)).indexOf(".") + 3)
					+ ","+ (String.valueOf((double)diskFree)).substring(0, (String.valueOf((double)diskFree)).indexOf(".") + 3);
			
			if(Double.toString(diskFree).indexOf("-")!=-1)
			{
				result = (String.valueOf(tempResult)).substring(0, (String.valueOf(tempResult)).indexOf(".") + 3)
					+ ","+ (String.valueOf((double)diskSize)).substring(0, (String.valueOf((double)diskSize)).indexOf(".") + 3)
					+ ","+ (String.valueOf((double)diskUsed)).substring(0, (String.valueOf((double)diskUsed)).indexOf(".") + 3)
					+ ","+ 0;
			}
			
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (chart.equals("edgeCoreCpu")) {
			
			String cpu = network.edgecoreCpu(chartIp[0]);
	
			String result = cpu;
//			System.out.println(" edgecore Cpu : " + result);

			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}else if (chart.equals("edgeCoreMemory")) {
			
		
			String memory = network.edgecoreMemory(chartIp[0]);
			
			String[] memorySplit = memory.split(",");
			
			double memory_total = Double.parseDouble(memorySplit[0]);
			double memory_used  = Double.parseDouble(memorySplit[1]);
			
			double tempResult = memory_used / memory_total *100 ;
			
			String result = String.valueOf((int)tempResult);
			
//			System.out.println(" edgecore memory : " + result);

			
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if (chart.equals("edgeCoreMemoryView")) {
			double memory_total = 0;
			double memory_used = 0;
			try {
				String memory = network.edgecoreMemory(memoryData[0]);			
				String[] memorySplit = memory.split(",");			
				memory_total = Double.parseDouble(memorySplit[0]);
				memory_used  = Double.parseDouble(memorySplit[1]);
//				double memory_freed = Double.parseDouble(memorySplit[2]);
			} catch(NullPointerException e) {
				logger.warn("Error: Cannot laod edgecoreMemory.");   
			}		
						
			double total = memory_total/1024/1024;
			double used = memory_used/1024/1024;
//			double free = total - used;
			
			String result = String.valueOf((int)total)+ "," + String.valueOf((int)used) + "," + String.valueOf((int)total - (int)used);
			
//			System.out.println(" edgecore memory : " + result);

			
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (chart.equals("get_active_connection")) {
		    String result = "";
            if (JSystem.osDistribution.equals("CentOS")) {
                result = "Failed";
                do {
                    String[] cmdArray = {"/bin/sh", "-c", "/usr/bin/curl http://127.0.0.1/nginx_status 2> /dev/null | /usr/bin/awk '$1==\"Active\" { print$3 }'"};
                    Process getActiveConnectionProcess;
                    try {
                        getActiveConnectionProcess = Runtime.getRuntime().exec(cmdArray);
                    }
                    catch (IOException e) {
                        System.out.println("run get_active_connection_script failed.");
                        break;
                    }
                    BufferedReader getActiveConnectionBuffReader;
                    try {
                        getActiveConnectionBuffReader = new BufferedReader(new InputStreamReader(getActiveConnectionProcess.getInputStream(), "UTF-8"));
                    }
                    catch (UnsupportedEncodingException e) {
                        System.out.println("create buffer about get_active_connection_script failed.");
                        break;
                    }
                    String line = "";
                    try {
                        while ((line = getActiveConnectionBuffReader.readLine()) != null) {
                            if (line.isEmpty()) continue;
                            //System.out.println("Active Connections: " + line);
                            result = line;
                            break;
                        }
                    }
                    catch (IOException e) {
                        System.out.println("reading the result from get_active_connection_script failed.");
                        break;
                    }
                    try {
                        getActiveConnectionBuffReader.close();
                    }
                    catch (IOException e) {
                        System.out.println("close buffer about get_active_connection_script failed.");
                        break;
                    }
                    try {
                        getActiveConnectionProcess.waitFor();
                    }
                    catch (InterruptedException e) {
                        System.out.println("terminate process about get_active_connection_script failed.");
                        break;
                    }
                } while (false);
            }
            else {
                result = "Not Supported";
            }
            
            try {
                response.getWriter().write(result);
            }
            catch (IOException e) {
                //
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
