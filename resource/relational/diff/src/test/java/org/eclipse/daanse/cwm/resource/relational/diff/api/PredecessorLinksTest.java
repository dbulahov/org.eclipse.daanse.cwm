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

import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.Dependency;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.RelationalFactory;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Schema;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Table;
import org.junit.jupiter.api.Test;

/** Predecessor dependencies between new and old model elements. */
class PredecessorLinksTest {

    private static final RelationalFactory R = RelationalFactory.eINSTANCE;

    @Test
    void linkIsReadableForwardWithoutCrossReferencer() {
        Table oldTable = R.createTable();
        oldTable.setName("customer");
        Table newTable = R.createTable();
        newTable.setName("customer_v2");

        Dependency d = PredecessorLinks.link(newTable, oldTable);

        assertThat(d.getKind()).isEqualTo(PredecessorLinks.KIND_PREDECESSOR);
        assertThat(PredecessorLinks.predecessors(newTable)).containsExactly(oldTable);
        assertThat(PredecessorLinks.predecessors(oldTable)).isEmpty();
    }

    @Test
    void linkIsOwnedByTheNewElementsNamespace() {
        Schema s = R.createSchema();
        s.setName("sales");
        Table oldTable = R.createTable();
        oldTable.setName("customer");
        Table newTable = R.createTable();
        newTable.setName("customer_v2");
        s.getOwnedElement().add(newTable);

        Dependency d = PredecessorLinks.link(newTable, oldTable);

        assertThat(s.getOwnedElement()).contains(d);
    }

    @Test
    void splitIsExpressible() {
        // one old table split into two new ones: two links sharing the supplier
        Table oldTable = R.createTable();
        oldTable.setName("customer");
        Table names = R.createTable();
        names.setName("customer_name");
        Table mails = R.createTable();
        mails.setName("customer_mail");

        PredecessorLinks.link(names, oldTable);
        PredecessorLinks.link(mails, oldTable);

        assertThat(PredecessorLinks.predecessors(names)).containsExactly(oldTable);
        assertThat(PredecessorLinks.predecessors(mails)).containsExactly(oldTable);
    }
}
