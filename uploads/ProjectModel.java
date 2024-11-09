package org.mql.java.umlgen.models;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.exceptions.ProjectNotValidException;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;

@ComplexElement(value="project")
public class ProjectModel implements Model{
	
	private String name = "";
	private String path;
	private List<PackageModel> packages;
	private ProjectContext projectContext;
	private DefaultPackageModel defaultPackage;
	private URLClassLoader classLoader;

	protected ProjectModel() {
		
	}
	
	public ProjectModel(String projectPath, String projectName) throws ProjectNotValidException{
		this(projectPath);
		this.name = projectName;
	}

	public ProjectModel(String projectPath) throws ProjectNotValidException {
		this.path = projectPath;
		projectContext = new ProjectContext(this);
		packages = new Vector<PackageModel>();
		
		defaultPackage = new DefaultPackageModel(projectContext);
		if(!defaultPackage.isEmpty()) {
			packages.add(defaultPackage);
			projectContext.addPackage(defaultPackage);
		}
		File file = new File(path);
		if(file.isDirectory()) {
			if(file.getName() == "bin") {
				this.name = file.getParentFile().getName();
			}
			try {
				this.classLoader = new URLClassLoader(new URL[] {file.toURI().toURL()}, null);
				this.projectContext.setClassloader(classLoader);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}else{
			throw new ProjectNotValidException();
		}
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				PackageModel pkg = new PackageModel(projectContext, f.getAbsolutePath());
				packages.add(pkg);
				projectContext.addPackage(pkg);
			} 
		}
		resolveRelations();
		projectContext.processInhertiance();
		
	}

	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}
	
	public void resolveRelations() {
		//TODO: can be optimized
		Collection<Entity> Entities = new Vector<Entity>();
		Entities.addAll(projectContext.getLoadedClasses().values());
		Entities.addAll(projectContext.getLoadedInterfaces().values());
		Entities.addAll(projectContext.getLoadedAnnotations().values());
		for (Entity entity : Entities) {
			entity.resolveRelations();
		}
	}
	
	public static ProjectModel getInstanceForParser(String name, String path) {
		ProjectModel project = new ProjectModel();
		project.name = name;
		project.path = path;
		project.packages = new Vector<PackageModel>();
		project.projectContext = new ProjectContext(project);
		return project;
	}
	
	public void addPackage(PackageModel pkg) {
		packages.add(pkg);
	}
	
	public ProjectContext getProjectContext() {
		return projectContext;
	}
	
	@SimpleElement(value="name", order=1)
	public String getName() {
		return name;
	}
	
	@SimpleElement(value="path", order=2)
	public String getPath() {
		return path;
	}
	
	@ComplexElement(value="packages", order=3)
	public List<PackageModel> getPackages() {
		return packages;
	}

}
