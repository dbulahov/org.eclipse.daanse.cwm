/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   SmartCity Jena - initial
 *   Stefan Bischof (bipolis.org) - initial
 */
package org.eclipse.daanse.cwm.resource.relational.diff.api;

import org.eclipse.daanse.cwm.model.cwm.resource.relational.Schema;

/** Compares two CWM relational Schemas into a {@link SchemaDiff}. OSGi service. */
public interface SchemaDiffer {

    /** What was added, dropped, or changed going from {@code oldSchema} to {@code newSchema}. */
    SchemaDiff diff(Schema oldSchema, Schema newSchema);

    /**
     * Like {@link #diff(Schema, Schema)}, but with the identity sources and the
     * comparison scope of {@code settings} (tag markers, predecessor
     * Dependencies, heuristic — individually switchable; precedence
     * Dependency &gt; tag &gt; heuristic).
     */
    SchemaDiff diff(Schema oldSchema, Schema newSchema, DiffSettings settings);

    /** Diff plus ordered operation list in one call — what the emitters consume. */
    default ChangePlan plan(Schema oldSchema, Schema newSchema, DiffSettings settings) {
        SchemaDiff diff = diff(oldSchema, newSchema, settings);
        return new ChangePlan(diff, ChangePlanner.create().plan(diff));
    }
}
