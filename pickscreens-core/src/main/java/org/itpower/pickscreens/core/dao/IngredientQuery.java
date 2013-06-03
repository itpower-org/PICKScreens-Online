// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;

import org.itpower.lib.util.InequalitySymbol;

/**
 * part of a {@link PICKScreensQuery}
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 06.05.2013
 */
public interface IngredientQuery {
  public Float getAmount();

  public InequalitySymbol getInequalitySymbol();

  public boolean searchFor(SearchFor searchFor);

  public String getUnit();
}
