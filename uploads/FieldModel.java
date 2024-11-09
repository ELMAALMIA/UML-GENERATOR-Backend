package org.mql.java.umlgen.models;

import static org.mql.java.umlgen.utils.StringUtils.*;

import java.lang.reflect.Field;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;

@ComplexElement(value="field")
public class FieldModel implements Model{
	
	private String name;
	private String returnType;
	private int modifiers;

	public FieldModel(Field field) {
		this.name = field.getName();
		this.returnType = field.getType().getName();
		modifiers = field.getModifiers();
	}
	
	public FieldModel(String name, String returnType, int modifiers) {
		super();
		this.name = name;
		this.returnType = returnType;
		this.modifiers = modifiers;
	}

	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}

	@SimpleElement(value="name", order=1)
	public String getName() {
		return name;
	}

	@SimpleElement(value="return", order=2)
	public String getReturnType() {
		return returnType;
	}
	
	@SimpleElement(value="modifiers", order=3)
	public int getModifiers() {
		return modifiers;
	}
	
	@Override
	public String toString() {
		String fieldString = "";
		fieldString += getAccessModifierSymbol(modifiers) + " ";
		fieldString += name + " : ";
		fieldString += getClassShortName(returnType);
		return fieldString;
	}

	
	
}
