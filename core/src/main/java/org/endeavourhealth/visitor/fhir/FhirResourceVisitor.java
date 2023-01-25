package org.endeavourhealth.visitor.fhir;

import org.endeavourhealth.support.KebabString;
import org.endeavourhealth.visitor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

public class FhirResourceVisitor implements ResourceVisitor {

    public static final String PERSON_REF_IDENTIFIER = "PERSON_REF_IDENTIFIER";
    public static final String ORGANIZATION_REF_IDENTIFIER = "ORGANIZATION_REF_IDENTIFIER";
    public static final String EXTENSION_IDENTIFIER = "EXTENSION_IDENTIFIER";
    private final ResourceFormat resourceFormat;

    private static final String BUNDLE_ID = "fhir_identifiers";

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    private UUID resourceId;
    private TripleReference organizationRef;
    private TripleReference personRef;
    private String name;
    private String type;
    private Optional<LocalDate> effectiveDate;

    private Extension extension;

    private ArbitraryJson arbitraryJson;

    private List<TripleReference> tripleReferences = new ArrayList<>();

    public FhirResourceVisitor(ResourceFormat resourceFormat) {
        this.resourceFormat = resourceFormat;
    }

    public FhirResourceVisitor traverse(String resource){
        arbitraryJson = JsonHandlerFactory.getInstance(resourceFormat, resource).generate();
        
        FhirBundleHandler fhirBundleHandler = new FhirBundleHandler(BUNDLE_ID, arbitraryJson);
        
        resourceId = UUID.fromString(fhirBundleHandler.propertyValue("RESOURCE_ID_IDENTIFIER"));
        type = fhirBundleHandler.propertyValue("RESOURCE_TYPE");
        name = new KebabString(
                        Arrays.stream(
                                fhirBundleHandler.propertyStructure("META_IDENTIFIER")
                                .propertyStructure(fhirBundleHandler.getKey("META_PROFILE_IDENTIFIER"))
                                .getItem(0)
                                .toString()
                                .split("/")
                                ).reduce((first, last)->last)
                                .get()
                        ).toCamel();

        personRef = TripleReferenceFactory.getInstance(
                resourceFormat,
                fhirBundleHandler.getKey(PERSON_REF_IDENTIFIER),
                fhirBundleHandler.propertyStructure(PERSON_REF_IDENTIFIER)
        );
        organizationRef = TripleReferenceFactory.getInstance(
                resourceFormat,
                fhirBundleHandler.getKey(ORGANIZATION_REF_IDENTIFIER),
                fhirBundleHandler.propertyStructure(ORGANIZATION_REF_IDENTIFIER)
        );

        //delete the corresponding structure
        fhirBundleHandler.deletePropertyStructure("RESOURCE_ID_IDENTIFIER");
        fhirBundleHandler.deletePropertyStructure(PERSON_REF_IDENTIFIER);
        fhirBundleHandler.deletePropertyStructure(ORGANIZATION_REF_IDENTIFIER);

        //process extension
        if (arbitraryJson.hasProperty(fhirBundleHandler.getKey(EXTENSION_IDENTIFIER))){
            extension = new Extension(resourceFormat, fhirBundleHandler.propertyStructure(EXTENSION_IDENTIFIER)).extract();
            fhirBundleHandler.deletePropertyStructure(EXTENSION_IDENTIFIER);
        }

        String effectiveDateAsString = fhirBundleHandler.propertyValue("EFFECTIVE_DATE_IDENTIFIER");
        effectiveDate = effectiveDateAsString != null ? Optional.of(new EffectiveDate(effectiveDateAsString).parse()) : Optional.empty();

        //push found references into list for DB resolution
        buildReferenceList();

        if (extension != null) {
            //merge the "master" json with the remaining extension json if any
            mergeExtensionJson(extension.extensionJson());

            //and merge extension references into the master reference list
            mergeExtensionReferences(extension);
        }

        return this;
    }

    @Override
    public Optional<LocalDate> effectiveDate() {
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

        FhirBundleHandler fhirBundleHandler = new FhirBundleHandler(BUNDLE_ID, null);

        if (!extensionJson.isList()){
            throw new IllegalArgumentException("Incompatible structure to merge, extension must be a List");
        }

        if (extensionJson.size() > 0) {
            arbitraryJson.addPropertyStructure(fhirBundleHandler.getKey(EXTENSION_IDENTIFIER), extensionJson.toJEncodedJson());
        }
    }

    private void buildReferenceList(){

        List<String> vaccumList = new ArrayList<>();

        arbitraryJson.valuesIterator().forEachRemaining(value -> {
            if (value instanceof Map.Entry){
                try {
                    Map.Entry<String, Object> mapEntry = (Map.Entry) value;
                    String referenceName = mapEntry.getKey();

                    if (JsonHandlerFactory.getInstance(resourceFormat, mapEntry.getValue()).isReference()) {
                        tripleReferences.add(TripleReferenceFactory.getInstance(
                                resourceFormat,
                                referenceName,
                                JsonHandlerFactory.getInstance(resourceFormat, mapEntry.getValue()))
                        );
                        vaccumList.add(referenceName);
                    }
                } catch (Exception e){
                    logger.error(e.getMessage());
                }
            }
        });

        //closure
        for (String nameFromVacuumList: vaccumList){
            arbitraryJson.deletePropertyStructure(nameFromVacuumList);
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

    @Override
    public String getType() {
        return type;
    }
}
