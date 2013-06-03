// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.aspects;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.codecs.MySQLCodec.Mode;

/**
 * some security aspects
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 06.05.2013
 */
public class PICKScreensSecurity {
  private Codec codec = new MySQLCodec(Mode.STANDARD);

  /** one and only instance of PICKScreensSecurity */
  private volatile static PICKScreensSecurity me;

  /** construct PICKScreensSecurity */
  private PICKScreensSecurity() {
  }

  /**
   * return the one and only instance of PICKScreensSecurity
   * 
   * @return the one and only instance of PICKScreensSecurity
   */
  public static PICKScreensSecurity getInstance() {
    if (me == null) {
      // ↖ no instance so far
      synchronized (PICKScreensSecurity.class) {
        if (me == null) {
          // ↖ still no instance so far
          // ↓ the one and only me
          me = new PICKScreensSecurity();
        }
      }
    }
    return me;
  }

  /**
   * short for {@link #getInstance()}
   * 
   * @return the one and only instance of PICKScreensSecurity
   */
  public static PICKScreensSecurity me() {
    return getInstance();
  }

  public String encodeForSQL(String userInput) {
    return ESAPI.encoder().encodeForSQL(this.codec, userInput);
  }
}
