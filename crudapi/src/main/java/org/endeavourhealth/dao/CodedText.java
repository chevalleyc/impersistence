package org.endeavourhealth.dao;

import org.endeavourhealth.udt.records.CodePhraseRecord;
import org.endeavourhealth.udt.records.CodedTextRecord;

import java.util.Objects;

public class CodedText {

    private CodedTextRecord codedTextRecord = new CodedTextRecord();

    public CodedText(String value) {
        codedTextRecord.setValue(Objects.requireNonNull(value));
    }

    public CodedText setTerminologyProps(String terminologyId, String code){
        CodePhraseRecord codePhraseRecord = new CodePhraseRecord(terminologyId, code);
        codedTextRecord.setDefiningCode(codePhraseRecord);
        return this;
    }

    public CodedTextRecord record(){
        return codedTextRecord;
    }
}
