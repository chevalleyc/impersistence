package org.endeavourhealth.visitor.fhir;

import org.endeavourhealth.visitor.ArbitraryJson;

import java.util.Objects;
import java.util.ResourceBundle;

public class FhirBundleHandler {

    private static final String MULTI_KEYS_DELIMITER = ";";
    private final ResourceBundle resourceBundle;
    private final ArbitraryJson arbitraryJson;
    private String effectiveKey;
    private boolean isCompositeKey = false;

    public FhirBundleHandler(String bundleId, ArbitraryJson arbitraryJson) {
        this.resourceBundle = ResourceBundle.getBundle(bundleId);
        this.arbitraryJson = arbitraryJson;
    }

    public String getKey(String key){
        return resourceBundle.getString(key);
    }

    private Object propertyValue(String key, boolean forValue){
        String actualKey = getKey(key);
        Object actualValue = null;
        if (isComposite(actualKey)){
            for (String subkey: actualKey.split(MULTI_KEYS_DELIMITER)) {
                Object propertyValue = forValue ? arbitraryJson.propertyValue(subkey) : arbitraryJson.propertyStructure(subkey);
                if (forValue ?
                        Objects.nonNull(propertyValue) :
                        Objects.nonNull(propertyValue) && (propertyValue instanceof ArbitraryJson && !((ArbitraryJson) propertyValue).isNull())) {
                    actualValue = forValue ? propertyValue.toString() : propertyValue;
                    effectiveKey = subkey;
                    isCompositeKey = true;
                    break;
                }
            }
        }
        else
            actualValue =  arbitraryJson.propertyValue(getKey(key));

        return actualValue;
    }

    public String propertyValue(String key){
        Object propertyVal = propertyValue(key, true);
        if (propertyVal == null)
            return null;
        else
            return propertyVal.toString();
    }

    public ArbitraryJson propertyStructure(String key){
        return (ArbitraryJson) propertyValue(key, false);
    }

    public void deletePropertyStructure(String key){
        arbitraryJson.deletePropertyStructure(getKey(key));
    }

    private boolean isComposite(String key){
        return key.contains(MULTI_KEYS_DELIMITER);
    }

    public String getEffectiveKey() {
        return effectiveKey;
    }

    public boolean isCompositeKey() {
        return isCompositeKey;
    }
}
