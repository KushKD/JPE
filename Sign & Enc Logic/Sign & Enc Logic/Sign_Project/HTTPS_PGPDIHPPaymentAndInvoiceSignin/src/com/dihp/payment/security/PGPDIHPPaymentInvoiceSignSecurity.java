package com.dihp.payment.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.openpgp.PGPSecretKey;

import com.dihp.util.PGPDIHPPaymentInvoiceSignUtil;

public class PGPDIHPPaymentInvoiceSignSecurity {

	public static void callPaymentServicesWithJSONFile_Sign_File()
			throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		String fileName = "C:/Users/kush.dhawan/Desktop/Encryption_Code/Data/PaymentReq_JSON.txt";

		FileInputStream secretKeyIn = new FileInputStream(
				"C:/Users/kush.dhawan/Desktop/Encryption_Code/Data/privateKey.txt");

		PGPSecretKey secretKey = PGPDIHPPaymentInvoiceSignUtil
				.readSecretKey(secretKeyIn);

		PGPDIHPPaymentInvoiceSignUtil.signFile(new FileOutputStream("C:/Users/kush.dhawan/Desktop/Encryption_Code/Data/JSON__ENCRYPTED_RESPONSE_x.txt"), fileName, secretKey, "", true, true);
	}

}