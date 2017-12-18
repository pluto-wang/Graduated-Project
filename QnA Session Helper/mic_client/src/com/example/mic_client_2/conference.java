package com.example.mic_client_2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class conference extends Activity {
	private Button applyButton, stopButton, controlButton;
	private ImageView talk;
	public byte[] buffer;
	public static DatagramSocket socket;
	AudioRecord recorder;

	private int sampleRate = 44100;
	@SuppressWarnings("deprecation")
	private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
	private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig,
			audioFormat);
	private boolean status = true;
	static String t = "no";
	static String account;
	static byte[] t_vo = new byte[15];

	int bufferSizeInBytes;
	int bufferSizeInShorts;
	int shortsRead;
	int id = 0;
	short audioBuffer[];
	static String IP = "192.168.0.161";
	int isTalking = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conference);

		applyButton = (Button) findViewById(R.id.apply_button);
		stopButton = (Button) findViewById(R.id.stop_button);

		talk = (ImageView) findViewById(R.id.talk);
		Bundle bundle = getIntent().getExtras(); // get user's id
		id = bundle.getInt("id");
		Log.d("VS", " id=  " + id);

		talk.setOnClickListener(talkactivity);

		applyButton.setOnClickListener(new View.OnClickListener() {// start
					public void onClick(View v) {
						// flag server allow
						sendApply();

					}

				});

		stopButton.setOnClickListener(new View.OnClickListener() {// stop
					public void onClick(View v) {
						if (isTalking == 1) {
							status = false;
							recorder.release();
							sentStop();
							Log.d("VS", "Recorder released");
							t = "no";
						}
						isTalking = 0;
					}

				});
		
		minBufSize += 2048;//2048
		System.out.println("minBufSize: " + minBufSize);
	}

	private OnClickListener talkactivity = new OnClickListener() {
		public void onClick(View v) {
			if (t.equals("yes") && isTalking == 0) {
				status = true;
				isTalking = 1;
				startStreaming();
			} else if (t.equals("yes") && isTalking == 1) {

			}

		}
	};

	public void startStreaming() {

		Thread streamThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					socket = new DatagramSocket();
					Log.d("VS", "Socket Created");
					byte[] buffer = new byte[minBufSize];
					Log.d("VS", "Buffer created of size " + minBufSize);
					DatagramPacket packet;
					// machine's IP
					final InetAddress destination = InetAddress.getByName(IP);// 163.21.245.164
					Log.d("VS", "Address retrieved");

					recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
							sampleRate, channelConfig, audioFormat,minBufSize * 10);
					Log.d("VS", "Recorder initialized");
					recorder.startRecording();

					while (status == true) { 
						// reading data from MIC into buffer
						minBufSize = recorder.read(buffer, 0, buffer.length);
						// putting buffer in the packet
						packet = new DatagramPacket(buffer, buffer.length,destination, 40000);
						socket.send(packet);
						System.out.println("MinBufferSize: " + minBufSize);

					}

				} catch (UnknownHostException e) {
					Log.e("VS", "UnknownHostException");
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("VS", "IOException");
				}
			}

		});
		streamThread.start();
	}

	public void sendApply() {
		Thread nameThread = new Thread(new Runnable() {
			
			public void run() {
				try { 

					DatagramSocket clientSocket = new DatagramSocket();
					byte[] sendStr = new byte[15];
					Log.e("VS", "sendApply");

					sendStr = "apply".getBytes();
					Log.e("VS", new String(sendStr, "UTF-8"));
					final InetAddress destination = InetAddress.getByName(IP);// 192.168.0.3,192.168.1.71
					DatagramPacket sendPacket = new DatagramPacket(sendStr,sendStr.length, destination, 55000);
					clientSocket.send(sendPacket);
					receiceturn();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("VS", "IOException");
				}
			}
		});
		nameThread.start();
	}

	public void receiveStop() {
		Thread receiveStop = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				byte[] receiveStop = new byte[1];
				try {
					DatagramSocket serverStop = new DatagramSocket(38000);
					DatagramPacket serverStopPacket = new DatagramPacket(
							receiveStop, receiveStop.length);

					serverStop.receive(serverStopPacket);
					status = false;
					recorder.release();
					sentStop();
					Log.d("VS", "Recorder released");
					t = "no";
					isTalking = 0;
					showStop();

				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		receiveStop.start();
	}

	public void showStop() {
		Looper.prepare();
		Toast.makeText(this, "You can't speak now", Toast.LENGTH_LONG).show();
		Looper.loop();
	}

	public void sentStop() {
		Thread stopThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					byte[] sendstop = new byte[1];
					DatagramSocket stopSocket = new DatagramSocket();
					final InetAddress destination = InetAddress.getByName(IP);
					DatagramPacket stopPacket = new DatagramPacket(sendstop,
							sendstop.length, destination, 35000);
					stopSocket.send(stopPacket);

				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
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

	public void receiceturn() {
		try {
			Log.e("VS", "receive start");
			DatagramSocket turn = new DatagramSocket(56000);// receice server
															// allow window turn

			DatagramPacket getturn = new DatagramPacket(t_vo, t_vo.length);// get
																			// allow
																			// from
																			// server

			Log.e("VS", "allow = " + t);
			turn.receive(getturn); // error no get turn
			t = new String(getturn.getData(), "UTF-8");

			t = t.substring(0, 3);
			Log.e("VS", "allow = " + t);
			// Context context = getApplication();
			Log.e("VS", "toast prepare");
			if (t.equals("yes")) {
				Looper.prepare();
				Toast.makeText(this, "You can speak now", Toast.LENGTH_LONG)
						.show();
				Looper.loop();
				Log.e("VS", "toast show");
				receiveStop();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("VS", "IOException");
		}

	}

}