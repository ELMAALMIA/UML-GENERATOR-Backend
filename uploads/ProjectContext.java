package org.mql.java.umlgen.models;

import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ProjectContext {

	private ProjectModel currentProject;
	private Map<String, ClassModel> loadedClasses;
	private Map<String, InterfaceModel> loadedInterfaces;
	private Map<String, AnnotationModel> loadedAnnotations;
	private Map<String, PackageModel> loadedPackages;
	private Map<String, Integer> classesInhertianceLevel;
	private List<RelationModel> relations;
	
	private URLClassLoader classloader;

	public ProjectContext(ProjectModel currentProject) {
		this.currentProject = currentProject;
		initLists();
	}
	
	public void initLists() {
		loadedClasses = new Hashtable<String, ClassModel>();
		loadedInterfaces = new Hashtable<String, InterfaceModel>();
		loadedAnnotations = new Hashtable<String, AnnotationModel>();
		loadedPackages = new Hashtable<String, PackageModel>();
		classesInhertianceLevel = new Hashtable<String, Integer>();
		relations = new Vector<RelationModel>();
	}
	
	public void addPackage(PackageModel pkg) {
		loadedPackages.put(pkg.getName(), pkg);
	}
	
	public void addClass(ClassModel clazz) {
		loadedClasses.put(clazz.getName(),clazz);
	}

	public void addInterface(InterfaceModel interf) {
		loadedInterfaces.put(interf.getName(), interf);
	}

	public void addAnnotation(AnnotationModel annotation) {
		loadedAnnotations.put(annotation.getName(), annotation);
	}
	
	public void addRelation(RelationModel relation) {
		relations.add(relation);
	}
	
	public ProjectModel getCurrentProject() {
		return currentProject;
	}
	
	public List<RelationModel> getRelations() {
		return relations;
	}
	
	public void setClassloader(URLClassLoader classloader) {
		this.classloader = classloader;
	}
	
	public URLClassLoader getClassloader() {
		return classloader;
	}

	public Map<String, ClassModel> getLoadedClasses() {
		return loadedClasses;
	}
	
	public Map<String, InterfaceModel> getLoadedInterfaces() {
		return loadedInterfaces;
	}

	public Map<String, AnnotationModel> getLoadedAnnotations() {
		return loadedAnnotations;
	}
	
	public Map<String, PackageModel> getLoadedPackages() {
		return loadedPackages;
	}
	
	public boolean isLoaded(String classname) {
		return loadedClasses.containsKey(classname) || loadedInterfaces.containsKey(classname) || loadedAnnotations.containsKey(classname);
	}
	
	public ClassModel getLoadedClassModel(String classname) {
		return loadedClasses.get(classname);
	}

	public InterfaceModel getLoadedInterfaceModel(String interfaceName) {
		return loadedInterfaces.get(interfaceName);
	}

	public AnnotationModel getLoadedAnnotationModel(String annotationName) {
		return loadedAnnotations.get(annotationName);
	}
	
	public PackageModel getLoadedPackageModel(String packageName) {
		return loadedPackages.get(packageName);
	}
	
	public Entity getLoadedEntity(String classname) {
		if (loadedAnnotations.containsKey(classname)) {
			return getLoadedAnnotationModel(classname);
		}
		if (loadedInterfaces.containsKey(classname)) {
			return getLoadedInterfaceModel(classname);
		}
		return getLoadedClassModel(classname);
	}
	
	public void processClassInheritanceLevel(ClassModel clazz) {
		String classname = clazz.getName();
		if (!classesInhertianceLevel.containsKey(classname)) {
			classesInhertianceLevel.put(classname, 0);
			for (RelationModel relationModel : clazz.getRelations()) {
				if(	relationModel.getSourceClassName().equals(classname) &&
					relationModel.getRelationType() == RelationModel.INHERITANCE) {
					String superclassName = relationModel.getTargetClassName();
					ClassModel superclass = getLoadedClassModel(superclassName);
					processClassInheritanceLevel(superclass);
					int superclassInheritanceLevel = classesInhertianceLevel.get(superclassName);
					classesInhertianceLevel.put(classname, superclassInheritanceLevel + 1);
				}
			}
		} //else it will be already processed
	}
	
	public void processInhertiance() {
		for (ClassModel clazz : loadedClasses.values()) {
			processClassInheritanceLevel(clazz);
		}
	}
	
	public Map<String, Integer> getClassesInhertianceLevel() {
		return classesInhertianceLevel;
	}
	
	public int getClassInheritanceLevel(String classname) {
		return classesInhertianceLevel.get(classname);
	}
	
	public int getClassInheritanceLevel(ClassModel clazz) {
		return classesInhertianceLevel.get(clazz.getName());
	}
	
	public int getEntitesCount() {
		int count = 0;
		count += loadedClasses.size();
		count += loadedInterfaces.size();
		count += loadedAnnotations.size();
		return count;
	}
	
	public int getPackagesCount() {
		return loadedPackages.size();
	}
	
	public List<InterfaceModel> getImplementedInterfaces(ClassModel clazz){
		List<InterfaceModel> implementedInterfaces = new Vector<InterfaceModel>();
		for (RelationModel relation : clazz.getRelations()) {
			if(relation.getRelationType() == RelationModel.REALIZATION) {
				InterfaceModel interf = getLoadedInterfaceModel(relation.getTargetClassName());
				implementedInterfaces.add(interf);
			}
		}
		return implementedInterfaces;
	}
	
	public List<RelationModel> getAssociations(){
		List<RelationModel> associations = new Vector<RelationModel>();
		for (RelationModel r : relations) {
			if(r.getRelationType() == RelationModel.ASSOCIATION) {
				associations.add(r);
			}
		}
		return associations;
	}
	

}
