package com.example.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SocketDemo extends Activity {
	// 声明部件
	private EditText hostip;
	private EditText hostport;
	private Button connect;
	private Button disconnect;
	private Button led1;
	private Button led2;
	private Button led3;
	private Button led4;
	private TextView temp;
	private TextView humi;
	private TextView t;
	private TextView h;
	private Context context;
	private SeekBar seekbar;

	// 声明变量
	private String _hostip = null;
	private int _hostport = 0;
	public int state1 = -1;
	public int state2 = -1;
	public int state3 = -1;
	public int state4 = -1;
	public int LED1 = 0;
	public int LED2 = 0;
	public int LED3 = 0;
	public int LED4 = 0;
	public int flag = 0;
	public int dis=1;
	public int v = 0;
	public int exit=0;
	// 声明套接字
	private Socket socket;

	// 声明输入输出流
	private DataInputStream is;
	private DataOutputStream os;
	

	@Override
	protected void onCreate(Bundle savedInstancestate1) {
		super.onCreate(savedInstancestate1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_socket_demo);
		context = this;
		final AlertDialog.Builder ab=new AlertDialog.Builder(context);
		

		// 实例化部件
		hostip = (EditText) findViewById(R.id.ethostip);
		hostport = (EditText) findViewById(R.id.ethostport);
		connect = (Button) findViewById(R.id.btconnect);
		disconnect = (Button) findViewById(R.id.btshutdown);
		temp = (TextView) findViewById(R.id.TEMP);
		humi = (TextView) findViewById(R.id.HUMI);
		t=(TextView)findViewById(R.id.t);
		h=(TextView)findViewById(R.id.h);
		led1 = (Button) findViewById(R.id.window1);
		led2 = (Button) findViewById(R.id.window2);
		led3 = (Button) findViewById(R.id.window3);
		led4 = (Button) findViewById(R.id.window4);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);

		// 连接服务器
		connect.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_hostip = hostip.getText().toString();
				_hostport = Integer.parseInt(hostport.getText().toString());
				new Thread(new ConnectThread()).start();
			}

		});

		// 发送消息
		led1.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				state1 = -state1;
				if (state1 == 1) {
					led1.setBackgroundResource(R.drawable.windowh2);
					LED1 = 1;
				}
				if (state1 == -1) {
					led1.setBackgroundResource(R.drawable.windowh1);
					LED1 = 0;
				}
				while (flag == 1)
					;
				new Thread(new sendThread()).start();
				flag = 1;
			}

		});

		led2.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				state2 = -state2;
				if (state2 == 1) {
					led2.setBackgroundResource(R.drawable.windowh2);
					LED2 = 1;
				}
				if (state2 == -1) {
					led2.setBackgroundResource(R.drawable.windowh1);
					LED2 = 0;
				}
				while (flag == 1)
					;
				new Thread(new sendThread()).start();
				flag = 1;
			}

		});

		led3.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				state3 = -state3;
				if (state3 == 1) {
					led3.setBackgroundResource(R.drawable.windowl2);
					LED3 = 1;
				}
				if (state3 == -1) {
					led3.setBackgroundResource(R.drawable.windowl1);
					LED3 = 0;
				}
				while (flag == 1)
					;
				new Thread(new sendThread()).start();
				flag = 1;
			}

		});

		led4.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				state4 = -state4;
				if (state4 == 1) {
					led4.setBackgroundResource(R.drawable.windowl2);
					LED4 = 1;
				}
				if (state4 == -1) {
					led4.setBackgroundResource(R.drawable.windowl1);
					LED4 = 0;
				}
				while (flag == 1)
					;
				new Thread(new sendThread()).start();
				flag = 1;
			}

		});

		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				v = progress;
				while (flag == 1)
					;
				new Thread(new sendThread()).start();
				flag = 1;
			}
		});

		// 断开服务器连接
		disconnect.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exit=1;
				while (flag == 1)
					;
				new Thread(new sendThread()).start();
				flag = 1;
				ab.setTitle("提示");
				ab.setMessage("是否断开连接");
				ab.setPositiveButton("是", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						System.exit(0);
					}
				});
				ab.setNegativeButton("否", null);
				ab.show();
				dis=0;
				try {
					closeSocket();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	private void closeSocket() {
		try {
			os.close();
			is.close();
			socket.close();
		} catch (IOException e) {
			handleException(e, "close exception: ");
		}
	}

	private void toastText(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public void handleException(Exception e, String prefix) {
		e.printStackTrace();
		toastText(prefix + e.toString());
	}

	// 显示消息
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (0x123 == msg.what) {
				toastText("连接成功！");
			}
			if (0x124 == msg.what) {
				String[] data=msg.obj.toString().split("#");
				temp.setText(data[1]);
				humi.setText(data[2]);
				if("0".equals(data[3])) {
					AlertDialog.Builder ab=new AlertDialog.Builder(context);
					ab.setTitle("警告");
					ab.setMessage("检测到可燃气体、烟雾警报");
					ab.setPositiveButton("确定", null);
					ab.show();
				}
			}
		}
	};

	// 连接服务器子线程
	public class ConnectThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = Message.obtain();
			try {
				if (socket == null || socket.isClosed()) {
					socket = new Socket(_hostip, _hostport);
				}
				new Thread(new ReceiveThread()).start();
				msg.what = 0x123;
				handler.sendMessage(msg);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	// 发送消息子线程
	public class sendThread implements Runnable {

		String msg = "";

		@Override
		public void run() {
			// TODO Auto-generated method stub
			msg = "buct#"+String.valueOf(LED1) + String.valueOf(LED2) + String.valueOf(LED3) + String.valueOf(LED4)
					+ String.valueOf(v);
			try {
				if (socket == null || socket.isClosed()) {
					socket = new Socket(_hostip, _hostport);
				}
				OutputStream ou = socket.getOutputStream();
				DataOutputStream os = new DataOutputStream(ou);
				if(exit==0) {
					os.write(msg.getBytes());
					os.flush();
				}
				if(exit==1) {
					os.write("buct#shutdown".getBytes());
					os.flush();
				}
				flag = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// 接收消息子线程
	public class ReceiveThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message mes = Message.obtain();
			while (dis==1) {
				try {
					if (socket == null || socket.isClosed()) {
						socket = new Socket(_hostip, _hostport);
					}
					InputStream input = socket.getInputStream();
					DataInputStream is = new DataInputStream(input);

					byte[] b = new byte[1024 * 100];
					int length = is.read(b);
					String receive = new String(b, 0, length, "UTF-8");
					mes.what = 0x124;
					mes.obj = receive;
					handler.sendMessage(mes);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
