package de.hsduesseldorf.medien.securesystems.editor.service.encryptor;

import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import java.security.GeneralSecurityException;

/** Encrypts and decrypts documents */
public interface DocumentEncryptor {

  /**
   * Encrypt a document
   *
   * @param document document object
   * @return encrypted document object
   * @throws GeneralSecurityException
   */
  public abstract Document encrypt(Document document) throws GeneralSecurityException;

  /**
   * Decrypt a document
   *
   * @param document encrypted document object
   * @return decrypted document object
   * @throws GeneralSecurityException
   */
  public abstract Document decrypt(Document document) throws GeneralSecurityException;
}
