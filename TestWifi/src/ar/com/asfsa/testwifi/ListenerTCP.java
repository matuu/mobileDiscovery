package ar.com.asfsa.testwifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import ar.com.asfsa.testwifi.model.Dispositivo;

public class ListenerTCP extends Service {
	private static final String TAG = "testWifi";
	private ServerSocket serverSocket;
	Thread serverThread = null;
    private static final int PORT = 2526;
    private static Handler mHandler;
    WifiManager mWifi;
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void onCreate() {
		mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		Log.d(TAG, "TCP service was Created");
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	// For time consuming an long tasks you can launch a new thread here...
    	Log.d(TAG, "TCP Service Started");
    	this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
    }
    

    @Override
    public void onDestroy() {
    	Log.d(TAG, "TCP Service Destroyed");

    }
    
    public static void setHandler(Handler handler){
    	mHandler = handler;
    }
    
    class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {

				try {

					socket = serverSocket.accept();
					
					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
    class CommunicationThread implements Runnable {

		private Socket clientSocket;
		private BufferedReader input;

		public CommunicationThread(Socket clientSocket) {

			this.clientSocket = clientSocket;
			try {

				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			if (!Thread.currentThread().isInterrupted()) {
				try {
					String read = input.readLine();
					Dispositivo disp = new Dispositivo();
					disp.processInput(read.substring(2));
					
					if(!disp.esIgual(MainActivity.me)){
						mHandler.obtainMessage(MainActivity.MOVIL_ENCONTRADO, disp.toString().length(), -1, disp.toString()) .sendToTarget();
						Log.d(TAG, "Recibí data TCP: " + read);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}

	}

}
