package org.dev.uml.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class JavaCodeAnalyzer {

    public List<String> analyzeClasses(String filePath) {
        List<String> classNames = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(filePath));

            // Create an instance of JavaParser
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(content).getResult().orElse(null);

            if (cu != null) {
                cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> classNames.add(c.getNameAsString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classNames;
    }
}
