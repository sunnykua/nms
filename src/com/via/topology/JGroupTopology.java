package com.via.topology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.via.database.JDatabase;
import com.via.model.JDevice;
import com.via.model.JEntry;
import com.via.model.JGroup;

public class JGroupTopology {
	private List<JGroup> groupList;
    private int a = 0;
    private final int iId = a++, iNAME = a++, iPARENT = a++, iIS_ROOT = a++, iALIVE_STATUS = a++;
	
	public JGroupTopology() {
		this.groupList = getAllGroup();
	}
	
	public List<JGroup> getAllGroup() {
		List<JGroup> groupList = new ArrayList<JGroup>();
        
        JDatabase db = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");

        String[][] deviceArray = db.getAll("GROUP03");
        
        ArrayList<String> child = new ArrayList<String>();
        
        if(deviceArray != null){
        	for (String[] data : deviceArray) {
	            JGroup group = new JGroup();
	            group.setId(data[iId]);
	            group.setName(data[iNAME]);
	            group.setParent(data[iPARENT]);
	            group.setChildren(child);
	            group.setRoot(data[iIS_ROOT].equals("true"));
	            group.setAliveStatus(Integer.parseInt(data[iALIVE_STATUS]));
	            
	            groupList.add(group);
	        }
        }
        
        db.disconnect();
        
        return groupList;
	}
	
	public JEntry findGroupTree() {
		if(groupList.size() > 0 ){
			for(JGroup group1 : groupList){
				ArrayList<String> child = new ArrayList<String>();
				for(JGroup group2 : groupList){
					
					if(group1.getId().equals(group2.getParent())){
						child.add(group2.getId());
						group1.setChildren(child);
					}
				}
			}
			
			List<JGroup> resultList = new ArrayList<JGroup>();
			for(JGroup group : groupList){
				if(group.isRoot()){
					resultList.add(group);
					groupList.remove(group);
					break;
				}
			}
			
			if(resultList.size() == 0){
				resultList.add(groupList.remove(0));
			}
			
			int currIndex = 0;
		    while (resultList.size() > currIndex) {
		        JGroup currNode = resultList.get(currIndex);
		        int childIndex = currIndex;
		        
		        if(currNode.getChildren().size()>0){
		        	for(String child : currNode.getChildren()){
			        	for(JGroup group : groupList){
				        	if(group.getId().equals(child)){
				        		resultList.add(++childIndex, group);
			                    groupList.remove(group);
			                    break;
				        	}
				        }
		        	}
		        }
		        else {
		        	for(JGroup group : groupList){
			        	if(group.getParent().equals(currNode.getId())){
			        		resultList.add(++childIndex, group);
		                    groupList.remove(group);
		                    break;
			        	}
			        }
		        }
		        currIndex++;
		    }
		    
		    
		    
		    int nodeIndex = resultList.size()-1;
		    
		    while(nodeIndex>=0){
		    	JGroup currNode = resultList.get(nodeIndex);
		    	for(JGroup group : resultList){
		    		if(currNode.getParent().equals(group.getId())){
		    			if(group.getEntry() == null){
		    				if(currNode.getEntry() == null){
		    					group.setEntry(new JEntry(group.getName(), "", group.getAliveStatus(), ""));
		    					group.getEntry().add(new JEntry(currNode.getName(), "", currNode.getAliveStatus(), ""));
				    			group.setEntry(group.getEntry());
		    				}
		    				else{
		    					group.setEntry(new JEntry(group.getName(), "", group.getAliveStatus(), ""));
			    				group.getEntry().add(currNode.getEntry());
				    			group.setEntry(group.getEntry());
		    				}
		    			}
		    			else {
		    				if(currNode.getEntry() == null){
		    					group.getEntry().add(new JEntry(currNode.getName(), "", currNode.getAliveStatus(), ""));
				    			group.setEntry(group.getEntry());
		    				}
		    				else{
			    				group.getEntry().add(currNode.getEntry());
				    			group.setEntry(group.getEntry());
		    				}
		    			}
		    			//System.out.println(group.getName()+" "+gson.toJson(group.getEntry()));
		    		}
		    	}
		    	if(currNode.isRoot()){
	    			currNode.setEntry(new JEntry(currNode.getName(), "", currNode.getAliveStatus(), ""));
	    			for(JGroup group : resultList){
		    			for(String child : currNode.getChildren()){
		    				if(group.getId().equals(child)){
		    					if(group.getEntry() == null){
		    						currNode.getEntry().add(new JEntry(group.getName(), "", group.getAliveStatus(), ""));
		    					}
		    					else{
			    					currNode.getEntry().add(group.getEntry());
			    					currNode.setEntry(currNode.getEntry());
			    					//System.out.println(currNode.getName()+" "+gson.toJson(currNode.getEntry()));
		    					}
		    				}
		    			}
	    			}
	    		}
		    	
		    	nodeIndex--;
		    }
		    
		    for(JGroup group : resultList){
		    	//System.out.println(group.getName());
		    	if(group.isRoot()){
		    		//System.out.println(new Gson().toJson(group.getEntry()));
		    		return group.getEntry();
		    	}
		    }
		}
		return null;
	}
	
