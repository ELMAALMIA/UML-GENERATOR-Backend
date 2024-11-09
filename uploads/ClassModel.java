package org.mql.java.umlgen.models;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;

@ComplexElement(value="class")
public class ClassModel implements Model, Entity{
	
	
	private ProjectContext projectContext;
	private String name;
	private int modifiers;
	private List<ConstructorModel> constructors;
	private List<FieldModel> fields;
	private List<MethodModel> methods;
	private List<RelationModel> relations;
	
	
	/*
	 * To figure out relations we need all class models to
	 * be already loaded, so we will delay the treatement.
	 * Maybe its not the best way to this.
	 */
	private List<Field> pendingFields;
	private Class<?> reflectClass; // for resolving relations only.

	//TODO: finish relations implementation
	public ClassModel(ProjectContext context,Class<?> clazz) {
		this.projectContext = context;
		this.reflectClass = clazz;
		modifiers = clazz.getModifiers();
		initLists();
		
		this.name = clazz.getName();
		
		for (Constructor<?> c : clazz.getConstructors()) {
			constructors.add(new ConstructorModel(c));
		}
		for (Field f : clazz.getDeclaredFields()) {
			Class<?> fieldType = f.getType();
			if(fieldType.isPrimitive()) {
				fields.add(new FieldModel(f));
			} else {
				pendingFields.add(f);
			}
		}
		for (Method m : clazz.getDeclaredMethods()) {
			methods.add(new MethodModel(m));
		}
		
	}
	
	

	public ClassModel(
			ProjectContext projectContext,
			String name,
			int modifiers,
			List<ConstructorModel> constructors,
			List<FieldModel> fields,
			List<MethodModel> methods,
			List<RelationModel> relations) {
		super();
		this.projectContext = projectContext;
		this.name = name;
		this.modifiers = modifiers;
		this.constructors = constructors;
		this.fields = fields;
		this.methods = methods;
		this.relations = relations;
	}

	

	public ClassModel(ProjectContext projectContext, String name, int modifiers) {
		super();
		this.projectContext = projectContext;
		this.name = name;
		this.modifiers = modifiers;
		initLists();
	}


	private void initLists() {
		this.constructors = new Vector<ConstructorModel>();
		this.fields = new Vector<FieldModel>();
		this.methods = new Vector<MethodModel>();
		this.relations = new Vector<RelationModel>();
		this.pendingFields = new Vector<Field>();
	}

	public void resolveRelations() {
		//adding inhertiance relation if superclass is within the project.
		Class<?> superclass = reflectClass.getSuperclass();
		if((!reflectClass.equals(Object.class)) && projectContext.isLoaded(superclass.getName())) {
			ClassModel superclassModel = projectContext.getLoadedClassModel(superclass.getName());
			RelationModel newRelation = new RelationModel(this, superclassModel, RelationModel.INHERITANCE);
			relations.add(newRelation);
			projectContext.addRelation(newRelation);
		}
		
		//adding relations for implemented classes
		Class<?>[] implementedInterfaces = reflectClass.getInterfaces();
		for (Class<?> interf : implementedInterfaces) {
			if(projectContext.isLoaded(interf.getName())) {
				RelationModel newRelation = new RelationModel(this, projectContext.getLoadedInterfaceModel(interf.getName()), RelationModel.REALIZATION);
				relations.add(newRelation);
				projectContext.addRelation(newRelation);
			}
		}
		
		//adding relations for fields of classes that exists in current project
		for (Field f : pendingFields) {
			Class<?> fieldType = f.getType();
			if(fieldType.isArray()) {
				fieldType = fieldType.arrayType();
			}else if(Collection.class.isAssignableFrom(fieldType)){
				try {
					ParameterizedType type = (ParameterizedType) f.getGenericType();
					fieldType = (Class<?>) type.getActualTypeArguments()[0];
				} catch (Exception e) {
					fieldType = Object.class;
				}
			}
			if(projectContext.isLoaded(fieldType.getName())) {
				RelationModel newRelation = new RelationModel(this, projectContext.getLoadedEntity(fieldType.getName()), RelationModel.ASSOCIATION);
				relations.add(newRelation);
				projectContext.addRelation(newRelation);
			} else {
				fields.add(new FieldModel(f));
			}
		}
	}	

	public void addConstructor(ConstructorModel constructor) {
		constructors.add(constructor);
	}
	
	public void addField(FieldModel field) {
		fields.add(field);
	}
	
	public void addMethod(MethodModel method) {
		methods.add(method);
	}
	
	public void addRelation(RelationModel relation) {
		relations.add(relation);
		projectContext.addRelation(relation);
	}
	
	public boolean isAbstract() {
		return Modifier.isAbstract(modifiers);
	}
	
	public String getSuperClassName() {
		for (RelationModel relationModel : relations) {
			if(relationModel.getRelationType() == RelationModel.INHERITANCE) {
				return relationModel.getTargetClassName();
			}
		}
		return null;
	}
	
	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}

	@Override
	@SimpleElement(value="name", order=0)
	public String getName() {
		return name;
	}

	@SimpleElement(value="modifiers", order=2)
	public int getModifiers() {
		return modifiers;
	}
	
	@ComplexElement(value="constructors", order=3)
	public List<ConstructorModel> getConstructors() {
		return constructors;
	}

	@ComplexElement(value="fields", order=4)
	public List<FieldModel> getFields() {
		return fields;
	}

	@ComplexElement(value="methods", order=5)
	public List<MethodModel> getMethods() {
		return methods;
	}
	
	@ComplexElement(value="relations", order=6)
	public List<RelationModel> getRelations() {
		return relations;
	}
	
	
	
	

}
