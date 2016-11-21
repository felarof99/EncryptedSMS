package com.shadowfax.apps.securesms;

import com.shadowfax.apps.securesms.commonmodules.PreferenceOperations;
import com.shadowfax.apps.securesms.commonmodules.SecureSMS;
import com.shadowfax.apps.securesms.rsa.RSA;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {
	public static final int send_sms_req_code=100;
	public static final int read_sms_req_code=114;
	public static final int settings_req_code=200;
	private static final int SETTINGS = 4017;
	
	static Button send_sms_btn,read_sms_btn,share_signature_btn,generate_signature_btn;
	
    private void init()
    {
    	share_signature_btn=(Button)findViewById(R.id.share_signature_btn);
    	generate_signature_btn=(Button)findViewById(R.id.generate_signature_btn);
    	send_sms_btn=(Button)findViewById(R.id.send_sms_btn);
    	read_sms_btn=(Button)findViewById(R.id.read_sms_btn);    	
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        init();
        generate_signature_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				PreferenceOperations secure_sms_pref=new PreferenceOperations(Main.this);
				
				int public_key_size=secure_sms_pref.getPublicKeySizePref();
				
				RSA rsa=new RSA(public_key_size);				
				
				String public_key=rsa.getPublicKey().toString();				
				String private_key=rsa.getPrivateKey().toString();
				String modulus=rsa.getModulus().toString();
				
				Toast.makeText(getApplicationContext(),"Public Key Size:"+public_key_size+"\n\n"+"Public Key:"+public_key, Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), "Modulus:"+public_key, Toast.LENGTH_SHORT).show();
				
				secure_sms_pref.storePublicKeyPref(public_key);
				secure_sms_pref.storePrivateKeyPref(private_key);
				secure_sms_pref.storeModulusPref(modulus);
				
				Toast.makeText(getApplicationContext(), "New Signature Generated!", Toast.LENGTH_SHORT).show();
			}
		});
        
        share_signature_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {				
				PreferenceOperations secure_sms_pref=new PreferenceOperations(Main.this);
				
				String public_key=secure_sms_pref.getPublicKeyPref();
				String modulus=secure_sms_pref.getModulusPref();
				
				String signature=public_key+" "+modulus;
				sendSMS(new SecureSMS(signature));
			}
		});
        
        send_sms_btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				Intent send_sms = new Intent(Main.this, SendSMS.class);
				startActivityForResult(send_sms, send_sms_req_code);				
			}
		});
        read_sms_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent read_sms = new Intent(Main.this, ReadSMS.class);
				startActivityForResult(read_sms, read_sms_req_code);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(menu.NONE, SETTINGS, menu.NONE, "Settings");
		return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case SETTINGS:
			Intent send_sms = new Intent(Main.this, Settings.class);
			startActivityForResult(send_sms, settings_req_code);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
