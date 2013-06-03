// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * part of a {@link PICKScreensQuery} default implementation
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 06.05.2013
 */
public class PICKScreensSubQueryDefault implements PICKScreensSubQuery {

  private String ingredientName;
  private List<IngredientQuery> ingredientQueries;

  public PICKScreensSubQueryDefault(String search) {
    this.ingredientName = null;
    this.ingredientQueries = new ArrayList<IngredientQuery>();
    search = search.trim();
    int beginOfIndexOfIngredientName = 0;
    if (search.matches("^[=<>].+")) {
      String[] searches = StringUtils.split(search, ' ');
      beginOfIndexOfIngredientName = searches[0].length() + 1;
      this.ingredientQueries.add(new IngredientQueryDefault(searches[0]));
      if (searches.length > 1 && searches[1].matches("^[=<>].+")) {
        beginOfIndexOfIngredientName += searches[1].length() + 1;
        this.ingredientQueries.add(new IngredientQueryDefault(searches[1]));
      }
    }
    if (search.length() > beginOfIndexOfIngredientName) {
      this.ingredientName = search.substring(beginOfIndexOfIngredientName).trim();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getIngredientName() {
    return ingredientName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<IngredientQuery> getIngredientQueries() {
    return this.ingredientQueries;
  }

  @Override
  public boolean queryIngredientName() {
    return this.ingredientName != null && !this.ingredientName.isEmpty();
  }
}
