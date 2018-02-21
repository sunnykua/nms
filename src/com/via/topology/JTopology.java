package com.via.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.via.model.*;

public class JTopology {
	private List<JNode> treeList;
	private List<JDevice> deviceList;
	private Logger logger = Logger.getLogger(this.getClass());

	public JTopology(List<JDevice> deviceList) {
		this.treeList = new ArrayList<JNode>();
		this.deviceList = deviceList;
	}

	// ====================================================================================================

	public ArrayList<String[]> getHtmlAndScript(String rootIP, String displayType, String userCheck) {
		List<JNode> multiTreeList = new ArrayList<JNode>();
		ArrayList<String[]> Allresult = new ArrayList<String[]>();
	    List<JNode> tempList = createNodeList();
	    JNode rootNode = findNode(tempList, rootIP);
		boolean isUtilization=false;		// will temporary down

	    while(tempList.size()>0){
	    	String[] result = {"", "", "", ""};        // html, script, root, ipSelect
		    String ipSelection = "";
		    List<JNode> treeList = findTree(tempList, rootNode);
		                      
		    if (treeList != null && !treeList.isEmpty()) {
		    	for (JNode node : treeList) {
		    		if (node.getDeviceType().indexOf("wlan") < 0) ipSelection += "," + node.getPublicIp();
		    	}
		    	if (!ipSelection.isEmpty()) ipSelection = ipSelection.substring(1);
		    }
		    if (displayType.equals("ip_name_utilization") || displayType.equals("ip_utilization") || 
		    		displayType.equals("name_utilization")|| displayType.equals("name_ip_utilization") ) {
		    	isUtilization = true;
		    }
		    else {isUtilization = false;}

//		    String[] pageString = genearatePageString(treeList, false, displayType, userCheck);
		    String[] pageString = genearatePageString(treeList, isUtilization, displayType, userCheck);

		    result[0] = pageString[0];
		    result[1] = pageString[1];
		    result[2] = !treeList.isEmpty() ? treeList.get(0).getPublicIp() : "";
		    result[3] = ipSelection;
		    Allresult.add(result);
		    multiTreeList.addAll(treeList);             // copy all treeList to multiTreeList
	    }
	    this.treeList = multiTreeList;                  // TODO for temporary store
	    /*for(String[] result : Allresult){
	    	System.out.println("result="+Arrays.toString(result));
	    }*/

	    return Allresult;
	}
    
    public List<String[][]> getPathBetween(String startIpAddr, String endIpAddr) {
        JNode startNode = findNode(treeList, startIpAddr);
        JNode endNode = findNode(treeList, endIpAddr);
        if (startNode == null || endNode == null) {
            logger.debug("Node not found by input IP.");
            return null;
        }
        
        List<String[][]> firstPairsList = getPathToRoot(treeList, startNode);               // from start device to root device
        List<String[][]> secondPairsList = getPathToRoot(treeList, endNode);                // from end device to root device
        logger.debug("Path from " + startIpAddr + " to root:");
        for (String[][] pair : firstPairsList) logger.debug(Arrays.deepToString(pair));
        logger.debug("Path from " + endIpAddr + " to root:");
        for (String[][] pair : secondPairsList) logger.debug(Arrays.deepToString(pair));
        
        while(true) {
            if (firstPairsList.size() < 1 || secondPairsList.size() < 1) break;
            if (JTools.compareArrays(firstPairsList.get(firstPairsList.size() - 1), secondPairsList.get(secondPairsList.size() - 1))) {
                firstPairsList.remove(firstPairsList.size() - 1);
                secondPairsList.remove(secondPairsList.size() - 1);
            }
            else {
                break;
            }
        }
        
        List<String[][]> resultPairList = new ArrayList<String[][]>(firstPairsList);
        for (int i = secondPairsList.size() - 1; i >= 0; i--) {             // reverse the direction from converged device to end device, all vectors also need 
            String[][] pair = secondPairsList.get(i);
            String[] temp = pair[0];                    // swap start and end point
            pair[0] = pair[1];
            pair[1] = temp;
            //String[] reversedPair = {pair[3], pair[4], pair[5], pair[0], pair[1], pair[2]};     // start/end {IP, port, type}
            resultPairList.add(pair);
        }
        
        logger.debug("Result of whole path:");
        for (String[][] pair : resultPairList) logger.debug(Arrays.deepToString(pair));
        
        return resultPairList;
    }
    
    // ====================================================================================================
    
