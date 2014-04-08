package ar.com.asfsa.testwifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class Configuracion extends Activity {

	Button guardarNombre;
	EditText nombre;
	CheckBox hacerVisisble;
	SharedPreferences sp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		sp = getSharedPreferences("TestWifi", Context.MODE_PRIVATE);
		guardarNombre = (Button) findViewById(R.id.guardar_nombre);
		nombre = (EditText) findViewById(R.id.nombre_dispositivo);
		hacerVisisble = (CheckBox) findViewById(R.id.chkVisible);
		guardarNombre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GuardarConfig();
			}
		});
		hacerVisisble.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("esVisible", isChecked);
				editor.commit();				
			}
			
		});
		nombre.setText(sp.getString("nombre-dispositivo", ""));
		hacerVisisble.setChecked(sp.getBoolean("esVisible", false));
	}
	
	private void GuardarConfig(){
		SharedPreferences.Editor editor = sp.edit();
		String nom = nombre.getText().toString();
		editor.putString("nombre-dispositivo", nom);
		editor.commit();
		Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	}
}
