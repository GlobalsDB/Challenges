package com.xlab.glodocs.gui.consts;


public interface DocumentAttributesListConsts {

	/* JSON types (for tree elements) */
	public static final String JSON_TYPE_OBJECT = "JSON_TYPE_OBJECT";
	public static final String JSON_TYPE_ARRAY = "JSON_TYPE_ARRAY";
	public static final String JSON_TYPE_STRING_ELEMENT = "JSON_TYPE_STRING_ELEMENT";
	public static final String JSON_TYPE_INT_ELEMENT = "JSON_TYPE_INT_ELEMENT";
	public static final String JSON_TYPE_BOOL_ELEMENT = "JSON_TYPE_BOOL_ELEMENT";
	public static final String JSON_TYPE_NULL_ELEMENT = "JSON_TYPE_NULL_ELEMENT";

	/* Service strings */
	public static final String ROOT_ELEMENT_TEXT = "Document Attributes";
	public static final String EMPTY_FIELD_VALUE_STRING = "<empty>";
	public static final String NEW_FIELD_VALUE_STRING = "<Enter value here>";
	public static final String NEW_ELEMENT_NAME_STRING = "<Enter name here>";
	public static final String BOOL_TRUE_VALUE_TEXT = "true";
	public static final String BOOL_FALSE_VALUE_TEXT = "false";
	public static final String SPOILER = "SPOILER";
	public static final String SPOILER_TEXT = "---------";

	/* Menu content strings */
	public static final String MENU_TEXT_ADD_NEW_OBJECT_TO_COLLECTION = "Add new object to the collection with the first member of type";
	public static final String MENU_ITEM_TEXT_OBJECT = "Object";
	public static final String MENU_ITEM_TEXT_ARRAY = "Array";
	public static final String MENU_ITEM_TEXT_FIELD = "Field";
	public static final String MENU_TEXT_ADD = "Add";
	public static final String MENU_TEXT_TO_THIS_OBJECT = "To this object";
	public static final String MENU_TEXT_TO_PARENT_OBJECT = "To parent object";
	public static final String MENU_TEXT_ADD_TO_PARENT_OBJECT = "Add to parent object";
	public static final String MENU_TEXT_TO_THIS_ARRAY = "To this array";
	public static final String MENU_ITEM_TEXT_NAMED_FIELD = "Named field";
	public static final String MENU_TEXT_ADD_TO_PARENT_ARRAY = "Add to parent array";
	public static final String MENU_TEXT_DELETE = "Delete";
	public static final String MENU_ITEM_TEXT_THIS_OBJECT = "This object";
	public static final String MENU_ITEM_TEXT_THIS_ARRAY = "This array";
	public static final String MENU_ITEM_TEXT_THIS_FIELD = "This field";
	public static final String MENU_ITEM_TEXT_PARENT_OBJECT = "Parent object";
	public static final String MENU_ITEM_TEXT_PARENT_ARRAY = "Parent array";
	public static final String MENU_ITEM_TEXT_RENAME = "Rename";
	public static final String MENU_ITEM_TEXT_EDIT_VALUE = "Edit value";
	public static final String MENU_ITEM_TEXT_SET_VALUE = "Set value";
	public static final String MENU_ITEM_TEXT_CLEAR_VALUE = "Clear value";

