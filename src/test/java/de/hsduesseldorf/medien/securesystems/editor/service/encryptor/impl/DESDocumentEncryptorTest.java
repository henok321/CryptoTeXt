package de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import de.hsduesseldorf.medien.securesystems.editor.model.CipherName;
import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DESDocumentEncryptorTest {

  private static Logger LOG = LoggerFactory.getLogger(DESDocumentEncryptorTest.class);

  private static byte[] TEST_MESSAGE =
      new byte[] {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
      };

  DESDocumentEncryptor cut;

  @BeforeEach
  void setup() throws Exception {
    this.cut = new DESDocumentEncryptor();
  }

  @Test
  void encrypt() throws Exception {
    Document document = new Document();
    document.setCipherName(CipherName.DES);
    document.setEncrypted(false);
    document.setPayloadLength(TEST_MESSAGE.length);
    document.setPayload(TEST_MESSAGE);
    Document actual = cut.encrypt(document);
    LOG.debug(Hex.toHexString(document.getPayload()));
    assertNotEquals(TEST_MESSAGE, actual.getPayload());
  }

  @Test
  void decrypt() throws Exception {
    Document document = new Document();
    document.setCipherName(CipherName.DES);
    document.setEncrypted(false);
    document.setPayload(TEST_MESSAGE);
    document.setPayloadLength(TEST_MESSAGE.length);
    Document actual = cut.encrypt(document);
    actual = cut.decrypt(actual);
    assertArrayEquals(TEST_MESSAGE, actual.getPayload());
  }
}
