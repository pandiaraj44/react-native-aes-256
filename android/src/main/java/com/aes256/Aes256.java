package com.aes256;

import android.util.Base64;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;




public class Aes256 {

  private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding";
  private static final int PBKDF2_ITERATION_COUNT = 1001;
  private static final int PBKDF2_KEY_LENGTH = 256;
  private static final int SECURE_IV_LENGTH = 64;
  private static final int SECURE_KEY_LENGTH = 128;
  private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final String PBKDF2_SALT = "VA9aJs1wud3al1aD";
  private static final Random RANDOM = new SecureRandom();

  /**
   * <p>
   * To perform the AES256 encryption
   * </p>
   *
   * @param secureKey A 32 bytes string, which will used as input key for AES256 encryption
   * @param iv        A 16 bytes string, which will used as initial vector for AES256 encryption
   * @param toBeEncrypted     A string which will be encrypted
   * @return AES Encrypted string
   * @throws Exception
   */
  public static String encrypt(String secureKey, String iv, String toBeEncrypted ) throws Exception {
    byte[] pbkdf2SecuredKey = generatePBKDF2(secureKey.toCharArray(), PBKDF2_SALT.getBytes("UTF-8"),
      PBKDF2_ITERATION_COUNT, PBKDF2_KEY_LENGTH);

    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
    SecretKeySpec secretKeySpec = new SecretKeySpec(pbkdf2SecuredKey, "AES");

    Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

    byte[] encrypted = cipher.doFinal(toBeEncrypted.getBytes());

    return Base64.encodeToString(encrypted, Base64.DEFAULT);

  }

  /**
   * <p>
   * To perform the AES256 decryption
   * </p>
   *
   * @param secureKey A 32 bytes string, which will used as input key for AES256 decryption
   * @param iv     A 16 bytes string, which will used as initial vector for AES256 decryption
   * @param toBeDecrypted        An AES256 encrypted data which will be decrypted
   * @return AES Decrypted string
   * @throws Exception
   */
  public static String decrypt(String secureKey, String iv, String toBeDecrypted) throws Exception {
    byte[] pbkdf2SecuredKey = generatePBKDF2(secureKey.toCharArray(), PBKDF2_SALT.getBytes("UTF-8"),
      PBKDF2_ITERATION_COUNT, PBKDF2_KEY_LENGTH);

    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
    SecretKeySpec secretKeySpec = new SecretKeySpec(pbkdf2SecuredKey, "AES");

    Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

    byte[] original = cipher.doFinal(Base64.decode(toBeDecrypted, Base64.DEFAULT));

    return new String(original);
  }

  /**
   * @param password       The password
   * @param salt           The salt
   * @param iterationCount The iteration count
   * @param keyLength      The length of the derived key.
   * @return PBKDF2 secured key
   * @throws Exception
   * @see <a href="https://docs.oracle.com/javase/8/docs/api/javax/crypto/spec/PBEKeySpec.html">
   * https://docs.oracle.com/javase/8/docs/api/javax/crypto/spec/PBEKeySpec.html</a>
   */
  private static byte[] generatePBKDF2(char[] password, byte[] salt, int iterationCount,
                                       int keyLength) throws Exception {
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
    KeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
    SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
    return secretKey.getEncoded();
  }

  /**
   * <p>
   * This method used to generate the secure key based on the PBKDF2 algorithm
   * </p>
   *
   * @param password The password
   * @return SecureKey
   * @throws Exception
   */
  public static String generateSecureKey(String password) throws Exception {
    byte[] secureKeyInBytes = generatePBKDF2(password.toCharArray(), generateRandomSalt(),
      PBKDF2_ITERATION_COUNT, SECURE_KEY_LENGTH);
    return Hex.encodeHexString(secureKeyInBytes);
  }

  /**
   * <p>
   * This method used to generate the secure IV based on the PBKDF2 algorithm
   * </p>
   *
   * @param password The password
   * @return SecureIV
   * @throws Exception
   */
  public static String generateSecureIV(String password) throws Exception {
    byte[] secureIVInBytes = generatePBKDF2(password.toCharArray(), generateRandomSalt(),
      PBKDF2_ITERATION_COUNT, SECURE_IV_LENGTH);
    return Hex.encodeHexString(secureIVInBytes);
  }

  /**
   * <p>
   * This method used to generate the random salt
   * </p>
   *
   * @return
   */
  private static byte[] generateRandomSalt() {
    byte[] salt = new byte[16];
    RANDOM.nextBytes(salt);
    return salt;
  }
}
