package com.xlab.glodocs.gui.Window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.json.JSONException;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Document;
import com.xlab.glodocs.api.utils.GlodocsException;
import com.xlab.glodocs.gui.Configuration;
import com.xlab.glodocs.gui.frames.CollectionPanel;
import com.xlab.glodocs.gui.frames.DocumentPanel;
import com.xlab.glodocs.gui.frames.JournalPanel;
import com.xlab.glodocs.gui.frames.LoginWin;
import com.xlab.glodocs.gui.frames.SearchPanel;
import com.xlab.glodocs.gui.frames.UserManagingPanel;
import com.xlab.glodocs.gui.frames.UserParametersPanel;

@SuppressWarnings("serial")
public class Winda extends JFrame {

	private JSplitPane mainSplitPane;
	public static JTabbedPane tabbedPane;
	public static int indexVisible = -1;
	public static String s;
	public static String s1;
	public static String s2;
	private HashMap<String, Collection> collections = new HashMap<String, Collection>();
	private JList collectList;

	public Object[] getCollectList() {
		return collections.keySet().toArray();
	}

	private JList docsList;
	private HashMap<String, Document> documents = new HashMap<String, Document>();

	private final static String iconsDirectory = "images/";

	private static Winda singleton = null;

	public static Winda getSingleton() {
		if (singleton == null)
			singleton = new Winda();
		return singleton;
	}

