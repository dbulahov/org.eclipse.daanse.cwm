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

import java.util.List;

import org.eclipse.daanse.sql.dialect.api.Dialect;

/**
 * Renders an ordered {@link ChangeOp} plan as {@code ALTER}/{@code DROP}/
 * {@code CREATE} statements for one dialect. The emitter translates operation
 * by operation and does not reorder — ordering is the {@link ChangePlanner}'s
 * job. OSGi service.
 */
public interface MigrationEmitter {

    List<String> emit(List<ChangeOp> ops, Dialect dialect);

    /**
     * @param cascadeOnDrop append {@code CASCADE} to {@code DROP TABLE} so PG
     *                     also drops dependent views/foreign keys outside the plan
     */
    List<String> emit(List<ChangeOp> ops, Dialect dialect, boolean cascadeOnDrop);
}
