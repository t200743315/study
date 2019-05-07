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

	private List<Channel> channelList; // Ƶ����Ϣ�б�

	public UpdateThread() {
		RSSService rssService = new RSSService();
		List<Channel> channelList = rssService.getChannelList();
	}

	// �߳����з���
	public void run() {
		// ��ѭ����ÿ5���Ӹ���һ��RSS�ļ�

		while (true) {
			System.out.println("���ڸ���........" + new Date());

			for (int i = 0; i < channelList.size(); i++) {
				Channel channel = channelList.get(i);
				System.out.println("����" + channel.getName());
				update(channel.getUrl(), channel.getFilePath());
			}

			System.out.println("�������........" + new Date());

			// ���߳�����5����
			try {
				sleep(DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}


	/**
	 * ������µ�RSS�ļ����͸���RSS�ļ�
	 * 
	 * @param urlPath
	 * @param filePath
	 */
	private void update(String urlPath, String filePath){
		// ����HTTP����
		HttpURLConnection httpURLConnection;
		try {
			URL url = new URL(urlPath);

			httpURLConnection = (HttpURLConnection) url.openConnection();

			

			// �ж�HTTP�����Ƿ�ɹ�
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

		// 1��http�������ϼ���Ƿ����µ�RSS�ļ�
		File file = new File(filePath);
		if (hasNewRss(httpURLConnection, file)) {
			System.out.println("���ڸ���");

			

			try {
				
				httpURLConnection.setConnectTimeout(TIMEOUT);
				
				httpURLConnection.connect();
				
				// 2����RSS�ļ������ұ��浽��������
				ByteBuffer buffer = download(httpURLConnection);
				// 3��RSS�ļ����ݱ��浽�ļ���
				if (buffer != null) {
					saveAs(buffer, file);
				httpURLConnection.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("RSS�ļ��Ѿ��������");
		}

	}

	/**
	 * ���ص�������
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
	 * �������е��ļ����浽�ļ���
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
	 * �ж�RSS�ļ��Ƿ��и���
	 * @param httpURLConnection
	 * @param file
	 */
	public boolean hasNewRss(HttpURLConnection httpURLConnection, File file) {
		// ��÷���������޸�ʱ��
		long current = System.currentTimeMillis();
		long httpLastModified = httpURLConnection.getHeaderFieldDate("Last-Modified", current);

		// �����ļ�����޸�ʱ��
		long fileLastModified = file.lastModified();
		// �ж��Ƿ����
		if (httpLastModified > fileLastModified) {
			return true;
		} else {
			return false;
		}
	}
}
