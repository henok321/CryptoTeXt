package de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl;

import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import de.hsduesseldorf.medien.securesystems.editor.model.Options;
import de.hsduesseldorf.medien.securesystems.editor.model.properties.BlockMode;
import de.hsduesseldorf.medien.securesystems.editor.model.properties.CipherName;
import de.hsduesseldorf.medien.securesystems.editor.model.properties.Padding;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class ARC4DocumentEncryptorTest {
    private static Logger LOG = LoggerFactory.getLogger(ARC4DocumentEncryptor.class);

    private static byte[] TEST_MESSAGE = new byte[]{
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

    ARC4DocumentEncryptor cut;
    Document document;

    @Before
    public void setup() throws Exception {
        Options options = new Options(CipherName.ARC4, BlockMode.None, Padding.NoPadding, 64);
        this.cut = new ARC4DocumentEncryptor();
        document = new Document();
        document.setOptions(options);
        document.setEncrypted(false);
        document.setPayload(TEST_MESSAGE);
        document.setPayloadLength(TEST_MESSAGE.length);
    }

    @Test
    public void encrypt() throws Exception {
        Document actual = cut.encrypt(document);
        LOG.debug(Hex.toHexString(document.getPayload()));
        assertTrue(actual.getEncrypted());
        assertNotEquals(TEST_MESSAGE, actual.getPayload());
    }

    @Test
    public void decrypt() throws Exception {
        Document actual = cut.encrypt(document);
        actual = cut.decrypt(actual);
        assertArrayEquals(TEST_MESSAGE, actual.getPayload());
        assertFalse(actual.getEncrypted());
    }

}