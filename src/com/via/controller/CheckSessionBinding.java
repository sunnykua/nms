package com.via.controller;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.via.model.JUserInfo;

/**
 * Application Lifecycle Listener implementation class CheckSession
 *
 */
@WebListener
public class CheckSessionBinding implements HttpSessionBindingListener {

    /**
     * Default constructor. 
     */
	private String username;
    private JUserInfo ui = JUserInfo.getInstance();
    
    public CheckSessionBinding() {
    	this.username =  "";
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }

	/**
     * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent arg0) {
    	//HttpSession session = arg0.getSession();
    	//System.out.println("下線用戶 : " + this.username);
        if(this.username != ""){
            ui.removeUser(username);    //移除該上線用戶
            //ui.removeUserArray(username, session.getId());
        }
    }

	/**
     * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent arg0) {
    	//System.out.println("上線用戶 : " + this.username);
    }
	
}