	private List<JNode> createNodeList() {
	    List<JNode> resultList = new ArrayList<JNode>();
	    List<String> tempIp = new ArrayList<String>();
	    
	    for (JDevice device : deviceList) {
	        // Node for switch device
	        JNode switchNode = new JNode();
	        switchNode.setPublicIp(device.getPublicIp());
	        switchNode.setPhyAddr(device.getPhyAddr());
	        switchNode.setSnmpSupport(device.getSnmpSupport());
	        switchNode.setSysObjectId(device.getSysObjectId());
	        switchNode.setAliasName(device.getAliasName());
	        switchNode.setDeviceType(device.getDeviceType());                // note here
	        switchNode.setRj45Num(device.getRj45Num());
	        switchNode.setStackNum(device.getStackNum());
	        switchNode.setStpPriority(device.getStpPriority());
	        switchNode.setStpRootCost(device.getStpRootCost());
	        switchNode.setStpRootPort(device.getStpRootPort());
	        switchNode.setInterfaces(device.getInterfaces());            // copy interface list from device
	        // TODO it has to consider if remote_id type is not macAddress(4)
	        switchNode.setParent(new String[]{"", "", "", ""});
	        switchNode.setChildren(new ArrayList<String[]>());
	        switchNode.setRoot(false);                                 // TODO will determine later
	        switchNode.setHasNext(false);
	        switchNode.setHasChild(false);
	        switchNode.setAlive(device.isAlive());
	        switchNode.setVirtual(device.isVirtual());
	        switchNode.setNodeHTML("");
	        resultList.add(switchNode);
	        
	        tempIp.add(device.getPublicIp());
	    }
	    
	    return resultList;
	}
	
