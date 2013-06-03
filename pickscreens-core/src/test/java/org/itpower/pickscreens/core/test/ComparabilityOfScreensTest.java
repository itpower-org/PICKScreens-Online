// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.itpower.lib.crosstable.CrossTableHitRatio;
import org.itpower.lib.crosstable.CrossTableHitRatioViewNumber;
import org.itpower.lib.crosstable.CrossTableHitRatioViewPercentage;
import org.itpower.lib.crosstable.CrossTableView;
import org.itpower.pickscreens.core.control.CrossTableFactory;
import org.itpower.pickscreens.core.control.DuplicatesFactory;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.IngredientType;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;
import org.junit.Test;

/**
 * test the comparability of screens
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 */
public class ComparabilityOfScreensTest {

  @Test
  public void compareSameScreen() {
    Screen screen1 = new Screen();
    screen1.setName("screen 1");
    screen1.setFilename("fn");
    screen1.setId(1);
    this.prepareDuplicationTest(screen1);

    List<Screen> screens = new ArrayList<Screen>();
    screens.add(screen1);
    screens.add(screen1);

    CrossTableFactory factory = this.getCrossTableFactory(screen1, screen1);
    CrossTableHitRatio cthr = factory.getCrossTableHitRatio();
    assertEquals(0, cthr.getValue(0, 0).getMade());
    assertEquals(0, cthr.getValue(1, 0).getMade());
    assertEquals(0, cthr.getValue(0, 1).getMade());
    assertEquals(0, cthr.getValue(1, 1).getMade());

    CrossTableView ctvn = new CrossTableHitRatioViewNumber(cthr);
    assertEquals("0", ctvn.getView(0, 0));
    assertEquals("0", ctvn.getView(1, 0));
    assertEquals("0", ctvn.getView(0, 1));
    assertEquals("0", ctvn.getView(1, 1));

    CrossTableView ctvp = new CrossTableHitRatioViewPercentage(cthr);
    assertEquals("100 %", ctvp.getView(0, 0));
    assertEquals("100 %", ctvp.getView(1, 0));
    assertEquals("100 %", ctvp.getView(1, 1));
    assertEquals("100 %", ctvp.getView(0, 1));
  }

  private CrossTableFactory getCrossTableFactory(Screen... screens) {
    return new CrossTableFactory(Arrays.asList(screens));
  }

  @Test
  public void compareTwoEqualScreens() {
    Screen screen1 = new Screen();
    screen1.setName("screen 1");
    screen1.setFilename("fn");
    screen1.setId(1);

    Screen screen2 = new Screen();
    screen2.setName("screen 2");
    screen2.setFilename("fn");
    screen2.setId(2);

    this.prepareDuplicationTest(screen1, screen2);

    CrossTableFactory factory = this.getCrossTableFactory(screen1, screen1);
    CrossTableHitRatio cthr = factory.getCrossTableHitRatio();
    assertEquals(0, cthr.getValue(0, 0).getMade());
    assertEquals(0, cthr.getValue(1, 0).getMade());
    assertEquals(0, cthr.getValue(0, 1).getMade());
    assertEquals(0, cthr.getValue(1, 1).getMade());

    CrossTableView ctvn = new CrossTableHitRatioViewNumber(cthr);
    assertEquals("0", ctvn.getView(0, 0));
    assertEquals("0", ctvn.getView(1, 0));
    assertEquals("0", ctvn.getView(0, 1));
    assertEquals("0", ctvn.getView(1, 1));

    CrossTableView ctvp = new CrossTableHitRatioViewPercentage(cthr);
    assertEquals("100 %", ctvp.getView(0, 0));
    assertEquals("100 %", ctvp.getView(1, 0));
    assertEquals("100 %", ctvp.getView(1, 1));
    assertEquals("100 %", ctvp.getView(0, 1));
  }

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

  @Test
  public void compareTwoDifferentScreensWithSameRecipesCountPercentage() {
    Screen screen1 = new Screen();
    screen1.setName("screen 1");
    screen1.setFilename("foo.xls");

    List<Recipe> recipes1 = this.getRecipes("");
    screen1.setRecipes(recipes1);
    screen1.setId(1);
    screen1.setFilename("fn");

    Screen screen2 = new Screen();
    screen2.setName("screen 2");
    screen2.setFilename("foo.xls");

    List<Recipe> recipes2 = this.getRecipes("");
    screen2.setRecipes(recipes2);
    screen2.setId(2);

    this.prepareDuplicationTest(screen1, screen2);

    CrossTableFactory factory = this.getCrossTableFactory(screen1, screen2);
    CrossTableHitRatio cthr = factory.getCrossTableHitRatio();
    assertEquals(4, cthr.getValue(0, 0).getMade());
    assertEquals(4, cthr.getValue(1, 0).getMade());
    assertEquals(4, cthr.getValue(0, 1).getMade());
    assertEquals(4, cthr.getValue(1, 1).getMade());

    CrossTableView ctvn = new CrossTableHitRatioViewNumber(cthr);
    assertEquals("4", ctvn.getView(0, 0));
    assertEquals("4", ctvn.getView(1, 0));
    assertEquals("4", ctvn.getView(0, 1));
    assertEquals("4", ctvn.getView(1, 1));

    CrossTableView ctvp = new CrossTableHitRatioViewPercentage(cthr);
    assertEquals("100 %", ctvp.getView(0, 0));
    assertEquals("100 %", ctvp.getView(1, 0));
    assertEquals("100 %", ctvp.getView(1, 1));
    assertEquals("100 %", ctvp.getView(0, 1));
  }

