// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.itpower.lib.util.CSVParserUtil;
import org.itpower.lib.util.Synonyms;
import org.itpower.lib.util.SynonymsDefault;
import org.itpower.pickscreens.core.aspects.PICKScreensConfig;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;

/**
 * handler for synonyms of substances where every name has a master name used in the system.
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 07.05.2013
 */
public class PICKScreensSynonyms {

  private Map<String, Synonyms> synonymsList;
  private List<String> synonymNames;
  private CSVParserUtil cp;

  /** one and only instance of PICKScreensSynonyms */
  private volatile static PICKScreensSynonyms me;

  /** construct PICKScreensSynonyms */
  private PICKScreensSynonyms() {
    cp = new CSVParserUtil();
    this.setFile(new File(PICKScreensConfig.globalProperty("file_csv_synonyms")));
  }

  /**
   * return the one and only instance of PICKScreensSynonyms
   * 
   * @return the one and only instance of PICKScreensSynonyms
   */
  public static PICKScreensSynonyms getInstance() {
    if (me == null) {
      // ↖ no instance so far
      synchronized (PICKScreensSynonyms.class) {
        if (me == null) {
          // ↖ still no instance so far
          // ↓ the one and only me
          me = new PICKScreensSynonyms();
        }
      }
    }
    return me;
  }

  /**
   * short for {@link #getInstance()}
   * 
   * @return the one and only instance of PICKScreensSynonyms
   */
  public static PICKScreensSynonyms me() {
    return getInstance();
  }

  /**
   * set the csv file the synonyms are read from.
   * 
   * 
   * @since 07.05.2013
   * @param csv
   */
  public void setFile(File csv) {
    List<String[]> matrix;
    synonymsList = new HashMap<String, Synonyms>();
    synonymNames = new ArrayList<String>();
    try {
      matrix = cp.getMatrixFrom(csv);
      for (String[] row : matrix) {
        synonymsList.put(row[0], new SynonymsDefault(row));
        synonymNames.addAll(Arrays.asList(row));
      }
    } catch (IOException e) {
      PICKScreensLog.exception("could not read csv into matrix", e, 1305071236);
    }
  }

  /**
   * return the ingredient name used for the ingredient.
   * 
   * 
   * @since 07.05.2013
   * @param name
   * @return
   */
  public String getIngredientName(String name) {
    String result = name;
    if (synonymNames != null && synonymNames.contains(name)) {
      for (String masterName : synonymsList.keySet()) {
        if (synonymsList.get(masterName).isKnown(name)) {
          result = masterName;
          break;
        }
      }
    }
    return result;
  }

  public List<String> getSynonyms(String substance) {
    List<String> result = null;
    if (synonymsList != null && synonymsList.containsKey(substance)) {
      result = synonymsList.get(substance).getSynonymsOf(substance);
    }
    return result;
  }
}
