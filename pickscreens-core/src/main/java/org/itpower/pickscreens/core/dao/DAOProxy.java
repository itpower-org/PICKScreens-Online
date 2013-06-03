// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;


/**
 * easy access to daos.
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 19.03.2013
 */
public class DAOProxy {

  private ScreenDAO screendao = new DAOIBatisScreen();

  /** one and only instance of DAOProxy */
  private volatile static DAOProxy me;

  /** construct DAOProxy */
  private DAOProxy() {
  }

  /**
   * return the one and only instance of DAOProxy
   * 
   * @return the one and only instance of DAOProxy
   */
  public static DAOProxy getInstance() {
    if (me == null) {
      // ↖ no instance so far
      synchronized (DAOProxy.class) {
        if (me == null) {
          // ↖ still no instance so far
          // ↓ the one and only me
          me = new DAOProxy();
        }
      }
    }
    return me;
  }

  /**
   * short for {@link #getInstance()}
   * 
   * @return the one and only instance of DAOProxy
   */
  public static DAOProxy me() {
    return getInstance();
  }

  public static ScreenDAO screendao() {
    return me().screendao;
  }
}
