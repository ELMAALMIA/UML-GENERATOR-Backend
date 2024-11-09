package org.mql.java.umlgen.models;

import static org.mql.java.umlgen.utils.StringUtils.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Vector;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;

@ComplexElement("constructor")
public class ConstructorModel implements Model{
	
	private String name;
	private List<ParameterModel> parameters;
	private int modifiers;
	
	public ConstructorModel(Constructor<?> constructor) {
		parameters = new Vector<ParameterModel>();
		modifiers = constructor.getModifiers();
		name = constructor.getName();
		for (Parameter parameter : constructor.getParameters()) {
			parameters.add(new ParameterModel(parameter));
		}
	}
	
	public ConstructorModel(String name, List<ParameterModel> parameters, int modifiers) {
		this.name = name;
		this.modifiers = modifiers;
		this.parameters = parameters;
	}
	
	public ConstructorModel(String name, int modifiers) {
		this.name = name;
		this.modifiers = modifiers;
		this.parameters = new Vector<ParameterModel>();
	}
	
	public void addParameter(ParameterModel parameter) {
		parameters.add(parameter);
	}

	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}
	
	@SimpleElement(value="name", order=0)
	public String getName() {
		return name;
	}

	@ComplexElement(value="parameters", order=1)
	public List<ParameterModel> getParameters() {
		return parameters;
	}

	@SimpleElement(value="modifiers", order=2)
	public int getModifiers() {
		return modifiers;
	}
	
	@Override
	public String toString() {
		String constructorString = "";
		constructorString += getAccessModifierSymbol(modifiers) + " ";
		constructorString += getClassShortName(name) + "(";
		if(!parameters.isEmpty()) {
			for (ParameterModel param : parameters) {
				constructorString += getClassShortName(param.getType()) + ", ";
			}
			constructorString = constructorString.substring(0, constructorString.length() - 2) + ")";
		}else {
			constructorString += ")";
		}
		return constructorString;
	}
	
	

	
}
