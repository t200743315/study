package com.ruanko.view;

import com.ruanko.service.UpdateThread;

public class Start {
	public static void main(String[] args) {
		JMainFrame frame = new JMainFrame();
		frame.setVisible(true);
		UpdateThread updateThread = new UpdateThread();
		updateThread.start();
	}
}
