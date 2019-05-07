package com.ruanko.service;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;

import com.ruanko.medol.Channel;

public class UpdateThread extends Thread {

	private final static int TIMEOUT = 5 * 1000; // 5 seconds
	private final static int DELAY_TIME = 300 * 1000; // 5 minutes
	private final static int BUFFER_SIZE = 65536; // 64KB

	private List<Channel> channelList; // 频道信息列表

	public UpdateThread() {
		RSSService rssService = new RSSService();
		List<Channel> channelList = rssService.getChannelList();
	}

	// 线程运行方法
	public void run() {
		// 死循环，每5分钟更新一次RSS文件

		while (true) {
			System.out.println("正在更新........" + new Date());

			for (int i = 0; i < channelList.size(); i++) {
				Channel channel = channelList.get(i);
				System.out.println("更新" + channel.getName());
				update(channel.getUrl(), channel.getFilePath());
			}

			System.out.println("更新完毕........" + new Date());

			// 让线程休眠5分钟
			try {
				sleep(DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}


	/**
	 * 如果有新的RSS文件，就更新RSS文件
	 * 
	 * @param urlPath
	 * @param filePath
	 */
	private void update(String urlPath, String filePath){
		// 创建HTTP连接
		HttpURLConnection httpURLConnection;
		try {
			URL url = new URL(urlPath);

			httpURLConnection = (HttpURLConnection) url.openConnection();

			

			// 判断HTTP连接是否成功
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				return;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// 1在http服务器上检查是否有新的RSS文件
		File file = new File(filePath);
		if (hasNewRss(httpURLConnection, file)) {
			System.out.println("现在更新");

			

			try {
				
				httpURLConnection.setConnectTimeout(TIMEOUT);
				
				httpURLConnection.connect();
				
				// 2下载RSS文件，并且保存到缓冲区中
				ByteBuffer buffer = download(httpURLConnection);
				// 3将RSS文件内容保存到文件中
				if (buffer != null) {
					saveAs(buffer, file);
				httpURLConnection.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("RSS文件已经更新完毕");
		}

	}

	/**
	 * 下载到缓冲区
	 * @throws IOException 
	 */
	private ByteBuffer download(HttpURLConnection httpURLConnection) throws IOException   {

		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		InputStream in = httpURLConnection.getInputStream();
		byte[] b = new byte[BUFFER_SIZE];
		for (int i = 0; i < b.length; i++) {
			b[i] = 0;
		}

		int length = 0;
		while(length >= 0){
			length = in.read(b);
			for(int i = 0;i < length;i++){
				buffer.put(b[i]);
			}
			buffer.flip();
		}

		return buffer;


	}

	/**
	 * 将缓冲中的文件保存到文件中
	 */
	public synchronized void saveAs(ByteBuffer buffer, File file) throws IOException {
		byte[] b = new byte[BUFFER_SIZE];
		for (int i = 0; i < b.length; i++) {
			b[i] = 0;
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		FileChannel channel = fileOutputStream.getChannel();
		channel.write(buffer);
		channel.close();
		fileOutputStream.close();
		return;

	}

	/**
	 * 判断RSS文件是否有跟新
	 * @param httpURLConnection
	 * @param file
	 */
	public boolean hasNewRss(HttpURLConnection httpURLConnection, File file) {
		// 获得服务器最后修改时间
		long current = System.currentTimeMillis();
		long httpLastModified = httpURLConnection.getHeaderFieldDate("Last-Modified", current);

		// 本地文件最后修改时间
		long fileLastModified = file.lastModified();
		// 判断是否更新
		if (httpLastModified > fileLastModified) {
			return true;
		} else {
			return false;
		}
	}
}
