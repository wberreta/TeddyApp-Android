package com.example.bluetoothstm32;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
 
import com.example.bluetoothstm32.MainActivity;
import com.example.bluetoothstm32.SetPreferenceActivity;
import com.example.bluetoothstm32.R;
 
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
	  private static final String TAG = "bluetoothSTM32";
	   
//	  Button btnForward, btnBackward, btnLeft, btnRight, btnStop, btnHover, btnThplus, btnThminus, btnStart;
	  ImageButton imageButtonWalk, imageButtonDance, imageButtonWave, imageButtonKick,
              imageButtonEyes, imageButtonSound1, imageButtonSound2, imageButtonSound3, imageButtonSound4, imageButtonConnectBT;
	  TextView txtArduino;
	  Handler h;
	  private String address; 	// MAC-address
	   
	  private static final int REQUEST_ENABLE_BT = 1;
	  final int RECIEVE_MESSAGE = 1;		// Status for Handler
	  private BluetoothAdapter btAdapter = null;
	  private BluetoothSocket btSocket = null;
	  private StringBuilder sb = new StringBuilder();
	  
	  private ConnectedThread mConnectedThread;
	   
	  // SPP UUID sevice 
	  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	 
   
	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	 
	    setContentView(R.layout.activity_main);
	    
	    address = (String) getResources().getText(R.string.default_MAC);
	 
//	    btnForward = (Button) findViewById(R.id.btn_Forward);					// button ON
//	    btnBackward = (Button) findViewById(R.id.btn_Backward);				// button OFF
//		btnLeft = (Button) findViewById(R.id.btn_Left);
//		btnRight = (Button) findViewById(R.id.btn_Right);
//		btnStop = (Button) findViewById(R.id.btn_Stop);
//		btnHover = (Button) findViewById(R.id.btn_Hover);
//		btnThplus = (Button) findViewById(R.id.btn_Thplus);
//		btnThminus = (Button) findViewById(R.id.btn_Thminus);
//		btnStart = (Button) findViewById(R.id.btn_Start);

		imageButtonWalk = (ImageButton) findViewById(R.id.imageButtonWalk);
		imageButtonDance = (ImageButton) findViewById(R.id.imageButtonDance);
        imageButtonWave = (ImageButton) findViewById(R.id.imageButtonWave);
        imageButtonKick = (ImageButton) findViewById(R.id.imageButtonKick);
        imageButtonEyes = (ImageButton) findViewById(R.id.imageButtonEyes);
        imageButtonSound1 = (ImageButton) findViewById(R.id.imageButtonSound1);
        imageButtonSound2 = (ImageButton) findViewById(R.id.imageButtonSound2);
        imageButtonSound3 = (ImageButton) findViewById(R.id.imageButtonSound3);
        imageButtonSound4 = (ImageButton) findViewById(R.id.imageButtonSound4);
        imageButtonConnectBT = (ImageButton) findViewById(R.id.imageButtonConnectBT);




		  loadPref();
	    
	    h = new Handler() {
	    	public void handleMessage(android.os.Message msg) {
	    		switch (msg.what) {
	            case RECIEVE_MESSAGE:													// if message is recieved
	            	byte[] readBuf = (byte[]) msg.obj;
	            	String strIncom = new String(readBuf, 0, msg.arg1);
	            	sb.append(strIncom);												// append string
	            	int endOfLineIndex = sb.indexOf("\r\n");							// determine end of line
	            	if (endOfLineIndex > 0) { 											// id end of line,
	            		String sbprint = sb.substring(0, endOfLineIndex);
	                    sb.delete(0, sb.length());										// slear sb
	                	txtArduino.setText("Arduino: " + sbprint); 	        // update TextView
//	                	btnForward.setEnabled(true);
//	                	btnBackward.setEnabled(true);
//						btnLeft.setEnabled(true);
//						btnRight.setEnabled(true);
//						btnStop.setEnabled(true);
//						btnHover.setEnabled(true);
//						btnThplus.setEnabled(true);
//						btnThminus.setEnabled(true);
//						btnStart.setEnabled(true);

                        imageButtonWalk.setEnabled(true);
                        imageButtonDance.setEnabled(true);
                        imageButtonWave.setEnabled(true);
                        imageButtonKick.setEnabled(true);
                        imageButtonSound1.setEnabled(true);
                        imageButtonSound2.setEnabled(true);
                        imageButtonSound3.setEnabled(true);
                        imageButtonSound4.setEnabled(true);
                        imageButtonConnectBT.setEnabled(true);

					}
	            	//Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
	            	break;
	    		}
	        };
		};
	     
	    btAdapter = BluetoothAdapter.getDefaultAdapter();		// Bluetooth adapter
	    checkBTState();

