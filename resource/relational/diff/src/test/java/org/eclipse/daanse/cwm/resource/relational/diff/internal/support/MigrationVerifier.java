/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.daanse.cwm.resource.relational.diff.internal.support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.daanse.sql.jdbc.api.meta.StructureInfo;
import org.eclipse.daanse.sql.jdbc.api.schema.ImportedKey;
import org.eclipse.daanse.sql.jdbc.api.schema.TableDefinition;
import org.eclipse.daanse.sql.model.schema.ColumnDefinition;
import org.eclipse.daanse.sql.model.schema.ColumnMetaData;
import org.eclipse.daanse.sql.model.schema.ColumnReference;
import org.eclipse.daanse.sql.model.schema.PrimaryKey;
import org.eclipse.daanse.sql.model.schema.SchemaReference;
import org.eclipse.daanse.sql.model.schema.TableReference;

/**
 * Reads the post-migration live structure ({@link StructureInfo}, snapshotted
 * via JDBC metadata) back down to the plain facts the {@code Diff*Test}s
 * assert on — table names, columns, primary key and foreign key shape —
 * scoped to one schema/table at a time.
 */
public final class MigrationVerifier {

    private MigrationVerifier() {
    }

    /** Names of the tables (or views) of the given {@code type} owned by {@code schemaName}. */
    public static List<String> tableNames(StructureInfo si, String schemaName, String type) {
        List<String> out = new ArrayList<>();
        for (TableDefinition td : si.tables()) {
            TableReference t = td.table();
            if (sameSchema(t.schema(), schemaName) && sameTableType(t.type(), type)) {
                out.add(t.name());
            }
        }
        return out;
    }

    /** Column name → metadata, for every column of {@code schemaName.tableName}. */
    public static Map<String, ColumnMetaData> columnsOf(StructureInfo si, String schemaName, String tableName) {
        Map<String, ColumnMetaData> out = new LinkedHashMap<>();
        for (ColumnDefinition cd : si.columns()) {
            ColumnReference ref = cd.column();
            Optional<TableReference> table = ref.table();
            if (table.isPresent() && sameSchema(table.get().schema(), schemaName)
                    && tableName.equals(table.get().name())) {
                out.put(ref.name(), cd.columnMetaData());
            }
        }
        return out;
    }

    /** Ordered primary-key column names of {@code schemaName.tableName}, empty if none. */
    public static List<String> primaryKeyColumns(StructureInfo si, String schemaName, String tableName) {
        for (PrimaryKey pk : si.primaryKeys()) {
            TableReference t = pk.table();
            if (sameSchema(t.schema(), schemaName) && tableName.equals(t.name())) {
                return pk.columns().stream().map(ColumnReference::name).toList();
            }
        }
        return List.of();
    }

    /**
     * Foreign keys owned by {@code schemaName.tableName}, one {@link ImportedKeyFacts}
     * per constraint — the per-column {@link ImportedKey} rows JDBC reports are
     * grouped by name and ordered by {@link ImportedKey#keySequence()}.
     */
    public static List<ImportedKeyFacts> foreignKeysOf(StructureInfo si, String schemaName, String tableName) {
        Map<String, List<ImportedKey>> byConstraint = new LinkedHashMap<>();
        for (ImportedKey ik : si.importedKeys()) {
            Optional<TableReference> fkTable = ik.foreignKeyColumn().table();
            if (fkTable.isEmpty() || !sameSchema(fkTable.get().schema(), schemaName)
                    || !tableName.equals(fkTable.get().name())) {
                continue;
            }
            byConstraint.computeIfAbsent(ik.name(), k -> new ArrayList<>()).add(ik);
        }
        List<ImportedKeyFacts> out = new ArrayList<>();
        for (List<ImportedKey> rows : byConstraint.values()) {
            rows.sort(Comparator.comparingInt(ImportedKey::keySequence));
            List<String> fkColumns = rows.stream().map(ik -> ik.foreignKeyColumn().name()).toList();
            List<String> referencedColumns = rows.stream().map(ik -> ik.primaryKeyColumn().name()).toList();
            String referencedTable = rows.get(0).primaryKeyColumn().table().map(TableReference::name).orElse(null);
            out.add(new ImportedKeyFacts(rows.get(0).name(), fkColumns, referencedTable, referencedColumns));
        }
        return out;
    }

    private static boolean sameSchema(Optional<SchemaReference> schema, String schemaName) {
        return schema.map(SchemaReference::name).map(n -> n.equals(schemaName)).orElse(schemaName == null);
    }

    /**
     * {@code true} when {@code actualType} (as a provider reports it) matches
     * {@code requestedType} ({@link TableReference#TYPE_TABLE}/{@link
     * TableReference#TYPE_VIEW}). H2's information_schema-flavoured provider
     * reports base tables as {@code "BASE TABLE"} rather than the classic JDBC
     * {@code "TABLE"} — treated as equivalent to {@link TableReference#TYPE_TABLE}.
     */
    private static boolean sameTableType(String actualType, String requestedType) {
        if (requestedType == null) {
            return true;
        }
        if (requestedType.equalsIgnoreCase(actualType)) {
            return true;
        }
        return TableReference.TYPE_TABLE.equalsIgnoreCase(requestedType) && "BASE TABLE".equalsIgnoreCase(actualType);
    }

    /**
     * One foreign key constraint, column order preserved.
     *
     * @param name               the constraint name
     * @param fkColumns          the referencing (child-side) columns, in
     *                           declaration order
     * @param referencedTable    the referenced (parent-side) table name
     * @param referencedColumns  the referenced columns, matching {@code
     *                           fkColumns} position-for-position
     */
    public record ImportedKeyFacts(String name, List<String> fkColumns, String referencedTable,
            List<String> referencedColumns) {
    }
}
