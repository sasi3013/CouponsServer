package com.sasi.coupons.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class CacheController {

	private Map<String, Object> dataMap;
	private Map<Long, ArrayList<String>> tokensMap;

	public CacheController() {
		this.dataMap = new ConcurrentHashMap<String, Object>();
		this.tokensMap = new ConcurrentHashMap<Long, ArrayList<String>>();
	}

	public void putData(String key, Object value) {
		this.dataMap.put(key, value);
	}

	public Object getData(String key) {
		return this.dataMap.get(key);
	}

	public void deleteData(long key, String token) {
		ArrayList<String> value = this.tokensMap.get(key);
		if (value.size() == 1) {
			this.tokensMap.remove(key);
		} else {
			value.remove(token);
			this.tokensMap.put(key, value);
		}
		this.dataMap.remove(token);
	}

	public void putTokenToIdLink(long key, String token) {
		ArrayList<String> value = this.tokensMap.get(key);
		if (value == null) {
			value = new ArrayList<String>();
		}
		value.add(token);
		this.tokensMap.put(key, value);
	}

	public void deleteAllUserData(long key) {
		ArrayList<String> tokens = this.tokensMap.get(key);
		this.tokensMap.remove(key);
		if (tokens != null) {
			for (String token : tokens) {
				this.dataMap.remove(token);
			}
		}
	}

}
