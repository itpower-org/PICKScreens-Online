// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.dao.PICKScreensQuery;
import org.itpower.pickscreens.core.dao.PICKScreensQueryDefault;
import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.IngredientType;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;
import org.junit.Test;

/**
 * test the tests and the environment
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 */
public class DAOTest {

  @Test
  public void testInsertAndGetScreen() {
    Screen screen = this.getScreen();
    screen.setFilename("foo_1.xls");
    assertTrue(screen.insert());
    assertNotNull(screen.getId());
    Screen back = DAOProxy.screendao().getOne(screen.getId());
    assertNotNull(back);
    assertTrue(back.isFreeForPublic());
    assertEquals("foo_1.xls", back.getFilename());
  }

  @Test
  public void getSubstanceNames() {
    DAOProxy.screendao().killEmAll();
    Screen screen = this.getTestScreen("1a", "1a", "1b", "1c");
    screen.insert();
    List<String> substances = DAOProxy.screendao().getAllSubstanceNames();
    assertEquals(3, substances.size());
  }

  @Test
  public void testInsertAndGetPrivateScreen() {
    Screen screen = this.getScreen();
    screen.setFreeForPublic(false);
    assertTrue(screen.insert());
    assertNotNull(screen.getId());
    Screen back = DAOProxy.screendao().getOne(screen.getId());
    assertNotNull(back);
    assertFalse(back.isFreeForPublic());
  }

  private List<Recipe> getRecipes(List<Ingredient> ingredients) {
    List<Recipe> result = new ArrayList<Recipe>();
    Recipe r1 = new Recipe();
    r1.setNumber(1);
    r1.setIngredients(ingredients);
    result.add(r1);
    return result;
  }

  private List<Recipe> getRecipes() {
    return this.getRecipes("milk");
  }

  private List<Recipe> getRecipes(String ingredientName) {
    List<Ingredient> ingredients = new ArrayList<Ingredient>();
    ingredients.add(this.getIngredient(ingredientName, 1.1f));
    return this.getRecipes(ingredients);
  }

  private Ingredient getIngredient(String name, String unit, float amount) {
    Ingredient result = new Ingredient();
    result.setName(name);
    result.setUnit(unit);
    result.setAmount(amount);
    result.setType(IngredientType.BUFFER);
    return result;
  }

  private Ingredient getIngredient(String name, float amount) {
    return this.getIngredient(name, "l", amount);
  }

  @Test
  public void testInsertAndGetScreenWithRecipe() {
    Screen screen = this.getScreen();
    assertTrue(screen.insert());
    Screen back = DAOProxy.screendao().getOne(screen.getId());
    assertNotNull(back);
    assertEquals(this.getRecipes().size(), back.getRecipes().size());
  }

  private Screen getScreen() {
    return this.getScreen("screen " + counter++);
  }

  private int counter = 0;

  @Test
  public void testInsertAndGetScreenWithRecipeAndIngredient() {
    Screen screen = this.getScreen();
    assertTrue(screen.insert());
    Screen back = DAOProxy.screendao().getOne(screen.getId());
    assertNotNull(back);
    assertNotNull(back.getRecipe(0).getIngredients());
    assertTrue(back.getRecipe(0).getIngredients().size() > 0);
    assertEquals("milk", back.getRecipe(0).getIngredients().get(0).getName());
    assertEquals("l", back.getRecipe(0).getIngredients().get(0).getUnit());
    assertEquals("1.1", back.getRecipe(0).getIngredients().get(0).getAmount() + "");
  }

  @Test
  public void testInsertAndGetList() {

    // prepare data
    String search = new Date().getTime() + "";
    String ingredientName = "foo bar" + search + "and more";

    Ingredient buffer = new Ingredient();
    buffer.setName(ingredientName);
    buffer.setUnit("l");
    buffer.setAmount(1.1f);
    buffer.setType(IngredientType.BUFFER);
    List<Ingredient> ingredients = new ArrayList<Ingredient>();
    ingredients.add(buffer);

    List<Recipe> recipes = this.getRecipes(ingredients);

    Screen screen = this.getScreen();
    screen.setRecipes(recipes);
    screen.setFreeForPublic(false);
    assertTrue(screen.insert());

    // get all with the search
    PICKScreensQuery pq = new PICKScreensQueryDefault(null, search, null);
    List<Recipe> result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ingredientName, result.get(0).getBuffer().getName());
    assertEquals("l", result.get(0).getBuffer().getUnit());

