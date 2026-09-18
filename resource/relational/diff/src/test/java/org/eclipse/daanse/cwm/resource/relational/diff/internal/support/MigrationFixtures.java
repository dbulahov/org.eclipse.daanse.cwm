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

import org.eclipse.daanse.cwm.model.cwm.foundation.datatypes.DatatypesFactory;
import org.eclipse.daanse.cwm.model.cwm.foundation.datatypes.QueryExpression;
import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.CoreFactory;
import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.Expression;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Column;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.ForeignKey;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.PrimaryKey;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.RelationalFactory;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.SQLSimpleType;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Schema;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Table;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.View;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.enumerations.NullableType;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.enumerations.ReferentialRuleType;
import org.eclipse.daanse.sql.dialect.api.Dialect;

/**
 * Old/new schema pair exercising one of every change kind the
 * differ/planner/emitter have to cooperate on: a widened column, an added
 * column with a default, a new table with its own PK and an FK to the
 * existing table, and a new view.
 *
 * <p>{@code buildOld}: {@code CUSTOMERS(ID PK, EMAIL VARCHAR(100))}.
 * {@code buildNew}: the above with {@code EMAIL} widened to {@code VARCHAR(200)},
 * plus a defaulted {@code REGION} column, plus {@code AUDIT_LOG(ID PK,
 * CUSTOMER_ID FK -> CUSTOMERS.ID, MESSAGE)}, plus a {@code CUSTOMER_SUMMARY}
 * view over {@code CUSTOMERS}.</p>
 */
public final class MigrationFixtures {

    private static final RelationalFactory RF = RelationalFactory.eINSTANCE;
    private static final CoreFactory CF = CoreFactory.eINSTANCE;
    private static final DatatypesFactory DF = DatatypesFactory.eINSTANCE;

    private MigrationFixtures() {
    }

    public static Schema buildOld(String schemaName, Dialect dialect) {
        Schema schema = RF.createSchema();
        schema.setName(schemaName);

        Table customers = RF.createTable();
        customers.setName("CUSTOMERS");
        Column id = col("ID", type("INTEGER", Types.INTEGER, 0), true);
        Column email = col("EMAIL", type("CHARACTER VARYING", Types.VARCHAR, 100), false);
        customers.getFeature().add(id);
        customers.getFeature().add(email);
        schema.getOwnedElement().add(customers);

        PrimaryKey pk = RF.createPrimaryKey();
        pk.setName("PK_CUSTOMERS");
        pk.getFeature().add(id);
        customers.getOwnedElement().add(pk);

        return schema;
    }

    public static Schema buildNew(String schemaName, Dialect dialect) {
        Schema schema = RF.createSchema();
        schema.setName(schemaName);

        Table customers = RF.createTable();
        customers.setName("CUSTOMERS");
        Column id = col("ID", type("INTEGER", Types.INTEGER, 0), true);
        Column email = col("EMAIL", type("CHARACTER VARYING", Types.VARCHAR, 200), false);
        Column region = col("REGION", type("CHARACTER VARYING", Types.VARCHAR, 50), false);
        Expression regionDefault = CF.createExpression();
        regionDefault.setLanguage("SQL");
        regionDefault.setBody("'EU'");
        region.setInitialValue(regionDefault);
        customers.getFeature().add(id);
        customers.getFeature().add(email);
        customers.getFeature().add(region);
        schema.getOwnedElement().add(customers);

        PrimaryKey customersPk = RF.createPrimaryKey();
        customersPk.setName("PK_CUSTOMERS");
        customersPk.getFeature().add(id);
        customers.getOwnedElement().add(customersPk);

        Table auditLog = RF.createTable();
        auditLog.setName("AUDIT_LOG");
        Column alId = col("ID", type("INTEGER", Types.INTEGER, 0), true);
        Column alCustomerId = col("CUSTOMER_ID", type("INTEGER", Types.INTEGER, 0), true);
        Column alMessage = col("MESSAGE", type("CHARACTER VARYING", Types.VARCHAR, 255), false);
        auditLog.getFeature().add(alId);
        auditLog.getFeature().add(alCustomerId);
        auditLog.getFeature().add(alMessage);
        schema.getOwnedElement().add(auditLog);

        PrimaryKey auditPk = RF.createPrimaryKey();
        auditPk.setName("PK_AUDIT_LOG");
        auditPk.getFeature().add(alId);
        auditLog.getOwnedElement().add(auditPk);

        ForeignKey auditFk = RF.createForeignKey();
        auditFk.setName("FK_AUDIT_LOG_CUSTOMER");
        auditFk.getFeature().add(alCustomerId);
        auditFk.setUniqueKey(customersPk);
        auditLog.getOwnedElement().add(auditFk);
        auditFk.setDeleteRule(ReferentialRuleType.IMPORTED_KEY_CASCADE);
        auditFk.setUpdateRule(ReferentialRuleType.IMPORTED_KEY_NO_ACTION);

        View summary = RF.createView();
        summary.setName("CUSTOMER_SUMMARY");
        QueryExpression qe = DF.createQueryExpression();
        qe.setLanguage("SQL");
        String custQ = dialect.quoteIdentifier(schemaName, "CUSTOMERS").toString();
        String idQ = dialect.quoteIdentifier("ID").toString();
        String emailQ = dialect.quoteIdentifier("EMAIL").toString();
        qe.setBody("SELECT " + idQ + ", " + emailQ + " FROM " + custQ);
        summary.setQueryExpression(qe);
        schema.getOwnedElement().add(summary);

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
