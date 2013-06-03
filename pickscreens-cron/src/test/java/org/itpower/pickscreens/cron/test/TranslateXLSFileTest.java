/*
 * © 2013 by IT-Power GmbH (http://www.it-power.org)
 */
package org.itpower.pickscreens.cron.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.itpower.pickscreens.core.control.PICKScreensSynonyms;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;
import org.itpower.pickscreens.cron.control.ScreenFactory;
import org.itpower.pickscreens.cron.control.ScreenFactoryFromXLSFile;
import org.junit.Before;
import org.junit.Test;

/**
 * test the tests and the environment
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 */
public class TranslateXLSFileTest {

  private Screen screen;

  @Before
  public void setUp() {
    String testfilename = "Emerald_Cryo_I.xls";
    this.screen = this.getScreenOfFile(testfilename);
  }

  private Screen getScreenOfFile(String filename) {
    File testfile = SimpleTestBeanFactory.getTestXLSFile(filename);
    ScreenFactory sf = new ScreenFactoryFromXLSFile(testfile);
    return sf.getScreen(false);
  }

  @Test
  public void screenHasRightName() {
    assertNotNull(screen);
    assertEquals("Emerald Cryo I", screen.getName());
  }

  @Test
  public void avoidNestedSQLExceptionOccuredInMDL_MemSys() {
    Screen screen = this.getScreenOfFile("MDL_MemSys.xls");
    assertNotNull(screen);
    assertEquals(48, screen.getRecipes().size());
    for (Recipe recipe : screen.getRecipes()) {
      assertNotNull(recipe.getNumber());
    }
  }

  @Test
  public void screenHasRecipesWithRightNumbers() {
    assertEquals(48, screen.getRecipes().size());
    Integer number = 1;
    for (Recipe r : screen.getRecipes()) {
      assertEquals(number, r.getNumber());
      number++;
    }
  }

  @Test
  public void recipeHasIngredients() {
    // test attributes
    Recipe r = screen.getRecipe(0);
    assertNotNull(r.getBuffer());
    assertNotNull(r.getPrecipitant1());
    assertNull(r.getPrecipitant2());
    assertNull(r.getSalt1());
    assertNull(r.getSalt2());
  }

  @Test
  public void ingredientHasAttributes() {
    // test attributes
    Recipe r = screen.getRecipe(0);
    assertEquals(1, r.getNumber().intValue());

    Ingredient buffer = r.getBuffer();
    assertNotNull(buffer.getName());
    assertNotNull(buffer.getUnit());
    assertNotNull(buffer.getAmount());
    assertEquals("phosphate citrate pH 4,2", buffer.getName());
    assertEquals("M", buffer.getUnit());
    assertEquals("0.1", buffer.getAmount() + "");

    Ingredient precipitant1 = r.getPrecipitant1();
    assertNotNull(precipitant1.getName());
    assertNotNull(precipitant1.getUnit());
    assertNotNull(precipitant1.getAmount());
    assertEquals("MPD", precipitant1.getName());
    assertEquals("%(v/v)", precipitant1.getUnit());
    assertEquals("40.0", precipitant1.getAmount() + "");

    r = screen.getRecipe(35);
    assertEquals(36, r.getNumber().intValue());

    Ingredient salt1 = r.getSalt1();
    assertNotNull(salt1.getName());
    assertNotNull(salt1.getUnit());
    assertNotNull(salt1.getAmount());
    assertEquals("lithium sulfate", salt1.getName());
    assertEquals("M", salt1.getUnit());
    assertEquals("0.05", salt1.getAmount() + "");
  }

  @Test
  public void testNullPointerExceptionGot130321() {
    try {
      Screen screen = this.getScreenOfFile("Qiagen_Nucleix_Suite.xls");
      assertTrue(screen.getRecipes().size() == 96);
    } catch (Exception e) {
      fail("should not throw " + e);
    }
  }

  @Test
  public void useSynonyms() {
    PICKScreensSynonyms s = PICKScreensSynonyms.me();
    URL url = TranslateXLSFileTest.class.getResource("/synonyms.csv");
    s.setFile(new File(url.getFile()));
    try {
      Screen screen = this.getScreenOfFile("Qiagen_Nucleix_Suite.xls");
      assertEquals("Milk", screen.getRecipe(0).getSalt2().getName());
    } catch (Exception e) {
      fail("should not throw " + e);
    }
  }

  /**
   * AVOID
   * 
   * Insert public screens of file: MDL_MemSys.xls Exception in thread "main"
   * java.lang.IllegalStateException: Cannot get a text value from a numeric cell at
   * org.apache.poi.hssf.usermodel.HSSFCell.typeMismatch(HSSFCell.java:643) at
   * org.apache.poi.hssf.usermodel.HSSFCell.getRichStringCellValue(HSSFCell.java:720) at
   * org.apache.poi.hssf.usermodel.HSSFCell.getStringCellValue(HSSFCell.java:703) at
   * org.itpower.pickscreens.cron.control.RecipeFactoryFromRow.getNumber(RecipeFactoryFromRow.java:101) at
   * org.itpower.pickscreens.cron.control.RecipeFactoryFromRow.getRecipe(RecipeFactoryFromRow.java:54) at
   * org.itpower.pickscreens.cron.control.ScreenFactoryFromXSLFile.getRecipes(ScreenFactoryFromXSLFile.java:87) at
   * org.itpower.pickscreens.cron.control.ScreenFactoryFromXSLFile.getScreen(ScreenFactoryFromXSLFile.java:53) at
   * org.itpower.pickscreens.cron.control.Main.main(Main.java:39)
   * 
   * 
   * 
   * @since 21.03.2013
   */
  @Test
  public void testNumericExceptionGot130321() {
    try {
      Screen screen = this.getScreenOfFile("MDL_MemSys.xls");
      assertTrue(screen.getRecipes().size() == 48);
    } catch (Exception e) {
      fail("should not throw " + e);
    }
  }

}