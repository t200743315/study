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
	private List<Channel> channelList; // Ƶ����Ϣ�б�
	private List<News> newsList; // ����Ƶ�������б�

	public RSSService() {
		NewsDao rssDao = new FileDaoImpl();
	}

	public List<Channel> getChannelList() {
		if (channelList == null) {
			channelList = new ArrayList<Channel>();

			// �������»��� - ����Ҫ�š�Ƶ��
			Channel channel1 = new Channel();
			channel1.setName("�»��� - ����Ҫ��");
			channel1.setFilePath("NewsFile/news_legal.xml");
			channel1.setUrl("http://www.xinhuanet/legal/news_legal.xml");

			// �������»��� - �������š�Ƶ��
			Channel channel2 = new Channel();
			channel2.setName("�»��� - ��������");
			channel2.setFilePath("NewsFile/news_overseas.xml");
			channel2.setUrl("http://www/xinhuanet/overseas/news_overseas.xml");

			// �������»��� - �������š�Ƶ��
			Channel channel3 = new Channel();
			channel3.setName("�»��� - ��������");
			channel3.setFilePath("NewsFile/news_politics.xml");
			channel3.setUrl("http://www/xinhuanet/politics/news_politics.xml");

			// �������»���_ʡ�����š�
			Channel channel4 = new Channel();
			channel4.setName("�»���_�ط�����");
			channel4.setFilePath("NewsFile/news_province.xml");
			channel4.setUrl("http://www/xinhuanet/province/news_province.xml");

			// ����Ϣ��ӵ�Ƶ����
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

		SAXBuilder saxBuilder = new SAXBuilder(false); // ʹ��jdomָ��

		// �ж��ļ��Ƿ���
		File fXml = new File(filePath);
		if (fXml.exists() && fXml.isFile()) {
			// ����һ��xml�ļ����õ�document����
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
		News news = new News();// ����News����

		// ������Žڵ�����
		String title = item.getChildText("title").trim();
		String link = item.getChildText("link");
		String author = item.getChildText("author");
		String guid = item.getChildText("guid");
		String pubDate = item.getChildText("pubDate");
		String category = item.getChildText("categoty");
		String description = item.getChildText("description").trim();

		// ���ýڵ�����
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
		content = "����" + news.getTitle() + "\r\n" + "����" + news.getLink() + "\r\n" + "����" + news.getAuthor() + "\r\n"
				+ "����ʱ��" + news.getPubDate() + "\r\n" + "---------------------------------------------------"
				+ news.getDescription() + "\r\n" + "\r\n";

		return content;
	}

	private List<News> parse(Document document) {
		List<News> newsList = new ArrayList<News>();// ����newslist�����б�
		News news = null;

		Element root = document.getRootElement(); // �õ�Xml�ĵ��ĸ���

		// ���item��ǩ
		Element eChannel = root.getChild("channel");
		List<Element> itemList = eChannel.getChildren("item");

		// ����News�����б�
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
