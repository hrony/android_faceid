package com.vyagoo.faceid.util;

/**
 * SharePreferences 管理封装类
 * 缓存xml格式的数据到本地,局限于本应用
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Set;

public class SharedPrefManager {
	private SharedPreferences shareMgr;
	private static String shareName = "SETTING_PREF";
	private static SharedPrefManager instance;

	private SharedPrefManager(Context context) {
		this(context, shareName);
	}

	private SharedPrefManager(Context context, String shareName) {
		shareMgr = context.getSharedPreferences(shareName, Context.MODE_PRIVATE);
	}

	/**
	 * get the SharedPrefManager instance, it is the singleton
	 * 
	 * @param context
	 *            Context
	 * @return SharedPrefManager
	 */
	public static SharedPrefManager getInstance(Context context) {
		return getInstance(context, shareName);
	}

	/**
	 * get the SharedPrefManager instance, it is the singleton
	 * 
	 * @param context
	 *            Context
	 * @param shareName
	 *            String
	 * @return SharedPrefManager
	 */
	public static SharedPrefManager getInstance(Context context, String shareName) {
		if (instance == null) {
			synchronized (SharedPrefManager.class) {
				if (instance == null) {
					instance = new SharedPrefManager(context, shareName);
				}
			}
		}
		return instance;
	}

	public void put(String key, boolean value) {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.putBoolean(key, value);
			edit.commit();
		}
	}

	public void put(String key, String value) {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.putString(key, value);
			edit.commit();
		}
	}

	public void put(String key, int value) {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.putInt(key, value);
			edit.commit();
		}
	}

	public void put(String key, float value) {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.putFloat(key, value);
			edit.commit();
		}
	}

	public void put(String key, long value) {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.putLong(key, value);
			edit.commit();
		}
	}

	public void put(String key, Set<String> value) {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.putStringSet(key, value);
			edit.commit();
		}
	}

	public String get(String key) {
		return shareMgr.getString(key, "");
	}

	public String get(String key, String defValue) {
		return shareMgr.getString(key, defValue);
	}

	public boolean get(String key, boolean defValue) {
		return shareMgr.getBoolean(key, defValue);
	}

	public int get(String key, int defValue) {
		return shareMgr.getInt(key, defValue);
	}

	public float get(String key, float defValue) {
		return shareMgr.getFloat(key, defValue);
	}

	public long get(String key, long defValue) {
		return shareMgr.getLong(key, defValue);
	}

	public Set<String> get(String key, Set<String> defValue) {
		return shareMgr.getStringSet(key, defValue);
	}

	/**
	 * 根据key删除
	 * 
	 * @param key
	 */
	public void reMove(String key) {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.remove(key);
			edit.commit();
		}
	}

	/**
	 * 删除所有
	 * 
	 * @param key
	 */
	public void clearAll() {
		Editor edit = shareMgr.edit();
		if (edit != null) {
			edit.clear();
			edit.commit();
		}
	}
}
