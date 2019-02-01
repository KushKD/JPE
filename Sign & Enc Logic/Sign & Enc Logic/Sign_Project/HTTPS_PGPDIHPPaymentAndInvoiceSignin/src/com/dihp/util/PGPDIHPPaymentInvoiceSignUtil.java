package com.dihp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.security.Provider;

import java.security.Security;

import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;

import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;

import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;

import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.PGPUtil;

public class PGPDIHPPaymentInvoiceSignUtil {
	private static final int BUFFER_SIZE = 1 << 16;

	private static final int KEY_FLAGS = 81;
	private static final int[] MASTER_KEY_CERTIFICATION_TYPES = new int[] {
			PGPSignature.POSITIVE_CERTIFICATION,
			PGPSignature.CASUAL_CERTIFICATION, PGPSignature.NO_CERTIFICATION,
			PGPSignature.DEFAULT_CERTIFICATION };

	@SuppressWarnings("rawtypes")
	private static boolean hasKeyFlags(PGPPublicKey encKey, int keyUsage) {
		if (encKey.isMasterKey()) {
			for (int i = 0; i != PGPDIHPPaymentInvoiceSignUtil.MASTER_KEY_CERTIFICATION_TYPES.length; i++) {
				for (Iterator eIt = encKey
						.getSignaturesOfType(PGPDIHPPaymentInvoiceSignUtil.MASTER_KEY_CERTIFICATION_TYPES[i]); eIt
						.hasNext();) {
					PGPSignature sig = (PGPSignature) eIt.next();
					if (!isMatchingUsage(sig, keyUsage)) {
						return false;
					}
				}
			}
		} else {
			for (Iterator eIt = encKey
					.getSignaturesOfType(PGPSignature.SUBKEY_BINDING); eIt
					.hasNext();) {
				PGPSignature sig = (PGPSignature) eIt.next();
				if (!isMatchingUsage(sig, keyUsage)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isForEncryption(PGPPublicKey key) {
		if (key.getAlgorithm() == PublicKeyAlgorithmTags.RSA_SIGN
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.DSA
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.EC
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.ECDSA
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.ECDSA
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.DIFFIE_HELLMAN) {
			return false;
		}
		return hasKeyFlags(key, KeyFlags.ENCRYPT_COMMS
				| KeyFlags.ENCRYPT_STORAGE);
	}

	private static boolean isMatchingUsage(PGPSignature sig, int keyUsage) {
		if (sig.hasSubpackets()) {
			PGPSignatureSubpacketVector sv = sig.getHashedSubPackets();
			if (sv.hasSubpacket(PGPDIHPPaymentInvoiceSignUtil.KEY_FLAGS)) {
				if ((sv.getKeyFlags() & keyUsage) == 0) {
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static PGPPublicKey readPublicKey(InputStream in)
			throws IOException, PGPException {
		PGPPublicKeyRingCollection keyRingCollection = new PGPPublicKeyRingCollection(
				PGPUtil.getDecoderStream(in));
		PGPPublicKey publicKey = null;
		Iterator rIt = keyRingCollection.getKeyRings();

		while (publicKey == null && rIt.hasNext()) {
			PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
			Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
			while (publicKey == null && kIt.hasNext()) {
				PGPPublicKey key = kIt.next();
				if (key.isEncryptionKey()) {
					publicKey = key;
				}
			}
		}
		if (publicKey == null) {
			throw new IllegalArgumentException(
					"Can't find public key in the key ring.");
		}
		if (!isForEncryption(publicKey)) {
			throw new IllegalArgumentException("KeyID " + publicKey.getKeyID()
					+ " not flagged for encryption.");
		}
		return publicKey;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static PGPSecretKey readSecretKey(InputStream in)
			throws IOException, PGPException {
		PGPSecretKeyRingCollection keyRingCollection = new PGPSecretKeyRingCollection(
				PGPUtil.getDecoderStream(in));
		PGPSecretKey secretKey = null;

		Iterator rIt = keyRingCollection.getKeyRings();
		while (secretKey == null && rIt.hasNext()) {
			PGPSecretKeyRing keyRing = (PGPSecretKeyRing) rIt.next();
			Iterator<PGPSecretKey> kIt = keyRing.getSecretKeys();
			while (secretKey == null && kIt.hasNext()) {
				PGPSecretKey key = kIt.next();
				if (key.isSigningKey()) {
					secretKey = key;
				}
			}
		}
		if (secretKey == null) {
			throw new IllegalArgumentException(
					"Can't find private key in the key ring.");
		}
		if (!secretKey.isSigningKey()) {
			throw new IllegalArgumentException(
					"Private key does not allow signing.");
		}
		if (secretKey.getPublicKey().isRevoked()) {
			throw new IllegalArgumentException("Private key has been revoked.");
		}
		if (!hasKeyFlags(secretKey.getPublicKey(), KeyFlags.SIGN_DATA)) {
			throw new IllegalArgumentException(
					"Key cannot be used for signing.");
		}
		return secretKey;
	}

	// Sign_File
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static void signFile(OutputStream out, String fileName, PGPSecretKey secretKey, String password, boolean armor, boolean withIntegrityCheck) throws Exception {
		Provider provider = new BouncyCastleProvider();
		Security.addProvider(provider);

		if (armor) {
			out = new ArmoredOutputStream(out);
		}
		PGPPrivateKey privateKey = secretKey.extractPrivateKey(
				password.toCharArray(), provider);
		
		PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(
				secretKey.getPublicKey().getAlgorithm(),
				HashAlgorithmTags.SHA1, provider);
		signatureGenerator.initSign(PGPSignature.BINARY_DOCUMENT, privateKey);

		boolean firstTime = true;
		Iterator it = secretKey.getPublicKey().getUserIDs();
		while (it.hasNext() && firstTime) {
			PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
			spGen.setSignerUserID(false, (String) it.next());
			signatureGenerator.setHashedSubpackets(spGen.generate());
			firstTime = false;
		}
		signatureGenerator.generateOnePassVersion(false).encode(out);

		PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
		OutputStream literalOut = literalDataGenerator.open(out,
				PGPLiteralData.BINARY, fileName, new Date(),
				new byte[PGPDIHPPaymentInvoiceSignUtil.BUFFER_SIZE]);
		System.out.println(literalOut.toString());

		FileInputStream in = new FileInputStream(fileName);
		byte[] buf = new byte[PGPDIHPPaymentInvoiceSignUtil.BUFFER_SIZE];
		int len;

		while ((len = in.read(buf)) > 0) {
			literalOut.write(buf, 0, len);
			signatureGenerator.update(buf, 0, len);
		}

		in.close();
		literalDataGenerator.close();
		//System.out.println("jkhjkhjkhjk"+signatureGenerator);
		//System.out.println("tyutu"+signatureGenerator.generate());
		signatureGenerator.generate().encode(out);
		System.out.println(signatureGenerator.toString());
		out.close();
		out.close();

		if (armor) {
			out.close();
		}
	}

}