	private List<JNode> findTree(List<JNode> inputList, JNode rootNode) {
	    List<JNode> resultList = new ArrayList<JNode>();
	    
	    if (inputList == null || inputList.isEmpty()) {
	        logger.debug("There is no node in the input list.");
	        return resultList;
	    }
	    
	    // Determine who is the root, and move it to the first place of result list
	    if (rootNode != null && inputList.contains(rootNode)) {    // use the specified root if exist, otherwise find the cost = 0 as the root, if none, use the first device
	        rootNode.setRoot(true);
	        resultList.add(rootNode);
	        inputList.remove(rootNode);
	        logger.debug("Use specified root: " + rootNode.getPublicIp());
	    }
	    else {
	        for (JNode node : inputList) {
	            if (node.getStpRootCost() == 0) {
	                logger.debug("Use found root: " + node.getPublicIp());
	                node.setRoot(true);
	                resultList.add(node);
	                inputList.remove(node);
	                break;
	            }
	        }
	        if (resultList.size() == 0) {
	            logger.debug("No auto root is found, use the first device.(" + inputList.get(0).getPublicIp());
	            resultList.add(inputList.remove(0));
	            resultList.get(0).setRoot(true);
	        }
	    }
	    
	    int currIndex = 0;
	    while (resultList.size() > currIndex) {
	        JNode currNode = resultList.get(currIndex);
	        int childIndex = currIndex;                 // the position used for adding child
	        
	        // find all children from input list
	        for (JInterface inf : currNode.getInterfaces()) {
	            if (inf.isManual()) {                                          //TODO if current interface is manual, find child by using IP, otherwise, use phyAddr
	                for (JNode origNode : inputList) {
	                    if (origNode.getPublicIp().equals(inf.getManualRemoteIp())) {
	                    	inf.setPortType("designated");
	                    	inf.setPortRemoteDev(origNode.getDeviceType());
	                    	inf.setPortRemoteIp(origNode.getPublicIp());
	                    	//inf.setAliasName(origNode.getAliasName());
	                        origNode.setHasNext(true);
	                        origNode.setParentIp(currNode.getPublicIp());
	                        origNode.setParentId(currNode.getPhyAddr());
	                        origNode.setParentPort(inf.getPortId());
	                        origNode.setParentPortIfIndex(inf.getIfIndex());
	                        origNode.setParentPortStack(inf.getStackId());
	                        origNode.setParentStackNum(currNode.getStackNum());
	                        origNode.setParentDeviceType(currNode.getDeviceType());
	                        // find the port link to parent
	                        for (JInterface origInf : origNode.getInterfaces()) {
	                            if (origInf.isManual() && origInf.getManualRemoteIp().equals(currNode.getPublicIp())) { // TODO the opposite of manual interface should also manual?
	                            	origInf.setPortType("Root");
	                            	origInf.setPortRemoteDev(currNode.getDeviceType());
	                            	origInf.setPortRemoteIp(currNode.getPublicIp());
	                            	//origInf.setAliasName(currNode.getAliasName());
	                            	origNode.setPortToParent(origInf.getPortId());
	                                origNode.setPortToParentIfIndex(origInf.getIfIndex());
	                                origNode.setPortToParentStack(origInf.getStackId());
	                                break;
	                            }
	                        }
	                        
	                        resultList.add(++childIndex, origNode);
	                        inputList.remove(origNode);
	                        break;
	                    }
	                }
	            }
	            else {
	                for (JNode origNode : inputList) {
                        if (!origNode.getPhyAddr().isEmpty() && origNode.getPhyAddr().equals(inf.getLldpRemoteId())) {  // TODO check empty to avoid device that does not have phyAddr
                        	inf.setPortType("designated");
                        	inf.setPortRemoteDev(origNode.getDeviceType());
                        	inf.setPortRemoteIp(origNode.getPublicIp());
                        	//inf.setAliasName(origNode.getAliasName());
                        	origNode.setHasNext(true);
                            origNode.setParentIp(currNode.getPublicIp());
                            origNode.setParentId(currNode.getPhyAddr());
                            origNode.setParentPort(inf.getPortId());
                            origNode.setParentPortIfIndex(inf.getIfIndex());
                            origNode.setParentPortStack(inf.getStackId());
                            origNode.setParentStackNum(currNode.getStackNum());
                            origNode.setParentDeviceType(currNode.getDeviceType());
                            // find the port link to parent
                            for (JInterface origInf : origNode.getInterfaces()) {
                                if (!origInf.isManual() && origInf.getLldpRemoteId().equals(currNode.getPhyAddr())) {   // TODO should the interface of this child is not manual
                                	origInf.setPortType("Root");
                                	origInf.setPortRemoteDev(currNode.getDeviceType());
                                	origInf.setPortRemoteIp(currNode.getPublicIp());
                                	//origInf.setAliasName(currNode.getAliasName());
                                	origNode.setPortToParent(origInf.getPortId());
                                    origNode.setPortToParentIfIndex(origInf.getIfIndex());
                                    origNode.setPortToParentStack(origInf.getStackId());
                                    break;
                                }
                            }
                            
                            resultList.add(++childIndex, origNode);
                            inputList.remove(origNode);
                            break;
                        }
                    }
	            }
	            
	        }
	        if (childIndex != currIndex) {
	            resultList.get(currIndex).setHasChild(true);         // child index is moved, so this parent node has child
	            resultList.get(childIndex).setHasNext(false);        // there is no next to the last child
	        }

	        /*System.out.println("current node is " + currNode.getPublicIp() + ", and tree: ");
	        for (JNode node : resultList) {
	            System.out.println("    " + node.getPublicIp());
	        }*/
	        
	        currIndex++;
	    }
	    
	    // setPortType() 
	    for (JDevice device : deviceList) {
		    for (JInterface inf : device.getInterfaces()) {
		    	if(inf.getPortType() !=null && !inf.getPortType().isEmpty()){
	        		if(inf.getPortType().equals("designated")){
	        			if(inf.getPortRemoteDev().equals("l2switch") || inf.getPortRemoteDev().equals("l3switch") || inf.getPortRemoteDev().equals("firewall")){
	        				inf.setPortType("designated");
	        			}
	        			else {
		        			inf.setPortType("terminal");
		        		}
	        		}
        		}
        		else {
        			inf.setPortType("terminal");
        		}
		    }
	    }
	    for (JDevice device : deviceList) {
	    	for (JNode node: resultList) {
		    	if(device.getPublicIp().equals(node.getPublicIp())){
		    		device.setParentIp(node.getParentIp());
		    	}
		    }
	    	//System.out.println(device.getParentIp());
	    }
	    
	    
	    return resultList;
	}
	
	private JNode findNode(List<JNode> inputList, String ip) {
	    if (inputList == null || ip == null) return null;
	    
	    for (JNode node : inputList) {
	        if (node.getPublicIp().equals(ip)) {
	            return node;
	        }
	    }
	    
	    return null;
	}
	
