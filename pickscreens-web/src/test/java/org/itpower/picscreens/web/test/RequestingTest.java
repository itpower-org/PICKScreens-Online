/*
 * © 2013-2013 by IT-Power GmbH (http://www.it-power.org)
 */

/**
 * J-Unit Testframe
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 1.00-SNAPSHOT (03/01/2013)
 */
package org.itpower.picscreens.web.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.itpower.pickscreens.core.aspects.PICKScreensConfig;
import org.itpower.pickscreens.core.control.DuplicatesFactory;
import org.itpower.pickscreens.core.dao.DAOProxy;
import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.IngredientType;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;
import org.itpower.pickscreens.web.controller.PICKScreensController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * test the web application. assert all tests of pickscreens-core module has been executed before!
 * 
 * 
 * @since 26.04.2013
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml" })
public class RequestingTest {
  private HandlerAdapter handlerAdapter;
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private PICKScreensController controller;
  private Screen screen;
  private File screenFile;

  @Before
  public void setUp() {
    handlerAdapter = applicationContext.getBean(AnnotationMethodHandlerAdapter.class);

    // prepare database
    Ingredient buffer = new Ingredient();
    buffer.setName("Milk");
    buffer.setUnit("l");
    buffer.setAmount(1.1f);
    buffer.setType(IngredientType.BUFFER);
    List<Ingredient> ingredients = new ArrayList<Ingredient>();
    ingredients.add(buffer);

    // prepare database
    Ingredient salt1 = new Ingredient();
    salt1.setName("Cacao");
    salt1.setUnit("ml");
    salt1.setAmount(0.3f);
    salt1.setType(IngredientType.SALT1);
    ingredients.add(salt1);

    List<Recipe> recipes1 = new ArrayList<Recipe>();
    Recipe r1 = new Recipe();
    r1.setNumber(1);
    r1.setIngredients(ingredients);
    recipes1.add(r1);

    List<Recipe> recipes2 = new ArrayList<Recipe>();
    Recipe r2 = new Recipe();
    r2.setNumber(1);
    r2.setIngredients(ingredients);
    recipes2.add(r2);

    this.screen = new Screen();
    Screen screen2 = new Screen();
    this.screen.setName("screen 1");
    this.screen.setFilename("screen 1.xls");
    this.screen.setFreeForPublic(true);
    this.screen.setRecipes(recipes1);
    screen2.setName("screen 2");
    screen2.setFilename("screen 2.xls");
    screen2.setRecipes(recipes2);
    List<Screen> screens = new ArrayList<Screen>();
    screens.add(this.screen);
    screens.add(screen2);
    DuplicatesFactory f = new DuplicatesFactory(screens);

    DAOProxy.screendao().killEmAll();
    for (Screen screen : screens)
      screen.insert();
    List<Duplicate> ds = f.getDuplicates();
    for (Duplicate d : ds)
      d.insert();

    // set id screens
    idScreens = new ArrayList<Integer>();
    screensThere = DAOProxy.screendao().getAll();
    for (Screen screen : screensThere) {
      idScreens.add(screen.getId());
    }

  }

  private List<Screen> screensThere;
  private List<Integer> idScreens;

  @Test
  public void selfTest() {
    assertEquals(this.screen.getRecipe(0).getIdScreen(), this.screen.getId());
  }

