package org.endeavourhealth.visitor.fhir;

import com.google.gson.internal.LinkedTreeMap;
import org.endeavourhealth.support.KebabString;
import org.endeavourhealth.visitor.*;

import java.time.LocalDateTime;
import java.util.*;

public class FhirResourceVisitor implements ResourceVisitor {

    private final ResourceFormat resourceFormat;
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("fhir_identifiers");

    private UUID resourceId;
    private TripleReference organizationRef;
    private TripleReference personRef;
    private String name;
    private Optional<LocalDateTime> effectiveDate;
    private String resource;

    private Extension extension;

    private ArbitraryJson arbitraryJson;

    private List<TripleReference> tripleReferences = new ArrayList<>();

    public FhirResourceVisitor(ResourceFormat resourceFormat) {
        this.resourceFormat = resourceFormat;
    }

    public FhirResourceVisitor traverse(String resource){
        arbitraryJson = JsonHandlerFactory.getInstance(resourceFormat, resource).generate();
        resourceId = UUID.fromString((String) arbitraryJson.propertyValue(resourceBundle.getString("RESOURCE_ID_IDENTIFIER")));

        name = new KebabString(
                        Arrays.stream(
                                arbitraryJson
                                .propertyStructure(resourceBundle.getString("META_IDENTIFIER"))
                                .propertyStructure(resourceBundle.getString("META_PROFILE_IDENTIFIER"))
                                .getItem(0)
                                .toString()
                                .split("/")
                                ).reduce((first, last)->last)
                                .get()
                        ).toCamel();

        personRef = TripleReferenceFactory.getInstance(
                resourceFormat,
                resourceBundle.getString("PERSON_REF_IDENTIFIER"),
                arbitraryJson.propertyStructure(resourceBundle.getString("PERSON_REF_IDENTIFIER"))
        );
        organizationRef = TripleReferenceFactory.getInstance(
                resourceFormat,
                resourceBundle.getString("ORGANIZATION_REF_IDENTIFIER"),
                arbitraryJson.propertyStructure(resourceBundle.getString("ORGANIZATION_REF_IDENTIFIER"))
        );

        //delete the corresponding structure
        arbitraryJson.deletePropertyStructure(resourceBundle.getString("RESOURCE_ID_IDENTIFIER"));
        arbitraryJson.deletePropertyStructure(resourceBundle.getString("PERSON_REF_IDENTIFIER"));
        arbitraryJson.deletePropertyStructure(resourceBundle.getString("ORGANIZATION_REF_IDENTIFIER"));

        //process extension
        if (arbitraryJson.hasProperty(resourceBundle.getString("EXTENSION_IDENTIFIER"))){
            extension = new Extension(resourceFormat, arbitraryJson.propertyStructure(resourceBundle.getString("EXTENSION_IDENTIFIER"))).extract();
            arbitraryJson.deletePropertyStructure(resourceBundle.getString("EXTENSION_IDENTIFIER"));
        }

        String effectiveDateAsString = (String)arbitraryJson.propertyValue(resourceBundle.getString("EFFECTIVE_DATE_IDENTIFIER"));
        effectiveDate = effectiveDateAsString != null ? Optional.of(LocalDateTime.parse(effectiveDateAsString)) : Optional.empty();

        //push found references into list for DB resolution
        buildReferenceList();

        //merge the "master" json with the remaining extension json if any
        mergeExtensionJson(extension.extensionJson());

        //and merge extension references into the master reference list
        mergeExtensionReferences(extension);

        return this;
    }

    @Override
    public Optional<LocalDateTime> effectiveDate() {
        return effectiveDate;
    }

    @Override
    public String getResourceType() {
        return null;
    }

    private void mergeExtensionReferences(Extension extension) {
       extension.referenceIterator().forEachRemaining(reference -> tripleReferences.add(reference));
    }

    private void mergeExtensionJson(ArbitraryJson extensionJson){

        if (!extensionJson.isList()){
            throw new IllegalArgumentException("Incompatible structure to merge, extension must be a List");
        }

        if (extensionJson.size() > 0) {
            arbitraryJson.addPropertyStructure(resourceBundle.getString("EXTENSION_IDENTIFIER"), extensionJson.toJEncodedJson());
        }
    }

    private void buildReferenceList(){

        List<String> vaccumList = new ArrayList<>();

        arbitraryJson.valuesIterator().forEachRemaining(value -> {
            if (value instanceof Map.Entry){
                try {
                    LinkedTreeMap.Entry<String, Object> mapEntry = (LinkedTreeMap.Entry) value;
                    String referenceName = mapEntry.getKey();

                    if (JsonHandlerFactory.getInstance(resourceFormat, mapEntry.getValue()).isReference()) {
                        tripleReferences.add(TripleReferenceFactory.getInstance(
                                resourceFormat,
                                referenceName,
                                JsonHandlerFactory.getInstance(resourceFormat, mapEntry.getValue()))
                        );
                        vaccumList.add(referenceName);
                    }
                } catch (Exception e){}
            }
        });

        //repeat for extension references if any
//        extension.referenceIterator().forEachRemaining(tripleReferences::add);

        //closure
        for (String name: vaccumList){
            arbitraryJson.deletePropertyStructure(name);
        }

    }

    @Override
    public String propertiesToJson(){
        return arbitraryJson.toJson();
    }

    @Override
    public Integer propertiesSize(){
        return arbitraryJson.propertyNames().size();
    }

    @Override
    public UUID getResourceId() {
        return resourceId;
    }

    @Override
    public TripleReference getOrganizationRef() {
        return organizationRef;
    }

    @Override
    public UUID getOrganizationRefId() {
        return organizationRef.getReferencedUUID(0);
    }

    @Override
    public TripleReference getPersonRef() {
        return personRef;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public UUID getPersonRefId() {
        return personRef.getReferencedUUID(0);
    }

    @Override
    public Integer referencesSize(){
        return tripleReferences.size();
    }

    @Override
    public Iterator<TripleReference> referenceIterator(){
        return tripleReferences.iterator();
    }
}
