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

import org.eclipse.daanse.cwm.model.cwm.resource.relational.RelationalFactory;
import org.eclipse.daanse.cwm.model.cwm.resource.relational.Table;
import org.junit.jupiter.api.Test;

/** Rename markers on the new model — the artifact-friendly link source. */
class ChangeMarkersTest {

    @Test
    void marksAndReadsBack() {
        Table t = RelationalFactory.eINSTANCE.createTable();
        t.setName("customer_v2");

        assertThat(ChangeMarkers.renamedFrom(t)).isEmpty();

        ChangeMarkers.markRenamedFrom(t, "customer");
        assertThat(ChangeMarkers.renamedFrom(t)).contains("customer");
    }

    @Test
    void markingTwiceUpserts() {
        Table t = RelationalFactory.eINSTANCE.createTable();
        t.setName("customer_v2");

        ChangeMarkers.markRenamedFrom(t, "kunde");
        ChangeMarkers.markRenamedFrom(t, "customer");

        assertThat(ChangeMarkers.renamedFrom(t)).contains("customer");
        assertThat(t.getTaggedValue()).hasSize(1);
    }
}
