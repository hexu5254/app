package com.app.platform.sm.user.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class DigestUtil {

	private DigestUtil() {
	}

	public static String md5HexLower(String plain) {
		return digestHex("MD5", plain);
	}

	public static String sha256HexLower(String plain) {
		return digestHex("SHA-256", plain);
	}

	private static String digestHex(String algorithm, String plain) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] raw = md.digest(plain.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(raw);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(algorithm, e);
		}
	}
}
