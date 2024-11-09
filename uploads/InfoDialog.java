package org.mql.java.umlgen.ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class InfoDialog {

	public InfoDialog(String msg) {
		JOptionPane.showMessageDialog(new JFrame(), msg, "Info", JOptionPane.INFORMATION_MESSAGE);
	}

}
