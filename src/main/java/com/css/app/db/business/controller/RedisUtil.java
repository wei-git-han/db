package com.css.app.db.business.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

/**
 * redis使用工具类
 * 
 * @author weizy
 *
 */

@Component
public class RedisUtil {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
//	public Jedis jedis =new Jedis("172.16.4.3",6379,"foobared");
	private HashOperations<String, String, String> opsForHash;

	
	
	/**
	 * String 类型插值,修改方法
	 * @param key
	 * @param value
	 * @return 插入成功返回  "OK"
	 */
	public void setString(String key,String value){
//		return jedis.set(key, value);
		redisTemplate.opsForValue().set(key, value);
//		redisTemplate.setStringSerializer();
	}
	
	/**
	 * String 类型取值方法
	 * @param key
	 * @return
	 */
	public String getString (String key){
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * redis哈希类型取值方法
	 * 
	 * @param hkey
	 * @param key
	 * @param value
	 */
	public void hset(String hkey, String key, String[] value) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		if (hkey != null && !"".equals(hkey.trim()) && key != null && !"".equals(key.trim()) && value != null
				&& value.length > 0) {
			for (int i = 0; i < value.length; i++) {
				opsForHash.put(hkey, key, value[i]);
			}
		}
	}

	/**
	 * 消息类型默认插入 默认接受、默认有声音
	 * 
	 * @param hkey
	 * @param map
	 */
	public void hsetMapDedult(String hkey, Map<String, String> map) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		if (hkey != null && !"".equals(hkey.trim()) && map != null && map.size() > 0) {
			map.put("isReceive", "1");// 0代表否 1代表是
			map.put("isSound", "1");
			opsForHash.putAll(hkey, map);

		}
	}

	/**
	 * rendis集合类型插值方式
	 * 
	 * @param hkey
	 * @param map
	 */
	public void hsetMap(String hkey, Map<String, String> map) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		if (hkey != null && !"".equals(hkey.trim()) && map != null && map.size() > 0) {
			opsForHash.putAll(hkey, map);

		}
	}

	/**
	 * redis根据hkey删除哈希数值
	 * 
	 * @param hkey
	 */
	public void hdeleteMap(String hkey) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		Map<String, String> map = this.hgetMap(hkey);
		if (hkey != null && !"".equals(hkey.trim()) && map != null && map.size() > 0) {
			Cursor<Entry<String, String>> scan = opsForHash.scan(hkey, ScanOptions.NONE);
			while (scan.hasNext()) {
				Entry<String, String> next = scan.next();
				opsForHash.delete(hkey, next.getKey());
			}
		}
	}

	/**
	 * redis根据hkey合key删除方法
	 * 
	 * @param hkey
	 * @param key
	 */
	public void hdelete(String hkey, String key) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		opsForHash.delete(hkey, key);
	}

	/**
	 * redis哈希值更新操作
	 * 
	 * @param hkey
	 * @param newMap
	 */
	public void hUpdate(String hkey, Map<String, String> newMap) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		String isAdmin = newMap.get("isAdmin");
		newMap.remove("isAdmin");
		if (!"true".equals(isAdmin)) {
			hsetMapForUpdate(hkey, newMap);
		} else {
			hsetMapForUpdate(hkey + "(admin)", newMap);
		}
	}

	private void hsetMapForUpdate(String hkey, Map<String, String> map) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		if (hkey != null && !"".equals(hkey.trim()) && map != null && map.size() > 0) {
			opsForHash.putAll(hkey, map);

		}
	}

	/**
	 * redis根据hkey获取map值
	 * 
	 * @param hkey
	 * @return Map<String, String>
	 */
	public Map<String, String> hgetMap(String hkey) {
		if (opsForHash == null) {
			opsForHash = redisTemplate.opsForHash();
		}
		Map<String, String> map = opsForHash.entries(hkey);
		return map;
	}

	/**
	 * 根据kehy模糊查询
	 * 
	 * @param hkey
	 * @return
	 */
	public List<Map<String, String>> hgetMapFuzzy(String hkey) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Set<String> keys = stringRedisTemplate.keys(hkey);
		keys.stream().sorted(Comparator.naturalOrder());
//		Set<String> keys = jedis.keys(hkey);
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Map<String, String> hgetMap = hgetMap(key);
			list.add(hgetMap);
		}
		return list;
	}

	/**
	 * 从redis获取模糊key对应的value的数量
	 */
	public int fuzzyCount(String hkey) {
		Set<String> keys = stringRedisTemplate.keys(hkey);
		return keys != null ? keys.size() : 0;
	}

	/**
	 * 判断是否包含模糊key值
	 */
	public Boolean isHasFuzzyKey(String hkey) {
		Set<String> keys = stringRedisTemplate.keys(hkey);
		return keys.size() > 0 ? true : false;
	}

	/**
	 * 判断是否包含模糊key值
	 */
	public Set<String> isHasFuzzyKey2(String hkey) {
		Set<String> keys = stringRedisTemplate.keys(hkey);
		return keys;
	}

/*	public List<Map<String, String>> hgetMapOfUser(String appId, String userId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Set<String> keys = stringredisTemplate.keys("*-" + userId);
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			String hkey = iterator.next();
			Map<String, String> hgetMap = hgetMap(hkey);
			list.add(hgetMap);
		}
		return list;

	}

	private String sort(String str) {
		char[] s1 = str.toCharArray();
		for (int i = 0; i < s1.length; i++) {
			for (int j = 0; j < i; j++) {
				if (s1[i] < s1[j]) {
					char temp = s1[i];
					s1[i] = s1[j];
					s1[j] = temp;
				}
			}

		}
		return new String(s1);
	}*/

}
