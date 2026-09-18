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

import java.sql.Types;

import org.eclipse.daanse.cwm.model.cwm.resource.relational.Column;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.PrimaryKey;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.RelationalFactory;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.SQLSimpleType;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Schema;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Table;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.enumerations.NullableType;

/**
 * A single-table, single-name-change fixture: {@code CUSTOMERS(ID PK,
 * <nameColumn> VARCHAR(100))}. Building the old and new schema with a
 * different {@code nameColumn} gives the differ's shape heuristic exactly one
 * dropped + one added column with an otherwise identical declaration — the
 * textbook rename case.
 */
public final class RenameFixtures {

    private static final RelationalFactory RF = RelationalFactory.eINSTANCE;

    private RenameFixtures() {
    }

    public static Schema build(String schemaName, String nameColumn) {
        Schema schema = RF.createSchema();
        schema.setName(schemaName);

        Table customers = RF.createTable();
        customers.setName("CUSTOMERS");
        Column id = col("ID", type("INTEGER", Types.INTEGER, 0), true);
        Column name = col(nameColumn, type("CHARACTER VARYING", Types.VARCHAR, 100), false);
        customers.getFeature().add(id);
        customers.getFeature().add(name);
        schema.getOwnedElement().add(customers);

        PrimaryKey pk = RF.createPrimaryKey();
        pk.setName("PK_CUSTOMERS");
        pk.getFeature().add(id);
        customers.getOwnedElement().add(pk);

        return schema;
    }

    private static SQLSimpleType type(String name, int jdbc, long charMax) {
        SQLSimpleType t = RF.createSQLSimpleType();
        t.setName(name);
        t.setTypeNumber(jdbc);
        if (charMax > 0) {
            t.setCharacterMaximumLength(charMax);
        }
        return t;
    }

    private static Column col(String name, SQLSimpleType type, boolean notNull) {
        Column c = RF.createColumn();
        c.setName(name);
        c.setType(type);
        c.setIsNullable(notNull ? NullableType.COLUMN_NO_NULLS : NullableType.COLUMN_NULLABLE);
        return c;
    }
}
