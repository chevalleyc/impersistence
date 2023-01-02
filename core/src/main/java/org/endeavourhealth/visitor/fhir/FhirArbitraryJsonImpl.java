package org.endeavourhealth.visitor.fhir;

import com.google.gson.GsonBuilder;
import java.util.Collections;
import org.endeavourhealth.support.EncodeUtil;
import org.endeavourhealth.visitor.ArbitraryJson;
import org.endeavourhealth.visitor.JsonHandlerFactory;
import org.endeavourhealth.visitor.ResourceFormat;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FhirArbitraryJsonImpl implements ArbitraryJson {

    protected String jsonStringOrigin;
    protected Object pojoJson;
    private final ResourceFormat fhirVersion;

    protected FhirArbitraryJsonImpl(ResourceFormat version, String jsonAsString) {
        this.fhirVersion = version;
        this.jsonStringOrigin = jsonAsString;
    }

    protected FhirArbitraryJsonImpl(ResourceFormat version, Object aPojoJson) {
        this.fhirVersion = version;
        this.pojoJson = aPojoJson;
    }

    protected Object toMap() {
        boolean isList = false;

        GsonBuilder gsondb = EncodeUtil.gsonBuilderInstance();
        if (jsonStringOrigin != null && jsonStringOrigin.startsWith("[")) {
                isList = true;
        }

       return gsondb.create().fromJson(jsonStringOrigin, isList ? List.class : Map.class);
    }

    @Override
    public FhirArbitraryJsonImpl generate(){
        pojoJson = toMap();
        return this;
    }

    @Override
    public boolean isList(){
        return pojoJson instanceof List<?>;
    }

    @Override
    public boolean isMap(){
        return pojoJson instanceof Map<?,?>;
    }

    @Override
    public boolean isScalar(){
        return !isList() && !isMap();
    }

    @Override
    public boolean isNull(){
        return pojoJson == null && jsonStringOrigin == null;
    }

    @Override
    public Set<String> propertyNames(){
        if (isMap())
            return ((Map)pojoJson).keySet();

        return Collections.emptySet();
    }

    @Override
    public Integer size(){
        if (isList())
            return ((List<Object>)pojoJson).size();

        return 0;
    }

    @Override
    public Object getItem(Integer index){
        if (isList())
            return ((List<Object>)pojoJson).get(0);

        return null;
    }

    @Override
    public ArbitraryJson namedItem(String name){
        if (pojoJson == null)
            return null;
        else
            return JsonHandlerFactory.getInstance(fhirVersion, ((Map)pojoJson).get(name));
    }

    @Override
    public Object propertyValue(String name){
        ArbitraryJson instance = namedItem(name);

        if (instance.isScalar())
            return instance.toJEncodedJson();

        return instance;
    }

    @Override
    public ArbitraryJson propertyStructure(String name){
        return namedItem(name);
    }

    @Override
    public ArbitraryJson deletePropertyStructure(String name){
        if (isMap() && propertyStructure(name) != null) {
                 ((Map)pojoJson).remove(name);

        }
        return JsonHandlerFactory.getInstance(fhirVersion,pojoJson);
    }

    @Override
    public ArbitraryJson addPropertyStructure(String name, Object value){
        if (isMap() && propertyStructure(name) != null) {
                ((Map)pojoJson).put(name, value);
        }
        return JsonHandlerFactory.getInstance(fhirVersion, pojoJson);
    }

    @Override
    public ArbitraryJson deleteItem(FhirArbitraryJsonImpl arbitraryJson){
        if (isList()) {
            ((List)pojoJson).remove(arbitraryJson);
        }
        return JsonHandlerFactory.getInstance(fhirVersion, pojoJson);
    }

    @Override
    public ArbitraryJson deleteItem(Map arbitraryJson){
        if (isList()) {
            ((List)pojoJson).remove(arbitraryJson);
        }
        return JsonHandlerFactory.getInstance(fhirVersion, pojoJson);
    }


    @Override
    public Iterator<Object> valuesIterator(){
        if (isList()){
            return ((List<Object>)pojoJson).iterator();
        }
        else if (isMap()){
            return ((Map)pojoJson).entrySet().iterator();
        }

        throw new IllegalStateException("cannot create iterator on scalar");
    }

    @Override
    public boolean hasProperty(String reference_key) {

        if (isMap()) {
                return ((Map)pojoJson).containsKey(reference_key);
        }
        else if (isList()){
            //iterates
            boolean hasProperty = true;
            for (Object item: ((List)pojoJson)){
                hasProperty = hasProperty && JsonHandlerFactory.getInstance(fhirVersion, item).hasProperty(reference_key);
            }
            return hasProperty;
        }

        return false;
    }

    @Override
    public boolean isReference(){
        return new FhirReference(null, this).isReference();
    }

    @Override
    public boolean isExtensionReference(){
        return new FhirReference(null, this).isExtensionReference();
    }


    @Override
    public String toJson(){
        return EncodeUtil.gsonBuilderInstance().setPrettyPrinting().create().toJson(pojoJson);
    }

    @Override
    public Object toJEncodedJson(){
        return pojoJson;
    }

    @Override
    public  void merge(ArbitraryJson anotherArbitraryJson){
        if (isMap()){
            if (!anotherArbitraryJson.isMap()){
                throw new IllegalArgumentException("Incompatible structure to merge, both must be either Map or List");
            }
            ((Map)pojoJson).putAll((Map) anotherArbitraryJson.toJEncodedJson());
        } else if (isList()){
            if (!anotherArbitraryJson.isList()){
                throw new IllegalArgumentException("Incompatible structure to merge, both must be either Map or List");
            }
            ((List)pojoJson).addAll((List) anotherArbitraryJson.toJEncodedJson());
        }
    }

    @Override
    public String toString(){
        return jsonStringOrigin;
    }


}
