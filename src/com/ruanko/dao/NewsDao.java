package com.ruanko.dao;

import java.util.List;

import com.ruanko.medol.News;

/**
 * 新闻数据接口操作
 */

public interface NewsDao {
	public boolean save(List<News> newsList);

}
