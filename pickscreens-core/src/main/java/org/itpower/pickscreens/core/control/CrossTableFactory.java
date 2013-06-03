// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.control;

import java.util.ArrayList;
import java.util.List;

import org.itpower.lib.crosstable.CrossTableCharacteristics;
import org.itpower.lib.crosstable.CrossTableCharacteristicsDefault;
import org.itpower.lib.crosstable.CrossTableHitRatio;
import org.itpower.lib.model.HitRatio;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.model.Screen;

/**
 * produce cross tables from given screens
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 25.04.2013
 */
public class CrossTableFactory {
  private final List<Screen> screens;
  private CrossTableHitRatio crossTableHitRatio = null;

  public CrossTableFactory(List<Screen> screens) {
    this.screens = screens;
  }

  private HitRatio getHitRatio(int x, int y) {
    int possible = this.screens.get(x).getRecipes().size();
    int made = x == y ? possible : DAOProxy.screendao().getDuplicateRecipesCount(this.screens.get(x), this.screens.get(y));
    return new HitRatio(made, possible);
  }

  /**
   * {@inheritDoc} 
   * 
   * return a count of recipes being exactly the same.
   */
  public CrossTableHitRatio getCrossTableHitRatio() {
    if (crossTableHitRatio == null) {
      CrossTableCharacteristics characteristics = this.getCharacteristics();
      crossTableHitRatio = new CrossTableHitRatio("cross table", characteristics, characteristics);
      for (int x = 0; x < this.screens.size(); x++) {
        for (int y = 0; y < this.screens.size(); y++) {
          crossTableHitRatio.setValue(x, y, this.getHitRatio(x, y));
        }
      }
    }
    return crossTableHitRatio;
  }

  private CrossTableCharacteristics getCharacteristics() {
    List<String> characteristics = new ArrayList<String>(this.screens.size());
    for (Screen screen : this.screens) {
      characteristics.add(screen.getName());
    }
    return new CrossTableCharacteristicsDefault(characteristics);
  }
}
