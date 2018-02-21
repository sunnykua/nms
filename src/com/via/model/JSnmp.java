package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;  
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

public class JSnmp {
	private Snmp snmp = null;
	private CommunityTarget target = null;
	private List<String> errorMessages = new ArrayList<String>();
	
	private static final String defaultAddress = "127.0.0.1/161";
	private static final String defaultCommunity = "public";
	private static final int defaultVersion = 2;
	private static final int defaultTimeout = 2000;
	private static final int defaultRetries = 1;
	
	private int version = 0;
	private UserTarget targetV3 = null;
	private static final OctetString PRIV = new OctetString("viaSwitch");
	private static final OID AUTH_PROTOCOL_MD5 = AuthMD5.ID;
	private static final OID AUTH_PROTOCOL_SHA = AuthSHA.ID;
	private static final OctetString AUTH_PASS = new OctetString("22185452");
	private static final OID PRIV_PROTOCOL_DES = PrivDES.ID;
	private static final OctetString PRIV_PASS = new OctetString("22185452");

	/**
	 * Create an SNMP object with default parameters.
	 * They are address = "127.0.0.1/161", community = "public", version = 2(2c), timeout = 1000 and retries = 1
	 * @throws IOException
	 */
	public JSnmp() throws IOException {
		this(defaultAddress, defaultCommunity, defaultVersion, defaultTimeout, defaultRetries);
	}

	/**
	 * Create an SNMP object with default parameters except address.
	 * They are community = "public", version = 2(2c), timeout = 1000 and retries = 1
	 * @param address
	 * @throws IOException
	 */
	public JSnmp(String address) throws IOException {
		this(address, defaultCommunity, defaultVersion, defaultTimeout, defaultRetries);
	}

	/**
	 * Create an SNMP object with specified parameters except community string and version.
	 * They are community = "public" and version = 2(2c)
	 * @param address
	 * @param timeout in millisecond
	 * @param retry
	 * @throws IOException
	 */
	public JSnmp(String address, int timeout, int retries) throws IOException {
		//snmp = new Snmp(new DefaultUdpTransportMapping());
		//setTarget("public", 2, address, timeout, retries);
		this(address, defaultCommunity, defaultVersion, timeout, retries);
	}
	
	public JSnmp(String address, String community, int version, int timeout, int retries) throws IOException {
		this.version = version;
		snmp = new Snmp(new DefaultUdpTransportMapping());
		setParameter(address, community, version, timeout, retries);
	}
	
	public JSnmp(String address, int version, int timeout, int retries, 
			String securityName, int securityLevel, String authProtocol, String authPass, String privacyProtocol, String privacyPass) throws IOException {
		
		//System.out.println("Snmp Version = " + version);
		//System.out.println("securityLevel = " + securityLevel);
		
		this.version = version;
		
		if(securityLevel==3){
			snmp = createSnmpSession(new OctetString(securityName), authProtocol.equals("MD5") ? AUTH_PROTOCOL_MD5 : AUTH_PROTOCOL_SHA, new OctetString(authPass), PRIV_PROTOCOL_DES, new OctetString(privacyPass));
		}
		else if(securityLevel==2){
			snmp = createSnmpSession(new OctetString(securityName), authProtocol.equals("MD5") ? AUTH_PROTOCOL_MD5 : AUTH_PROTOCOL_SHA, new OctetString(authPass), null, null);
		}
		else if(securityLevel==1){
			snmp = createSnmpSession(new OctetString(securityName), null, null, null, null);
		}
		setParameterV3(address, version, timeout, retries, new OctetString(securityName), securityLevel);
	}
	
	private static Snmp createSnmpSession(OctetString securityName, OID authProtocol, OctetString authPass, OID privacyProtocol, OctetString privacyPass) throws IOException {
	   Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
	   USM usm = new USM(SecurityProtocols.getInstance(), new
	   OctetString(MPv3.createLocalEngineID()), 0);
	   SecurityModels.getInstance().addSecurityModel(usm);
	   UsmUser user = new UsmUser(securityName, authProtocol, authPass, privacyProtocol, privacyPass);
	   snmp.getUSM().addUser(securityName, user);
	   return snmp;
	 }
/*
	private boolean setTarget(String community, int version, String address, int timeout, int retry) {
		boolean isOK = true;
		
		target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setVersion(version == 2 ? SnmpConstants.version2c :
			version == 3 ? SnmpConstants.version3 : SnmpConstants.version1);
		isOK = setAddress(address);
		target.setTimeout(timeout);
		target.setRetries(retry);
		
		return isOK;
	}
*/
	public void setParameter(String address, String community, int version, int timeout, int retries) {
		if (target == null) {
			target = new CommunityTarget();
		}
		
		target.setAddress(new UdpAddress(address));
		target.setCommunity(new OctetString(community));
		target.setVersion(version == 2 ? SnmpConstants.version2c :
			version == 3 ? SnmpConstants.version3 : SnmpConstants.version1);
		target.setTimeout(timeout);
		target.setRetries(retries);
	}
	