	public List<JEntry> findGroupDevice(String name, List<JDevice> deviceList) {
		List<JNode> inputList = new ArrayList<JNode>();
		ArrayList<String> child = new ArrayList<String>();
		for (JDevice device : deviceList) {
	        // Node for switch device
	        JNode switchNode = new JNode();
	        switchNode.setPublicIp(device.getPublicIp());
	        switchNode.setParentIp(device.getParentIp());
	        switchNode.setDeviceType(device.getDeviceType());
	        switchNode.setGroupName(device.getGroupName());
	        switchNode.setAlive(device.getAlive());
	        switchNode.setAliasName(device.getAliasName());
	        inputList.add(switchNode);
		}
		
		List<JGroup> groupList = new ArrayList<JGroup>();
		Iterator<JNode> iter = inputList.iterator();
		while(iter.hasNext()){
			JNode device = iter.next();
        	if(device.getGroupName().equals(name)){
        		JGroup group = new JGroup();
        		group.setName(device.getPublicIp());
        		group.setAliasName(device.getAliasName());
        		group.setParent(device.getParentIp());
        		group.setChildren(child);
        		group.setType(device.getDeviceType());
        		group.setBelongGroup(true);
        		group.setAliveStatus(device.isAlive());
        		groupList.add(group);
        		iter.remove();
        	}
        }
		
		List<String> tempParent = new ArrayList<String>();
		
		Iterator<JNode> iter2 = inputList.iterator();
		while(iter2.hasNext()){
			JNode device = iter2.next();
			for(JGroup group : groupList){
				if(group.getParent() != null && group.getParent().equals(device.getPublicIp()) ){
					tempParent.add(device.getPublicIp() + "," + device.getDeviceType() + "," + device.getAliasName());
					if(!tempParent.contains(device.getPublicIp() + "," + device.getDeviceType() + "," + device.getAliasName())){
						iter2.remove();
					}
				}
			}
		}
		
		for(String parentIp : tempParent){
			String[] data = parentIp.split(",");
			JGroup group = new JGroup();
        		group.setName(data[0]);
        		group.setAliasName(data[2]);
        		group.setChildren(child);
        		group.setType(data[1]);
        		group.setBelongGroup(false);
        		groupList.add(group);
		}
		
		/*for(JGroup group : groupList){
			System.out.println(group.getName());
		}*/
		
		return findGroupDeviceTree(groupList);
	}
	
