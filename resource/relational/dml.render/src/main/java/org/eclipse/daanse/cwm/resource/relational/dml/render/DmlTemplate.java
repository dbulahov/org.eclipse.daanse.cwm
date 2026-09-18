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

import java.util.List;


/**
 * One generated statement template.
 *
 * @param kind       which template this is
 * @param sql        the dialect-specific SQL text
 * @param parameters the expected parameters in placeholder order (empty for
 *                   {@code SELECT_ALL} and literal-style templates)
 */
public record DmlTemplate(TemplateKind kind, String sql, List<ParameterSpec> parameters) {

    public DmlTemplate {
        parameters = List.copyOf(parameters);
    }
}
