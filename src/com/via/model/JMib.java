package com.via.model;

import java.util.List;
import java.util.Map;

public class JMib {
	public static final String[] unavailableType = {JCommon.notAvailable, JCommon.notAvailable};
	public static final String[] unknownType = {JCommon.unknown, JCommon.unknown};
	public static final String[] thousandFull = {JCommon.speed1000M, JCommon.duplexFull};		// speed, duplex
	public static final String[] thousandHalf = {JCommon.speed1000M, JCommon.duplexHalf};
	public static final String[] hundredFull = {JCommon.speed100M, JCommon.duplexFull};
	public static final String[] hundredHalf = {JCommon.speed100M, JCommon.duplexHalf};
	public static final String[] tenFull = {JCommon.speed10M, JCommon.duplexFull};
	public static final String[] tenHalf = {JCommon.speed10M, JCommon.duplexHalf};
	public static final String prefixOidMauType = "1.3.6.1.2.1.26.4.";

	public static String[] getMauTypes(String oid) {
		if (!oid.startsWith(prefixOidMauType)) {
			return unavailableType;
		}

		int mauId;
		try {
			mauId = Integer.parseInt(oid.substring(prefixOidMauType.length()));
		} catch (NumberFormatException e) {
			return unavailableType;
		}

		String[] mauType;
		switch (mauId) {
		case 30:					// 1000BaseTFD
			mauType = thousandFull;
			break;
		case 29:					// 1000BaseTHD
			mauType = thousandHalf;
			break;
		case 28:					// 1000BaseCXFD
			mauType = thousandFull;
			break;
		case 27:					// 1000BaseCXHD
			mauType = thousandHalf;
			break;
		case 26:					// 1000BaseSXFD
			mauType = thousandFull;
			break;
		case 25:					// 1000BaseSXHD
			mauType = thousandHalf;
			break;
		case 24:					// 1000BaseLXFD
			mauType = thousandFull;
			break;
		case 23:					// 1000BaseLXHD
			mauType = thousandHalf;
			break;
		case 22:					// 1000BaseXFD
			mauType = thousandFull;
			break;
		case 21:					// 1000BaseXHD
			mauType = thousandHalf;
			break;
		case 20:					// 100BaseT2FD
			mauType = hundredFull;
			break;
		case 19:					// 100BaseT2HD
			mauType = hundredHalf;
			break;
		case 18:					// 100BaseFXFD
			mauType = hundredFull;
			break;
		case 17:					// 100BaseFXHD
			mauType = hundredHalf;
			break;
		case 16:					// 100BaseTXFD
			mauType = hundredFull;
			break;
		case 15:					// 100BaseTXHD
			mauType = hundredHalf;
			break;
		case 14:					// 100BaseT4
			mauType = unavailableType;
			break;
		case 13:					// 10BaseFLFD
			mauType = tenFull;
			break;
		case 12:					// 10BaseFLHD
			mauType = tenHalf;
			break;
		case 11:					// 10BaseTFD
			mauType = tenFull;
			break;
		case 10:					// 10BaseTHD
			mauType = tenHalf;
			break;
		case 9:					// 10Broad36
		case 8:					// 10BaseFL
		case 7:					// 10BaseFB
		case 6:					// 10BaseFP
		case 5:					// 10BaseT
		case 4:					// 10Base2
		case 3:					// Foirl
		case 2:					// 10Base5
		case 1:					// AUI
			mauType = unavailableType;
			break;
		default:				// *** maybe some other unknown type ***
			mauType = unknownType;
		}

		return mauType;
	}
	
	public static void printRawMap(Map<String, List<String>> map, boolean isEnabled) {
		if (!isEnabled) return;
		System.out.println("***** size=" + map.size());
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			String index = entry.getKey();
			List<String> valueList = entry.getValue();
			System.out.println("index=" + index + ", value=" + valueList);
		}
	}
}
