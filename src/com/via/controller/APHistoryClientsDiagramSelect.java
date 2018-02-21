package com.via.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.via.model.JNetwork;
import com.via.model.JTools;

/**
 * Servlet implementation class HistoryChart
 */
@WebServlet("/APHistoryClientsDiagramSelect")
public class APHistoryClientsDiagramSelect extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APHistoryClientsDiagramSelect() {
		super();
		// TODO Auto-generated constructor stub
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Calendar calendar = Calendar.getInstance();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String timeSelect = request.getParameter("time_select");
		String chartType = request.getParameter("chart_type");
		String ip = request.getParameter("ip");
		String startdate = request.getParameter("startdate");
		String enddate = request.getParameter("enddate");
		String dateText = request.getParameter("dateText");
		Map<String, int[]> dataSet = new LinkedHashMap<String, int[]>();

		String Jsp_StartTime = dateText != null && !dateText.isEmpty() ? dateText + " 00:00:00.000" : startdate != null && !startdate.isEmpty() ? startdate + " 00:00:00.000" : null;
		String Jsp_EndTime = dateText != null && !dateText.isEmpty() ? dateText + " 23:59:59.999" : enddate != null && !enddate.isEmpty() ? enddate + " 23:59:59.999" : null;

		System.out.println(String.format("=====ip: %s, StartTime: %s, EndTime: %s, chart_type: %s", ip, Jsp_StartTime, Jsp_EndTime, chartType));

		ArrayList<String[]> myList = new ArrayList<String[]>();
		String[][] chartData = null;
		//月曆最早、最晚可選
		String DBFirstTime = sdf.format(calendar.getTime()) + " 00:00:00.000";
		String DBLastTime = sdf.format(calendar.getTime()) + " 00:00:00.000";

