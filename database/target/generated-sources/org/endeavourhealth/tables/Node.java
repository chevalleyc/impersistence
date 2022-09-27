/*
 * This file is generated by jOOQ.
 */
package org.endeavourhealth.tables;


import java.sql.Timestamp;
import java.util.UUID;
import java.util.function.Function;

import org.endeavourhealth.Keys;
import org.endeavourhealth.Quadstore;
import org.endeavourhealth.tables.records.NodeRecord;
import org.endeavourhealth.udt.records.CodedTextRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function10;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row10;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Node extends TableImpl<NodeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>quadstore.node</code>
     */
    public static final Node NODE = new Node();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<NodeRecord> getRecordType() {
        return NodeRecord.class;
    }

    /**
     * The column <code>quadstore.node.id</code>.
     */
    public final TableField<NodeRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>quadstore.node.person_id</code>.
     */
    public final TableField<NodeRecord, UUID> PERSON_ID = createField(DSL.name("person_id"), SQLDataType.UUID, this, "");

    /**
     * The column <code>quadstore.node.organisation_id</code>.
     */
    public final TableField<NodeRecord, UUID> ORGANISATION_ID = createField(DSL.name("organisation_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>quadstore.node.iri</code>.
     */
    public final TableField<NodeRecord, String> IRI = createField(DSL.name("iri"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>quadstore.node.name</code>.
     */
    public final TableField<NodeRecord, CodedTextRecord> NAME = createField(DSL.name("name"), org.endeavourhealth.udt.CodedText.CODED_TEXT.getDataType(), this, "");

    /**
     * The column <code>quadstore.node.effective_date</code>.
     */
    public final TableField<NodeRecord, Timestamp> EFFECTIVE_DATE = createField(DSL.name("effective_date"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>quadstore.node.type</code>.
     */
    public final TableField<NodeRecord, Integer> TYPE = createField(DSL.name("type"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>quadstore.node.feeder_audit</code>.
     */
    public final TableField<NodeRecord, JSONB> FEEDER_AUDIT = createField(DSL.name("feeder_audit"), SQLDataType.JSONB, this, "");

    /**
     * The column <code>quadstore.node.properties</code>.
     */
    public final TableField<NodeRecord, JSONB> PROPERTIES = createField(DSL.name("properties"), SQLDataType.JSONB, this, "");

    /**
     * The column <code>quadstore.node.created_ts</code>.
     */
    public final TableField<NodeRecord, Timestamp> CREATED_TS = createField(DSL.name("created_ts"), SQLDataType.TIMESTAMP(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.TIMESTAMP)), this, "");

    private Node(Name alias, Table<NodeRecord> aliased) {
        this(alias, aliased, null);
    }

    private Node(Name alias, Table<NodeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>quadstore.node</code> table reference
     */
    public Node(String alias) {
        this(DSL.name(alias), NODE);
    }

    /**
     * Create an aliased <code>quadstore.node</code> table reference
     */
    public Node(Name alias) {
        this(alias, NODE);
    }

    /**
     * Create a <code>quadstore.node</code> table reference
     */
    public Node() {
        this(DSL.name("node"), null);
    }

    public <O extends Record> Node(Table<O> child, ForeignKey<O, NodeRecord> key) {
        super(child, key, NODE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Quadstore.QUADSTORE;
    }

    @Override
    public UniqueKey<NodeRecord> getPrimaryKey() {
        return Keys.NODE_PKEY;
    }

    @Override
    public Node as(String alias) {
        return new Node(DSL.name(alias), this);
    }

    @Override
    public Node as(Name alias) {
        return new Node(alias, this);
    }

    @Override
    public Node as(Table<?> alias) {
        return new Node(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Node rename(String name) {
        return new Node(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Node rename(Name name) {
        return new Node(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Node rename(Table<?> name) {
        return new Node(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<UUID, UUID, UUID, String, CodedTextRecord, Timestamp, Integer, JSONB, JSONB, Timestamp> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function10<? super UUID, ? super UUID, ? super UUID, ? super String, ? super CodedTextRecord, ? super Timestamp, ? super Integer, ? super JSONB, ? super JSONB, ? super Timestamp, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function10<? super UUID, ? super UUID, ? super UUID, ? super String, ? super CodedTextRecord, ? super Timestamp, ? super Integer, ? super JSONB, ? super JSONB, ? super Timestamp, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}