package de.hsduesseldorf.medien.securesystems.editor.exception;

public class InvalidChecksum extends RuntimeException {

  public InvalidChecksum() {
    super("checksum mismatch - maybe the data was manipulated");
  }
}
