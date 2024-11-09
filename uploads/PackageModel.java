package org.mql.java.umlgen.models;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;

@ComplexElement(value="package")
public class PackageModel implements Model{
	
	protected ProjectContext projectContext;
	protected String name;
	protected String path;
	protected List<ClassModel> classes;
	protected List<InterfaceModel> interfaces;
	protected List<PackageModel> subPackages;
	protected List<AnnotationModel> annotations;
	
	protected PackageModel() {
		initLists();
	}

	public PackageModel(ProjectContext projectContext, String pathToPackageDirectory) {
		initLists();
		this.projectContext = projectContext;
		this.path = pathToPackageDirectory;
		String projectPath = projectContext.getCurrentProject().getPath();
		String relativePath = pathToPackageDirectory.substring(projectPath.length() + 1);
		name = relativePath.replace(File.separator, ".");
		packageExplorer();
	}
	
	
	
	public PackageModel(
			ProjectContext projectContext,
			String name,
			String path,
			List<ClassModel> classes,
			List<InterfaceModel> interfaces,
			List<PackageModel> subPackages,
			List<AnnotationModel> annotatons ) {
		super();
		this.name = name;
		this.path = path;
		this.classes = classes;
		this.interfaces = interfaces;
		this.subPackages = subPackages;
		this.annotations = annotatons;
		this.projectContext = projectContext;
	}

	protected void initLists() {
		classes = new Vector<ClassModel>();
		interfaces = new Vector<InterfaceModel>();
		subPackages = new Vector<PackageModel>();
		annotations = new Vector<AnnotationModel>();
	}
	
	public void addClass(ClassModel clazz) {
		classes.add(clazz);
	}
	
	public void addInterface(InterfaceModel interf) {
		interfaces.add(interf);
	}
	
	public void addPackage(PackageModel subPackage) {
		subPackages.add(subPackage);
	}
	
	public void addAnnotation(AnnotationModel annotation) {
		annotations.add(annotation);
	}

	public PackageModel(ProjectContext projectContext, String name, String path) {
		super();
		this.projectContext = projectContext;
		this.name = name;
		this.path = path;
		initLists();
	}

	
	
	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}

	//TODO: maybe this should not be here, keep the models clean.
	protected void packageExplorer() {
		File file = new File(path);
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				PackageModel subPackage = new PackageModel(projectContext, f.getAbsolutePath());
				subPackages.add(subPackage);
				projectContext.addPackage(subPackage);
			} else {
				String fileName = f.getName();
				if (fileName.endsWith(".class")) {
					String className = fileName.substring(0, fileName.length() - 6);
					try {
						Class<?> clazz = projectContext.getClassloader().loadClass(name + "."  + className);
						if (clazz.isAnnotation()) {
							AnnotationModel model = new AnnotationModel(projectContext, clazz);
							annotations.add(model);
							projectContext.addAnnotation(model);
						} else if (clazz.isInterface()) {
							InterfaceModel model = new InterfaceModel(projectContext, clazz);
							interfaces.add(model);
							projectContext.addInterface(model);
						} else {
							ClassModel model = new ClassModel(projectContext, clazz);
							classes.add(model);
							projectContext.addClass(model);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SimpleElement(value="name", order=1)
	public String getName() {
		return name;
	}

	@ComplexElement(value="classes", order=3)
	public List<ClassModel> getClasses() {
		return classes;
	}
	
	@ComplexElement(value="interfaces", order=4)
	public List<InterfaceModel> getInterfaces() {
		return interfaces;
	}

	@ComplexElement(value="packages", order=6)
	public List<PackageModel> getSubPackages() {
		return subPackages;
	}

	@ComplexElement(value="annotations", order=5)
	public List<AnnotationModel> getAnnotations() {
		return annotations;
	}

	@SimpleElement(value="path", order=2)
	public String getPath() {
		return path;
	}
	
	

}
