package ar.com.asfsa.testwifi.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ar.com.asfsa.testwifi.Configuracion;
import ar.com.asfsa.testwifi.EnviarMensaje;
import ar.com.asfsa.testwifi.MainActivity;
import ar.com.asfsa.testwifi.R;
import ar.com.asfsa.testwifi.model.Dispositivo;

public class DispositivoAdapter extends ArrayAdapter<Dispositivo>{

	public Activity context;
	private ArrayList<Dispositivo> dispositivos;
	
	public DispositivoAdapter(Activity context, ArrayList<Dispositivo> dispositivos ) {
		super(context, R.layout.activity_main, dispositivos);
		this.context = context;
		this.dispositivos = dispositivos;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int po = position;
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(R.layout.itemlist_dispositivos, null);
		TextView nombre = (TextView) item.findViewById(R.id.dispositivo_nombre);
		nombre.setText(dispositivos.get(position).getNombre());
		
		TextView ip = (TextView) item.findViewById(R.id.dispositivo_ip);
		ip.setText(dispositivos.get(position).getIp().getHostAddress());
		// onSelectEvent
		item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(MainActivity.TAG, "Tap");
				Intent intent = new Intent(v.getContext(), EnviarMensaje.class);
				Dispositivo disp = dispositivos.get(po);
				intent.putExtra("nombre", disp.getNombre());
				intent.putExtra("ip", disp.getIp().getHostAddress());
			    v.getContext().startActivity(intent);
			}
		});
		return item;
	}
	
	
}
