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

import java.util.List;

import org.eclipse.daanse.cwm.model.cwm.resource.relational.Schema;

/**
 * Turns a {@link SchemaDiff} into an ordered {@link ChangeOp} list. This is
 * where the execution-order topology lives (today wired procedurally inside
 * {@code MigrationEmitterImpl}): drops before adds, FKs around PK rebuilds,
 * views last — including the previously unsupported "PK change on a table
 * with inbound FKs" (drop/re-add the referencing FKs around the rebuild) and
 * rename ops instead of drop+create where the dialect supports them.
 */
public interface ChangePlanner {

    /** Plans the ordered operations for a full diff. */
    List<ChangeOp> plan(SchemaDiff diff);

    /**
     * Artifact case: derives rename ops from {@link ChangeMarkers} on the new
     * schema alone — no old model needed.
     */
    List<ChangeOp> planMarkersOnly(Schema newSchema);

    /** A stateless planner instance. */
    static ChangePlanner create() {
        return new org.eclipse.daanse.cwm.resource.relational.diff.internal.ChangePlannerImpl();
    }
}
