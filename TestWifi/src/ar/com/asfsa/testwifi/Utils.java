package ar.com.asfsa.testwifi;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Utils {
	private static final String TAG = "testWifi";
    private static final String REMOTE_KEY = "s91id#%ihdhv#$%!";
	
	public InetAddress getBroadcastAddress( WifiManager mWifi) throws IOException {
		
	    DhcpInfo dhcp = mWifi.getDhcpInfo();
	    if (dhcp == null) {
	    	Log.d(TAG, "Could not get dhcp info");
	      return null;
	    }

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	  }
	
	public InetAddress getIPAddress( WifiManager mWifi) throws IOException {
		
	    DhcpInfo dhcp = mWifi.getDhcpInfo();
	    if (dhcp == null) {
	    	Log.d(TAG, "Could not get dhcp info");
	      return null;
	    }
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((dhcp.ipAddress >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	  }
	
	public InetAddress getIPAddress( WifiManager mWifi, String ip) throws IOException {
		
//		String[] ipArray = ip.split(".");
//	    byte[] quads = new byte[4];
//	    for (int k = 0; k < 4; k++){
//	    	quads[k] = (byte) (Integer.parseInt(ipArray[k]));
//	    } 
//	    return InetAddress.getByAddress(quads);
	    return InetAddress.getByName(ip);
	  }
	
	/**
	   * Calculate the signature we need to send with the request. It is a string
	   * containing the hex md5sum of the challenge and REMOTE_KEY.
	   *
	   * @return signature string
	   */
	  public String getSignature(String challenge) {
	    MessageDigest digest;
	    byte[] md5sum = null;
	    try {
	      digest = java.security.MessageDigest.getInstance("MD5");
	      digest.update(challenge.getBytes());
	      digest.update(REMOTE_KEY.getBytes());
	      md5sum = digest.digest();
	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }

	    StringBuffer hexString = new StringBuffer();
	    for (int k = 0; k < md5sum.length; ++k) {
	      String s = Integer.toHexString((int) md5sum[k] & 0xFF);
	      if (s.length() == 1)
	        hexString.append('0');
	      hexString.append(s);
	    }
	    return hexString.toString();
	  }
}
