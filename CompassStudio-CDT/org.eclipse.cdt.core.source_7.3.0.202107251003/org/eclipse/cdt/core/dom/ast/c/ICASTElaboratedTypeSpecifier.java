/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    John Camelon (IBM Rational Software) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.core.dom.ast.c;

import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;

/**
 * C's elaborated type specifier. (same as IASTElaboratedTypeSpecifier, except
 * for the addition of the restrict keyword.
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ICASTElaboratedTypeSpecifier extends IASTElaboratedTypeSpecifier, ICASTDeclSpecifier {

	/**
	 * @since 5.1
	 */
	@Override
	public ICASTElaboratedTypeSpecifier copy();

	/**
	 * @since 5.3
	 */
	@Override
	public ICASTElaboratedTypeSpecifier copy(CopyStyle style);
}