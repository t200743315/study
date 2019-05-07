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

	// ��������
	private final static int WIDTH = 800; // ���ô��ڿ��
	private final static int HEIGHT = 600;// ���ô��ڸ߶�
	private final static String TItile = "RSS�Ķ���";// ���ô��ڱ���

	// �������
	private JComboBox jcbChannel; // ����Ƶ��������
	private JButton jbRead; // ��ȡ��ť
	private JButton jbExport; // ������ť
	private JTextArea jtaContent; // ���������ı���
	private DefaultTableModel dtmTableModel; // �������ģ��
	private JTable jTable; // ���

	private List<News> newsList; // �������ݱ��

	private RSSService rssService; // ҵ���߼�����

	public JMainFrame() {
		rssService = new RSSService();

		this.setTitle(TItile);// ���ô��ڱ���
		this.setSize(WIDTH, HEIGHT);// ���ô��ڴ�С
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ���ô���Ĭ�Ϲر�
		this.setCenter();
		this.setContentPane(getJPMain());
	}

	private void setCenter() {
		Toolkit kToolkit = Toolkit.getDefaultToolkit(); // ���幤�߰�
		Dimension screenSize = kToolkit.getScreenSize(); // ��ȡ��Ļ�ĳߴ�
		setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);

	}

	private JPanel getJPMain() {
		JPanel jpMain = new JPanel(); // ����������
		jpMain.setLayout(new BorderLayout()); // ���������沼��

		jpMain.add(getJPNorth(), BorderLayout.NORTH); // �����������ӵ�������ı���
		jpMain.add(getJSPTable(), BorderLayout.CENTER); // ���������������������ı�����ӵ�������
		jpMain.add(getJSPContent(), BorderLayout.SOUTH); // ���������������������ı�����ӵ������

		return jpMain;
	}

	private JPanel getJPNorth() {
		JPanel jpNorth = new JPanel(); // ������������
		jpNorth.setLayout(new FlowLayout(FlowLayout.LEFT)); // ���ñ������沼��

		JLabel jlChannel = new JLabel("վ��"); // ����վ���ǩ
		jpNorth.add(jlChannel); // ��վ���ǩ��������������
		jpNorth.add(getJCBChannel()); // ��Ƶ����������ӵ���������
		jpNorth.add(getJBRead()); // ����ȡ��ť��ӵ���������
		jpNorth.add(getJBExport()); // ��������ť��ӵ���������

		return jpNorth;
	}

	private JComboBox getJCBChannel() {
		if (jcbChannel == null) {
			jcbChannel = new JComboBox(); // ����Ƶ��������

			// �������Ƶ����Ϣ
			List<Channel> channelList = rssService.getChannelList();
			for (int i = 0; i < channelList.size(); i++) {
				jcbChannel.addItem(channelList.get(i));
			}
		}
		return jcbChannel;
	}

	private JButton getJBRead() {
		if (jbRead == null) {
			jbRead = new JButton("��ȡ");
			jbRead.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// 1��������б�
					Channel selectedChannel = (Channel) jcbChannel.getSelectedItem();// ����û�ѡ�е�Ƶ����Ϣ
					String filePath = selectedChannel.getFilePath();// ��ȡ�û�ѡ�е�Ƶ����Ϣ��ַ
					newsList = rssService.getNewsList(filePath);
					showTable(newsList);

				}
			});

		}
		return jbRead;
	}

	private JButton getJBExport() {
		if (jbExport == null) {
			jbExport = new JButton("����");
			jbExport.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (rssService.save(newsList)) {
						JOptionPane.showMessageDialog(null, "������Ϣ����ɹ�");
					} else {
						JOptionPane.showMessageDialog(null, "������Ϣ����ɹ�");
					}

				}
			});
		}
		return jbExport;
	}

	private JScrollPane getJSPContent() {
		JScrollPane jspContent = null;
		if (jtaContent == null) {
			jtaContent = new JTextArea(); // �������������ı���
			jtaContent.setLineWrap(true); // �����ı����Ļ��в���
			jspContent = new JScrollPane(jtaContent); // �������ı����Ĺ���
			jspContent.setPreferredSize(new Dimension(780, 260));
		}
		return jspContent;
	}

	private JScrollPane getJSPTable() {
		JScrollPane jspTable = null;
		if (jTable == null) {
			// �����������ģ�ͣ�������Ӹ��б���
			dtmTableModel = new DefaultTableModel();
			dtmTableModel.addColumn("����");
			dtmTableModel.addColumn("����ʱ��");
			dtmTableModel.addColumn("����ʱ��");
			dtmTableModel.addColumn("����");

			jTable = new JTable(dtmTableModel); // ���ݱ������ģ�ʹ������

			// ���������ڲ������ʽΪ��������굥���¼�������
			jTable.addMouseListener(new MouseAdapter() {
				/**
				 * ��дmouseClicked()��������������¼�
				 */
				public void mouseClicked(MouseEvent e) {
					// �ж��Ƿ񵥻�
					if (e.getClickCount() == 1) {
						int selectedRow = jTable.getSelectedRow(); // �����굥������Ϣ
						News selectedNews = newsList.get(selectedRow); // ���ѡ�е�������Ϣ
						jtaContent.setText(rssService.newsToString(selectedNews)); // ��ѡ�е���Ϣ���ı���

					}
				}
			});

			jspTable = new JScrollPane(jTable); // �������������ı��
		}
		return jspTable;
	}

	private void showTable(List<News> newsList) {
		// �����������
		int rowCount = dtmTableModel.getRowCount();
		while (rowCount > 0) {
			dtmTableModel.removeRow(0);
			rowCount--;
		}

		// �����б�����,����Ӧ������������ʾ�������
		for (int i = 0; i < newsList.size(); i++) {
			News news = newsList.get(i);

			// ��ָ����ʱ���ʽ,��õ�ǰ����
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String currentDate = dateFormat.format(date);

			// �����ŵı���,��ǰ����,�������ں�������ʾ�ڱ����
			String[] data = { news.getTitle(), currentDate, news.getPubDate(), news.getAuthor() };
			dtmTableModel.addRow(data);
		}
	}

}