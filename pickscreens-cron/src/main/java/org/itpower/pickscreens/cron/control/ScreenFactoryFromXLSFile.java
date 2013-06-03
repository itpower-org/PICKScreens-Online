// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.cron.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;

/**
 * convert an excel file (< Excel 2007) into a screen.
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 * 
 */
public class ScreenFactoryFromXLSFile implements ScreenFactory {

  private HSSFSheet sheet;
  private boolean validFile = true;
  private String filename;

  /** construct XSLFile2Screen */
  public ScreenFactoryFromXLSFile(File file) {
    HSSFWorkbook workbook = this.getWorkbook(file);
    this.validFile = workbook != null;
    if (this.validFile) {
      this.sheet = workbook.getSheetAt(0);
      this.filename = file.getName();
    }
  }

  /**
   * return the screen represented by the file.
   * 
   * @param file excel sheet (< Excel 2007) representing the screen.
   * @return the screen represented by the file.
   */
  @Override
  public Screen getScreen(boolean freeForPublic) {
    Screen result = null;
    if (this.validFile) {
      result = new Screen();
      result.setFreeForPublic(freeForPublic);
      result.setName(this.getScreenName());
      result.setRecipes(this.getRecipes());
      result.setFilename(this.filename);
    }
    return result;
  }

  /**
   * return recipes represented by the sheet. first row of the sheet concerning the attributes of the recipe. next row means one recipe for one row.
   * 
   * 
   * @since 05.03.2013
   * @see RecipeFactoryFromRow
   * @param sheet
   * @return recipes represented by the sheet.
   */
  private List<Recipe> getRecipes() {
    List<Recipe> result = new ArrayList<Recipe>();
    Iterator<Row> rowIterator = sheet.iterator();

    // create headlines
    List<String> headlines = new ArrayList<String>();
    if (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      Iterator<Cell> cellIterator = row.cellIterator();
      while (cellIterator.hasNext()) {
        Cell cell = cellIterator.next();
        String headline = cell.getStringCellValue().trim().toLowerCase().replaceAll(" ", "");
        headlines.add(headline);
      }
    }

    // create recipes
    while (rowIterator.hasNext()) {
      RecipeFactory factory = new RecipeFactoryFromRow(rowIterator.next(), headlines);
      Recipe recipe = factory.getRecipe();
      if (recipe != null) result.add(recipe);
    }
    return result;
  }

  private String getScreenName() {
    return this.sheet.getSheetName();
  }

  private HSSFWorkbook getWorkbook(File file) {
    HSSFWorkbook result = null;
    if (file.canRead()) {
      try {
        result = new HSSFWorkbook(new FileInputStream(file));
      } catch (IOException e) {
        PICKScreensLog.exception(e, 1303051357);
      }
    } else {
      PICKScreensLog.error("cannot read file " + file.getAbsolutePath(), 1303051419);
    }
    return result;
  }

}