	public List<JEntry> findGroupDeviceTree(List<JGroup> groupList) {
		List<JEntry> entryList = new ArrayList<JEntry>();
		
		while(groupList.size() > 0){
			for(JGroup group1 : groupList){
				ArrayList<String> child = new ArrayList<String>();
				for(JGroup group2 : groupList){
					
					if(group1.getName().equals(group2.getParent())){
						child.add(group2.getName());
						group1.setChildren(child);
					}
				}
			}
			
			List<JGroup> resultList = new ArrayList<JGroup>();
			for(JGroup group : groupList){
				if(group.getParent() == null){
					resultList.add(group);
					groupList.remove(group);
					break;
				}
			}
			
			if(resultList.size() == 0){
				resultList.add(groupList.remove(0));
			}
			
			int currIndex = 0;
		    while (resultList.size() > currIndex) {
		        JGroup currNode = resultList.get(currIndex);
		        int childIndex = currIndex;
		        
		        if(currNode.getChildren().size()>0){
		        	for(String child : currNode.getChildren()){
			        	for(JGroup group : groupList){
				        	if(group.getName().equals(child)){
				        		resultList.add(++childIndex, group);
			                    groupList.remove(group);
			                    break;
				        	}
				        }
		        	}
		        }
		        else {
		        	for(JGroup group : groupList){
			        	if(group.getParent() != null && group.getParent().equals(currNode.getName())){
			        		resultList.add(++childIndex, group);
		                    groupList.remove(group);
		                    break;
			        	}
			        }
		        }
		        currIndex++;
		    }
		    
		    boolean hasNext = false;
		    
		    int nodeIndex = resultList.size()-1;
		    
		    while(nodeIndex>=0){
		    	JGroup currNode = resultList.get(nodeIndex);
		    	for(JGroup group : resultList){
		    		if(currNode.getParent() != null && currNode.getParent().equals(group.getName())){
		    			if(group.getEntry() == null){
		    				if(currNode.getEntry() == null){
		    					group.setEntry(new JEntry(group.getName(), group.getAliasName(), group.getAliveStatus(), group.getType()));
		    					group.getEntry().add(new JEntry(currNode.getName(), currNode.getAliasName(), currNode.getAliveStatus(), currNode.getType()));
				    			group.setEntry(group.getEntry());
		    				}
		    				else{
		    					group.setEntry(new JEntry(group.getName(), group.getAliasName(), group.getAliveStatus(), group.getType()));
			    				group.getEntry().add(currNode.getEntry());
				    			group.setEntry(group.getEntry());
		    				}
		    			}
		    			else {
		    				if(currNode.getEntry() == null){
		    					group.getEntry().add(new JEntry(currNode.getName(), currNode.getAliasName(), currNode.getAliveStatus(), currNode.getType()));
				    			group.setEntry(group.getEntry());
		    				}
		    				else{
			    				group.getEntry().add(currNode.getEntry());
				    			group.setEntry(group.getEntry());
		    				}
		    			}
		    			hasNext = true;
		    		}
		    	}
		    	if(currNode.getParent() == null){
		    		if(hasNext == true) {
		    			currNode.setEntry(new JEntry(currNode.getName(), currNode.getAliasName(), 3, currNode.getType()));
		    		}
		    		else {
		    			currNode.setEntry(new JEntry(currNode.getName(), currNode.getAliasName(), currNode.getAliveStatus(), currNode.getType()));
		    		}
	    			for(JGroup group : resultList){
		    			for(String child : currNode.getChildren()){
		    				if(group.getName().equals(child)){
		    					if(group.getEntry() == null){
		    						currNode.getEntry().add(new JEntry(group.getName(), group.getAliasName(), group.getAliveStatus(), group.getType()));
		    					}
		    					else{
			    					currNode.getEntry().add(group.getEntry());
			    					currNode.setEntry(currNode.getEntry());
			    					//System.out.println(currNode.getName()+" "+gson.toJson(currNode.getEntry()));
		    					}
		    				}
		    			}
	    			}
	    		}
		    	
		    	nodeIndex--;
		    }
		    for(JGroup group : resultList){
		    	//System.out.println(group.getName());
		    	if(group.getParent() == null && group.isBelongGroup() || group.getParent() == null && hasNext == true){
		    		//System.out.println(new Gson().toJson(group.getEntry()));
		    		entryList.add(group.getEntry());
		    	}
		    }
		}
		
		
		return entryList;
	}
}