  @Test
  public void getAnswer() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/");
    request.setMethod("GET");

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }
  }

  @Test
  public void requestSearchResultGetNoRecipesBecauseOfMissingFilter() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/search.json");
    request.setMethod("GET");
    request.addParameter("query", "Milk");

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());

      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.getBoolean("succ"));
      assertTrue(answer.has("hits"));
      assertTrue(answer.has("rows"));
      JSONArray screens = answer.getJSONArray("rows");
      assertEquals(0, screens.length());
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }
  }

  @Test
  public void requestSearchResultGetRecipesWithMilkFromNonsenseRecipeSelection() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/search.json");
    request.setMethod("GET");
    request.addParameter("query", "Milk");
    request.addParameter("screens", "[\"321231\",\"987987\"]"); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());

      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.getBoolean("succ"));
      assertTrue(answer.has("hits"));
      assertTrue(answer.has("rows"));
      JSONArray screens = answer.getJSONArray("rows");
      assertEquals(0, screens.length());
      assertEquals(0, answer.getInt("hits"));
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }
  }

  @Test
  public void requestCrossTable() {

    // prepare server request
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/crosstable.json");
    request.setMethod("GET");
    JSONArray screensAsJSON = new JSONArray();
    for (Integer idScreen : idScreens) {
      screensAsJSON.put(idScreen);
    }
    request.addParameter("screens", screensAsJSON.toString()); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());

      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.has("succ"));
      assertTrue(answer.has("rows"));
      assertTrue(answer.getBoolean("succ"));
      JSONObject rows = answer.getJSONObject("rows");
      assertTrue(rows.has("num"));
      assertTrue(rows.has("percentage"));
      JSONObject num = rows.getJSONObject("num");
      assertTrue(num.has("values"));
      JSONArray values = num.getJSONArray("values");
      assertEquals(screensThere.size(), values.length());
      JSONObject row0 = values.getJSONObject(0);
      assertTrue(row0.has("0"));
      assertTrue(row0.has("1"));
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }
  }

  @Test
  public void requestDownload() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    String dir = PICKScreensConfig.globalProperty("dir_xls_public");
    this.screenFile = new File(dir + this.screen.getFilename());
    if (this.screenFile.exists()) {
      assertTrue(this.screenFile.delete());
    }
    try {
      assertTrue(this.screenFile.createNewFile());
    } catch (IOException e) {
      fail("should not have thrown " + e);
    }

    request.setRequestURI("/download.xls");
    request.setMethod("GET");
    request.addParameter("id", idScreens.get(0) + ""); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertTrue(this.screenFile.delete());
      assertEquals(200, response.getStatus());
      assertEquals("application/octet-stream", response.getContentType());
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }
  }

  @Test
  public void requestSearchResultGetRecipesWithMilkFromRightRecipeSelection() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/search.json");
    request.setMethod("GET");
    request.addParameter("query", "Lithium");
    request.addParameter("screens", "[\"" + idScreens.get(0) + "\",\"987987\"]"); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());

      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.getBoolean("succ"));
      assertTrue(answer.has("hits"));
      assertTrue(answer.has("rows"));
      JSONArray screens = answer.getJSONArray("rows");
      assertEquals(1, screens.length());
      assertEquals(1, answer.getInt("hits"));
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }
  }

  @Test
  public void requestManyIngredients() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/search.json");
    request.setMethod("GET");
    request.addParameter("query", "Lithium & acao");
    request.addParameter("screens", "[\"" + idScreens.get(0) + "\"]"); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());

      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.getBoolean("succ"));
      assertTrue(answer.has("hits"));
      assertTrue(answer.has("rows"));
      JSONArray screens = answer.getJSONArray("rows");
      assertEquals(1, screens.length());
      assertEquals(1, answer.getInt("hits"));
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }
  }

  @Test
  public void showDetailsScreens() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/details-screens.json");
    request.setMethod("GET");
    request.addParameter("screens", "[\"" + idScreens.get(0) + "\",\"987987\"]"); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());
      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.getBoolean("succ"));
      JSONArray rows = answer.getJSONArray("rows");
      int hits = answer.getInt("hits");
      assertEquals(screensThere.get(0).getRecipes().size(), rows.length());
      assertEquals(rows.length(), hits);
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }

  }

  @Test
  public void showDetailsDuplicates_onlyOneSelected() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/details-duplicates.json");
    request.setMethod("GET");
    request.addParameter("screens", "[\"" + idScreens.get(0) + "\",\"987987\"]"); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());
      JSONObject answer = new JSONObject(response.getContentAsString());
      assertFalse(answer.getBoolean("succ"));
      assertTrue(answer.has("rows"));
      assertEquals(0, answer.getJSONArray("rows").length());
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }

  }

  @Test
  public void showDetailsDuplicates_2Selected() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/details-duplicates.json");
    request.setMethod("GET");
    request.addParameter("screens", "[\"" + StringUtils.join(idScreens, "\",\"") + "\",\"987987\"]"); // must not exist

    // request it and check response
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());
      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.getBoolean("succ"));
      assertTrue(answer.has("rows"));
      assertEquals(1, answer.getJSONArray("rows").length());
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }

  }

  @Test
  public void showSubstances() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setRequestURI("/substances.json");
    request.setMethod("GET");
    try {
      handlerAdapter.handle(request, response, controller);
      assertEquals(200, response.getStatus());
      JSONObject answer = new JSONObject(response.getContentAsString());
      assertTrue(answer.getBoolean("succ"));
      JSONArray rows = answer.getJSONArray("rows");
      assertEquals(2, rows.length());
      JSONObject milk = rows.getJSONObject(1);
      assertEquals("Lithium sulfate", milk.getString("substance_name"));
      assertEquals("Sugar, Milk, Corn, Salt", milk.getString("synonyms"));
    } catch (Exception e) {
      fail("should not have thrown exception " + e);
    }

  }
}