package org.mql.java.umlgen.models;

import org.mql.java.umlgen.annotations.ComplexElement;
import org.mql.java.umlgen.annotations.SimpleElement;
import org.mql.java.umlgen.xml.generators.XMLElement;
import org.mql.java.umlgen.xml.generators.XMLElementGenerator;


@ComplexElement(value="relation")
public class RelationModel implements Model {
	
	
	public final static int ASSOCIATION = 0;
	public final static int AGGREGATION = 1; //TODO not yet implemented
	public final static int COMPOSITION = 2; //TODO not yet implemented
	public final static int INHERITANCE = 3;
	public final static int REALIZATION = 4;
	public final static int DEPENDENCY = 5; //TODO not yet implemented
	
	private String source;
	private String target;
	private int relationType;
	private String cardinality = "N/A";

	public RelationModel(Entity source, Entity target) {
		this.source = source.getName();
		this.target = target.getName();
		this.relationType = ASSOCIATION; //If not specified it is an association
	}
	
	public RelationModel(Entity source, Entity target, int relationType) {
		this(source, target);
		this.relationType = relationType;
	}
	
	public RelationModel(Entity source, Entity target, int relationType, String cardinality) {
		this(source, target);
		this.relationType = relationType;
		this.cardinality = cardinality;
	}
	
	public RelationModel(String source, String target, int relationType, String cardinality) {
		this.source = source;
		this.target = target;
		this.relationType = relationType;
		this.cardinality = cardinality;
	}

	@Override
	public XMLElement getElementModel(XMLElementGenerator generator) {
		return generator.generate(this);
	}

	@SimpleElement(value="source", order=1)
	public String getSourceClassName() {
		return source;
	}

	@SimpleElement(value="target", order=2)
	public String getTargetClassName() {
		return target;
	}
	
	@SimpleElement(value="type", order=3)
	public int getRelationType() {
		return relationType;
	}
	
	@SimpleElement(value="cardinality", order=4)
	public String getCardinality() {
		return cardinality;
	}

	
	

}
