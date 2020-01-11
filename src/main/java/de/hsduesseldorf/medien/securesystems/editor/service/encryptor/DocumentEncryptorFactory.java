package de.hsduesseldorf.medien.securesystems.editor.service.encryptor;

import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.impl.PBEDocumentEncryptor;

/** factory provides several implementations of the interface {@link DocumentEncryptor} */
public class DocumentEncryptorFactory {

  /**
   * get an instance by the type
   *
   * @param type concrete type
   * @param password password to encrypt the data
   * @return an concrete instance of {@link DocumentEncryptor}
   * @throws IllegalArgumentException thrown if the type is not supported
   */
  public static DocumentEncryptor getInstance(String type, char[] password) {
    switch (type) {
      case "AES":
        return new PBEDocumentEncryptor(
            password, 256, "AES/CBC/PKCS5Padding", "PBEWithSHA256And256BitAES-CBC-BC", true);
      case "DES":
        return new PBEDocumentEncryptor(
            password, 64, "PBEWithSHAAnd3KeyTripleDES", "PBEWithSHAAnd3KeyTripleDES", false);
      case "ARC4":
        return new PBEDocumentEncryptor(password, 128, "ARC4", "PBEWithSHAAnd128BitRC4", false);
      default:
        throw new IllegalArgumentException("The cipher type <" + type + "> is not supported.");
    }
  }
}
