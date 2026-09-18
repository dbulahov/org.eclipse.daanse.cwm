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


/**
 * Options for template generation.
 *
 * @param parameterStyle how parameters render ({@code ?}, {@code :name}, or literals)
 * @param includeSchema  qualify the table with its schema name
 */
public record DmlSettings(ParameterStyle parameterStyle, boolean includeSchema) {

    public static DmlSettings defaults() {
        return new DmlSettings(ParameterStyle.POSITIONAL, true);
    }

    public DmlSettings withParameterStyle(ParameterStyle style) {
        return new DmlSettings(style, includeSchema);
    }

    public DmlSettings withIncludeSchema(boolean include) {
        return new DmlSettings(parameterStyle, include);
    }
}
