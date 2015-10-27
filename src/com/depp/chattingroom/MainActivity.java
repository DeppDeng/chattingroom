package com.depp.chattingroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable{
	private TextView txtshow;
	private EditText editsend;
	private Button btnsend;
	private static final String HOST = "192.168.56.1";
	private static final int PORT = 12345;
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String content = "";
	private StringBuilder sb = null;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				sb.append(content);
				txtshow.setText(sb.toString());
			}
		}

		;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sb = new StringBuilder();
		txtshow = (TextView) findViewById(R.id.txtshow);
		editsend = (EditText) findViewById(R.id.editsend);
		btnsend = (Button) findViewById(R.id.btnsend);

		new Thread() {

			public void run() {
				try {
					socket = new Socket(HOST, PORT);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

		btnsend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String msg = editsend.getText().toString();
				if (socket.isConnected()) {
					if (!socket.isOutputShutdown()) {
						out.println(msg);
					}
				}
			}
		});
		new Thread(MainActivity.this).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (socket.isConnected()) {
					if (!socket.isInputShutdown()) {
						if ((content = in.readLine()) != null) {
							content += "\n";
							handler.sendEmptyMessage(0x123);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
