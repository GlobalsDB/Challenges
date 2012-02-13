package com.xlab.glodocs.api.security;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Document;
import com.xlab.glodocs.api.utils.GlodocsException;

/**
 * Wrapper for user actions
 * 
 * @author Lapitskiy Anton
 * 
 */
public class AccessManager {

	/**
	 * 
	 * @param user
	 *            Current user
	 * @param role
	 *            System role to check
	 * @param collection
	 *            Collection to check permission
	 * @return true - if this action is permitted, false - otherwise
	 */
	public static boolean checkPermissions(User user, Role role,
			Collection collection) {
		switch (role) {
		// Creating document in current collection
		case CREATE_DOCUMENT:
			return checkPermissions(user, collection, ObjectAction.WRITE)
					&& checkPermissions(user, role);
		case BROWSE_DOCUMENT:
			return checkPermissions(user, collection, ObjectAction.READ)
					&& checkPermissions(user, role);
		case EDIT_DOCUMENT:
			return checkPermissions(user, collection, ObjectAction.WRITE)
					&& checkPermissions(user, role);
		case DROP_DOCUMENT:
			return checkPermissions(user, collection, ObjectAction.FULL)
					&& checkPermissions(user, role);
		case BROWSE_COLLECTION:
			return checkPermissions(user, collection, ObjectAction.READ)
					&& checkPermissions(user, role);
		case EDIT_COLLECTION:
			return checkPermissions(user, collection, ObjectAction.WRITE)
					&& checkPermissions(user, role);
		case DROP_COLLECTION:
			return checkPermissions(user, collection, ObjectAction.FULL)
					&& checkPermissions(user, role);
		case MANAGE_ACTIONS:
			return (collection.getOwner().equals(DBConnection.getLoggedUser()
					.getLogin()));
		default:
			return checkPermissions(user, role);
		}
	}

	public static boolean checkPermissions(User user, Collection collection,
			ObjectAction action) {
		switch (action) {
		case NO_ACCESS:
			return user.has(collection, action);
		case READ:
			return checkPermissions(user, Role.BROWSE_COLLECTION)
					&& ((user.getAction(collection) == null || (user
							.getAction(collection) != null && user.getAction(
							collection).getIntegerCode() > 0)))
					|| (collection.getOwner().equals(user.getLogin()));
		case WRITE:
			return checkPermissions(user, Role.EDIT_COLLECTION)
					&& ((user.getAction(collection) != null && user.getAction(
							collection).getIntegerCode() > 1) || collection
							.getOwner().equals(user.getLogin()));
		case FULL:
			return checkPermissions(user, Role.DROP_COLLECTION)
					&& checkPermissions(user, collection, ObjectAction.READ)
					&& checkPermissions(user, collection, ObjectAction.WRITE)
					&& ((user.getAction(collection) != null && user.getAction(
							collection).getIntegerCode() > 2) || (collection
							.getOwner().equals(user.getLogin())));
		default:
			return false;
		}
	}

	public static boolean checkPermissions(User user, Document document,
			ObjectAction action) {
		Collection collection = document.getCollection();
		switch (action) {
		case NO_ACCESS:
			return user.has(document, action);
		case READ:
			return checkPermissions(user, Role.BROWSE_DOCUMENT)
					&& checkPermissions(user, Role.BROWSE_COLLECTION)
					&& (((user.getAction(document) == null || (user
							.getAction(document) != null && user.getAction(
							document).getIntegerCode() > 0))
							&& (user.getAction(collection) == null || (user
									.getAction(collection) != null && user
									.getAction(collection).getIntegerCode() > 0))
							|| user.getLogin().equals(collection.getOwner()) || user
							.getLogin().equals(document.getOwner())));
		case WRITE:
			return checkPermissions(user, Role.EDIT_DOCUMENT)
					&& checkPermissions(user, Role.EDIT_COLLECTION)
					&& (((user.getAction(document) != null && user.getAction(
							document).getIntegerCode() > 1) && (user
							.getAction(collection) != null && user.getAction(
							collection).getIntegerCode() > 1))
							|| user.getLogin().equals(collection.getOwner()) || user
							.getLogin().equals(document.getOwner()));
		case FULL:
			return checkPermissions(user, Role.DROP_DOCUMENT)
					&& ((checkPermissions(user, document, ObjectAction.READ)
							&& checkPermissions(user, document,
									ObjectAction.WRITE) && (user
							.getAction(document) != null && user.getAction(
							document).getIntegerCode() > 2))
							|| user.getLogin().equals(collection.getOwner()) || user
							.getLogin().equals(document.getOwner()));
		default:
			return false;
		}
	}

	public static boolean checkPermissions(User user, Role role) {
		return user.has(role);
	}

	/**
	 * Add system role to user
	 * 
	 * @param role
	 *            Role to add
	 * @throws GlodocsException
	 */
	public void addRole(User user, Role role) throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.MANAGE_ROLES)) {
			user.rolesList.add(role);
			user.save();
		} else {
			throw new GlodocsException("Access Denied");
		}

	}

}
