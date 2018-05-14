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
public class CoreConstantsGenerator {

    private static List<String> findProperties(String fileLocation, String nameSpace) {
        Model model = ModelFactory.createDefaultModel();
        model.read(fileLocation);
        List<String> result = new ArrayList<>();

        String queryString = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "SELECT ?ont "
                + "WHERE { ?ont a rdf:Property; }";

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            List<String> attrs = new ArrayList<>();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                Resource resource = soln.getResource("ont");
                if (resource.getNameSpace() != null && resource.getNameSpace().equals(nameSpace)) {
                    result.add(soln.getResource("ont").getLocalName());
                }
            }
        }
        return result;
    }

    private static List<String> findClasses(String fileLocation, String nameSpace) {
        Model model = ModelFactory.createDefaultModel();
        model.read(fileLocation);
        List<String> result = new ArrayList<>();

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
                if (resource.getNameSpace() != null && !resource.getNameSpace().equalsIgnoreCase("http://schema.org")) {
                    result.add(soln.getResource("ont").getLocalName());
                }

            }
        }
        return result;
    }

    public static void generate(String nameSpace, String ouputFolder, String... files) {
        String result = "package org.mobivoc;\npublic final class CoreConstants {\n";
        result += "public static final String NAMESPACE = \"" + nameSpace + "\";" + "\n\n";

        for (String file : files) {
            List<String> findClasses = CoreConstantsGenerator.findClasses(file, nameSpace);
            List<String> findProperties = CoreConstantsGenerator.findProperties(file, nameSpace);
            for (String findProperty : findProperties) {
                result += "public static final String PROPERTY_" + fixNames(findProperty) + "= NAMESPACE + \"" + findProperty + "\";" + "\n\n";
            }
            for (String findClasse : findClasses) {
                result += "public static final String TYPE_" + fixNames(findClasse) + "= NAMESPACE + \"" + findClasse + "\";" + "\n\n";
            }
        }
        saveFile(ouputFolder, "CoreConstants", result + "}");
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

    private static String fixNames(String name) {
        String fixedName = "";
        for (int i = 0; i < name.length(); i++) {
            String charAt = name.substring(i, i + 1);
            if (i != 0 && charAt.equals(charAt.toUpperCase())) {
                fixedName += "_";
            }
            fixedName += charAt.toUpperCase();
        }
        return fixedName;
    }

}
