package com.ruanko.medol;

public class Channel {
	private String name; // Ƶ������
	private String filePath; // ����Ƶ���ļ�·��
	private String url; // ����Ƶ���ļ�·��

	public Channel() {
		super();
		this.name = "";
		this.filePath = "";
		this.url = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString() {
		return this.name;
	}
}
