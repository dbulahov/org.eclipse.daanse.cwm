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
import java.util.Optional;

import org.eclipse.daanse.cwm.model.cwm.resource.relational.Table;

/**
 * All templates generated for one table. Kinds that don't apply (e.g.
 * {@code SELECT_BY_PK} without a primary key) are simply absent.
 *
 * @param table     the source table
 * @param templates the generated templates, one per applicable {@link TemplateKind}
 */
public record DmlTemplates(Table table, List<DmlTemplate> templates) {

    public DmlTemplates {
        templates = List.copyOf(templates);
    }

    public Optional<DmlTemplate> find(TemplateKind kind) {
        return templates.stream().filter(t -> t.kind() == kind).findFirst();
    }
}
