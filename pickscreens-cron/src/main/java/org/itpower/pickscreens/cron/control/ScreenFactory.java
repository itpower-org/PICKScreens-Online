// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.cron.control;

import org.itpower.pickscreens.core.model.Screen;

/**
 * produce {@link Screen} objects
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 05.03.2013
 */
public interface ScreenFactory {
  /**
   * return a {@link Screen}
   * 
   * 
   * @param freeForPublic decide, if the screen is free for the public
   * @since 05.03.2013
   * @return a {@link Screen}
   */
  public Screen getScreen(boolean freeForPublic);
}
