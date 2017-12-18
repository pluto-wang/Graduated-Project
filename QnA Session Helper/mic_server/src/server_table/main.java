package mic_server;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JRootPane;

public class main {
	static server_t serverUI = new server_t();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame("QnA Session Helper");

		frame.setUndecorated(true);
		frame.add(serverUI);
		serverUI.refresh();
		serverUI.acceptApply();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(800, 600);
		frame.setResizable(false);

		frame.getRootPane().setWindowDecorationStyle(
				JRootPane.INFORMATION_DIALOG);
	}

}