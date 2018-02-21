package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JEntry {
	
	private String name;
	private String aliasName;
	private String url;
	private String circleColor;
	private String type;
	private String icon;
	
	
	public JEntry(String name, String aliasName, int aliveStatus, String deviceType) {
		this.name = name;
		this.aliasName = aliasName;
		this.url = "groupDevice.jsp?name="+name;
		clolor(aliveStatus);
		mate(deviceType);
	}
	
	private List<JEntry> children = new ArrayList<JEntry>();

    public void add(JEntry node){
          
       children.add(0,node);
    }
    
    private void clolor(int aliveStatus) {
    	
    	if (aliveStatus == 0) {
    		circleColor = "red";
    	}
    	else if (aliveStatus == 1) {
    		circleColor = "";
    	}
    	else if (aliveStatus == 2) {
    		circleColor = "orange";
    	}
    	else if (aliveStatus == 3) {
    		circleColor = "black";
    	}
    }
	
    private void mate(String deviceType) {
    	
    	if (deviceType.equals("l2switch")) {
			type = "L2 Switch";
			icon = "images/switch_layer2.png";
		} 
    	else if (deviceType.equals("l3switch")) {
			type = "L3 Switch";
			icon = "images/switch_layer3.png";
		} 
    	else if (deviceType.equals("wlanAC")) {
			type = "AC";
			icon = "images/wlan_ac.png";
		} 
    	else if (deviceType.equals("wlanAP")) {
			type = "AP";
			icon = "images/wlan_ap.png";
		} 
    	else if (deviceType.equals("firewall")) {
			type = "Firewall";
			icon = "images/firewall.png";
		} 
    	else if (deviceType.equals("server")) {
			type = "Server";
			icon = "images/server.png";
		} 
    	else if (deviceType.equals("pc")) {
			type = "PC";
			icon = "images/desktop.png";
		} 
    	else if (deviceType.equals("internet")) {
			type = "Internet";
			icon = "images/cloud.png";
		} 
    	else if (deviceType.equals("NMS")) {
			type = "NMS";
			icon = "images/nms.png";
		} 
    	else if (deviceType.equals("MGVChiefServer")) {
			type = "MGV Chief Server";
			icon = "images/mgv_chief_server.png";
		} 
    	else if (deviceType.equals("MGVCommandServer")) {
			type = "MGV Command Server";
			icon = "images/mgv_command_server.png";
		} 
    	else if (deviceType.equals("MGVPlayer")) {
			type = "MGV Player";
			icon = "images/mgv_player.png";
		}
    	
    }
	
}
