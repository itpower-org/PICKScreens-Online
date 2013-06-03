// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;

import org.itpower.lib.util.InequalitySymbol;
import org.itpower.pickscreens.core.aspects.PICKScreensLog;

/**
 * part of a {@link PICKScreensQuery} default implementation
 * 
 * @author "Daniel Oltmanns <daniel.oltmanns@it-power.org>"
 * @since 06.05.2013
 */
public class IngredientQueryDefault implements IngredientQuery {

  private Float amount = null;
  private String unit = null;
  private InequalitySymbol inequalitySymbol = InequalitySymbol.EQUAL;

  public IngredientQueryDefault(String search) {
    search = search.trim();
    if (search.matches("^[=<>].+")) {
      int inequalitySymbolLength = 1;
      if (search.startsWith(">")) {
        if (search.startsWith(">=")) {
          this.inequalitySymbol = InequalitySymbol.GREATER_EQUAL;
          inequalitySymbolLength++;
        } else {
          this.inequalitySymbol = InequalitySymbol.GREATER;
        }
      } else if (search.startsWith("<")) {
        if (search.startsWith("<=")) {
          this.inequalitySymbol = InequalitySymbol.LESS_EQUAL;
          inequalitySymbolLength++;
        } else {
          this.inequalitySymbol = InequalitySymbol.LESS;
        }
      } else {
        this.inequalitySymbol = InequalitySymbol.EQUAL;
      }
      String tmp = search.substring(inequalitySymbolLength);
      String tmpAmount = tmp.replaceAll("[^0-9,.]", "");
      tmpAmount = tmpAmount.replace(',', '.');
      if (tmpAmount.length() > 0) {
        try {
          this.amount = Float.parseFloat(tmpAmount.trim());
          if (tmp.length() > tmpAmount.length()) {
            this.unit = tmp.substring(tmpAmount.length());
          }
        } catch (NumberFormatException e) {
          PICKScreensLog.info("could not handle search: " + search, 1305061133);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Float getAmount() {
    return amount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InequalitySymbol getInequalitySymbol() {
    return inequalitySymbol;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUnit() {
    return this.unit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean searchFor(SearchFor searchFor) {
    if (searchFor == SearchFor.AMOUNT && this.getAmount() != null) return true;
    else if (searchFor == SearchFor.UNIT && this.getUnit() != null && this.getUnit().isEmpty() == false) return true;
    else return false;
  }

}
