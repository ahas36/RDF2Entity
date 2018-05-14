/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.csiro.coaas.rdf2entity;

/**
 *
 * @author ali
 */
public class Execute {

    public static void execute(String nameSpace, String ouputFolder, String... files) {
        CoreConstantsGenerator.generate(nameSpace, ouputFolder, files);
        InterfaceGenerator.generate(nameSpace, ouputFolder, files);
        ImplGenerator.generate(nameSpace, ouputFolder, files);
    }

    public static void main(String[] args) {
        Execute.execute("http://schema.mobivoc.org/", "/Users/ali/Project/semantic", "/Users/ali/NetBeansProjects/RDF2Entity/files/mobivoc/core.ttl",
                "/Users/ali/NetBeansProjects/RDF2Entity/files/mobivoc/Parking.ttl", "/Users/ali/NetBeansProjects/RDF2Entity/files/mobivoc/ChargingPoints.ttl");
    }
}
