package com.dihp.payment.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.bouncycastle.openpgp.PGPException;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.dihp.payment.security.PGPDIHPPaymentInvoiceSignSecurity;
import com.dihp.payment.verifier.AllTrustManager;
import com.dihp.payment.verifier.AllVerifier;

public class HTTPSDIHPPaymentInvoice {
	public static void main(String[] args) throws NoSuchProviderException,
			IOException, ParserConfigurationException, SAXException,
			TransformerFactoryConfigurationError, TransformerException,
			ParseException, PGPException, NoSuchAlgorithmException,
			java.text.ParseException {
		HTTPSDIHPPaymentInvoice paymentRESTFulWebServiceClient = new HTTPSDIHPPaymentInvoice();
		paymentRESTFulWebServiceClient.sendHTTPRequestToServer_With_JSON_Request();
	}

	public void sendHTTPRequestToServer_With_JSON_Request()
			throws java.text.ParseException {
		boolean signAndEncrypt = true;

		System.setProperty("java.net.useSystemProxies", "false");
		System.setProperty("https.protocols", "TLSv1.2");
		SSLContext sslContext = null;
		try {

			HttpsURLConnection.setDefaultHostnameVerifier(new AllVerifier());
			try {
				sslContext = SSLContext.getInstance("TLSv1.2");
			}

			catch (NoSuchAlgorithmException ex) {
				ex.printStackTrace();
			}
			sslContext.init(null, new TrustManager[] { new AllTrustManager() },
					null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		if (signAndEncrypt) {
			String string = "";
			try {
				PGPDIHPPaymentInvoiceSignSecurity.callPaymentServicesWithJSONFile_Sign_File();

				InputStream paymentInputStream = new FileInputStream(
						"C:/Users/kush.dhawan/Desktop/Datahemant/JSON__ENCRYPTED_RESPONSE.txt");
				
				//D:/PGP_RESPONSE_DATA/DIHP/JSON__ENCRYPTED_RESPONSE.txt
				
				 
				InputStreamReader paymentReader = new InputStreamReader(
						paymentInputStream);
				BufferedReader br = new BufferedReader(paymentReader);
				String line;

				while ((line = br.readLine()) != null) {
					string += line + "\n";
				}

				try {

					String https_url = "https://qah2h.axisbank.co.in/RESTAdapter/AxisBank/Dihp/Pay";
					URL url;
					url = new URL(https_url);
					Authenticator.setDefault(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication("corpuser",
									"axiscorpcon1!".toCharArray());
						}
					});
					HttpsURLConnection httpsConnection = (HttpsURLConnection) url
							.openConnection();
					httpsConnection.setDoOutput(true);
					httpsConnection.setRequestProperty("Content-Type",
							"application/json");
					httpsConnection.setConnectTimeout(10000);
					httpsConnection.setReadTimeout(100000);
					if (httpsConnection != null) {
						System.out.println(httpsConnection.toString());
					}

					OutputStreamWriter out = new OutputStreamWriter(
							httpsConnection.getOutputStream());
					out.write(string);
					out.close();

					BufferedReader in = null;
					try {
						in = new BufferedReader(new InputStreamReader(
								httpsConnection.getInputStream()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out
							.println("\nPayment REST Service Invoked Successfully..");
					in.close();
				} catch (Exception e) {
					System.out
							.println("\nError while calling Payment REST Service");
					e.printStackTrace();
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}