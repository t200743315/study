package com.ruanko.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.xml.soap.Detail;

import com.ruanko.medol.Channel;
import com.ruanko.medol.News;
import com.ruanko.service.RSSService;

public class JMainFrame extends JFrame {

	// 窗口属性
	private final static int WIDTH = 800; // 设置窗口宽度
	private final static int HEIGHT = 600;// 设置窗口高度
	private final static String TItile = "RSS阅读器";// 设置窗口标题

	// 窗口组件
	private JComboBox jcbChannel; // 新闻频道下拉框
	private JButton jbRead; // 读取按钮
	private JButton jbExport; // 导出按钮
	private JTextArea jtaContent; // 新闻内容文本区
	private DefaultTableModel dtmTableModel; // 表格数据模型
	private JTable jTable; // 表格

	private List<News> newsList; // 新闻内容表格

	private RSSService rssService; // 业务逻辑对象

	public JMainFrame() {
		rssService = new RSSService();

		this.setTitle(TItile);// 设置窗口标题
		this.setSize(WIDTH, HEIGHT);// 设置窗口大小
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置窗口默认关闭
		this.setCenter();
		this.setContentPane(getJPMain());
	}

	private void setCenter() {
		Toolkit kToolkit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kToolkit.getScreenSize(); // 获取屏幕的尺寸
		setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);

	}

	private JPanel getJPMain() {
		JPanel jpMain = new JPanel(); // 创建主版面
		jpMain.setLayout(new BorderLayout()); // 设置主版面布局

		jpMain.add(getJPNorth(), BorderLayout.NORTH); // 将北部面板添加到主板面的北部
		jpMain.add(getJSPTable(), BorderLayout.CENTER); // 将带滚动面板的新闻内容文本区域加到主板面
		jpMain.add(getJSPContent(), BorderLayout.SOUTH); // 将带滚动面板的新闻内容文本区添加到主面板

		return jpMain;
	}

	private JPanel getJPNorth() {
		JPanel jpNorth = new JPanel(); // 创建北部版面
		jpNorth.setLayout(new FlowLayout(FlowLayout.LEFT)); // 设置北部版面布局

		JLabel jlChannel = new JLabel("站点"); // 创建站点标签
		jpNorth.add(jlChannel); // 将站点标签创建到主把面中
		jpNorth.add(getJCBChannel()); // 将频道下拉框添加到主板面中
		jpNorth.add(getJBRead()); // 将读取按钮添加到主板面中
		jpNorth.add(getJBExport()); // 将导出按钮添加到主板面中

		return jpNorth;
	}

	private JComboBox getJCBChannel() {
		if (jcbChannel == null) {
			jcbChannel = new JComboBox(); // 创建频道下拉框

			// 获得新闻频道信息
			List<Channel> channelList = rssService.getChannelList();
			for (int i = 0; i < channelList.size(); i++) {
				jcbChannel.addItem(channelList.get(i));
			}
		}
		return jcbChannel;
	}

	private JButton getJBRead() {
		if (jbRead == null) {
			jbRead = new JButton("读取");
			jbRead.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// 1获得新闻列表
					Channel selectedChannel = (Channel) jcbChannel.getSelectedItem();// 获得用户选中的频道信息
					String filePath = selectedChannel.getFilePath();// 获取用户选中的频道信息地址
					newsList = rssService.getNewsList(filePath);
					showTable(newsList);

				}
			});

		}
		return jbRead;
	}

	private JButton getJBExport() {
		if (jbExport == null) {
			jbExport = new JButton("导出");
			jbExport.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (rssService.save(newsList)) {
						JOptionPane.showMessageDialog(null, "新闻信息保存成功");
					} else {
						JOptionPane.showMessageDialog(null, "新闻信息保存成功");
					}

				}
			});
		}
		return jbExport;
	}

	private JScrollPane getJSPContent() {
		JScrollPane jspContent = null;
		if (jtaContent == null) {
			jtaContent = new JTextArea(); // 创建新闻内容文本区
			jtaContent.setLineWrap(true); // 设置文本区的换行策略
			jspContent = new JScrollPane(jtaContent); // 创建带文本区的滚动
			jspContent.setPreferredSize(new Dimension(780, 260));
		}
		return jspContent;
	}

	private JScrollPane getJSPTable() {
		JScrollPane jspTable = null;
		if (jTable == null) {
			// 创建表格数据模型，并且添加各列标题
			dtmTableModel = new DefaultTableModel();
			dtmTableModel.addColumn("主题");
			dtmTableModel.addColumn("接受时间");
			dtmTableModel.addColumn("发布时间");
			dtmTableModel.addColumn("作者");

			jTable = new JTable(dtmTableModel); // 根据表格数据模型创建表格

			// 采用匿名内部类的形式为表格添加鼠标单机事件监听器
			jTable.addMouseListener(new MouseAdapter() {
				/**
				 * 重写mouseClicked()方法处理鼠标点击事件
				 */
				public void mouseClicked(MouseEvent e) {
					// 判断是否单击
					if (e.getClickCount() == 1) {
						int selectedRow = jTable.getSelectedRow(); // 获得鼠标单击的信息
						News selectedNews = newsList.get(selectedRow); // 获得选中的新闻信息
						jtaContent.setText(rssService.newsToString(selectedNews)); // 将选中的信息在文本区

					}
				}
			});

			jspTable = new JScrollPane(jTable); // 创建带滚动条的表格
		}
		return jspTable;
	}

	private void showTable(List<News> newsList) {
		// 清除表格的内容
		int rowCount = dtmTableModel.getRowCount();
		while (rowCount > 0) {
			dtmTableModel.removeRow(0);
			rowCount--;
		}

		// 遍历列表内容,将相应的新闻内容显示到表格中
		for (int i = 0; i < newsList.size(); i++) {
			News news = newsList.get(i);

			// 按指定点时间格式,获得当前日期
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String currentDate = dateFormat.format(date);

			// 将新闻的标题,当前日期,发布日期和作者显示在表格中
			String[] data = { news.getTitle(), currentDate, news.getPubDate(), news.getAuthor() };
			dtmTableModel.addRow(data);
		}
	}

}