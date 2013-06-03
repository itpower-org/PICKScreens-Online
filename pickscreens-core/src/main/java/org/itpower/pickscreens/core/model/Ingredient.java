// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.model;

import org.itpower.pickscreens.core.control.PICKScreensSynonyms;
import org.itpower.pickscreens.core.dao.DAOProxy;

/**
 * part of a recipe
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 */
public class Ingredient {
  private Integer id;
  private Integer idRecipe;
  private IngredientType type;
  private String name;
  private String unit;
  private Float amount;

  public IngredientType getType() {
    return type;
  }

  public void setType(IngredientType type) {
    this.type = type;
  }

  public Integer getIdRecipe() {
    return idRecipe;
  }

  public void setIdRecipe(Integer idRecipe) {
    this.idRecipe = idRecipe;
  }

  public Ingredient() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
    this.name = PICKScreensSynonyms.me().getIngredientName(name);
  }

  /**
   * return the unit
   * 
   * 
   * @since 05.03.2013
   * @return the unit
   */
  public String getUnit() {
    return unit;
  }

  /**
   * the unit to set
   * 
   * 
   * @since 05.03.2013
   * @param unit the unit to set
   * @return this
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * return the amount
   * 
   * 
   * @since 05.03.2013
   * @return the amount
   */
  public Float getAmount() {
    return amount;
  }

  /**
   * the amount to set
   * 
   * 
   * @since 05.03.2013
   * @param amount the amount to set
   * @return this
   */
  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public boolean is(IngredientType type) {
    return this.type == type;
  }

  public String getAmountUnitName() {
    String result = String.format("%s %s %s", this.getAmount(), this.getUnit(), this.getName());
    return result.trim();
  }

  /**
   * return a string for comparsion of ingredients. case insensitive for name. case sensitive for unit.
   * 
   * 
   * @since 30.04.2013
   * @return
   */
  public String getComparaterString() {
    Character type = 'N';
    switch (this.getType()) {
    case BUFFER:
      type = 'B';
      break;
    case SALT1:
      type = 'S';
      break;
    case SALT2:
      type = 'T';
      break;
    case PRECIPITANT1:
      type = 'P';
      break;
    case PRECIPITANT2:
      type = 'Q';
      break;
    }
    String amount = (this.getAmount() + "").toLowerCase().replaceAll("\\s+", " ").trim();
    String unit = this.getUnit().replaceAll("\\s+", " ").trim();
    String name = this.getName().toLowerCase().replaceAll("\\s+", " ").trim();
    return String.format("%c:%s:%s:%s", type, amount, unit, name);
  }

  public Screen getScreen() {
    Screen result = null;
    if(this.idRecipe != null) {
      Recipe recipe = DAOProxy.screendao().getOneRecipe(this.idRecipe);
      if(recipe != null) {
        result = recipe.getScreen();
      }
    }
    return result;
  }

}
