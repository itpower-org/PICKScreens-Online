// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;

import java.util.List;

import org.itpower.pickscreens.core.model.Screen;

/**
 * a helper for database queries like ">3.5 milk, <=8 cheese" where everything between the commas are {@link PICKScreensSubQueryDefault}s
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 11.04.2013
 */
public interface PICKScreensQuery {
  /**
   * return all subqueries between the commas
   * 
   * 
   * @since 11.04.2013
   * @return a search for the ingredient name
   */
  public List<PICKScreensSubQueryDefault> getSubQueries();

  /**
   * return which restriction of private / public screens to use.
   * <code>null</code> means all screens. <code>true</code> means public screens
   * only and <code>false</code> means private screens only
   * 
   * 
   * @since 11.04.2013
   * @return true, if screens private screens are included in query
   */
  public Boolean getFreeForPublic();

  /**
   * return the ids of the {@link Screen}s to search in. return null if
   * searching in all {@link Screen}s.
   * 
   * 
   * @since 16.04.2013
   * @return ids of screens to search in or null for all {@link Screen}s
   */
  List<Integer> getSearchInScreens();
}