//		  btnHover.setOnClickListener(new OnClickListener() {
//			  public void onClick(View v) {
//				  btnHover.setEnabled(true);
//				  mConnectedThread.write("1");	// Send string via Bluetooth
//				  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
//			  }
//		  });
//
//	    btnForward.setOnClickListener(new OnClickListener() {
//	      public void onClick(View v) {
//	    	btnForward.setEnabled(true);
//	    	mConnectedThread.write("2");	// Send string via Bluetooth
//	        //Toast.makeText(getBaseContext(), "Try LED On", Toast.LENGTH_SHORT).show();
//	      }
//	    });
//
//        btnLeft.setOnClickListener(new OnClickListener() {
//              public void onClick(View v) {
//                  btnLeft.setEnabled(true);
//                  mConnectedThread.write("3");	// Send string via Bluetooth
//                  //Toast.makeText(getBaseContext(), "Try LED On", Toast.LENGTH_SHORT).show();
//              }
//          });
//
//        btnRight.setOnClickListener(new OnClickListener() {
//              public void onClick(View v) {
//                  btnRight.setEnabled(true);
//                  mConnectedThread.write("4");	// Send string via Bluetooth
//                  //Toast.makeText(getBaseContext(), "Try LED On", Toast.LENGTH_SHORT).show();
//              }
//          });
//
//	    btnBackward.setOnClickListener(new OnClickListener() {
//	      public void onClick(View v) {
//	    	btnBackward.setEnabled(true);
//	    	mConnectedThread.write("5");	// Send string via Bluetooth
//	        //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
//	      }
//	    });
//
//		  btnStop.setOnClickListener(new OnClickListener() {
//			  public void onClick(View v) {
//			  btnStop.setEnabled(true);
//			  mConnectedThread.write("6");	// Send string via Bluetooth
//			  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
//			  }
//		  });
//		  btnThplus.setOnClickListener(new OnClickListener() {
//			  public void onClick(View v) {
//				  btnThplus.setEnabled(true);
//				  mConnectedThread.write("7");	// Send string via Bluetooth
//				  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
//			  }
//		  });
//		  btnThminus.setOnClickListener(new OnClickListener() {
//			  public void onClick(View v) {
//				  btnThminus.setEnabled(true);
//				  mConnectedThread.write("8");	// Send string via Bluetooth
//				  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
//			  }
//		  });
//		  btnStart.setOnClickListener(new OnClickListener() {
//			  public void onClick(View v) {
//				  btnStart.setEnabled(true);
//				  mConnectedThread.write("9");	// Send string via Bluetooth
//				  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
//			  }
//		  });

 		  imageButtonWalk.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
                  imageButtonWalk.setEnabled(true);
				  mConnectedThread.write("1");	// Send string via Bluetooth
				  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
			  }
		  });

          imageButtonDance.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonDance.setEnabled(true);
                  mConnectedThread.write("2");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonWave.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonWave.setEnabled(true);
                  mConnectedThread.write("3");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonKick.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonKick.setEnabled(true);
                  mConnectedThread.write("4");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonEyes.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonEyes.setEnabled(true);
                  mConnectedThread.write("5");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonSound1.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonSound1.setEnabled(true);
                  mConnectedThread.write("6");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonSound2.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonSound2.setEnabled(true);
                  mConnectedThread.write("7");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonSound3.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonSound3.setEnabled(true);
                  mConnectedThread.write("8");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonSound4.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonSound4.setEnabled(true);
                  mConnectedThread.write("9");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

          imageButtonConnectBT.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  imageButtonConnectBT.setEnabled(true);
                  mConnectedThread.write("0");	// Send string via Bluetooth
                  //Toast.makeText(getBaseContext(), "Try LED Off", Toast.LENGTH_SHORT).show();
              }
          });

	  }
	   
	  @Override
	  public void onResume() {
	    super.onResume();
	 
	    Log.d(TAG, "...onResume - try connect...");
	   
	    
	    if(!BluetoothAdapter.checkBluetoothAddress(address)){
    		//errorExit("Fatal Error", "Incorrect MAC-address");
    		Toast.makeText(getBaseContext(), "Incorrect MAC-address", Toast.LENGTH_SHORT).show();
    	}
    	else{
		    // Set up a pointer to the remote node using it's address.
		    BluetoothDevice device = btAdapter.getRemoteDevice(address);
		   
		    // Two things are needed to make a connection:
		    //   A MAC address, which we got above.
		    //   A Service ID or UUID.  In this case we are using the
		    //     UUID for SPP.
		    try {
		      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		    } catch (IOException e) {
		      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
		    }
		   
		    // Discovery is resource intensive.  Make sure it isn't going on
		    // when you attempt to connect and pass your message.
		    btAdapter.cancelDiscovery();
		   
		    // Establish the connection.  This will block until it connects.
		    Log.d(TAG, "...Connecting...");
		    try {
		      btSocket.connect();
		      Log.d(TAG, "...Connection is OK...");
		    } catch (IOException e) {
		      try {
		        btSocket.close();
		      } catch (IOException e2) {
		        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
		      }
		    }
		     
		    // Create a data stream so we can talk to server.
		    Log.d(TAG, "...Create Socket...");
		   
		    mConnectedThread = new ConnectedThread(btSocket);
		    mConnectedThread.start();
    	}
	  }
	 
	  @Override
	  public void onPause() {
	    super.onPause();
	 
	    Log.d(TAG, "...In onPause()...");
	  
	    try     {
	      btSocket.close();
	    } catch (IOException e2) {
	      errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
	    }
	  }
	   
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  loadPref();
	  }
	  
	  
	  private void checkBTState() {
	    // Check for Bluetooth support and then check to make sure it is turned on
	    // Emulator doesn't support Bluetooth and will return null
	    if(btAdapter==null) { 
	      errorExit("Fatal Error", "Bluetooth is not support");
	    } else {
	      if (btAdapter.isEnabled()) {
	        Log.d(TAG, "...Bluetooth is ON...");
	      } else {
	        //Prompt user to turn on Bluetooth
	        Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	      }
	    }
	  }
	 
	  private void errorExit(String title, String message){
	    Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
	    finish();
	  }
	 
	  private class ConnectedThread extends Thread {
		    private final BluetoothSocket mmSocket;
		    private final InputStream mmInStream;
		    private final OutputStream mmOutStream;
		 
		    public ConnectedThread(BluetoothSocket socket) {
		        mmSocket = socket;
		        InputStream tmpIn = null;
		        OutputStream tmpOut = null;
		 
		        // Get the input and output streams, using temp objects because
		        // member streams are final
		        try {
		            tmpIn = socket.getInputStream();
		            tmpOut = socket.getOutputStream();
		        } catch (IOException e) { }
		 
		        mmInStream = tmpIn;
		        mmOutStream = tmpOut;
		    }
		 
		    public void run() {
		        byte[] buffer = new byte[256];  // buffer store for the stream
		        int bytes; // bytes returned from read()

		        // Keep listening to the InputStream until an exception occurs
		        while (true) {
		        	try {
		                // Read from the InputStream
		                bytes = mmInStream.read(buffer);
	                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
		            } catch (IOException e) {
		                break;
		            }
		        }
		    }
		 
		    /* Call this from the main activity to send data to the remote device */
		    public void write(String message) {
		    	Log.d(TAG, "...String to send: " + message + "...");
		    	byte[] msgBuffer = message.getBytes();
		    	try {
		            mmOutStream.write(msgBuffer);
		        } catch (IOException e) {
		            Log.d(TAG, "...Error send: " + e.getMessage() + "...");     
		          }
		    }
		 
		    /* Call this from the main activity to shutdown the connection */
		    public void cancel() {
		        try {
		            mmSocket.close();
		        } catch (IOException e) { }
		    }
		}
	  
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.activity_main, menu);
	        return true;
	    }
	  
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {  
			 Intent intent = new Intent();
		     intent.setClass(MainActivity.this, SetPreferenceActivity.class);
		     startActivityForResult(intent, 0); 
		  
		     return true;
		 }  
	  
	private void loadPref(){
	    	SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);  
	    	
	    	address = mySharedPreferences.getString("pref_MAC_address", address);
	    	//Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
	}
}