    // get private only returns result
    pq = new PICKScreensQueryDefault(false, search, null);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());

    // get public only returns no result
    pq = new PICKScreensQueryDefault(true, search, null);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());
  }

  @Test
  public void testInsertAndGetListWithAmountWithFilter() {

    // prepare data
    Ingredient ing = this.getIngredient("water", "ml", 4.2f);
    Screen screen = this.getTestScreen(ing);
    assertTrue(screen.insert());

    List<Integer> searchInScreens = new ArrayList<Integer>(1);
    searchInScreens.add(screen.getId());

    // get exactly that
    String search = "=4.2 water";
    PICKScreensQuery pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    List<Recipe> result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get nothing
    search = "<4.2 water";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // get nothing
    search = ">4.2 water";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // get it!
    search = ">=4 ate";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it!
    search = ">=4";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it!
    search = ">4.19999";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it!
    search = ">=4.20001";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // get it!
    search = ">4.1 <5";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it!
    search = ">4.1 <5 ater";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it not!
    search = ">4.2 <5";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // get it not!
    search = ">1 <4.2";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // get it!
    search = ">4.1ml <5ml";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it!
    search = ">4.1 <5ml";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it NOT!
    search = ">4.1l <5ml";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // get it NOT!
    search = ">4.1%";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // get it!
    search = ">4,1 <5ml WATER";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());

    // get it!
    search = "  >4,1      <5ml WATER    ";
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ing.getId(), result.get(0).getIngredients().get(0).getId());
  }

  @Test
  public void testInsertAndGetListWithFilter() {

    // prepare data
    String search = new Date().getTime() + "";
    String ingredientName = "foo bar" + search + "and more";
    Screen screen = this.getTestScreen(ingredientName);
    assertTrue(screen.insert());

    List<Integer> searchInScreens = new ArrayList<Integer>(1);
    searchInScreens.add(screen.getId());
    searchInScreens.add(99999999); // nonesense id

    // get all with the filter
    PICKScreensQuery pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    List<Recipe> result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
    assertEquals(ingredientName, result.get(0).getBuffer().getName());
    assertEquals("l", result.get(0).getBuffer().getUnit());

    // same query with other filter
    searchInScreens = new ArrayList<Integer>(1);
    searchInScreens.add(99999999); // nonesense id only
    pq = new PICKScreensQueryDefault(null, search, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());
  }

  private Screen getTestScreen(List<Ingredient> ingredients) {
    List<Recipe> recipes = this.getRecipes(ingredients);
    Screen result = this.getScreen();
    result.setRecipes(recipes);
    result.setFreeForPublic(false);
    return result;
  }

  private Screen getTestScreen(Ingredient... ingredients) {
    return this.getTestScreen(Arrays.asList(ingredients));
  }

  private Screen getTestScreen(String... ingredientNames) {
    List<Ingredient> ingredients = new ArrayList<Ingredient>();
    for (String ingredientName : ingredientNames) {
      Ingredient ing = new Ingredient();
      ing.setName(ingredientName);
      ing.setUnit("l");
      ing.setAmount(1.1f);
      ing.setType(IngredientType.BUFFER);
      ingredients.add(ing);
    }
    return this.getTestScreen(ingredients);
  }

  @Test
  public void testSearchWithMultipleSubstances() {
    DAOProxy.screendao().killEmAll();
    String search1 = new Date().getTime() + "a";
    String search2 = new Date().getTime() + "b";
    String ingredientName1 = "foo bar" + search1 + "and more";
    String ingredientName2 = "foo bar" + search2 + "and more";
    Screen screen = this.getTestScreen(ingredientName1, ingredientName2);
    assertTrue(screen.insert());
    List<Integer> searchInScreens = new ArrayList<Integer>(1);
    searchInScreens.add(screen.getId());
    searchInScreens.add(99999999); // nonesense id

    // find the recipe
    PICKScreensQuery pq = new PICKScreensQueryDefault(null, search1 + "& " + search2, searchInScreens);
    List<Recipe> result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());

    // search without whitespace
    pq = new PICKScreensQueryDefault(null, search1 + "&" + search2, searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());

    // with only one name matches
    pq = new PICKScreensQueryDefault(null, search1 + "& notAIngredient", searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(0, result.size());

    // case insensitive
    pq = new PICKScreensQueryDefault(null, search1.toUpperCase() + "& " + search2.toUpperCase(), searchInScreens);
    result = DAOProxy.screendao().getRecipes(pq);
    assertEquals(1, result.size());
  }

  @Test
  public void testKillEmAll() {
    Screen screen = this.getScreen();
    assertTrue(screen.insert());
    Screen back = DAOProxy.screendao().getOne(screen.getId());
    assertNotNull(back);
    Duplicate d = new Duplicate();
    d.setRecipes(screen.getRecipe(0), screen.getRecipe(0));
    d.setScreens(screen, screen);
    assertTrue(d.insert());
    DAOProxy.screendao().killEmAll();
    assertNull(DAOProxy.screendao().getOne(screen.getId()));
    assertEquals(0, DAOProxy.screendao().getDuplicates().size());
  }

  private Screen getScreen(String name, List<Recipe> recipes) {
    Screen result = new Screen();
    result.setName(name);
    result.setFilename("foo.xls");
    result.setRecipes(recipes);
    return result;
  }

  @Test
  public void insertAndSetDuplicate() {
    DAOProxy.screendao().killEmAll();
    List<Recipe> recipes = this.getRecipes();
    recipes.addAll(this.getRecipes());
    Screen screen1 = this.getScreen("screen 1", recipes);
    screen1.insert();
    Screen screen2 = this.getScreen("screen 1", recipes);
    screen2.insert();

    Duplicate d = new Duplicate();
    d.setRecipes(screen1.getRecipe(0), screen1.getRecipe(1));
    d.setScreens(screen1, screen2);
    assertTrue(d.insert());

    List<Duplicate> ds = DAOProxy.screendao().getDuplicates();
    assertNotNull(ds);
    assertEquals(1, ds.size());
    assertEquals(ds.get(0).getComparatorString(), recipes.get(0).getComparatorString());
  }

  @Test
  public void selectAllScreens() {
    DAOProxy.screendao().killEmAll();

    Screen privateScreen = this.getScreen();
    privateScreen.setFreeForPublic(false);
    privateScreen.insert();

    Screen publicScreen1 = this.getScreen();
    publicScreen1.setFreeForPublic(true);
    publicScreen1.insert();

    Screen publicScreen2 = this.getScreen();
    publicScreen2.setFreeForPublic(true);
    publicScreen2.insert();

    List<Screen> privateScreens = DAOProxy.screendao().getAll(false);
    List<Screen> publicScreens = DAOProxy.screendao().getAll(true);
    List<Screen> allScreens = DAOProxy.screendao().getAll();

    assertEquals(1, privateScreens.size());
    assertEquals(2, publicScreens.size());
    assertEquals(3, allScreens.size());
  }

  private Screen getScreen(String name) {
    return this.getScreen(name, this.getRecipes());
  }
}