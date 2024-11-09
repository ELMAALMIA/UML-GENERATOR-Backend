package org.mql.java.umlgen.models;

import java.lang.reflect.Parameter;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;

@ComplexElement(value="parameter")
public class ParameterModel implements Model {
	
	private String type;
	private String name;

	public ParameterModel(Parameter parameter) {
		this.type = parameter.getType().getName();
		this.name = parameter.getName();
	}

	public ParameterModel(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}

	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}
	
	@SimpleElement(value="type", order=2)
	public String getType() {
		return type;
	}

	@SimpleElement(value="name", order=1)
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name + " : " + type;
	}
	
	

}