		String[][] DBData = network.getSTANumberHistoryByColumValue("ACIP", ip);
		ArrayList<String[]> ChartTypeData = new ArrayList<String[]>();
		if (DBData == null) {
			System.out.println("No Wireless Client Number History Data");

			chartData = new String[1][3];
			chartData[0][0] = sdf.format(calendar.getTime()) + " 00:00:00.000";
			chartData[0][1] = "0";
			chartData[0][2] = "0";
		} else {//依Total、an、bgn過濾存到ChartTypeData
			if (chartType.equals("Total")) {
				//System.out.println(chartType);

				for (int i = 0; i < DBData.length; i++) {
					//System.out.println(DBData[i][1]);//RECTIME
					//System.out.println(DBData[i][3]);//TOTAL
					//System.out.println(DBData[i][4]);//AN
					//System.out.println(DBData[i][5]);//BGN
					ChartTypeData.add(new String[] { DBData[i][1], DBData[i][3], DBData[i][3] });
				}
			} else if (chartType.equals("an")) {
				//System.out.println(chartType);

				for (int i = 0; i < DBData.length; i++) {
					//System.out.println(DBData[i][1]);//RECTIME
					//System.out.println(DBData[i][3]);//TOTAL
					//System.out.println(DBData[i][4]);//AN
					//System.out.println(DBData[i][5]);//BGN
					ChartTypeData.add(new String[] { DBData[i][1], DBData[i][4], DBData[i][4] });
				}
			} else if (chartType.equals("bgn")) {
				//System.out.println(chartType);

				for (int i = 0; i < DBData.length; i++) {
					//System.out.println(DBData[i][1]);//RECTIME
					//System.out.println(DBData[i][3]);//TOTAL
					//System.out.println(DBData[i][4]);//AN
					//System.out.println(DBData[i][5]);//BGN
					ChartTypeData.add(new String[] { DBData[i][1], DBData[i][5], DBData[i][5] });
				}
			} else if (chartType.equals("an & bgn")) {
				//System.out.println(chartType);

				for (int i = 0; i < DBData.length; i++) {
					//System.out.println(DBData[i][1]);//RECTIME
					//System.out.println(DBData[i][3]);//TOTAL
					//System.out.println(DBData[i][4]);//AN
					//System.out.println(DBData[i][5]);//BGN
					ChartTypeData.add(new String[] { DBData[i][1], DBData[i][4], DBData[i][5] });
				}
			}

			//月曆最早可選
			DBFirstTime = ChartTypeData.get(0)[0];
			if (timeSelect == null || timeSelect == "" || timeSelect.equals("day")) {// 預設頁
				//System.out.println("timeSelect == \"null\"、timeSelect == \"\" || timeSelect.equals(\"day\")");

				for (int i = 0; i < ChartTypeData.size(); i++) {
					//取DB內每行時間
					long DBTimeGetTime = 0;
					try {
						DBTimeGetTime = sdf.parse(ChartTypeData.get(i)[0]).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}

					//月曆最晚可選
					DBLastTime = ChartTypeData.get(i)[0];

					//前24小時以內
					if (DBTimeGetTime >= (new Date().getTime() - 86400000)) {
						myList.add(ChartTypeData.get(i));
					}

				}
			} else if (timeSelect.equals("monthly") || timeSelect.equals("choose_date")) {// 月歷單天、範圍
				//System.out.println("timeSelect == \"monthly\"、timeSelect == \"\" || timeSelect.equals(\"choose_date\")");

				for (int i = 0; i < ChartTypeData.size(); i++) {

					// 每行時間
					long DBTimeGetTime = 0;
					try {
						DBTimeGetTime = sdf.parse(ChartTypeData.get(i)[0]).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}

					//月曆最晚可選
					DBLastTime = ChartTypeData.get(i)[0];

					// 網頁送來的StartTime
					long Jsp_StartTimeGetTime = 0;
					try {
						Jsp_StartTimeGetTime = sdf.parse(Jsp_StartTime).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// 網頁送來的EndTime
					long Jsp_EndTimeGetTime = 0;
					try {
						Jsp_EndTimeGetTime = sdf.parse(Jsp_EndTime).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//Jsp_StartTimeGetTime<= DBTimeGetTime <=Jsp_EndTimeGetTime
					if (Jsp_StartTimeGetTime <= DBTimeGetTime && DBTimeGetTime <= Jsp_EndTimeGetTime) {
						myList.add(ChartTypeData.get(i));
					}
				}
			} else if (timeSelect.equals("week")) {
				//System.out.println("timeSelect.equals(\"week\")");

				for (int i = 0; i < ChartTypeData.size(); i++) {

					// 每行時間
					long DBTimeGetTime = 0;
					try {
						DBTimeGetTime = sdf.parse(ChartTypeData.get(i)[0]).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}

					//月曆最晚可選
					DBLastTime = ChartTypeData.get(i)[0];

					//前一週以內
					if (DBTimeGetTime >= (new Date().getTime() - 604800000)) {
						myList.add(ChartTypeData.get(i));
					}

				}
			} else if (timeSelect.equals("month")) {
				//System.out.println("timeSelect.equals(\"month\")");

				for (int i = 0; i < ChartTypeData.size(); i++) {

					// 每行時間
					long DBTimeGetTime = 0;
					try {
						DBTimeGetTime = sdf.parse(ChartTypeData.get(i)[0]).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}

					//月曆最晚可選
					DBLastTime = ChartTypeData.get(i)[0];

					calendar.add(Calendar.MONTH, -1);
					long monthtime = calendar.getTime().getTime();

					//前一個月以內
					if (DBTimeGetTime >= (new Date().getTime() - monthtime)) {
						myList.add(ChartTypeData.get(i));
					}
				}
			}

			// 存入chartData
			chartData = new String[myList.size()][3];
			for (int j = 0; j < myList.size(); j++) {
				for (int k = 0; k < myList.get(0).length; k++) {
					chartData[j][0] = myList.get(j)[0];
					chartData[j][1] = String.valueOf(Integer.parseInt(myList.get(j)[1]));
					chartData[j][2] = String.valueOf(Integer.parseInt(myList.get(j)[2]));
				}
			}
		}

		/*
				String tmp = null;
				JDbTable Data = null;
				Data = new JDbTable();
				// ACAP code start Section 1
				// AP Table 或 SSID 名稱
				String APTableName = null;
				if (chartType.equals("an & bgn")) {
					//System.out.println(chartType);

					JDbTable Data2 = null;
					APTableName = "GLOBAL_INFO_SAVE";
					Data2 = network.getAPList(APTableName);

					if (Data2 == null) {
						System.out.println("No 802.11 an & bgn Wireless Client Number History Data");
					} else {
						String[][] ssid = new String[Data2.getData().length][8];

						if (Data2.getData()[0][0] != "") {
							for (int i = 0; i < Data2.getData().length; i++) {
								// 時間
								ssid[i][0] = Data2.getData()[i][0];
								// RX bps/1024=RX kbps
								ssid[i][5] = Data2.getData()[i][3];
								// TX bps/1024=TX kbps
								ssid[i][7] = Data2.getData()[i][4];
							}
							Data.setData(ssid);
						}
					}
				} else if (chartType.equals("Total")) {
					// System.out.println(chartType);

					JDbTable Data2 = null;
					APTableName = "GLOBAL_INFO_SAVE";
					Data2 = network.getAPList(APTableName);

					if (Data2 == null) {
						System.out.println("Data2==null");
					} else {
						String[][] ssid = new String[Data2.getData().length][8];

						if (Data2.getData()[0][0] != "") {
							for (int i = 0; i < Data2.getData().length; i++) {
								// 時間
								ssid[i][0] = Data2.getData()[i][0];
								// RX bps/1024=RX kbps
								ssid[i][5] = Data2.getData()[i][2];
								// TX bps/1024=TX kbps
								ssid[i][7] = Data2.getData()[i][2];
							}
							Data.setData(ssid);
						}
					}
				} else if (chartType.equals("an")) {
					// System.out.println(chartType);

					JDbTable Data2 = null;
					APTableName = "GLOBAL_INFO_SAVE";
					Data2 = network.getAPList(APTableName);

					if (Data2 == null) {
						System.out.println("Data2==null");
					} else {
						String[][] ssid = new String[Data2.getData().length][8];

						if (Data2.getData()[0][0] != "") {
							for (int i = 0; i < Data2.getData().length; i++) {
								// 時間
								ssid[i][0] = Data2.getData()[i][0];
								// RX bps/1024=RX kbps
								ssid[i][5] = Data2.getData()[i][3];
								// TX bps/1024=TX kbps
								ssid[i][7] = Data2.getData()[i][3];
							}
							Data.setData(ssid);
						}
					}
				} else if (chartType.equals("bgn")) {
					// System.out.println(chartType);

					JDbTable Data2 = null;
					APTableName = "GLOBAL_INFO_SAVE";
					Data2 = network.getAPList(APTableName);

					if (Data2 == null) {
						System.out.println("Data2==null");
					} else {
						String[][] ssid = new String[Data2.getData().length][8];

						if (Data2.getData()[0][0] != "") {
							for (int i = 0; i < Data2.getData().length; i++) {
								// 時間
								ssid[i][0] = Data2.getData()[i][0];
								// RX bps/1024=RX kbps
								ssid[i][5] = Data2.getData()[i][4];
								// TX bps/1024=TX kbps
								ssid[i][7] = Data2.getData()[i][4];
							}
							Data.setData(ssid);
						}
					}
				}

				if (timeSelect == null || timeSelect == "" || timeSelect.equals("day")) {// 預設頁
					System.out.println("timeSelect == \"null\"、timeSelect == \"\" || timeSelect.equals(\"day\")");
					// System.out.println(Data.getData().length);
					// System.out.println(Data.getData()[0].length);
					if (Data.getData()[0][0] != "") {
						long now_time = 0;

						for (int i = 0; i < Data.getData().length; i++) {
							// System.out.println(Data.getData()[i][0] + "," +
							// Data.getData()[i][5] + "," + Data.getData()[i][7]);

							// 迴圈讀的
							long database_time = 0;
							try {
								database_time = sdf.parse(Data.getData()[i][0]).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 現在時間
							now_time = new java.sql.Timestamp(new Date().getTime()).getTime();

							// 頭尾的月曆範圍用，跟其他選項頁面都要一樣
							starttime = Data.getData()[0][0];
							endtime = Data.getData()[Data.getData().length - 1][0];
							// System.out.println("starttime：" + starttime);
							// System.out.println("endtime：" + endtime);

							// 24小時以前
							if (!(Data.getData()[i][0]).equals(tmp) && (database_time > (now_time - 86400000))) {
								// System.out.println(Data.getData()[i][0] + "," +
								// Data.getData()[i][5] + ","
								// + Data.getData()[i][7]);

								HashSet set = new HashSet();
								set.add(Data.getData()[i]);

								myList.add(Data.getData()[i]);

								// 因為時間有照順序所以可以
								tmp = Data.getData()[i][0];
							}

						}

						// 存入chartData
						chartData = new String[myList.size()][3];
						for (int j = 0; j < myList.size(); j++) {
							for (int k = 0; k < myList.get(0).length; k++) {
								// System.out.println((myList.get(j)[0]) + "," +
								// Integer.parseInt(myList.get(j)[5]) + ","
								// + Integer.parseInt(myList.get(j)[7]));
								// 時間
								chartData[j][0] = myList.get(j)[0];
								// RX bps/1024=RX kbps
								chartData[j][1] = String.valueOf(Integer.parseInt(myList.get(j)[5]));
								// TX bps/1024=RX kbps
								chartData[j][2] = String.valueOf(Integer.parseInt(myList.get(j)[7]));
							}
						}

						// 防止沒有任何資料
						if (chartData.length == 0) {
							System.out.println("chartData.length == 0");
							chartData = new String[2][3];
							chartData[0][0] = sdf.format(now_time - 86400000);
							chartData[0][1] = "0";
							chartData[0][2] = "0";
							chartData[1][0] = sdf.format(now_time);
							chartData[1][1] = "0";
							chartData[1][2] = "0";
						}
					} else if (Data.getData()[0][0] == "") {// 防止沒有這個APTable
						System.out.println("Data.getData()[0][0] == \"\"");
						chartData[0][0] = sdf.format(calendar.getTime());
						chartData[0][1] = "0";
						chartData[0][2] = "0";

						// 頭尾的月曆範圍用
						starttime = sdf.format(calendar.getTime());
						endtime = sdf.format(calendar.getTime());
					}
				} else if (timeSelect.equals("monthly") || timeSelect.equals("choose_date")) {// 月歷單天、範圍
					System.out.println("timeSelect == \"monthly\"、timeSelect == \"\" || timeSelect.equals(\"choose_date\")");
					// System.out.println(Data.getData().length);
					// System.out.println(Data.getData()[0].length);
					if (Data.getData()[0][0] != "") {
						for (int i = 0; i < Data.getData().length; i++) {
							// System.out.println(Data.getData()[i][0] + "," +
							// Data.getData()[i][5] + "," + Data.getData()[i][7]);

							// 迴圈讀的
							long database_time = 0;
							try {
								database_time = sdf.parse(Data.getData()[i][0]).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 網頁送來的starttime
							long start_time = 0;
							try {
								start_time = sdf.parse(startTime).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 網頁送來的endtime
							long end_time = 0;
							try {
								end_time = sdf.parse(endTime).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 現在時間
							long now_time = new java.sql.Timestamp(new Date().getTime()).getTime();

							// 頭尾的月曆範圍用，跟其他選項頁面都要一樣
							starttime = Data.getData()[0][0];
							endtime = Data.getData()[Data.getData().length - 1][0];
							// System.out.println("starttime：" + starttime);
							// System.out.println("endtime：" + endtime);

							// starttime< 要的範圍 <endtime
							if (!(Data.getData()[i][0]).equals(tmp) && (database_time > start_time) && (database_time < end_time)) {
								// System.out.println(Data.getData()[i][0] + "," +
								// Data.getData()[i][5] + ","
								// + Data.getData()[i][7]);

								myList.add(Data.getData()[i]);

								tmp = Data.getData()[i][0];
							}
						}

						// 存入chartData
						chartData = new String[myList.size()][3];
						for (int j = 0; j < myList.size(); j++) {
							for (int k = 0; k < myList.get(0).length; k++) {
								// System.out.println((myList.get(j)[0]) + "," +
								// int.parseint(myList.get(j)[5]) + "," +
								// int.parseint(myList.get(j)[7]));
								// 時間
								chartData[j][0] = myList.get(j)[0];
								// RX bps/1024=RX kbps
								chartData[j][1] = String.valueOf(Integer.parseInt(myList.get(j)[5]));
								// TX bps/1024=RX kbps
								chartData[j][2] = String.valueOf(Integer.parseInt(myList.get(j)[7]));
							}
						}

						// 防止沒有任何資料
						if (chartData.length == 0) {
							System.out.println("chartData.length == 0");
							chartData = new String[2][3];
							chartData[0][0] = startTime;
							chartData[0][1] = "0";
							chartData[0][2] = "0";
							chartData[1][0] = endTime;
							chartData[1][1] = "0";
							chartData[1][2] = "0";
						}
					} else if (Data.getData()[0][0] == "") {// 防止沒有這個APTable
						// System.out.println("Data.getData()[0][0] == \"\"");
						chartData[0][0] = sdf.format(calendar.getTime());
						chartData[0][1] = "0";
						chartData[0][2] = "0";

						// 頭尾的月曆範圍用
						starttime = sdf.format(calendar.getTime());
						endtime = sdf.format(calendar.getTime());
					}
				} else if (timeSelect.equals("week")) {
					System.out.println("timeSelect.equals(\"week\")");
					// System.out.println(Data.getData().length);
					// System.out.println(Data.getData()[0].length);
					if (Data.getData()[0][0] != "") {
						for (int i = 0; i < Data.getData().length; i++) {
							// System.out.println(Data.getData()[i][0] + "," +
							// Data.getData()[i][5] + "," + Data.getData()[i][7]);

							// 迴圈讀的
							long database_time = 0;
							try {
								database_time = sdf.parse(Data.getData()[i][0]).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 現在時間
							long now_time = new java.sql.Timestamp(new Date().getTime()).getTime();

							// 頭尾的月曆範圍用，跟其他選項頁面都要一樣
							starttime = Data.getData()[0][0];
							endtime = Data.getData()[Data.getData().length - 1][0];
							// System.out.println("starttime：" + starttime);
							// System.out.println("endtime：" + endtime);

							// 一週以前
							if (!(Data.getData()[i][0]).equals(tmp) && (database_time > (now_time - 604800000))) {
								// System.out.println(Data.getData()[i][0] + "," +
								// Data.getData()[i][5] + ","
								// + Data.getData()[i][7]);

								myList.add(Data.getData()[i]);

								// 因為時間有照順序所以可以
								tmp = Data.getData()[i][0];
							}
						}

						// 存入chartData
						chartData = new String[myList.size()][3];
						for (int j = 0; j < myList.size(); j++) {
							for (int k = 0; k < myList.get(0).length; k++) {
								// System.out.println((myList.get(j)[0]) + "," +
								// int.parseint(myList.get(j)[5]) + "," +
								// int.parseint(myList.get(j)[7]));
								// 時間
								chartData[j][0] = myList.get(j)[0];
								// RX bps/1024=RX kbps
								chartData[j][1] = String.valueOf(Integer.parseInt(myList.get(j)[5]));
								// TX bps/1024=RX kbps
								chartData[j][2] = String.valueOf(Integer.parseInt(myList.get(j)[7]));
							}
						}

						// 防止沒有任何資料
						if (chartData.length == 0) {
							System.out.println("chartData.length == 0");
							chartData = new String[2][3];
							chartData[0][0] = startTime;
							chartData[0][1] = "0";
							chartData[0][2] = "0";
							chartData[1][0] = endTime;
							chartData[1][1] = "0";
							chartData[1][2] = "0";
						}
					} else if (Data.getData()[0][0] == "") {// 防止沒有這個APTable
						System.out.println("Data.getData()[0][0] == \"\"");
						chartData[0][0] = sdf.format(calendar.getTime());
						chartData[0][1] = "0";
						chartData[0][2] = "0";

						// 頭尾的月曆範圍用
						starttime = sdf.format(calendar.getTime());
						endtime = sdf.format(calendar.getTime());
					}
				} else if (timeSelect.equals("month")) {
					System.out.println("timeSelect.equals(\"month\")");
					// System.out.println(Data.getData().length);
					// System.out.println(Data.getData()[0].length);
					if (Data.getData()[0][0] != "") {
						for (int i = 0; i < Data.getData().length; i++) {
							// System.out.println(Data.getData()[i][0] + "," +
							// Data.getData()[i][5] + "," + Data.getData()[i][7]);

							// 迴圈讀的
							long database_time = 0;
							try {
								database_time = sdf.parse(Data.getData()[i][0]).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 現在時間
							long now_time = new java.sql.Timestamp(new Date().getTime()).getTime();

							// 頭尾的月曆範圍用，跟其他選項頁面都要一樣
							starttime = Data.getData()[0][0];
							endtime = Data.getData()[Data.getData().length - 1][0];
							// System.out.println("starttime：" + starttime);
							// System.out.println("endtime：" + endtime);

							Calendar cal = Calendar.getInstance();
							cal.add(cal.MONTH, -1);
							long monthtime = cal.getTime().getTime();
							// 一月以前
							if (!(Data.getData()[i][0]).equals(tmp) && (database_time > monthtime)) {
								// System.out.println(Data.getData()[i][0] + "," +
								// Data.getData()[i][5] + ","
								// + Data.getData()[i][7]);
								myList.add(Data.getData()[i]);

								// 因為時間有照順序所以可以
								tmp = Data.getData()[i][0];
							}
						}

						// 存入chartData
						chartData = new String[myList.size()][3];
						for (int j = 0; j < myList.size(); j++) {
							for (int k = 0; k < myList.get(0).length; k++) {
								// System.out.println((myList.get(j)[0]) + "," +
								// int.parseint(myList.get(j)[5]) + "," +
								// int.parseint(myList.get(j)[7]));
								// 時間
								chartData[j][0] = myList.get(j)[0];
								// RX bps/1024=RX kbps
								chartData[j][1] = String.valueOf(Integer.parseInt(myList.get(j)[5]));
								// TX bps/1024=RX kbps
								chartData[j][2] = String.valueOf(Integer.parseInt(myList.get(j)[7]));
							}
						}

						// 防止沒有任何資料
						if (chartData.length == 0) {
							System.out.println("chartData.length == 0");
							chartData = new String[2][3];
							chartData[0][0] = startTime;
							chartData[0][1] = "0";
							chartData[0][2] = "0";
							chartData[1][0] = endTime;
							chartData[1][1] = "0";
							chartData[1][2] = "0";
						}
					} else if (Data.getData()[0][0] == "") {// 防止沒有這個APTable
						System.out.println("Data.getData()[0][0] == \"\"");
						chartData[0][0] = sdf.format(calendar.getTime());
						chartData[0][1] = "0";
						chartData[0][2] = "0";

						// 頭尾的月曆範圍用
						starttime = sdf.format(calendar.getTime());
						endtime = sdf.format(calendar.getTime());
					}
				}
		*/
		// System.out.println("chartData.length：" + chartData.length);
		// System.out.println("chartData[0][0]:" + chartData[0][0]);

		// System.out.println("chartData.length：" + chartData.length);
		//for (int i = 0; i < chartData.length; i++) {
		//	for (int j = 0; j < chartData[0].length; j++) {
		//		// System.out.println("chartData:" + chartData[i][j]);
		//	}
		//}
		// ACAP code end Section 1

		// if timeSelect is specified, means to change the start time according
		// to which button selected on web page
		// if (timeSelect != null) {
		// if (timeSelect.equals("day")) {
		// calendar.add(Calendar.DAY_OF_YEAR, -1);
		// startTime = sdf.format(calendar.getTime());
		// } else if (timeSelect.equals("week")) {
		// calendar.add(Calendar.DAY_OF_YEAR, -7);
		// startTime = sdf.format(calendar.getTime());
		// } else if (timeSelect.equals("month")) {
		// calendar.add(Calendar.DAY_OF_YEAR, -30);
		// startTime = sdf.format(calendar.getTime());
		// }
		// }
		System.out.println("=====StartTime: " + Jsp_StartTime + ", EndTime: " + Jsp_EndTime);

		// String[][] chartData = null;
		// if (chartType != null) {
		// // Rx Packet Type
		// if (chartType.equals("rx_packet_type_history")) {
		// chartData = network.getRxPacketTypeHistory(ip, port, startTime,
		// endTime, pointNumber);
		// }
		// else if (chartType.equals("rx_tx_packet_history")) {
		// chartData = network.getRxTxPacketHistory(ip, port, startTime,
		// endTime, pointNumber);
		// }
		// // Rx Packet Size
		// else if (chartType.equals("rx_packet_size_history")) {
		// chartData = network.getRxPacketSizeHistory(ip, port, startTime,
		// endTime, pointNumber);
		// }
		// // Rx Tx Octets bps
		// if (chartType.equals("rx_tx_octet_bps_history")) {
		// chartData = network.getRxTxOctetBpsHistory(ip, port, startTime,
		// endTime, pointNumber);
		// }
		// // Tx Packet Type
		// else if (chartType.equals("tx_packet_type_history")) {
		// chartData = network.getTxPacketTypeHistory(ip, port, startTime,
		// endTime, pointNumber);
		// }
		// // Rx Packet Interface Error
		// else if (chartType.equals("rx_packet_interface_error_history")) {
		// chartData = network.getRxPacketInterfaceErrorHistory(ip, port,
		// startTime, endTime, pointNumber);
		// }
		// // Rx Packet RMON Error
		// else if (chartType.equals("rx_packet_rmon_error_history")) {
		// chartData = network.getRxPacketRmonErrorHistory(ip, port, startTime,
		// endTime, pointNumber);
		// }
		// // Rx Packet EtherLike Error
		// else if (chartType.equals("rx_packet_etherlike_error_history")) {
		// chartData = network.getRxPacketEtherLikeErrorHistory(ip, port,
		// startTime, endTime, pointNumber);
		// }
		// // Tx Packet Interface Error
		// else if (chartType.equals("tx_packet_interface_error_history")) {
		// chartData = network.getTxPacketInterfaceErrorHistory(ip, port,
		// startTime, endTime, pointNumber);
		// }
		// }
		if (chartData != null) {
			JTools.print(chartData, false);

			int y1 = 0, m1 = 0, d1 = 0, y2 = 0, m2 = 0, d2 = 0;
			try {
				calendar.setTime(sdf.parse(DBFirstTime));
				System.out.println("DBFirstTime：" + DBFirstTime);
				// calendar.setTime(sdf.parse(chartData[0][0]));
				y1 = calendar.get(Calendar.YEAR);
				m1 = calendar.get(Calendar.MONTH) + 1;
				d1 = calendar.get(Calendar.DAY_OF_MONTH);

				calendar.setTime(sdf.parse(DBLastTime));
				System.out.println("DBLastTime：" + DBLastTime);
				// calendar.setTime(sdf.parse(chartData[chartData.length -
				// 1][0]));
				y2 = calendar.get(Calendar.YEAR);
				m2 = calendar.get(Calendar.MONTH) + 1;
				d2 = calendar.get(Calendar.DAY_OF_MONTH);
				// System.out.println(y1 + "," + m1 + "," + d1 + "," + y2 + ","
				// + m2 + "," + d2);
			} catch (ParseException e1) {
				System.out.println("time parse failed.");
			}

			for (int i = 0; i < chartData.length; i++) {
				if (chartData[i].length != chartData[0].length)
					System.out.println("Data size of chart is wrong at i=" + i);
				int[] data = new int[chartData[i].length - 1 + 6]; // data[]
				// does
				// not
				// contain
				// the
				// time
				int j = 0;
				for (; j < chartData[i].length - 1; j++) {
					data[j] = Integer.parseInt(chartData[i][j + 1]);
				}
				data[j++] = y1;
				data[j++] = m1;
				data[j++] = d1;
				data[j++] = y2;
				data[j++] = m2;
				data[j++] = d2;
				dataSet.put(chartData[i][0], data);
			}
		}

		String json_data = new Gson().toJson(dataSet);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(json_data);
		} catch (IOException e) {
			System.out.println(e.getMessage());
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
