/*
 * © 2013 by IT-Power GmbH (http://www.it-power.org)
 */package org.itpower.pickscreens.cron.test;

import java.io.File;
import java.net.URL;

/**
 * 
 * generate valid beans
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 * 
 */
public class SimpleTestBeanFactory {

  public static File[] getTestXLSFiles() {
    URL testResourcesUrl = SimpleTestBeanFactory.class.getResource("/");
    File currentFolder = new File(testResourcesUrl.getFile());
    File testResourcesFolder = new File(currentFolder.getParentFile().getParentFile().getAbsolutePath() + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "xls");
    return testResourcesFolder.listFiles();
  }

  public static File getTestXLSFile(String testfile) {
    File result = null;
    for (File file : getTestXLSFiles()) {
      if (file.getName().equals(testfile)) {
        result = file;
        break;
      }
    }
    return result;
  }

}
