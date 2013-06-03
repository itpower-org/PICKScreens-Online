// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.model;

import org.itpower.pickscreens.core.aspects.PICKScreensLog;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * duplication of a recipe in another screen
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 30.04.2013
 * 
 */
public class Duplicate {
  private Integer id, idScreen1, idScreen2, idRecipe1, idRecipe2;
  private String comparatorString;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIdScreen1() {
    return idScreen1;
  }

  public void setIdScreen1(Integer idScreen1) {
    this.idScreen1 = idScreen1;
  }

  public Integer getIdScreen2() {
    return idScreen2;
  }

  public void setIdScreen2(Integer idScreen2) {
    this.idScreen2 = idScreen2;
  }

  public Integer getIdRecipe1() {
    return idRecipe1;
  }

  public void setIdRecipe1(Integer idRecipe1) {
    this.idRecipe1 = idRecipe1;
  }

  public Integer getIdRecipe2() {
    return idRecipe2;
  }

  public void setIdRecipe2(Integer idRecipe2) {
    this.idRecipe2 = idRecipe2;
  }

  public void setComparatorString(String comparatorString) {
    this.comparatorString = comparatorString;
  }

  public String getComparatorString() {
    return comparatorString;
  }

  /**
   * set the ids of the given {@link Recipe}s and the comparator string.
   * 
   * 
   * @since 30.04.2013
   * @param recipe1
   * @param recipe2
   */
  public void setRecipes(Recipe recipe1, Recipe recipe2) {
    this.setIdRecipe1(recipe1.getId());
    this.setIdRecipe2(recipe2.getId());
    this.setComparatorString(recipe1.getComparatorString());
  }

  /**
   * 
   * set the ids of the given {@link Screen}s
   * 
   * 
   * @since 30.04.2013
   * @param screen1
   * @param screen2
   */
  public void setScreens(Screen screen1, Screen screen2) {
    this.setIdScreen1(screen1.getId());
    this.setIdScreen2(screen2.getId());
  }

  public boolean insert() {
    return DAOProxy.screendao().insert(this);
  }

  /**
   * produce a {@link Duplicate} with the given {@link Recipe}s and the
   * {@link Screen}s of the {@link Recipe}s.
   * 
   * 
   * @since 30.04.2013
   * @param duplicateRecipe1
   * @param duplicateRecipe2
   * @return
   */
  public static Duplicate getDuplicate(Recipe duplicateRecipe1, Recipe duplicateRecipe2) {
    Duplicate result = new Duplicate();
    result.setRecipes(duplicateRecipe1, duplicateRecipe2);
    result.setScreens(duplicateRecipe1.getScreen(), duplicateRecipe2.getScreen());
    return result;
  }

  public JSONObject toJSON(boolean withRecipeSubstances) {
    JSONObject result = new JSONObject();
    try {
      result.put("id", this.id);
      result.put("comparatorString", this.comparatorString);
      result.put("idRecipe1", this.idRecipe1);
      result.put("idRecipe2", this.idRecipe2);
      result.put("idScreen1", this.idScreen1);
      result.put("idScreen2", this.idScreen2);
      Recipe recipe1 = DAOProxy.screendao().getOneRecipe(this.idRecipe1);
      Recipe recipe2 = DAOProxy.screendao().getOneRecipe(this.idRecipe2);
      String format = "%s (%s)";
      result.put("screen1_condition_number", String.format(format, recipe1.getScreen().getName(), recipe1.getNumber()));
      result.put("screen2_condition_number", String.format(format, recipe2.getScreen().getName(), recipe2.getNumber()));
      if(withRecipeSubstances) {
        result.put("recipe_substances", recipe1.getTextView());
      }
    } catch (JSONException e) {
      PICKScreensLog.exception(e, 1305081137);
    }
    return result;
  }

}
