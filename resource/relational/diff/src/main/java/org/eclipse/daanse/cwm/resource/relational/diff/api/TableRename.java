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

import org.eclipse.daanse.cwm.model.cwm.resource.relational.Table;

/**
 * A table rename detected by the differ: {@code oldTable} and {@code newTable}
 * were resolved to the same identity (predecessor Dependency, {@code
 * renamedFrom} marker, or the conservative shape heuristic) but carry
 * different names.
 *
 * @param oldTable the table as it existed in the old schema
 * @param newTable the same table's declaration in the new schema
 */
public record TableRename(Table oldTable, Table newTable) {
}
