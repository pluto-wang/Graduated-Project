package mic_server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class server_t extends JPanel {
	static final int w = 30;
	static final int h = 50;
	static final int Max = 100;
	static int count, flag, index, x, y, li, getr, getc, bu = 0;

	static Rectangle[] squares = new Rectangle[Max];
	static int[] arrX = new int[Max];
	static int[] arrY = new int[Max];
	static String name[] = new String[2];
	static InetAddress clientIP[];
	static JButton[] b = new JButton[Max];
	static JButton create, finish, clean, mesh;
	static JLabel l1, l2, l3, r, c;
	static JTextField row, col;
	private Label seatTableText;
	private Label speakers, login, apply, talking, talkingName, loginText,
			applyText, talkingText;
	private JButton interrupt;
	private JPanel seatTable, statusPanel, space, space2;
	private static JPanel seatTableSpace, buttonPanel, upPanel, downPanel;

	AudioInputStream audioInputStream;
	protected static DatagramSocket applySocket;
	protected static ServerSocket serverSocket3;
	protected static ServerSocket serverSocket2;
	static AudioInputStream ais;
	static AudioFormat format;
	static boolean status = true;
	static int sampleRate = 44100;
	static DatagramSocket servervo;
	static DatagramPacket getControl;
	static DatagramSocket control;
	private static Thread startThread;
	static Font font;
	static int iname = -1;
	static int hasTalking = 0;
	static Thread stopThread;
	static Thread repaintThread;
	static Thread acceptThread;

	static ServerSocket serverSocket;
	static ServerSocket serverSocket1;
	static boolean yes = true;

	public server_t() {
		clientIP = new InetAddress[Max];
		font = new Font(Font.SERIF, Font.PLAIN, 20);
		setLayout(new BorderLayout());
		// seatTable
		seatTable = new JPanel();
		space2 = new JPanel();
		seatTableSpace = new JPanel();
		buttonPanel = new JPanel();
		upPanel = new JPanel();
		downPanel = new JPanel();
		r = new JLabel();
		c = new JLabel();
		row = new JTextField();
		col = new JTextField();
		seatTableText = new Label("Seating Table");
		space2.setPreferredSize(new Dimension(300, 20));
		seatTableText.setPreferredSize(new Dimension(350, 20));
		seatTable.setPreferredSize(new Dimension(650, 600));
		seatTable.add(space2);
		seatTable.add(seatTableText);
		seatTableSpace.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) { // call add
				int x = evt.getX();
				int y = evt.getY();

				if (x < 600 && y < 370 && flag == 1) { // not inside a square

					arrX[count] = x;
					arrY[count] = y;
					count++;
					add(x, y);

				}

			}
		});
		for (int i = 0; i < Max; i++) {
			clientIP[i] = null;
			b[i] = new JButton();
			b[i].setName(Integer.toString(i));
			b[i].setMargin(new Insets(0, 0, 0, 0));
			b[i].setFont(font);
			b[i].setPreferredSize(new Dimension(50, 30));
			b[i].addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (talkingName.getText().equals("")) {
						iname = Integer.parseInt(((JButton) (e.getSource()))
								.getName());
						System.out.print(((JButton) (e.getSource())).getText());
						if (((JButton) (e.getSource())).getBackground() == Color.red
								&& (iname != -1)
								& !(((JButton) (e.getSource())).getText()
										.equals(""))) {

							Object[] options = { "ACCEPT", "REJECT" };
							int response = JOptionPane.showOptionDialog(null,
									"   Do you allow the user to talk ?",
									"Confirm", JOptionPane.YES_NO_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, options,
									options[0]);

							if (response == JOptionPane.YES_OPTION) {
								talkingName.setAlignment(Label.CENTER);
								talkingName.setFont(new Font("sansserif",
										Font.BOLD, 24));
								talkingName.setText(((JButton) e.getSource())
										.getText());
								((JButton) (e.getSource()))
										.setBackground(Color.GREEN);
								for(int i=0;i<index;i++){
									if(i!=iname& !b[i].getText()
											.equals("")){
										b[i].setBackground(Color.blue);
									}
								}
								status = true;
								allow_talk();
							} else {
								((JButton) (e.getSource())).setBackground(Color.BLUE);
								iname = -1;
							}
						} else if (((JButton) (e.getSource())).getText()
								.equals("")) {
							iname = -1;
						}
					}
				}
			});
			seatTableSpace.repaint();
		}
		create = new JButton("create");
		clean = new JButton("clean");
		finish = new JButton("finish");
		mesh = new JButton("mesh");
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				flag = 1;
			}

		}

		);

		mesh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getr = Integer.parseInt(row.getText());
				getc = Integer.parseInt(col.getText());
				seatTableSpace.repaint();
				for (int i = 0; i < getr; i++) {
					for (int j = 0; j < getc; j++) {
						seatTableSpace.add(b[index]).setBounds(85 + (j * 60),
								100 + (i * 50), 50, 30);
						index++;
						arrX[count] = 85 + (j * 60);
						arrY[count] = 100 + (i * 50);
						count++;
					}
				}

			}

		});

		clean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] options = { "YES", "NO" };
				int response = JOptionPane.showOptionDialog(null,
						"   Do you want to clean table?", "Confirm",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, options, options[0]);

				if (response == JOptionPane.YES_OPTION) {

					for (int i = 0; i < count; i++) {
						seatTableSpace.remove(b[i]);

						arrX[i] = 0;
						arrY[i] = 0;

						li = 0;

					}
					index = 0;
					count = 0;
					seatTableSpace.repaint();
				}
			}
		});

		finish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				flag = 0;
				for (int i = 0; i < arrX.length && arrX[i] > 0; i++) {
					li++;// arrayX content size
				}
				seatTableSpace.repaint();
				for (int i = 0; i < count; i++)
					b[i].setLocation(arrX[i], arrY[i]);
				sendX();
				sendY();

			}
		}

		);
		seatTable.add(create);
		seatTable.add(clean);
		seatTable.add(finish);
		seatTable.add(mesh);
		seatTableSpace.setPreferredSize(new Dimension(650, 400));
		seatTableSpace.setBackground(Color.WHITE);
		seatTable.add(seatTableSpace);
		upPanel.add(create);
		upPanel.add(clean);
		upPanel.add(finish);

		// upPanel.add(repaint);
		seatTable.add(buttonPanel);
		buttonPanel.setLayout(new GridLayout(2, 1));
		downPanel.add(c);
		c.setText("column =");
		downPanel.add(col);
		col.setPreferredSize(new Dimension(50, 30));
		col.setText("");
		downPanel.add(r);
		r.setText("row =");
		downPanel.add(row);
		row.setPreferredSize(new Dimension(50, 30));
		row.setText("");
		downPanel.add(mesh);
		buttonPanel.add(upPanel);
		buttonPanel.add(downPanel);
		add(seatTable, BorderLayout.CENTER);
		// status

		speakers = new Label();
		login = new Label();
		apply = new Label();
		talking = new Label();
		talkingName = new Label();
		loginText = new Label("login");
		applyText = new Label("apply");
		talkingText = new Label("talking");
		interrupt = new JButton();
		statusPanel = new JPanel();
		space = new JPanel();
		space.setPreferredSize(new Dimension(150, 200));
		statusPanel.setPreferredSize(new Dimension(150, 600));
		speakers.setText("Speaker:");
		speakers.setPreferredSize(new Dimension(150, 50));
		interrupt.setText("Interrupt");
		talkingName.setText("");
		talkingName.setPreferredSize(new Dimension(150, 50));
		login.setBackground(Color.BLUE);
		login.setPreferredSize(new Dimension(60, 30));
		apply.setBackground(Color.RED);
		apply.setPreferredSize(new Dimension(60, 30));
		talking.setBackground(Color.GREEN);
		talking.setPreferredSize(new Dimension(60, 30));
		statusPanel.add(speakers);
		statusPanel.add(talkingName);
		statusPanel.add(interrupt);
		statusPanel.add(space);
		statusPanel.add(login);
		loginText.setPreferredSize(new Dimension(60, 30));
		statusPanel.add(loginText);
		statusPanel.add(apply);
		applyText.setPreferredSize(new Dimension(60, 30));
		statusPanel.add(applyText);
		statusPanel.add(talking);
		talkingText.setPreferredSize(new Dimension(60, 30));
		statusPanel.add(talkingText);
		add(statusPanel, BorderLayout.AFTER_LINE_ENDS);
		interrupt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				Object[] options = { "YES", "NO" };
				int response = JOptionPane.showOptionDialog(null,
						"   Do you want to interrupt speakers ?", "Confirm",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, options, options[0]);

				if (response == JOptionPane.YES_OPTION) {
					talkingName.setText("");
					if (iname != -1) {
						b[iname].setBackground(Color.BLUE);
						status = false;
						System.out.print("stop!");
						sendStop();
						//notifyAll();
						statusPanel.repaint();
						re();
					}
					
				}
			}

		});

		try {
			servervo = new DatagramSocket(40000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getId();
		update();
		receiveStop();

	}

	public static void add(int x, int y) {
		if (index < Max) {
			squares[index] = new Rectangle(x, y, h, w);
			seatTableSpace.add(b[index]).setBounds(x, y, h, w);
			index++;
			seatTableSpace.repaint();
			// invalidate();
		}
	}

	public void setButton() {
		int x;// = evt.getX();
		int y;// = evt.getY();

		for (int i = 0; i < b.length; i++) {
			x = b[i].getLocation().x;
			y = b[i].getLocation().y;

		}

	}

	public static void sendX() {
		new Thread() {
			public void run() {
				try {

					serverSocket = new ServerSocket(53000);
					while (yes) {

						Socket connection = serverSocket.accept();
						String out = "";
						for (int j = 0; j < li; j++) {
							out += arrX[j] + "::";
						}

						DataOutputStream output = new DataOutputStream(
								connection.getOutputStream());
						output.writeBytes(out + "\n");

						connection.close();
					}
				} catch (IOException e) {e.printStackTrace();}
			}
		}.start();
	}

	public static void sendY() {
		new Thread() {
			public void run() {
				try {

					serverSocket1 = new ServerSocket(54000);
					while (yes) {
						Socket connection = serverSocket1.accept();

						String out = "";
						for (int j = 0; j < li; j++) {

							out += arrY[j] + "::";
						}
						DataOutputStream output = new DataOutputStream(
								connection.getOutputStream());
						output.writeBytes(out + "\n");
						connection.close();
						// serverSocket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	public static void update() {// always run in background to update the
									// seating table
		new Thread() {
			public void run() {

				try {
					serverSocket2 = new ServerSocket(50000);
					while (yes) {
						Socket connection = serverSocket2.accept();

						String out = "";
						DataOutputStream output = new DataOutputStream(
								connection.getOutputStream());
						for (int j = 0; j < count; j++) {
							out += j + "::" + b[j].getText() + " /:"; // (index,username)
						}
						output.writeBytes(out + "\n");
						connection.close();

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void getId() { // get client button information
		new Thread() {
			public void run() {
				String info = "";
				try {

					serverSocket3 = new ServerSocket(51000);
					while (yes) {
						Socket client = serverSocket3.accept();
						BufferedReader input = new BufferedReader(
								new InputStreamReader(client.getInputStream()));
						info = input.readLine();
						System.out.println(info);
						String name[] = info.split("::");

						System.out.print(client.getInetAddress());
						bu = Integer.parseInt(name[0]);
						b[bu].setText(name[1]);
						clientIP[bu] = client.getInetAddress();
						b[bu].setFont(new Font("sansserif", Font.BOLD, 12));
						b[bu].setHorizontalAlignment(JButton.LEFT);
						b[bu].setBackground(Color.blue);
						seatTableSpace.repaint();
						for (int i = 0; i < count; i++)
							b[i].setLocation(arrX[i], arrY[i]);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	public static void toSpeaker(byte soundbytes[]) {
		try {

			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

			sourceDataLine.open(format);

			FloatControl volumeControl = (FloatControl) sourceDataLine
					.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(6.00f);

			sourceDataLine.start();
			sourceDataLine.open(format);
			sourceDataLine.start();
			sourceDataLine.write(soundbytes, 0, soundbytes.length);
			sourceDataLine.drain();
			sourceDataLine.close();

		} catch (Exception e) {
			System.out.println("Not working in speakers...");
			e.printStackTrace();
		}
	}

	public static void acceptApply() {
		acceptThread = new Thread(new Runnable() {
			public void run() {

				try {
					byte[] accept = new byte[15];
					applySocket = new DatagramSocket(55000);
					DatagramPacket apply = new DatagramPacket(accept,accept.length);
					while (yes) {
						System.out.print("ready to accept");
						applySocket.receive(apply);
						System.out.print(new String(accept, "UTF-8").substring(
								0, 5));
						if ((new String(accept, "UTF-8")).substring(0, 5)
								.equals("apply")) {
							for (int i = 0; i < index; i++) {
								if (apply.getAddress().equals(clientIP[i])) {
									b[i].setBackground(Color.RED);
								}
							}
						}

					}
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		acceptThread.start();
	}

	public static void allow_talk() {// send to client

		try {
			DatagramSocket allow = new DatagramSocket();
														
			yes = false;
			serverSocket.close();
			serverSocket1.close();
			serverSocket2.close();
			serverSocket3.close();
			applySocket.close();
			byte[] turn = new byte[15];
			String flag = new String("yes");
			turn = flag.getBytes();
			final InetAddress destination;
			if (iname != -1) {
				destination = clientIP[iname];
			} else {
				destination = null;
			}
			System.out.println("test yes");
			DatagramPacket sendturn = new DatagramPacket(turn, turn.length,
					destination, 56000);
			allow.send(sendturn);
			System.out.println("send ");
			startTalk();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public static void startTalk() {
		startThread = new Thread(new Runnable() {

			@Override
			public void run() {

				byte[] receiveData = new byte[8192];//8192
				format = new AudioFormat(sampleRate, 16, 1, true, false);
				startThread.setPriority(Thread.MAX_PRIORITY);
				while (status == true) {

					DatagramPacket receivePacket = new DatagramPacket(
							receiveData, receiveData.length);
					try {
						servervo.receive(receivePacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ByteArrayInputStream baiss = new ByteArrayInputStream(
							receivePacket.getData());
					ais = new AudioInputStream(baiss, format,
							receivePacket.getLength());
					toSpeaker(receivePacket.getData());

				}
			}

		});

		startThread.start();

	}

	public void sendStop() {
		Thread serverStop = new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				byte[] sendStopMsg = new byte[1];
				try {
					DatagramSocket serverStopSocket = new DatagramSocket();
					DatagramPacket serverStopPacket = new DatagramPacket(
							sendStopMsg, sendStopMsg.length, clientIP[iname],
							38000);
					serverStopSocket.send(serverStopPacket);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		serverStop.start();
	}

	public void re() {
		if (!yes) {
			yes = true;
			sendX();
			sendY();
			update();
			getId();
			acceptApply();
		}
	}

	public void receiveStop() {
		stopThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					byte[] receive = new byte[1];

					DatagramSocket stopSocket = new DatagramSocket(35000);
					DatagramPacket stopPacket = new DatagramPacket(receive,
							receive.length);
					while (true) {
						stopSocket.receive(stopPacket);
						talkingName.setText("");
						if (iname != -1) {
							b[iname].setBackground(Color.BLUE);
							status = false;
							statusPanel.repaint();
							re();
						}
					}

				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		stopThread.start();
	}

	public void refresh() {
		repaintThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					seatTableSpace.repaint();
					for (int i = 0; i < count; i++)
						b[i].setLocation(arrX[i], arrY[i]);
				}
			}

		});
		repaintThread.start();
	}

}





