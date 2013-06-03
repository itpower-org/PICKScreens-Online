/*
 * © 2013 by IT-Power GmbH (http://www.it-power.org)
 */
package org.itpower.pickscreens.cron.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * test the tests and the environment
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 */
public class SelfTest {

  @Test
  public void testTests() {
    assertTrue(true);
  }

  @Test
  public void testFilesExist() {
    File[] testfiles = SimpleTestBeanFactory.getTestXLSFiles();
    assertEquals(4, testfiles.length);
  }
}