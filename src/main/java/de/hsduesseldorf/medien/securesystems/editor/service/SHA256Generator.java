package de.hsduesseldorf.medien.securesystems.editor.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SHA256Generator {

  /**
   * Generate SHA-256 hashes to void manipulations of the stored data
   *
   * @param message stored data
   * @return hash of the stored data
   * @throws GeneralSecurityException
   */
  public static byte[] generateHash(byte[] message) throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
    return md.digest(message);
  }
}
