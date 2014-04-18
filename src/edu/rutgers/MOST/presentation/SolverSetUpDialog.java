package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.data.SettingsFactory;

public class SolverSetUpDialog  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JLabel solverSelectionLabel = new JLabel(GraphicalInterfaceConstants.SOLVER_SELECTION_LABEL);
	private JRadioButton glpkRadioButton = new JRadioButton(GraphicalInterfaceConstants.GLPK_SOLVER_BUTTON_LABEL);
	private JRadioButton gurobiRadioButton = new JRadioButton(GraphicalInterfaceConstants.GUROBI_SOLVER_BUTTON_LABEL);
	public static JButton fileButton = new JButton(GraphicalInterfaceConstants.GUROBI_JAR_PATH_BUTTON);
	public static JButton okButton = new JButton("    OK    ");
	public static JButton cancelButton = new JButton("  Cancel  ");
	public static JButton clearButton = new JButton("Clear");
	public static final JTextField textField = new JTextField();
	public static JLabel gurobiLabel = new JLabel();
	
	//Methods of saving current directory
	public static SettingsFactory curSettings;
	
	private String path;

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public boolean fileSelected;
	
	public SolverSetUpDialog() {

		setTitle(GraphicalInterfaceConstants.SOLVER_DIALOG_TITLE);		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getRootPane().setDefaultButton(okButton);
		
		disableGurobiComponents();
		
		//textField.setText("");

	    fileSelected = false;
		
		//box layout
		Box vb = Box.createVerticalBox();
		Box hbSolverSelection = Box.createHorizontalBox();
		Box glpkButtonBox = Box.createHorizontalBox();   
		Box gurobiButtonBox = Box.createHorizontalBox();	
		Box hbGurobiLabel = Box.createHorizontalBox();
		Box hbMetab = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		
		solverSelectionLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		solverSelectionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		//solverSelectionLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel solverSelectionPanel = new JPanel();
		solverSelectionPanel.setLayout(new BoxLayout(solverSelectionPanel, BoxLayout.X_AXIS));
		solverSelectionPanel.add(solverSelectionLabel);
		solverSelectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		solverSelectionLabel.setMinimumSize(new Dimension(200, 15));
		
		JPanel glpkButtonPanel = new JPanel();
		glpkButtonPanel.setLayout(new BoxLayout(glpkButtonPanel, BoxLayout.X_AXIS));
		glpkButtonPanel.add(glpkRadioButton);
		glpkButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		
		JPanel gurobiButtonPanel = new JPanel();
		gurobiButtonPanel.setLayout(new BoxLayout(gurobiButtonPanel, BoxLayout.X_AXIS));
		gurobiButtonPanel.add(gurobiRadioButton);
		gurobiButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		glpkRadioButton.setMnemonic(KeyEvent.VK_L);
		gurobiRadioButton.setMnemonic(KeyEvent.VK_U);
		
		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(glpkRadioButton);
		group.add(gurobiRadioButton);
		glpkRadioButton.setSelected(true);
		
		hbSolverSelection.add(solverSelectionPanel);		
		hbSolverSelection.add(glpkButtonPanel);
		hbSolverSelection.add(gurobiButtonPanel);

		gurobiLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		gurobiLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		gurobiLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(gurobiLabel);
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		hbGurobiLabel.add(labelPanel);
		
		gurobiLabel.setMinimumSize(new Dimension(200, 15));
		textField.setEditable(false);
		textField.setBackground(Color.white);

		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setEnabled(false);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);

		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				enableOKButton();
			}
			public void removeUpdate(DocumentEvent e) {
				enableOKButton();
			}
			public void insertUpdate(DocumentEvent e) {
				enableOKButton();
			}

			public void enableOKButton() {
				if (textField.getText() != null && textField.getText().length() > 0) {
					okButton.setEnabled(true);
					//LocalConfig.getInstance().hasMetabolitesFile = true;
				} else {
					okButton.setEnabled(false);
					//LocalConfig.getInstance().hasMetabolitesFile = false;
				}
			}
		});
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.add(textField);
		textPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

		JLabel blank2 = new JLabel("      ");
		JLabel blank3 = new JLabel("      ");
		
		fileButton.setMnemonic(KeyEvent.VK_G);
		clearButton.setMnemonic(KeyEvent.VK_E);
		
		hbMetab.add(blank2);
		hbMetab.add(fileButton);
		hbMetab.add(textPanel);
		hbMetab.add(clearButton);
		hbMetab.add(blank3);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,20,15,20));

		hbButton.add(buttonPanel);

		vb.add(hbSolverSelection);
		vb.add(hbGurobiLabel);
		vb.add(hbMetab);
		vb.add(hbButton);
		add(vb);	
		
		glpkRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disableGurobiComponents();
			}
		});
		
		gurobiRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableGurobiComponents();
			}
		});
		
		ActionListener fileButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				
			}
		};
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				
			}
		};
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				
			}
		}; 
		
		ActionListener clearButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				textField.setText("");	 
			}
		}; 
		
		fileButton.addActionListener(fileButtonActionListener);
		okButton.addActionListener(okButtonActionListener);
		cancelButton.addActionListener(cancelButtonActionListener);
		clearButton.addActionListener(clearButtonActionListener);
		
	} 	
	
	public void enableGurobiComponents() {
		fileButton.setEnabled(true);
		clearButton.setEnabled(true);
		textField.setForeground(Color.BLACK);
		gurobiLabel.setForeground(Color.BLACK);
	}
	
	public void disableGurobiComponents() {
		fileButton.setEnabled(false);
		clearButton.setEnabled(false);
		textField.setForeground(GraphicalInterfaceConstants.FORMULA_BAR_NONEDITABLE_COLOR);
		gurobiLabel.setForeground(GraphicalInterfaceConstants.FORMULA_BAR_NONEDITABLE_COLOR);
	}
	
	public static void main(String[] args) throws Exception {
		curSettings = new SettingsFactory();
		
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		String lastGurobi_path = curSettings.get("LastGurobi");
		if (lastGurobi_path == null) {
			SolverSetUpDialog frame = new SolverSetUpDialog();
			frame.setIconImages(icons);
			frame.setSize(GraphicalInterfaceConstants.SOLVER_DIALOG_WIDTH, GraphicalInterfaceConstants.SOLVER_DIALOG_HEIGHT);
			frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} else {
			System.out.println("gui load");
		}
	}
}









