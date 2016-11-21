package com.shadowfax.apps.securesms;

import java.math.BigInteger;
import java.util.StringTokenizer;

import com.shadowfax.apps.securesms.commonmodules.PreferenceOperations;
import com.shadowfax.apps.securesms.commonmodules.RetrieveSMS;
import com.shadowfax.apps.securesms.commonmodules.SecureSMS;
import com.shadowfax.apps.securesms.rsa.RSA;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendSMS extends Activity{
	private static final int READ_PUBLIC_KEY_RESULT_CODE=101;

	private static int publicKeySize;
	private static BigInteger modulus,publicKey;
	
	private static Button select_contact_btn,request_public_key_btn,read_public_key_btn;
	private static Button encrypt_message_btn,send_message_btn, clear_message_btn;
	private static EditText original_message;
	private static TextView request_public_key_status,read_public_key_status, encrypt_message_status,read_modulus_status;
	
	
	private void init()
	{
		modulus=null;
		publicKey=null;
		select_contact_btn=(Button)findViewById(R.id.select_contact_btn);
		request_public_key_btn=(Button)findViewById(R.id.request_public_key_btn);
		read_public_key_btn=(Button)findViewById(R.id.read_public_key_btn);
		encrypt_message_btn=(Button)findViewById(R.id.encrypt_message_btn);
		send_message_btn=(Button)findViewById(R.id.send_message_btn);
		clear_message_btn=(Button)findViewById(R.id.clear_message_btn);
		
		original_message=(EditText)findViewById(R.id.original_message);
		
		request_public_key_status=(TextView)findViewById(R.id.request_public_key_status);
		read_public_key_status=(TextView)findViewById(R.id.read_public_key_status);
		encrypt_message_status=(TextView)findViewById(R.id.encrypt_message_status);
		read_modulus_status=(TextView)findViewById(R.id.read_modulus_status);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_sms);
		init();

		PreferenceOperations secure_sms_pref=new PreferenceOperations(SendSMS.this);
		publicKeySize=secure_sms_pref.getPublicKeySizePref();
		
		select_contact_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

			}
		});
		request_public_key_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				sendSMS(new SecureSMS("Please open \"Secure SMS\" App and send your signature to this contact."));
				request_public_key_status.setText("Receiver's Signature: Request Sent");
			}
		});
		read_public_key_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent retrieve_sms=new Intent(getApplicationContext(), RetrieveSMS.class);
				startActivityForResult(retrieve_sms, READ_PUBLIC_KEY_RESULT_CODE);
			}
		});
		encrypt_message_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String message=original_message.getText().toString();
				if(message.trim().equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please enter a message!", Toast.LENGTH_SHORT).show();
				}
				else if(publicKey==null || modulus==null)
				{
					Toast.makeText(getApplicationContext(), "Please read Receiver's Public Key from the messages!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					String encrypted_message;
					RSA rsa=new RSA(publicKeySize,publicKey,modulus);
					encrypted_message=rsa.getEncryptedMessage(message);
					original_message.setText(encrypted_message);
					encrypt_message_btn.setVisibility(View.GONE);
					clear_message_btn.setVisibility(View.VISIBLE);
				}
				
			}
		});
		send_message_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				sendSMS(new SecureSMS(original_message.getText().toString()));
			}
		});
		clear_message_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				original_message.setText("");
				clear_message_btn.setVisibility(View.GONE);
				encrypt_message_btn.setVisibility(View.VISIBLE);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == READ_PUBLIC_KEY_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				String sms_read_content=data.getExtras().getString("sms_content");
				setReceiverPublicKey(sms_read_content);
				//Toast.makeText(getApplicationContext(), "SMS Content:"+sms_read_content, Toast.LENGTH_LONG).show();
			}
		}
	}
	private void setReceiverPublicKey(String sms_read_content)
	{
		StringTokenizer st=new StringTokenizer(sms_read_content);
		try
		{
			
			publicKey=new BigInteger(st.nextToken());
			modulus=new BigInteger(st.nextToken());
			read_public_key_status.setText("Public Key:"+publicKey.toString());
			read_modulus_status.setText("Modulus:"+modulus.toString());
			read_public_key_btn.setEnabled(false);
			
		}
		catch(NumberFormatException e)
		{
			Toast.makeText(getApplicationContext(), "Wrong Message was selected! Couldn't read Public Key", Toast.LENGTH_LONG).show();
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "Error in Message Format. Please request the user to resend the signature!", Toast.LENGTH_LONG).show();
		}
	}
	private void sendSMS(SecureSMS sms)
	{
		Intent sendIntent=new Intent(Intent.ACTION_VIEW);
		String sms_content=null;

		sms_content=sms.getSMSContent();
		try{
			sendIntent.putExtra("sms_body",sms_content);
			sendIntent.setType("vnd.android-dir/mms-sms");
			startActivity(sendIntent);
		}	catch(Exception e)
		{
			//Toast.makeText(getApplicationContext(),"Error Sending the Message: "+e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}

