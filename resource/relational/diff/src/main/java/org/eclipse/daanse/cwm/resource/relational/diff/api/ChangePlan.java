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

/**
 * A diff result together with its ordered, executable operation list — what
 * the emitters (SQL, Liquibase) consume.
 *
 * @param diff the structural diff the plan was derived from; may be null for
 *             marker-only plans (artifact case, no old model)
 * @param ops  the operations in execution order
 */
public record ChangePlan(SchemaDiff diff, List<ChangeOp> ops) {

    public ChangePlan {
        ops = List.copyOf(ops);
    }

    public boolean isEmpty() {
        return ops.isEmpty();
    }
}
