package com.shadowfax.apps.securesms;

import java.math.BigInteger;

import com.shadowfax.apps.securesms.commonmodules.PreferenceOperations;
import com.shadowfax.apps.securesms.commonmodules.RetrieveSMS;
import com.shadowfax.apps.securesms.rsa.RSA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReadSMS extends Activity{
	private static final int READ_ENCRYPTED_MESSAGE_RESULT_CODE=101;

	private static Button read_encrypted_message_btn;

	private static TextView decrypted_message;

	private static int publicKeySize;
	
	private void init()
	{
		read_encrypted_message_btn=(Button)findViewById(R.id.read_encrypted_message_btn);

		decrypted_message=(TextView)findViewById(R.id.decrypted_message);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_sms);

		init();

		PreferenceOperations secure_sms_pref=new PreferenceOperations(ReadSMS.this);
		publicKeySize=secure_sms_pref.getPublicKeySizePref();
		
		read_encrypted_message_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent retrieve_sms=new Intent(getApplicationContext(), RetrieveSMS.class);
				startActivityForResult(retrieve_sms, READ_ENCRYPTED_MESSAGE_RESULT_CODE);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == READ_ENCRYPTED_MESSAGE_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				String sms_read_content=data.getExtras().getString("sms_content");
				setDecryptedMessage(sms_read_content);
				//Toast.makeText(getApplicationContext(), "SMS Content:"+sms_read_content, Toast.LENGTH_LONG).show();
			}
		}
	}

	private void setDecryptedMessage(String sms_content)
	{
		String encrypted_message=sms_content;		
		PreferenceOperations secure_sms_pref=new PreferenceOperations(ReadSMS.this);
		BigInteger private_key=new BigInteger(secure_sms_pref.getPrivateKeyPref());
		BigInteger modulus=new BigInteger(secure_sms_pref.getModulusPref());
		BigInteger public_key=new BigInteger(secure_sms_pref.getPublicKeyPref());
		RSA rsa=new RSA(publicKeySize,public_key,private_key, modulus);

		try{
			String message=rsa.getDecryptedMessage(encrypted_message);
			decrypted_message.setText(message);
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Wrong Message Selected! Please make sure you select the message ecrypted by Secure SMS", Toast.LENGTH_LONG).show();
		}


		//Toast.makeText(getApplicationContext(), "Private Key:"+secure_sms_pref.getPrivateKeyPref(),Toast.LENGTH_SHORT).show();
	}
}
