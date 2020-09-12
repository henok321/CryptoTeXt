package de.hsduesseldorf.medien.securesystems.editor.service.encryptor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl.PBEDocumentEncryptor;
import org.junit.jupiter.api.Test;

class DocumentEncryptorFactoryTest {

  private static final char[] PASSWORD = "test".toCharArray();

  @Test
  void getInstance() throws Exception {
    DocumentEncryptor encryptor = DocumentEncryptorFactory.getInstance("AES", PASSWORD);
    assertTrue(encryptor instanceof PBEDocumentEncryptor);
    encryptor = DocumentEncryptorFactory.getInstance("DES", PASSWORD);
    assertTrue(encryptor instanceof PBEDocumentEncryptor);
    encryptor = DocumentEncryptorFactory.getInstance("ARC4", PASSWORD);
    assertTrue(encryptor instanceof PBEDocumentEncryptor);
  }

  @Test
  void getInstance_illegalArg() throws Exception {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          DocumentEncryptorFactory.getInstance("INVALID", PASSWORD);
        });
  }
}