	public void setParameterV3(String address, int version, int timeout, int retries, OctetString securityName, int securityLevel) {
		if (targetV3 == null) {
			targetV3 = new UserTarget();
		}
		
		targetV3.setAddress(new UdpAddress(address));
		targetV3.setVersion(version == 2 ? SnmpConstants.version2c :
			version == 3 ? SnmpConstants.version3 : SnmpConstants.version1);
		targetV3.setTimeout(timeout);
		targetV3.setRetries(retries);
		targetV3.setSecurityLevel((Integer) (securityLevel == 3 ? SecurityLevel.AUTH_PRIV : securityLevel == 2 ? SecurityLevel.AUTH_NOPRIV : SecurityLevel.NOAUTH_NOPRIV));
		targetV3.setSecurityName(securityName);
	}
	
	public String getAddress() {
		return target != null ? target.getAddress().toString() : null;
	}
	
	public void setAddress(String address) {
		if (target != null) {
			target.setAddress(new UdpAddress(address));
			//System.out.println("Set Snmp addresss to " + address);
		}
	}

	public String getCommunity() {
		return target != null ? target.getCommunity().toString() : null;
	}
	
	public void setCommunity(String community) {
		if (target != null) {
			target.setCommunity(new OctetString(community));
			System.out.println("Set Snmp community to " + community);
		}
	}
	
	public int getVersion() {
		return target != null ? target.getVersion() : -1;
	}
	
	public void setVersion(int version) {
		if (target != null) {
			target.setVersion(version == 2 ? SnmpConstants.version2c : version == 3 ? SnmpConstants.version3 : SnmpConstants.version1);
			System.out.println("Set Snmp version to " + version);
		}
	}
	
	public long getTimeout() {
		return target != null ? target.getTimeout() :-1;
	}
	
	public void setTimeout(int timeout) {
		if (target != null) {
			target.setTimeout(timeout);
			//System.out.println("Set Snmp timeout to " + timeout);
		}
	}
	
	public int getRetries() {
		return target != null ? target.getRetries() : -1;
	}
	
	public void setRetries(int retries) {
		if (target != null) {
			target.setRetries(retries);
			//System.out.println("Set Snmp retries to " + retries);
		}
	}
	
/*	
	public boolean setAddress(String address) {
		if (target == null) return false;
		boolean isOK = true;

		try {
			target.setAddress(new UdpAddress(address));
		} catch (IllegalArgumentException e) {
			target.setAddress(new UdpAddress("127.0.0.1/161"));
			errorMessages.add("Address invalid.");
			isOK = false;
		}

		return isOK;
	}

	public boolean setCommunity(String community) {
		if (target == null) return false;

		target.setCommunity(new OctetString(community));

		return true;
	}
*/
	/*##################################################*/
	/**
	 * Get one value from an OID by using 'Get' command
	 * Note: Invalid OID will return 'noSuchInstance' string.
	 * @param oid
	 * @return
	 * @throws IOException
	 */
	public String getNode(String oid) throws IOException {
		errorMessages.clear();
		List<String> result = sendRequest(createGetPdu(oid));
		if (result.size() > 1) System.out.println("Get has not only one value.");
		return result.size() == 0 ? "" : result.get(0);
	}

	/**
	 * Get values from corresponding OIDs by using 'Get' command.
	 * @param oidList A List of queried Oids.
	 * @return The values from queried Oids.
	 * @throws IOException
	 */
	/*public List<String> getNodes(List<String> oidList) throws IOException {
		errorMessages.clear();
		return sendRequest(createGetPdu(oidList));
	}*/
	public String[] getNodes(String[] oids) throws IOException {
		errorMessages.clear();
		return sendRequest(createGetPdu(oids)).toArray(new String[0]);
	}

	/**
	 * Get a number of values from an OID by using 'GetBulk' command.
	 * @param oid
	 * @param maxNumber The number of got values one time.
	 * @return
	 * @throws IOException
	 */
	public List<String> getNodes(String oid, int maxNumber) throws IOException {
		errorMessages.clear();
		return sendRequest(createGetBulkPdu(oid, maxNumber));
	}

