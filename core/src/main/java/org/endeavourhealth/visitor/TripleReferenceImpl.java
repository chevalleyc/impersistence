package org.endeavourhealth.visitor;

import org.endeavourhealth.visitor.properties.ReferencedItem;

import java.util.*;

public abstract class TripleReferenceImpl implements TripleReference {

    public static final String REFERENCE_KEY = "REFERENCE_KEY";
    protected ResourceBundle resourceBundle = ResourceBundle.getBundle("fhir_identifiers");

    protected ArbitraryJson referenceJson;
    protected List<ReferencedItem> referencedItemList = new ArrayList<>();
    protected String referenceName;

    @Override
    public boolean isReference(){
        return referenceJson.hasProperty(resourceBundle.getString(REFERENCE_KEY));
    }

    @Override
    public boolean isExtensionReference(){
        return referenceJson.hasProperty(resourceBundle.getString("EXTENSION_VALUE_REFERENCE_IDENTIFIER"));
    }

    protected TripleReference extract(){

        if (referenceJson == null || referenceJson.isNull())
            return this;


        String referenceValue = null;
        if (referenceJson.isReference())
            referenceValue = (String) referenceJson.propertyValue(resourceBundle.getString(REFERENCE_KEY));
        else if (referenceJson.isExtensionReference()){
            ArbitraryJson subRef = referenceJson.propertyStructure(resourceBundle.getString("EXTENSION_VALUE_REFERENCE_IDENTIFIER"));
            if (subRef.isReference()){
                referenceValue = (String) subRef.propertyValue(resourceBundle.getString(REFERENCE_KEY));
            }
        }


        try {
            if (referenceValue != null)
                referencedItemList.add(new ReferencedItem(referenceName, referenceValue.split("/")[0], UUID.fromString(referenceValue.split("/")[1])));
        } catch (Exception e){
            throw new IllegalArgumentException("malformed reference parsable:"+referenceValue);
        }

        return this;
    }

    @Override
    public Iterator<ReferencedItem> referenceIterator(){
        return referencedItemList.iterator();
    }

    @Override
    public String getReferencedItem(int index) {
        return referencedItemList.get(index).getReferencedItem();
    }

    @Override
    public UUID getReferencedUUID(int index){
        if (referencedItemList.size() == 0)
            return null;

        return referencedItemList.get(index).getReferencedUUID();
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }
}
