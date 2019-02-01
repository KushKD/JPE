package com.dihp.payment.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Iterator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;

import org.bouncycastle.openpgp.PGPUtil;

import com.dihp.util.PGPDIHPPaymentInvoiceEncryptUtil;

public class PGPDIHPPaymentInvoiceEncryptSecurity {
	private static PGPPublicKey readPublicKey(InputStream in)
			throws IOException, PGPException {
		in = PGPUtil.getDecoderStream(in);

		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

		@SuppressWarnings("rawtypes")
		Iterator rIt = pgpPub.getKeyRings();

		while (rIt.hasNext()) {
			PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
			@SuppressWarnings("rawtypes")
			Iterator kIt = kRing.getPublicKeys();

			while (kIt.hasNext()) {
				PGPPublicKey k = (PGPPublicKey) kIt.next();
				if (k.isEncryptionKey()) {
					return k;
				}
			}
		}
		throw new IllegalArgumentException(
				"Can't find encryption key in key ring.");
	}

	// Encrypt_File
	public static void callPaymentServicesWithJSONFile_Encrypt_File()
			throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		String fileName = "D:/PGP_RESPONSE_DATA/DIHP/JSON_USER_SENDING_REQUEST.txt";

		FileInputStream pubKey = new FileInputStream(
				"D:/PGP_RESPONSE_DATA/DIHP/public_key.txt");

		PGPDIHPPaymentInvoiceEncryptUtil.encryptFile(new FileOutputStream(
				"D:/PGP_RESPONSE_DATA/DIHP/JSON__ENCRYPTED_RESPONSE.txt"),
				fileName, readPublicKey(pubKey), true, true);
	}

}