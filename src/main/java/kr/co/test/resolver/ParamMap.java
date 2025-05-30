package kr.co.test.resolver;

import java.util.HashMap;

public class ParamMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public Object putLowerCase(String key, Object value) {
		return super.put(key.toLowerCase(), value);
	}

	public String getString(Object key) {
		return super.containsKey(key) ? String.valueOf(super.get(key)) : "";
	}

	public int getInteger(Object key) {
		return Integer.parseInt(String.valueOf(super.get(key)));
	}

	public double getDouble(Object key) {
		return Double.parseDouble(String.valueOf(super.get(key)));
	}

	public boolean getBoolean(Object key) {
		return Boolean.parseBoolean(String.valueOf(super.get(key)));
	}

}
