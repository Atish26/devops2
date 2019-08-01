/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.bind;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * Generates relaxed name variations from a given source.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @see RelaxedDataBinder
 * @see RelaxedPropertyResolver
 */
public final class RelaxedNames implements Iterable<String> {

	private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([^A-Z-])([A-Z])");

	private static final Pattern SEPARATED_TO_CAMEL_CASE_PATTERN = Pattern
			.compile("[_\\-.]");

	private final String name;

	private final Set<String> values = new LinkedHashSet<String>();

	/**
	 * Create a new {@link RelaxedNames} instance.
	 * @param name the source name. For the maximum number of variations specify the name
	 * using dashed notation (e.g. {@literal my-property-name}
	 */
	public RelaxedNames(String name) {
		this.name = (name == null ? "" : name);
		initialize(RelaxedNames.this.name, this.values);
	}

	@Override
	public Iterator<String> iterator() {
		return this.values.iterator();
	}

	private void initialize(String name, Set<String> values) {
		if (values.contains(name)) {
			return;
		}
		for (Variation variation : Variation.values()) {
			for (Manipulation manipulation : Manipulation.values()) {
				String result = name;
				result = manipulation.apply(result);
				result = variation.apply(result);
				values.add(result);
				initialize(result, values);
			}
		}
	}

	/**
	 * Name variations.
	 */
	enum Variation {

		NONE {
			@Override
			public String apply(String value) {
				return value;
			}
		},

		LOWERCASE {
			@Override
			public String apply(String value) {
				return value.toLowerCase();
			}
		},

		UPPERCASE {
			@Override
			public String apply(String value) {
				return value.toUpperCase();
			}
		};

		public abstract String apply(String value);

	}

	/**
	 * Name manipulations.
	 */
	enum Manipulation {

		NONE {
			@Override
			public String apply(String value) {
				return value;
			}
		},

		HYPHEN_TO_UNDERSCORE {
			@Override
			public String apply(String value) {
				return value.replace("-", "_");
			}
		},

		UNDERSCORE_TO_PERIOD {
			@Override
			public String apply(String value) {
				return value.replace("_", ".");
			}
		},

		PERIOD_TO_UNDERSCORE {
			@Override
			public String apply(String value) {
				return value.replace(".", "_");
			}
		},

		CAMELCASE_TO_UNDERSCORE {
			@Override
			public String apply(String value) {
				Matcher matcher = CAMEL_CASE_PATTERN.matcher(value);
				StringBuffer result = new StringBuffer();
				while (matcher.find()) {
					matcher.appendReplacement(result, matcher.group(1) + '_'
							+ StringUtils.uncapitalize(matcher.group(2)));
				}
				matcher.appendTail(result);
				return result.toString();
			}
		},

		CAMELCASE_TO_HYPHEN {
			@Override
			public String apply(String value) {
				Matcher matcher = CAMEL_CASE_PATTERN.matcher(value);
				StringBuffer result = new StringBuffer();
				while (matcher.find()) {
					matcher.appendReplacement(result, matcher.group(1) + '-'
							+ StringUtils.uncapitalize(matcher.group(2)));
				}
				matcher.appendTail(result);
				return result.toString();
			}
		},

		SEPARATED_TO_CAMELCASE {
			@Override
			public String apply(String value) {
				return separatedToCamelCase(value, false);
			}
		},

		CASE_INSENSITIVE_SEPARATED_TO_CAMELCASE {
			@Override
			public String apply(String value) {
				return separatedToCamelCase(value, true);
			}
		};

		public abstract String apply(String value);

		private static String separatedToCamelCase(String value,
				boolean caseInsensitive) {
			StringBuilder builder = new StringBuilder();
			for (String field : SEPARATED_TO_CAMEL_CASE_PATTERN.split(value)) {
				field = (caseInsensitive ? field.toLowerCase() : field);
				builder.append(
						builder.length() == 0 ? field : StringUtils.capitalize(field));
			}
			for (String suffix : new String[] { "_", "-", "." }) {
				if (value.endsWith(suffix)) {
					builder.append(suffix);
				}
			}
			return builder.toString();

		}
	}

	/**
	 * Return a {@link RelaxedNames} for the given source camelCase source name.
	 * @param name the source name in camelCase
	 * @return the relaxed names
	 */
	public static RelaxedNames forCamelCase(String name) {
		return new RelaxedNames(Manipulation.CAMELCASE_TO_HYPHEN.apply(name));
	}

}
