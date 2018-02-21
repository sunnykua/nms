package com.via.controller;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.via.model.JDevice;
import com.via.model.JNetwork;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;
import com.via.system.Config;

/**
 * Servlet implementation class HistoryChart
 */
@WebServlet("/Dashboard")
public class Dashboard extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Dashboard() {
		super();
		// TODO Auto-generated constructor stub
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//Random ran = new Random();

		//double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");

		String method = request.getParameter("method");

		if (method.equals("getLocation")) {
			//System.out.println("getLocation");

			String address = request.getParameter("address");
			//System.out.println("address: " + address);

			if ((address == null) && Config.getNmsType().equals("2")) {
				List<Object> AllList = new ArrayList<Object>();

				AllList.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				AllList.add(JNetwork.getRemoteNMSListLocationAddress());

				String json_data = new Gson().toJson(AllList);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				try {
					response.getWriter().write(json_data);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			} else if (!address.equals("")) {
				String result = "";

				RemoteInterface Service = RemoteService.creatClient(address, 3);

				if (Service != null) {
					String Location = null;

					try {
						Location = Service.getRemoteLocationAddress();
					} catch (RemoteException e) {
						System.out.println("Location = Service.getRemoteLocationAddress();\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}

					if (Location != null) {
						result = Location;
					}
				}

				result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "|" + result;

				//System.out.println(result);

				response.setContentType("text/text");
				response.setCharacterEncoding("UTF-8");

				try {
					response.getWriter().write(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (method.equals("getSysInfo")) {
			//System.out.println("getSysInfo");

			String result = "";

			String address = request.getParameter("address");
			//System.out.println("address: " + address);

			if (address == null) {
				String[] info = null;

				info = JNetwork.getRemoteDeviceSystemInfo();

				if (info != null && info.length == 6) {
					result = info[0] + "|" + info[1] + "|" + info[2] + "|" + info[3] + "|" + info[4] + "|" + info[5];
				}
			} else if (!address.equals("")) {
				RemoteInterface Service = RemoteService.creatClient(address, 3);

				if (Service != null) {
					String[] info = null;

					try {
						info = Service.getRemoteDeviceSystemInfo();
					} catch (RemoteException e) {
						System.out.println("info = Service.getRemoteDeviceSystemInfo();\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}

					if (info != null && info.length == 6) {
						result = info[0] + "|" + info[1] + "|" + info[2] + "|" + info[3] + "|" + info[4] + "|" + info[5];
					}
				}
			}

			result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "|" + result;

			//System.out.println(result);

			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (method.equals("getDevices")) {
			//System.out.println("getDevices");

			String result = "";

			String address = request.getParameter("address");
			//System.out.println("address: " + address);

			int l2switch = 0, l3switch = 0, firewall = 0, wlanAC = 0, wlanAP = 0, NMS = 0, server = 0, pc = 0, internet = 0, unknown = 0, MGVChiefServer = 0, MGVCommandServer = 0, MGVPlayer = 0;
			int online_l2switch = 0, online_l3switch = 0, online_firewall = 0, online_wlanAC = 0, online_wlanAP = 0, online_NMS = 0, online_server = 0, online_pc = 0, online_internet = 0, online_unknown = 0, online_MGVChiefServer = 0, online_MGVCommandServer = 0, online_MGVPlayer = 0;
			int offline_l2switch = 0, offline_l3switch = 0, offline_firewall = 0, offline_wlanAC = 0, offline_wlanAP = 0, offline_NMS = 0, offline_server = 0, offline_pc = 0, offline_internet = 0, offline_unknown = 0, offline_MGVChiefServer = 0, offline_MGVCommandServer = 0, offline_MGVPlayer = 0;
			String online = "";
			String offline = "";

			List<JDevice> getDevice = null;

			if (address == null) {
				getDevice = network.getDeviceList();
			} else if (!address.equals("")) {
				getDevice = network.getRemoteDeviceTable(address);
			}

			//total online、offline for remote nms			
			int total_online = 0, total_offline = 0;

			if (getDevice != null) {
				for (JDevice Device : getDevice) {
					if (Device.isAlive() == 1) {
						total_online++;
					} else {
						total_offline++;
					}

					//System.out.println(Device.getDeviceType());

					if (Device.getDeviceType().equals("l2switch")) {
						l2switch++;

						if (Device.isAlive() == 1)
							online_l2switch++;
						else
							offline_l2switch++;
					}

					if (Device.getDeviceType().equals("l3switch")) {
						l3switch++;

						if (Device.isAlive() == 1)
							online_l3switch++;
						else
							offline_l3switch++;
					}

					if (Device.getDeviceType().equals("firewall")) {
						firewall++;

						if (Device.isAlive() == 1)
							online_firewall++;
						else
							offline_firewall++;
					}

					if (Device.getDeviceType().equals("wlanAC")) {
						wlanAC++;

						if (Device.isAlive() == 1)
							online_wlanAC++;
						else
							offline_wlanAC++;
					}

					if (Device.getDeviceType().equals("wlanAP")) {
						wlanAP++;

						if (Device.isAlive() == 1)
							online_wlanAP++;
						else
							offline_wlanAP++;
					}

					if (Device.getDeviceType().equals("server")) {
						server++;

						if (Device.isAlive() == 1)
							online_server++;
						else
							offline_server++;
					}

					if (Device.getDeviceType().equals("NMS")) {
						NMS++;

						if (Device.isAlive() == 1)
							online_NMS++;
						else
							offline_NMS++;
					}

					if (Device.getDeviceType().equals("pc")) {
						pc++;

						if (Device.isAlive() == 1)
							online_pc++;
						else
							offline_pc++;
					}

					if (Device.getDeviceType().equals("internet")) {
						internet++;

						if (Device.isAlive() == 1)
							online_internet++;
						else
							offline_internet++;
					}

					if (Device.getDeviceType().equals("unknown")) {
						unknown++;

						if (Device.isAlive() == 1)
							online_unknown++;
						else
							offline_unknown++;
					}

					if (Device.getDeviceType().equals("MGVChiefServer")) {
						MGVChiefServer++;

						if (Device.isAlive() == 1)
							online_MGVChiefServer++;
						else
							offline_MGVChiefServer++;
					}

					if (Device.getDeviceType().equals("MGVCommandServer")) {
						MGVCommandServer++;

						if (Device.isAlive() == 1)
							online_MGVCommandServer++;
						else
							offline_MGVCommandServer++;
					}

					if (Device.getDeviceType().equals("MGVPlayer")) {
						MGVPlayer++;

						if (Device.isAlive() == 1)
							online_MGVPlayer++;
						else
							offline_MGVPlayer++;
					}
				}
			}

			result = total_online + total_offline + "|" + total_online + "|" + total_offline;

			if (online_l2switch != 0)
				online = online + "L2 Switch (" + online_l2switch + ")";
			if (online_l3switch != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "L3 Switch (" + online_l3switch + ")";
			}
			if (online_firewall != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "Firewall (" + online_firewall + ")";
			}
			if (online_wlanAC != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "AC (" + online_wlanAC + ")";
			}
			if (online_wlanAP != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "AP(" + online_wlanAP + ")";
			}
			if (online_server != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "Server (" + online_server + ")";
			}
			if (online_NMS != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "NMS (" + online_NMS + ")";
			}
			if (online_pc != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "PC (" + online_pc + ")";
			}
			if (online_internet != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "Internet (" + online_internet + ")";
			}
			if (online_unknown != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "Other (" + online_unknown + ")";
			}
			if (online_MGVChiefServer != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "MGV Chief Server (" + online_MGVChiefServer + ")";
			}
			if (online_MGVCommandServer != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "MGV Command Server (" + online_MGVCommandServer + ")";
			}
			if (online_MGVPlayer != 0) {
				if (!online.equals(""))
					online = online + ", ";
				online = online + "MGV Player (" + online_MGVPlayer + ")";
			}

			if (offline_l2switch != 0)
				offline = offline + "L2 Switch (" + offline_l2switch + ")";
			if (offline_l3switch != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "L3 Switch (" + offline_l3switch + ")";
			}
			if (offline_firewall != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "Firewall (" + offline_firewall + ")";
			}
			if (offline_wlanAC != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "AC (" + offline_wlanAC + ")";
			}
			if (offline_wlanAP != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "AP (" + offline_wlanAP + ")";
			}
			if (offline_server != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "Server (" + offline_server + ")";
			}
			if (offline_NMS != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "NMS (" + offline_NMS + ")";
			}
			if (offline_pc != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "PC (" + offline_pc + ")";
			}
			if (offline_internet != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "Internet (" + offline_internet + ")";
			}
			if (offline_unknown != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "Other (" + offline_unknown + ")";
			}
			if (offline_MGVCommandServer != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "MGV Command Server (" + offline_MGVCommandServer + ")";
			}
			if (offline_MGVChiefServer != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "MGV Chief Server (" + offline_MGVChiefServer + ")";
			}
			if (offline_MGVPlayer != 0) {
				if (!offline.equals(""))
					offline = offline + ", ";
				offline = offline + "MGV Player (" + offline_MGVPlayer + ")";
			}

			result = result + "|" + l2switch + "|" + l3switch + "|" + firewall + "|" + wlanAC + "|" + wlanAP + "|" + server + "|" + NMS + "|" + pc + "|" + internet + "|" + unknown + "|" + MGVChiefServer + "|" + MGVCommandServer + "|" + MGVPlayer + "|" + online + "|" + offline;

			result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "|" + result;

			//System.out.println(result);

			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (method.equals("getLost")) {
			//System.out.println("getLost");

			String address = request.getParameter("address");
			//System.out.println("address: " + address);

			ArrayList<String[]> arrayList2 = new ArrayList<String[]>();

			Calendar calendar = Calendar.getInstance();

			String result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
			//System.out.println(result);

			long endTime_getTime = calendar.getTime().getTime();
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(calendar.getTime());
			//System.out.println("endTime: " + endTime);

			calendar.add(Calendar.DAY_OF_YEAR, -1);
			long startTime_getTime = calendar.getTime().getTime();
			String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(calendar.getTime());
			//System.out.println("startTime: " + startTime);

			arrayList2.add(new String[] { result, startTime, endTime });

			Map<String, Double> hashMap = new HashMap<String, Double>();

			//转换
			ArrayList<Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String, Double>>();

			List<List<String[]>> test = new ArrayList<List<String[]>>();

			List<JDevice> getDevice = null;

			if (address == null) {
				test = JNetwork.getAliveStatus(new String[] { "*" }, "1", startTime, endTime, "");

				getDevice = network.getDeviceList();
			} else if (!address.equals("")) {
				RemoteInterface Service = RemoteService.creatClient(address, 3);

				if (Service != null) {
					try {
						test = Service.getAliveStatus(new String[] { "*" }, "1", startTime, endTime, "");
					} catch (RemoteException e) {
						System.out.println("test = Service.getAliveStatus(new String[] { \"*\" }, \"1\", startTime, endTime, \"\");\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}

					try {
						getDevice = Service.getRemoteDevice();
					} catch (RemoteException e) {
						System.out.println("getDevice = Service.getRemoteDevice();\nRemoteException:" + e.getMessage());
						e.printStackTrace();
					}
				}
			}

			//System.out.println(test.size());
			if (test.size() == 2) {
				/*System.out.println(test.get(0).size());
				for (int i = 0; i < test.get(0).size(); i++) {
					System.out.println(test.get(0).get(i)[0]);
				}*/

				/*System.out.println(test.get(1).size());
				for (int i = 0; i < test.get(1).size(); i++) {
					System.out.println(test.get(1).get(i)[0] + ", " + test.get(1).get(i)[1] + ", " + test.get(1).get(i)[2] + ", " + test.get(1).get(i)[3] + ", " + test.get(1).get(i)[4]);
				}*/

				/*System.out.println(endTime_getTime - startTime_getTime);
				try {
					System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(endTime).getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(startTime).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}*/

				String tmp_time = "";
				String tmp_boolean = "";
				long tmp_sum = 0;

				int all_sum = 0;
				int true_sum = 0;
				for (JDevice Device : getDevice) {

					//System.out.println(Device.getPublicIp());

					int count = 0;
					for (int j = 0; j < test.get(1).size(); j++) {
						if (Device.getPublicIp().equals(test.get(1).get(j)[3])) {
							//System.out.println(test.get(1).get(j)[0] + ", " + test.get(1).get(j)[1] + ", " + test.get(1).get(j)[2] + ", " + test.get(1).get(j)[3] + ", " + test.get(1).get(j)[4]);

							/*//方法1.累積前後為true的線上時間
							if (!tmp_time.equals("")) {
								try {
									if ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(test.get(1).get(j)[0]).getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(tmp_time).getTime()) < 54000000 && tmp_boolean.equals("true") && test.get(1).get(j)[4].equals("true")) {
										//System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(test.get(1).get(j)[0]).getTime() + " - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(tmp_time).getTime() + " = " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(test.get(1).get(j)[0]).getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(tmp_time).getTime()));
										tmp_sum = tmp_sum + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(test.get(1).get(j)[0]).getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(tmp_time).getTime());
									}
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							tmp_time = test.get(1).get(j)[0];
							tmp_boolean = test.get(1).get(j)[4];*/

							//方法2.true的總次數
							all_sum = all_sum + 1;
							if (test.get(1).get(j)[4].equals("true")) {
								true_sum = true_sum + 1;
							}

							count++;
						}
					}

					if (count > 0) {
						/*//方法1.取Select完頭尾時間當分母算Online %
						//System.out.println(tmp_sum);
						try {
							hashMap.put(Device.getPublicIp(), tmp_sum / (double) (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(test.get(1).get(test.get(1).size() - 1)[0]).getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(test.get(1).get(0)[0]).getTime()) * 100);
						} catch (ParseException e) {
							e.printStackTrace();
						}*/

						//方法2.取Select完總次數當分母算Online %
						//System.out.println(true_sum);
						//System.out.println(all_sum);
						hashMap.put(Device.getPublicIp(), true_sum / (double) all_sum * 100);
					}

					tmp_time = "";
					tmp_boolean = "";
					tmp_sum = 0;

					all_sum = 0;
					true_sum = 0;
				}

				arrayList = new ArrayList<Map.Entry<String, Double>>(hashMap.entrySet());

				//排序
				Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> map2, Map.Entry<String, Double> map1) {
						return ((map2.getValue() - map1.getValue() == 0) ? 0 : (map2.getValue() - map1.getValue() > 0) ? 1 : -1);
					}
				});

				String type = "";
				//输出
				for (Entry<String, Double> entry : arrayList) {
					//System.out.println(entry.getKey() + "\t" + entry.getValue());
					for (int i = 0; i < test.get(1).size(); i++) {
						if (test.get(1).get(i)[3].equals(entry.getKey())) {
							//System.out.println(test.get(1).get(i)[2]);
							type = test.get(1).get(i)[2];
						}
					}

					//100 - Online % = Offline %，Offline 0% 的也秀在網頁
					arrayList2.add(new String[] { type, entry.getKey(), new DecimalFormat("#.##").format(100 - entry.getValue()) });

					/*//100 - Online % = Offline %，Offline 0% 的不秀在網頁
					if (!new DecimalFormat("#.##").format(100 - entry.getValue()).equals("0")) {
						arrayList2.add(new String[] { type, entry.getKey(), new DecimalFormat("#.##").format(100 - entry.getValue()) });
					}*/
				}
			}

			String json_data = new Gson().toJson(arrayList2);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else if (method.equals("getHost")) {
			//System.out.println("getHost");

			String result = "";

			String cpu = "";

			String memory = "";

			String disk = "";

			String address = request.getParameter("address");
			//System.out.println("address: " + address);

			if (address == null) {
				cpu = JNetwork.RemotehostCpu();
				memory = JNetwork.RemotehostMemory();
				disk = JNetwork.RemotehostDisk();
			} else if (!address.equals("")) {
				RemoteInterface Service = RemoteService.creatClient(address, 3);

				if (Service != null) {
					try {
						cpu = Service.RemotehostCpu();
					} catch (RemoteException e) {
						System.out.println("cpu = Service.RemotehostCpu();\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}

					try {
						memory = Service.RemotehostMemory();
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					try {
						disk = Service.RemotehostDisk();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}

			//System.out.println("hostCpu:" + cpu);

			if (cpu != "") {
				String[] cupSplit = cpu.split(",");

				/*int cup1 = Integer.parseInt(cupSplit[0]);
				int cup2 = Integer.parseInt(cupSplit[1]);
				int cup3 = Integer.parseInt(cupSplit[2]);
				int cup4 = Integer.parseInt(cupSplit[3]);
				int tempResult = (cup1 + cup2 + cup3 + cup4) / 4;*/

				double cupSplit_total = 0;
				for (int i = 0; i <= cupSplit.length - 1; i++) {
					cupSplit_total += Double.parseDouble(cupSplit[i]);
				}

				double tempResult = cupSplit_total / (cupSplit.length);

				String cpuUsage = Integer.toString((int) tempResult);
				//String cpuUsage = Integer.toString((int) ran.nextInt(100) + 1);

				//System.out.println("hostCpuUsage:" + cpuUsage);

				result = cpuUsage;
			}

			//System.out.println("hostMemory:" + memory);

			if (memory != "") {
				String[] memorySplit = memory.split(",");

				double physical_size = Double.parseDouble(memorySplit[0]);
				double physical_used = Double.parseDouble(memorySplit[1]);

				double tempResult = physical_used / physical_size * 100;

				String memoryUsage = String.valueOf((int) tempResult);
				//String memoryUsage = String.valueOf((int) ran.nextInt(100) + 1);

				//System.out.println("hostMemory:" + memoryUsage);

				result = result + "," + memoryUsage;
			}

			//System.out.println("hostDisk:" + disk);

			if (disk != "") {
				String[] diskSplit = disk.split(",");

				double disk_size_total = 0;
				for (int i = 0; i <= diskSplit.length - 1; i = i + 2) {
					disk_size_total += Double.parseDouble(diskSplit[i]);
				}

				double disk_used_total = 0;
				for (int i = 1; i <= diskSplit.length - 1; i = i + 2) {
					disk_used_total += Double.parseDouble(diskSplit[i]);
				}

				double diskSize = disk_size_total / 1024 / 1024 / 1024;
				double diskUsed = disk_used_total / 1024 / 1024 / 1024;
				double diskFree = diskSize - diskUsed;

				double tempResult = disk_used_total / disk_size_total * 100;

				//result = (String.valueOf(tempResult)).substring(0, (String.valueOf(tempResult)).indexOf(".") + 3) + "," + (String.valueOf((double) diskSize)).substring(0, (String.valueOf((double) diskSize)).indexOf(".") + 3) + "," + (String.valueOf((double) diskUsed)).substring(0, (String.valueOf((double) diskUsed)).indexOf(".") + 3) + "," + (String.valueOf((double) diskFree)).substring(0, (String.valueOf((double) diskFree)).indexOf(".") + 3);
				String diskUsage = (String.valueOf(tempResult)).substring(0, (String.valueOf(tempResult)).indexOf(".") + 3);

				//System.out.println("hostDisk:" + diskUsage);

				result = result + "," + diskUsage;
			}

			result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "," + result;

			//System.out.println(result);

			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (method.equals("getErrorLog")) {
			//System.out.println("getErrorLog");

			String address = request.getParameter("address");
			//System.out.println("address: " + address);

			ArrayList<String> Log = new ArrayList<String>();

			Log.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

			String[][] DBData = null;

			if (address == null) {
				DBData = network.getLast50Log();
			} else if (!address.equals("")) {
				DBData = network.getNmsLast50Log(address);
			}

			if (DBData != null) {
				for (int i = 0; i < DBData.length; i++) {
					//System.out.println(DBData[i][0] + " " + DBData[i][1]);

					try {
						Log.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DBData[i][0])) + " " + DBData[i][1]);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			String json_data = new Gson().toJson(Log);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else if (method.equals("getLostChart")) {
			//System.out.println("getLostChart");

			String address = request.getParameter("address");
			//System.out.println("address: " + address);
			String device = request.getParameter("device");
			//System.out.println(device);
			String startTime = request.getParameter("startTime");
			//System.out.println(startTime);
			String endTime = request.getParameter("endTime");
			//System.out.println(endTime);

			ArrayList<String[]> arrayList2 = new ArrayList<String[]>();

			Calendar calendar = Calendar.getInstance();

			String result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
			//System.out.println(result);

			arrayList2.add(new String[] { result, startTime, endTime });

			RemoteInterface Service = RemoteService.creatClient(address, 3);

			List<List<String[]>> test = new ArrayList<List<String[]>>();

			if (address == null) {
				test = JNetwork.getAliveStatus(new String[] { "*" }, "1", startTime, endTime, device);
			} else if (!address.equals("")) {
				if (Service != null) {
					try {
						test = Service.getAliveStatus(new String[] { "*" }, "1", startTime, endTime, device);
					} catch (RemoteException e) {
						System.out.println("test = Service.getAliveStatus(new String[] { \"*\" }, \"1\", startTime, endTime, device);\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}
				}
			}

			//System.out.println(test.size());
			if (test.size() == 2) {
				/*System.out.println(test.get(0).size());
				for (int i = 0; i < test.get(0).size(); i++) {
					System.out.println(test.get(0).get(i)[0]);
				}*/

				/*System.out.println(test.get(1).size());
				for (int i = 0; i < test.get(1).size(); i++) {
					System.out.println(test.get(1).get(i)[0] + ", " + test.get(1).get(i)[1] + ", " + test.get(1).get(i)[2] + ", " + test.get(1).get(i)[3] + ", " + test.get(1).get(i)[4]);
				}*/

				/*System.out.println(endTime_getTime - startTime_getTime);
				try {
					System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(endTime).getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(startTime).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}*/
			}

			String json_data = new Gson().toJson(test);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			try {
				response.getWriter().write(json_data);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else if (method.equals("getRemoteNMSListLocationAddress")) {
			//System.out.println("getRemoteNMSListLocationAddress");

			String address = request.getParameter("address");
			//System.out.println("address: " + address);

			if (!address.equals("")) {
				List<Object> AllList = new ArrayList<Object>();

				List<Map<String, String>> NMSListLocationAddress = new ArrayList<Map<String, String>>();

				RemoteInterface Service = RemoteService.creatClient(address, 3);

				if (Service != null) {
					try {
						NMSListLocationAddress = Service.getRemoteNMSListLocationAddress();
					} catch (RemoteException e) {
						System.out.println("NMSListLocationAddress = Service.getRemoteNMSListLocationAddress();\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}
				}

				AllList.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				AllList.add(NMSListLocationAddress);

				String json_data = new Gson().toJson(AllList);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				try {
					response.getWriter().write(json_data);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}
