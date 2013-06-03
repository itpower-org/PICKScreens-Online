// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * the default implementation of {@link PICKScreensQuery}
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 11.04.2013
 */
public class PICKScreensQueryDefault implements PICKScreensQuery {

  private List<PICKScreensSubQueryDefault> subqueries;
  private Boolean freeForPublic;
  private List<Integer> searchInScreens;

  /**
   * construct a default query
   * 
   * 
   * @since 11.04.2013
   * @param freeForPublic @link {@link #getFreeForPublic()}
   * @param searchInput as typed in by the user
   */
  public PICKScreensQueryDefault(Boolean freeForPublic, String searchInput, List<Integer> searchInScreens) {
    if (searchInput == null) {
      this.subqueries = new ArrayList<PICKScreensSubQueryDefault>();
    } else {
      // clean up search
      searchInput = searchInput.replaceAll("\\s+", " ");
      searchInput = searchInput.trim();
      // split
      String[] tmp = StringUtils.split(searchInput, "&");
      for (int i = 0; i < tmp.length; i++)
        tmp[i] = tmp[i].trim();
      List<String> searchSubQueries = Arrays.asList(tmp);
      this.subqueries = new ArrayList<PICKScreensSubQueryDefault>(searchSubQueries.size());
      for (String searchSubQuery : searchSubQueries) {
        this.subqueries.add(new PICKScreensSubQueryDefault(searchSubQuery));
      }
    }
    this.freeForPublic = freeForPublic;
    this.searchInScreens = searchInScreens;
  }

  public PICKScreensQueryDefault(Boolean freeForPublic, List<Integer> searchInScreens) {
    this(freeForPublic, null, searchInScreens);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Integer> getSearchInScreens() {
    return searchInScreens;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PICKScreensSubQueryDefault> getSubQueries() {
    return this.subqueries;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean getFreeForPublic() {
    return freeForPublic;
  }

}
