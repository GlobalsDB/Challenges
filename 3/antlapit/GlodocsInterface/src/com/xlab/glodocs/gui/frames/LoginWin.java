package com.xlab.glodocs.gui.frames;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.gui.Configuration;
import com.xlab.glodocs.gui.Window.Winda;

@SuppressWarnings("serial")
public class LoginWin extends JDialog {

	private JTextField loginField;
	private JPasswordField passwordField;

	public static LoginWin singleton;
	public String login, pass;

	public static LoginWin getInstance() {
		if (singleton == null)
			singleton = new LoginWin();
		return singleton;
	}

	public LoginWin() {
		super();
		setTitle(Configuration.localization.getString("loginWindow"));
		setSize(new Dimension(250, 150));
		MigLayout layout = new MigLayout("flowx", "[]15[]",
				"[c,pref!]15[c,pref!]15[c,pref!]");
		setLayout(layout);
		setFont(new Font("Serif", Font.PLAIN, 14));

		JLabel jh = new JLabel(Configuration.localization.getString("login")); // �����
		add(jh);

		loginField = new JTextField(25);
		add(loginField, "wrap");

		JLabel jhh = new JLabel(
				Configuration.localization.getString("password")); // ������
		add(jhh);

		passwordField = new JPasswordField(25);
		add(passwordField, "wrap");

		loginField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				login = loginField.getText();
			}
		});

		passwordField.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent ae) {
				pass = passwordField.getText();
			}

		});

		passwordField.addFocusListener(new FocusAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				pass = passwordField.getText();
			}
		});

		JButton bl = new JButton(
				Configuration.localization.getString("apply_title")); // ���������
		bl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				login();
			}

		});

		this.getRootPane().setDefaultButton(bl);

		add(bl);

		JButton b2 = new JButton(
				Configuration.localization.getString("cancel_title"));
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loginField.setText("");
				passwordField.setText("");
			}
		});
		add(b2);

	}

	protected void login() {
		try {
			System.out.println(login + " " + pass);
			if (login == null)
				login = "";
			if (pass == null)
				pass = "";

			if (login.isEmpty() && pass.isEmpty()) {
				JOptionPane.showMessageDialog(singleton,
						Configuration.localization.getString("errorLog"),
						Configuration.localization.getString("error_title"),
						JOptionPane.ERROR_MESSAGE);
			} else {
				if (login.isEmpty()) {
					if (pass.isEmpty()) {
						JOptionPane.showMessageDialog(singleton,
								Configuration.localization
										.getString("loginAndPassword"),
								Configuration.localization
										.getString("error_title"),
								JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(singleton,
								Configuration.localization
										.getString("notLogin"),
								Configuration.localization
										.getString("error_title"),
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					if (pass.isEmpty()) {
						JOptionPane.showMessageDialog(singleton,
								Configuration.localization
										.getString("notPassword"),
								Configuration.localization
										.getString("error_title"),
								JOptionPane.ERROR_MESSAGE);
					} else {
						DBConnection conn = DBConnection.getInstance(login, pass);
						//conn.login(login, pass);
						dispose();
						Winda.getSingleton().setVisible(true);
						passwordField.setText("");
						pass = "";
						Winda.getSingleton().loadData();
					}
				}
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(singleton, e.getMessage(),
					Configuration.localization.getString("error_title"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

}
