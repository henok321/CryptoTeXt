package de.hsduesseldorf.medien.securesystems.editor.service.encryptor;

import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl.PBEDocumentEncryptor;
import org.junit.Assert;
import org.junit.Test;

public class DocumentEncryptorFactoryTest {

  static char[] PASSWORD = "test".toCharArray();

  @Test
  public void getInstance() throws Exception {
    DocumentEncryptor encryptor = DocumentEncryptorFactory.getInstance("AES", PASSWORD);
    Assert.assertTrue(encryptor instanceof PBEDocumentEncryptor);
    encryptor = DocumentEncryptorFactory.getInstance("DES", PASSWORD);
    Assert.assertTrue(encryptor instanceof PBEDocumentEncryptor);
    encryptor = DocumentEncryptorFactory.getInstance("ARC4", PASSWORD);
    Assert.assertTrue(encryptor instanceof PBEDocumentEncryptor);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getInstance_illegalArg() throws Exception {
    DocumentEncryptor encryptor = DocumentEncryptorFactory.getInstance("INVALID", PASSWORD);
  }
}
