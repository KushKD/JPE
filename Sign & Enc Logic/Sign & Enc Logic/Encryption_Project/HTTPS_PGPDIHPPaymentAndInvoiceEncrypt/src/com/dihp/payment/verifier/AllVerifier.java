package com.dihp.payment.verifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class AllVerifier implements HostnameVerifier {

	public boolean verify(String hostname, SSLSession session) {
		return true;
	}

}
