package com.via.model;

public class JPortStatisticsItem {

	private int index;
	private String rxOctets;
	private String rxUnicast;
	private String rxMulticast;
	private String rxBroadcast;
	private String rxErrors;
	private String rxDiscards;
	private String rxUnknowns;
	private String txOctets;
	private String txUnicast;
	private String txMulticast;
	private String txBroadcast;
	private String txErrors;
	private String txDiscards;
	
	public JPortStatisticsItem() {
		this.index = 0;
		this.rxOctets = "-";
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getRxOctets() {
		return rxOctets;
	}
	public void setRxOctets(String rxOctets) {
		this.rxOctets = rxOctets;
	}
	public String getRxUnicast() {
		return rxUnicast;
	}
	public void setRxUnicast(String rxUnicast) {
		this.rxUnicast = rxUnicast;
	}
	public String getRxMulticast() {
		return rxMulticast;
	}
	public void setRxMulticast(String rxMulticast) {
		this.rxMulticast = rxMulticast;
	}
	public String getRxBroadcast() {
		return rxBroadcast;
	}
	public void setRxBroadcast(String rxBroadcast) {
		this.rxBroadcast = rxBroadcast;
	}
	public String getRxErrors() {
		return rxErrors;
	}
	public void setRxErrors(String rxErrors) {
		this.rxErrors = rxErrors;
	}
	public String getRxDiscards() {
		return rxDiscards;
	}
	public void setRxDiscards(String rxDiscards) {
		this.rxDiscards = rxDiscards;
	}
	public String getRxUnknowns() {
		return rxUnknowns;
	}
	public void setRxUnknowns(String rxUnknowns) {
		this.rxUnknowns = rxUnknowns;
	}
	public String getTxOctets() {
		return txOctets;
	}
	public void setTxOctets(String txOctets) {
		this.txOctets = txOctets;
	}
	public String getTxUnicast() {
		return txUnicast;
	}
	public void setTxUnicast(String txUnicast) {
		this.txUnicast = txUnicast;
	}
	public String getTxMulticast() {
		return txMulticast;
	}
	public void setTxMulticast(String txMulticast) {
		this.txMulticast = txMulticast;
	}
	public String getTxBroadcast() {
		return txBroadcast;
	}
	public void setTxBroadcast(String txBroadcast) {
		this.txBroadcast = txBroadcast;
	}
	public String getTxErrors() {
		return txErrors;
	}
	public void setTxErrors(String txErrors) {
		this.txErrors = txErrors;
	}
	public String getTxDiscards() {
		return txDiscards;
	}
	public void setTxDiscards(String txDiscards) {
		this.txDiscards = txDiscards;
	}
}
