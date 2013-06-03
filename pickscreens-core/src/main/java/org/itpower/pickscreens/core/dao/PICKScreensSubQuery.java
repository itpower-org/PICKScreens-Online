// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;

import java.util.List;

/**
 * part of a {@link PICKScreensQuery}
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 06.05.2013
 */
public interface PICKScreensSubQuery {
  public String getIngredientName();

  public List<IngredientQuery> getIngredientQueries();

  public boolean queryIngredientName();

}
