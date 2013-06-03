/*
 * © 2013 by IT-Power GmbH (http://www.it-power.org)
 */package org.itpower.pickscreens.cron.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.itpower.pickscreens.cron.control.ConfigurationFileFactory;
import org.junit.Test;

/**
 * 
 * test configuration factory
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 21.03.2013
 * 
 */
public class TestConfigurationFileFactory {
  @Test
  public void noException() {
    try {
      ConfigurationFileFactory factory = new ConfigurationFileFactory(true);
      assertTrue(true);
      assertTrue(factory.getXLSFiles().size() > 0);
    } catch (Exception e) {
      fail("should not throw exception");
    }
  }

}
