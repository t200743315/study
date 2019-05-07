package com.ruanko.dao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.ruanko.dao.NewsDao;
import com.ruanko.medol.News;
import com.ruanko.service.RSSService;

public class FileDaoImpl implements NewsDao {

	private static final String filePath = "NewsFile/rss.txt";

	@Override
	public boolean save(List<News> newsList) {
		boolean flag = true;
		File file = new File(filePath);
		FileWriter fWriter = null;
		BufferedWriter bWriter = null;
		try {
			fWriter = new FileWriter(file, true);
			bWriter = new BufferedWriter(fWriter);

			// 遍历新闻内容列表，将新闻内容保存到文件中
			RSSService rssService = new RSSService();
			for (News news : newsList) {
				String content = rssService.newsToString(news);
				bWriter.write(content);
			}
			bWriter.flush();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {// 关闭输出流输入流
				bWriter.close();
				fWriter.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return flag;
	}

}
