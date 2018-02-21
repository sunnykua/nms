package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JEtherlikeStatistics {
	
	public class JEtherlikeStatisticsItem{

		private int portId;
		private int ifIndex;
		private int stackId;
		private String alignmentErrors;
		private String fcsErrors;
		private String internalMacReceiveErrors;
		private String symbolErrors;
		private String sqeTestErrors;
		private String internalMacTransmitErrors;
		private String carrierSenseErrors;
		private String frameTooLongs;
		private String deferredTransmissions;
		private String lateCollisions;
		private String singleCollisionFrames;
		private String multipleCollisionFrames;
		private String excessiveCollisions;
		private String inPauseFrames;
		private String outPauseFrames;

		public int getPortId() {
			return portId;
		}
		public void setPortId(int portId) {
			this.portId = portId;
		}
		public int getIfIndex() {
			return ifIndex;
		}
		public void setIfIndex(int ifIndex) {
			this.ifIndex = ifIndex;
		}
		public int getStackId() {
			return stackId;
		}
		public void setStackId(int stackId) {
			this.stackId = stackId;
		}
		public String getAlignmentErrors() {
			return alignmentErrors;
		}
		public void setAlignmentErrors(String alignmentErrors) {
			this.alignmentErrors = alignmentErrors;
		}
		public String getFcsErrors() {
			return fcsErrors;
		}
		public void setFcsErrors(String fcsErrors) {
			this.fcsErrors = fcsErrors;
		}
		public String getInternalMacReceiveErrors() {
			return internalMacReceiveErrors;
		}
		public void setInternalMacReceiveErrors(String internalMacReceiveErrors) {
			this.internalMacReceiveErrors = internalMacReceiveErrors;
		}
		public String getSymbolErrors() {
			return symbolErrors;
		}
		public void setSymbolErrors(String symbolErrors) {
			this.symbolErrors = symbolErrors;
		}
		public String getSqeTestErrors() {
			return sqeTestErrors;
		}
		public void setSqeTestErrors(String sqeTestErrors) {
			this.sqeTestErrors = sqeTestErrors;
		}
		public String getInternalMacTransmitErrors() {
			return internalMacTransmitErrors;
		}
		public void setInternalMacTransmitErrors(String internalMacTransmitErrors) {
			this.internalMacTransmitErrors = internalMacTransmitErrors;
		}
		public String getCarrierSenseErrors() {
			return carrierSenseErrors;
		}
		public void setCarrierSenseErrors(String carrierSenseErrors) {
			this.carrierSenseErrors = carrierSenseErrors;
		}
		public String getFrameTooLongs() {
			return frameTooLongs;
		}
		public void setFrameTooLongs(String frameTooLongs) {
			this.frameTooLongs = frameTooLongs;
		}
		public String getDeferredTransmissions() {
			return deferredTransmissions;
		}
		public void setDeferredTransmissions(String deferredTransmissions) {
			this.deferredTransmissions = deferredTransmissions;
		}
		public String getLateCollisions() {
			return lateCollisions;
		}
		public void setLateCollisions(String lateCollisions) {
			this.lateCollisions = lateCollisions;
		}
		public String getSingleCollisionFrames() {
			return singleCollisionFrames;
		}
		public void setSingleCollisionFrames(String singleCollisionFrames) {
			this.singleCollisionFrames = singleCollisionFrames;
		}
		public String getMultipleCollisionFrames() {
			return multipleCollisionFrames;
		}
		public void setMultipleCollisionFrames(String multipleCollisionFrames) {
			this.multipleCollisionFrames = multipleCollisionFrames;
		}
		public String getExcessiveCollisions() {
			return excessiveCollisions;
		}
		public void setExcessiveCollisions(String excessiveCollisions) {
			this.excessiveCollisions = excessiveCollisions;
		}
		public String getInPauseFrames() {
			return inPauseFrames;
		}
		public void setInPauseFrames(String inPauseFrames) {
			this.inPauseFrames = inPauseFrames;
		}
		public String getOutPauseFrames() {
			return outPauseFrames;
		}
		public void setOutPauseFrames(String outPauseFrames) {
			this.outPauseFrames = outPauseFrames;
		}	
	}
	
	private List<JEtherlikeStatisticsItem> etherlikeStatisticsItems;
	
	public JEtherlikeStatistics() {
		this.etherlikeStatisticsItems = new ArrayList<JEtherlikeStatisticsItem>();
	}
	
	public void addEtherlikeStatistics(int portId, int ifIndex, int stackId, 
			String alignmentErrors, String fcsErrors, String internalMacReceiveErrors, String symbolErrors, String sqeTestErrors, String internalMacTransmitErrors, String carrierSenseErrors,
			String frameTooLongs, String deferredTransmissions, String lateCollisions, String singleCollisionFrames, String multipleCollisionFrames, String excessiveCollisions, String inPauseFrames, String outPauseFrames){
		JEtherlikeStatisticsItem item = new JEtherlikeStatisticsItem();
		item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);
		item.setAlignmentErrors(alignmentErrors);
		item.setFcsErrors(fcsErrors);
		item.setInternalMacReceiveErrors(internalMacReceiveErrors);
		item.setSymbolErrors(symbolErrors);
		item.setSqeTestErrors(sqeTestErrors);
		item.setInternalMacTransmitErrors(internalMacTransmitErrors);
		item.setCarrierSenseErrors(carrierSenseErrors);
		item.setFrameTooLongs(frameTooLongs);
		item.setDeferredTransmissions(deferredTransmissions);
		item.setLateCollisions(lateCollisions);
		item.setSingleCollisionFrames(singleCollisionFrames);
		item.setMultipleCollisionFrames(multipleCollisionFrames);
		item.setExcessiveCollisions(excessiveCollisions);
		item.setInPauseFrames(inPauseFrames);
		item.setOutPauseFrames(outPauseFrames);
		this.etherlikeStatisticsItems.add(item);
	}
	
	public final List<JEtherlikeStatisticsItem> getEtherlikeStatisticsItems() {
		return etherlikeStatisticsItems;
	}
}
