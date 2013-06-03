// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * a screen containing recipes
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 */
public class Recipe {
  private Integer id;
  private Integer idScreen;

  public Recipe() {
    this.ingredients = new ArrayList<Ingredient>();
  }

  public Integer getIdScreen() {
    return idScreen;
  }

  public Screen getScreen() {
    Screen result = null;
    if (idScreen != null) result = DAOProxy.screendao().getOne(idScreen);
    return result;
  }

  public void setIdScreen(Integer idScreen) {
    this.idScreen = idScreen;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  private Integer number = 1;
  private List<Ingredient> ingredients = null;

  /**
   * 
   * @since 20.03.2013
   * @return
   */
  public List<Ingredient> getIngredients() {
    if (this.ingredients.size() == 0) {
      this.ingredients = DAOProxy.screendao().getIngredientsOf(this);
    }
    return ingredients;
  }

  /**
   * 
   * @since 20.03.2013
   * @param ingredients
   */
  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = new ArrayList<Ingredient>();
    for (Ingredient ingredient : ingredients)
      this.addIngredient(ingredient);
  }

  /**
   * return the number
   * 
   * 
   * @since 05.03.2013
   * @return the number
   */
  public Integer getNumber() {
    return number;
  }

  /**
   * the number to set
   * 
   * 
   * @since 05.03.2013
   * @param number the number to set
   * @return this
   */
  public void setNumber(Integer number) {
    this.number = number;
  }

  /**
   * if ingredient is not null and has a type, add it to the ingredients of this recipe and set the id of the ingredient to this id. if ingredient is
   * null do nothing. if ingredient has no type throw a DataIntegrityViolationException.
   * 
   * 
   * @since 20.03.2013
   * @param ingredient to add to the recipe
   */
  public void addIngredient(Ingredient ingredient) {
    if (ingredient != null) {
      if (ingredient.getType() == null) {
        throw new DataIntegrityViolationException("Ingredient must have a type [1303201258]");
      } else {
        ingredient.setIdRecipe(this.getId());
        this.ingredients.add(ingredient);
      }
    }
  }

  public Ingredient getBuffer() {
    return this.getFirstIngredientOfType(IngredientType.BUFFER);
  }

  private Ingredient getFirstIngredientOfType(IngredientType type) {
    Ingredient result = null;
    for (Ingredient ingredient : this.getIngredients()) {
      if (ingredient.is(type)) {
        result = ingredient;
        break;
      }
    }
    return result;
  }

  public Ingredient getPrecipitant1() {
    return this.getFirstIngredientOfType(IngredientType.PRECIPITANT1);
  }

  public Ingredient getSalt1() {
    return this.getFirstIngredientOfType(IngredientType.SALT1);
  }

  public Ingredient getPrecipitant2() {
    return this.getFirstIngredientOfType(IngredientType.PRECIPITANT2);
  }

  public Ingredient getSalt2() {
    return this.getFirstIngredientOfType(IngredientType.SALT2);
  }

  public String getTextView() {
    List<String> results = new ArrayList<String>();
    
    Ingredient salt1 = this.getSalt1();
    if(salt1 != null) results.add("Salt 1: " + salt1.getAmountUnitName());

    Ingredient salt2 = this.getSalt2();
    if(salt2 != null) results.add("Salt 2: " + salt2.getAmountUnitName());

    Ingredient buffer = this.getBuffer();
    if(buffer != null) results.add("Buffer: " + buffer.getAmountUnitName());

    Ingredient precipitant1 = this.getPrecipitant1();
    if(precipitant1 != null) results.add("Precipitant 1: " + precipitant1.getAmountUnitName());

    Ingredient precipitant2 = this.getPrecipitant2();
    if(precipitant2 != null) results.add("Precipitant 2: " + precipitant2.getAmountUnitName());

    return StringUtils.join(results, "; ");
  }
  public String getComparatorString() {
    Ingredient salt1 = this.getSalt1();
    String salt1str = salt1 == null ? "-" : salt1.getComparaterString();

    Ingredient salt2 = this.getSalt2();
    String salt2str = salt2 == null ? "-" : salt2.getComparaterString();

    Ingredient buffer = this.getBuffer();
    String bufferstr = buffer == null ? "-" : buffer.getComparaterString();

    Ingredient precipitant1 = this.getPrecipitant1();
    String precipitant1str = precipitant1 == null ? "-" : precipitant1.getComparaterString();

    Ingredient precipitant2 = this.getPrecipitant2();
    String precipitant2str = precipitant2 == null ? "-" : precipitant2.getComparaterString();

    return String.format("%s|%s|%s|%s|%s", salt1str, salt2str, bufferstr, precipitant1str, precipitant2str);
  }
  
}
