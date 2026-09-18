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

import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.CoreFactory;
import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.Dependency;
import org.eclipse.daanse.cwm.model.cwm.objectmodel.core.ModelElement;

/**
 * Dependency-based predecessor links between the NEW and the OLD model — the
 * CWM-pure link source when both models are loaded. The new element is the
 * {@code client} (it knows where it came from), the old element the
 * {@code supplier}; both ends are 1..* so split/merge is expressible.
 *
 * <p>Reading uses the forward direction only ({@code clientDependency} on the
 * new element), so no {@code ECrossReferenceAdapter} is needed for the diff.</p>
 */
public final class PredecessorLinks {

    /** {@link Dependency#getKind() kind} marking a new→old predecessor link. */
    public static final String KIND_PREDECESSOR = "predecessor";

    private PredecessorLinks() {
    }

    /**
     * Links {@code newElement} to its predecessor {@code oldElement}. The
     * Dependency is owned by the new element's namespace when it has one
     * (otherwise the caller must attach it before serializing).
     */
    public static Dependency link(ModelElement newElement, ModelElement oldElement) {
        Dependency d = CoreFactory.eINSTANCE.createDependency();
        d.setName(newElement.getName() + "_predecessor");
        d.setKind(KIND_PREDECESSOR);
        d.getClient().add(newElement);
        d.getSupplier().add(oldElement);
        if (newElement.getNamespace() != null) {
            newElement.getNamespace().getOwnedElement().add(d);
        }
        return d;
    }

    /** The predecessors of {@code newElement} (empty without links). */
    public static List<ModelElement> predecessors(ModelElement newElement) {
        return newElement.getClientDependency().stream()
                .filter(d -> KIND_PREDECESSOR.equals(d.getKind()))
                .flatMap(d -> d.getSupplier().stream())
                .toList();
    }
}
