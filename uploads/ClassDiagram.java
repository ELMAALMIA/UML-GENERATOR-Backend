package org.mql.java.umlgen.ui;

import static java.lang.Math.max;
import static org.mql.java.umlgen.utils.MathUtils.max;
import static org.mql.java.umlgen.utils.MathUtils.sum;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mql.java.umlgen.models.ClassModel;
import org.mql.java.umlgen.models.InterfaceModel;
import org.mql.java.umlgen.models.ProjectContext;
import org.mql.java.umlgen.models.ProjectModel;
import org.mql.java.umlgen.models.RelationModel;
import org.mql.java.umlgen.utils.UIUtils;

public class ClassDiagram extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_HEIGHT = 600;
	private static final int DEFAULT_WIDTH = 800;
	private static final int MARGIN = 50;
	private static final int SPACER_X = 50;
	private static final int SPACER_Y = 200;
	private static final int EXTRA_WIDTH = 50;
	private int extraSpacer;
	private int height;
	private int width;
	private int currentPrintingX;
	private int currentPrintingY;
	private boolean isColored;
	
	private int classRows;
	private int currentClassLevel;
	
	private ProjectModel project;
	private ProjectContext projectContext;
	
	private int classSectionStartingX;
	private int classSectionStartingY;
	
	private int interfaceSectionStartingX;
	private int interfaceSectionStartingY;
	private int interfaceSectionWidth;
	private int interfaceSectionHeight;
	
	private int relationBreakPointX, relationBreakPointY;
	
	private Collection<ClassModel> projectClasses;
	private Collection<InterfaceModel> projectInterfaces;
	private List<RelationModel> projectAssociations;
	private List<RelationModel> drawnRelations;
	private List<List<ClassVisual>> classes;
	private List<InterfaceVisual> interfaces;
	private Map<Integer, Integer> classRowsHeight;
	private Map<Integer, Integer> classRowsWidth;
	private Map<String, Integer> classLevel;
	private Map<String, int[]> entitiesCoordinates;
	private Map<String, Integer> entityAssociationCount;
	private Map<String, Integer> entityInhertianceCount;
	private Map<Integer, Point> classLevelCoordinates;

	public ClassDiagram(ProjectModel projectModel, boolean isColored) {
		project = projectModel;
		this.isColored = isColored;
		projectContext = project.getProjectContext();
		interfaceSectionWidth = 0;
		interfaceSectionHeight = 0;
		interfaceSectionStartingX = MARGIN * 2;
		interfaceSectionStartingY = MARGIN * 2;
		currentClassLevel = 0;
		height = MARGIN * 4;
		width = MARGIN * 4;
		initLists();
		fillEntites();
		initHeight();
		initWidth();
		relationBreakPointX = width - SPACER_X - EXTRA_WIDTH;
		relationBreakPointY = interfaceSectionStartingY + interfaceSectionHeight + (SPACER_Y / 2);
		if(width < DEFAULT_WIDTH) width = DEFAULT_WIDTH;
		if(height < DEFAULT_HEIGHT) height = DEFAULT_HEIGHT;
		
		classSectionStartingX = MARGIN * 2;
		classSectionStartingY = interfaceSectionStartingY + interfaceSectionHeight();
	}
	
	public ClassDiagram(ProjectModel project) {
		this(project, false);
	}
	
	private void initLists() {
		classes = new Vector<List<ClassVisual>>();
		interfaces = new Vector<InterfaceVisual>();
		projectClasses = projectContext.getLoadedClasses().values();
		projectInterfaces = projectContext.getLoadedInterfaces().values();
		projectAssociations = projectContext.getAssociations();
		classRowsHeight = new Hashtable<Integer, Integer>();
		classRowsWidth = new Hashtable<Integer, Integer>();
		entitiesCoordinates = new Hashtable<String, int[]>();
		classLevel = new Hashtable<String, Integer>();
		classRows = max(projectContext.getClassesInhertianceLevel().values());
		for(int i = 0; i <= classRows; i++) {
			classes.add(new Vector<ClassVisual>());
			classRowsHeight.put(i, 0);
			classRowsWidth.put(i, 0);
		}
	}
	
	private void fillEntites() {
		for (ClassModel classModel : projectClasses) {
			ClassVisual v = new ClassVisual(classModel);
			int inheritanceLevel = projectContext.getClassInheritanceLevel(classModel);
			int addedWidth = v.getPreferredSize().width + SPACER_X;
			classes.get(inheritanceLevel).add(v);
			classRowsHeight.put(inheritanceLevel, max(v.getPreferredSize().height, classRowsHeight.get(inheritanceLevel)));
			classRowsWidth.put(inheritanceLevel, classRowsWidth.get(inheritanceLevel) + addedWidth);
			classLevel.put(classModel.getName(), inheritanceLevel);
		}
		for (InterfaceModel interfaceModel : projectInterfaces) {
			InterfaceVisual v = new InterfaceVisual(interfaceModel);
			int entityHeight = v.getPreferredSize().height;
			int addedWidth = v.getPreferredSize().width + SPACER_X;
			interfaces.add(v);
			interfaceSectionHeight = max(interfaceSectionHeight, entityHeight);
			interfaceSectionWidth += addedWidth;
		}
	}
	
	private void initHeight() {
		this.height += interfaceSectionHeight() + SPACER_Y + classSectionHeight();
	}
	
	private void initWidth() {
		this.width += max(classSectionWidth(), interfaceSectionWidth());
		this.width += EXTRA_WIDTH;
	}
	
	private int classSectionWidth() {
		return max(classRowsWidth.values());
	}
	
	private int classSectionHeight() {
		int classRowsCount = classRowsHeight.size();
		int verticalSpacesHeight = SPACER_Y * (classRowsCount - 1);
		int classesHeight = sum(classRowsHeight.values());
		return verticalSpacesHeight + classesHeight;
	}
	
	private int interfaceSectionWidth() {
		return interfaceSectionWidth;
	}
	
	private int interfaceSectionHeight() {
		return interfaceSectionHeight > 0 ? interfaceSectionHeight + SPACER_Y : 0 ;
	}
	
	private Color getColor(Object obj) {
		if(isColored) {
			return UIUtils.getColor(obj);
		}
		return Color.BLACK;
	}
	
	private Color getColor(Object ...objs) {
		if(isColored) {
			return UIUtils.getColor(objs);
		}
		return Color.BLACK;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		currentPrintingX = MARGIN;
		currentPrintingY = MARGIN;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		paintInterfaces(g, interfaces);
		paintClassRows(g, classes);
		paintAssociations(g, projectAssociations);
	}
	
	private void paintClassRows(Graphics g, List<List<ClassVisual>> lists) {
		classLevelCoordinates = new Hashtable<Integer, Point>();
		classLevelCoordinates.put(-1, new Point(interfaceSectionStartingX, interfaceSectionStartingY));
		currentClassLevel = 0;
		currentPrintingX = classSectionStartingX;
		currentPrintingY = classSectionStartingY;
		for (List<ClassVisual> list : lists) {
			classLevelCoordinates.put(currentClassLevel, new Point(currentPrintingX, currentPrintingY));
			paintClassRow(g, list);
			currentPrintingY += SPACER_Y + classRowsHeight.get(currentClassLevel);
			currentPrintingX = classSectionStartingX;
			currentClassLevel++;
		}
	}
	
	//TODO refactor this (its getting too long)
	private void paintClassRow(Graphics g, List<ClassVisual> elements) {
		entityInhertianceCount = new Hashtable<String, Integer>();
		extraSpacer = 0;
		int breakX = relationBreakPointX;
		int breakY = relationBreakPointY;
		int interfaceRelationSpacer = 5;
		for (ClassVisual v : elements) {
			ClassModel clazz = v.getClassModel();
			List<InterfaceModel> implementedInterfaces = projectContext.getImplementedInterfaces(clazz);
			Graphics translatedGraphics = g.create(currentPrintingX, currentPrintingY, width, height);
			int[] currentClassCoordiantes = new int[]{currentPrintingX, currentPrintingY, v.getPreferredSize().width, v.getPreferredSize().height};
			entitiesCoordinates.put(clazz.getName(), currentClassCoordiantes);
			v.paintComponent(translatedGraphics);
			if(currentClassLevel > 0) {
				g.setColor(getColor(clazz.getName(), clazz.getSuperClassName()));
				String superclassName = clazz.getSuperClassName();
				entityInhertianceCount.putIfAbsent(superclassName, 0);
				int superclassInheritanceCount = entityInhertianceCount.get(superclassName);
				entityInhertianceCount.put(superclassName, superclassInheritanceCount + 1);
				int[] superClassCoordiantes = entitiesCoordinates.get(superclassName);
				int x1 = currentClassCoordiantes[0] + (currentClassCoordiantes[2] / 2);
				int y1 = currentClassCoordiantes[1];
				int x2 = superClassCoordiantes[0] + (superClassCoordiantes[2] / 2)  + (superclassInheritanceCount * 3);
				int y2 = superClassCoordiantes[1] + superClassCoordiantes[3];
				UIUtils.drawHorizontallyBrokenArrow(g, x1, y1, x2, y2, ((SPACER_Y / 3) + extraSpacer));
				extraSpacer += 5;
				g.setColor(Color.BLACK);
			}
			if(implementedInterfaces.size() > 0) {
				for (InterfaceModel interfaceModel : implementedInterfaces) {
					String interfaceName = interfaceModel.getName();
					entityInhertianceCount.putIfAbsent(interfaceName, 0);
					int interfaceInheritanceCount = entityInhertianceCount.get(interfaceName);
					entityInhertianceCount.put(interfaceName, interfaceInheritanceCount + 1);
					g.setColor(getColor(interfaceName, clazz.getName()));
					int[] interfaceCoordiantes = entitiesCoordinates.get(interfaceName);
					int x1 = currentClassCoordiantes[0] + (currentClassCoordiantes[2] / 2) + interfaceRelationSpacer;
					int y1 = currentClassCoordiantes[1];
					int x2 = interfaceCoordiantes[0] + (interfaceCoordiantes[2] / 2) + (interfaceInheritanceCount * 3);
					int y2 = interfaceCoordiantes[1] + interfaceCoordiantes[3];
					if(currentClassLevel == 0) {
						UIUtils.drawHorizontallyDashedBrokenArrow(
									g, x1, y1, x2, y2, ((SPACER_Y / 3) + extraSpacer)
								);
					}else {
						UIUtils.drawHorizontallyDashedBrokenLine(
									g, x1, y1, breakX, breakY, ((SPACER_Y / 3) + extraSpacer)
								);
						UIUtils.drawHorizontallyDashedBrokenArrow(
									g, breakX, breakY, x2, y2, (10 + extraSpacer)
								);
						breakX += 5;
					}
					interfaceRelationSpacer += 3;
					extraSpacer += 5;
					g.setColor(Color.BLACK);
				}
			}
			currentPrintingX += v.getPreferredSize().width + SPACER_X;
		}
	}
	
	private void paintInterfaces(Graphics g, List<InterfaceVisual> elements) {
		extraSpacer = 0;
		currentPrintingX = interfaceSectionStartingX;
		currentPrintingY = interfaceSectionStartingY;
		for (InterfaceVisual v : elements) {
			InterfaceModel interf = v.getInterfaceModel();
			Graphics translatedGraphics = g.create(currentPrintingX, currentPrintingY, width, height);
			int[] currentClassCoordiantes = new int[]{currentPrintingX, currentPrintingY, v.getPreferredSize().width, v.getPreferredSize().height};
			entitiesCoordinates.put(interf.getName(), currentClassCoordiantes);
			v.paintComponent(translatedGraphics);
			//TODO: draw inhertiance
			currentPrintingX += v.getPreferredSize().width + SPACER_X;
		}
	}
	
	private void paintAssociations(Graphics g, List<RelationModel> relations) {
		drawnRelations = new Vector<RelationModel>();
		entityAssociationCount = new Hashtable<String, Integer>();
		Map<Integer, Integer> classLevelRelationCount = new Hashtable<Integer, Integer>();
		int leftAssocCount = 0;
		for (RelationModel r : relations) {
			String sourceClassName = r.getSourceClassName();
			String targetClassName = r.getTargetClassName();
			entityAssociationCount.putIfAbsent(targetClassName, 0);
			entityAssociationCount.putIfAbsent(sourceClassName, 0);
			classLevelRelationCount.putIfAbsent(classLevel.getOrDefault(sourceClassName, -1), 0);
			classLevelRelationCount.putIfAbsent(classLevel.getOrDefault(targetClassName, -1), 0);
			if(!isAlreadyDrawn(r) && !sourceClassName.equals(targetClassName)) {
				g.setColor(getColor(r));
				int sourceAssocCount = entityAssociationCount.get(sourceClassName);
				int targetAssocCount = entityAssociationCount.get(targetClassName);
				entityAssociationCount.put(sourceClassName, sourceAssocCount + 1);
				entityAssociationCount.put(targetClassName, targetAssocCount + 1);
				int sourceLevel = classLevel.getOrDefault(sourceClassName, -1);
				int targetLevel = classLevel.getOrDefault(targetClassName, -1);
				int[] sourceCoordinates = entitiesCoordinates.get(sourceClassName);
				int[] targetCoordinates = entitiesCoordinates.get(targetClassName);
				if(sourceLevel == targetLevel) {
					classLevelRelationCount.put(sourceLevel, classLevelRelationCount.get(sourceLevel) + 1);
					//
					//					p2-------------------------------------p3
					//					|										|
					//					|		|--|							|		|--|
					//					p1----p0|  |							p4----p5|  |
					//							|  |									|  |
					//							|--|									|--|
					//
					Point[] p = new Point[6];
					p[0] = new Point(sourceCoordinates[0], sourceCoordinates[1] + 5 + (sourceAssocCount * 5));
					p[1] = new Point(p[0].x - 20 + (sourceAssocCount * 3), p[0].y);
					p[2] = new Point(p[1].x, p[1].y - 25 - (classLevelRelationCount.get(sourceLevel) * 3));
					p[5] = new Point(targetCoordinates[0], targetCoordinates[1] + 5 + (targetAssocCount * 5));
					p[4] = new Point(p[5].x - 20 + (targetAssocCount * 3), p[5].y);
					p[3] = new Point(p[4].x, p[2].y);
					g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
					g.drawLine(p[1].x, p[1].y, p[2].x, p[2].y);
					g.drawLine(p[2].x, p[2].y, p[3].x, p[3].y);
					g.drawLine(p[3].x, p[3].y, p[4].x, p[4].y);
					g.drawLine(p[4].x, p[4].y, p[5].x, p[5].y);
					
				}
				else if (Math.abs(sourceLevel - targetLevel) == 1) {										
					//				|--|						|--|
					//		p1----p0|  |				p2----p3|  |
					//				|  |						|  |
					//				|--|						|--|
					Point[] p = new Point[4];
					p[0] = new Point(sourceCoordinates[0], sourceCoordinates[1] + 5 + (sourceAssocCount * 5));
					p[1] = new Point(p[0].x - 20 + (sourceAssocCount * 3), p[0].y);
					p[3] = new Point(targetCoordinates[0], targetCoordinates[1] + 5 + (targetAssocCount * 5));
					p[2] = new Point(p[3].x - 20 + (targetAssocCount * 3), p[3].y);
					g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
					g.drawLine(p[2].x, p[2].y, p[3].x, p[3].y);
					if(p[2].y < p[1].y) {
						classLevelRelationCount.put(sourceLevel, classLevelRelationCount.get(sourceLevel) + 1);
						UIUtils.drawHorizontallyBrokenLine(g, p[1].x, p[1].y, p[2].x, p[2].y, 30 + (classLevelRelationCount.get(sourceLevel) * 3));
					}else {
						classLevelRelationCount.put(targetLevel, classLevelRelationCount.get(targetLevel) + 1);
						UIUtils.drawHorizontallyBrokenLine(g, p[2].x, p[2].y, p[1].x, p[1].y, 30 + (classLevelRelationCount.get(targetLevel) * 3));
					}
				}
				else {
					classLevelRelationCount.put(sourceLevel, classLevelRelationCount.get(sourceLevel) + 1);
					classLevelRelationCount.put(targetLevel, classLevelRelationCount.get(targetLevel) + 1);
					Point sourceBreakPoint = classLevelCoordinates.get(sourceLevel);
					Point targetBreakPoint = classLevelCoordinates.get(targetLevel);
					Point[] sourcePoints = new Point[4];
					sourcePoints[3] = new Point(sourceBreakPoint.x - 50 -(3 * leftAssocCount), sourceBreakPoint.y - 10 - (3 * leftAssocCount));
					sourcePoints[0] = new Point(sourceCoordinates[0], sourceCoordinates[1] + 5 + (sourceAssocCount * 5));
					sourcePoints[1] = new Point(sourcePoints[0].x - 20 + (sourceAssocCount * 3), sourcePoints[0].y);
					sourcePoints[2] = new Point(sourcePoints[1].x, sourcePoints[3].y);
					
					g.drawLine(sourcePoints[0].x, sourcePoints[0].y, sourcePoints[1].x, sourcePoints[1].y);
					g.drawLine(sourcePoints[1].x, sourcePoints[1].y, sourcePoints[2].x, sourcePoints[2].y);
					g.drawLine(sourcePoints[2].x, sourcePoints[2].y, sourcePoints[3].x, sourcePoints[3].y);
					
					Point[] targetPoints = new Point[4];
					targetPoints[3] = new Point(targetBreakPoint.x - 50 - (3 * leftAssocCount), targetBreakPoint.y - 50 - (3 * leftAssocCount));
					targetPoints[0] = new Point(targetCoordinates[0], targetCoordinates[1] + 5 + (targetAssocCount * 5));
					targetPoints[1] = new Point(targetPoints[0].x - 20 + (targetAssocCount * 3), targetPoints[0].y);
					targetPoints[2] = new Point(targetPoints[1].x, targetPoints[3].y);
					
					g.drawLine(targetPoints[0].x, targetPoints[0].y, targetPoints[1].x, targetPoints[1].y);
					g.drawLine(targetPoints[1].x, targetPoints[1].y, targetPoints[2].x, targetPoints[2].y);
					g.drawLine(targetPoints[2].x, targetPoints[2].y, targetPoints[3].x, targetPoints[3].y);
					
					g.drawLine(targetPoints[3].x, targetPoints[3].y, sourcePoints[3].x, sourcePoints[3].y);
					
					leftAssocCount++;
				}
				drawnRelations.add(r);
				g.setColor(Color.BLACK);
			}
		}
	}
	
	private boolean isAlreadyDrawn(RelationModel relation) {
		for (RelationModel drawn : drawnRelations) {
			String sourceDrawn = drawn.getSourceClassName();
			String targetDrawn = drawn.getTargetClassName();
			int typeDrawn = drawn.getRelationType();
			String source = relation.getSourceClassName();
			String target = relation.getTargetClassName();
			int type = relation.getRelationType();
			if (source.equals(sourceDrawn) && target.equals(targetDrawn) && type == typeDrawn)
				return true;
			if (source.equals(targetDrawn) && target.equals(sourceDrawn) && type == typeDrawn)
				return true;
		}
		return false;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public JScrollPane asScrollPane(int width, int height) {
		JScrollPane scrollPane =  new JScrollPane(this);
		scrollPane.setPreferredSize(new Dimension(width, height));
		UIUtils.fixScrolling(scrollPane);
		return scrollPane;
	}
	
	
	

}
