package com.via.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class JACAPInfo {
	static OutputStream out;

	public static void acapsocketsend(String mess) throws IOException {
		byte b[] = new byte[1024];
		b = mess.getBytes("UTF-8");
		out.write(b);
	}

	public static ArrayList<String[]> getFriendlyAPList(final String acip) {
		System.out.println("Get " + acip + " Friendly AP List");

		int TIMEOUT = 10000; //设置接收数据的超时时间
		int MAXNUM = 3; //设置重发数据的最多次数

		//Friendly AP List
		String str_send = "viamanagAPFriendlyAPStart";
		byte[] buf = new byte[65507];

		//系統配給客户端閒置端口(0)或指定(端口號)监听接收到的数据
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			System.out.println("ds = new DatagramSocket(0);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		}

		InetAddress loc = null;
		try {
			loc = InetAddress.getByName(acip);
		} catch (UnknownHostException e) {
			System.out.println("loc = InetAddress.getByName(acip);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		}

		//定义用来发送数据的DatagramPacket实例
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, 8888);

		//定义用来接收数据的DatagramPacket实例
		DatagramPacket dp_receive = new DatagramPacket(buf, 65507);

		//数据发向目的8888端口
		try {
			ds.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ds.setSoTimeout(TIMEOUT);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		} //设置接收数据时阻塞的最长时间
		int tries = 0; //重发数据的次数
		boolean receivedResponse = false; //是否接收到数据的标志位
		//直到接收到数据，或者重发次数达到预定值，则退出循环

		ArrayList<String> List = new ArrayList<String>();

		out: {
			while (tries < MAXNUM) {
				//发送数据
				try {
					ds.send(dp_send);
				} catch (IOException e) {
					System.out.println("ds.send(dp_send);\nIOException:" + e.getMessage());
					e.printStackTrace();
					break out;
				} catch (Exception e) {
					System.out.println("ds.send(dp_send);\nException:" + e.getMessage());
					e.printStackTrace();
					break out;
				}

				try {
					while (true) {
						//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
						//所以这里要将dp_receive的内部消息长度重新置为65507
						dp_receive.setLength(65507);

						//接收从服务端发送回来的数据
						ds.receive(dp_receive);

						//如果接收到的数据不是来自目标地址，则抛出异常
						if (!dp_receive.getAddress().equals(loc)) {
							System.out.println("Received packet from an umknown source(" + dp_receive.getAddress().toString().split("/")[1] + ")");

							break out;
						}

						//如果收到数据，则打印出来
						String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
						if (str_receive != "") {
							//如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
							receivedResponse = true;

							if (str_receive.indexOf("viamanagAPFriendlyAPEnd") != -1) {
								//System.out.println(str_receive);

								break out;
							}

							if (str_receive.indexOf("viamanagAPFriendlyAPStart") != -1) {
								//System.out.println(str_receive);
								//收到的格式
								//viamanagAPFriendlyAPStart BSSID=00:40:63:12:02:60&&ESSID="ssid1"&&SecurityMode=WPA2 802.1X(TKIP,CCMP)

								List.add(str_receive);
							}
						} else {
							break out;
						}
					}
				} catch (InterruptedIOException e) {
					System.out.println("while (true) {\nInterruptedIOException:" + e.getMessage());
					e.printStackTrace();
					
					//如果接收数据时阻塞超时，重发并减少一次重发的次数
					tries += 1;
					System.out.println("Time out, " + (MAXNUM - tries) + " more tries...");
					List.clear();
					receivedResponse = false; //是否接收到数据的标志位
				} catch (IOException e) {
					System.out.println("while (true) {\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ds.close();

		ArrayList<String[]> FriendlyAPList = new ArrayList<String[]>();

		int friendlyapNumIndex = 0;

		for (int i = 0; i < List.size(); i++) {
			//System.out.println("AP:" + List.get(i).split("\n").length);

			for (int j = 0; j < List.get(i).split("\n").length; j++) {
				try {
					ArrayList<String> items = new ArrayList<String>();

					if (List.get(i).split("\n")[j].split("&&").length > 2 && List.get(i).split("\n")[j].split("&&")[2].split("=").length > 1) {
						//System.out.println(List.get(i).split("\n")[j]);

						items.add("");
						items.add("");
						items.add(List.get(i).split("\n")[j].split("&&")[0].split("=")[1]);
						items.add(List.get(i).split("\n")[j].split("&&")[1].split("=")[1].replace("\"", ""));
						items.add(List.get(i).split("\n")[j].split("&&")[2].split("=")[1]);

						String[] StringToArray = new String[items.size()];

						StringToArray = items.toArray(StringToArray);

						FriendlyAPList.add(StringToArray);

						friendlyapNumIndex++;
					}
				} catch (Exception e) {
					System.out.println("items.add\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		System.out.println("friendlyapNumIndex: " + friendlyapNumIndex);

		if (receivedResponse) {
			System.out.println("Friendly AP List response.");
		} else {
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("Friendly AP List no response -- give up.");
		}

		return FriendlyAPList;
	}

	public static ArrayList<String[]> getRogueAPList(final String acip) {
		System.out.println("Get " + acip + " Rogue AP List");

		int TIMEOUT = 10000; //设置接收数据的超时时间
		int MAXNUM = 3; //设置重发数据的最多次数

		//Rogue AP List
		String str_send = "viamanagAPRogueAPStart";
		byte[] buf = new byte[65507];

		//系統配給客户端閒置端口(0)或指定(端口號)监听接收到的数据
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			System.out.println("ds = new DatagramSocket(0);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		}

		InetAddress loc = null;
		try {
			loc = InetAddress.getByName(acip);
		} catch (UnknownHostException e) {
			System.out.println("loc = InetAddress.getByName(acip);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		}

		//定义用来发送数据的DatagramPacket实例
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, 8888);

		//定义用来接收数据的DatagramPacket实例
		DatagramPacket dp_receive = new DatagramPacket(buf, 65507);

		//数据发向目的8888端口
		try {
			ds.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ds.setSoTimeout(TIMEOUT);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		} //设置接收数据时阻塞的最长时间
		int tries = 0; //重发数据的次数
		boolean receivedResponse = false; //是否接收到数据的标志位
		//直到接收到数据，或者重发次数达到预定值，则退出循环

		ArrayList<String> List = new ArrayList<String>();

		out: {
			while (tries < MAXNUM) {
				//发送数据
				try {
					ds.send(dp_send);
				} catch (IOException e) {
					System.out.println("ds.send(dp_send);\nIOException:" + e.getMessage());
					e.printStackTrace();
					break out;
				} catch (Exception e) {
					System.out.println("ds.send(dp_send);\nException:" + e.getMessage());
					e.printStackTrace();
					break out;
				}

				try {
					while (true) {
						//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
						//所以这里要将dp_receive的内部消息长度重新置为65507
						dp_receive.setLength(65507);

						//接收从服务端发送回来的数据
						ds.receive(dp_receive);

						//如果接收到的数据不是来自目标地址，则抛出异常
						if (!dp_receive.getAddress().equals(loc)) {
							System.out.println("Received packet from an umknown source(" + dp_receive.getAddress().toString().split("/")[1] + ")");

							break out;
						}

						//如果收到数据，则打印出来
						String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
						if (str_receive != "") {
							//如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
							receivedResponse = true;

							if (str_receive.indexOf("viamanagAPRogueAPEnd") != -1) {
								//System.out.println(str_receive);

								break out;
							}

							if (str_receive.indexOf("viamanagAPRogueAPStart") != -1) {
								//System.out.println(str_receive);
								//收到的格式
								//viamanagAPRogueAPStart BSSID=00:40:63:11:e1:92&&ESSID="VIA_Visitors"&&SecurityMode=WPA PSK (TKIP)

								List.add(str_receive);
							}
						} else {
							break out;
						}
					}
				} catch (InterruptedIOException e) {
					System.out.println("while (true) {\nInterruptedIOException:" + e.getMessage());

					//如果接收数据时阻塞超时，重发并减少一次重发的次数
					tries += 1;
					System.out.println("Time out, " + (MAXNUM - tries) + " more tries...");
					List.clear();
					receivedResponse = false; //是否接收到数据的标志位
				} catch (IOException e) {
					System.out.println("while (true) {\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ds.close();

		ArrayList<String[]> RogueAPList = new ArrayList<String[]>();

		int rogueapNumIndex = 0;

		for (int i = 0; i < List.size(); i++) {
			//System.out.println("AP:" + List.get(i).split("\n").length);

			for (int j = 0; j < List.get(i).split("\n").length; j++) {
				try {
					ArrayList<String> items = new ArrayList<String>();
					//System.out.println(List.get(i).split("\n")[j]);

					items.add("");
					items.add("");
					items.add(List.get(i).split("\n")[j].split("&&")[0].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split("&&")[1].split("=")[1].replace("\"", ""));
					items.add(List.get(i).split("\n")[j].split("&&")[2].split("=")[1]);

					String[] StringToArray = new String[items.size()];

					StringToArray = items.toArray(StringToArray);

					RogueAPList.add(StringToArray);

					rogueapNumIndex++;
				} catch (Exception e) {
					System.out.println("items.add\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		System.out.println("rogueapNumIndex: " + rogueapNumIndex);

		if (receivedResponse) {
			System.out.println("Rogue AP List response.");
		} else {
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("Rogue AP List no response -- give up.");
		}

		return RogueAPList;
	}

	public static ArrayList<String[]> getOnlineAPList(final String acip) {
		System.out.println("Get " + acip + " Online AP List");

		int TIMEOUT = 10000; //设置接收数据的超时时间
		int MAXNUM = 3; //设置重发数据的最多次数

		//Online AP List
		String str_send = "viamanagAPListRequestStart";
		byte[] buf = new byte[65507];

		//系統配給客户端閒置端口(0)或指定(端口號)监听接收到的数据
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			System.out.println("ds = new DatagramSocket(0);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		}

		InetAddress loc = null;
		try {
			loc = InetAddress.getByName(acip);
		} catch (UnknownHostException e) {
			System.out.println("loc = InetAddress.getByName(acip);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		}

		//定义用来发送数据的DatagramPacket实例
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, 8888);

		//定义用来接收数据的DatagramPacket实例
		DatagramPacket dp_receive = new DatagramPacket(buf, 65507);

		//数据发向目的8888端口
		try {
			ds.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ds.setSoTimeout(TIMEOUT);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		} //设置接收数据时阻塞的最长时间
		int tries = 0; //重发数据的次数
		boolean receivedResponse = false; //是否接收到数据的标志位
		//直到接收到数据，或者重发次数达到预定值，则退出循环

		ArrayList<String> List = new ArrayList<String>();

		out: {
			while (tries < MAXNUM) {
				//发送数据
				try {
					ds.send(dp_send);
				} catch (IOException e) {
					System.out.println("ds.send(dp_send);\nIOException:" + e.getMessage());
					e.printStackTrace();
					break out;
				} catch (Exception e) {
					System.out.println("ds.send(dp_send);\nException:" + e.getMessage());
					e.printStackTrace();
					break out;
				}

				try {
					while (true) {
						//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
						//所以这里要将dp_receive的内部消息长度重新置为65507
						dp_receive.setLength(65507);

						//接收从服务端发送回来的数据
						ds.receive(dp_receive);

						//如果接收到的数据不是来自目标地址，则抛出异常
						if (!dp_receive.getAddress().equals(loc)) {
							System.out.println("Received packet from an umknown source(" + dp_receive.getAddress().toString().split("/")[1] + ")");

							break out;
						}

						//如果收到数据，则打印出来
						String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
						if (str_receive != "") {
							//如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
							receivedResponse = true;

							if (str_receive.indexOf("viamanagAPListRequestEnd") != -1) {
								//System.out.println(str_receive);

								break out;
							}

							if (str_receive.indexOf("viamanagAPListRequestStart") != -1) {
								//System.out.println(str_receive);
								//收到的格式
								//viamanagAPListRequestStart APName=VNT5901 Model=VN5901 IPAddress=192.168.2.80 Netmask=255.255.0.0 Gateway=192.168.1.1 MACAddress=00:12:7b:81:1f:cf Channel=11/149 Status=managed Profile=nms_test Group=default FirmwareVersion=1.03.52.20

								List.add(str_receive);
							}
						} else {
							break out;
						}
					}
				} catch (InterruptedIOException e) {
					System.out.println("while (true) {\nInterruptedIOException:" + e.getMessage());

					//如果接收数据时阻塞超时，重发并减少一次重发的次数
					tries += 1;
					System.out.println("Time out, " + (MAXNUM - tries) + " more tries...");
					List.clear();
					receivedResponse = false; //是否接收到数据的标志位
				} catch (IOException e) {
					System.out.println("while (true) {\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ds.close();

		ArrayList<String[]> OnlineAPList = new ArrayList<String[]>();

		int onlineapNumIndex = 0;

		for (int i = 0; i < List.size(); i++) {
			//System.out.println("AP:" + List.get(i).split("\n").length);

			for (int j = 0; j < List.get(i).split("\n").length; j++) {

				try {
					ArrayList<String> items = new ArrayList<String>();

					//System.out.println(List.get(i).split("\n")[j]);

					items.add("");
					items.add("");
					if (List.get(i).split("\n")[j].split(" ")[1].split("=").length > 1) {
						items.add(List.get(i).split("\n")[j].split(" ")[1].split("=")[1]);
					} else {
						items.add("");
					}
					items.add(List.get(i).split("\n")[j].split(" ")[2].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[11].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[3].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[6].split("=")[1].toUpperCase());
					items.add(List.get(i).split("\n")[j].split(" ")[7].split("=")[1].split("/")[0]);
					items.add(List.get(i).split("\n")[j].split(" ")[7].split("=")[1].split("/")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[8].split("=")[1]);
					if (List.get(i).split("\n")[j].split(" ")[9].split("=").length > 1) {
						items.add(List.get(i).split("\n")[j].split(" ")[9].split("=")[1]);
					} else {
						items.add("");
					}
					if (List.get(i).split("\n")[j].split(" ")[10].split("=").length > 1) {
						items.add(List.get(i).split("\n")[j].split(" ")[10].split("=")[1]);
					} else {
						items.add("");
					}
					items.add("online");

					String[] StringToArray = new String[items.size()];

					StringToArray = items.toArray(StringToArray);

					OnlineAPList.add(StringToArray);

					onlineapNumIndex++;
				} catch (Exception e) {
					System.out.println("items.add\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		System.out.println("onlineapNumIndex: " + onlineapNumIndex);

		if (receivedResponse) {
			System.out.println("Online AP List response.");
		} else {
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("Online AP List no response -- give up.");
		}

		return OnlineAPList;
	}

	public static ArrayList<String[]> getOfflineAPList(final String acip) {
		System.out.println("Get " + acip + " Offline AP List");

		int TIMEOUT = 10000; //设置接收数据的超时时间
		int MAXNUM = 3; //设置重发数据的最多次数

		//Login AP List
		String str_send = "viamanagLoginApStart";
		byte[] buf = new byte[65507];

		//系統配給客户端閒置端口(0)或指定(端口號)监听接收到的数据
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			System.out.println("ds = new DatagramSocket(0);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		}

		InetAddress loc = null;
		try {
			loc = InetAddress.getByName(acip);
		} catch (UnknownHostException e) {
			System.out.println("loc = InetAddress.getByName(acip);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		}

		//定义用来发送数据的DatagramPacket实例
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, 8888);

		//定义用来接收数据的DatagramPacket实例
		DatagramPacket dp_receive = new DatagramPacket(buf, 65507);

		//数据发向目的8888端口
		try {
			ds.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ds.setSoTimeout(TIMEOUT);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		} //设置接收数据时阻塞的最长时间
		int tries = 0; //重发数据的次数
		boolean receivedResponse = false; //是否接收到数据的标志位
		//直到接收到数据，或者重发次数达到预定值，则退出循环

		ArrayList<String> List = new ArrayList<String>();

		out: {
			while (tries < MAXNUM) {
				//发送数据
				try {
					ds.send(dp_send);
				} catch (IOException e) {
					System.out.println("ds.send(dp_send);\nIOException:" + e.getMessage());
					e.printStackTrace();
					break out;
				} catch (Exception e) {
					System.out.println("ds.send(dp_send);\nException:" + e.getMessage());
					e.printStackTrace();
					break out;
				}

				try {
					while (true) {
						//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
						//所以这里要将dp_receive的内部消息长度重新置为65507
						dp_receive.setLength(65507);

						//接收从服务端发送回来的数据
						ds.receive(dp_receive);

						//如果接收到的数据不是来自目标地址，则抛出异常
						if (!dp_receive.getAddress().equals(loc)) {
							System.out.println("Received packet from an umknown source(" + dp_receive.getAddress().toString().split("/")[1] + ")");

							break out;
						}

						//如果收到数据，则打印出来
						String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
						if (str_receive != "") {
							//如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
							receivedResponse = true;

							if (str_receive.indexOf("viamanagLoginApEnd") != -1) {
								//System.out.println(str_receive);

								break out;
							}

							if (str_receive.indexOf("viamanagLoginApStart") != -1) {
								//System.out.println(str_receive);
								//收到的格式
								//viamanagLoginApStart line= 00:12:7b:81:0f:f0,AP001,VN5901,1.03.52.20,192.168.0.112,1,24tw,default

								List.add(str_receive);
							}
						} else {
							break out;
						}
					}
				} catch (InterruptedIOException e) {
					System.out.println("while (true) {\nInterruptedIOException:" + e.getMessage());

					//如果接收数据时阻塞超时，重发并减少一次重发的次数
					tries += 1;
					System.out.println("Time out, " + (MAXNUM - tries) + " more tries...");
					List.clear();
					receivedResponse = false; //是否接收到数据的标志位
				} catch (IOException e) {
					System.out.println("while (true) {\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ds.close();

		ArrayList<String[]> OfflineAPList = new ArrayList<String[]>();

		int offlineapNumIndex = 0;

		ArrayList<String[]> OnlineAPList = JACAPInfo.getOnlineAPList(acip);

		for (int i = 0; i < List.size(); i++) {
			//System.out.println("AP:" + List.get(i).split("\n").length);

			for (int j = 0; j < List.get(i).split("\n").length; j++) {
				try {
					ArrayList<String> items = new ArrayList<String>();

					//System.out.println(List.get(i).split("\n")[j]);

					items.add("");
					items.add("");
					items.add(List.get(i).split("\n")[j].split(" ")[2].split(",")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[2].split(",")[2]);
					items.add(List.get(i).split("\n")[j].split(" ")[2].split(",")[3]);
					items.add(List.get(i).split("\n")[j].split(" ")[2].split(",")[4]);
					items.add(List.get(i).split("\n")[j].split(" ")[2].split(",")[0].toUpperCase());
					items.add("");
					items.add("");

					if (Integer.parseInt(List.get(i).split("\n")[j].split(" ")[2].split(",")[5]) == 1) {
						items.add("managed");
					} else {
						items.add("unknown");
					}
					items.add(List.get(i).split("\n")[j].split(" ")[2].split(",")[6]);
					items.add(List.get(i).split("\n")[j].split(" ")[2].split(",")[7]);
					items.add("offline");

					String[] StringToArray = new String[items.size()];

					StringToArray = items.toArray(StringToArray);

					int same = 0;
					if (OnlineAPList.size() == 0) {
						//System.out.println(List.get(i).split("\n")[j]);
						//System.out.println(List.get(i).split("\n")[j].split(" ")[2].split(",")[4] + " Offline");

						OfflineAPList.add(StringToArray);

						offlineapNumIndex++;
					} else {
						for (int k = 0; k < OnlineAPList.size(); k++) {
							//System.out.println(List.get(i).split("\n")[j].split(" ")[2].split(",")[4]);

							//找多的==>少.indexOf(多)==>如沒有就是多的same++
							//或是用equals，用MAC較準
							//if (OnlineAPList.get(k)[6].toUpperCase().indexOf(List.get(i).split("\n")[j].split(" ")[2].split(",")[0].toUpperCase()) != -1) {
							if (OnlineAPList.get(k)[6].toUpperCase().equals(List.get(i).split("\n")[j].split(" ")[2].split(",")[0].toUpperCase())) {
								//System.out.println("same:" + OnlineAPList.get(k)[6].toUpperCase());
								same++;
							}
						}

						//紀錄多的						
						if (same == 0) {
							//System.out.println(List.get(i).split("\n")[j].split(" ")[2].split(",")[0].toUpperCase() + " Offline");

							OfflineAPList.add(StringToArray);

							offlineapNumIndex++;
						}
					}
				} catch (Exception e) {
					System.out.println("items.add\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		System.out.println("offlineapNumIndex: " + offlineapNumIndex);

		if (receivedResponse) {
			System.out.println("Offline AP List response.");
		} else {
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("Offline AP List no response -- give up.");
		}

		return OfflineAPList;
	}

	public static ArrayList<String[]> getAPSSIDList(final String acip) {
		System.out.println("Get " + acip + " APSSID List");

		int TIMEOUT = 10000; //设置接收数据的超时时间
		int MAXNUM = 3; //设置重发数据的最多次数

		//APSSID List
		String str_send = "viamanagAPSsidListRequestStart";
		byte[] buf = new byte[65507];

		//系統配給客户端閒置端口(0)或指定(端口號)监听接收到的数据
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			System.out.println("ds = new DatagramSocket(0);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		}

		InetAddress loc = null;
		try {
			loc = InetAddress.getByName(acip);
		} catch (UnknownHostException e) {
			System.out.println("loc = InetAddress.getByName(acip);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		}

		//定义用来发送数据的DatagramPacket实例
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, 8888);

		//定义用来接收数据的DatagramPacket实例
		DatagramPacket dp_receive = new DatagramPacket(buf, 65507);

		//数据发向目的8888端口
		try {
			ds.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ds.setSoTimeout(TIMEOUT);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		} //设置接收数据时阻塞的最长时间
		int tries = 0; //重发数据的次数
		boolean receivedResponse = false; //是否接收到数据的标志位
		//直到接收到数据，或者重发次数达到预定值，则退出循环

		ArrayList<String> List = new ArrayList<String>();

		out: {
			while (tries < MAXNUM) {
				//发送数据
				try {
					ds.send(dp_send);
				} catch (IOException e) {
					System.out.println("ds.send(dp_send);\nIOException:" + e.getMessage());
					e.printStackTrace();
					break out;
				} catch (Exception e) {
					System.out.println("ds.send(dp_send);\nException:" + e.getMessage());
					e.printStackTrace();
					break out;
				}

				try {
					while (true) {
						//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
						//所以这里要将dp_receive的内部消息长度重新置为65507
						dp_receive.setLength(65507);

						//接收从服务端发送回来的数据
						ds.receive(dp_receive);

						//如果接收到的数据不是来自目标地址，则抛出异常
						if (!dp_receive.getAddress().equals(loc)) {
							System.out.println("Received packet from an umknown source(" + dp_receive.getAddress().toString().split("/")[1] + ")");

							break out;
						}

						//如果收到数据，则打印出来
						String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
						if (str_receive != "") {
							//如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
							receivedResponse = true;

							if (str_receive.indexOf("viamanagAPSsidListRequestEnd") != -1) {
								//System.out.println(str_receive);

								break out;
							}

							if (str_receive.indexOf("viamanagAPSsidListRequestStart") != -1) {
								//System.out.println(str_receive);
								//收到的格式
								//viamanagAPSsidListRequestStart IPAddress=192.168.0.112 APName=AP001 SsidIndex=1 SsidName=24 RadioType=2.4G

								List.add(str_receive);
							}
						} else {
							break out;
						}
					}
				} catch (InterruptedIOException e) {
					System.out.println("while (true) {\nInterruptedIOException:" + e.getMessage());

					//如果接收数据时阻塞超时，重发并减少一次重发的次数
					tries += 1;
					System.out.println("Time out, " + (MAXNUM - tries) + " more tries...");
					List.clear();
					receivedResponse = false; //是否接收到数据的标志位
				} catch (IOException e) {
					System.out.println("while (true) {\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ds.close();

		ArrayList<String[]> APSSIDList = new ArrayList<String[]>();

		int apssidNumIndex = 0;

		for (int i = 0; i < List.size(); i++) {
			//System.out.println("AP:" + List.get(i).split("\n").length);

			for (int j = 0; j < List.get(i).split("\n").length; j++) {
				try {
					ArrayList<String> items = new ArrayList<String>();

					//System.out.println(List.get(i).split("\n")[j]);

					items.add(List.get(i).split("\n")[j].split(" ")[1].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[2].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[3].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[4].split("=")[1]);
					items.add(List.get(i).split("\n")[j].split(" ")[5].split("=")[1]);

					String[] StringToArray = new String[items.size()];

					StringToArray = items.toArray(StringToArray);

					APSSIDList.add(StringToArray);

					apssidNumIndex++;
				} catch (Exception e) {
					System.out.println("items.add\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		System.out.println("apssidNumIndex: " + apssidNumIndex);

		if (receivedResponse) {
			System.out.println("APSSID List response.");
		} else {
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("APSSID List no response -- give up.");
		}

		return APSSIDList;
	}

	public static ArrayList<String[]> getClientList(final String acip) {
		System.out.println("Get " + acip + " Client List");

		int TIMEOUT = 10000; //设置接收数据的超时时间
		int MAXNUM = 3; //设置重发数据的最多次数

		//Client List
		String str_send = "viamanagClientListRequestStart";
		byte[] buf = new byte[65507];

		//系統配給客户端閒置端口(0)或指定(端口號)监听接收到的数据
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			System.out.println("ds = new DatagramSocket(0);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		}

		InetAddress loc = null;
		try {
			loc = InetAddress.getByName(acip);
		} catch (UnknownHostException e) {
			System.out.println("loc = InetAddress.getByName(acip);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		}

		//定义用来发送数据的DatagramPacket实例
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, 8888);

		//定义用来接收数据的DatagramPacket实例
		DatagramPacket dp_receive = new DatagramPacket(buf, 65507);

		//数据发向目的8888端口
		try {
			ds.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ds.setSoTimeout(TIMEOUT);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		} //设置接收数据时阻塞的最长时间
		int tries = 0; //重发数据的次数
		boolean receivedResponse = false; //是否接收到数据的标志位
		//直到接收到数据，或者重发次数达到预定值，则退出循环

		ArrayList<String> List = new ArrayList<String>();

		out: {
			while (tries < MAXNUM) {
				//发送数据
				try {
					ds.send(dp_send);
				} catch (IOException e) {
					System.out.println("ds.send(dp_send);\nIOException:" + e.getMessage());
					e.printStackTrace();
					break out;
				} catch (Exception e) {
					System.out.println("ds.send(dp_send);\nException:" + e.getMessage());
					e.printStackTrace();
					break out;
				}

				try {
					while (true) {
						//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
						//所以这里要将dp_receive的内部消息长度重新置为65507
						dp_receive.setLength(65507);

						//接收从服务端发送回来的数据
						ds.receive(dp_receive);

						//如果接收到的数据不是来自目标地址，则抛出异常
						if (!dp_receive.getAddress().equals(loc)) {
							System.out.println("Received packet from an umknown source(" + dp_receive.getAddress().toString().split("/")[1] + ")");

							break out;
						}

						//如果收到数据，则打印出来
						String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
						if (str_receive != "") {
							//如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
							receivedResponse = true;

							if (str_receive.indexOf("viamanagClientListRequestEnd") != -1) {
								//System.out.println(str_receive);

								break out;
							}

							if (str_receive.indexOf("viamanagClientListRequestStart") != -1) {
								//System.out.println(str_receive);
								//收到的格式
								//viamanagClientListRequestStart MACAddress=68:09:27:c4:74:74 IPAddress=192.168.0.56 SSID=FORNMS Signal=61 RXRate=54 TXRate=48 APNameIPAddress=FORNMS/192.168.0.251

								List.add(str_receive);
							}
						} else {
							break out;
						}
					}
				} catch (InterruptedIOException e) {
					System.out.println("while (true) {\nInterruptedIOException:" + e.getMessage());

					//如果接收数据时阻塞超时，重发并减少一次重发的次数
					tries += 1;
					System.out.println("Time out, " + (MAXNUM - tries) + " more tries...");
					List.clear();
					receivedResponse = false; //是否接收到数据的标志位
				} catch (IOException e) {
					System.out.println("while (true) {\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ds.close();

		ArrayList<String[]> ClientList = new ArrayList<String[]>();

		int clientNumIndex = 0;

		String Path = JSystem.projectSpace + "/AC/";
		//String Path = System.getProperty("user.home") + "/AC/";

		File clientiniFile = new File(Path + acip.replace(".", "") + ".ini");
		//File clientiniFile = new File("./resources/client.ini");
		//System.out.println(clientiniFile);
		Wini clientini = null;
		if (clientiniFile.exists()) {
			try {
				clientini = new Wini(clientiniFile);
			} catch (InvalidFileFormatException e) {
				System.out.println("Client List:clientini = new Wini(clientiniFile); ==> 發生 InvalidFileFormatException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Client List:clientini = new Wini(clientiniFile); ==> 發生 IOException");
				e.printStackTrace();
			}
		}

		File ouiiniFile = new File(JACAPInfo.class.getResource("/oui.ini").getFile());
		//System.out.println(ouiiniFile);
		Wini ouiini = null;
		//Download File						
		/*if (!ouiiniFile.exists()) {
			int bytesum = 0;
			int byteread = 0;

			URL url = new URL("http://kaichun.googlecode.com/svn/oui.ini");

			try {
				URLConnection conn = url.openConnection();
				InputStream inStream = conn.getInputStream();
				FileOutputStream fs = new FileOutputStream(ouiiniFile);

				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					//System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
			} catch (FileNotFoundException e) {
				System.out.println("Download http://kaichun.googlecode.com/svn/oui.ini ==> 發生 FileNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Download http://kaichun.googlecode.com/svn/oui.ini ==> 發生 IOException");
				e.printStackTrace();
			}
		}*/

		if (ouiiniFile.exists()) {
			try {
				ouiini = new Wini(ouiiniFile);
			} catch (InvalidFileFormatException e) {
				System.out.println("Client List:ouiini = new Wini(ouiiniFile); ==> 發生 InvalidFileFormatException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Client List:ouiini = new Wini(ouiiniFile); ==> 發生 IOException");
				e.printStackTrace();
			}
		}

		for (int i = 0; i < List.size(); i++) {
			//System.out.println("AP:" + List.get(i).split("\n").length);

			for (int j = 0; j < List.get(i).split("\n").length; j++) {

				try {
					ArrayList<String> items = new ArrayList<String>();

					//System.out.println(List.get(i).split("\n")[j]);
					if (List.get(i).split("\n")[j].split(" ")[2].split("=")[1].indexOf("0.0.0.0") == -1) {
						items.add("");
						items.add("");
						items.add(List.get(i).split("\n")[j].split(" ")[7].split("=")[1].split("/")[0]);
						items.add(List.get(i).split("\n")[j].split(" ")[7].split("=")[1].split("/")[1]);
						items.add("");
						items.add(List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase());
						items.add(List.get(i).split("\n")[j].split(" ")[2].split("=")[1]);
						items.add(List.get(i).split("\n")[j].split(" ")[3].split("=")[1]);
						items.add(List.get(i).split("\n")[j].split(" ")[4].split("=")[1]);
						items.add(List.get(i).split("\n")[j].split(" ")[5].split("=")[1]);
						items.add(List.get(i).split("\n")[j].split(" ")[6].split("=")[1]);

						String device_client = "";
						if (clientiniFile.exists()) {
							if (clientini.get(List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase(), "client", String.class) != null) {
								device_client = clientini.get(List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase(), "client", String.class);
								//System.out.println("device_client:" + device_client);
							}
						}
						items.add(device_client);

						String device_vendor = "";
						if (ouiiniFile.exists()) {
							if (ouiini.get(List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase().split(":")[0] + List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase().split(":")[1] + List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase().split(":")[2], "vendor", String.class) != null) {
								device_vendor = ouiini.get(List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase().split(":")[0] + List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase().split(":")[1] + List.get(i).split("\n")[j].split(" ")[1].split("=")[1].toUpperCase().split(":")[2], "vendor", String.class).replace("\"", "");
								//System.out.println("device_vendor:" + device_vendor);
							}
						}
						items.add(device_vendor);

						String[] StringToArray = new String[items.size()];

						StringToArray = items.toArray(StringToArray);

						ClientList.add(StringToArray);

						clientNumIndex++;
					}
				} catch (Exception e) {
					System.out.println("items.add\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		System.out.println("clientNumIndex: " + clientNumIndex);

		if (receivedResponse) {
			System.out.println("Client List response.");
		} else {
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("Client List no response -- give up.");
		}

		return ClientList;
	}

	public static ArrayList<String[]> getClientDeviceList(final String acip) {
		System.out.println("Get " + acip + " Client Device List");

		ArrayList<String[]> ClientDeviceList = new ArrayList<String[]>();

		String Path = JSystem.projectSpace + "/AC/";

		File ClientDeviceList_csv = new File(Path + acip.replace(".", "") + ".csv");

		if (ClientDeviceList_csv.exists()) {
			InputStreamReader fr = null;
			BufferedReader br = null;
			try {
				fr = new InputStreamReader(new FileInputStream(ClientDeviceList_csv), "UTF-8");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				System.out.println("fr = new InputStreamReader(new FileInputStream(ClientDeviceList_csv), \"UTF-8\")\nFileNotFoundException:" + e.getMessage());
				e.printStackTrace();
			}
			br = new BufferedReader(fr);
			String rec = null;
			try {
				while ((rec = br.readLine()) != null) {
					if (rec.split(",").length == 2) {
						String[] line_string = { rec.split(",")[0], rec.split(",")[1] };
						ClientDeviceList.add(line_string);
					}
				}
			} catch (IOException e) {
				System.out.println("while ((rec = br.readLine()) != null) {\nIOException:" + e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					fr.close();
				} catch (IOException e) {
					System.out.println("fr.close();\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("br.close();\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}

			if (ClientDeviceList.size() > 0) {
				String[][] Dataarray = new String[ClientDeviceList.size()][ClientDeviceList.get(0).length];
				ClientDeviceList.toArray(Dataarray);
				Arrays.sort(Dataarray, new Comparator<String[]>() {
					public int compare(final String[] entry1, final String[] entry2) {
						final String time1 = entry1[0];
						final String time2 = entry2[0];
						return time1.compareTo(time2);
					}
				});

				ClientDeviceList.clear();
				for (int s = 0; s < Dataarray.length; s++) {
					ClientDeviceList.add(Dataarray[s]);
				}
			}
		}

		return ClientDeviceList;
	}

	public static ArrayList<String> getGlobalInfo(final String acip) {
		System.out.println("Get " + acip + " Global Info");

		int TIMEOUT = 10000; //设置接收数据的超时时间
		int MAXNUM = 3; //设置重发数据的最多次数

		//Global Info
		String str_send = "viamanagGlobalInfoStart";
		byte[] buf = new byte[65507];

		//系統配給客户端閒置端口(0)或指定(端口號)监听接收到的数据
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			System.out.println("ds = new DatagramSocket(0);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		}

		InetAddress loc = null;
		try {
			loc = InetAddress.getByName(acip);
		} catch (UnknownHostException e) {
			System.out.println("loc = InetAddress.getByName(acip);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		}

		//定义用来发送数据的DatagramPacket实例
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, 8888);

		//定义用来接收数据的DatagramPacket实例
		DatagramPacket dp_receive = new DatagramPacket(buf, 65507);

		//数据发向目的8888端口
		try {
			ds.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			System.out.println("ds.setSoTimeout(TIMEOUT);\nSocketException:" + e.getMessage());
			e.printStackTrace();
		} //设置接收数据时阻塞的最长时间
		int tries = 0; //重发数据的次数
		boolean receivedResponse = false; //是否接收到数据的标志位
		//直到接收到数据，或者重发次数达到预定值，则退出循环

		ArrayList<String> List = new ArrayList<String>();

		out: {
			while (tries < MAXNUM) {
				//发送数据
				try {
					ds.send(dp_send);
				} catch (IOException e) {
					System.out.println("ds.send(dp_send);\nIOException:" + e.getMessage());
					e.printStackTrace();
					break out;
				} catch (Exception e) {
					System.out.println("ds.send(dp_send);\nException:" + e.getMessage());
					e.printStackTrace();
					break out;
				}

				try {
					while (true) {
						//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
						//所以这里要将dp_receive的内部消息长度重新置为65507
						dp_receive.setLength(65507);

						//接收从服务端发送回来的数据
						ds.receive(dp_receive);

						//如果接收到的数据不是来自目标地址，则抛出异常
						if (!dp_receive.getAddress().equals(loc)) {
							System.out.println("Received packet from an umknown source(" + dp_receive.getAddress().toString().split("/")[1] + ")");

							break out;
						}

						//如果收到数据，则打印出来
						String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
						if (str_receive != "") {
							//如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
							receivedResponse = true;

							if (str_receive.indexOf("viamanagGlobalInfoStart") != -1) {
								//System.out.println(str_receive);
								//收到的格式
								//viamanagGlobalInfoStart TotalAps=127 ManagedAps=127 ConnFailedAps=0 MaxManagedAps=300 TotalClients=0 80211anClients=0 80211bgnClients=0

								List.add(str_receive);

								break out;
							}
						} else {
							break out;
						}
					}
				} catch (InterruptedIOException e) {
					System.out.println("while (true) {\nInterruptedIOException:" + e.getMessage());

					//如果接收数据时阻塞超时，重发并减少一次重发的次数
					tries += 1;
					System.out.println("Time out, " + (MAXNUM - tries) + " more tries...");
					List.clear();
					receivedResponse = false; //是否接收到数据的标志位
				} catch (IOException e) {
					System.out.println("while (true) {\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ds.close();

		ArrayList<String> GlobalInfo = new ArrayList<String>();

		if (List.size() > 0) {
			if (List.get(0).split(" ").length == 8) {
				try {
					GlobalInfo.add(List.get(0).split(" ")[5].split("=")[1]);
					GlobalInfo.add(List.get(0).split(" ")[6].split("=")[1]);
					GlobalInfo.add(List.get(0).split(" ")[7].split("=")[1]);
				} catch (Exception e) {
					System.out.println("Global Info:GlobalInfo.add(List.get(0).split(\" \")[5].split(\"=\")[1]);\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (receivedResponse) {
			System.out.println("Global Info response.");
		} else {
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("Global Info no response -- give up.");
		}

		return GlobalInfo;
	}

	public static ArrayList<String> getACLogIndex(final String acip) {
		System.out.println("Get " + acip + " AC Log Index");

		String command = "CheckACLog;Poles_Time_Check;END;";
		//System.out.println(command);

		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(acip, 8001), 5000);
		} catch (Exception e) {
			System.out.println("socket.connect(new InetSocketAddress(acip, 8001), 5000);\nException:" + e.getMessage());
			e.printStackTrace();
		}

		String tmp = "";

		if (socket.isConnected()) {
			try {
				out = socket.getOutputStream();
			} catch (Exception e) {
				System.out.println("out = socket.getOutputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			try {
				acapsocketsend(command);
			} catch (Exception e) {
				System.out.println("acapsocketsend(command);\nException:" + e.getMessage());
				e.printStackTrace();
			}

			// Socket接收
			InputStream in = null;
			try {
				in = socket.getInputStream();
			} catch (Exception e) {
				System.out.println("in = socket.getInputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			BufferedInputStream inin;
			inin = new BufferedInputStream(in);
			int len = 0;
			byte[] bytes = null;

			while (true) {
				try {
					len = inin.available();
				} catch (Exception e) {
					System.out.println("len = inin.available();\nException:" + e.getMessage());
				}

				if (len > 0) {
					bytes = new byte[len];
					try {
						inin.read(bytes);
					} catch (Exception e) {
						System.out.println("inin.read(bytes);\nException:" + e.getMessage());
						e.printStackTrace();
					}
				}

				if (len != 0) {
					String value = null;

					try {
						value = new String(bytes, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						System.out.println("value = new String(bytes, \"UTF-8\");\nUnsupportedEncodingException:" + e.getMessage());
						e.printStackTrace();
					}

					//System.out.println(value);
					tmp = tmp + value;

					if (value.indexOf("END") != -1) {
						try {
							inin.close();
							in.close();
							out.close();
							socket.close();
						} catch (Exception e) {
							System.out.println("inin.close();\nException:" + e.getMessage());
							e.printStackTrace();
						}
						break;
					}
				}
			}

			try {
				inin.close();
				in.close();
				out.close();
				socket.close();
			} catch (Exception e) {
				System.out.println("inin.close();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			//System.out.println(tmp);

			//System.out.println(tmp.split("&&").length);
		}

		ArrayList<String> ACLogIndex = new ArrayList<String>();

		if (tmp.split("&&").length > 3 && Integer.parseInt(tmp.split("&&")[tmp.split("&&").length - 2]) > 0) {
			//System.out.println(tmp.split("&&").length - 3);
			//System.out.println(tmp.split("&&")[tmp.split("&&").length - 2]);
			//Log:ACLog.split("&&")[1] ~ ACLog.split("&&")[ACLog.split("&&").length - 2]
			for (int i = 1; i < tmp.split("&&").length - 2; i++) {
				try {
					//System.out.println(tmp.split("&&")[i].split(";")[1]);
					ACLogIndex.add(tmp.split("&&")[i].split(";")[1]);
				} catch (Exception e) {
					System.out.println("ACLogIndex.add(tmp.split(\"&&\")[i].split(\";\")[1]);\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return ACLogIndex;
	}

	public static String getACLog(final String acip, final String startTime, final String endTime) {
		System.out.println("Get " + acip + " AC Log");

		String command = "CheckACLog;Time_Check;" + startTime + ";" + endTime + ";END;";
		//System.out.println(command);

		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(acip, 8001), 5000);
		} catch (Exception e) {
			System.out.println("socket.connect(new InetSocketAddress(acip, 8001), 5000);\nException:" + e.getMessage());
			e.printStackTrace();
		}

		String tmp = "";

		if (socket.isConnected()) {
			try {
				out = socket.getOutputStream();
			} catch (Exception e) {
				System.out.println("out = socket.getOutputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			try {
				acapsocketsend(command);
			} catch (Exception e) {
				System.out.println("acapsocketsend(command);\nException:" + e.getMessage());
				e.printStackTrace();
			}

			// Socket接收
			InputStream in = null;
			try {
				in = socket.getInputStream();
			} catch (Exception e) {
				System.out.println("in = socket.getInputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			BufferedInputStream inin;
			inin = new BufferedInputStream(in);
			int len = 0;
			byte[] bytes = null;

			while (true) {
				try {
					len = inin.available();
				} catch (Exception e) {
					System.out.println("len = inin.available();\nException:" + e.getMessage());
					e.printStackTrace();
				}

				if (len > 0) {
					bytes = new byte[len];
					try {
						inin.read(bytes);
					} catch (Exception e) {
						System.out.println("inin.read(bytes);\nException:" + e.getMessage());
						e.printStackTrace();
					}
				}

				if (len != 0) {
					String value = null;

					try {
						value = new String(bytes, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						System.out.println("value = new String(bytes, \"UTF-8\");\nUnsupportedEncodingException:" + e.getMessage());
						e.printStackTrace();
					}

					//System.out.println(value);
					tmp = tmp + value;

					if (value.indexOf("END") != -1) {
						try {
							inin.close();
							in.close();
							out.close();
							socket.close();
						} catch (Exception e) {
							System.out.println("inin.close();\nException:" + e.getMessage());
							e.printStackTrace();
						}
						break;
					}
				}
			}

			try {
				inin.close();
				in.close();
				out.close();
				socket.close();
			} catch (Exception e) {
				System.out.println("inin.close();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			//System.out.println(tmp);

			//System.out.println(tmp.split("&&").length);
		}

		return tmp;
	}

	public static ArrayList<String> getAPSTALogIndex(final String acip) {
		System.out.println("Get " + acip + " APSTA Log Index");

		String command = "CheckApStaLog;Poles_Time_Check;END;";
		//System.out.println(command);

		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(acip, 8001), 5000);
		} catch (Exception e) {
			System.out.println("socket.connect(new InetSocketAddress(acip, 8001), 5000);\nException:" + e.getMessage());
			e.printStackTrace();
		}

		String tmp = "";

		if (socket.isConnected()) {
			try {
				out = socket.getOutputStream();
			} catch (Exception e) {
				System.out.println("out = socket.getOutputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			try {
				acapsocketsend(command);
			} catch (Exception e) {
				System.out.println("acapsocketsend(command);\nException:" + e.getMessage());
				e.printStackTrace();
			}

			// Socket接收
			InputStream in = null;
			try {
				in = socket.getInputStream();
			} catch (Exception e) {
				System.out.println("in = socket.getInputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			BufferedInputStream inin;
			inin = new BufferedInputStream(in);
			int len = 0;
			byte[] bytes = null;

			while (true) {
				try {
					len = inin.available();
				} catch (Exception e) {
					System.out.println("len = inin.available();\nException:" + e.getMessage());
					e.printStackTrace();
				}

				if (len > 0) {
					bytes = new byte[len];
					try {
						inin.read(bytes);
					} catch (Exception e) {
						System.out.println("inin.read(bytes);\nException:" + e.getMessage());
						e.printStackTrace();
					}
				}

				if (len != 0) {
					String value = null;

					try {
						value = new String(bytes, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						System.out.println("value = new String(bytes, \"UTF-8\");\nUnsupportedEncodingException:" + e.getMessage());
						e.printStackTrace();
					}

					//System.out.println(value);
					tmp = tmp + value;

					if (value.indexOf("END") != -1) {
						try {
							inin.close();
							in.close();
							out.close();
							socket.close();
						} catch (Exception e) {
							System.out.println("inin.close();\nException:" + e.getMessage());
							e.printStackTrace();
						}
						break;
					}
				}
			}

			try {
				inin.close();
				in.close();
				out.close();
				socket.close();
			} catch (Exception e) {
				System.out.println("inin.close();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			//System.out.println(tmp);

			//System.out.println(tmp.split("&&").length);
		}

		ArrayList<String> APSTALogIndex = new ArrayList<String>();

		if (tmp.split("&&").length > 3 && Integer.parseInt(tmp.split("&&")[tmp.split("&&").length - 2]) > 0) {
			//System.out.println(tmp.split("&&").length - 3);
			//System.out.println(tmp.split("&&")[tmp.split("&&").length - 2]);
			//Log:APSTALog.split("&&")[1] ~ APSTALog.split("&&")[APSTALog.split("&&").length - 2]
			for (int i = 1; i < tmp.split("&&").length - 2; i++) {
				try {
					//System.out.println(tmp.split("&&")[i].split(";")[0]);
					APSTALogIndex.add(tmp.split("&&")[i].split(";")[0]);
				} catch (Exception e) {
					System.out.println("APSTALogIndex.add(tmp.split(\"&&\")[i].split(\";\")[0]);\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return APSTALogIndex;
	}

	public static String getAPSTALog(final String acip, final String startTime, final String endTime) {
		System.out.println("Get " + acip + " APSTA Log");

		String command = "CheckApStaLog;Time_Check;" + startTime + ";" + endTime + ";END;";
		//System.out.println(command);

		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(acip, 8001), 5000);
		} catch (Exception e) {
			System.out.println("socket.connect(new InetSocketAddress(acip, 8001), 5000);\nException:" + e.getMessage());
			e.printStackTrace();
		}

		String tmp = "";

		if (socket.isConnected()) {
			try {
				out = socket.getOutputStream();
			} catch (Exception e) {
				System.out.println("out = socket.getOutputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			try {
				acapsocketsend(command);
			} catch (Exception e) {
				System.out.println("acapsocketsend(command);\nException:" + e.getMessage());
				e.printStackTrace();
			}

			// Socket接收
			InputStream in = null;
			try {
				in = socket.getInputStream();
			} catch (Exception e) {
				System.out.println("in = socket.getInputStream();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			BufferedInputStream inin;
			inin = new BufferedInputStream(in);
			int len = 0;
			byte[] bytes = null;

			while (true) {
				try {
					len = inin.available();
				} catch (Exception e) {
					System.out.println("len = inin.available();\nException:" + e.getMessage());
					e.printStackTrace();
				}

				if (len > 0) {
					bytes = new byte[len];
					try {
						inin.read(bytes);
					} catch (Exception e) {
						System.out.println("inin.read(bytes);\nException:" + e.getMessage());
						e.printStackTrace();
					}
				}

				if (len != 0) {
					String value = null;

					try {
						value = new String(bytes, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						System.out.println("value = new String(bytes, \"UTF-8\");\nUnsupportedEncodingException:" + e.getMessage());
						e.printStackTrace();
					}

					//System.out.println(value);
					tmp = tmp + value;

					if (value.indexOf("END") != -1) {
						try {
							inin.close();
							in.close();
							out.close();
							socket.close();
						} catch (Exception e) {
							System.out.println("inin.close();\nException:" + e.getMessage());
							e.printStackTrace();
						}
						break;
					}
				}
			}

			try {
				inin.close();
				in.close();
				out.close();
				socket.close();
			} catch (Exception e) {
				System.out.println("inin.close();\nException:" + e.getMessage());
				e.printStackTrace();
			}

			//System.out.println(tmp);

			//System.out.println(tmp.split("&&").length);
		}

		return tmp;
	}
}