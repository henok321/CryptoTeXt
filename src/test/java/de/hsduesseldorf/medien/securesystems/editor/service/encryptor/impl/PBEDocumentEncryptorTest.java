package de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hsduesseldorf.medien.securesystems.editor.exception.InvalidChecksum;
import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.DocumentEncryptor;
import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.DocumentEncryptorFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PBEDocumentEncryptorTest {

  private static final char[] PASSWORD = "test".toCharArray();
  private static final Logger LOG = LoggerFactory.getLogger(PBEDocumentEncryptorTest.class);
  private static final byte[] TEST_MESSAGE =
      new byte[] {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
      };

  private List<DocumentEncryptor> cut;

  @BeforeEach
  public void setup() throws Exception {
    // generate instances of all supported cipher and pbe combinations
    cut = new ArrayList<>();
    cut.add(DocumentEncryptorFactory.getInstance("AES", PASSWORD));
    cut.add(DocumentEncryptorFactory.getInstance("DES", PASSWORD));
    cut.add(DocumentEncryptorFactory.getInstance("ARC4", PASSWORD));
  }

  @Test
  void encrypt() throws Exception {
    for (DocumentEncryptor e : cut) {
      Document document = new Document();
      document.setPayload(TEST_MESSAGE);
      document.setPayloadLength(TEST_MESSAGE.length);
      document.setEncrypted(false);
      document = e.encrypt(document);
      LOG.debug("cleartext lenght:\t" + TEST_MESSAGE.length);
      LOG.debug("ciphertext length:\t" + document.getPayload().length);
      assertNotEquals(new String(TEST_MESSAGE), new String(document.getPayload()));
    }
  }

  @Test
  void decrypt() throws Exception {
    for (DocumentEncryptor e : cut) {
      Document document = new Document();
      document.setPayload(TEST_MESSAGE);
      document.setPayloadLength(TEST_MESSAGE.length);
      document.setEncrypted(false);
      document = e.encrypt(document);
      document = e.decrypt(document);
      LOG.debug("cleartext lenght:\t" + TEST_MESSAGE.length);
      LOG.debug("ciphertext length:\t" + document.getPayload().length);
      assertEquals(new String(TEST_MESSAGE), new String(document.getPayload()));
    }
  }

  @Test
  void decrypt_invalid_checkup() throws Exception {
    for (DocumentEncryptor e : cut) {
      final Document raw = new Document();
      raw.setPayload(TEST_MESSAGE);
      raw.setPayloadLength(TEST_MESSAGE.length);
      raw.setEncrypted(false);
      final Document encrypt = e.encrypt(raw);
      encrypt.getPayload()[0] = 0x01;

      assertThrows(
          InvalidChecksum.class,
          () -> {
            e.decrypt(encrypt);
          });
    }
  }
}
