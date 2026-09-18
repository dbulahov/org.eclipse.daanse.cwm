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

import java.util.EnumSet;

import org.eclipse.daanse.cwm.model.cwm.resource.relational.Column;

/**
 * A structural change to a column matched (by identity, possibly also
 * renamed) between the old and new schema — type, size, nullability and/or
 * default value.
 *
 * @param oldColumn the column as it existed in the old table
 * @param newColumn the same column's declaration in the new table
 * @param aspects   which facets changed; never empty for an instance actually
 *                  emitted by {@code SchemaDiffer}
 */
public record ColumnChange(Column oldColumn, Column newColumn, EnumSet<Aspect> aspects) {

    public ColumnChange {
        aspects = aspects == null ? EnumSet.noneOf(Aspect.class) : EnumSet.copyOf(aspects);
    }

    /** A single facet of a column declaration the differ compares. */
    public enum Aspect {
        /** The SQL type name differs (case/whitespace-insensitive). */
        TYPE,
        /** Length, precision or scale differs. */
        SIZE,
        /** Nullability differs. */
        NULLABILITY,
        /** The default-value expression differs. */
        DEFAULT
    }
}
