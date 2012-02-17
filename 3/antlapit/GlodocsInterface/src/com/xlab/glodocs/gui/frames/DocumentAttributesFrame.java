package com.xlab.glodocs.gui.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.gui.consts.DocumentAttributesListConsts;

public class DocumentAttributesFrame extends JPanel {
	private DefaultTreeModel treeModel;
	private MyJTree attributesTree;
	private DefaultMutableTreeNode rootElement;
	private DefaultMutableTreeNode lastSpoiler = null;

	public DocumentAttributesFrame() {
		super(new MigLayout("", "grow, fill", "grow"));
	}

	public void init(String title) {
		this.rootElement = new DefaultMutableTreeNode(title);
		this.treeModel = new DefaultTreeModel(this.rootElement);
		this.attributesTree = new MyJTree(this.treeModel);
		this.add(new JScrollPane(this.attributesTree), "growx");
	}

	public void drawJSONObjectSubtree(JSONObject obj, String jsonObjectId)
			throws JSONException {
		drawJSONObjectSubtree(obj, this.rootElement, jsonObjectId);
	}

	public void drawSpoiler() {
		DefaultMutableTreeNode spoiler = new DefaultMutableTreeNode(
				new ElementData(DocumentAttributesListConsts.SPOILER_TEXT, "",
						DocumentAttributesListConsts.SPOILER, ""));
		this.rootElement.add(spoiler);
		this.lastSpoiler = spoiler;
	}

	public void removeLastSpoiler() {
		if (this.lastSpoiler != null) {
			this.lastSpoiler.removeFromParent();
		}
	}

	private void drawJSONObjectSubtree(JSONObject obj,
			DefaultMutableTreeNode parentTreeNode, String jsonObjectId)
			throws JSONException {
		// Get all element names from the object
		String[] names = JSONObject.getNames(obj);
		// And iterate over them
		if (names != null)
			for (String name : names) {
				DefaultMutableTreeNode element = new DefaultMutableTreeNode();
				element.setUserObject(drawChild(obj.get(name), element, name,
						jsonObjectId));
				parentTreeNode.add(element);
			}
	}

	private void drawJSONArraySubtree(JSONArray arr,
			DefaultMutableTreeNode parentTreeNode, String jsonObjectId)
			throws JSONException {
		for (int i = 0; i < arr.length(); i++) {
			DefaultMutableTreeNode element = new DefaultMutableTreeNode();
			ElementData ed = drawChild(arr.get(i), element, parentTreeNode, "",
					jsonObjectId);
			element.setUserObject(ed);
			if (!DocumentAttributesListConsts.JSON_TYPE_OBJECT.equals(ed
					.getType())
					&& !DocumentAttributesListConsts.JSON_TYPE_ARRAY.equals(ed
							.getType())) {
				parentTreeNode.add(element);
			}
		}
	}

	private ElementData drawChild(Object child, DefaultMutableTreeNode element,
			String name, String jsonObjectId) throws JSONException {
		return drawChild(child, element, null, name, jsonObjectId);
	}

	private ElementData drawChild(Object child, DefaultMutableTreeNode element,
			DefaultMutableTreeNode explicitParentNode, String name,
			String jsonObjectId) throws JSONException {
		ElementData result = new ElementData();
		if (child.getClass().equals(JSONObject.class)) {
			result.setType(DocumentAttributesListConsts.JSON_TYPE_OBJECT);
			if (explicitParentNode != null) {
				drawJSONObjectSubtree((JSONObject) child, explicitParentNode,
						jsonObjectId);
			} else {
				drawJSONObjectSubtree((JSONObject) child, element, jsonObjectId);
			}
		} else if (child.getClass().equals(JSONArray.class)) {
			result.setType(DocumentAttributesListConsts.JSON_TYPE_ARRAY);
			if (explicitParentNode != null) {
				drawJSONArraySubtree((JSONArray) child, explicitParentNode,
						jsonObjectId);
			} else {
				drawJSONArraySubtree((JSONArray) child, element, jsonObjectId);
			}
		} else if (child.getClass().equals(JSONObject.NULL.getClass())) {
			result.setEmptyValue();
			result.setType(DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT);
		} else {
			if (child.getClass().equals(Integer.class)) {
				result.setType(DocumentAttributesListConsts.JSON_TYPE_INT_ELEMENT);
			} else if (child.getClass().equals(Boolean.class)) {
				result.setType(DocumentAttributesListConsts.JSON_TYPE_BOOL_ELEMENT);
			} else {
				result.setType(DocumentAttributesListConsts.JSON_TYPE_STRING_ELEMENT);
			}
			result.setValue(child.toString());
		}
		if (!"".equals(name)) {
			result.setName(name);
		}
		result.setKey(jsonObjectId);
		return result;
	}

