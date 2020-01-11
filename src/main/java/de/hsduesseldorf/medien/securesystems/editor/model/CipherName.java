package de.hsduesseldorf.medien.securesystems.editor.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum
public enum CipherName {
  AES,
  DES,
  ARC4;
}