	/**
	 * Create the application.
	 */
	public Winda() {
		super();
		setTitle(Configuration.localization.getString("glodocsTitle"));
		initialize();
		setVisible(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// AuthorizationFrame frame = AuthorizationFrame.getSingleton(this);
		// frame.setVisible(true);

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				iconsDirectory + "Graphite Globe.png"));
		setBounds(100, 100, 980, 731);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		toolBar.setBackground(SystemColor.inactiveCaptionBorder);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton newCollectionToolbarButton = new JButton("");
		newCollectionToolbarButton.setToolTipText(Configuration.localization
				.getString("newCollectionTitle"));
		newCollectionToolbarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addTab(new CollectionPanel(), Configuration.localization
						.getString("newCollectionTitle"));

			}
		});
		newCollectionToolbarButton.setIcon(new ImageIcon(iconsDirectory
				+ "new archive.png"));
		toolBar.add(newCollectionToolbarButton);

		JButton newDocumentButton = new JButton("");
		newDocumentButton
				.setIcon(new ImageIcon(iconsDirectory + "new doc.png"));
		newDocumentButton.setToolTipText(Configuration.localization
				.getString("newDocTitle"));
		newDocumentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addTab(new DocumentPanel(),
						Configuration.localization.getString("newDocTitle"));

			}
		});
		toolBar.add(newDocumentButton);

		JButton propertiesButton = new JButton("");
		propertiesButton
				.setIcon(new ImageIcon(iconsDirectory + "advanced.png"));
		propertiesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTab(new UserParametersPanel(), DBConnection.getLoggedUser()
						.getLogin());

			}
		});
		toolBar.add(propertiesButton);

		JButton userManagingButton = new JButton("");
		userManagingButton.setIcon(new ImageIcon(iconsDirectory + "users.png"));
		userManagingButton.setToolTipText(Configuration.localization
				.getString("userManaging"));
		userManagingButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTab(new UserManagingPanel(),
						Configuration.localization.getString("userManaging"));
			}
		});
		toolBar.add(userManagingButton);

		JButton findButton = new JButton("");
		findButton.setIcon(new ImageIcon(iconsDirectory + "find.png"));
		findButton.setToolTipText(Configuration.localization
				.getString("search"));
		findButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTab(new SearchPanel(),
						Configuration.localization.getString("search"));

			}
		});
		toolBar.add(findButton);

		JButton journalButton = new JButton("");
		journalButton.setIcon(new ImageIcon(iconsDirectory + "journal.png"));
		journalButton.setToolTipText(Configuration.localization
				.getString("journal"));
		journalButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTab(new JournalPanel(),
						Configuration.localization.getString("journal"));

			}
		});
		toolBar.add(journalButton);

		JButton helpButton = new JButton("");
		helpButton.setIcon(new ImageIcon(iconsDirectory + "help.png"));
		toolBar.add(helpButton);

		JButton logoffButton = new JButton("");
		logoffButton.setIcon(new ImageIcon(iconsDirectory + "logoff.png"));
		logoffButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LoginWin.getInstance().setVisible(true);
				Winda.getSingleton().setVisible(false);

			}
		});
		toolBar.add(logoffButton);

		JButton exitButton = new JButton("");
		exitButton.setIcon(new ImageIcon(iconsDirectory + "exit.png"));
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				singleton.dispose();

			}
		});
		toolBar.add(exitButton);

		toolBar.addSeparator();
		toolBar.add(new JLabel("                                   "));
		toolBar.addSeparator();

		JButton englishButton = new JButton("");
		englishButton.setIcon(new ImageIcon(iconsDirectory + "en.png"));
		englishButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Configuration.currentLocale = Configuration.locales[0];
				Configuration.localization = ResourceBundle.getBundle(
						"com.xlab.glodocs.gui.localization.Localization",
						Configuration.currentLocale);
				singleton.validateTree();

			}
		});
		toolBar.add(englishButton);

		JButton russianButton = new JButton("");
		russianButton.setIcon(new ImageIcon(iconsDirectory + "ru.png"));
		russianButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Configuration.currentLocale = Configuration.locales[1];
				Configuration.localization = ResourceBundle.getBundle(
						"com.xlab.glodocs.gui.localization.Localization",
						Configuration.currentLocale);
				singleton.validateTree();

			}
		});
		toolBar.add(russianButton);

		/**
		 * SplitPane needed
		 */

		mainSplitPane = new JSplitPane();
		mainSplitPane.setDividerLocation(300);
		mainSplitPane.setBackground(UIManager.getColor("Button.highlight"));
		mainSplitPane
				.setForeground(UIManager.getColor("Button.disabledShadow"));
		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		mainSplitPane.setPreferredSize(new Dimension(580, 200));

		JSplitPane leftSplit = new JSplitPane();

		// layeredPane.setLayer(layeredPane_7, 1);
		leftSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		leftSplit.setBounds(0, 0, 284, 207);
		leftSplit.setDividerLocation(250);
		mainSplitPane.setLeftComponent(leftSplit);

		JPanel collectionPanel = new JPanel();
		collectionPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
		collectionPanel.setForeground(Color.WHITE);
		collectionPanel.setBackground(Color.WHITE);
		// collectionPanel.setLayout(new GridLayout(0, 1, 0, 0));
		MigLayout layout = new MigLayout("flowx", "[]15[]15[]",
				"[c,pref!]15[c,pref!]30[c,grow 80,fill]");

		collectionPanel.setLayout(layout);

		JLabel collectionsLabel = new JLabel(
				Configuration.localization.getString("collections"));
		collectionsLabel.setSize(collectionsLabel.getPreferredSize());

		JButton newCollectionButton = new JButton(
				Configuration.localization.getString("newCollection"));
		newCollectionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTab(new CollectionPanel(), Configuration.localization
						.getString("newCollectionTitle"));

			}
		});
		newCollectionButton.setFont(new Font("Tahoma", Font.PLAIN, 10));

		final JButton editCollectionButton = new JButton(
				Configuration.localization.getString("edit"));
		editCollectionButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		editCollectionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CollectionPanel pan = new CollectionPanel();
				pan.setSource(collections.get(collectList.getSelectedValue()
						.toString()));
				addTab(pan, collectList.getSelectedValue().toString());

			}
		});
		editCollectionButton.setEnabled(false);

		final JButton deleteCollectionButton = new JButton(
				Configuration.localization.getString("delete"));
		deleteCollectionButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
		deleteCollectionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					DBConnection.getDatabase().removeCollectionById(
							collectList.getSelectedValue().toString());
				} catch (GlodocsException e) {
					e.printStackTrace();
				}
				loadData();

			}
		});
		deleteCollectionButton.setEnabled(false);

		final JButton newDocument = new JButton(
				Configuration.localization.getString("newDoc"));
		final JButton removeAll = new JButton(
				Configuration.localization.getString("removeAll"));

		collectList = new JList(new DefaultComboBoxModel());
		collectList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				try {
					if (collectList.getSelectedValue() == null) {
						editCollectionButton.setEnabled(false);
						deleteCollectionButton.setEnabled(false);
						removeAll.setEnabled(false);
					} else {
						editCollectionButton.setEnabled(true);
						deleteCollectionButton.setEnabled(true);
						newDocument.setEnabled(true);
						removeAll.setEnabled(true);
						loadDocuments(collections.get(collectList
								.getSelectedValue().toString()));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});

		collectList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = collectList.locationToIndex(e.getPoint());
					ListModel dlm = collectList.getModel();
					Object item = dlm.getElementAt(index);
					collectList.ensureIndexIsVisible(index);
					CollectionPanel pan = new CollectionPanel();
					pan.setSource(collections.get(item.toString()));
					addTab(pan, item.toString());
				}
			}
		});

		JScrollPane collectScroll = new JScrollPane(collectList);

		// collectionPanel.setLayout(null);
		collectionPanel.add(collectionsLabel, "wrap");
		collectionPanel.add(newCollectionButton);
		collectionPanel.add(editCollectionButton);
		collectionPanel.add(deleteCollectionButton, "wrap");
		collectionPanel.add(collectScroll, "span 3, growx");
		// leftSplit.add(textArea);

		MigLayout layout2 = new MigLayout("flowx", "[]15[]15[]",
				"[c,pref!]15[c,pref!]30[c,grow 80,fill]");

		leftSplit.setTopComponent(collectionPanel);

		JPanel documentsListPanel = new JPanel();
		documentsListPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
		documentsListPanel.setForeground(Color.WHITE);
		documentsListPanel.setBackground(Color.WHITE);
		documentsListPanel.setLayout(layout2);

		JLabel documentsLabel = new JLabel(
				Configuration.localization.getString("documents"));

		newDocument.setFont(new Font("Tahoma", Font.PLAIN, 10));
		newDocument.setEnabled(false);
		newDocument.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DocumentPanel pan = new DocumentPanel();
				pan.setCollection(collectList.getSelectedValue().toString());
				addTab(pan, Configuration.localization.getString("newDocTitle"));

			}
		});

		final JButton removeDoc = new JButton(
				Configuration.localization.getString("removeDoc"));
		removeDoc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		removeDoc.setEnabled(false);
		removeDoc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					collections.get(collectList.getSelectedValue().toString())
							.removeDocumentById(
									docsList.getSelectedValue().toString());

					loadDocuments(collections.get(collectList
							.getSelectedValue().toString()));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (GlodocsException e) {
					e.printStackTrace();
				}

			}
		});

		removeAll.setEnabled(false);
		removeAll.setFont(new Font("Tahoma", Font.PLAIN, 10));
		removeAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {

					collections.get(collectList.getSelectedValue().toString())
							.clear();
					((DefaultComboBoxModel) docsList.getModel())
							.removeAllElements();
				} catch (GlodocsException e) {
					e.printStackTrace();
				}

			}
		});

		docsList = new JList(new DefaultComboBoxModel());
		docsList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (docsList.getSelectedValue() == null) {
					removeDoc.setEnabled(false);
				} else {
					removeDoc.setEnabled(true);
				}

			}
		});
		docsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = docsList.locationToIndex(e.getPoint());
					ListModel dlm = docsList.getModel();
					Object item = dlm.getElementAt(index);
					docsList.ensureIndexIsVisible(index);
					DocumentPanel pan = new DocumentPanel(documents.get(item
							.toString()));
					// pan.setSource(documents.get(item.toString()));
					addTab(pan, item.toString());
				}
			}
		});

		JScrollPane docsScroll = new JScrollPane(docsList);

		documentsListPanel.add(documentsLabel, "wrap");
		documentsListPanel.add(newDocument);
		documentsListPanel.add(removeDoc);
		documentsListPanel.add(removeAll, "wrap");
		documentsListPanel.add(docsScroll, "span 3, growx");

		leftSplit.setBottomComponent(documentsListPanel);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				System.out.println("stateChange");
				System.out.println("indexVisible=" + indexVisible);
				indexVisible = tabbedPane.getSelectedIndex();
				System.out.println("indexVisible=" + indexVisible);

			}
		});

		mainSplitPane.setRightComponent(tabbedPane);

	}

	public static void addTab(final JPanel panel, String title) {
		// �������� �������
		indexVisible = tabbedPane.getTabCount();
		tabbedPane.add(panel);
		JPanel pan = new JPanel();
		MigLayout layout = new MigLayout("", "[]5[]", "[c,pref!]");
		pan.setLayout(layout);
		pan.add(new JLabel(title));
		JButton but = new JButton("x");
		but.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tabbedPane.remove(panel);
			}
		});

		pan.add(but, "w 25:25:25");
		tabbedPane.setTabComponentAt(indexVisible, pan);
		/*
		 * tabbedPane.insertTab(title, new ImageIcon(iconsDirectory +
		 * "close.png"), panel, "", indexVisible);
		 */
		/*
		 * tabbedPane.setTabComponentAt(tabbedPane.countComponents() - 1, new
		 * JButton("ss"));
		 */
		tabbedPane.setSelectedIndex(indexVisible);

	}

	public void loadData() {
		DefaultComboBoxModel model = (DefaultComboBoxModel) docsList.getModel();
		model.removeAllElements();
		documents.clear();

		model = (DefaultComboBoxModel) collectList.getModel();
		collections = DBConnection.getDatabase().getCollections();
		Iterator<String> iter = collections.keySet().iterator();
		model.removeAllElements();
		while (iter.hasNext()) {
			model.addElement(iter.next());
		}

	}

	public void loadDocuments(Collection collection) throws JSONException,
			ParseException {
		if (!collectList.getSelectedValue().toString()
				.equals(collection.getId()))
			collectList.setSelectedValue(collection.getId(), true);
		DefaultComboBoxModel model = (DefaultComboBoxModel) docsList.getModel();
		documents = collection.getDocuments();
		Iterator<String> iter = documents.keySet().iterator();
		model.removeAllElements();
		while (iter.hasNext()) {
			model.addElement(iter.next());
		}
	}
}