	public JSONObject getUpdatedJSONObjectsCollection() throws JSONException {
		String currentId = "";
		Enumeration<DefaultMutableTreeNode> allParentItems = this.rootElement
				.children();
		JSONObject currentObject = null;
		while (allParentItems.hasMoreElements()) {
			DefaultMutableTreeNode currentItem = allParentItems.nextElement();
			ElementData elementData = (ElementData) currentItem.getUserObject();
			if (!DocumentAttributesListConsts.SPOILER.equals(elementData
					.getType())) {
				if (!currentId.equals(elementData.getKey())) {
					// A new object creation starts
					currentId = elementData.getKey();
					currentObject = new JSONObject();
				}
				// Put an item to an object
				putItemToObject(currentObject, currentItem);
				if (currentItem.equals(this.rootElement.getLastChild())) {
					// We have reached the end of the object tree => put
					// currentObject into the collection and exit
					// putObjectToCollection(currentObject, currentId);
					return currentObject;
				}
			} else {
				// We have reached the spoiler which indicates the end of the
				// currentObject => put it into the collection and proceed to
				// the next obejct
				// putObjectToCollection(currentObject, currentId);
				return currentObject;
			}
		}
		return currentObject;
	}

	private void putItemToObject(JSONObject object, DefaultMutableTreeNode item)
			throws JSONException {
		ElementData elementData = (ElementData) item.getUserObject();
		if (DocumentAttributesListConsts.JSON_TYPE_OBJECT.equals(elementData
				.getType())) {
			JSONObject tempObject = new JSONObject();
			Enumeration<DefaultMutableTreeNode> childItems = item.children();
			while (childItems.hasMoreElements()) {
				putItemToObject(tempObject, childItems.nextElement());
			}
			object.put(elementData.getName(), tempObject);
		} else if (DocumentAttributesListConsts.JSON_TYPE_ARRAY
				.equals(elementData.getType())) {
			JSONArray tempArray = new JSONArray();
			Enumeration<DefaultMutableTreeNode> childItems = item.children();
			while (childItems.hasMoreElements()) {
				putItemToArray(tempArray, childItems.nextElement());
			}
			object.put(elementData.getName(), tempArray);
		} else if (DocumentAttributesListConsts.JSON_TYPE_STRING_ELEMENT
				.equals(elementData.getType())) {
			object.put(elementData.getName(), elementData.getValue());
		} else if (DocumentAttributesListConsts.JSON_TYPE_INT_ELEMENT
				.equals(elementData.getType())) {
			object.put(elementData.getName(),
					Integer.parseInt(elementData.getValue()));
		} else if (DocumentAttributesListConsts.JSON_TYPE_BOOL_ELEMENT
				.equals(elementData.getType())) {
			object.put(elementData.getName(),
					Boolean.parseBoolean(elementData.getValue()));
		} else if (DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT
				.equals(elementData.getType())) {
			object.put(elementData.getName(), JSONObject.NULL);
		}
	}

	private void putItemToArray(JSONArray array, DefaultMutableTreeNode item)
			throws JSONException {
		ElementData elementData = (ElementData) item.getUserObject();
		if (!"".equals(elementData.getName())) {
			JSONObject tempObject = new JSONObject();
			putItemToObject(tempObject, item);
			array.put(tempObject);
		} else if (DocumentAttributesListConsts.JSON_TYPE_STRING_ELEMENT
				.equals(elementData.getType())) {
			array.put(elementData.getValue());
		} else if (DocumentAttributesListConsts.JSON_TYPE_INT_ELEMENT
				.equals(elementData.getType())) {
			array.put(Integer.parseInt(elementData.getValue()));
		} else if (DocumentAttributesListConsts.JSON_TYPE_BOOL_ELEMENT
				.equals(elementData.getType())) {
			array.put(Boolean.parseBoolean(elementData.getValue()));
		} else if (DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT
				.equals(elementData.getType())) {
			array.put(JSONObject.NULL);
		}
	}

}

class ElementData {
	private String name = "", value = "", type = "", key = "";

	public ElementData() {
	}

	public ElementData(String name, String value, String type, String key) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setEmptyValue() {
		this.value = DocumentAttributesListConsts.EMPTY_FIELD_VALUE_STRING;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String toString() {
		if ((name != null) && (!"".equals(name)) && (value != null)
				&& (!"".equals(value))) {
			return name + " = " + value;
		} else if ((name != null) && (!"".equals(name))) {
			return name;
		} else if ((value != null) && (!"".equals(value))) {
			return value;
		} else {
			return DocumentAttributesListConsts.EMPTY_FIELD_VALUE_STRING;
		}
	}
}

class MyJTree extends JTree implements ActionListener {
	private DefaultMutableTreeNode selectedNode = null;
	private DefaultTreeModel treeModel = null;
	private String lastJSONObjectID = "";

