package com.via.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JACAPInfo;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;

/**
 * Servlet implementation class ACOverview
 */
@WebServlet("/APList")
public class APList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APList() {
		super();
		// TODO Auto-generated constructor stub
	}

	// ====================================================================================================

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//udp
		String action = request.getParameter("action");
		System.out.println("action= " + action);

		String ip = request.getParameter("ip");

		String remote_address = request.getParameter("remote_address");
		String remote_device_ip = request.getParameter("remote_device_ip");

		if (action.indexOf("APList") != -1) {
			ArrayList<String[]> OnlineAPList = new ArrayList<String[]>();
			ArrayList<String[]> OnlineAPList_TMP = new ArrayList<String[]>();
			ArrayList<String[]> OfflineAPList = new ArrayList<String[]>();
			ArrayList<String[]> OfflineAPList_TMP = new ArrayList<String[]>();

			if (ip != null) {
				OnlineAPList_TMP = JACAPInfo.getOnlineAPList(ip);

				//for Sort
				if (OnlineAPList_TMP.size() > 0) {
					String[][] Dataarray = new String[OnlineAPList_TMP.size()][OnlineAPList_TMP.get(0).length];
					OnlineAPList_TMP.toArray(Dataarray);
					Arrays.sort(Dataarray, new Comparator<String[]>() {
						public int compare(final String[] entry1, final String[] entry2) {
							final String time1 = entry1[5];
							final String time2 = entry2[5];
							return time1.compareTo(time2);
						}
					});

					OnlineAPList = new ArrayList<String[]>();
					for (int s = 0; s < Dataarray.length; s++) {
						OnlineAPList.add(Dataarray[s]);
					}
				}

				if (OnlineAPList_TMP.size() > 0) {
					OfflineAPList_TMP = JACAPInfo.getOfflineAPList(ip);

					//for Sort
					if (OfflineAPList_TMP.size() > 0) {
						String[][] Dataarray = new String[OfflineAPList_TMP.size()][OfflineAPList_TMP.get(0).length];
						OfflineAPList_TMP.toArray(Dataarray);
						Arrays.sort(Dataarray, new Comparator<String[]>() {
							public int compare(final String[] entry1, final String[] entry2) {
								final String time1 = entry1[5];
								final String time2 = entry2[5];
								return time1.compareTo(time2);
							}
						});

						OfflineAPList = new ArrayList<String[]>();
						for (int s = 0; s < Dataarray.length; s++) {
							OfflineAPList.add(Dataarray[s]);
						}
					}
				}
			} else if (remote_address != null && remote_device_ip != null) {
				System.out.println("remote_address= " + remote_address);
				System.out.println("remote_device_ip= " + remote_device_ip);

				RemoteInterface Service = RemoteService.creatClient(remote_address, 3);

				if (Service != null) {
					try {
						OnlineAPList_TMP = Service.getOnlineAPList(remote_device_ip);
					} catch (RemoteException e) {
						System.out.println("OnlineAPList_TMP = Service.getOnlineAPList(remote_device_ip);\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}

					//for Sort
					if (OnlineAPList_TMP.size() > 0) {
						String[][] Dataarray = new String[OnlineAPList_TMP.size()][OnlineAPList_TMP.get(0).length];
						OnlineAPList_TMP.toArray(Dataarray);
						Arrays.sort(Dataarray, new Comparator<String[]>() {
							public int compare(final String[] entry1, final String[] entry2) {
								final String time1 = entry1[5];
								final String time2 = entry2[5];
								return time1.compareTo(time2);
							}
						});

						for (int s = 0; s < Dataarray.length; s++) {
							OnlineAPList.add(Dataarray[s]);
						}
					}

					if (OnlineAPList_TMP.size() > 0) {
						try {
							OfflineAPList_TMP = Service.getOfflineAPList(remote_device_ip);
						} catch (RemoteException e) {
							System.out.println("OfflineAPList_TMP = Service.getOfflineAPList(remote_device_ip);\nRemoteException:" + e.getMessage());
							e.printStackTrace();
						}

						//for Sort
						if (OfflineAPList_TMP.size() > 0) {
							String[][] Dataarray = new String[OfflineAPList_TMP.size()][OfflineAPList_TMP.get(0).length];
							OfflineAPList_TMP.toArray(Dataarray);
							Arrays.sort(Dataarray, new Comparator<String[]>() {
								public int compare(final String[] entry1, final String[] entry2) {
									final String time1 = entry1[5];
									final String time2 = entry2[5];
									return time1.compareTo(time2);
								}
							});

							for (int s = 0; s < Dataarray.length; s++) {
								OfflineAPList.add(Dataarray[s]);
							}
						}
					}
				}
			}

			request.setAttribute("OnlineAPList", OnlineAPList);
			request.setAttribute("OnlineAPList_size", OnlineAPList.size());
			request.setAttribute("OfflineAPList", OfflineAPList);
			request.setAttribute("OfflineAPList_size", OfflineAPList.size());
		} else if (action.indexOf("ap_realtime_chart") != -1) {
			if (ip != null) {

				ArrayList<String[]> OnlineAPList = JACAPInfo.getOnlineAPList(ip);

				ArrayList<String[]> APNameIPList = new ArrayList<String[]>();
				ArrayList<String[]> APSSIDNameRadioTypeIndexList = new ArrayList<String[]>();

				if (OnlineAPList.size() > 0) {
					//for Sort
					String[][] Dataarray = new String[OnlineAPList.size()][OnlineAPList.get(0).length];
					OnlineAPList.toArray(Dataarray);
					Arrays.sort(Dataarray, new Comparator<String[]>() {
						public int compare(final String[] entry1, final String[] entry2) {
							final String time1 = entry1[5];
							final String time2 = entry2[5];
							return time1.compareTo(time2);
						}
					});

					ArrayList<String[]> List = new ArrayList<String[]>();
					for (int s = 0; s < Dataarray.length; s++) {
						List.add(Dataarray[s]);
					}

					OnlineAPList = List;

					ArrayList<String[]> APSSIDList = JACAPInfo.getAPSSIDList(ip);

					for (int i = 0; i < OnlineAPList.size(); i++) {
						ArrayList<String> items = new ArrayList<String>();
						//APName、APNameIP
						//System.out.println(APList.get(i)[2] + "、" + APList.get(i)[5]);
						items.add(OnlineAPList.get(i)[2]);
						items.add(OnlineAPList.get(i)[5]);

						String[] StringToArray = new String[items.size()];

						StringToArray = items.toArray(StringToArray);

						APNameIPList.add(StringToArray);

						items = new ArrayList<String>();
						for (int j = 0; j < APSSIDList.size(); j++) {
							if (APSSIDList.get(j)[0].trim().equals(OnlineAPList.get(i)[5].trim())) {
								//SSIDName&SSIDRadioType&SSIDIndex
								//System.out.println(APSSIDList.get(j)[3] + "&" + APSSIDList.get(j)[4] + "&" + APSSIDList.get(j)[2]);
								items.add(APSSIDList.get(j)[3] + "&" + APSSIDList.get(j)[4] + "&" + APSSIDList.get(j)[2]);
							}
						}

						StringToArray = new String[items.size()];

						StringToArray = items.toArray(StringToArray);

						APSSIDNameRadioTypeIndexList.add(StringToArray);
					}

					/*for (int i = 0; i < APNameIPList.size(); i++) {
						//APName、APNameIP
						System.out.println(APNameIPList.get(i)[0] + "、" + APNameIPList.get(i)[1]);

						for (int j = 0; j < APSSIDNameRadioTypeIndexList.get(i).length; j++) {
							//SSIDName&SSIDRadioType&SSIDIndex
							System.out.println(APSSIDNameRadioTypeIndexList.get(i)[j]);
						}
					}*/
				}

				request.setAttribute("data", APNameIPList);
				request.setAttribute("data2", APSSIDNameRadioTypeIndexList);
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}
}