  private void prepareDuplicationTest(Screen... screens) {
    DAOProxy.screendao().killEmAll();
    for (Screen screen : screens)
      screen.insert();
    DuplicatesFactory f = new DuplicatesFactory(screens);
    List<Duplicate> ds = f.getDuplicates();
    for (Duplicate d : ds)
      d.insert();

  }

  /**
   * get duplicates between given screens and not between a screen and another screen
   * 
   * 
   * @since 13.05.2013
   */
  @Test
  public void getDuplicatesOfTwoScreensOnly() {
    Screen screen1 = new Screen();
    screen1.setName("screen 1");
    screen1.setFilename("foo.xls");
    List<Recipe> recipes1 = this.getRecipes("1");
    recipes1.addAll(this.getRecipes("2"));
    screen1.setRecipes(recipes1);

    Screen screen2 = new Screen();
    screen2.setName("screen 2");
    screen2.setFilename("foo.xls");
    List<Recipe> recipes2 = this.getRecipes("1");
    screen2.setRecipes(recipes2);

    Screen screen3 = new Screen();
    screen3.setName("screen 3");
    screen3.setFilename("foo.xls");
    List<Recipe> recipes3 = this.getRecipes("2");
    recipes3.remove(0);
    screen3.setRecipes(recipes3);

    // self test
    assertEquals(8, recipes1.size());
    assertEquals(4, recipes2.size());
    assertEquals(3, recipes3.size());

    this.prepareDuplicationTest(screen1, screen2, screen3);

    List<Duplicate> ds = DAOProxy.screendao().getDuplicates(screen1, screen2);
    assertEquals(4, ds.size());

    ds = DAOProxy.screendao().getDuplicates(screen1, screen3);
    assertEquals(3, ds.size());

    ds = DAOProxy.screendao().getDuplicates(screen1);
    assertEquals(0, ds.size());

    ds = DAOProxy.screendao().getDuplicates(screen1, screen2, screen3);
    assertEquals(7, ds.size());
  }

  /**
   * scenario: two screens. screen1 with 8 recipes, screen2 with 4 recipes. 4 recipes of screen2 equals recipe no 1-4 of screen2.
   * 
   * 
   * @since 25.04.2013
   */
  @Test
  public void compareTwoDifferentScreensWithSameRecipesCountNumber() {
    Screen screen1 = new Screen();
    screen1.setName("screen 1");
    screen1.setFilename("foo.xls");
    List<Recipe> recipes1 = this.getRecipes("1");
    recipes1.addAll(this.getRecipes("2"));
    screen1.setRecipes(recipes1);

    Screen screen2 = new Screen();
    screen2.setName("screen 2");
    screen2.setFilename("foo.xls");
    List<Recipe> recipes2 = this.getRecipes("1");
    screen2.setRecipes(recipes2);

    // self test
    assertEquals(8, recipes1.size());
    assertEquals(4, recipes2.size());

    this.prepareDuplicationTest(screen1, screen2);

    // check comperators
    CrossTableFactory factory = this.getCrossTableFactory(screen1, screen2);
    CrossTableHitRatio cthr = factory.getCrossTableHitRatio();
    assertEquals(8, cthr.getValue(0, 0).getMade());
    assertEquals(4, cthr.getValue(1, 0).getMade());
    assertEquals(4, cthr.getValue(0, 1).getMade());
    assertEquals(4, cthr.getValue(1, 1).getMade());

    CrossTableView ctvn = new CrossTableHitRatioViewNumber(cthr);
    assertEquals("8", ctvn.getView(0, 0));
    assertEquals("4", ctvn.getView(1, 0));
    assertEquals("4", ctvn.getView(0, 1));
    assertEquals("4", ctvn.getView(1, 1));

    CrossTableView ctvp = new CrossTableHitRatioViewPercentage(cthr);
    assertEquals("100 %", ctvp.getView(0, 0));
    assertEquals("100 %", ctvp.getView(1, 0));
    assertEquals("100 %", ctvp.getView(1, 1));
    assertEquals("50 %", ctvp.getView(0, 1));

    // change one recipe of recipe 2
    recipes2.get(0).getBuffer().setAmount(999f);
    screen2.setRecipes(recipes2);
    this.prepareDuplicationTest(screen1, screen2);

    factory = this.getCrossTableFactory(screen1, screen2);
    cthr = factory.getCrossTableHitRatio();
    ctvn = new CrossTableHitRatioViewNumber(cthr);
    ctvp = new CrossTableHitRatioViewPercentage(cthr);

    // check comperators again
    ctvn = new CrossTableHitRatioViewNumber(cthr);
    assertEquals("8", ctvn.getView(0, 0));
    assertEquals("3", ctvn.getView(1, 0));
    assertEquals("3", ctvn.getView(0, 1));
    assertEquals("4", ctvn.getView(1, 1));

    ctvp = new CrossTableHitRatioViewPercentage(cthr);
    assertEquals("100 %", ctvp.getView(0, 0));
    assertEquals("75 %", ctvp.getView(1, 0));
    assertEquals("100 %", ctvp.getView(1, 1));
    assertEquals("38 %", ctvp.getView(0, 1));
  }
}