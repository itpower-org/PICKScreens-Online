// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.dao;


/**
 * dao for db transactions concerning customers.
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 1.00-SNAPSHOT (03/01/2013)
 */
public interface DAO<T> {

  /**
   * insert a t object into the database and return true on success. if t does
   * already exist, return false.
   * 
   * @param bean to insert
   * @return true, on success and false on errors or if bean exists.
   */
  boolean insert(T bean);

  /**
   * return the t with the given id or null if no t exists.
   * 
   * 
   * @since 19.03.2013
   * @param id
   * @return
   */
  T getOne(int id);

  /**
   * delete the entire database and return true on success
   * 
   * 
   * @since 20.03.2013
   * @return true if killing entire database succeeded
   */
  boolean killEmAll();

}