	/**
	 * Get the value next to specified OID by using 'GetNext' command.
	 * @param oid
	 * @return The value of this OID.
	 * @throws IOException
	 */
	public String getNextNode(String oid) throws IOException {
		errorMessages.clear();
		List<String> result = sendRequest(createGetNextPdu(oid));
		if (result.size() > 1) System.out.println("GetNext has not only one value.");
		return result.size() == 0 ? "" : result.get(0);
	}
	
	/**
	 * Get values next to specified OIDs by using 'GetNext' command.
	 * @param oidList
	 * @return
	 * @throws IOException
	 */
	/*public List<String> getNextNodes(List<String> oidList) throws IOException {
		errorMessages.clear();
		return sendRequest(createGetNextPdu(oidList));
	}*/
	public String[] getNextNodes(String[] oids) throws IOException {
		errorMessages.clear();
		return sendRequest(createGetNextPdu(oids)).toArray(new String[0]);
	}
	
	public boolean setNode(String oid, String value) throws IOException {
		errorMessages.clear();
		
		List<String> result;
		
		if(version == 3){
			result = sendRequest(createSetPdu(oid, value));
		}
		else {
		
		OctetString community = target.getCommunity();
		
		target.setCommunity(new OctetString("private"));
		result = sendRequest(createSetPdu(oid, value));
		
		target.setCommunity(community);
		
		}
		
		if (result.isEmpty()) return false;
		
		return true;
	}
	
	public boolean setNode(String oid, int value) throws IOException {
		errorMessages.clear();
		
		List<String> result;
		
		if(version == 3){
			result = sendRequest(createSetPdu(oid, value));
		}
		else {
		
		OctetString community = target.getCommunity();
		
		target.setCommunity(new OctetString("private"));
		result = sendRequest(createSetPdu(oid, value));
		
		target.setCommunity(community);
		
		}
		
		if (result.isEmpty()) return false;
		
		return true;
	}
	
	/**
	 * Get a range of values of specified columns from any table
	 * @param columnOIDs
	 * <p style="text-indent:25px;">An array of OIDs of the columnar objects whose instances should be retrieved.
	 * The columnar objects may belong to different tables.</p>
	 * @param lowerBoundIndex
	 * <p style="text-indent:25px;">Optional. If not null, all returned rows have an index greater than lowerBoundIndex.</p>
	 * @param upperBoundIndex
	 * <p style="text-indent:25px;">Optional. If not null, all returned rows have an index less or equal than upperBoundIndex.</p>
	 * @return
	 * <p>A Map that keys are table index and values are data of queried columns.
	 * Values belong to a key is a List, and it's number should be equal to queried columns.</p>
	 */
	public Map<String, List<String>> getTable(String[] columnOIDs, int lowerBoundIndex, int upperBoundIndex) {
		errorMessages.clear();
		OID lowerId = new OID(String.valueOf(lowerBoundIndex));
		OID upperId = new OID(String.valueOf(upperBoundIndex));
		OID[] colOIDs = new OID[columnOIDs.length];
		for (int i = 0; i < columnOIDs.length; i++) {
			colOIDs[i] = new OID(columnOIDs[i]);
		}
		
		return walkTable(colOIDs, lowerId, upperId);			// TODO: seems no work to multiple index table
	}
	
	/**
	 * Get all values of specified columns from any table.
	 * @param columnOIDs
	 * <p style="text-indent:25px;">An array of OIDs of the columnar objects whose instances should be retrieved.
	 * The columnar objects may belong to different tables.</p>
	 * @return
	 * <p>A Map that keys are table index and values are data of queried columns.
	 * Values belong to a key is a List, and it's number should be equal to queried columns.</p>
	 */
	public Map<String, List<String>> getTable(String[] columnOIDs) {
		errorMessages.clear();

		OID[] colOIDs = new OID[columnOIDs.length];
		for (int i = 0; i < columnOIDs.length; i++) {
			colOIDs[i] = new OID(columnOIDs[i]);
		}

		return walkTable(colOIDs);
	}

	public void discoverAgent(String oid) throws IOException {
		errorMessages.clear();

		System.out.println("addr:" + target.getAddress() + " timeout:" + target.getTimeout() +
				" retry:" + target.getRetries() + " ver:" + target.getVersion());
		sendAsyncRequest(createGetPdu(oid));
	}

	/**
	 * @return
	 */
	public String getLastError() {
		return errorMessages.size() != 0 ? errorMessages.get(errorMessages.size() - 1) : "";
	}

