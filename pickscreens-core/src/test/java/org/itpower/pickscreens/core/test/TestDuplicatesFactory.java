/*
 * © 2013 by IT-Power GmbH (http://www.it-power.org)
 */package org.itpower.pickscreens.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.itpower.pickscreens.core.control.DuplicatesFactory;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.IngredientType;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;
import org.junit.Test;

/**
 * test duplicate factory
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 21.03.2013
 * 
 */
public class TestDuplicatesFactory {
  
  private Recipe getRecipe(int number, List<Ingredient> ingredients) {
    Recipe result = new Recipe();
    result.setNumber(number);
    result.setIngredients(ingredients);
    return result;
  }

  private Ingredient getIngredient(String name, String unit, float amount, IngredientType type) {
    Ingredient result = new Ingredient();
    result.setName(name);
    result.setUnit(unit);
    result.setAmount(amount);
    result.setType(type);
    return result;
  }

  private List<Ingredient> getIngredients(String nameSalt) {
    List<Ingredient> result = new ArrayList<Ingredient>();
    result.add(this.getIngredient(nameSalt + "_buffer", "unit", 1.5f, IngredientType.BUFFER));
    result.add(this.getIngredient(nameSalt + "_salt1", "unit", 1.5f, IngredientType.SALT1));
    result.add(this.getIngredient(nameSalt + "_precipitant2", "unit", 1.5f, IngredientType.PRECIPITANT2));
    return result;
  }

  private List<Recipe> getRecipes(String salt) {
    List<Recipe> result = new ArrayList<Recipe>();
    result.add(this.getRecipe(0, this.getIngredients("foo" + salt)));
    result.add(this.getRecipe(1, this.getIngredients("bar" + salt)));
    result.add(this.getRecipe(2, this.getIngredients("oof" + salt)));
    result.add(this.getRecipe(3, this.getIngredients("rab" + salt)));
    return result;
  }

  /**
   * insert three screens as configured
   * 
   * 
   * @since 30.04.2013
   * @return true on success
   */
  private void insertScreensWithDuplicateRecipes() {
    DAOProxy.screendao().killEmAll();
    Screen screen1 = new Screen();
    screen1.setName("screen 1");
    screen1.setFilename("foo.xls");
    screen1.setId(1);
    List<Recipe> recipes1 = this.getRecipes("1");
    recipes1.addAll(this.getRecipes("2"));
    screen1.setRecipes(recipes1);

    Screen screen2 = new Screen();
    screen2.setName("screen 2");
    screen2.setFilename("foo.xls");
    screen2.setId(2);
    List<Recipe> recipes2 = this.getRecipes("1");
    recipes2.get(0).getBuffer().setAmount(999f);
    screen2.setRecipes(recipes2);


    Screen screen3 = new Screen();
    screen3.setName("screen 3");
    screen3.setFilename("foo.xls");
    screen3.setId(3);
    List<Recipe> recipes3 = this.getRecipes("1");
    screen3.setRecipes(recipes3);

    // self test
    assertEquals(8, recipes1.size());
    assertEquals(4, recipes2.size());
    assertEquals(4, recipes3.size());
    screen1.insert();
    screen2.insert();
    screen3.insert();
  }

  @Test
  public void produceDuplicates() {
    this.insertScreensWithDuplicateRecipes();
    try {
      DuplicatesFactory factory = new DuplicatesFactory();
      List<Duplicate> duplicates = factory.getDuplicates();
      for(Duplicate duplicate : duplicates) duplicate.insert();
      assertEquals(10, duplicates.size());
    } catch (Exception e) {
      fail("should not throw exception");
    }
  }

}
