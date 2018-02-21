package com.via.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.AlreadyBoundException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.via.model.JTools;

public class RemoteService {
	private Registry registry;
	private RemoteInterface RemoteService;
	private boolean isRunning;

	public void setRemoteServiceisRunning(boolean run) {
		isRunning = run;
	}

	public boolean getRemoteServiceisRunning() {
		return isRunning;
	}

	public void start(String Address) {
		isRunning = true;

		System.out.println("Start Remote Service!");

		try {
			System.setProperty("java.rmi.server.useLocalHostname", "true");
			System.out.println("java.rmi.server.useLocalHostname: " + System.getProperty("java.rmi.server.useLocalHostname"));

			//要在RemoteInterface RemoteService = new RemoteImplement();之前設定
			System.setProperty("java.rmi.server.hostname", Address);
			System.out.println("java.rmi.server.hostname: " + System.getProperty("java.rmi.server.hostname"));

			//System.out.println("java.rmi.server.disableHttp:" + System.getProperty("java.rmi.server.disableHttp"));

			//System.out.println(port_check("", 55555));
			if (port_check("", 55555) != 55555) {
				//指定資料傳輸埠
				this.RemoteService = new RemoteImplement();
				System.out.println("UnicastRemoteObject.exportObject(this.RemoteService, 55555);");
				UnicastRemoteObject.exportObject(this.RemoteService, 55555);
			}
			//System.out.println(port_check("", 55555));

			//System.out.println(port_check("", 1099));
			if (port_check("", 1099) != 1099) {
				// 註冊通訊埠
				System.out.println("this.registry = LocateRegistry.createRegistry(1099);");
				this.registry = LocateRegistry.createRegistry(1099);

				// 註冊通訊路徑
				System.out.println("Naming.rebind(\"rmi://localhost:1099/RemoteService\", this.RemoteService);");
				Naming.rebind("rmi://localhost:1099/RemoteService", this.RemoteService);
			}
			//System.out.println(port_check("", 1099));
		} catch (RemoteException e) {
			System.out.println("Remote Service 創建遠端物件發生異常!\nRemoteException:" + e.getMessage());
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			System.out.println("Remote Service 發生重複綁定物件異常!\nAlreadyBoundException:" + e.getMessage());
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("Remote Service 發生URL畸形異常!\nMalformedURLException:" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Remote Service Exception!\nException:" + e.getMessage());
			e.printStackTrace();
		}

		if (this.registry != null) {
			try {
				System.out.println("RMI registry length: " + this.registry.list().length);
			} catch (AccessException e) {
				System.out.println("this.registry.list().length\nAccessException:" + e.getMessage());
				e.printStackTrace();
			} catch (RemoteException e) {
				System.out.println("this.registry.list().length\nRemoteException:" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("this.registry.list().length\nException:" + e.getMessage());
				e.printStackTrace();
			}

			try {
				System.out.print("Names bound to RMI registry: ");
				for (final String name : this.registry.list()) {
					System.out.println(name);
				}
			} catch (AccessException e) {
				System.out.println("this.registry.list()\nAccessException:" + e.getMessage());
				e.printStackTrace();
			} catch (RemoteException e) {
				System.out.println("this.registry.list()\nRemoteException:" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("this.registry.list()\nException:" + e.getMessage());
				e.printStackTrace();
			}
		}

		JTools.CLI("iptables -D INPUT -m state --state NEW -m tcp -p tcp --dport 1099 -j ACCEPT");
		JTools.CLI("iptables -D INPUT -m state --state NEW -m tcp -p tcp --dport 55555 -j ACCEPT");

		JTools.CLI("iptables -I INPUT -m state --state NEW -m tcp -p tcp --dport 1099 -j ACCEPT");
		JTools.CLI("iptables -I INPUT -m state --state NEW -m tcp -p tcp --dport 55555 -j ACCEPT");

		isRunning = false;
	}

	public void stop() {
		isRunning = true;

		System.out.println("Stop Remote Service!");

		//System.out.println(port_check("", 1099));
		if (port_check("", 1099) == 1099) {
			try {
				Naming.unbind("rmi://localhost:1099/RemoteService");
				System.out.println("Naming.unbind(\"rmi://localhost:1099/RemoteService\");");
			} catch (RemoteException e) {
				System.out.println("Naming.unbind(\"rmi://localhost:1099/RemoteService\");\nRemoteException:" + e.getMessage());
				e.printStackTrace();
			} catch (MalformedURLException e) {
				System.out.println("Naming.unbind(\"rmi://localhost:1099/RemoteService\");\nMalformedURLException:" + e.getMessage());
				e.printStackTrace();
			} catch (NotBoundException e) {
				System.out.println("Naming.unbind(\"rmi://localhost:1099/RemoteService\");\nNotBoundException:" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Naming.unbind(\"rmi://localhost:1099/RemoteService\");\nException:" + e.getMessage());
				e.printStackTrace();
			}

			try {
				UnicastRemoteObject.unexportObject(this.registry, true);
				System.out.println("UnicastRemoteObject.unexportObject(this.registry, true);");
			} catch (NoSuchObjectException e) {
				System.out.println("UnicastRemoteObject.unexportObject(this.registry, true);\nNoSuchObjectException:" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("UnicastRemoteObject.unexportObject(this.registry, true);\nException:" + e.getMessage());
				e.printStackTrace();
			}
		}
		//System.out.println(port_check("", 1099));

		//System.out.println(port_check("", 55555));
		if (port_check("", 55555) == 55555) {
			try {
				UnicastRemoteObject.unexportObject(this.RemoteService, true);
				System.out.println("UnicastRemoteObject.unexportObject(this.RemoteService, true);");
			} catch (NoSuchObjectException e) {
				System.out.println("UnicastRemoteObject.unexportObject(this.RemoteService, true);\nNoSuchObjectException:" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("UnicastRemoteObject.unexportObject(this.RemoteService, true);\nException:" + e.getMessage());
				e.printStackTrace();
			}
		}
		//System.out.println(port_check("", 55555));

		if (this.registry != null) {
			try {
				System.out.println("RMI registry length:" + this.registry.list().length);
			} catch (AccessException e) {
				System.out.println("this.registry.list().length\nAccessException:" + e.getMessage());
				e.printStackTrace();
			} catch (RemoteException e) {
				System.out.println("this.registry.list().length\nRemoteException:" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("this.registry.list().length\nException:" + e.getMessage());
				e.printStackTrace();
			}
		}

		JTools.CLI("iptables -D INPUT -m state --state NEW -m tcp -p tcp --dport 1099 -j ACCEPT");
		JTools.CLI("iptables -D INPUT -m state --state NEW -m tcp -p tcp --dport 55555 -j ACCEPT");

		isRunning = false;
	}

	public int port_check(String Address, int port) {
		Socket Skt = null;
		int server_port = 0;

		try {
			//System.out.println("Looking for " + port);
			Skt = new Socket(Address, port);

			try {
				Skt.close();
			} catch (IOException e) {
				System.out.println("Skt.close();\nIOException:" + e.getMessage());
				e.printStackTrace();
			}

			server_port = port;
		} catch (UnknownHostException e) {
			System.out.println("Skt = new Socket(Address, port);\nUnknownHostException:" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
		}

		if (server_port == port) {
			//System.out.println("There is a server on port " + server_port + " of " + Address);
		}

		return server_port;
	}

	public static RemoteInterface creatClient(String remote_address, int timeout_sec) {
		//System.out.println("Connect Remote Server: " + remote_address + ", timeout(sec): " + timeout_sec);

		int timeout = timeout_sec * 1000;

		RemoteInterface RemoteClientService = null;

		try {
			ClientSocketFactory csf = new ClientSocketFactory(timeout);
			csf.createSocket(remote_address, 1099);
			String name = "RemoteService";
			Registry registry = LocateRegistry.getRegistry(remote_address, 1099, csf);
			RemoteClientService = (RemoteInterface) registry.lookup(name);
		} catch (UnknownHostException e) {
			System.out.println("csf.createSocket(remote_address, 1099);\nUnknownHostException:" + e.getMessage());
			//e.printStackTrace();
		} catch (Exception e) {
			System.out.println("csf.createSocket(remote_address, 1099);\nException:" + e.getMessage());
			//e.printStackTrace();
		}

		return RemoteClientService;
	}
}
