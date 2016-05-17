package com.deanveloper.playtime.util;

/**
 * @author Dean B
 */
public class CaseInsensitiveString implements CharSequence {
	private String original;
	private String lowercase;

	public CaseInsensitiveString(String s) {
		original = s;
		lowercase = s.toLowerCase();
	}

	@Override
	public int length() {
		return original.length();
	}

	@Override
	public char charAt(int index) {
		return original.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new CaseInsensitiveString(original.subSequence(start, end).toString());
	}

	public String toLowerCase() {
		return lowercase;
	}

	@Override
	public String toString() {
		return original;
	}

	@Override
	public int hashCode() {
		return lowercase.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof String) {
			return ((String) o).toLowerCase().equals(lowercase);
		} else if (o instanceof CaseInsensitiveString) {
			return o.equals(lowercase);
		} else {
			return false;
		}
	}
}
