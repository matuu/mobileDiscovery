package ar.com.asfsa.testwifi;

import java.io.IOException;
import java.util.ArrayList;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import ar.com.asfsa.testwifi.adapter.DispositivoAdapter;
import ar.com.asfsa.testwifi.model.Dispositivo;

public class MainActivity extends Activity {

	
	public static final String TAG = "testWifi";
    WifiManager mWifi;
    private static  ArrayList<Dispositivo> dispositivos = new ArrayList<Dispositivo>();
    DispositivoAdapter dispoAdapter;
    SharedPreferences sp;
	private Button send_bc;
	private TextView ip, status;
	private ProgressDialog pDialog;
	private ListView list;
	EnviarBroadcastTask enviarTask;
	Utils utils = new Utils();
	
	
	// Constantes
	public static final int MOVIL_ENCONTRADO = 1;
    public static final int BUSQUEDA_SIN_EXITO = 2;
    public static final int WIFI_NO_CONECTADO = 3;
    public static final int ERROR = 4;
	
    public static Dispositivo me = new Dispositivo();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		send_bc = (Button) findViewById(R.id.send_bc);		
		ip = (TextView) findViewById(R.id.my_ip);
		status = (TextView) findViewById(R.id.status);
		list = (ListView) findViewById(R.id.listResultado);
		try {
			ip.setText("IP: " + utils.getIPAddress(mWifi).getHostAddress());
		} catch (IOException e) {
			ip.setText("Falló la obtención de la IP");
		}
		send_bc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				enviarBC();
			}
		});
		dispoAdapter = new DispositivoAdapter(this, dispositivos);
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	@Override
	public void onStart() {

		super.onStart();
		Log.d(TAG, "++ ON START ++");
		if (!mWifi.isWifiEnabled()) {
			status.setText("WiFi no está habilitado.");
			send_bc.setEnabled(false);
		}else{
			sp = getSharedPreferences("TestWifi", Context.MODE_PRIVATE);
			me.setNombre(sp.getString("nombre-dispositivo", ""));
			if(me.getNombre() == ""){
				send_bc.setEnabled(false);
				status.setText("Configure su nombre");
			}else{
				send_bc.setEnabled(true);
				status.setText("");
				try {
					me.setIp(utils.getIPAddress(mWifi));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sp.getBoolean("esVisible", false) ){
				startUDPService();
				startTCPService();
			}else{
				stopUDPService();
				stopTCPService();
			}

		}
	}
	
	public synchronized void onResume(){
		super.onResume();
		Log.d(TAG, "+++ ON RESUME +++");
	}
	public void onPause(){
		super.onPause();
		Log.d(TAG, "+++ ON PAUSE +++");
	}
	
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "+++ ON DESTROY +++");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_view_broadcast:
	        	MostrarConfiguracion();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// Start the  service
	public void startTCPService() {	 
		startService(new Intent(this, ListenerTCP.class));
		ListenerTCP.setHandler(mHandler);
	}
	 
	// Stop the  service
    public void stopTCPService() {
		 
		stopService(new Intent(this, ListenerTCP.class));
    }
    
 // Start the  service
 	public void startUDPService() {	 
 		startService(new Intent(this, ServiceUDP.class));
 	}
 	 
 	// Stop the  service
     public void stopUDPService() {
 		 
 		stopService(new Intent(this, ServiceUDP.class));
     }
	
	private void MostrarConfiguracion(){
		Intent intent = new Intent(this, Configuracion.class);
	    startActivity(intent);
	}
	

	  private void enviarBC(){
		  dispositivos.clear();
		  pDialog = new ProgressDialog(this);
          pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
          pDialog.setMessage("Buscando...");
          pDialog.setCancelable(true);
          pDialog.setMax(100);
          enviarTask = new EnviarBroadcastTask();
          enviarTask.execute();
		 
	  }
	  
	  	//Tarea en segundo plano
		private class EnviarBroadcastTask extends AsyncTask<Void, Integer, Boolean> {		 
	        @Override
	        protected Boolean doInBackground(Void... params) {
	        	
    			ServiceUDP service = ServiceUDP.getInstance();
    			boolean r = service.SendBroadcast();
    			
    			return r;
	        }	
	 
	        @Override
	        protected void onPreExecute() {
	 
	            pDialog.setOnCancelListener(new OnCancelListener() {
	            @Override
	            public void onCancel(DialogInterface dialog) {
	            }
	            
	        });
	            pDialog.setProgress(0);
	            pDialog.show();
	        }
	 
	        @Override
	        protected void onPostExecute(Boolean result) {
	            if(result)
	            {
	            }
	            pDialog.cancel();
	        }
	    }
		
		// The Handler that gets information back from the BluetoothChatService
	    private final Handler mHandler = new Handler() {
	    	
	        @Override
	        public void handleMessage(Message msg) {
	        	byte[] readBuf;
	        	String mensaje;
	        	try{
	        		readBuf = (byte[]) msg.obj;
	        		mensaje = new String(readBuf, 0, msg.arg1);
	        	}catch (Exception e) {
	        		try{
	        			readBuf = new byte[0];
	        		mensaje = (String) msg.obj;
	        		mensaje = mensaje.substring(0, msg.arg1);
	        		}catch(Exception ex){
	        			mensaje = "No se pudo obtener el mensaje resultado";
	        			readBuf = new byte[0];
	        		}
				}
	        	switch (msg.what) {
	            case MOVIL_ENCONTRADO:
	            	Dispositivo disp = new Dispositivo();
	            	disp.processInput(mensaje);
	            	AddDispositivo(disp);
		            break;
	            case BUSQUEDA_SIN_EXITO:
	            	break;
	            case WIFI_NO_CONECTADO:
	            	break;
	            case ERROR:           	
	            	break;
	            	
	        	}
	        }

			
	    };

	    private void AddDispositivo(Dispositivo disp){
	    	boolean existe = false;
	    	for(Dispositivo d: dispositivos){
	    		if(d.esIgual(disp)){
	    			existe = true;
	    		}
	    	}
	    	if(!existe)
	    		dispositivos.add(disp);
        	list.setAdapter(dispoAdapter);
	    }

}
