package com.digitalsolution.waterdispenser.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpRequest1 implements Runnable {
	public static InputStream in = null;
	public static OutputStream os = null;
	static SharedPreferences sharedPreferences;
	private static final int HTTP_CONNECTION_TIMEOUT = 1000 * 30; // 30 seconds
	private static final String TAG = HttpRequest1.class.getSimpleName();
	private static HttpURLConnection hc = null;
	private static InputStream input = null;
	private static OutputStream out = null;
    public static final String RESPONSE_ERR_SECURITY_EXC_MSG = "HTTP Error: GPRS Access Denied. Please restart application. Error Code : ";
    public static final String RESPONSE_ERR_IO_EXC_MSG = "Error: Please check GPRS availablity or the service is currently unavailable. Error Code : ";
    public static final String RESPONSE_ERR_ILLEGAL_ARG_EXC_MSG = "HTTP Error: Illegal Argument. Error Code : ";


    public void run() {
		System.out.println("inside run");

		try {

			byte[] b = new byte[1024];
			if (in.available() > 0)
				in.read(b);
		} catch (Exception e) {
			System.out.println("exp in run : " + e);
		}
	}

	public static String sendGetRequestJSON(Context context, String server_url, byte[] data) {

		Log.d(TAG, "sendGetRequestJSON: " + server_url);
		HttpURLConnection hc = null;
		OutputStream os = null;
		InputStream in = null;
		String response = "";
		try {
			URL url = new URL(server_url);
			if (server_url.startsWith("https")) {
			} else {
				hc = (HttpURLConnection) url.openConnection();
			}


			//URL url = new URL(server_url);
			//hc = (HttpURLConnection) url.openConnection();
			//hc.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
			// hc.setReadTimeout(HTTP_CONNECTION_TIMEOUT*3);
			//hc.setDoInput(true);
            /*hc.setDoOutput(true);
            hc.setUseCaches(false);*/
			hc.setRequestMethod("GET");
			//hc.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            /*if (data.length > 0) {
                hc.setRequestProperty("Content-Length", Integer.toString(data.length));
                hc.setFixedLengthStreamingMode(data.length);
            }*/

            /*JSONObject header = getJSONReqHeader();
            // Logger.addLog("header ::" + header.toString());
            try {*/
			//hc.setRequestProperty("Authorization", "Bearer " + header.getString("AuthKey"));
			//Log.d(TAG, "sendPostRequestJSON: " + header.getString("AuthKey"));
			//String credentials = "JanagananaAPI" + ":" + "J@n@ganana#2O!9";
			//String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
			//hc.setRequestProperty("Authorization", basic);
            /*} catch (JSONException e) {
                e.printStackTrace();
            }*/
			// temporary added to disable silend req


            /*os = hc.getOutputStream();
            os.write(data);*/

			in = hc.getInputStream();
			//  Logger.addLog("hc Response code: " + hc.getResponseCode());

			if (hc.getResponseCode() == 200) {
				StringBuffer s = new StringBuffer();
				int cr = 0;
				while ((cr = in.read()) != -1) {
					s.append((char) cr);
				}
				response = s.toString();
				//  Logger.logRequestResponse("Response: " + response);
			} else {
				response = "Error:" + hc.getResponseMessage();
			}
			//  Logger.logRequestResponse("JSON Response: " + response);

		} catch (SecurityException ex) // start exception
		{
			// Logger.addLog("Error: GPRS Access Denied. Please restart application");
			return RESPONSE_ERR_SECURITY_EXC_MSG;// "HTTP Error: \nGPRS Access Denied. Please restart application.";
		} catch (java.net.SocketTimeoutException e) {
			return RESPONSE_ERR_IO_EXC_MSG;
		} catch (IOException ex) {
			//   Logger.addLog(TAG+"IO exc" + ex.toString());
			//   Logger.addLog(TAG+"IO exc" + ex.toString());
			//Constants.ERROR_CODE = Constants.IOException;
			try {
				StringBuffer s = new StringBuffer();
				int cr = 0;
				input = hc.getErrorStream();
				// Logger.addLog("input :" + input);
				while ((cr = input.read()) != -1) {
					s.append((char) cr);
				}
				response = s.toString();
				//  Logger.addLog("response : " + response);
			} catch (Exception e) {
				//  Logger.addLog("ExceptionPrashant :" + e.toString());
				//Constants.ERROR_CODE = Constants.IOException;
				//  Logger.addLog(Constants.ERROR_CODE + " : " + ex.getMessage());
				//  Logger.addLog(Constants.ERROR_CODE + " : " + ex.toString());

				// Logger.addLog("Error: Connection Failed");
				return RESPONSE_ERR_IO_EXC_MSG;// "HTTP Error: \nSorry!!This service is currently unavailable."//Connection
				// Failed."
			}
		} catch (IllegalArgumentException ex1) {
			// Logger.addLog("Error: IllegalArgumentException");
			return RESPONSE_ERR_ILLEGAL_ARG_EXC_MSG;// "HTTP Error: \nIllegal Argument.";
		}

	/*	catch (Exception ex)
		{
			// Logger.addLog("105");
			// System.out.println("exp in web : "+ex);
			return Constants.RESPONSE_ERR_EXC_MSG;// "HTTP Error: "+ex+".";
		}*/ finally {
			try {

				if (os != null) {
					os.close();
				}
				if (in != null) {
					in.close();
				}
				if (hc != null) {
					hc.disconnect();
				}
			} catch (IOException ioe) {
			} finally {
			}
		}

		return response;

	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[]{};
			}

		}};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}