package com.topnews.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.topnews.dao.CatDao;
import com.topnews.db.SQLHelper;

import android.database.SQLException;
import android.util.Log;

public class CatManage {
	public static CatManage catManage;
	/**
	 * 默认的用户选择频道列表
	 * */
	public static List<CatItem> defaultUserChannels;
	/**
	 * 默认的其他频道列表
	 * */
	public static List<CatItem> defaultOtherChannels;
	private CatDao catDao;
	/** 判断数据库中是否存在用户数据 */
	private boolean userExist = false;
	static {
		defaultUserChannels = new ArrayList<CatItem>();
		defaultOtherChannels = new ArrayList<CatItem>();
		defaultUserChannels.add(new CatItem(1, "推荐", 1, 1));
		defaultUserChannels.add(new CatItem(2, "热点", 2, 1));
		defaultUserChannels.add(new CatItem(3, "杭州", 3, 1));
		defaultUserChannels.add(new CatItem(4, "时尚", 4, 1));
		defaultUserChannels.add(new CatItem(5, "科技", 5, 1));
		defaultUserChannels.add(new CatItem(6, "体育", 6, 1));
		defaultUserChannels.add(new CatItem(7, "军事", 7, 1));
		defaultOtherChannels.add(new CatItem(8, "财经", 1, 0));
		defaultOtherChannels.add(new CatItem(9, "汽车", 2, 0));
		defaultOtherChannels.add(new CatItem(10, "房产", 3, 0));
		defaultOtherChannels.add(new CatItem(11, "社会", 4, 0));
		defaultOtherChannels.add(new CatItem(12, "情感", 5, 0));
		defaultOtherChannels.add(new CatItem(13, "女人", 6, 0));
		defaultOtherChannels.add(new CatItem(14, "旅游", 7, 0));
		defaultOtherChannels.add(new CatItem(15, "健康", 8, 0));
		defaultOtherChannels.add(new CatItem(16, "美女", 9, 0));
		defaultOtherChannels.add(new CatItem(17, "游戏", 10, 0));
		defaultOtherChannels.add(new CatItem(18, "数码", 11, 0));
		defaultUserChannels.add(new CatItem(19, "娱乐", 12, 0));
	}

	private CatManage(SQLHelper paramDBHelper) throws SQLException {
		if (catDao == null)
			catDao = new CatDao(paramDBHelper.getContext());
		// NavigateItemDao(paramDBHelper.getDao(NavigateItem.class));
		return;
	}

	/**
	 * 初始化频道管理类
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static CatManage getManage(SQLHelper dbHelper)throws SQLException {
		if (catManage == null)
			catManage = new CatManage(dbHelper);
		return catManage;
	}

	/**
	 * 清除所有的频道
	 */
	public void deleteAllChannel() {
		catDao.clearFeedTable();
	}
	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
	 */
	public List<CatItem> getUserChannel() {
		Object cacheList = catDao.listCache(SQLHelper.SELECTED + "= ?",new String[] { "1" });
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			userExist = true;
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			List<CatItem> list = new ArrayList<CatItem>();
			for (int i = 0; i < count; i++) {
				CatItem navigate = new CatItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		initDefaultChannel();
		return defaultUserChannels;
	}
	
	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
	 */
	public List<CatItem> getOtherChannel() {
		Object cacheList = catDao.listCache(SQLHelper.SELECTED + "= ?" ,new String[] { "0" });
		List<CatItem> list = new ArrayList<CatItem>();
		if (cacheList != null && !((List) cacheList).isEmpty()){
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				CatItem navigate= new CatItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		if(userExist){
			return list;
		}
		cacheList = defaultOtherChannels;
		return (List<CatItem>) cacheList;
	}
	
	/**
	 * 保存用户频道到数据库
	 * @param userList
	 */
	public void saveUserChannel(List<CatItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			CatItem catItem = (CatItem) userList.get(i);
			catItem.setOrderId(i);
			catItem.setSelected(Integer.valueOf(1));
			catDao.addCache(catItem);
		}
	}
	
	/**
	 * 保存其他频道到数据库
	 * @param otherList
	 */
	public void saveOtherChannel(List<CatItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			CatItem catItem = (CatItem) otherList.get(i);
			catItem.setOrderId(i);
			catItem.setSelected(Integer.valueOf(0));
			catDao.addCache(catItem);
		}
	}
	
	/**
	 * 初始化数据库内的频道数据
	 */
	private void initDefaultChannel(){
		Log.d("deleteAll", "deleteAll");
		deleteAllChannel();
		saveUserChannel(defaultUserChannels);
		saveOtherChannel(defaultOtherChannels);
	}
}
