package org.mql.java.umlgen.ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorDialog {

	public ErrorDialog(String errorMsg) {
		JOptionPane.showMessageDialog(new JFrame(), errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
	}

}
