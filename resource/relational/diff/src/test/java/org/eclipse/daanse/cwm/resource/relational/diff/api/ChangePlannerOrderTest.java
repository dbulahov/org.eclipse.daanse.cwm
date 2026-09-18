/*********************************************************************
* Copyright (c) 2026 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.daanse.cwm.resource.relational.diff.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Types;
import java.util.List;

import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.Classifier;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Column;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.ForeignKey;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.PrimaryKey;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.RelationalFactory;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.SQLSimpleType;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Schema;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Table;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.enumerations.NullableType;
import org.eclipse.daanse.cwm.resource.relational.diff.internal.SchemaDifferImpl;
import org.junit.jupiter.api.Test;

/**
 * Planner topology:
 * PK modification for incoming FKs — currently the documented gap in the emitter.
 * Offline, no database.
 */
class ChangePlannerOrderTest {

    private static final RelationalFactory R = RelationalFactory.eINSTANCE;

    @Test
    void pkChangeDropsAndReaddsInboundForeignKeysAroundTheRebuild() {
        Schema oldS = customerOrders(false);
        Schema newS = customerOrders(true); // customer-PK um Spalte erweitert

        SchemaDiff diff = new SchemaDifferImpl().diff(oldS, newS);
        List<ChangeOp> ops = ChangePlanner.create().plan(diff);

        int dropFk = indexOf(ops, ChangeOp.DropForeignKey.class);
        int dropPk = indexOf(ops, ChangeOp.DropPrimaryKey.class);
        int addPk = indexOf(ops, ChangeOp.AddPrimaryKey.class);
        int addFk = indexOf(ops, ChangeOp.AddForeignKey.class);

        assertThat(dropFk).isLessThan(dropPk);
        assertThat(dropPk).isLessThan(addPk);
        assertThat(addPk).isLessThan(addFk);
    }

    @Test
    void markersOnlyPlanYieldsPureRenames() {
        Schema newS = customerOrders(false);
        Table customer = table(newS, "customer");
        ChangeMarkers.markRenamedFrom(customer, "kunde");

        List<ChangeOp> ops = ChangePlanner.create().planMarkersOnly(newS);

        assertThat(ops).hasSize(1);
        assertThat(ops.get(0)).isInstanceOf(ChangeOp.RenameTable.class);
        assertThat(((ChangeOp.RenameTable) ops.get(0)).oldName()).isEqualTo("kunde");
    }

    // fixture

    /** customer(id PK[, region]) ←FK— orders(customer_id). */
    private static Schema customerOrders(boolean widerPk) {
        Schema s = R.createSchema();
        s.setName("sales");
        SQLSimpleType tInt = R.createSQLSimpleType();
        tInt.setName("INTEGER");
        tInt.setTypeNumber(Types.INTEGER);
        s.getOwnedElement().add(tInt);

        Table customer = R.createTable();
        customer.setName("customer");
        s.getOwnedElement().add(customer);
        Column cId = column(customer, "id", tInt);
        Column cRegion = column(customer, "region", tInt);
        PrimaryKey pk = R.createPrimaryKey();
        pk.setName("pk_customer");
        pk.getFeature().add(cId);
        if (widerPk) {
            pk.getFeature().add(cRegion);
        }
        customer.getOwnedElement().add(pk);

        Table orders = R.createTable();
        orders.setName("orders");
        s.getOwnedElement().add(orders);
        Column oCust = column(orders, "customer_id", tInt);
        ForeignKey fk = R.createForeignKey();
        fk.setName("fk_orders_customer");
        fk.getFeature().add(oCust);
        fk.setUniqueKey(pk);
        orders.getOwnedElement().add(fk);
        return s;
    }

    private static Column column(Table table, String name, SQLSimpleType type) {
        Column c = R.createColumn();
        c.setName(name);
        c.setType((Classifier) type);
        c.setIsNullable(NullableType.COLUMN_NO_NULLS);
        table.getFeature().add(c);
        return c;
    }

    private static Table table(Schema s, String name) {
        return s.getOwnedElement().stream()
                .filter(Table.class::isInstance).map(Table.class::cast)
                .filter(t -> name.equals(t.getName())).findFirst().orElseThrow();
    }

    private static int indexOf(List<ChangeOp> ops, Class<? extends ChangeOp> type) {
        for (int i = 0; i < ops.size(); i++) {
            if (type.isInstance(ops.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
