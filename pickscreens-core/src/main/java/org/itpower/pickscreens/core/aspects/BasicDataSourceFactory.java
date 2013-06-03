// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.aspects;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * provide a {@link BasicDataSource} and setting the values as configured in
 * global configuration properties file.
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 19.03.2013
 */
public class BasicDataSourceFactory {

  private BasicDataSource bds = null;

  /**
   * create a basic data source. if the url has not a autoReconnect=true.
   */
  private BasicDataSourceFactory() {
  }

  /** one and only instance of BasicDataSourceFactory */
  private volatile static BasicDataSourceFactory me;

  /**
   * return the one and only instance of BasicDataSourceFactory
   * 
   * @return the one and only instance of BasicDataSourceFactory
   */
  public static BasicDataSourceFactory getInstance() {
    if (me == null) {
      // ↖ no instance so far
      synchronized (BasicDataSourceFactory.class) {
        if (me == null) {
          // ↖ still no instance so far
          // ↓ the one and only me
          me = new BasicDataSourceFactory();
        }
      }
    }
    return me;
  }

  /**
   * short for {@link #getInstance()}
   * 
   * @return the one and only instance of BasicDataSourceFactory
   */
  public static BasicDataSourceFactory me() {
    return getInstance();
  }

  /**
   * return an instance of the {@link BasicDataSource}
   * 
   * @since 20.03.2013
   * @return the {@link BasicDataSource}
   */
  public BasicDataSource getBasicDataSource() {
    if (this.bds == null) {
      this.bds = new BasicDataSource();
      this.bds.setDriverClassName(PICKScreensConfig.globalProperty("sql_driver_class_name"));
      String url = PICKScreensConfig.globalProperty("sql_url");
      String autoReconnect = "autoReconnect=true";
      if (url.matches(".*" + autoReconnect + ".*") == false) {
        if (url.matches(".+\\?.+")) {
          // there is already a query
          url += "&" + autoReconnect;
        } else {
          // no query so far
          url += "?" + autoReconnect;
        }
      }
      this.bds.setUrl(url);
      this.bds.setUsername(PICKScreensConfig.globalProperty("sql_username"));
      this.bds.setPassword(PICKScreensConfig.globalProperty("sql_password"));
    }
    return this.bds;
  }
}
