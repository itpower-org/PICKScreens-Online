// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.cron.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;

import org.itpower.pickscreens.core.aspects.PICKScreensConfig;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;
import org.itpower.pickscreens.core.control.DuplicatesFactory;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.model.Duplicate;

/**
 * kill all entries in database and reinit all xls-files (containing screens and recipes and ingredients).
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 21.03.2013
 */
public class Main {

  /**
   * print messages to this {@link PrintStream}
   */
  private static PrintStream out = System.out;

  private static void errAndExit() {
    out.println("ERROR configuration problems! check logfiles.");
    System.exit(1);
  }

  private static boolean readingAndInsertFiles() {
    boolean result = false;
    out.println(String.format("START READING AND INSERTING FILES: %s", new Date()));
    try {
      ConfigurationFileFactory publicFileFactory = new ConfigurationFileFactory(true);
      ConfigurationFileFactory privateFileFactory = new ConfigurationFileFactory(false);
      List<File> publicFiles = publicFileFactory.getXLSFiles();
      List<File> privateFiles = privateFileFactory.getXLSFiles();
      if (publicFiles.size() > 0) {
        DAOProxy.screendao().killEmAll();
        ScreenFactory factory;
        for (File publicFile : publicFiles) {
          out.println(String.format("Insert public screens of file: %s", publicFile));
          factory = new ScreenFactoryFromXLSFile(publicFile);
          factory.getScreen(true).insert();
        }
        for (File privateFile : privateFiles) {
          out.println(String.format("Insert private screens of file: %s", privateFile));
          factory = new ScreenFactoryFromXLSFile(privateFile);
          factory.getScreen(false).insert();
        }
        result = true;
      } else {
        PICKScreensLog.error("no public files", 1303211223);
        errAndExit();
      }
    } catch (ConfigurationException e) {
      PICKScreensLog.exception(e, 1303211219);
      errAndExit();
    }
    out.println(String.format("END READING AND INSERTING FILES: %s", new Date()));
    return result;
  }

  public static void main(String[] args) {
    if (execute()) {
      boolean succ = readingAndInsertFiles();
      if (succ) {
        createAndInsertingDuplicates();
      }
    } else {
      out.println("CRONTAB NOT EXECUTED");
    }
  }

  private static boolean execute() {
    String filename = PICKScreensConfig.globalProperty("stop_crontab_file");
    File file = new File(filename);
    boolean result = !file.exists();
    try {
      if (file.createNewFile()) {
        out.println("created " + filename);
      }
    } catch (IOException e) {
      out.println("FAIL created " + filename);
    }
    return result;
  }

  private static void createAndInsertingDuplicates() {
    out.println(String.format("START PRODUCING AND INSERTING DUPLICATES: %s", new Date()));
    DuplicatesFactory factory = new DuplicatesFactory();
    List<Duplicate> duplicates = factory.getDuplicates();
    for (Duplicate duplicate : duplicates) {
      out.println(String.format("insert: %s|%s|%s|%s|%s", duplicate.getIdScreen1(), duplicate.getIdScreen2(), duplicate.getIdRecipe1(), duplicate.getIdRecipe2(), duplicate.getComparatorString()));
      duplicate.insert();
    }
    out.println(String.format("END PRODUCING AND INSERTING DUPLICATES: %s", new Date()));
  }

}
