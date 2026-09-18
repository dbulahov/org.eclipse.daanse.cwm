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

import org.eclipse.daanse.cwm.model.cwm.resource.relational.PrimaryKey;

/**
 * The primary key of a matched table was added, dropped, or rebuilt (column
 * list or name changed).
 *
 * @param oldPk the primary key as it existed in the old table, or
 *              {@code null} when one is being added where there was none
 * @param newPk the primary key in the new table, or {@code null} when the old
 *              one is being dropped outright
 */
public record PrimaryKeyChange(PrimaryKey oldPk, PrimaryKey newPk) {
}
