package com.via.model;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class JSystem {
    public final static String javaVersion;
    public final static String javaHome;
    public final static String javaVmVersion;
    public final static String javaVmName;
    public final static String osName;
    public final static String osDistribution;
    public final static String osArch;
    public final static String osVersion;
    public final static String fileEncoding;
    public final static boolean isServer;
    public final static String userName;
    public final static String userHome;
    public final static String userDir;
    public final static String projectSpace;
    public final static String localHostAddress;
    public final static String localHostName;
    public final static Map<String, List<String>> networkInterfaceMap;
    public final static String ethAddress;
    
    private static String webappHome;
    private static String projectHome;
    
    static {
        javaVersion = System.getProperty("java.version");
        javaHome = System.getProperty("java.home");
        javaVmVersion = System.getProperty("java.vm.version");
        javaVmName = System.getProperty("java.vm.name");
        System.out.println(String.format("java.ver:%s, java.home:%s",  javaVersion, javaHome));

        osName = System.getProperty("os.name");
        osDistribution = getOsDistribution(osName);
        osArch = System.getProperty("os.arch");
        osVersion = System.getProperty("os.version");
        isServer = osName.startsWith("Linux") ? true : false;
        System.out.println(String.format("osName:%s, osDistr:%s, os.arch:%s, os.ver:%s, isServer:%s", osName, osDistribution, osArch, osVersion, isServer));

        fileEncoding = System.getProperty("file.encoding");
        System.out.println("file system encoding: " + fileEncoding);
        
        userName = System.getProperty("user.name");
        userHome = System.getProperty("user.home");
        userDir = System.getProperty("user.dir");
        System.out.println("user.name: " + userName);
        System.out.println("user.home: " + userHome);
        System.out.println("user.dir: " + userDir);

        localHostAddress = getLocalHostAddress();
        localHostName = getLocalHostName();
        networkInterfaceMap = getNetworkInterface();
        ethAddress = getEthAddress();

        if (isServer) {
            String pathToFolder = "/etc/tomcat7";
            new File(pathToFolder).mkdirs();
            projectSpace = pathToFolder;
        }
        else {
            projectSpace = userHome;
        }
        System.out.println("project.space: " + projectSpace);
        
        webappHome = "";
    }
    
    private static String getOsDistribution(final String osName) {
        String distName = "";
        
        if (osName.equalsIgnoreCase("Linux")) {
            String linuxDistributeFile = "/etc/issue";
            
            try {
                FileInputStream fstream = new FileInputStream(linuxDistributeFile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (strLine.indexOf("Ubuntu") >= 0) distName = "Ubuntu";
                    else if (strLine.indexOf("CentOS") >= 0) distName = "CentOS";
                    else continue;
                    
                    break;
                }
                in.close();
            }
            catch (FileNotFoundException e1) {
                System.out.println("distribution file not found: " + linuxDistributeFile);
            }
            catch (IOException e2) {
                System.out.println("failed to read file: " + linuxDistributeFile);
            }
        }
        else if (osName.equalsIgnoreCase("Windows")) {
        }
        else {
        }
        
        return distName;
    }
    
    private static String getLocalHostAddress() {
        String addr = "";
        try {
            addr = InetAddress.getLocalHost().getHostAddress();       // Note that it may only return local IP rather than public IP
            System.out.println("Local Host Address: " + addr);
        }
        catch (UnknownHostException e) {
            System.out.println("UnknownHostException for getHostAddress()");
        }
        return addr;
    }
    
    private static String getLocalHostName() {
    	String name = "";
        try {
            name = InetAddress.getLocalHost().getHostName();
            System.out.println("Local Host Name: " + name);
        }
        catch (UnknownHostException e) {
            System.out.println("UnknownHostException for getHostName()");
        }
        return name;
    }
    
    private static Map<String, List<String>> getNetworkInterface() {
        Map<String, List<String>> networkInterfaceMap = new TreeMap<String, List<String>>();
        
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                String infName = iface.getName();
//                System.out.println(infName + ":");
                
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                List<String> inetAddrList = new ArrayList<String>();
                while (addresses.hasMoreElements()) {
                    String inetAddr = addresses.nextElement().toString();
                    while (inetAddr.startsWith("/")) inetAddr = inetAddr.substring(1);
                    inetAddrList.add(inetAddr);
//                    System.out.println("  " + inetAddr);
                }
                networkInterfaceMap.put(infName, inetAddrList);					// NOTE some interface does not bind any address
            }
        }
        catch (SocketException e) {
            System.out.println("Get NetworkInterface exception.");
        }
        
        return networkInterfaceMap;
    }
    
    private static String getEthAddress() {
        if (networkInterfaceMap == null || networkInterfaceMap.isEmpty()) return "";
        
        Map<String, String> ethAddrMap = new TreeMap<String, String>();
        
        for (Map.Entry<String, List<String>> entry : networkInterfaceMap.entrySet()) {
            String infName = entry.getKey();
            if (infName.startsWith("eth") || infName.startsWith("net")) {		// TODO find out if wireless always use 'net' in interface name
                for (String addr : entry.getValue()) {
                    if (JTools.validIpv4Addr(addr)) {       // will only get the first valid address
                        ethAddrMap.put(infName, addr);
                        System.out.println(infName + ": " + addr);
                        break;
                    }
                }
            }
        }
        
        int minId = Integer.MAX_VALUE;
        String infName = "";
        String infAddr = "";
        for (Map.Entry<String, String> entry : ethAddrMap.entrySet()) {
            try {
                int infIdx = Integer.parseInt(entry.getKey().substring(3));
                if (infIdx < minId) {
                	minId = infIdx;
                	infName = entry.getKey();
                	infAddr = entry.getValue();
                }
            }
            catch (NumberFormatException e) {
            }
        }
        
        if (minId != Integer.MAX_VALUE)
        	System.out.println("Eth Address: " + infName + ", " + infAddr);
        else
        	System.out.println("No Ethernet port found.");
        
        return infAddr;
    }

	public static final String getWebappHome() {
		return webappHome;
	}

	public static final void setWebappHome(String webappHome) {
		JSystem.webappHome = webappHome;
	}

	public static final String getProjectHome() {
		return projectHome;
	}

	public static final void setProjectHome(String projectHome) {
		JSystem.projectHome = projectHome;
	}
}
