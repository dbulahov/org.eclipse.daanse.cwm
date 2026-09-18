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

import org.eclipse.daanse.cwm.model.cwm.resource.relational.View;

/**
 * A same-named view whose query body differs between the old and new schema.
 *
 * @param oldView the view as it existed in the old schema
 * @param newView the same view's declaration in the new schema
 */
public record ViewBodyChange(View oldView, View newView) {
}
