package org.itpower.pickscreens.core.dao;

import java.util.List;

import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;

public interface ScreenDAO extends DAO<Screen> {
  /**
   * return a list with the recipes of the screen. if screen does not have an id
   * or on other problems return empty list.
   * 
   * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
   * @since 20.03.2013
   * @param recipe
   * @return recipes of screen
   */
  List<Recipe> getRecipesOf(Screen screen);

  /**
   * return a list with the ingredients of the recipe. if recipe does not have
   * an id or on other problems return empty list.
   * 
   * 
   * @since 20.03.2013
   * @param recipe
   * @return ingredients of recipe
   */
  List<Ingredient> getIngredientsOf(Recipe recipe);

  List<Screen> getAll();

  List<Screen> getAll(boolean freeForPublic);

  List<Recipe> getRecipes(PICKScreensQuery pq);

  List<Screen> getScreens(List<Integer> searchInScreens);

  boolean insert(Duplicate duplicate);

  List<Duplicate> getDuplicates();

  /**
   * return a list with duplicates in given screens (not between a given screen and a third one).
   * 
   * 
   * @since 13.05.2013
   * @param screens
   * @return
   */
  List<Duplicate> getDuplicates(List<Screen> screens);

  Integer getDuplicateRecipesCount(Screen screen1, Screen screen2);

  /**
   * return all substance names with same name
   * 
   * 
   * @since 06.05.2013
   * @return
   */
  List<String> getAllSubstanceNames();

  Recipe getOneRecipe(int id);

  /**
   * alias for {@link #getDuplicates(List)}
   * 
   * 
   * @since 13.05.2013
   * @param screen1
   * @param screen2
   * @return
   */
  List<Duplicate> getDuplicates(Screen ... screens);
}
