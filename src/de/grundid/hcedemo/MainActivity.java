package de.grundid.hcedemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    private static TextView activityLog;
    private static final StringBuilder logCache = new StringBuilder();
    private static final DateFormat timeStampFmt = new SimpleDateFormat("HH:mm:ss.SSS  ");

    private static final String TAG = MainActivity.class.getSimpleName();
    
     
    private static void log(String tag, Object... messageFragments) {
        StringBuilder message = new StringBuilder();
        for(Object fragment : messageFragments) {
            message.append(fragment.toString());
        }
        String text = message.toString();

        logCache.append(timeStampFmt.format(new Date())).append(tag).append(" ").append(text).append('\n');

        // any logging before activityLog is initialized does not get
        // cleared and will be printed to the screen later
        if (activityLog != null) {
            activityLog.append(logCache);
            logCache.setLength(0);
        }

        
    }


    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            log(data.getString("tag"), (Object[])data.getStringArray("messageFragments"));
            super.handleMessage(msg);
        }
    };


    public static void sendLog(String tag, String ...messageFragments) {
        Message message = Message.obtain();
        Bundle data = new Bundle();
        data.putString("tag", tag);
        data.putStringArray("messageFragments", messageFragments);
        message.setData(data);
        handler.sendMessage(message);
    }

    Button carddetails;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
        activityLog = (TextView) findViewById(R.id.activity_log);

        CardEmulation cardEmulationManager = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(this));
        ComponentName paymentServiceComponent =
                new ComponentName(getApplicationContext(), MyHostApduService.class.getCanonicalName());

        if (!cardEmulationManager.isDefaultServiceForCategory(paymentServiceComponent, CardEmulation.CATEGORY_PAYMENT)) {
    		Intent intent = new Intent(CardEmulation.ACTION_CHANGE_DEFAULT);
    		intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);
    		intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, paymentServiceComponent);
    		startActivityForResult(intent, 0);
            log(TAG, "Card Tap and pay is the default payment app");
        } else {
            log(TAG, "Card Tap and pay is the default NFC payment app");
        }
        carddetails=(Button)findViewById(R.id.carddetailsbtn);
        carddetails.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    				    			 
    			Intent cardscan=new Intent(getApplicationContext(),MyScanActivity.class);
    			startActivity(cardscan);
    				
    			}
    		});
	}

   

}
