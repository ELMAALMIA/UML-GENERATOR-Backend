package org.dev.uml;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.dev.uml.xml.ClassStructure;
import org.dev.uml.xml.ClassStructure.AttributeInfo;
import org.dev.uml.xml.ClassStructure.ClassInfo;
import org.dev.uml.xml.ClassStructure.MethodInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";
    private static final String XML_OUTPUT_DIR = "uploads/xml/";

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file temporarily
            Path uploadDir = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadDir); // Ensure the directory exists
            Path uploadPath = uploadDir.resolve(file.getOriginalFilename());
            Files.write(uploadPath, file.getBytes());

            // Read the content of the file
            String content = Files.readString(uploadPath);

            // Parse the content using JavaParser
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(content).getResult().orElseThrow(() -> 
                new RuntimeException("Failed to parse the Java file"));

            List<ClassInfo> classInfos = new ArrayList<>();

            // Extract class information
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                ClassInfo classInfo = new ClassInfo();
                classInfo.setClassName(clazz.getNameAsString());
                classInfo.setSuperClass(clazz.getExtendedTypes().isEmpty() ? null : clazz.getExtendedTypes(0).getNameAsString());

                List<String> interfaces = new ArrayList<>();
                clazz.getImplementedTypes().forEach(interf -> interfaces.add(interf.getNameAsString()));
                classInfo.setInterfaces(interfaces);

                // Collect attributes
                List<AttributeInfo> attributes = new ArrayList<>();
                for (FieldDeclaration field : clazz.getFields()) {
                    AttributeInfo attributeInfo = new AttributeInfo();
                    attributeInfo.setAccessModifier(field.getAccessSpecifier().asString());
                    attributeInfo.setDataType(field.getCommonType().asString());
                    attributeInfo.setName(field.getVariable(0).getNameAsString());
                    attributes.add(attributeInfo);
                }
                classInfo.setAttributes(attributes);

                // Collect methods
                List<MethodInfo> methods = new ArrayList<>();
                for (MethodDeclaration method : clazz.getMethods()) {
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.setMethodName(method.getNameAsString());
                    methodInfo.setReturnType(method.getType().asString());

                    List<String> parameters = new ArrayList<>();
                    method.getParameters().forEach(param -> parameters.add(param.getType().asString() + " " + param.getNameAsString()));
                    methodInfo.setParameters(parameters);

                    methods.add(methodInfo);
                }
                classInfo.setMethods(methods);

                classInfos.add(classInfo);
            });

            // Create a ClassStructure object and set the classes
            ClassStructure classStructure = new ClassStructure();
            classStructure.setClasses(classInfos);

            // Generate XML file
            Path xmlOutputDir = Paths.get(XML_OUTPUT_DIR);
            Files.createDirectories(xmlOutputDir); // Ensure the directory exists
            String xmlFilePath = XML_OUTPUT_DIR + "class_structure.xml";
            classStructure.generateXml(xmlFilePath);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("classes", classInfos);
            response.put("xmlFilePath", xmlFilePath);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Create an error response as a Map
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
