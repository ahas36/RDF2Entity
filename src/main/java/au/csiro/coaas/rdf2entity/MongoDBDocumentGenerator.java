/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.csiro.coaas.rdf2entity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ali
 */
public class MongoDBDocumentGenerator {

    public static void main(String[] args) throws IOException {
        InputStream is
                = new FileInputStream("/Users/ali/NetBeansProjects/RDF2Entity/src/main/java/au/csiro/coaas/rdf2entity/vals.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");

        JSONArray ja = new JSONArray(jsonTxt);

        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = new JSONObject("{\n"
                    + "  \"@context\": {\n"
                    + "    \"datatype\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#datatype\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"name\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#name\"\n"
                    + "    },\n"
                    + "    \"description\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#description\",\n"
                    + "      \"@type\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML\"\n"
                    + "    },\n"
                    + "    \"path\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#path\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"rest\": {\n"
                    + "      \"@id\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#rest\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"first\": {\n"
                    + "      \"@id\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#first\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"nodeKind\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#nodeKind\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"or\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#or\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"class\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#class\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"subClassOf\": {\n"
                    + "      \"@id\": \"http://www.w3.org/2000/01/rdf-schema#subClassOf\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"label\": {\n"
                    + "      \"@id\": \"http://www.w3.org/2000/01/rdf-schema#label\"\n"
                    + "    },\n"
                    + "    \"comment\": {\n"
                    + "      \"@id\": \"http://www.w3.org/2000/01/rdf-schema#comment\",\n"
                    + "      \"@type\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML\"\n"
                    + "    },\n"
                    + "    \"property\": {\n"
                    + "      \"@id\": \"http://www.w3.org/ns/shacl#property\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"supersededBy\": {\n"
                    + "      \"@id\": \"http://schema.org/supersededBy\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"equivalentClass\": {\n"
                    + "      \"@id\": \"http://www.w3.org/2002/07/owl#equivalentClass\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"sameAs\": {\n"
                    + "      \"@id\": \"http://schema.org/sameAs\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"category\": {\n"
                    + "      \"@id\": \"http://schema.org/category\"\n"
                    + "    },\n"
                    + "    \"versionInfo\": {\n"
                    + "      \"@id\": \"http://www.w3.org/2002/07/owl#versionInfo\",\n"
                    + "      \"@type\": \"http://www.w3.org/2001/XMLSchema#dateTime\"\n"
                    + "    },\n"
                    + "    \"imports\": {\n"
                    + "      \"@id\": \"http://www.w3.org/2002/07/owl#imports\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    },\n"
                    + "    \"schema\": \"http://schema.org/\",\n"
                    + "    \"rdf\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\",\n"
                    + "    \"owl\": \"http://www.w3.org/2002/07/owl#\",\n"
                    + "    \"sh\": \"http://www.w3.org/ns/shacl#\",\n"
                    + "    \"xsd\": \"http://www.w3.org/2001/XMLSchema#\",\n"
                    + "    \"rdfs\": \"http://www.w3.org/2000/01/rdf-schema#\"\n"
                    + "  }\n"
                    + "}");
            JSONObject arrayItem = ja.getJSONObject(i);
            Iterator<String> keys = arrayItem.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                jo.put(key, arrayItem.get(key));
            }
            System.out.println(jo.toString());
        }
    }
}
