// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.aspects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;

/**
 * container for all config vars
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 19.03.2013
 */
public class PICKScreensConfig {

  private Properties properties;
  /** one and only instance of PICKScreensConfig */
  private volatile static PICKScreensConfig me;

  /**
   * return the one and only instance of PICKScreensConfig
   * 
   * @return the one and only instance of PICKScreensConfig
   */
  public static PICKScreensConfig getInstance() {
    if (me == null) {
      // ↖ no instance so far
      synchronized (PICKScreensConfig.class) {
        if (me == null) {
          // ↖ still no instance so far
          // ↓ the one and only me
          me = new PICKScreensConfig();
        }
      }
    }
    return me;
  }

  /**
   * short for {@link #getInstance()}
   * 
   * @return the one and only instance of PICKScreensConfig
   */
  public static PICKScreensConfig me() {
    return getInstance();
  }

  /**
   * construct with given configuration directory
   * 
   * 
   * @since 20.03.2013
   * @param configDirectory used for configuration parameters
   */
  private PICKScreensConfig() {
    InputStream inputStream = PICKScreensConfig.class.getResourceAsStream("/config.properties");
    PropertyResourceBundle pTmp;
    try {
      pTmp = new PropertyResourceBundle(inputStream);
      String configDirectory = pTmp.getString("config.directory");
      this.prepareLog(pTmp);
      PICKScreensLog.info("use config dir: " + configDirectory, 1303191528);
      assert configDirectory != null;
      assert this.properties == null;
      if (this.properties == null) {
        this.properties = new Properties();
        try {
          PropertyResourceBundle p = new PropertyResourceBundle(new FileInputStream(configDirectory + System.getProperty("file.separator") + "pickscreens.conf"));
          for (String key : p.keySet()) {
            this.properties.put(key, p.getString(key));
          }
        } catch (IOException ex) {
          PICKScreensLog.exception(ex, 1303191527);
        }
      }
    } catch (IOException e) {
      PICKScreensLog.exception(e, 1303211334);
    }
  }

  private void prepareLog(PropertyResourceBundle config) {
    String log4jFileStr = config.getString("log4j.file");
    File log4jFile = new File(log4jFileStr);
    if (!log4jFile.canWrite()) {
      if (!log4jFile.exists()) {
        try {
          log4jFile.createNewFile();
        } catch (IOException e) {
          System.err.println("could not create logfile");
        }
      }
      if (log4jFile.exists()) {
        if (!log4jFile.canWrite()) log4jFile.setWritable(true);
        if (!log4jFile.canWrite()) System.err.println("could not create logfile");
      } else {
        System.err.println("could not create logfile");
      }
    }
  }

  private String getGlobalProperty(String key) {
    return this.properties.getProperty(key);
  }

  /**
   * return the global properties as configured
   * 
   * 
   * @since 20.03.2013
   * @return global properties
   */
  protected Properties getGlobalProperties() {
    return properties;
  }

  /**
   * Return true, if this is the development environment.
   * 
   * @return true, if this is the development environment.
   */
  protected final boolean isDev() {
    return this.propertyValueEquals("env_dev", "true");
  }

  private boolean propertyValueEquals(String key, String value) {
    return this.getGlobalProperty(key) != null && this.getGlobalProperty(key).equalsIgnoreCase(value);
  }

  public static String globalProperty(String key) {
    return me().getGlobalProperty(key);
  }

}
