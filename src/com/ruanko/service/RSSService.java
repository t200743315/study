package com.ruanko.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ruanko.dao.NewsDao;
import com.ruanko.dao.impl.FileDaoImpl;
import com.ruanko.medol.Channel;
import com.ruanko.medol.News;

public class RSSService {
	private List<Channel> channelList; // 频道信息列表
	private List<News> newsList; // 新闻频道内容列表

	public RSSService() {
		NewsDao rssDao = new FileDaoImpl();
	}

	public List<Channel> getChannelList() {
		if (channelList == null) {
			channelList = new ArrayList<Channel>();

			// 创建“新华网 - 法制要闻”频道
			Channel channel1 = new Channel();
			channel1.setName("新华网 - 法制要闻");
			channel1.setFilePath("NewsFile/news_legal.xml");
			channel1.setUrl("http://www.xinhuanet/legal/news_legal.xml");

			// 创建“新华网 - 海外新闻”频道
			Channel channel2 = new Channel();
			channel2.setName("新华网 - 华人新闻");
			channel2.setFilePath("NewsFile/news_overseas.xml");
			channel2.setUrl("http://www/xinhuanet/overseas/news_overseas.xml");

			// 创建“新华网 - 政治新闻”频道
			Channel channel3 = new Channel();
			channel3.setName("新华网 - 政治新闻");
			channel3.setFilePath("NewsFile/news_politics.xml");
			channel3.setUrl("http://www/xinhuanet/politics/news_politics.xml");

			// 创建“新华网_省内新闻”
			Channel channel4 = new Channel();
			channel4.setName("新华网_地方新闻");
			channel4.setFilePath("NewsFile/news_province.xml");
			channel4.setUrl("http://www/xinhuanet/province/news_province.xml");

			// 将信息添加到频道中
			channelList.add(channel1);
			channelList.add(channel2);
			channelList.add(channel3);
			channelList.add(channel4);

		}
		return channelList;

	}

	public List<News> getNewsList(String filePath) {
		Document document = load(filePath);
		newsList = parse(document);
		return newsList;
	}

	public Document load(String filePath) {
		Document document = null;

		SAXBuilder saxBuilder = new SAXBuilder(false); // 使用jdom指定

		// 判断文件是否纯在
		File fXml = new File(filePath);
		if (fXml.exists() && fXml.isFile()) {
			// 加载一个xml文件，得到document对象
			try {
				document = saxBuilder.build(fXml);
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return document;
	}

	private News itemToNews(Element item) {
		News news = new News();// 创建News对象

		// 获得新闻节点内容
		String title = item.getChildText("title").trim();
		String link = item.getChildText("link");
		String author = item.getChildText("author");
		String guid = item.getChildText("guid");
		String pubDate = item.getChildText("pubDate");
		String category = item.getChildText("categoty");
		String description = item.getChildText("description").trim();

		// 设置节点内容
		news.setTitle(title);
		news.setLink(link);
		news.setAuthor(author);
		news.setGuid(guid);
		news.setPubDate(pubDate);
		news.setCategory(category);
		news.setDescription(description);

		return news;

	}

	public String newsToString(News news) {
		String content = null;
		content = "标题" + news.getTitle() + "\r\n" + "链接" + news.getLink() + "\r\n" + "作者" + news.getAuthor() + "\r\n"
				+ "发布时间" + news.getPubDate() + "\r\n" + "---------------------------------------------------"
				+ news.getDescription() + "\r\n" + "\r\n";

		return content;
	}

	private List<News> parse(Document document) {
		List<News> newsList = new ArrayList<News>();// 定义newslist返回列表
		News news = null;

		Element root = document.getRootElement(); // 得到Xml文档的根节

		// 获得item标签
		Element eChannel = root.getChild("channel");
		List<Element> itemList = eChannel.getChildren("item");

		// 生成News对象列表
		for (int i = 0; i < itemList.size(); i++) {
			Element item = itemList.get(i);
			news = itemToNews(item);
			newsList.add(news);
		}

		return newsList;

	}

	public boolean save(List<News> newslist) {
		boolean flag = false;
		NewsDao dao = new FileDaoImpl();
		if (dao.save(newslist)) {
			flag = true;
		}
		return false;
	}
}
