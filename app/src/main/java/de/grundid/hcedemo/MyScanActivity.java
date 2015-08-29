package de.grundid.hcedemo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyScanActivity extends Activity{

	private static final String MY_CARDIO_APP_TOKEN = "accd15094aca4fea91167f831469fdd7";

	final String TAG = getClass().getName();
	
	private Button scanButton;
	private TextView resultTextView;

	private int MY_SCAN_REQUEST_CODE = 100; 

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_card);
		
        resultTextView = (TextView)findViewById(R.id.TextView);
		scanButton = (Button)findViewById(R.id.scanbtn);
	
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (CardIOActivity.canReadCardWithCamera(this)) {
			scanButton.setText("Scan a credit card");
		}
		else {
			scanButton.setText("Enter credit card information");
		}
	}

	public void onScanPress(View v) {
		
		Intent scanIntent = new Intent(this, CardIOActivity.class);

		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, MY_CARDIO_APP_TOKEN);

		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);

		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false);

		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String resultStr;
		if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
			CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

			resultStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

			if (scanResult.isExpiryValid()) {
				resultStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n"; 
			}

			if (scanResult.cvv != null) { 
				// Never log or display a CVV
				resultStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
			}

			if (scanResult.postalCode != null) {
				resultStr += "Postal Code: " + scanResult.postalCode + "\n";
			}
		}
		else {
			resultStr = "Scan was canceled.";
		}
		
		resultTextView.setText(resultStr);
		
	}
	
	
}

