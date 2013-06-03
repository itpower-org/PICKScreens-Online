// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.itpower.lib.util.InequalitySymbol;
import org.itpower.pickscreens.core.dao.IngredientQuery;
import org.itpower.pickscreens.core.dao.PICKScreensQuery;
import org.itpower.pickscreens.core.dao.PICKScreensQueryDefault;
import org.itpower.pickscreens.core.dao.PICKScreensSubQuery;
import org.itpower.pickscreens.core.dao.PICKScreensSubQueryDefault;
import org.junit.Test;

/**
 * test the query helper classes
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 */
public class QueryTest {

  @Test
  public void testGet2IngredientNamesA() {
    PICKScreensQuery query = new PICKScreensQueryDefault(null, "a&b", new ArrayList<Integer>());
    List<PICKScreensSubQueryDefault> queries = query.getSubQueries();
    assertEquals(2, queries.size());
    assertEquals("a", queries.get(0).getIngredientName());
    assertEquals("b", queries.get(1).getIngredientName());
  }

  @Test
  public void testGet2IngredientNamesB() {
    PICKScreensQuery query = new PICKScreensQueryDefault(null, "apfel mus&b", new ArrayList<Integer>());
    List<PICKScreensSubQueryDefault> queries = query.getSubQueries();
    assertEquals(2, queries.size());
    assertEquals("apfel mus", queries.get(0).getIngredientName());
    assertEquals("b", queries.get(1).getIngredientName());
  }

  @Test
  public void testGet1IngredientName() {
    PICKScreensQuery query = new PICKScreensQueryDefault(null, "b", new ArrayList<Integer>());
    List<PICKScreensSubQueryDefault> queries = query.getSubQueries();
    assertEquals(1, queries.size());
    assertEquals("b", queries.get(0).getIngredientName());
  }

  @Test
  public void testGetIngredientNameNoSearch() {
    PICKScreensQuery query = new PICKScreensQueryDefault(null, new ArrayList<Integer>());
    List<PICKScreensSubQueryDefault> queries = query.getSubQueries();
    assertEquals(0, queries.size());
  }

  @Test
  public void testSearchAmount() {
    String search = "<3 anthrax milk& >=5.2 cokes& orange juice";
    PICKScreensQuery query = new PICKScreensQueryDefault(null, search, new ArrayList<Integer>());
    List<PICKScreensSubQueryDefault> queries = query.getSubQueries();

    PICKScreensSubQuery sq = queries.get(0);
    IngredientQuery iq = sq.getIngredientQueries().get(0);
    assertEquals("anthrax milk", sq.getIngredientName());
    assertEquals(new Float(3.f), iq.getAmount());
    assertEquals(InequalitySymbol.LESS, iq.getInequalitySymbol());

    sq = queries.get(1);
    iq = sq.getIngredientQueries().get(0);
    assertEquals("cokes", sq.getIngredientName());
    assertEquals(new Float(5.2), iq.getAmount());
    assertEquals(InequalitySymbol.GREATER_EQUAL, iq.getInequalitySymbol());

    sq = queries.get(2);
    assertEquals(0, sq.getIngredientQueries().size());
    assertEquals("orange juice", sq.getIngredientName());
  }

  @Test
  public void testAmountOnly() {
    PICKScreensSubQuery sq = new PICKScreensSubQueryDefault("<=4");
    IngredientQuery iq = sq.getIngredientQueries().get(0);
    assertEquals(null, sq.getIngredientName());
    assertEquals(new Float(4), iq.getAmount());
    assertEquals(InequalitySymbol.LESS_EQUAL, iq.getInequalitySymbol());
  }
}