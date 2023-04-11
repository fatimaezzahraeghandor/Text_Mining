package tp1_moteur_rechairche;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import javax.swing.JButton;
import javax.swing.JEditorPane;

import java.awt.Font;

import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.event.ActionEvent;

public class TFIDF_Swing extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JFrame frame2;
	private JTextField SearchTextField;
	private JPasswordField passwordfield;
	private JPanel jpane;
	private JTable table;
	private JPanel contentPane;

	void fermer() {
		frame.dispose();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					TFIDF_Swing frame = new TFIDF_Swing();
					frame.frame.setVisible(true);
					frame.frame.setLocationRelativeTo(null);
					frame.frame.setTitle("textcherche");
					frame.repaint(); // refresh the Frame
					frame.setBackground(new Color(25,25,25));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TFIDF_Swing() {

		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(50, 50,600, 600);
		frame.getContentPane().setLayout(null);
		frame.setBackground(new Color(194, 214, 214));

		SearchTextField = new JTextField();
		SearchTextField.setBounds(45, 5, 500, 40);
		SearchTextField.setColumns(10);
		SearchTextField.setToolTipText("search ");
		SearchTextField.setBackground(new Color(133, 173, 173));
		frame.getContentPane().add(SearchTextField);

		JButton RechercheButton = new JButton("lancer");
		RechercheButton.setBackground(new Color(148, 184, 184));
		RechercheButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String username = SearchTextField.getText().toString();

				if (username.equals("")) {
					JOptionPane.showMessageDialog(null, " Enter some thing ! ");

				} else {

					TFIDF t = new TFIDF();
					Map<String, Double> listOfCos = t.tifidf_Search(t, username);
					DefaultListModel<String> listCos = new DefaultListModel<>();

					for (Entry<String, Double> entry_maps : listOfCos.entrySet()) {
						if (entry_maps.getValue() > 0 && entry_maps.getValue() <= 1)
							listCos.addElement(entry_maps.getKey());
					}

					if (listCos.size() == 0) {
						JOptionPane.showMessageDialog(null, "noting");
					} else {

						JList<String> list2 = new JList<>(listCos);
						list2.setBounds(10,10 , 200, 20);
						list2.setBackground(new Color(209, 224, 224));
						JScrollPane scrollPane = new JScrollPane();
						scrollPane.setBounds(45, 90, 500, 600);
						frame.getContentPane().add(scrollPane);
					scrollPane.setViewportView(list2);

					}
				}

			}
		});
		RechercheButton.setBounds(45, 50, 500, 30);
		frame.getContentPane().add(RechercheButton);

	}
}