	/**
	 * Starts the listen mode.
	 * @throws IOException
	 */
	public void start() throws IOException {
		if (snmp != null) snmp.listen();
	}

	/**
	 * Free all resources.
	 * @throws IOException
	 */
	public void end() throws IOException {
		if (snmp != null) snmp.close();
	}

	/******************************************************************************************************/
	/**
	 * SNMP GET
	 * @param oid
	 * @return
	 */
	private PDU createGetPdu(String oid) {
		if(version == 3){
			ScopedPDU pdu = new ScopedPDU();
			pdu.setType(PDU.GET);
	        pdu.add(new VariableBinding(new OID(oid)));
	        return pdu;
		} else {	
			PDU pdu = new PDU();
	        pdu.setType(PDU.GET);
	        pdu.add(new VariableBinding(new OID(oid)));
	        return pdu;
		}
	}
	
	/**
	 * SNMP GET
	 * @param oidList
	 * @return
	 */
	/*private PDU createGetPdu(List<String> oidList) {
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        for (String oid : oidList)
        	pdu.add(new VariableBinding(new OID(oid)));
        return pdu;
    }*/
	private PDU createGetPdu(String[] oids) {
		if(version == 3){
			ScopedPDU pdu = new ScopedPDU();
			pdu.setType(PDU.GET);
	        for (String oid : oids)
	        	pdu.add(new VariableBinding(new OID(oid)));
	        return pdu;
		}else {
			PDU pdu = new PDU();
			pdu.setType(PDU.GET);
	        for (String oid : oids)
	        	pdu.add(new VariableBinding(new OID(oid)));
	        return pdu;
		}
        
    }
	
	/*##################################################*/
	/**
	 * SNMP GETNEXT
	 * @param oid
	 * @return
	 */
	private PDU createGetNextPdu(String oid) {
		PDU pdu = createGetPdu(oid);
		pdu.setType(PDU.GETNEXT);
		return pdu;
	}
	
	/**
	 * SNMP GETNEXT
	 * @param oidList
	 * @return
	 */
	/*private PDU createGetNextPdu(List<String> oidList) {
        PDU pdu = createGetPdu(oidList);
        pdu.setType(PDU.GETNEXT);
        return pdu;
    }*/
	private PDU createGetNextPdu(String[] oids) {
        PDU pdu = createGetPdu(oids);  
        pdu.setType(PDU.GETNEXT);
        return pdu;
    }
	
	/*##################################################*/
	/**
	 * SNMP GETBULK
	 * @return
	 */
	private PDU createGetBulkPdu(String oid, int maxNum) {
		if(version == 3){
			ScopedPDU pdu = new ScopedPDU();
			pdu.setType(PDU.GETBULK);
	        pdu.setMaxRepetitions(maxNum);  // must set it, default is 0
	        pdu.setNonRepeaters(0);
	        pdu.add(new VariableBinding(new OID(oid)));
	        return pdu;
		} else {
	        PDU pdu = new PDU();
	        pdu.setType(PDU.GETBULK);
	        pdu.setMaxRepetitions(maxNum);  // must set it, default is 0
	        pdu.setNonRepeaters(0);
	        pdu.add(new VariableBinding(new OID(oid)));
	        return pdu;
		}
    }
	
	/*##################################################*/
	/**
	 * SNMP SET
	 * @return
	 */
	private PDU createSetPdu(String oid, String value) {
		if(version == 3){
			ScopedPDU pdu = new ScopedPDU();
			pdu.setType(PDU.SET);
			pdu.add(new VariableBinding(new OID(oid), new OctetString(value)));
			return pdu;
		} else {
			PDU pdu = new PDU();
			pdu.setType(PDU.SET);
			pdu.add(new VariableBinding(new OID(oid), new OctetString(value)));
			return pdu;
		}
	}
	
	/*##################################################*/
	/**
	 * SNMP SET
	 * @return
	 */
	private PDU createSetPdu(String oid, int value) {
		if(version == 3){
			ScopedPDU pdu = new ScopedPDU();
			pdu.setType(PDU.SET);
			pdu.add(new VariableBinding(new OID(oid), new Integer32(value)));
			return pdu;
		} else {
			PDU pdu = new PDU();
			pdu.setType(PDU.SET);
			pdu.add(new VariableBinding(new OID(oid), new Integer32(value)));
			return pdu;
		}
	}

