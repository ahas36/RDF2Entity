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
import sun.misc.OSEnvironment;

/**
 *
 * @author ali
 */
public class ImplGenerator {

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

    private static void generateJavaClass(String fileLocation, String outputFolder, Resource className, String namespace) {
        String result = "package org.mobivoc;\nimport com.google.common.collect.Multimap;\n"
                + "import com.google.schemaorg.SchemaOrgTypeImpl;\n"
                + "import com.google.schemaorg.ValueType;\n"
                + "import com.google.schemaorg.core.Thing;\npublic class " + className.getLocalName() + "Impl extends ";
        String extendString = " SchemaOrgTypeImpl ";
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
                    } else if (resource.getNameSpace().equalsIgnoreCase("http://schema.org/")) {
                        extendString = "com.google.schemaorg.core." + resource.getLocalName();
                    } else {
                        String[] split = resource.toString().replaceAll("http://", "").replaceAll("https://", "").split("/");
                        extendString = "";
                        for (String item : split) {
                            if (item.trim().isEmpty()) {
                                continue;
                            }
                        }
                        extendString = extendString.substring(0, extendString.length());
                    }
                } else {
                    if (className.getNameSpace().equalsIgnoreCase("http://schema.org/")) {
                        extendString = "com.google.schemaorg.core." + className.getLocalName();
                    } else {
                        String[] split = className.toString().replaceAll("http://", "").replaceAll("https://", "").split("/");
                        extendString = "";
                        for (String item : split) {
                            if (item.trim().isEmpty()) {
                                continue;
                            }
                        }
                        extendString = extendString.substring(0, extendString.length());
                    }
                }
            }
        }
        result += extendString + "Impl implements " + className.getLocalName() + " {\n";

        result = result + "public " + className.getLocalName() + "Impl(Multimap<String, ValueType> properties, Multimap<String, Thing> reverseMap) {\n"
                + "        super(properties, reverseMap);\n"
                + "    }\n";

        result = result + "static final class BuilderImpl extends SchemaOrgTypeImpl.BuilderImpl<" + className.getLocalName() + ".Builder>\n"
                + "            implements " + className.getLocalName() + ".Builder {";
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
                        result += " @Override\n"
                                + "        public " + className.getLocalName() + ".Builder add" + Capitalise(resource.getLocalName()) + "(" + Capitalise(subResource.getLocalName()) + " value) {\n";

                        result += "            return addProperty(CoreConstants.PROPERTY_" + fixNames(resource.getLocalName()) + ", value);\n}\n";
                        isExtraParams = true;
                    }
                }
            }
        }
        if (isExtraParams) {

            result += "}\n"
                    + "\n"
                    + "    @Override\n"
                    + "    public String getFullTypeName() {\n"
                    + "        return CoreConstants.TYPE_" + fixNames(className.getLocalName()) + ";\n"
                    + "    }\n"
                    + "\n"
                    + "}";
            saveFile(outputFolder, className.getLocalName() + "Impl", result);
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

            List<Resource> findClasses = ImplGenerator.findClasses(file);

            for (Resource findClasse : findClasses) {
                generateJavaClass(file, outputFolder, findClasse, namespace);
                //System.out.println(result);
            }

        }
    }

    private static String Capitalise(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
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
