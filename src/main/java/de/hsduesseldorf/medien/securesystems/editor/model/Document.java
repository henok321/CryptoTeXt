package de.hsduesseldorf.medien.securesystems.editor.model;

import java.io.File;
import java.util.Date;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Document {

  private Date lastModified;

  @XmlElement private CipherName cipherName;

  private Integer payloadLength;

  private byte[] salt;

  private byte[] iv;

  private byte[] hash;

  private byte[] payload;

  private Boolean isEncrypted;

  @XmlTransient private File file;

  public Document(
      Date lastModified,
      CipherName cipherName,
      Integer payloadLength,
      byte[] salt,
      byte[] iv,
      byte[] payload,
      File file) {
    this.lastModified = lastModified;
    this.cipherName = cipherName;
    this.payloadLength = payloadLength;
    this.salt = salt;
    this.iv = iv;
    this.payload = payload;
    this.file = file;
  }

  public Document(
      Date lastModified,
      CipherName cipherName,
      Integer payloadLength,
      byte[] salt,
      byte[] payload,
      File file) {
    this(lastModified, cipherName, payloadLength, salt, null, payload, file);
  }

  public Document(
      Date lastModified, CipherName cipherName, Integer payloadLength, byte[] payload, File file) {
    this(lastModified, cipherName, payloadLength, null, null, payload, file);
  }

  public Document() {
    // default
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Integer getPayloadLength() {
    return payloadLength;
  }

  public void setPayloadLength(Integer payloadLength) {
    this.payloadLength = payloadLength;
  }

  public byte[] getSalt() {
    return salt;
  }

  public void setSalt(byte[] salt) {
    this.salt = salt;
  }

  public byte[] getIv() {
    return iv;
  }

  public void setIv(byte[] iv) {
    this.iv = iv;
  }

  public byte[] getHash() {
    return hash;
  }

  public void setHash(byte[] hash) {
    this.hash = hash;
  }

  public byte[] getPayload() {
    return payload;
  }

  public void setPayload(byte[] payload) {
    this.payload = payload;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public CipherName getCipherName() {
    return this.cipherName;
  }

  public void setCipherName(CipherName cipherName) {
    this.cipherName = cipherName;
  }

  public Boolean getEncrypted() {
    return isEncrypted;
  }

  public void setEncrypted(Boolean encrypted) {
    isEncrypted = encrypted;
  }
}
