// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.cron.control;

import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.itpower.lib.poi.CellValueUtil;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.IngredientType;
import org.itpower.pickscreens.core.model.Recipe;

/**
 * create a recipe from something else
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 */
class RecipeFactoryFromRow implements RecipeFactory {

  private List<String> headlines;
  private Row row;

  /**
   * construct the factory
   * 
   * 
   * @since 05.03.2013
   * @param row representing the recipe
   * @param headlines as given in the first row of the sheet and signals the attribute of the recipe (salt, buffer etc.)
   */
  public RecipeFactoryFromRow(Row row, List<String> headlines) {
    this.row = row;
    this.headlines = headlines;
  }

  /**
   * return a {@link Recipe} from a row representing the recipe. just try it, do not validate the result.
   * 
   * 
   * @since 05.03.2013
   * @return a {@link Recipe} represented by the given row
   */
  @Override
  public Recipe getRecipe() {
    Recipe result = new Recipe();
    Iterator<Cell> cellIterator = row.cellIterator();
    while (cellIterator.hasNext()) {
      Cell cell = cellIterator.next();
      String type = headlines.get(cell.getColumnIndex());
      if (type.equals("number")) result.setNumber(this.getNumber(cell));
      else result.addIngredient(this.getIngredient(cell, type));
    }
    return result;
  }

  private Ingredient getIngredient(Cell cell, String type) {
    Ingredient result = null;
    String val = CellValueUtil.getStringCellValue(cell);
    if (val != null) {
      val = val.trim();
      if (val.isEmpty() == false && val.split(" ").length > 2) {
        result = new Ingredient();
        try {
          String[] subvals = val.split(" ", 3);
          Float amount = Float.parseFloat(subvals[0].replace(',', '.').replaceAll("[^0-9.]", ""));
          result.setAmount(amount);
          result.setUnit(subvals[1]);
          result.setName(subvals[2]);
          result.setType(this.getIngredientType(type));
        } catch (NumberFormatException e) {
          PICKScreensLog.error("could not create ingredient from " + val, 1303190958);
          result = null;
        }
      }
    }
    return result;
  }

  private IngredientType getIngredientType(String type) {
    IngredientType result = IngredientType.BUFFER;
    if (type.equals("salt1")) result = IngredientType.SALT1;
    if (type.equals("salt2")) result = IngredientType.SALT2;
    if (type.equals("precipitant1")) result = IngredientType.PRECIPITANT1;
    if (type.equals("precipitant2")) result = IngredientType.PRECIPITANT2;
    return result;
  }

  private Integer getNumber(Cell cell) {
    Integer result = null;
    try {
      result = CellValueUtil.getIntegerCellValue(cell);
    } catch (NumberFormatException e) {
      PICKScreensLog.exception(e, 1303051547);
    }
    return result;
  }

}