	/******************************************************************************************************/
	/**
	 * @param pdu
	 * @return
	 * @throws IOException
	 */
	private List<String> sendRequest(PDU pdu) throws IOException {
		List<String> result = new ArrayList<String>();
		
		if (snmp == null || pdu == null) {
			errorMessages.add("Not initialed.");
			return result;
		}
		
		ResponseEvent responseEvent = snmp.send(pdu, version == 3 ? targetV3 : target);
		PDU response = responseEvent.getResponse();

		if (response == null) {
			errorMessages.add("TimeOut...");
			//System.out.println("TimeOut, response is null.");
		}
		else {
			if (response.getErrorStatus() == PDU.noError) {
				Vector<? extends VariableBinding> vbs = response.getVariableBindings();
				for (VariableBinding vb : vbs) {
					result.add(vb.getVariable().toString());
					//System.out.println(vb.getOid() + " = " + vb.getVariable());
				}
			}
			else {
				errorMessages.add("Error:" + response.getErrorStatusText());
				System.out.println("SNMP sendRequest() errorStatus:" + response.getErrorStatus() + ", errorStatusText:" + response.getErrorStatusText());
			}
		}
		
		return result;
	}
	
	/*##################################################*/
	/**
	 * @param pdu
	 * @param target
	 * @throws IOException
	 */
	private void sendAsyncRequest(PDU pdu) throws IOException {
		snmp.send(pdu, version == 3 ? targetV3 : target, null, new ResponseListener(){

			@Override
			public void onResponse(ResponseEvent event) {  
				PDU response = event.getResponse();
				System.out.println("Got response from " + event.getPeerAddress());  
				if (response == null) {
					System.out.println("TimeOut...");  
				}
				else {
					if (response.getErrorStatus() == PDU.noError) {  
						Vector<? extends VariableBinding> vbs = response.getVariableBindings();  
						for (VariableBinding vb : vbs) {
							//System.out.println(vb + " ," + vb.getVariable().getSyntaxString());  
							System.out.println(vb.getOid() + " = " + vb.getVariable());
						}
					}
					else {  
						System.out.println("Error:" + response.getErrorStatusText());  
					}
				}
			}});
	}

	/*##################################################*/
	/**
	 * @param columns
	 * @param lower
	 * @param upper
	 * @return
	 */
	private Map<String, List<String>> walkTable(OID[] columns, OID lower, OID upper) {
		TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(version == 3 ? ScopedPDU.GETBULK : version == 2 ? PDU.GETBULK : PDU.GETNEXT));	//GETNEXT or GETBULK
		utils.setMaxNumRowsPerPDU(20);   //only for GETBULK, set max-repetitions, default is 10
		utils.setMaxNumColumnsPerPDU(5);
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();

		// If not null, all returned rows have an index in a range (lowerBoundIndex, upperBoundIndex]
		List<TableEvent> listRows = utils.getTable(version == 3 ? targetV3 : target, columns, lower, upper);
		if (listRows == null) return result;
		
		for (TableEvent e : listRows) {
			if (e.isError()) continue;
			
			List<VariableBinding> vbList = Arrays.asList(e.getColumns());	
			List<String> stringList = new ArrayList<String>();
			
			//System.out.println("vbList size: " + vbList.size());		// vbList size will be the same as input columns
			for (VariableBinding vb : vbList) {
				if (vb == null) stringList.add("NULL_ITEM");					// null element
				else stringList.add(vb.getVariable().toString());					// only store the value part
			}
//			System.out.println("key=" + e.getIndex() + ", Columns=" + stringList);
			result.put(e.getIndex().toString(), stringList);
		}
		
		return result;
	}

	private Map<String, List<String>> walkTable(OID[] columns) {
		TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(version == 3 ? ScopedPDU.GETBULK : version == 2 ? PDU.GETBULK : PDU.GETNEXT));	//GETNEXT or GETBULK
		utils.setMaxNumRowsPerPDU(20);   //only for GETBULK, set max-repetitions, default is 10
		utils.setMaxNumColumnsPerPDU(5);
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();

		List<TableEvent> listRows = utils.getTable(version == 3 ? targetV3 : target, columns, null, null);	// List contains all possible indexes; data with the same index
																					//    will place together, otherwise place to a new item.
		if (listRows == null) return result;

		for (TableEvent e : listRows) {
			if (e.isError()) continue;
			List<String> dataList = new ArrayList<String>();

			for (VariableBinding vb : e.getColumns()) {								// column with mismatch index will get null vb
				dataList.add(vb == null ? "null" : vb.getVariable().toString());	// keep the size are all equal by filling null
			}
//			System.out.println("key=" + e.getIndex() + ", Columns=" + stringList);
			result.put(e.getIndex().toString(), dataList);
		}

		return result;
	}
}
