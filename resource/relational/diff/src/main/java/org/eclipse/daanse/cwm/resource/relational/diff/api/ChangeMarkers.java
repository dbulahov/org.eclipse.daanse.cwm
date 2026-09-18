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

import java.util.Optional;

import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.ModelElement;
import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.TaggedValue;
import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.util.TaggedValues;

/**
 * TaggedValue-based change markers on the NEW model — the artifact-friendly
 * link source: it needs no old model in memory and survives XMI interchange.
 *
 * <p>{@code daanse.change.renamedFrom = "old_name"} on a Table, Column or View
 * tells the differ that this element used to carry that name, replacing the
 * shape heuristic for it.</p>
 */
public final class ChangeMarkers {

    /** Tag on the new element naming its previous name in the old schema. */
    public static final String TAG_RENAMED_FROM = "daanse.change.renamedFrom";

    private ChangeMarkers() {
    }

    /** The old name this element was renamed from, if marked. */
    public static Optional<String> renamedFrom(ModelElement element) {
        return TaggedValues.value(element, TAG_RENAMED_FROM);
    }

    /** Marks the element as renamed from {@code oldName} (upsert semantics). */
    public static TaggedValue markRenamedFrom(ModelElement element, String oldName) {
        return TaggedValues.set(element, TAG_RENAMED_FROM, oldName);
    }
}
