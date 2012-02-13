package com.xlab.glodocs.api.utils;

/**
 * Constants used in Glodocs system
 * 
 * @author Lapitskiy Anton
 * 
 */
public class Constants {
	/**
	 * Subscript with collection list
	 */
	public static final String GLOBAL_COLLECTIONLIST = "COLLECTIONLIST";

	/**
	 * Prefix of collection's global
	 */
	public static final String GLOBAL_COLLECTIONS = "COLLECTIONS";

	/**
	 * Name of user global
	 */
	public static final String GLOBAL_USERS = "%SYS.USERS";

	/**
	 * Name of system global
	 */
	public static final String GLOBAL_SYSTEM = "%SYS.META";

	/**
	 * Date format in Glodocs
	 */
	public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

	/**
	 * Collection's subscript with list of indexes
	 */
	public static final String GLOBALS_COLLECTION_INDEX = "INDEX";

	/**
	 * Simple Index
	 */
	public static final short INDEX_TYPE_INDEX = 0;

	/**
	 * Unique field
	 */
	public static final short INDEX_TYPE_UNIQUE = 1;

	/**
	 * Document's subscript with list of indexed_fields
	 */
	public static final String GLOBAL_DOCUMENT_INDEXES = "INDEXED_FIELDS";

	/**
	 * User's subscript with list of system roles
	 */
	public static final String USER_ROLES = "USER.ROLE";

	/**
	 * User's subscript with list of special user permissions for documents
	 */
	public static final String USER_OBJECT_PRIV_DOC = "USER.PRIV.DOC";

	/**
	 * User's subscript with list of special user permissions for documents
	 */
	public static final String USER_OBJECT_PRIV_COL = "USER.PRI.COL";

	/**
	 * Name of journal global
	 */
	public static final String GLOBAL_JOURNAL = "%SYS.JOURNAL";
}
