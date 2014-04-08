package ar.com.asfsa.testwifi;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;


public class ServiceUDP extends Service {

	private static final String TAG = "testWifi";
    private static final int DISCOVERY_PORT = 2525;
    private static final int PORT = 2526;
    WifiManager mWifi;
    private DatagramSocket socketUDP;
	Thread serverThread = null;
	static ServiceUDP sInstance;
	Utils utils = new Utils();
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static ServiceUDP getInstance(){
		return sInstance;
	}

	@Override
    public void onCreate() {
		Log.d(TAG, "UDP Service was Created");
		mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		sInstance = this;
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	// For time consuming an long tasks you can launch a new thread here...
    	Log.d(TAG, "UDP Service Started");
    	this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
    	Log.d(TAG, "UDP Service Started");
    	this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
		return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	while(this.socketUDP.isConnected()){
    		this.socketUDP.disconnect();
    	}
    	this.socketUDP.close();
    	Log.d(TAG, "UDP Service Destroyed");
    }
    
    public boolean SendBroadcast(){
    	try{
    		
    		sendDiscoveryRequest(this.socketUDP);
    		Thread.sleep(2000);
    		sendDiscoveryRequest(this.socketUDP);
    	}
    	catch (InterruptedException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}catch (Exception e) {
			return false;
		}
    	return true;
    }
    
	  private void sendDiscoveryRequest(DatagramSocket socket) throws IOException {
	    String ip = utils.getIPAddress(mWifi).getHostAddress();
		String data = String.format("F0%s", ip);
	    Log.d(TAG, "Sending data UDP " + data);

	    DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
	        utils.getBroadcastAddress(mWifi), DISCOVERY_PORT);
	    socket.send(packet);
	  }
    
    class ServerThread implements Runnable {

		public void run() {
			DatagramPacket packet = null;
			
			try {
				socketUDP = new DatagramSocket(DISCOVERY_PORT);
				socketUDP.setBroadcast(true);
		  	    //socketUDP.setSoTimeout(5000);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {
				byte[] buf = new byte[1024];
				try {
					
			  	    packet = new DatagramPacket(buf, buf.length);
			        socketUDP.receive(packet);
			        if(packet.getAddress().equals(MainActivity.me.getIp())){
			        	Log.d(TAG, "Recibí un paquete UDP de " + packet.getAddress().getHostAddress()+ "(ignorar)");
			        }else{
			        	String s = new String(packet.getData(), 0, packet.getLength());
						CommunicationThread commThread = new CommunicationThread(s);
						new Thread(commThread).start();
			        }
			        

				} catch (IOException e) {
					Log.e(TAG, e.getCause().getMessage().toString());
				}
			}
		}
	}
    
    class CommunicationThread implements Runnable {

		private Socket clientSocket;
		private String ipServer = "";
	    private Utils utils = new Utils();

		public CommunicationThread(String msg) {
			this.ipServer = msg.substring(2);
			
		}

		public void run() {

			if (!Thread.currentThread().isInterrupted()) {
				Log.d(TAG, "Recibí mensaje UDP de: " + this.ipServer);
				try {
					InetAddress ip = utils.getIPAddress(mWifi, this.ipServer);
					clientSocket = new Socket(ip, PORT);

					PrintWriter out = new PrintWriter(new BufferedWriter(
							 new OutputStreamWriter(this.clientSocket.getOutputStream())),true);
					String msg = "F1"+MainActivity.me.toString();
					out.println(msg);
					Log.d(TAG, "Envié mensaje TCP a " + this.ipServer +": " + msg);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
