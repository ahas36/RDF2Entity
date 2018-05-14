/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.csiro.coaas.rdf2entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author ali
 */
public class InterfaceGenerator {

    private static List<Resource> findClasses(String fileLocation) {
        Model model = ModelFactory.createDefaultModel();
        model.read(fileLocation);
        List<Resource> result = new ArrayList<>();

        String queryString = "PREFIX owl:<http://www.w3.org/2002/07/owl#> \n"
                + "SELECT ?ont "
                + "WHERE { ?ont a owl:Class; }";

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            List<String> attrs = new ArrayList<>();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                Resource resource = soln.getResource("ont");
                if (resource.getNameSpace() != null) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

    private static void generateJavaInterface(String fileLocation, String outputFolder, Resource className, String namespace) {
        String result = "package org.mobivoc;\npublic interface " + className.getLocalName() + " extends ";
        String extendString = "JsonLdNode, SchemaOrgType";
        String builderExtendString = "JsonLdNode.Builder, SchemaOrgType.Builder";
        Model model = ModelFactory.createDefaultModel();
        model.read(fileLocation);
        String queryString = "prefix mv:      <http://schema.mobivoc.org/>\n"
                + "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix owl:   <http://www.w3.org/2002/07/owl#>\n"
                + "prefix xsd:   <http://www.w3.org/2001/XMLSchema#>\n"
                + "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "\n"
                + "select ?q where {\n"
                + "<" + className.toString() + "> rdfs:subClassOf ?q \n"
                + "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            List<String> attrs = new ArrayList<>();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                Resource resource = soln.getResource("q");
                if (className.getNameSpace().equals(namespace)) {
                    if (resource.getNameSpace().equals(namespace)) {
                        extendString = resource.getLocalName();
                        builderExtendString = resource.getLocalName() + ".Builder";
                    } else if (resource.getNameSpace().equalsIgnoreCase("http://schema.org/")) {
                        extendString = "com.google.schemaorg.core." + resource.getLocalName();
                        builderExtendString = "com.google.schemaorg.core." + resource.getLocalName() + ".Builder";
                    } else {
                        String[] split = resource.toString().replaceAll("http://", "").replaceAll("https://", "").split("/");
                        extendString = "";
                        for (String item : split) {
                            if (item.trim().isEmpty()) {
                                continue;
                            }
                            builderExtendString += item + ".";
                        }
                        builderExtendString = extendString + "Builder";
                        extendString = extendString.substring(0, extendString.length());
                    }
                } else {
                    if (className.getNameSpace().equalsIgnoreCase("http://schema.org/")) {
                        extendString = "com.google.schemaorg.core." + className.getLocalName();
                        builderExtendString = "com.google.schemaorg.core." + className.getLocalName() + ".Builder";
                    } else {
                        String[] split = className.toString().replaceAll("http://", "").replaceAll("https://", "").split("/");
                        extendString = "";
                        for (String item : split) {
                            if (item.trim().isEmpty()) {
                                continue;
                            }
                            builderExtendString += item + ".";
                        }
                        builderExtendString = extendString + "Builder";
                        extendString = extendString.substring(0, extendString.length());
                    }
                }
            }
        }
        result += extendString + " {\n";
        result += "  public interface Builder extends " + builderExtendString
                + "  {\n";
        queryString = "prefix mv:      <http://schema.mobivoc.org/>\n"
                + "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix owl:   <http://www.w3.org/2002/07/owl#>\n"
                + "prefix xsd:   <http://www.w3.org/2001/XMLSchema#>\n"
                + "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "\n"
                + "select ?p where {\n"
                + "  ?p rdfs:domain/(owl:unionOf/rdf:rest*/rdf:first)* <" + className.toString() + ">\n"
                + "}";
        query = QueryFactory.create(queryString);
        boolean isExtraParams = false;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            List<String> attrs = new ArrayList<>();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                Resource resource = soln.getResource("p");
                String subQueryString = "prefix mv:      <http://schema.mobivoc.org/>\n"
                        + "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                        + "prefix owl:   <http://www.w3.org/2002/07/owl#>\n"
                        + "prefix xsd:   <http://www.w3.org/2001/XMLSchema#>\n"
                        + "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n"
                        + "\n"
                        + "select ?q where {\n"
                        + "<" + resource.toString() + "> rdfs:range ?q \n"
                        + "}";

                Query subQuery = QueryFactory.create(subQueryString);
                try (QueryExecution subQexec = QueryExecutionFactory.create(subQuery, model)) {
                    ResultSet subResults = subQexec.execSelect();
                    List<String> subAttrs = new ArrayList<>();
                    for (; subResults.hasNext();) {
                        QuerySolution subSoln = subResults.nextSolution();
                        Resource subResource = subSoln.getResource("q");
                        result += "Builder add" + fixNames(resource.getLocalName()) + "(" + fixNames(subResource.getLocalName()) + " value);\n\n";
                        isExtraParams = true;
                    }
                }
            }
        }
        if (isExtraParams) {
            saveFile(outputFolder, className.getLocalName(), result + " " + className.getLocalName() + " build();\n}\n}");
        } else {
            System.out.println(className.getLocalName());
        }
    }

    private static void saveFile(String folderName, String fileName, String data) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            File f = new File(folderName);
            f.mkdirs();
            fw = new FileWriter(folderName + File.separator + fileName + ".java");
            bw = new BufferedWriter(fw);
            bw.write(data);

            System.out.println("Done");

        } catch (IOException e) {
        } finally {

            try {

                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {
            }

        }
    }

    public static void generate(String namespace, String outputFolder, String... files) {

        for (String file : files) {

            List<Resource> findClasses = InterfaceGenerator.findClasses(file);

            for (Resource findClasse : findClasses) {
                generateJavaInterface(file, outputFolder, findClasse, namespace);
                //System.out.println(result);
            }

        }
    }

    private static String fixNames(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