	public MyJTree(DefaultTreeModel treeModel) {
		super(treeModel);
		this.treeModel = treeModel;

		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					TreePath path = getPathForLocation(e.getX(), e.getY());
					if (path != null) {
						setSelectionPath(path);
					} else {
						return;
					}
					selectedNode = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					getPopupMenu().show((JComponent) e.getSource(), e.getX(),
							e.getY());
				}
			}

		});
	}

	private JPopupMenu getPopupMenu() {
		if (this.selectedNode.isRoot()) {
			return createRootMenu();
		} else {
			String elementType = ((ElementData) this.selectedNode
					.getUserObject()).getType();
			if (DocumentAttributesListConsts.JSON_TYPE_OBJECT
					.equals(elementType)) {
				return createObjectMenu();
			} else if (DocumentAttributesListConsts.JSON_TYPE_ARRAY
					.equals(elementType)) {
				return createArrayMenu();
			} else if (DocumentAttributesListConsts.JSON_TYPE_STRING_ELEMENT
					.equals(elementType)
					|| DocumentAttributesListConsts.JSON_TYPE_INT_ELEMENT
							.equals(elementType)
					|| DocumentAttributesListConsts.JSON_TYPE_BOOL_ELEMENT
							.equals(elementType)) {
				return createFieldMenu(true, isParentArray(),
						!"".equals(((ElementData) this.selectedNode
								.getUserObject()).getName()));
			} else if (DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT
					.equals(elementType)) {
				return createFieldMenu(false, isParentArray(),
						!"".equals(((ElementData) this.selectedNode
								.getUserObject()).getName()));
			} else {
				return new JPopupMenu();
			}
		}
	}

	private JPopupMenu createRootMenu() {
		JPopupMenu result = new JPopupMenu();

		JMenu createNewObjectMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_ADD_NEW_OBJECT_TO_COLLECTION);
		JMenuItem menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_OBJECT);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_CREATE_OBJECT_WITH_OBJECT);
		createNewObjectMenu.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_ARRAY);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_CREATE_OBJECT_WITH_ARRAY);
		createNewObjectMenu.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_CREATE_OBJECT_WITH_FIELD);
		createNewObjectMenu.add(menuItem);
		result.add(createNewObjectMenu);

		return result;
	}

	private JPopupMenu createObjectMenu() {
		JPopupMenu result = new JPopupMenu();

		JMenu addElementMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_ADD);
		JMenu addToThisObjectMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_TO_THIS_OBJECT);
		JMenuItem menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_OBJECT);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_OBJECT_TO_THIS_OBJECT);
		addToThisObjectMenu.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_ARRAY);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_ARRAY_TO_THIS_OBJECT);
		addToThisObjectMenu.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_THIS_OBJECT);
		addToThisObjectMenu.add(menuItem);
		addElementMenu.add(addToThisObjectMenu);

		addElementMenu.add(createAddToParentObjectMenu(false));
		result.add(addElementMenu);

		JMenu deleteMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_DELETE);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_THIS_OBJECT);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_DELETE_THIS_OBJECT);
		deleteMenu.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_PARENT_OBJECT);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_DELETE_PARENT_OBJECT);
		deleteMenu.add(menuItem);
		result.add(deleteMenu);

		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_RENAME);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_RENAME);
		result.add(menuItem);

		return result;
	}

	private JPopupMenu createArrayMenu() {
		JPopupMenu result = new JPopupMenu();

		JMenu addElementMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_ADD);
		JMenu addToThisArrayMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_TO_THIS_ARRAY);
		JMenuItem menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_THIS_ARRAY);
		addToThisArrayMenu.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_NAMED_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_NAMED_FIELD_TO_THIS_ARRAY);
		addToThisArrayMenu.add(menuItem);
		addElementMenu.add(addToThisArrayMenu);

		addElementMenu.add(createAddToParentObjectMenu(false));
		result.add(addElementMenu);

		JMenu deleteMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_DELETE);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_THIS_ARRAY);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_DELETE_THIS_ARRAY);
		deleteMenu.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_PARENT_OBJECT);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_DELETE_PARENT_OBJECT);
		deleteMenu.add(menuItem);
		result.add(deleteMenu);

		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_RENAME);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_RENAME);
		result.add(menuItem);

		return result;
	}

	private JPopupMenu createFieldMenu(boolean fieldTypeIsNull,
			boolean inArray, boolean showRenameItem) {
		JPopupMenu result = new JPopupMenu();

		result.add((inArray ? createAddToParentArrayMenu()
				: createAddToParentObjectMenu(true)));

		JMenu deleteMenu = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_DELETE);
		JMenuItem menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_THIS_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_DELETE_THIS_FIELD);
		deleteMenu.add(menuItem);
		if (inArray) {
			menuItem = new JMenuItem(
					DocumentAttributesListConsts.MENU_ITEM_TEXT_PARENT_ARRAY);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_DELETE_PARENT_ARRAY);
		} else {
			menuItem = new JMenuItem(
					DocumentAttributesListConsts.MENU_ITEM_TEXT_PARENT_OBJECT);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_DELETE_PARENT_OBJECT);
		}
		deleteMenu.add(menuItem);
		result.add(deleteMenu);

		if (!inArray || showRenameItem) {
			menuItem = new JMenuItem(
					DocumentAttributesListConsts.MENU_ITEM_TEXT_RENAME);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_RENAME);
			result.add(menuItem);
		}

		if (fieldTypeIsNull) {
			menuItem = new JMenuItem(
					DocumentAttributesListConsts.MENU_ITEM_TEXT_EDIT_VALUE);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_EDIT_VALUE);
			result.add(menuItem);
		} else {
			menuItem = new JMenuItem(
					DocumentAttributesListConsts.MENU_ITEM_TEXT_SET_VALUE);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_SET_VALUE);
			result.add(menuItem);
		}

		if (fieldTypeIsNull) {
			menuItem = new JMenuItem(
					DocumentAttributesListConsts.MENU_ITEM_TEXT_CLEAR_VALUE);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_CLEAR_VALUE);
			result.add(menuItem);
		}

		return result;
	}

	private JMenu createAddToParentObjectMenu(boolean forElement) {
		JMenu result = new JMenu(
				(forElement ? DocumentAttributesListConsts.MENU_TEXT_ADD_TO_PARENT_OBJECT
						: DocumentAttributesListConsts.MENU_TEXT_TO_PARENT_OBJECT));

		JMenuItem menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_OBJECT);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_OBJECT_TO_PARENT_OBJECT);
		result.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_ARRAY);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_ARRAY_TO_PARENT_OBJECT);
		result.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_PARENT_OBJECT);
		result.add(menuItem);

		return result;
	}

	private JMenu createAddToParentArrayMenu() {
		JMenu result = new JMenu(
				DocumentAttributesListConsts.MENU_TEXT_ADD_TO_PARENT_ARRAY);

		JMenuItem menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_PARENT_ARRAY);
		result.add(menuItem);
		menuItem = new JMenuItem(
				DocumentAttributesListConsts.MENU_ITEM_TEXT_NAMED_FIELD);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(DocumentAttributesListConsts.MENU_COMMAND_ADD_NAMED_FIELD_TO_PARENT_ARRAY);
		result.add(menuItem);

		return result;
	}

	private boolean isParentArray() {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
				.getParent();
		if (parentNode.isRoot()) {
			return false;
		}
		return (DocumentAttributesListConsts.JSON_TYPE_ARRAY
				.equals(((ElementData) parentNode.getUserObject()).getType()));
	}

	public void actionPerformed(ActionEvent ae) {
		String menuCommand = ae.getActionCommand();

		if (DocumentAttributesListConsts.MENU_COMMAND_CREATE_OBJECT_WITH_OBJECT
				.equals(menuCommand)) {
			createObjectWithObject();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_CREATE_OBJECT_WITH_ARRAY
				.equals(menuCommand)) {
			createObjectWithArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_CREATE_OBJECT_WITH_FIELD
				.equals(menuCommand)) {
			createObjectWithField();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_OBJECT_TO_THIS_OBJECT
				.equals(menuCommand)) {
			addObjectToObject(false);
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_ARRAY_TO_THIS_OBJECT
				.equals(menuCommand)) {
			addArrayToObject(false);
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_THIS_OBJECT
				.equals(menuCommand)) {
			addFieldToObject(false);
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_OBJECT_TO_PARENT_OBJECT
				.equals(menuCommand)) {
			addObjectToParentObject();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_ARRAY_TO_PARENT_OBJECT
				.equals(menuCommand)) {
			addArrayToParentObject();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_PARENT_OBJECT
				.equals(menuCommand)) {
			addFieldToParentObject();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_THIS_ARRAY
				.equals(menuCommand)) {
			addFieldToArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_NAMED_FIELD_TO_THIS_ARRAY
				.equals(menuCommand)) {
			addNamedFieldToArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_FIELD_TO_PARENT_ARRAY
				.equals(menuCommand)) {
			addFieldToParentArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_ADD_NAMED_FIELD_TO_PARENT_ARRAY
				.equals(menuCommand)) {
			addNamedFieldToParentArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_DELETE_THIS_OBJECT
				.equals(menuCommand)) {
			deleteThisObjectOrArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_DELETE_THIS_ARRAY
				.equals(menuCommand)) {
			deleteThisObjectOrArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_DELETE_THIS_FIELD
				.equals(menuCommand)) {
			deleteThisField();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_DELETE_PARENT_OBJECT
				.equals(menuCommand)) {
			deleteParentObject();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_DELETE_PARENT_ARRAY
				.equals(menuCommand)) {
			deleteParentArray();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_RENAME
				.equals(menuCommand)) {
			rename();
		} else if (DocumentAttributesListConsts.MENU_COMMAND_EDIT_VALUE
				.equals(menuCommand)) {
			editOrSetValue(false);
		} else if (DocumentAttributesListConsts.MENU_COMMAND_SET_VALUE
				.equals(menuCommand)) {
			editOrSetValue(true);
		} else if (DocumentAttributesListConsts.MENU_COMMAND_CLEAR_VALUE
				.equals(menuCommand)) {
			clearValue();
		}
	}

	private void createObjectWithObject() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeModel
				.getRoot();
		Integer newIndex = getNewObjectIndex(root);

		if (root.getChildCount() > 0) {
			DefaultMutableTreeNode spoiler = new DefaultMutableTreeNode(
					new ElementData(DocumentAttributesListConsts.SPOILER_TEXT,
							"", DocumentAttributesListConsts.SPOILER, ""));
			this.treeModel.insertNodeInto(spoiler, root, root.getChildCount());
		}

		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData(
						DocumentAttributesListConsts.NEW_ELEMENT_NAME_STRING,
						"", DocumentAttributesListConsts.JSON_TYPE_OBJECT,
						newIndex.toString()));
		this.treeModel.insertNodeInto(newNode, root, root.getChildCount());

		TreePath newSelectionPath = new TreePath(root.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		this.selectedNode = newNode;
		rename();
	}

	private void createObjectWithArray() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getModel()
				.getRoot();
		Integer newIndex = getNewObjectIndex(root);

		if (root.getChildCount() > 0) {
			DefaultMutableTreeNode spoiler = new DefaultMutableTreeNode(
					new ElementData(DocumentAttributesListConsts.SPOILER_TEXT,
							"", DocumentAttributesListConsts.SPOILER, ""));
			this.treeModel.insertNodeInto(spoiler, root, root.getChildCount());
		}

		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData(
						DocumentAttributesListConsts.NEW_ELEMENT_NAME_STRING,
						"", DocumentAttributesListConsts.JSON_TYPE_ARRAY,
						newIndex.toString()));
		this.treeModel.insertNodeInto(newNode, root, root.getChildCount());

		TreePath newSelectionPath = new TreePath(root.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		this.selectedNode = newNode;
		rename();
	}

	private void createObjectWithField() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getModel()
				.getRoot();
		Integer newIndex = getNewObjectIndex(root);

		if (root.getChildCount() > 0) {
			DefaultMutableTreeNode spoiler = new DefaultMutableTreeNode(
					new ElementData(DocumentAttributesListConsts.SPOILER_TEXT,
							"", DocumentAttributesListConsts.SPOILER, ""));
			this.treeModel.insertNodeInto(spoiler, root, root.getChildCount());
		}

		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData(
						DocumentAttributesListConsts.NEW_ELEMENT_NAME_STRING,
						DocumentAttributesListConsts.NEW_FIELD_VALUE_STRING,
						DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT,
						newIndex.toString()));
		this.treeModel.insertNodeInto(newNode, root, root.getChildCount());

		TreePath newSelectionPath = new TreePath(root.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		this.selectedNode = newNode;
		rename();
		editOrSetValue(true);
	}

	private void addObjectToObject(boolean addToRoot) {
		String key = "";
		if (addToRoot) {
			key = this.lastJSONObjectID;
		} else {
			key = ((ElementData) this.selectedNode.getUserObject()).getKey();
		}
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData(
						DocumentAttributesListConsts.NEW_ELEMENT_NAME_STRING,
						"", DocumentAttributesListConsts.JSON_TYPE_OBJECT, key));
		int insertIndex = 0;
		if (addToRoot) {
			insertIndex = getInsertIndex();
		} else {
			insertIndex = this.selectedNode.getChildCount();
		}
		this.treeModel.insertNodeInto(newNode, this.selectedNode, insertIndex);

		this.selectedNode = newNode;
		TreePath newSelectionPath = new TreePath(this.selectedNode
				.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		rename();
	}

	private void addArrayToObject(boolean addToRoot) {
		String key = "";
		if (addToRoot) {
			key = this.lastJSONObjectID;
		} else {
			key = ((ElementData) this.selectedNode.getUserObject()).getKey();
		}
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData(
						DocumentAttributesListConsts.NEW_ELEMENT_NAME_STRING,
						"", DocumentAttributesListConsts.JSON_TYPE_ARRAY, key));
		int insertIndex = 0;
		if (addToRoot) {
			insertIndex = getInsertIndex();
		} else {
			insertIndex = this.selectedNode.getChildCount();
		}
		this.treeModel.insertNodeInto(newNode, this.selectedNode, insertIndex);

		this.selectedNode = newNode;
		TreePath newSelectionPath = new TreePath(this.selectedNode
				.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		rename();
	}

	private void addFieldToObject(boolean addToRoot) {
		String key = "";
		if (addToRoot) {
			key = this.lastJSONObjectID;
		} else {
			key = ((ElementData) this.selectedNode.getUserObject()).getKey();
		}
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData(
						DocumentAttributesListConsts.NEW_ELEMENT_NAME_STRING,
						DocumentAttributesListConsts.NEW_FIELD_VALUE_STRING,
						DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT,
						key));
		int insertIndex = 0;
		if (addToRoot) {
			insertIndex = getInsertIndex();
		} else {
			insertIndex = this.selectedNode.getChildCount();
		}
		this.treeModel.insertNodeInto(newNode, this.selectedNode, insertIndex);

		this.selectedNode = newNode;
		TreePath newSelectionPath = new TreePath(this.selectedNode
				.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		rename();
		editOrSetValue(true);
	}

	private void addObjectToParentObject() {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
				.getParent();
		this.lastJSONObjectID = ((ElementData) this.selectedNode
				.getUserObject()).getKey();
		this.selectedNode = parentNode;
		addObjectToObject(parentNode.isRoot());
	}

	private void addArrayToParentObject() {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
				.getParent();
		this.lastJSONObjectID = ((ElementData) this.selectedNode
				.getUserObject()).getKey();
		this.selectedNode = parentNode;
		addArrayToObject(parentNode.isRoot());
	}

	private void addFieldToParentObject() {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
				.getParent();
		this.lastJSONObjectID = ((ElementData) this.selectedNode
				.getUserObject()).getKey();
		this.selectedNode = parentNode;
		addFieldToObject(parentNode.isRoot());
	}

	private void addFieldToArray() {
		String key = ((ElementData) this.selectedNode.getUserObject()).getKey();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData("",
						DocumentAttributesListConsts.NEW_FIELD_VALUE_STRING,
						DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT,
						key));
		this.treeModel.insertNodeInto(newNode, this.selectedNode,
				this.selectedNode.getChildCount());

		TreePath newSelectionPath = new TreePath(this.selectedNode
				.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		this.selectedNode = newNode;
		editOrSetValue(true);
	}

	private void addNamedFieldToArray() {
		String key = ((ElementData) this.selectedNode.getUserObject()).getKey();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new ElementData(
						DocumentAttributesListConsts.NEW_ELEMENT_NAME_STRING,
						DocumentAttributesListConsts.NEW_FIELD_VALUE_STRING,
						DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT,
						key));
		this.treeModel.insertNodeInto(newNode, this.selectedNode,
				this.selectedNode.getChildCount());

		TreePath newSelectionPath = new TreePath(this.selectedNode
				.getLastLeaf().getPath());
		this.setSelectionPath(newSelectionPath);
		this.scrollPathToVisible(newSelectionPath);
		this.selectedNode = newNode;
		rename();
		editOrSetValue(true);
	}

	private void addFieldToParentArray() {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
				.getParent();
		this.selectedNode = parentNode;
		addFieldToArray();
	}

	private void addNamedFieldToParentArray() {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
				.getParent();
		this.selectedNode = parentNode;
		addNamedFieldToArray();
	}

	private void deleteThisObjectOrArray() {
		int rc = JOptionPane
				.showOptionDialog(
						null,
						DocumentAttributesListConsts.DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TEXT,
						DocumentAttributesListConsts.DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TITLE,
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						null, null, null);
		if (JOptionPane.YES_OPTION == rc) {
			this.treeModel.removeNodeFromParent(this.selectedNode);
			removeExtraSpoilers();
		}
	}

	private void deleteThisField() {
		int rc = JOptionPane.showOptionDialog(null,
				DocumentAttributesListConsts.DELETE_FIELD_CONFIRMATION_TEXT,
				DocumentAttributesListConsts.DELETE_FIELD_CONFIRMATION_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				null, null);
		if (JOptionPane.YES_OPTION == rc) {
			this.treeModel.removeNodeFromParent(this.selectedNode);
			removeExtraSpoilers();
		}
	}

	private void deleteParentObject() {
		int rc = JOptionPane
				.showOptionDialog(
						null,
						DocumentAttributesListConsts.DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TEXT,
						DocumentAttributesListConsts.DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TITLE,
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						null, null, null);
		if (JOptionPane.YES_OPTION == rc) {
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
					.getParent();
			if (parentNode.isRoot()) {
				this.lastJSONObjectID = ((ElementData) this.selectedNode
						.getUserObject()).getKey();
				deleteObjectFromTree();
			} else {
				this.treeModel.removeNodeFromParent(parentNode);
				removeExtraSpoilers();
			}
		}
	}

	private void deleteParentArray() {
		int rc = JOptionPane
				.showOptionDialog(
						null,
						DocumentAttributesListConsts.DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TEXT,
						DocumentAttributesListConsts.DELETE_OBJECT_OR_ARRAY_CONFIRMATION_TITLE,
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						null, null, null);
		if (JOptionPane.YES_OPTION == rc) {
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) this.selectedNode
					.getParent();
			this.treeModel.removeNodeFromParent(parentNode);
			removeExtraSpoilers();
		}
	}

	private void rename() {
		ElementData elementData = (ElementData) this.selectedNode
				.getUserObject();
		String oldName = elementData.getName();
		while (true) {
			String newName = (String) JOptionPane.showInputDialog(null,
					DocumentAttributesListConsts.RENAME_DIALOG_PROMPT,
					DocumentAttributesListConsts.RENAME_DIALOG_TITLE,
					JOptionPane.QUESTION_MESSAGE, null, null, oldName);
			if (newName == null) {
				break;
			} else if ("".equals(newName)) {
				JOptionPane
						.showMessageDialog(
								null,
								DocumentAttributesListConsts.RENAME_DIALOG_NAME_EMPTY_ERROR,
								DocumentAttributesListConsts.RENAME_DIALOG_TITLE,
								JOptionPane.ERROR_MESSAGE);
			} else {
				elementData.setName(newName);
				this.selectedNode.setUserObject(elementData);
				this.treeModel.nodeChanged(this.selectedNode);
				break;
			}
		}
	}

	private void editOrSetValue(boolean set) {
		ElementData elementData = (ElementData) this.selectedNode
				.getUserObject();
		String oldValue = elementData.getValue();
		String prompt = "";
		String title = "";
		String[] values = null;
		String name = ("".equals(elementData.getName()) ? ":" : " '"
				+ elementData.getName() + "':");
		if (set) {
			prompt = DocumentAttributesListConsts.EDIT_DIALOG_SET_VALUE_PROMPT
					+ name;
			title = DocumentAttributesListConsts.EDIT_DIALOG_SET_VALUE_TITLE;
		} else if (DocumentAttributesListConsts.JSON_TYPE_BOOL_ELEMENT
				.equals(elementData.getType())) {
			prompt = DocumentAttributesListConsts.EDIT_DIALOG_SELECT_VALUE_PROMPT
					+ name;
			title = DocumentAttributesListConsts.EDIT_DIALOG_SELECT_VALUE_TITLE;
			values = new String[] {
					DocumentAttributesListConsts.BOOL_TRUE_VALUE_TEXT,
					DocumentAttributesListConsts.BOOL_FALSE_VALUE_TEXT };
		} else {
			prompt = DocumentAttributesListConsts.EDIT_DIALOG_ENTER_VALUE_PROMPT
					+ name;
			title = DocumentAttributesListConsts.EDIT_DIALOG_ENTER_VALUE_TITLE;
		}
		while (true) {
			String newValue = (String) JOptionPane.showInputDialog(null,
					prompt, title, JOptionPane.QUESTION_MESSAGE, null, values,
					oldValue);
			if (newValue == null) {
				break;
			} else if ("".equals(newValue)) {
				JOptionPane
						.showMessageDialog(
								null,
								DocumentAttributesListConsts.EDIT_DIALOG_VALUE_EMPTY_ERROR,
								title, JOptionPane.ERROR_MESSAGE);
			} else {
				if (DocumentAttributesListConsts.JSON_TYPE_INT_ELEMENT
						.equals(elementData.getType())) {
					try {
						Integer.parseInt(newValue);
					} catch (NumberFormatException e) {
						JOptionPane
								.showMessageDialog(
										null,
										DocumentAttributesListConsts.EDIT_DIALOG_VALUE_NOT_INTEGER_ERROR,
										title, JOptionPane.ERROR_MESSAGE);
						continue;
					}
				}
				elementData.setValue(newValue);
				if (set) {
					elementData.setType(getItemType(newValue));
				}
				this.selectedNode.setUserObject(elementData);
				this.treeModel.nodeChanged(this.selectedNode);
				break;
			}
		}
	}

	private void clearValue() {
		int rc = JOptionPane.showOptionDialog(null,
				DocumentAttributesListConsts.CLEAR_VALUE_CONFIRMATION_TEXT,
				DocumentAttributesListConsts.CLEAR_VALUE_CONFIRMATION_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				null, null);
		if (JOptionPane.YES_OPTION == rc) {
			ElementData elementData = (ElementData) this.selectedNode
					.getUserObject();
			elementData.setEmptyValue();
			elementData
					.setType(DocumentAttributesListConsts.JSON_TYPE_NULL_ELEMENT);
			this.selectedNode.setUserObject(elementData);
			this.treeModel.nodeChanged(this.selectedNode);
		}
	}

	private Integer getNewObjectIndex(DefaultMutableTreeNode root) {
		/*
		 * TODO Here instead of all this stuff I want to get and return an id
		 * for the new document (for example
		 * "return com.xlab.glodocs.api.db.Database.getNextGlobalsId()"
		 */
		DefaultMutableTreeNode lastNode = root.getLastLeaf();
		if (lastNode.isRoot()) {
			return 0;
		}
		String lastObjectKey = ((ElementData) lastNode.getUserObject())
				.getKey();
		int lastIndex = Integer.parseInt(lastObjectKey);
		return lastIndex + 1;
	}

	private int getInsertIndex() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeModel
				.getRoot();
		Enumeration<DefaultMutableTreeNode> rootChildren = root.children();
		boolean searchSpoiler = false;
		int i = 0;
		while (rootChildren.hasMoreElements()) {
			ElementData childData = (ElementData) (rootChildren.nextElement())
					.getUserObject();
			if (!searchSpoiler) {
				if (lastJSONObjectID.equals(childData.getKey())) {
					searchSpoiler = true;
				}
			} else {
				if (DocumentAttributesListConsts.SPOILER.equals(childData
						.getType())) {
					return i;
				}
			}
			i++;
		}
		return root.getChildCount();
	}

	private void deleteObjectFromTree() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeModel
				.getRoot();
		Enumeration<DefaultMutableTreeNode> rootChildren = root.children();
		while (rootChildren.hasMoreElements()) {
			DefaultMutableTreeNode child = rootChildren.nextElement();
			if (this.lastJSONObjectID.equals(((ElementData) child
					.getUserObject()).getKey())) {
				this.treeModel.removeNodeFromParent(child);
				rootChildren = root.children();
			}
		}
		removeExtraSpoilers();
	}

	private void removeExtraSpoilers() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeModel
				.getRoot();
		if (root.getChildCount() != 0) {
			DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) root
					.getFirstChild();
			if (DocumentAttributesListConsts.SPOILER
					.equals(((ElementData) firstChild.getUserObject())
							.getType())) {
				this.treeModel.removeNodeFromParent(firstChild);
			}
		}
		if (root.getChildCount() != 0) {
			DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode) root
					.getLastChild();
			if (DocumentAttributesListConsts.SPOILER
					.equals(((ElementData) lastChild.getUserObject()).getType())) {
				this.treeModel.removeNodeFromParent(lastChild);
			}
		}
		boolean secondSpoiler = false;
		Enumeration<DefaultMutableTreeNode> rootChildren = root.children();
		while (rootChildren.hasMoreElements()) {
			DefaultMutableTreeNode child = rootChildren.nextElement();
			if (DocumentAttributesListConsts.SPOILER
					.equals(((ElementData) child.getUserObject()).getType())) {
				if (!secondSpoiler) {
					secondSpoiler = true;
				} else {
					this.treeModel.removeNodeFromParent(child);
				}
			}
		}
	}

	private String getItemType(String newValue) {
		try {
			Integer.parseInt(newValue);
			return DocumentAttributesListConsts.JSON_TYPE_INT_ELEMENT;
		} catch (NumberFormatException e) {
			// Do nothing here
		}
		if (DocumentAttributesListConsts.BOOL_TRUE_VALUE_TEXT.equals(newValue
				.toLowerCase())
				|| DocumentAttributesListConsts.BOOL_FALSE_VALUE_TEXT
						.equals(newValue.toLowerCase())) {
			return DocumentAttributesListConsts.JSON_TYPE_BOOL_ELEMENT;
		}
		return DocumentAttributesListConsts.JSON_TYPE_STRING_ELEMENT;
	}
}
