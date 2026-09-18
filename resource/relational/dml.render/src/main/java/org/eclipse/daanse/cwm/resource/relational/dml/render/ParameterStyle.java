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

/** How parameters render in the generated templates. */
public enum ParameterStyle {
    /** JDBC positional placeholders: {@code ?} (or the dialect's indexed marker, e.g. {@code $1}). */
    POSITIONAL,
    /** Named placeholders: {@code :name}. */
    NAMED
}
