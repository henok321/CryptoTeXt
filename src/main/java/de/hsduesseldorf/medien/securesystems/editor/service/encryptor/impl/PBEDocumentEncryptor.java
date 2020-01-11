package de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl;

import de.hsduesseldorf.medien.securesystems.editor.exception.InvalidChecksum;
import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import de.hsduesseldorf.medien.securesystems.editor.service.SHA256Generator;
import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.DocumentEncryptor;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PBEDocumentEncryptor can encrypt and decrypt documents with a password. The cipher and pbe
 * method can be specified by constuctor arguments
 *
 * @author Hendrik Brinkmann
 */
public class PBEDocumentEncryptor implements DocumentEncryptor {

  /** iteration counter */
  static int iterCount = 65536;

  /** logger object to log to console */
  static Logger LOG = LoggerFactory.getLogger(PBEDocumentEncryptor.class);

  /** the password that will used to encrypt/ decrypt the data */
  char[] password;

  /** size of the generated encryption key */
  int keySize;

  /** name of the used cipher (e.g. AES/CBC/PKCS5Padding) */
  String cipherName;

  /**
   * name of the PBE method used to generate the key from the given password (e.g.
   * PBEWithSHA256And256BitAES-CBC-BC)
   */
  String pbeMethod;

  /** {@code true} if an IV is required */
  boolean ivRequired;

  /**
   * Create a new instance.
   *
   * @param password password to encrypt/ decrypt data
   * @param keySize the key size
   * @param cipherName name of the used cipher
   * @param pbeMethod name of the used pbe method
   * @param ivRequired {@code true} if no IV is required
   */
  public PBEDocumentEncryptor(
      char[] password, int keySize, String cipherName, String pbeMethod, boolean ivRequired) {
    this.password = password;
    this.keySize = keySize;
    this.cipherName = cipherName;
    this.pbeMethod = pbeMethod;
    this.ivRequired = ivRequired;
  }

  /**
   * generate a new secret key
   *
   * @param salt salt
   * @return secret key
   * @throws GeneralSecurityException
   */
  SecretKey generateSecret(byte[] salt) throws GeneralSecurityException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance(pbeMethod, "BC");
    KeySpec spec = new PBEKeySpec(this.password, salt, iterCount, keySize);
    SecretKey secret = factory.generateSecret(spec);
    return secret;
  }

  /**
   * Encrypt a document
   *
   * @param document document object
   * @return encrypted document object
   * @throws GeneralSecurityException
   */
  @Override
  public Document encrypt(Document document) throws GeneralSecurityException {
    // load BC from spi provider
    Security.addProvider(new BouncyCastleProvider());

    // generate a new salt
    byte[] salt = new byte[256];
    new SecureRandom().nextBytes(salt);

    // store salt to xml document
    document.setSalt(salt);

    // generate secret key with this salt
    SecretKey secret = generateSecret(salt);
    Cipher cipher = Cipher.getInstance(cipherName, "BC");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    AlgorithmParameters parameters = cipher.getParameters();

    // IV is only required for block cipher
    if (this.ivRequired) {
      // generate iv and store it to the xml document
      byte iv[] = parameters.getParameterSpec(IvParameterSpec.class).getIV();
      document.setIv(iv);
    }

    // generate a hash to avoid manipulation of the stored data
    document.setHash(SHA256Generator.generateHash(document.getPayload()));

    // do the encryption an put encrypted data into the document object
    byte[] ciphertext = cipher.doFinal(document.getPayload());
    document.setEncrypted(true);
    document.setPayload(ciphertext);
    return document;
  }

  /**
   * Decrypt a document
   *
   * @param document document object
   * @return decrypted document object
   * @throws GeneralSecurityException
   * @throws InvalidChecksum if the hash value is corrupted
   */
  @Override
  public Document decrypt(Document document) throws GeneralSecurityException {
    // load BC from spi provider
    Security.addProvider(new BouncyCastleProvider());
    // get salt from xml document
    SecretKey secret = generateSecret(document.getSalt());
    Cipher cipher = Cipher.getInstance(cipherName, "BC");

    // IV is only required for block cipher
    if (ivRequired)
      // get IV from the xml document
      cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(document.getIv()));
    else cipher.init(Cipher.DECRYPT_MODE, secret);

    // do decryption
    byte[] cleartext = cipher.doFinal(document.getPayload());
    document.setPayload(cleartext);
    document.setEncrypted(false);

    // check if the data was manipulated
    if (!Arrays.equals(SHA256Generator.generateHash(cleartext), document.getHash()))
      throw new InvalidChecksum();

    return document;
  }
}