	/**
	 * @param treeList
	 * @param enableRateTag
	 * @return [0]: html, [1]: script
	 */
	private String[] genearatePageString(List<JNode> treeList, boolean enableRateTag, String displayType, String userCheck) {
        if (treeList == null || treeList.isEmpty()) return null;
        List<JNode> stackNodes = new ArrayList<JNode>();
        int treeIndex = 0;
        int stackIndex = -1;
        String treeHTML = "";
        String treeScript = "";
        int rateIdIndex = 0;

        while (true) {
            JNode lastNode = stackNodes.size() > 0 ? stackNodes.get(stackNodes.size() - 1) : null;
            boolean toGetNext = ((lastNode == null) || lastNode.isHasChild() || lastNode.isHasNext()) ? true : false;

            if (toGetNext) {
                JNode node = treeList.get(treeIndex++);
                stackNodes.add(node);
                stackIndex++;
                if (!node.isHasChild()) {
                    String deviceImage;
                    if (node.getDeviceType().equals("wlanAC")) deviceImage = "images/wlan_ac.png";
                    else if (node.getDeviceType().equals("wlanAP")) deviceImage = "images/wlan_ap.png";
                    else if (node.getDeviceType().equals("firewall")) deviceImage = "images/firewall.png";
                    else if (node.getDeviceType().equals("l3switch")) deviceImage = "images/switch_layer3.png";
                    else if (node.getDeviceType().equals("server")) deviceImage = "images/server.png";
                    else if (node.getDeviceType().equals("pc")) deviceImage = "images/desktop.png";
                    else if (node.getDeviceType().equals("internet")) deviceImage = "images/cloud.png";
                    else if (node.getDeviceType().equals("NMS")) deviceImage = "images/nms.png";
                    else if (node.getDeviceType().equals("MGVChiefServer")) deviceImage = "images/mgv_chief_server.png";
                    else if (node.getDeviceType().equals("MGVCommandServer")) deviceImage = "images/mgv_command_server.png";
                    else if (node.getDeviceType().equals("MGVPlayer")) deviceImage = "images/mgv_player.png";
                    else deviceImage = "images/switch_layer2.png";
                    
                    String rateTag = "";
                    String parentPortTag = "";
                    String localPortTag = "";
                    if (!node.isRoot()) {           // TODO whether it is STP root or manual root
                        rateIdIndex++;
                        String upRateId = "upRate" + rateIdIndex;  // tx
                        String dnRateId = "dnRate" + rateIdIndex; // rx
                        String upRateImage = "<img src='images/arrow_up_double.png' height='18' width='18'>";
                        String downRateImage = "<img src='images/arrow_down_double.png' height='18' width='18'>";
                        
                        if (enableRateTag && node.getSnmpSupport()>0 && !node.getDeviceType().equals("wlanAP")) {
                            rateTag = String.format("<span> [ %s <span id='%s'> </span>, %s <span id='%s'> </span>]</span>",
                            		upRateImage, upRateId, downRateImage, dnRateId);
                            treeScript += String.format("getData('%s', '%s', '%s', '%s');", node.getPublicIp(), node.getPortToParentIfIndex(), upRateId, dnRateId);
                        }
                        
                        parentPortTag = "<div class='bef_port'>" + (node.getParentStackNum() > 1 ? node.getParentPortStack() + "-" + node.getParentPort() : node.getParentPort() <= 0 ? "*" : node.getParentPort()) + "</div>";
                        localPortTag = "<div class='pre_port'>" + (node.getStackNum() > 1 ? node.getPortToParentStack() + "-" + node.getPortToParent() : node.getRj45Num()==1 || node.getPortToParent() <= 0 ? "*" : node.getPortToParent()) + "</div>";
                    }
                    
                    String deviceTagIpSection = String.format(
                    		!userCheck.equals("2") && node.getSnmpSupport()>0 && (node.getDeviceType().equals("l2switch") || node.getDeviceType().equals("l3switch") || (node.getDeviceType().equals("wlanAC") && node.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1"))) ?
                            //"<a href=\"http://%1$s/index_452.htm\" target=\"_blank\">%1$s</a>" :
                            //"<a href='javascript:void(0)' onclick=\"window.open('http://%1$s/', '_blank');\">%1$s</a>" :
                            //"<a href='javascript:void(0)' onclick=\"openOnNewPage('http://%1$s');\">%1$s</a>" :
                            "<a href='javascript:void(0)' onclick=\"window.open('single_ip.jsp?ip=%1$s', 'single');\">%1$s</a>" :
                            "%1$s",
                            node.getPublicIp());
                    
                    String deviceTag = "";
                    if (displayType.equals("ip_only")) {
                        deviceTag = deviceTagIpSection;
                    }
                    else if (displayType.equals("name_only")) {
                        deviceTag = node.getAliasName();
                    }
                    else if (displayType.equals("name_ip")) {
                        deviceTag = node.getAliasName() + " (" + deviceTagIpSection + ")";
                    }
                    else if (displayType.equals("name_utilization")) {
                        deviceTag = node.getAliasName();
                    }
                    else if (displayType.equals("ip_utilization")) {
                        deviceTag = deviceTagIpSection;
                    }
                    else if (displayType.equals("name_ip_utilization")) {
                        deviceTag = node.getAliasName() + " (" + deviceTagIpSection + ")";
                    }
                    else {
                        deviceTag = deviceTagIpSection + " (" + node.getAliasName() + ")";  // default: ip (name)
                    }
                    
                    String nodeHTML = String.format(
                            "<input type='checkbox' name='selectSwitch' value='%1$s'>" +
                            "<img src='%2$s' height='24' width='24'>" +
                            "%3$s" +
                            "%4$s",
                            node.getPublicIp(), deviceImage, deviceTag, rateTag);
                    
                    node.setNodeHTML("\n" + parentPortTag + localPortTag + "<li>" + nodeHTML + "</li>");
                }
                //System.out.println("Add node " + node.getPublicIp() + " to Stack.");
                continue;
            }

            // Collect HTML codes from end nodes and store in their parent
            //    At last, the root will contain all children's HTML code and still be added <ul> tag in next loop.
            String childHTML = "";
            while (stackIndex >= 0 && !stackNodes.get(stackIndex).isHasChild()) {   // Traverse back to parent node
                childHTML = stackNodes.get(stackIndex).getNodeHTML() + childHTML;       // The order is backward in Stack
                stackNodes.remove(stackIndex);
                stackIndex--;
                //System.out.println("Update html: " + childHTML);
            }
            childHTML = "\n<ul>" + childHTML + "</ul>";
            if (stackIndex >= 0) {                              // Should be parent
                JNode node = stackNodes.get(stackIndex);
                
                String deviceImage;
                if (node.getDeviceType().equals("wlanAC")) deviceImage = "images/wlan_ac.png";
                else if (node.getDeviceType().equals("wlanAP")) deviceImage = "images/wlan_ap.png";
                else if (node.getDeviceType().equals("firewall")) deviceImage = "images/firewall.png";
                else if (node.getDeviceType().equals("l3switch")) deviceImage = "images/switch_layer3.png";
                else if (node.getDeviceType().equals("server")) deviceImage = "images/server.png";
                else if (node.getDeviceType().equals("pc")) deviceImage = "images/desktop.png";
                else if (node.getDeviceType().equals("internet")) deviceImage = "images/cloud.png";
                else if (node.getDeviceType().equals("NMS")) deviceImage = "images/nms.png";
                else if (node.getDeviceType().equals("MGVChiefServer")) deviceImage = "images/mgv_chief_server.png";
                else if (node.getDeviceType().equals("MGVCommandServer")) deviceImage = "images/mgv_command_server.png";
                else if (node.getDeviceType().equals("MGVPlayer")) deviceImage = "images/mgv_player.png";
                else deviceImage = "images/switch_layer2.png";
                
                String rateTag = "";
                String parentPortTag = "";
                String localPortTag = "";
                if (!node.isRoot()) {
                    rateIdIndex++;
                    String upRateId = "upRate" + rateIdIndex;  // tx
                    String dnRateId = "dnRate" + rateIdIndex; // rx
                    String upRateImage = "<img src='images/arrow_up_double.png' height='18' width='18'>";
                    String downRateImage = "<img src='images/arrow_down_double.png' height='18' width='18'>";
                    
                    if (enableRateTag && node.getSnmpSupport()>0 && !node.getDeviceType().equals("wlanAP")) {
                    	rateTag = String.format("<span> [ %s <span id='%s'> </span>, %s <span id='%s'> </span>]</span>",
                        		upRateImage, upRateId, downRateImage, dnRateId);
                        treeScript += String.format("getData('%s', '%s', '%s', '%s');", node.getPublicIp(), node.getPortToParentIfIndex(), upRateId, dnRateId);
                    }
                    
                    parentPortTag = "<div class='bef_port'>" + (node.getParentStackNum() > 1 ? node.getParentPortStack() + "-" + node.getParentPort() : node.getParentPort() <= 0 ? "*" : node.getParentPort()) + "</div>";
                    localPortTag = "<div class='pre_port'>" + (node.getStackNum() > 1 ? node.getPortToParentStack() + "-" + node.getPortToParent() : node.getRj45Num()==1 || node.getPortToParent() <= 0 ? "*" : node.getPortToParent()) + "</div>";
                }
                
                String deviceTagIpSection = String.format(
                		!userCheck.equals("2") && node.getSnmpSupport()>0 && (node.getDeviceType().equals("l2switch") || node.getDeviceType().equals("l3switch") || (node.getDeviceType().equals("wlanAC") && node.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1"))) ?
                        //"<a href=\"http://%1$s/index_452.htm\" target=\"_blank\">%1$s</a>" :
                        //"<a href='javascript:void(0)' onclick=\"window.open('http://%1$s/', '_blank');\">%1$s</a>" :
                        //"<a href='javascript:void(0)' onclick=\"openOnNewPage('http://%1$s');\">%1$s</a>" :
                        "<a href='javascript:void(0)' onclick=\"window.open('single_ip.jsp?ip=%1$s', 'single');\">%1$s</a>" :
                        "%1$s",
                        node.getPublicIp());
                
                String deviceTag = "";
                if (displayType.equals("ip_only")) {
                    deviceTag = deviceTagIpSection;
                }
                else if (displayType.equals("name_only")) {
                    deviceTag = node.getAliasName();
                }
                else if (displayType.equals("name_ip")) {
                    deviceTag = node.getAliasName() + " (" + deviceTagIpSection + ")";
                }
                else if (displayType.equals("name_utilization")) {
                    deviceTag = node.getAliasName();
                }
                else if (displayType.equals("ip_utilization")) {
                    deviceTag = deviceTagIpSection;
                }
                else if (displayType.equals("name_ip_utilization")) {
                    deviceTag = node.getAliasName() + " (" + deviceTagIpSection + ")";
                }
                else {
                    deviceTag = deviceTagIpSection + " (" + node.getAliasName() + ")";  // default: ip (name)
                }
                
                String nodeHTML = String.format(
                        "<input type='checkbox' name='selectSwitch' value='%1$s'>" +
                        "<img src='%2$s' height='24' width='24'>" +
                        "%3$s" +
                        "%4$s",
                        node.getPublicIp(), deviceImage, deviceTag, rateTag);
                
                node.setNodeHTML("\n" + parentPortTag + localPortTag + "<li>" + nodeHTML + childHTML + "</li>");
                node.setHasChild(false);                                            // All its' children are collapsed
            }
            else {
                treeHTML = childHTML;
                break;
            }

            //System.out.println("Node has child: " + stackNodes.get(stackIndex).getPublicIp() + ": " + stackNodes.get(stackIndex).getNodeHTML());
        }
        //System.out.println("=====treeHTML:\n" + treeHTML);

        return new String[]{treeHTML, treeScript};
	}
	
	/**
	 * Trace to root and find all link pairs by using the relationship between child and parent
	 * @param node
	 * @return A list of pairs from specified node to the root node of this tree.
	 * Each pair represent two edges of a link, and they are device IP, port and type from star point and end point.
	 */
	private List<String[][]> getPathToRoot(List<JNode> treeList, JNode node) {
	    if (treeList == null || node == null) {
            logger.debug("Input list or node is null.");
            return null;
        }
	    //JNode childNode = node;
	    JNode currNode = node;
        List<String[][]> pairList = new ArrayList<String[][]>();
        logger.debug("=====Trace path from " + node.getPublicIp() + " to root.");
        
        for (int i = treeList.indexOf(node) - 1; i >= 0; i--) {
            JNode parentNode = treeList.get(i);
            if (parentNode.getPublicIp().equals(currNode.getParentIp())) {
                String[][] pair = new String[][]{
                        {currNode.getPublicIp(), currNode.getPortToParent() > 0 ? Integer.toString(currNode.getPortToParent()) : "",
                                currNode.getDeviceType(), currNode.isVirtual() ? "true" : "false", currNode.getPortToParentIfIndex() > 0 ? Integer.toString(currNode.getPortToParentIfIndex()) : ""},
                        {parentNode.getPublicIp(), currNode.getParentPort() > 0 ? Integer.toString(currNode.getParentPort()) : "",  // NOTE: parentPort has already known in findTree()
                                parentNode.getDeviceType(), parentNode.isVirtual() ? "true" : "false",currNode.getParentPortIfIndex() > 0 ? Integer.toString(currNode.getParentPortIfIndex()) : ""} };
                logger.debug("found pair:" + Arrays.deepToString(pair));
                pairList.add(pair);
                currNode = parentNode;
            }

        }
        
        return pairList;
	}

}
