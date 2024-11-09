package org.mql.java.umlgen.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.mql.java.umlgen.exceptions.ProjectNotValidException;
import org.mql.java.umlgen.models.ProjectModel;

public class HomePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_HEIGHT = 350;
	private static final int DEFAULT_WIDTH = 650;
	
	private int width;
	private int height;
	
	private ActionListener loadListener;
	
	private ProjectModel project;
	private ProjectChooser chooser;
	private ProjectPanel info;
	
	private GridLayout layout;
	
	public HomePanel(int width, int height) {
		initListener();
		this.width = width;
		this.height = height;
		layout = new GridLayout(1,2);
		chooser = new ProjectChooser(loadListener);
		info = new ProjectPanel();
		setLayout(layout);
		add(chooser);
		add(info);
	}

	public HomePanel() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	private void initListener() {
		loadListener = (e) -> {
			try {
				project = new ProjectModel(chooser.getPath());
				info.updateProject(project);
			} catch (ProjectNotValidException e1) {
				new ErrorDialog(e1.getMessage());
				e1.printStackTrace();
			}
		};
	}

}
