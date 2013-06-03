package org.itpower.pickscreens.core.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.itpower.lib.util.InequalitySymbol;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;
import org.itpower.pickscreens.core.aspects.PICKScreensSecurity;
import org.itpower.pickscreens.core.model.Duplicate;
import org.itpower.pickscreens.core.model.Ingredient;
import org.itpower.pickscreens.core.model.Recipe;
import org.itpower.pickscreens.core.model.Screen;

public class DAOIBatisScreen implements ScreenDAO {

  @Override
  public Screen getOne(int id) {
    String where = String.format("id = %d", id);
    List<Screen> results = this.getScreensWhere(where);
    return results.size() == 1 ? results.get(0) : null;
  }

  @SuppressWarnings("unchecked")
  private List<Screen> getScreensWhere(String where) {
    List<Screen> results = null;
    try {
      results = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Screen.select.sqlwhere", where);
    } catch (SQLException e) {
      PICKScreensLog.exception(e, 1303191609);
    }
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public boolean insert(Screen screen) {
    boolean result = false;
    try {
      PICKScreensSqlMapClientDaoSupport.sqlMap().insert("Screen.insert", screen);
      for (Recipe recipe : screen.getRecipes()) {
        recipe.setIdScreen(screen.getId());
        PICKScreensSqlMapClientDaoSupport.sqlMap().insert("Recipe.insert", recipe);
        for (Ingredient ingredient : recipe.getIngredients()) {
          ingredient.setIdRecipe(recipe.getId());
          if (ingredient != null) PICKScreensSqlMapClientDaoSupport.sqlMap().insert("Ingredient.insert", ingredient);
        }
      }
      result = true;
    } catch (Exception e) {
      PICKScreensLog.exception(e, 1303191500);
    }
    return result;
  }

  @Override
  public boolean killEmAll() {
    boolean result = false;
    try {
      PICKScreensSqlMapClientDaoSupport.sqlMap().delete("Duplicate.delete");
      PICKScreensSqlMapClientDaoSupport.sqlMap().delete("Ingredient.delete");
      PICKScreensSqlMapClientDaoSupport.sqlMap().delete("Recipe.delete");
      PICKScreensSqlMapClientDaoSupport.sqlMap().delete("Screen.delete");
      result = true;
    } catch (Exception e) {
      PICKScreensLog.exception(e, 1303201201);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Recipe> getRecipesOf(Screen screen) {
    List<Recipe> results = new ArrayList<Recipe>();
    if (screen.getId() != null) {
      String where = String.format("idScreen = %d", screen.getId());
      try {
        results = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Recipe.select.sqlwhere", where);
        if (results == null) results = new ArrayList<Recipe>();
      } catch (SQLException e) {
        PICKScreensLog.exception(e, 1303201317);
      }
    }
    return results;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Ingredient> getIngredientsOf(Recipe recipe) {
    List<Ingredient> results = new ArrayList<Ingredient>();
    if (recipe.getId() != null) {
      String where = String.format("idRecipe = %d", recipe.getId());
      try {
        results = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Ingredient.select.sqlwhere", where);
        if (results == null) results = new ArrayList<Ingredient>();
      } catch (SQLException e) {
        PICKScreensLog.exception(e, 1303201317);
      }
    }
    return results;
  }

  @Override
  public List<Screen> getAll() {
    return this.getScreensWhere("1");
  }

  @Override
  public List<Screen> getAll(boolean freeForPublic) {
    String where = String.format("freeForPublic = %d", freeForPublic ? 1 : 0);
    return this.getScreensWhere(where);
  }

  /**
   * {@inheritDoc} example sql query:
   * 
   * <pre>
   * SELECT * 
   * FROM recipe 
   * WHERE id IN (SELECT idRecipe FROM ingredient WHERE ingredient.name LIKE "%ammon%") 
   * AND idScreen IN (SELECT id FROM screen WHERE freeForPublic = 1)
   * AND idScreen IN (565,987987)
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<Recipe> getRecipes(PICKScreensQuery pq) {

    List<Recipe> results = new ArrayList<Recipe>();
    if ((pq.getSearchInScreens() != null && pq.getSearchInScreens().size() > 0) || pq.getSearchInScreens() == null) {
      String where = "";

      if (pq.getSubQueries() != null && !pq.getSubQueries().isEmpty()) {
        List<String> whereIngredientNames = new ArrayList<String>();
        for (PICKScreensSubQuery subQuery : pq.getSubQueries()) {
          List<String> whereIngredientQueries = new ArrayList<String>();
          for (IngredientQuery ingredientQuery : subQuery.getIngredientQueries()) {
            if (ingredientQuery.searchFor(SearchFor.AMOUNT)) {
              whereIngredientQueries.add(String.format("ROUND(ingredient.amount,5) %s %s", this.getSQL(ingredientQuery.getInequalitySymbol()), ingredientQuery.getAmount()));
            }
            if (ingredientQuery.searchFor(SearchFor.UNIT)) {
              whereIngredientQueries.add(String.format("ingredient.unit LIKE \"%s\"", this.getSQLIngredientUnit(ingredientQuery)));
            }
          }
          if (subQuery.queryIngredientName()) {
            whereIngredientQueries.add(String.format("ingredient.name LIKE \"%s\"", "%" + this.getSQLIngredientName(subQuery) + "%"));
          }
          if (whereIngredientQueries.size() > 0) {
            whereIngredientNames.add(String.format("(SELECT idRecipe FROM ingredient WHERE %s)", StringUtils.join(whereIngredientQueries, " AND ")));
          }
        }
        where = String.format("id IN %s", StringUtils.join(whereIngredientNames, " AND id IN "));
      }
      if (pq.getFreeForPublic() != null) {
        if (where.isEmpty() == false) where += " AND ";
        where += String.format("idScreen IN (SELECT id FROM screen WHERE freeForPublic = %d)", pq.getFreeForPublic() ? 1 : 0);
      }
      if (pq.getSearchInScreens() != null) {
        if (where.isEmpty() == false) where += " AND ";
        String searchIn = StringUtils.join(pq.getSearchInScreens(), ",");
        where += String.format("idScreen IN (%s)", searchIn);
      }
      if (where.isEmpty()) where += "1"; // get all recipes

      try {
        results = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Recipe.select.sqlwhere", where);
        if (results == null) results = new ArrayList<Recipe>();
      } catch (SQLException e) {
        PICKScreensLog.exception(e, 1304151501);
      }
    }
    return results;
  }

  private String getSQL(InequalitySymbol inequalitySymbol) {
    String result = "=";
    switch (inequalitySymbol) {
    case GREATER:
      result = ">";
      break;
    case GREATER_EQUAL:
      result = ">=";
      break;
    case LESS:
      result = "<";
      break;
    case LESS_EQUAL:
      result = "<=";
      break;
    }
    return result;
  }

  private String getSQLIngredientUnit(IngredientQuery ingredientQuery) {
    return PICKScreensSecurity.me().encodeForSQL(ingredientQuery.getUnit());
  }

  private String getSQLIngredientName(PICKScreensSubQuery subQuery) {
    return PICKScreensSecurity.me().encodeForSQL(subQuery.getIngredientName());
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Screen> getScreens(List<Integer> searchInScreens) {
    List<Screen> results = new ArrayList<Screen>(searchInScreens.size());
    try {
      String searchIn = StringUtils.join(searchInScreens, ",");
      String where = String.format("id IN (%s)", searchIn);
      results = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Screen.select.sqlwhere", where);
      if (results == null) results = new ArrayList<Screen>();
    } catch (SQLException e) {
      PICKScreensLog.exception(e, 1304261534);
    }
    return results;
  }

  @Override
  public boolean insert(Duplicate duplicate) {
    boolean result = false;
    try {
      PICKScreensSqlMapClientDaoSupport.sqlMap().insert("Duplicate.insert", duplicate);
      result = true;
    } catch (Exception e) {
      PICKScreensLog.exception(e, 1304301147);
    }
    return result;
  }

  @Override
  public List<Duplicate> getDuplicates() {
    return this.getDuplicatesWhere("1");
  }

  @SuppressWarnings("unchecked")
  private List<Duplicate> getDuplicatesWhere(String where) {
    List<Duplicate> results = new ArrayList<Duplicate>();
    try {
      results = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Duplicate.select.sqlwhere", where);
      if (results == null) results = new ArrayList<Duplicate>();
    } catch (SQLException e) {
      PICKScreensLog.exception(e, 1304301158);
    }
    return results;
  }

  @Override
  public List<Duplicate> getDuplicates(List<Screen> screens) {
    List<Integer> searchInScreens = new ArrayList<Integer>();
    for (Screen screen : screens) {
      searchInScreens.add(screen.getId());
    }
    String searchIn = StringUtils.join(searchInScreens, ",");
    String where = String.format("idScreen1 IN (%s) AND idScreen2 IN (%s)", searchIn, searchIn);
    return this.getDuplicatesWhere(where);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getDuplicateRecipesCount(Screen screen1, Screen screen2) {
    Integer result = null;
    Duplicate parameter = new Duplicate();
    parameter.setIdScreen1(screen1.getId());
    parameter.setIdScreen2(screen2.getId());
    try {
      result = (Integer) PICKScreensSqlMapClientDaoSupport.sqlMap().queryForObject("Duplicate.countRecipesInScreens", parameter);
    } catch (SQLException e) {
      PICKScreensLog.exception(e, 1305020855);
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getAllSubstanceNames() {
    List<String> result = new ArrayList<String>();
    try {
      @SuppressWarnings("unchecked")
      List<Ingredient> ings = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Ingredient.select.groupBy", "name");
      for (Ingredient ing : ings) {
        result.add(ing.getName());
      }
    } catch (SQLException e) {
      PICKScreensLog.exception(e, 1305061710);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Recipe getOneRecipe(int id) {
    String where = String.format("id = %d", id);
    List<Recipe> results = this.getRecipeWhere(where);
    return results.size() == 1 ? results.get(0) : null;
  }

  @SuppressWarnings("unchecked")
  private List<Recipe> getRecipeWhere(String where) {
    List<Recipe> results = null;
    try {
      results = PICKScreensSqlMapClientDaoSupport.sqlMap().queryForList("Recipe.select.sqlwhere", where);
    } catch (SQLException e) {
      PICKScreensLog.exception(e, 1305061729);
    }
    return results;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Duplicate> getDuplicates(Screen... screens) {
    return this.getDuplicates(Arrays.asList(screens));
  }
}
