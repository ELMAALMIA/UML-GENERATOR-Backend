package org.dev.uml.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.util.List;

@XmlRootElement(name = "classStructure")
public class ClassStructure {
    private List<ClassInfo> classes;

    @XmlElementWrapper(name = "classes")
    @XmlElement(name = "class")
    public List<ClassInfo> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassInfo> classes) {
        this.classes = classes;
    }

    public static class ClassInfo {
        private String className;
        private String superClass;
        private List<String> interfaces;
        private List<AttributeInfo> attributes;
        private List<MethodInfo> methods;

        @XmlElement(name = "className")
        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        @XmlElement(name = "superClass")
        public String getSuperClass() {
            return superClass;
        }

        public void setSuperClass(String superClass) {
            this.superClass = superClass;
        }

        @XmlElementWrapper(name = "interfaces")
        @XmlElement(name = "interface")
        public List<String> getInterfaces() {
            return interfaces;
        }

        public void setInterfaces(List<String> interfaces) {
            this.interfaces = interfaces;
        }

        @XmlElementWrapper(name = "attributes")
        @XmlElement(name = "attribute")
        public List<AttributeInfo> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<AttributeInfo> attributes) {
            this.attributes = attributes;
        }

        @XmlElementWrapper(name = "methods")
        @XmlElement(name = "method")
        public List<MethodInfo> getMethods() {
            return methods;
        }

        public void setMethods(List<MethodInfo> methods) {
            this.methods = methods;
        }
    }

    public static class AttributeInfo {
        private String accessModifier;
        private String dataType;
        private String name;

        @XmlElement(name = "accessModifier")
        public String getAccessModifier() {
            return accessModifier;
        }

        public void setAccessModifier(String accessModifier) {
            this.accessModifier = accessModifier;
        }

        @XmlElement(name = "dataType")
        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        @XmlElement(name = "name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MethodInfo {
        private String methodName;
        private List<String> parameters;
        private String returnType;

        @XmlElement(name = "methodName")
        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        @XmlElementWrapper(name = "parameters")
        @XmlElement(name = "parameter")
        public List<String> getParameters() {
            return parameters;
        }

        public void setParameters(List<String> parameters) {
            this.parameters = parameters;
        }

        @XmlElement(name = "returnType")
        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }
    }

    // Method to generate the XML file
    public void generateXml(String outputPath) {
        try {
            JAXBContext context = JAXBContext.newInstance(ClassStructure.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, new File(outputPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
