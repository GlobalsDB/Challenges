package com.xlab.glodocs.gui.main;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel;

import com.xlab.glodocs.gui.Window.Winda;
import com.xlab.glodocs.gui.frames.LoginWin;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String home = System.getenv("GLOBALS_HOME");
		if (home == null) {
			System.out.println("GLOBALS_HOME is not set");
			return;
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SubstanceLookAndFeel laf = new SubstanceGeminiLookAndFeel();
					try {
						UIManager.setLookAndFeel(laf);
						// JFrame.setDefaultLookAndFeelDecorated(true);
						JDialog.setDefaultLookAndFeelDecorated(true);
						UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS,
								Boolean.TRUE);
					} catch (Throwable e1) {
						e1.printStackTrace();
					}
					final LoginWin logWin = LoginWin.getInstance();
					final Winda frame = Winda.getSingleton();
					logWin.setVisible(true);

					frame.addWindowListener(new WindowListener() {

						@Override
						public void windowOpened(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowIconified(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowDeiconified(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowDeactivated(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowClosing(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowClosed(WindowEvent arg0) {
							System.out.println("closed event");
							logWin.dispose();
							frame.dispose();
							System.exit(0);

						}

						@Override
						public void windowActivated(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}
					});

					// frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
