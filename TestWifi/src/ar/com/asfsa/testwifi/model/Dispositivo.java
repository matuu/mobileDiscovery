package ar.com.asfsa.testwifi.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Dispositivo {

	InetAddress ip;
	String nombre;
	public InetAddress getIp() {
		return ip;
	}
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
		
	
	@Override
	public String toString() {
		return ip.getHostAddress()+";"+nombre;
	}
	
	
	public void processInput(String data){
		String[] line = data.split(";");
		try {
			ip = InetAddress.getByName(line[0]);
		} catch (UnknownHostException e) {
			ip = null;
		}
		nombre = line[1];
	}
	
	public boolean esIgual(Dispositivo otro){
		if(!this.nombre.equals(otro.getNombre())){
			return false;
		}
		if(!this.ip.getHostAddress().equals(otro.getIp().getHostAddress())){
			return false;
		}
		return true;
	}
	
}
