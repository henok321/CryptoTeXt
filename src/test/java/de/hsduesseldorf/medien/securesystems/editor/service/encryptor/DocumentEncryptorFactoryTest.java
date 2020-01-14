package de.hsduesseldorf.medien.securesystems.editor.service.encryptor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl.PBEDocumentEncryptor;
import org.junit.jupiter.api.Test;

public class DocumentEncryptorFactoryTest {

  static char[] PASSWORD = "test".toCharArray();

  @Test
  public void getInstance() throws Exception {
    DocumentEncryptor encryptor = DocumentEncryptorFactory.getInstance("AES", PASSWORD);
    assertTrue(encryptor instanceof PBEDocumentEncryptor);
    encryptor = DocumentEncryptorFactory.getInstance("DES", PASSWORD);
    assertTrue(encryptor instanceof PBEDocumentEncryptor);
    encryptor = DocumentEncryptorFactory.getInstance("ARC4", PASSWORD);
    assertTrue(encryptor instanceof PBEDocumentEncryptor);
  }

  @Test
  public void getInstance_illegalArg() throws Exception {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          DocumentEncryptorFactory.getInstance("INVALID", PASSWORD);
        });
  }
}
