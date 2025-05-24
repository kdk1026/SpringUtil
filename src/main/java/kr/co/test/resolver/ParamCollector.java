package kr.co.test.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * HandlerMethodArgumentResolver 에서 Map 객체를 사용하기 위한 클래스
 */
@Getter
@Setter
public class ParamCollector {

	private HttpServletRequest request;
	private ParamMap map = new ParamMap();
	private ParamMap heardMap = new ParamMap();

	public Object get(String key) {
		return map.get(key);
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

    public void remove(String key){
        map.remove(key);
    }

    public void putAll(Map<String, Object> m) {
    	map.putAll(m);
    }

	public void putDefaultBlank(String ... keys) {
		for (int i=0; i < keys.length; i++) {
			map.put(keys[i], "");
		}
	}

    public void clear() {
    	map.clear();
    }

    public int size() {
		return map.size();
	}

    public boolean isEmpty() {
		return map.isEmpty();
	}

    public boolean containsKey(String key) {
		return map.containsKey(key);
	}

    public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Collection<Object> values() {
		return map.values();
	}

	public String getString(String key) {
		return map.getString(key);
	}

	public int getInt(String key) {
		return map.getInteger(key);
	}

	public double getDouble(String key) {
		return map.getDouble(key);
	}

	public boolean getBoolean(String key) {
		return map.getBoolean(key);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getArray(String key) {
		List<Object> list = null;
		try {
			list = (List<Object>) map.get(key);
		} catch (ClassCastException e) {
			list = new ArrayList<>();
			list.add(map.get(key));
		}
		return list;
	}

	public MultipartFile getMultipartFile(String key) {
		return (MultipartFile) map.get(key);
	}

	public ParamMap getMapAll() {
		ParamMap paramMap = this.getHeardMap();
		paramMap.putAll(this.map);
		return paramMap;
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