	/* Menu command strings */
	public static final String MENU_COMMAND_CREATE_OBJECT_WITH_OBJECT = "MENU_COMMAND_CREATE_OBJECT_WITH_OBJECT";
	public static final String MENU_COMMAND_CREATE_OBJECT_WITH_ARRAY = "MENU_COMMAND_CREATE_OBJECT_WITH_ARRAY";
	public static final String MENU_COMMAND_CREATE_OBJECT_WITH_FIELD = "MENU_COMMAND_CREATE_OBJECT_WITH_FIELD";
	public static final String MENU_COMMAND_ADD_OBJECT_TO_THIS_OBJECT = "MENU_COMMAND_ADD_OBJECT_TO_THIS_OBJECT";
	public static final String MENU_COMMAND_ADD_ARRAY_TO_THIS_OBJECT = "MENU_COMMAND_ADD_ARRAY_TO_THIS_OBJECT";
	public static final String MENU_COMMAND_ADD_FIELD_TO_THIS_OBJECT = "MENU_COMMAND_ADD_FIELD_TO_THIS_OBJECT";
	public static final String MENU_COMMAND_ADD_OBJECT_TO_PARENT_OBJECT = "MENU_COMMAND_ADD_OBJECT_TO_PARENT_OBJECT";
	public static final String MENU_COMMAND_ADD_ARRAY_TO_PARENT_OBJECT = "MENU_COMMAND_ADD_ARRAY_TO_PARENT_OBJECT";
	public static final String MENU_COMMAND_ADD_FIELD_TO_PARENT_OBJECT = "MENU_COMMAND_ADD_FIELD_TO_PARENT_OBJECT";
	public static final String MENU_COMMAND_ADD_FIELD_TO_THIS_ARRAY = "MENU_COMMAND_ADD_FIELD_TO_THIS_ARRAY";
	public static final String MENU_COMMAND_ADD_NAMED_FIELD_TO_THIS_ARRAY = "MENU_COMMAND_ADD_NAMED_FIELD_TO_THIS_ARRAY";
	public static final String MENU_COMMAND_ADD_FIELD_TO_PARENT_ARRAY = "MENU_COMMAND_ADD_FIELD_TO_PARENT_ARRAY";
	public static final String MENU_COMMAND_ADD_NAMED_FIELD_TO_PARENT_ARRAY = "MENU_COMMAND_ADD_NAMED_FIELD_TO_PARENT_ARRAY";
	public static final String MENU_COMMAND_DELETE_THIS_OBJECT = "MENU_COMMAND_DELETE_THIS_OBJECT";
	public static final String MENU_COMMAND_DELETE_THIS_ARRAY = "MENU_COMMAND_DELETE_THIS_ARRAY";
	public static final String MENU_COMMAND_DELETE_THIS_FIELD = "MENU_COMMAND_DELETE_THIS_FIELD";
	public static final String MENU_COMMAND_DELETE_PARENT_OBJECT = "MENU_COMMAND_DELETE_PARENT_OBJECT";
	public static final String MENU_COMMAND_DELETE_PARENT_ARRAY = "MENU_COMMAND_DELETE_PARENT_ARRAY";
	public static final String MENU_COMMAND_RENAME = "MENU_COMMAND_RENAME";
	public static final String MENU_COMMAND_EDIT_VALUE = "MENU_COMMAND_EDIT_VALUE";
	public static final String MENU_COMMAND_SET_VALUE = "MENU_COMMAND_SET_VALUE";
	public static final String MENU_COMMAND_CLEAR_VALUE = "MENU_COMMAND_CLEAR_VALUE";

	/* Dialog strings */
	public static final String DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TEXT = "Do you really want to delete the object (or array)? Warning: all children will be also deleted. This action cannot be undone!";
	public static final String DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TITLE = "Delete object or array";
	public static final String DELETE_FIELD_CONFIRMATION_TEXT = "Do you really want to delete this field? Warning: this action cannot be undone!";
	public static final String DELETE_FIELD_CONFIRMATION_TITLE = "Delete field";
	public static final String CLEAR_VALUE_CONFIRMATION_TEXT = "Do you really want to clear the value? Warning: this action cannot be undone!";
	public static final String CLEAR_VALUE_CONFIRMATION_TITLE = "Clear value";
	public static final String RENAME_DIALOG_PROMPT = "Please enter the name:";
	public static final String RENAME_DIALOG_TITLE = "Rename";
	public static final String RENAME_DIALOG_NAME_EMPTY_ERROR = "The name must not be empty.";
	public static final String EDIT_DIALOG_SET_VALUE_PROMPT = "Please enter the new value to set for the field";
	public static final String EDIT_DIALOG_SET_VALUE_TITLE = "Set value";
	public static final String EDIT_DIALOG_SELECT_VALUE_PROMPT = "Please select the new value for the field";
	public static final String EDIT_DIALOG_SELECT_VALUE_TITLE = "Select value";
	public static final String EDIT_DIALOG_ENTER_VALUE_PROMPT = "Please enter the new value for the field";
	public static final String EDIT_DIALOG_ENTER_VALUE_TITLE = "Edit value";
	public static final String EDIT_DIALOG_VALUE_EMPTY_ERROR = "The new value must not be empty.";
	public static final String EDIT_DIALOG_VALUE_NOT_INTEGER_ERROR = "The new value must be an integer.";
}
