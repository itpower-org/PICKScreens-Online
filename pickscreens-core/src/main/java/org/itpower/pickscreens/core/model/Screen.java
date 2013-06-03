// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.model;

import java.io.File;
import java.util.List;

import org.itpower.pickscreens.core.aspects.PICKScreensConfig;
import org.itpower.pickscreens.core.dao.DAOProxy;

/**
 * a screen containing recipes
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 * 
 */
public class Screen {
  private Integer id;
  private boolean freeForPublic = true;

  public boolean isFreeForPublic() {
    return freeForPublic;
  }

  public void setFreeForPublic(boolean freeForPublic) {
    this.freeForPublic = freeForPublic;
  }

  /**
   * get id
   * 
   * 
   * @since 19.03.2013
   * @return id of Screen
   */
  public Integer getId() {
    return id;
  }

  /**
   * set id
   * 
   * 
   * @since 19.03.2013
   * @param id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  private String name;
  private String filename;
  private List<Recipe> recipes;

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  /**
   * return the recipes
   * 
   * 
   * @since 05.03.2013
   * @return the recipes
   */
  public List<Recipe> getRecipes() {
    if (this.recipes == null) {
      this.recipes = DAOProxy.screendao().getRecipesOf(this);
    }
    return recipes;
  }

  /**
   * the recipes to set
   * 
   * 
   * @since 05.03.2013
   * @param recipes the recipes to set
   * @return this
   */
  public void setRecipes(List<Recipe> recipes) {
    for (Recipe recipe : recipes)
      recipe.setIdScreen(this.getId());
    this.recipes = recipes;
  }

  /**
   * return the name
   * 
   * 
   * @since 05.03.2013
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * the name to set
   * 
   * 
   * @since 05.03.2013
   * @param name the name to set
   * @return this
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * return your {@link Recipe} instances
   * 
   * 
   * @since 19.03.2013
   * @param i position of recipe
   * @return your recipes
   */
  public Recipe getRecipe(int i) {
    return this.getRecipes().get(i);
  }

  /**
   * insert yourself
   * 
   * 
   * @since 19.03.2013
   * @return true if succeeded
   */
  public boolean insert() {
    return DAOProxy.screendao().insert(this);
  }

  /**
   * return the file of the screen. if the file does not exist anymore or is not readable anymore return <code>null</code>.
   * 
   * 
   * @since 02.05.2013
   * @return the file of the screen
   */
  public File getFile() {
    File result = null;
    String directoryKey = this.isFreeForPublic() ? "dir_xls_public" : "dir_xls_private";
    String dir = PICKScreensConfig.globalProperty(directoryKey);
    result = new File(dir + this.getFilename());
    if (result.canRead() == false) result = null;
    return result;
  }

}
