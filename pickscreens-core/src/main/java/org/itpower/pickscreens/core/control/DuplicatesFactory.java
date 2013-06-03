// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;

/**
 * produce {@link Duplicate} objects
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 30.04.2013
 */
public class DuplicatesFactory {

  private List<Screen> screens;

  /**
   * factory with all available screens.
   * 
   * 
   * @since 30.04.2013
   */
  public DuplicatesFactory() {
    this(DAOProxy.screendao().getAll());
  }

  public DuplicatesFactory(List<Screen> screens) {
    this.screens = screens;
  }
  public DuplicatesFactory(Screen ... screens) {
    this(Arrays.asList(screens));
  }
  /**
   * return a list with duplicate {@link Recipe}s in {@link Screen}s set without using the database.
   * 
   * 
   * @since 30.04.2013
   * @return
   */
  public List<Duplicate> getDuplicates() {
    // collect from screens
    Map<String, List<Recipe>> recipesWithComparatorString = new HashMap<String, List<Recipe>>();
    for (Screen screen : this.screens) {
      for (Recipe recipe : screen.getRecipes()) {
        String comparatorString = recipe.getComparatorString();
        if (!recipesWithComparatorString.containsKey(comparatorString)) {
          recipesWithComparatorString.put(comparatorString, new ArrayList<Recipe>());
        }
        List<Recipe> existings = recipesWithComparatorString.get(comparatorString);
        existings.add(recipe);
        recipesWithComparatorString.put(comparatorString, existings);
      }
    }
    // set duplicates
    List<Duplicate> result = new ArrayList<Duplicate>();
    for (String comparatorString : recipesWithComparatorString.keySet()) {
      if (recipesWithComparatorString.get(comparatorString).size() > 1) {
        List<Recipe> duplicateRecipes = recipesWithComparatorString.get(comparatorString);
        while (duplicateRecipes.size() > 1) {
          Recipe duplicateRecipe1 = duplicateRecipes.remove(0);
          for (Recipe duplicateRecipe2 : duplicateRecipes) {
            Duplicate duplicate = Duplicate.getDuplicate(duplicateRecipe1, duplicateRecipe2);
            result.add(duplicate);
          }
        }
      }
    }
    return result;
  }

}
