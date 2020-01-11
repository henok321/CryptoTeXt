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

@Deprecated
public class AESDocumentEncryptorWithPBE implements DocumentEncryptor {

  static final int IT_COUNT = 65536;
  static final int KEY_SIZE = 256;
  private static Logger LOG = LoggerFactory.getLogger(AESDocumentEncryptorWithPBE.class);
  char[] password;

  public AESDocumentEncryptorWithPBE(char[] password) throws Exception {
    this.password = password;
  }

  SecretKey generateSecret(byte[] salt) throws GeneralSecurityException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
    KeySpec spec = new PBEKeySpec(this.password, salt, IT_COUNT, KEY_SIZE);
    SecretKey secret = factory.generateSecret(spec);
    return secret;
  }

  @Override
  public Document encrypt(Document document) throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    byte[] salt = new byte[256];
    new SecureRandom().nextBytes(salt);
    document.setSalt(salt);
    SecretKey secret = generateSecret(salt);
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    AlgorithmParameters parameters = cipher.getParameters();
    byte iv[] = parameters.getParameterSpec(IvParameterSpec.class).getIV();
    document.setIv(iv);
    document.setHash(SHA256Generator.generateHash(document.getPayload()));
    byte[] ciphertext = cipher.doFinal(document.getPayload());
    document.setEncrypted(true);
    document.setPayload(ciphertext);
    return document;
  }

  @Override
  public Document decrypt(Document document) throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    SecretKey secret = generateSecret(document.getSalt());
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(document.getIv()));
    byte[] cleartext = cipher.doFinal(document.getPayload());
    document.setPayload(cleartext);
    document.setEncrypted(false);

    if (!Arrays.equals(SHA256Generator.generateHash(cleartext), document.getHash()))
      throw new InvalidChecksum();

    return document;
  }
}
