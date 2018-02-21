package com.via.rmi;

import java.io.Serializable;
import java.util.ArrayList;

import com.via.model.JACAPInfo;

public class RemoteACAPInfo implements Serializable {
	public static ArrayList<String[]> getFriendlyAPList(final String acip) {
		return JACAPInfo.getFriendlyAPList(acip);
	}

	public static ArrayList<String[]> getRogueAPList(final String acip) {
		return JACAPInfo.getRogueAPList(acip);
	}

	public static ArrayList<String[]> getOnlineAPList(final String acip) {
		return JACAPInfo.getOnlineAPList(acip);
	}

	public static ArrayList<String[]> getOfflineAPList(final String acip) {
		return JACAPInfo.getOfflineAPList(acip);
	}

	public static ArrayList<String[]> getAPSSIDList(final String acip) {
		return JACAPInfo.getAPSSIDList(acip);
	}

	public static ArrayList<String[]> getClientList(final String acip) {
		return JACAPInfo.getClientList(acip);
	}

	public static ArrayList<String[]> getClientDeviceList(final String acip) {
		return JACAPInfo.getClientDeviceList(acip);
	}

	public static ArrayList<String> getGlobalInfo(final String acip) {
		return JACAPInfo.getGlobalInfo(acip);
	}

	public static ArrayList<String> getACLogIndex(final String acip) {
		return JACAPInfo.getACLogIndex(acip);
	}

	public static String getACLog(final String acip, final String startTime, final String endTime) {
		return JACAPInfo.getACLog(acip, startTime, endTime);
	}

	public static ArrayList<String> getAPSTALogIndex(final String acip) {
		return JACAPInfo.getAPSTALogIndex(acip);
	}

	public static String getAPSTALog(final String acip, final String startTime, final String endTime) {
		return JACAPInfo.getAPSTALog(acip, startTime, endTime);
	}
}