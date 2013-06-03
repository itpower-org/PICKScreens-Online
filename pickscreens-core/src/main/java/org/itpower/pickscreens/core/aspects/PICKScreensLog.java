// © 2013 by IT-Power GmbH (http://www.it-power.org)
package org.itpower.pickscreens.core.aspects;

import org.apache.log4j.Logger;

/**
 * central point to log information.
 * 
 * @author Daniel Oltmanns <daniel.oltmanns@it-power.org>
 * @since 1.00-SNAPSHOT (03/01/2013)
 */
public class PICKScreensLog {

	/**
	 * log an info
	 * 
	 * @param clazz
	 *            as in {@link Logger#getLogger(java.lang.Class)}
	 * @param message
	 *            as in {@link Logger#info(java.lang.Object)}
	 * @param id
	 *            set as identifier for the message
	 */
	public static final void logInfo(Class<?> clazz, Object message, int id) {
		Logger.getRootLogger().info(getMessage(clazz, message, id));
	}

	/**
	 * log an exception
	 * 
	 * @param clazz
	 *            as in {@link Logger#getLogger(java.lang.Class)}
	 * @param exception
	 *            to log
	 * @param message
	 *            as in {@link Logger#info(java.lang.Object)}
	 * @param id
	 *            set as identifier for the message
	 */
	public static final void logException(Class<?> clazz, Exception exception, Object message, int id) {
		Logger.getRootLogger().fatal(getMessage(clazz, message, id, exception));
	}

	private static final String getMessage(Class<?> clazz, Object message, int id, Exception exception) {
		return getMessage(clazz, message + " / " + exception.toString(), id);
	}
	private static final String getMessage(Object message, int id, Exception exception) {
		return getMessage(message + " / " + exception.toString(), id);
	}

	private static final String getMessage(Class<?> clazz, Object message, int id) {
		return String.format("[%s] %s @%s", id, message, clazz);
	}
	private static final String getMessage(Object message, int id) {
		return String.format("%s @%s", id, message);
	}

	private PICKScreensLog() {
	}

	public static void error(Class<?> clazz, String message, int id) {
		Logger.getRootLogger().error(getMessage(clazz, message, id));
	}

	public static void exception(Exception exception, int id) {
		exception("no message", exception, id);
	}

	public static void exception(String message, Exception e, int id) {
		Logger.getRootLogger().fatal(getMessage(message, id, e));
	}

	public static void info(String message, int id) {
		Logger.getRootLogger().info(getMessage(message, id));
	}

	public static void error(String message, int id) {
		Logger.getRootLogger().error(getMessage(message, id));
	}

	public static void debug(String message, int id) {
		Logger.getRootLogger().debug(getMessage(message, id));
	}

	public static void warn(String message, int id) {
		Logger.getRootLogger().warn(getMessage(message, id));
	}

}
