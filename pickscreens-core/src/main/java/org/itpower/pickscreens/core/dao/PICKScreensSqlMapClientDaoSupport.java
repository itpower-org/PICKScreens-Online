// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;

/**
 * central dataholder for the {@link PICKScreensSqlMapClientDaoSupport}
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 20.03.2013
 */
public class PICKScreensSqlMapClientDaoSupport {
  private SqlMapClientImpl sqlMap;
  /** one and only instance of me */
  private volatile static PICKScreensSqlMapClientDaoSupport me;

  /**
   * return the sql map. this is the central point for getting access to the
   * database via ibatis.
   * 
   * @return the sql map.
   */
  public static SqlMapClientImpl sqlMap() {
    return getInstance().sqlMap;
  }

  /** construct me */
  private PICKScreensSqlMapClientDaoSupport() {
    ApplicationContext context = new ClassPathXmlApplicationContext("ibatis.xml");
    this.sqlMap = (SqlMapClientImpl) context.getBean("sqlMap");
  }

  /**
   * return the one and only instance of DasSqlMapClientDaoSupport
   * 
   * @return the one and only instance of DasSqlMapClientDaoSupport
   */
  public static PICKScreensSqlMapClientDaoSupport getInstance() {
    if (me == null) { // no instance so far
      synchronized (PICKScreensSqlMapClientDaoSupport.class) {
        if (me == null) { // still no instance so far
          me = new PICKScreensSqlMapClientDaoSupport(); // the one and only
        }
      }
    }
    return me;
  }
}
