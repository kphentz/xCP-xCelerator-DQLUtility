package com.emc.xcelerators.utils.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Tord Svensson, EMC Software
 * @version 1.0 (Sep 17, 2008)
 */
public class STTCompiler {

	final public static String	DELIM	= "$";

	public static TextTemplate compile(final String template, final Map<String, String> attrs) {
		final String intermediary = compile(template).apply(attrs);
		return compile(intermediary);
	}

	public static TextTemplate compile(final String templateString, final String... requiredVariables) {
		final TextTemplate template = compile(templateString);
		final Collection<String> vars = template.getVariables();
		for (final String requiredVariable : requiredVariables) {
			if (!vars.contains(requiredVariable)) {
				final String message = "The required variable: " + requiredVariable + " is missing from the template \"" + templateString
						+ "\".";
				throw new IllegalArgumentException(message);

			}
		}

		return template;
	}

	public static TextTemplate compile(final String template) {
		final StringTokenizer st = new StringTokenizer(template, DELIM, true);

		final List<Fragment> fragments = new ArrayList<Fragment>();

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.equals(DELIM)) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					if (token.equals(DELIM)) {
						fragments.add(new Fragment(token, false));
					} else {
						fragments.add(new Fragment(token, true));
						if (st.hasMoreTokens()) {
							token = st.nextToken();
							if (!token.equals(DELIM)) {
								throw new IllegalArgumentException("Invalid text template: " + template);
							}
						} else {
							throw new IllegalArgumentException("Unterminated text template " + template);
						}
					}
				} else {
					throw new IllegalArgumentException("Unterminated text template " + template);
				}
			} else {
				fragments.add(new Fragment(token, false));
			}
		}

		return new SimpleTextTemplate(fragments);
	}

	static class Fragment {
		String	value;
		boolean	isVariable;

		Fragment(final String value, final boolean isVariable) {
			this.value = value;
			this.isVariable = isVariable;
		}
	}

	static class SimpleTextTemplate implements TextTemplate {

		private final List<Fragment>	fragments;

		public SimpleTextTemplate(final List<Fragment> fragments) {
			this.fragments = fragments;
		}

		@Override
		public String apply(final Map<String, String> attrs) {
			final StringBuilder buf = new StringBuilder();

			for (final Fragment f : fragments) {
				if (f.isVariable) {
					if (attrs.containsKey(f.value)) {
						buf.append(attrs.get(f.value));
					} else {
						throw new IllegalArgumentException("Missing text template attribute: " + f.value);
					}
				} else {
					buf.append(f.value);
				}
			}

			return buf.toString();
		}

		@Override
		public SimpleTextTemplate partial(final Map<String, String> attrs) {
			final List<Fragment> newFragments = new ArrayList<Fragment>();
			Fragment lastFragment = null;
			for (final Fragment f : fragments) {
				String value = f.value;
				if (f.isVariable) {
					if (attrs.containsKey(value)) {
						value = attrs.get(value);
					} else {
						lastFragment = null;
						newFragments.add(f);
						continue;
					}
				}

				if (lastFragment != null) {
					lastFragment.value += value;
				} else {
					lastFragment = new Fragment(value, false);
					newFragments.add(lastFragment);
				}
			}
			return new SimpleTextTemplate(newFragments);
		}

		@Override
		public Collection<String> getVariables() {
			final Collection<String> vars = new HashSet<String>();
			for (final Fragment f : fragments) {
				if (f.isVariable) {
					vars.add(f.value);
				}
			}
			return vars;
		}
	}
}
