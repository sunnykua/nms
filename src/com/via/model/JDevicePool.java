package com.via.model;

import java.util.ArrayList;

public class JDevicePool extends ArrayList<JDevice> {
	private static final long serialVersionUID = 1L;

	public boolean addDevice(JDevice device) {
		return add(device);
	}
}
