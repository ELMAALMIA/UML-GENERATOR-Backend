package org.mql.java.umlgen.models;

import static org.mql.java.umlgen.utils.StringUtils.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Vector;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;

@ComplexElement("method")
public class MethodModel implements Model{
	
	//private AnnotationModel[] annotations;
	private String name;
	private List<ParameterModel> parameters;
	private int modifiers;
	private String returnType;

	public MethodModel(Method method) {
		parameters = new Vector<ParameterModel>();
		modifiers = method.getModifiers();		
		name = method.getName();
		this.returnType = method.getReturnType().getName();
		for (Parameter p : method.getParameters()) {
			parameters.add(new ParameterModel(p));
		}
	}

	public MethodModel(String name, List<ParameterModel> parameters, int modifiers, String returnType) {
		super();
		this.name = name;
		this.parameters = parameters;
		this.modifiers = modifiers;
		this.returnType = returnType;
	}

	public MethodModel(String name, int modifiers, String returnType) {
		super();
		this.name = name;
		this.parameters = new Vector<ParameterModel>();
		this.modifiers = modifiers;
		this.returnType = returnType;
	}
	
	public void addParameter(ParameterModel parameter) {
		parameters.add(parameter);
	}

	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}

	@ComplexElement(value="parameters", order=3)
	public List<ParameterModel> getParameters() {
		return parameters;
	}

	@SimpleElement(value="modifiers", order=4)
	public int getModifiers() {
		return modifiers;
	}
	
	@SimpleElement(value="name", order=1)
	public String getName() {
		return name;
	}
	
	@SimpleElement(value="return", order=2)
	public String getReturnType() {
		return returnType;
	}
	
	@Override
	public String toString() {
		String methodString = "";
		methodString += getAccessModifierSymbol(modifiers) + " ";
		methodString += name + "(";
		if(!parameters.isEmpty()) {
			for (ParameterModel param : parameters) {
				methodString += getClassShortName(param.getType()) + ", ";
			}
			methodString = methodString.substring(0, methodString.length() - 2) + ") : ";
		}else {
			methodString += ") : ";
		}
		methodString += getClassShortName(returnType);
		return methodString;
	}
	
	
}
