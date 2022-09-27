package org.endeavourhealth.visitor.fhir;

import org.endeavourhealth.support.KebabString;
import org.endeavourhealth.visitor.ResourceFormat;
import org.endeavourhealth.visitor.ArbitraryJson;
import org.endeavourhealth.visitor.TripleReference;
import org.endeavourhealth.visitor.TripleReferenceFactory;
import org.endeavourhealth.visitor.JsonHandlerFactory;

import java.util.*;

/**
 * traverse an extension json struct and resolve references if any
 */
public class Extension {

    private ArbitraryJson extensionJson;
    private List<TripleReference> fhirReferences = new ArrayList<>();
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("fhir_identifiers");
    private List<Map> vacuumList = new ArrayList<>();
    private final ResourceFormat fhirVersion;

    public Extension(ResourceFormat version, ArbitraryJson extensionJson) {
        this.extensionJson = extensionJson;
        this.fhirVersion = version;
    }

    public Extension extract(){
        if (!extensionJson.isList())
            throw new IllegalArgumentException("Malformed json input, extension must be an array");

        Iterator<Object> referenceIterator = extensionJson.valuesIterator();

        referenceIterator.forEachRemaining(extension -> {
            ArbitraryJson extensionArbitraryJson = JsonHandlerFactory.getInstance(fhirVersion, extension);

            //add a query key built from the last identifier of the url
            String lastIdentifier =
                new KebabString(
                        Arrays.stream(((String) extensionArbitraryJson.propertyValue(resourceBundle.getString("EXTENSION_URL_IDENTIFIER")))
                        .split("/"))
                        .reduce((first, last)->last)
                        .get()
                        .replace("-extension","")
                ).toCamel();

            if (lastIdentifier != null){
                extensionArbitraryJson.addPropertyStructure("key", lastIdentifier);
            }

            if (extensionArbitraryJson.hasProperty(resourceBundle.getString("EXTENSION_VALUE_REFERENCE_IDENTIFIER"))){
                String url = (String) extensionArbitraryJson.propertyValue(resourceBundle.getString("EXTENSION_URL_IDENTIFIER"));
                ArbitraryJson extensionReference = extensionArbitraryJson.propertyStructure(resourceBundle.getString("EXTENSION_VALUE_REFERENCE_IDENTIFIER"));
                if (extensionReference.isReference()){
                    fhirReferences.add(TripleReferenceFactory.getInstance(fhirVersion, lastIdentifier, extensionArbitraryJson));
                    vacuumList.add((Map)extension);
                }
            }
        });

        return this.vacuum();
    }

    public Extension vacuum(){
        for (Map toVacuum: vacuumList){
            extensionJson.deleteItem(toVacuum);
        }

        return this;
    }

    public Iterator<TripleReference> referenceIterator(){
        return fhirReferences.iterator();
    }

    public Integer referenceListSize(){
        return fhirReferences.size();
    }

    public ArbitraryJson extensionJson(){
        return extensionJson;
    }

}
