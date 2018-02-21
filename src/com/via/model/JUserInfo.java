package com.via.model;

import java.util.ArrayList;
import java.util.Vector;

public class JUserInfo {
	
	private static JUserInfo user = new JUserInfo();
    private Vector<String> list = null;
    private ArrayList<String[]> arraylist = null;
    
    public JUserInfo(){
        this.list = new Vector<String>();
        this.arraylist = new ArrayList<String[]>();
    }
    
    public static JUserInfo getInstance(){
        return user;
    }
    
    public boolean addUser(String user){   //add new one user, represent one user login. 
        if(user != null /*&& !list.contains(user)*/){
            this.list.add(user);
            //System.out.println("add list="+list);
            return true;
        }else{
            return false;
        }
    }
    
    public boolean addUserArray(String user, String id){
    	String [] data = new String[2];
    	data[0]=user;
    	data[1]=id;
        if(user != null){
            this.arraylist.add(data);
            for(int i=0;i<arraylist.size();i++){
            	if(arraylist.get(i)[0].equals(user) && !arraylist.get(i)[1].equals(id)){
            		arraylist.remove(i);
            	}
            	//System.out.println("add list=" + arraylist.get(i)[0] + " " + arraylist.get(i)[1]);
            }
            return true;
        }else{
            return false;
        }
    }
    
    public Vector<String> getList(){   
        return this.list;
    }
    
    public ArrayList<String[]> getArrayList(){   
        return this.arraylist;
    }
    
    public String[] getListArray(){
    	String[] listArray = list.toArray(new String[list.size()]);
    	
        return listArray;
    }
    
    public int getListSize(){   
        return this.list.size();
    }
    
    public void removeUser(String user){    //remove one user in array list, represent the user logout.
        if(user != null){
            this.list.removeElement(user);
            //System.out.println("remove list="+list);
        }
    }
    
    public void removeUserArray(String user, String id){    //移除某一個線上用戶  代表下線
        if(user != null){
        	
        	for(int i=0;i<arraylist.size();i++){
        		if(arraylist.get(i)[0].equals(user) && arraylist.get(i)[1].equals(id)){
        			arraylist.remove(i);
        			//System.out.println("remove list=" + arraylist.get(i)[0] + " " + arraylist.get(i)[1]);
        		}
            	//System.out.println("remove list=" + arraylist.get(i)[0] + " " + arraylist.get(i)[1]);
            }
        }
    }
}
