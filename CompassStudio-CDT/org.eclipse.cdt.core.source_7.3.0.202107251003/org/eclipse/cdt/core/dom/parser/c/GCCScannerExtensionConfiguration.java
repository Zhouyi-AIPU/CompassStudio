/*******************************************************************************
 * Copyright (c) 2004, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *     Ed Swartz (Nokia)
 *     Markus Schorn (Wind River Systems)
 *     Sergey Prigogin (Google)
 *******************************************************************************/
package org.eclipse.cdt.core.dom.parser.c;

import java.util.Map;

import org.eclipse.cdt.core.dom.parser.GNUScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.GCCKeywords;
import org.eclipse.cdt.core.parser.IGCCToken;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IToken;

/**
 * Configures the preprocessor for parsing c-sources as accepted by gcc.
 */
public class GCCScannerExtensionConfiguration extends GNUScannerExtensionConfiguration {
	private static enum CompilerType {
		GCC, MSVC
	}

	private static final int VERSION_4_2 = version(4, 2);
	private static final int VERSION_4_7 = version(4, 7);
	private static GCCScannerExtensionConfiguration CONFIG = new GCCScannerExtensionConfiguration();
	private static GCCScannerExtensionConfiguration CONFIG_4_2 = new GCCScannerExtensionConfiguration(VERSION_4_2);
	private static GCCScannerExtensionConfiguration CONFIG_4_7 = new GCCScannerExtensionConfiguration(VERSION_4_7);
	private static GCCScannerExtensionConfiguration CONFIG_MSVC = new GCCScannerExtensionConfiguration(
			CompilerType.MSVC, 0 /* version is ignored for now */);

	/**
	 * @since 5.1
	 */
	public static GCCScannerExtensionConfiguration getInstance() {
		return CONFIG;
	}

	/**
	 * @since 5.5
	 */
	public static GCCScannerExtensionConfiguration getInstance(IScannerInfo info) {
		if (info != null) {
			try {
				final Map<String, String> definedSymbols = info.getDefinedSymbols();

				String mscVer = definedSymbols.get("_MSC_VER"); //$NON-NLS-1$
				if (mscVer != null && Integer.valueOf(mscVer) > 0) {
					// Note: this is also used for clang-cl.
					return CONFIG_MSVC;
				}

				int major = Integer.valueOf(definedSymbols.get("__GNUC__")); //$NON-NLS-1$
				int minor = Integer.valueOf(definedSymbols.get("__GNUC_MINOR__")); //$NON-NLS-1$
				int version = version(major, minor);
				if (version >= VERSION_4_7) {
					return CONFIG_4_7;
				}
				if (version >= VERSION_4_2) {
					return CONFIG_4_2;
				}
			} catch (Exception e) {
				// Fall-back to the default configuration.
			}
		}
		return CONFIG;
	}

	public GCCScannerExtensionConfiguration() {
		this(0);
		// <CUSTOMIZATION FOR OPENCL>
		addKeyword("kernel".toCharArray(), IGCCToken.t__declspec);
		addKeyword("__kernel".toCharArray(), IGCCToken.t__declspec);
		addKeyword("__entry".toCharArray(), IGCCToken.t__declspec);

		// qualifiers
		addKeyword("local".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__local".toCharArray(), IGCCToken.t_volatile);
		addKeyword("global".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__global".toCharArray(), IGCCToken.t_volatile);
		addKeyword("constant".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__constant".toCharArray(), IGCCToken.t_volatile);

		addKeyword("__lsram0".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__lsram1".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__gsram0".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__gsram1".toCharArray(), IGCCToken.t_volatile);

		addKeyword("lsram0".toCharArray(), IGCCToken.t_volatile);
		addKeyword("lsram1".toCharArray(), IGCCToken.t_volatile);
		addKeyword("gsram0".toCharArray(), IGCCToken.t_volatile);
		addKeyword("gsram1".toCharArray(), IGCCToken.t_volatile);

		addKeyword("lsram".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__lsram".toCharArray(), IGCCToken.t_volatile);
		addKeyword("private".toCharArray(), IGCCToken.t_volatile);
		addKeyword("__private".toCharArray(), IGCCToken.t_volatile);

		// _Bool vs bool
		addKeyword("bool".toCharArray(), IToken.t__Bool);
		// type
//		addKeyword("uchar".toCharArray(), IToken.t_char);
		addKeyword("half".toCharArray(), IToken.t_half);
		addKeyword("event_t".toCharArray(), IToken.t_event_t);

		// </CUSTOMIZATION FOR OPENCL>
	}

	/**
	 * @since 5.5
	 */
	public GCCScannerExtensionConfiguration(int version) {
		this(CompilerType.GCC, version);
	}

	/**
	 * @since 6.10
	 */
	@SuppressWarnings("nls")
	public GCCScannerExtensionConfiguration(CompilerType compiler, int version) {
		addMacro("__null", "(void *)0");
		addMacro("__builtin_offsetof(T,m)", "((size_t) &((T *)0)->m)");

		if (compiler != CompilerType.MSVC) {
			// MSVC only defines this when compiling in C mode and /Za is used.
			addMacro("__STDC__", "1");
		}

		if (version >= VERSION_4_2) {
			addKeyword(GCCKeywords.cp_decimal32, IGCCToken.t_decimal32);
			addKeyword(GCCKeywords.cp_decimal64, IGCCToken.t_decimal64);
			addKeyword(GCCKeywords.cp_decimal128, IGCCToken.t_decimal128);
		}
		if (version >= VERSION_4_7) {
			addKeyword(GCCKeywords.cp__float128, IGCCToken.t__float128);
			addKeyword(GCCKeywords.cp__int128, IGCCToken.t__int128);
		}
	}

	@Override
	public boolean supportMinAndMaxOperators() {
		return false;
	}
}
