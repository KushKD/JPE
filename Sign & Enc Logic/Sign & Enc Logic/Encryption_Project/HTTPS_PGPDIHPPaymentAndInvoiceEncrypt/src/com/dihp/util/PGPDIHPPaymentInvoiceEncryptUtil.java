package com.dihp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;

import java.security.SecureRandom;
import java.security.Security;

import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;

import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;

import org.bouncycastle.openpgp.PGPPublicKey;

import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;

import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.PGPUtil;

public class PGPDIHPPaymentInvoiceEncryptUtil {
	private static final int KEY_FLAGS = 81;
	private static final int[] MASTER_KEY_CERTIFICATION_TYPES = new int[] {
			PGPSignature.POSITIVE_CERTIFICATION,
			PGPSignature.CASUAL_CERTIFICATION, PGPSignature.NO_CERTIFICATION,
			PGPSignature.DEFAULT_CERTIFICATION };

	@SuppressWarnings("rawtypes")
	private static boolean hasKeyFlags(PGPPublicKey encKey, int keyUsage) {
		if (encKey.isMasterKey()) {
			for (int i = 0; i != PGPDIHPPaymentInvoiceEncryptUtil.MASTER_KEY_CERTIFICATION_TYPES.length; i++) {
				for (Iterator eIt = encKey
						.getSignaturesOfType(PGPDIHPPaymentInvoiceEncryptUtil.MASTER_KEY_CERTIFICATION_TYPES[i]); eIt
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
			if (sv.hasSubpacket(PGPDIHPPaymentInvoiceEncryptUtil.KEY_FLAGS)) {
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

	// Encrypt_File
	@SuppressWarnings("deprecation")
	public static void encryptFile(OutputStream out, String fileName,
			PGPPublicKey encKey, boolean armor, boolean withIntegrityCheck)
			throws IOException, NoSuchProviderException, PGPException {
		Security.addProvider(new BouncyCastleProvider());

		if (armor) {
			out = new ArmoredOutputStream(out);
		}

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
				PGPCompressedData.ZIP);
		PGPUtil.writeFileToLiteralData(comData.open(bOut),
				PGPLiteralData.BINARY, new File(fileName));

		comData.close();

		PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
				PGPEncryptedData.CAST5,

				withIntegrityCheck, new SecureRandom(), "BC");

		cPk.addMethod(encKey);
		byte[] bytes = bOut.toByteArray();
		OutputStream cOut = cPk.open(out, bytes.length);
		cOut.write(bytes);
		cOut.close();
		out.close();
	}
}