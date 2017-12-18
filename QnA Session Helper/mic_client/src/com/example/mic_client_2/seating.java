package com.example.mic_client_2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.animation.AnimatorSet.Builder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class seating extends Activity {

	static String IP = "192.168.0.161";
	public final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	RelativeLayout linearLayout;
	int flag, one, index = 0;
	int li = 0;
	int X[] = new int[150];
	int Y[] = new int[150];
	Button[] saveButton;
	String upname = "";
	String name = "";
	int id;
	boolean keep = true;

	private class connectX extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... params) {
			String result = "";

			try {
				Socket client = new Socket(IP, 53000);// 192.168.1.69
				BufferedReader input = new BufferedReader(
						new InputStreamReader(client.getInputStream()));
				result = input.readLine();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			String arrayX[] = result.split("::");

			for (int i = 0; i < arrayX.length; i++) {
				X[i] = Integer.parseInt(arrayX[i]);
				Log.e("X", arrayX[i]);

			}

			for (int i = 0; X[i] != 0; i++) {

				li++;// arrayX content size
			}
			Log.e("li=", li + "");
			if (flag == 1) {
				setButton();
			}
			flag = 1;
		}

	}

	private class connectY extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... params) {
			String result = "";

			try {
				Socket client = new Socket(IP, 54000);// 192.168.1.69
				BufferedReader input = new BufferedReader(
						new InputStreamReader(client.getInputStream()));
				result = input.readLine();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			String arrayY[] = result.split("::");
			for (int i = 0; i < arrayY.length; i++) {
				Y[i] = Integer.parseInt(arrayY[i]);
				Log.e("Y", arrayY[i]);
			}
			if (flag == 1) {
				setButton();
			}
			flag = 1;
		}
	}

	private class update extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... params) {

			String result = "";

			try {
				Thread.sleep(1500);
				Socket client = new Socket(IP, 50000);// 192.168.1.69
				BufferedReader input = new BufferedReader(
						new InputStreamReader(client.getInputStream()));
				result = input.readLine();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch blockljjjjk
				e.printStackTrace();
			}

			return result;

		}

		protected void onPostExecute(String result) {
			String token[] = result.split("/:");
			for (int i = 0; i < token.length; i++) {
				String up[] = token[i].split("::");
				index = Integer.parseInt(up[0]);
				upname = up[1];
				Log.e("update=", index + "" + upname);
				saveButton[index].setText(upname);
			}
			if (keep) {
				new update().execute();
			}
		}
	}

	void setButton() {

		saveButton = new Button[li];

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Button next = new Button(this);
		next.setText("NEXT");
		next.setX(metrics.widthPixels / 2 - 30); // need to adjust
		next.setY(metrics.heightPixels - 200);

		for (int c = 0; c < li; c++) {

			Button b = new Button(this);
			saveButton[c] = b;
			b.setText("");
			b.setId(c); // button index
			b.setX(X[c]);
			b.setY(Y[c]);

			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					id = v.getId();
					AlertDialog(id);

				}
			});

			linearLayout.addView(b, createParam(60, 50));

		}
		linearLayout.addView(next);
		next.setOnClickListener(new OnClickListener() {// send user's id
			public void onClick(View v) {

				Intent i = new Intent(seating.this, conference.class);
				Bundle bundle = new Bundle();
				bundle.putInt("id", id);
				i.putExtras(bundle);
				startActivity(i);
				keep = false;
				finish();

			}
		});

	}

	private class sendId extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Socket connection;
			try {
				connection = new Socket(IP, 51000);
				if (one == 0) {
					DataOutputStream output = new DataOutputStream(
							connection.getOutputStream());
					output.writeBytes(params[0] + "\n");
				}
				one = 1;
				connection.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	void AlertDialog(final int id) {
		// one time problem ,reset location problem
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("set location").setMessage(
				"According to the location of button,please set your seating");

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.e("click", "" + id);
				String out = "";
				out += id + "::" + name;
				new sendId().execute(out);

			}
		};
		DialogInterface.OnClickListener no = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		};

		builder.setPositiveButton("Cancel", no);
		builder.setNegativeButton("Finish", OkClick);
		builder.show();
	}

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras(); // get user's account
		name = bundle.getString("account");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		linearLayout = new RelativeLayout(this);
		setContentView(linearLayout);

		new connectX().execute();
		new connectY().execute();
		new update().execute();

	}

	private LinearLayout.LayoutParams createParam(int w, int h) {
		return new LinearLayout.LayoutParams(w, h);
	}

}
