// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.cron.control;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.itpower.pickscreens.core.aspects.PICKScreensConfig;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;

/**
 * produce {@link File} objects with all xls files in the public or private
 * directory.
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 21.03.2013
 */
public class ConfigurationFileFactory {
  private File directory;

  public ConfigurationFileFactory(boolean freeForPublic) throws ConfigurationException {
    String directoryKey = freeForPublic ? "dir_xls_public" : "dir_xls_private";
    String dir = PICKScreensConfig.globalProperty(directoryKey);
    this.directory = new File(dir);
    if (!this.directory.isDirectory()) throw new ConfigurationException("is not a directory: " + this.directory);
    if (!this.directory.canRead()) throw new ConfigurationException("is not a readable directory: " + this.directory);
  }

  public List<File> getXLSFiles() {
    List<File> result = new ArrayList<File>();
    FilenameFilter filter = new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith("xls");
      }
    };
    File[]  candidates = this.directory.listFiles(filter);
    for (File candidate : candidates) {
      if (candidate.canRead()) {
        result.add(candidate);
      } else {
        PICKScreensLog.error("cannot read " + candidate, 1303211218);
      }
    }
    return result;
  }

}
