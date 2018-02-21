package com.via.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

import org.apache.log4j.Logger;

public class JTools {

	public static int defaultSnmpPort = 161;
	
	public static void print(final String[][] array, boolean isEnable) {
		if (!isEnable) return;
		Logger logger = Logger.getLogger(JTools.class);
		
		logger.debug("JTools:Print 2-D Array, length=" + (array != null ? array.length : "null"));
		if (array != null){
		    for (String[] s : array) {
		        logger.debug(Arrays.toString(s));
			}
		}
	}
	
	public static void print(Map<String, List<String>> map, boolean isEnabled) {
        if (!isEnabled) return;
        Logger logger = Logger.getLogger(JTools.class);
        
        logger.debug("Print Map, size=" + map != null ? map.size() : "null");
        if (map != null) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String index = entry.getKey();
                List<String> valueList = entry.getValue();
                logger.debug("index=" + index + ", value=" + valueList);
            }
        }
    }
	
	public static void print(final String[] array, boolean isEnable) {
		if (!isEnable) return;
		Logger logger = Logger.getLogger(JTools.class);
		
		logger.debug("Print 1-D Array, length=" + array != null ? array.length : "null");
		if (array != null)  logger.debug(Arrays.toString(array));
	}
	
	public static boolean compareArrays(String[] array1, String[] array2) {
	    boolean flag = true;
	    
	    if (array1 != null && array2 != null){
	        if (array1.length != array2.length)
	            flag = false;
	        else
	            for (int i = 0; i < array2.length; i++) {
	                if (!array2[i].equals(array1[i])) {
	                    flag = false;
	                    break;
	                }
	            }
	    }
	    else {
	        flag = false;
	    }

	    return flag;
	}
	
	public static boolean compareArrays(String[][] array1, String[][] array2) {
        if (array1 == null || array2 == null) return false;
        if (array1.length != array2.length) return false;
        
        boolean flag = true;
        for (int i = 0; i < array1.length; i++) {
            if (!compareArrays(array1[i], array2[i])) {
                flag = false;
                break;
            }
        }

        return flag;
    }

	public static String parseSnmpIp(final String ipAddr) {
		if (ipAddr != null && ipAddr.indexOf("/") != -1) {		// if there is a "/", return the processed string, otherwise, return the origin.
			return ipAddr.substring(0, ipAddr.indexOf("/"));
		}
		else {
			return ipAddr;
		}
	}
	
	public static String parseSnmpPort(final String ipAddr) {
		if (ipAddr != null && ipAddr.indexOf("/") != -1) {
			return ipAddr.substring(ipAddr.indexOf("/") + 1);
		}
		else {
			return "";
		}
	}
	
	public static String[] parseSnmpAddress(final String[] ipArray) {
		if (ipArray == null || ipArray.length < 1) return null;
		List<String> snmpAddressList = new ArrayList<String>();
		
		for (String ip : ipArray) {
			String tempIp, tempPort;
			if (ip.indexOf("/") == -1) {
				tempIp = ip;
				tempPort = String.valueOf(defaultSnmpPort);
			}
			else {
				tempIp = ip.substring(0, ip.indexOf("/"));
				tempPort = ip.substring(ip.indexOf("/") + 1);
			}
			if (validIpv4Addr(tempIp) != true || validPortNum(tempPort) != true) continue;

			snmpAddressList.add(tempIp + "/" + tempPort);
		}
		if (snmpAddressList.size() != 0) {
			String[] resultAddrArray = new String[snmpAddressList.size()];
			for (int i = 0; i < snmpAddressList.size(); i++) {
				resultAddrArray[i] = snmpAddressList.get(i);
			}
			return resultAddrArray;
		}
		else {
			return null;
		}
	}
	
	public static int[] parseIpToInt(final String ipAddr) {
	    String[] parts = ipAddr.split("\\.");
	    if (parts == null || parts.length != 4) return null;
	    int[] ipIntArray = {0, 0, 0, 0};
	    int i = 0;
	    
	    for (String s : parts) {
            try {
                ipIntArray[i++] = Integer.parseInt(s);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
	    
	    return ipIntArray;
	}
	
	public static String parseIntToIp(final int[] ipIntegerArray) {
	    if (ipIntegerArray == null || ipIntegerArray.length != 4) return null;
	    for (int i : ipIntegerArray) if ((i < 0) || (i > 255)) return null;
	    
	    return String.format("%d.%d.%d.%d", ipIntegerArray[0], ipIntegerArray[1], ipIntegerArray[2], ipIntegerArray[3]);
	}
	
	public static boolean validIpv4Addr(final String ipAddr) {
		String[] parts = ipAddr.split("\\.");

		if (parts.length != 4) return false;

		for (String s : parts) {
			int i = 0;
			try {
				i = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return false;
			}
			if ((i < 0) || (i > 255)) return false;
		}

		return true;
	}

	public static boolean validPortNum(final String port) {
		if (port.isEmpty()) return false;

		try {
			int nPort = Integer.parseInt(port);
			if (nPort < 1 || nPort > 65535) return false;
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}
	
	public static int Ping(String host, int count, int timeout_sec) {
		if (count > 20) {
			count = 20;
		}

		if (count <= 0) {
			count = 1;
		}

		int timeout_ms = timeout_sec * 1000;

		if (timeout_ms > 60000) {
			timeout_ms = 60000;
			timeout_sec = 60;
		}

		if (timeout_ms <= 0) {
			timeout_ms = 1;
			timeout_sec = 1;
		}

		try {
			String cmd = "";
			//String pingResult = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
				// For Windows
				cmd = "ping -n " + count + " -w " + timeout_ms + " " + host;
			} else {
				// For Linux and OSX
				cmd = "ping -c " + count + " -W " + timeout_sec + " " + host;
			}

			System.out.println(cmd);

			Process myProcess = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(myProcess.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				//System.out.println(inputLine);
			}
			in.close();

			myProcess.waitFor();

			//System.out.println(myProcess.exitValue());

			if (myProcess.exitValue() == 0) {

				return 1;
			} else {

				return 0;
			}

		} catch (Exception e) {

			//e.printStackTrace();
			return 0;
		}
	}

	public static String getOSType(String host, int timeout_sec) {

		if (timeout_sec > 60) {
			timeout_sec = 60;
		}

		if (timeout_sec <= 0) {
			timeout_sec = 1;
		}

		String OSType = "Cannot determine.";

		try {
			String cmd = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
				// For Windows
				cmd = "C:" + File.separator + "Program Files (x86)" + File.separator + "Nmap" + File.separator + "nmap -F -sV --host_timeout " + timeout_sec + "s -PN -n -v " + host;
			} else {
				// For Linux and OSX
				cmd = "nmap -F -sV --host_timeout " + timeout_sec + "s -PN -n -v " + host;
			}

			System.out.println(cmd);

			Process myProcess = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(myProcess.getInputStream(), "Big5"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				//System.out.println(inputLine);
				if (inputLine.indexOf("Service Info: OS: ") != -1) {
					//System.out.println(inputLine);
					//System.out.println(inputLine.split(";")[0].split(" ")[3]);
					OSType = inputLine.split(";")[0].split(" ")[3];
				}
			}
			in.close();

			myProcess.waitFor();

			/*System.out.println(myProcess.exitValue());

			if (myProcess.exitValue() == 0) {

				return true;
			} else {

				return false;
			}*/

			//System.out.println("OSType：" + OSType);
			return OSType;
		} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println("OSType：" + OSType);
			return OSType;
		}
	}
	
	public static String getMAC(String host, int timeout_sec) {

		if (timeout_sec > 60) {
			timeout_sec = 60;
		}

		if (timeout_sec <= 0) {
			timeout_sec = 1;
		}

		String MAC = "Cannot determine.";

		try {
			String cmd = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
				// For Windows
				cmd = "C:" + File.separator + "Program Files (x86)" + File.separator + "Nmap" + File.separator + "nmap -n -sP --host_timeout " + timeout_sec + "s -v " + host;
			} else {
				// For Linux and OSX
				cmd = "nmap -n -sP --host_timeout " + timeout_sec + "s " + host;
			}

			System.out.println(cmd);

			Process myProcess = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(myProcess.getInputStream(), "Big5"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				//System.out.println(inputLine);
				if (inputLine.indexOf("MAC Address: ") != -1) {
					//System.out.println(inputLine);
					//System.out.println(inputLine.split(" ")[2]);
					MAC = inputLine.split(" ")[2];
				}
			}
			in.close();

			myProcess.waitFor();

			/*System.out.println(myProcess.exitValue());

			if (myProcess.exitValue() == 0) {

				return true;
			} else {

				return false;
			}*/

			//System.out.println("MAC：" + MAC);
			return MAC;
		} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println("MAC：" + MAC);
			return MAC;
		}
	}

	public static void CLI(String cmd) {
		if (JSystem.osName.equalsIgnoreCase("Linux")) {
			String line = "";

			try {
				String[] cmd_array = { "/bin/sh", "-c", cmd };
				Process Process_bak = Runtime.getRuntime().exec(cmd_array);
				BufferedReader BufferedReader_bak = new BufferedReader(new InputStreamReader(Process_bak.getInputStream(), "UTF-8"));
				while ((line = BufferedReader_bak.readLine()) != null) {
				}
				BufferedReader_bak.close();
				Process_bak.waitFor();
			} catch (Exception e) {
				System.out.println(cmd + "\nException:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public static boolean copyFile(String source, String dest) {
		boolean result = false;
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(new File(source));
			output = new FileOutputStream(new File(dest));
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
			
			result = true;
		} catch(FileNotFoundException e) {
			System.out.println("Tool@copyFile: FileNotFoundException.");
		} catch(IOException e) {
			System.out.println("Tool@copyFile: Read byes exception.");
		} finally {
			try {
				if (input != null) input.close();
				if (output != null) output.close();
			} catch (IOException e) {
			}
		}
		
		return result;
	}
	
	public static boolean moveFile(String source, String dest) {
		File sourceFile = new File(source);
		if (sourceFile.isFile() == false) {
			System.out.println("Tool@moveFile: source file does not exist.");
			return false;
		}
		File destFile = new File(dest);
		destFile.delete();
		return sourceFile.renameTo(destFile);
	}
	
	public static List<List<String>> getIpScanList(String startIp, String endIp, String community, int snmptimeout, int snmpVersion, String type) {
		List<List<String>> ipScanList = new ArrayList<List<String>>();
        
        // check start and end IP validation
        int[] startIpArray = JTools.parseIpToInt(JTools.parseSnmpIp(startIp));
        int[] endIpArray = JTools.parseIpToInt(JTools.parseSnmpIp(endIp));
        
        String port = JTools.parseSnmpPort(startIp);

        for (int i = 0; i < 4; i++) {
            if (startIpArray[i] < endIpArray[i]) {
                break;
            }
            else if (startIpArray[i] == endIpArray[i]) {
                continue;
            }
            else {                          // this block will be entered only if prior numbers are all equal
            	return ipScanList;
            }
        }
        
        // generate all IPs
        for (int a = startIpArray[0]; a <= endIpArray[0]; a++) {
            
            for (int b = (a == startIpArray[0] ? startIpArray[1] : 0); b <= (a == endIpArray[0] ? endIpArray[1] : 255); b++) {
                
                for (int c = (b == startIpArray[1] ? startIpArray[2] : 0); c <= (b == endIpArray[1] ? endIpArray[2] : 255); c++) {
                    
                    for (int d = (c == startIpArray[2] ? startIpArray[3] : 1); d <= (c == endIpArray[2] ? endIpArray[3] : 254); d++) {
                        
                        String ipAddr = JTools.parseIntToIp(new int[]{a, b, c, d});
                        if (ipAddr != null && !ipAddr.isEmpty()) {                            
                            List<String> Column = new ArrayList<String>();
                            Column = Arrays.asList(new String[] {ipAddr, "", port, Integer.toString(snmpVersion), community, Integer.toString(snmptimeout), type});
                            ipScanList.add(Column);
                        }
                    }
                }
            }
        }
        
        return ipScanList;
    }
}
