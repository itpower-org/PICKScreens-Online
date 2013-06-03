// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.cron.control;

import org.itpower.pickscreens.core.model.Recipe;

/**
 * produce {@link Recipe} objects
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 */
interface RecipeFactory {
  /**
   * return a {@link Recipe}
   * 
   * 
   * @since 05.03.2013
   * @return a {@link Recipe}
   */
  Recipe getRecipe();
}
