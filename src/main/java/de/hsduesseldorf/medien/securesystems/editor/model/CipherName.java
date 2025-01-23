package de.hsduesseldorf.medien.securesystems.editor.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum
public enum CipherName {
  AES,
  DES,
  ARC4;
}
