package de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl;

import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.DocumentEncryptor;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DESDocumentEncryptor implements DocumentEncryptor {

  private static final Logger LOG = LoggerFactory.getLogger(DESDocumentEncryptor.class);

  byte[] keyBytes = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

  @Override
  public Document encrypt(Document document) throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    String cipherType = "DES/ECB/PKCS7PADDING";
    SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");

    byte[] input = Arrays.copyOf(document.getPayload(), document.getPayload().length);

    Cipher cipher = Cipher.getInstance(cipherType, "BC");

    LOG.debug("input : " + Hex.toHexString(input));

    // encryption pass
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
    int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
    ctLength += cipher.doFinal(cipherText, ctLength);

    LOG.debug("cipher: " + Hex.toHexString(cipherText) + " bytes: " + ctLength);

    document.setEncrypted(true);
    document.setPayload(cipherText);
    return document;
  }

  @Override
  public Document decrypt(Document document) throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    String cipherType = "DES/ECB/PKCS7PADDING";
    SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");

    byte[] cipherText = Arrays.copyOf(document.getPayload(), document.getPayload().length);

    int ctLength = cipherText.length;

    Cipher cipher = Cipher.getInstance(cipherType, "BC");

    cipher.init(Cipher.DECRYPT_MODE, key);

    byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

    int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
    LOG.debug("cipher " + Hex.toHexString(cipherText));
    ptLength += cipher.doFinal(plainText, ptLength);
    LOG.debug("decypted " + Hex.toHexString(plainText));
    plainText = Arrays.copyOf(plainText, document.getPayloadLength());
    LOG.debug("remove padding " + Hex.toHexString(plainText));
    document.setPayload(plainText);
    document.setEncrypted(false);
    return document;
  }
}
