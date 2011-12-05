import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Sun Dec 04 12:27:29 GMT 2011
 */



/**
 * @author Graham Vincent
 */
public class layout extends JFrame {
	public layout() {
		initComponents();
	}

	public static void main(String args[]) {
       layout l = new layout();
    }

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Graham Vincent
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		iptxt = new JTextField();
		Find = new JButton();
		label2 = new JLabel();
		datetxt = new JTextField();
		ReadLog = new JButton();
		label3 = new JLabel();
		porttxt = new JTextField();
		Next = new JButton();
		Prev = new JButton();
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();
		buttonBar = new JPanel();
		okButton = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

			// JFormDesigner evaluation mark
			dialogPane.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), dialogPane.getBorder())); dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new GridBagLayout());
				((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 95, 303, 326, 0};
				((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 189, 0};
				((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
				((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

				//---- label1 ----
				label1.setText("IP Address");
				contentPanel.add(label1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				contentPanel.add(iptxt, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- Find ----
				Find.setText("Find");
				contentPanel.add(Find, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label2 ----
				label2.setText("Date");
				contentPanel.add(label2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				contentPanel.add(datetxt, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- ReadLog ----
				ReadLog.setText("Read log");
				contentPanel.add(ReadLog, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label3 ----
				label3.setText("Port number");
				contentPanel.add(label3, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				contentPanel.add(porttxt, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- Next ----
				Next.setText("Next");
				contentPanel.add(Next, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- Prev ----
				Prev.setText("Prev");
				contentPanel.add(Prev, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//======== scrollPane1 ========
				{

					//---- textArea1 ----
					textArea1.setEditable(false);
					scrollPane1.setViewportView(textArea1);
				}
				contentPanel.add(scrollPane1, new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

				//---- okButton ----
				okButton.setText("OK");
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Graham Vincent
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label1;
	private JTextField iptxt;
	private JButton Find;
	private JLabel label2;
	private JTextField datetxt;
	private JButton ReadLog;
	private JLabel label3;
	private JTextField porttxt;
	private JButton Next;
	private JButton Prev;
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private JPanel buttonBar;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
