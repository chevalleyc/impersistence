package org.endeavourhealth.visitor;

import org.endeavourhealth.visitor.fhir.FhirArbitraryJsonImpl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface ArbitraryJson {

    FhirArbitraryJsonImpl generate();

    boolean isList();

    boolean isMap();

    boolean isScalar();

    boolean isNull();

    Set<String> propertyNames();

    Integer size();

    Object getItem(Integer index);

    ArbitraryJson namedItem(String name);

    Object propertyValue(String name);

    ArbitraryJson propertyStructure(String name);

    ArbitraryJson deletePropertyStructure(String name);

    ArbitraryJson addPropertyStructure(String name, Object value);

    ArbitraryJson deleteItem(FhirArbitraryJsonImpl arbitraryJson);

    ArbitraryJson deleteItem(Map arbitraryJson);

    Iterator<Object> valuesIterator();

    boolean hasProperty(String reference_key);

    boolean isReference();

    boolean isExtensionReference();

    String toJson();

    Object toJEncodedJson();

    void merge(ArbitraryJson anotherArbitraryJson);
}
