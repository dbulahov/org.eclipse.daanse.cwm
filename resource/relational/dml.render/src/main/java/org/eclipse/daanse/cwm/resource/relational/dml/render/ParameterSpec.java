/*********************************************************************
* Copyright (c) 2026 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.daanse.cwm.resource.relational.dml.render;

import java.sql.JDBCType;

/**
 * One parameter of a generated template, in placeholder order.
 *
 * @param position one-based position in the SQL (JDBC order)
 * @param name     the source column's name ({@code offset}/{@code limit} for paging)
 * @param type     the JDBC type from the CWM column's SQLSimpleType, or {@code null}
 */
public record ParameterSpec(int position, String name, JDBCType type) {

    public ParameterSpec {
        if (position < 1) {
            throw new IllegalArgumentException("position must be >= 1");
        }
    }
}
