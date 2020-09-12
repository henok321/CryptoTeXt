package de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hsduesseldorf.medien.securesystems.editor.exception.InvalidChecksum;
import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ARC4DocumentEncryptorWithPBETest {

  static char[] PASSWORD = "test".toCharArray();
  private static Logger LOG = LoggerFactory.getLogger(AESDocumentEncryptorWithPBETest.class);
  private static byte[] TEST_MESSAGE =
      new byte[] {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
      };

  ARC4DocumentEncryptorWithPBE cut;

  @BeforeEach
  void setup() throws Exception {
    cut = new ARC4DocumentEncryptorWithPBE(PASSWORD);
  }

  @Test
  void encrypt() throws Exception {
    Document document = new Document();
    document.setPayload(TEST_MESSAGE);
    document.setPayloadLength(TEST_MESSAGE.length);
    document.setEncrypted(false);
    document = cut.encrypt(document);
    LOG.debug("cleartext lenght:\t" + TEST_MESSAGE.length);
    LOG.debug("ciphertext length:\t" + document.getPayload().length);
    assertNotEquals(new String(TEST_MESSAGE), new String(document.getPayload()));
  }

  @Test
  void decrypt() throws Exception {
    Document document = new Document();
    document.setPayload(TEST_MESSAGE);
    document.setPayloadLength(TEST_MESSAGE.length);
    document.setEncrypted(false);
    document = cut.encrypt(document);
    document = cut.decrypt(document);
    LOG.debug("cleartext lenght:\t" + TEST_MESSAGE.length);
    LOG.debug("ciphertext length:\t" + document.getPayload().length);
    assertEquals(new String(TEST_MESSAGE), new String(document.getPayload()));
  }

  @Test
  void decrypt_invalid_checkup() throws Exception {
    final Document raw = new Document();
    raw.setPayload(TEST_MESSAGE);
    raw.setPayloadLength(TEST_MESSAGE.length);
    raw.setEncrypted(false);
    final Document encrypt = cut.encrypt(raw);
    encrypt.getPayload()[0] = 0x01;
    assertThrows(
        InvalidChecksum.class,
        () -> {
          cut.decrypt(encrypt);
        });
  }
}
