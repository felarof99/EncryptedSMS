package com.shadowfax.apps.securesms.commonmodules;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

public class PreferenceOperations {
	Context context;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	
	public PreferenceOperations(Context context)
	{
		this.context=context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = preferences.edit();
	}
	
	public void storePublicKeyPref(String public_key)
	{
		editor.putString("public_key", public_key);
		editor.commit();
	}
	public void storePrivateKeyPref(String private_key)
	{
		editor.putString("private_key", private_key);
		editor.commit();
	}
	public void storeModulusPref(String modulus)
	{
		editor.putString("modulus", modulus);
		editor.commit();
	}
	public void storePublicKeySizePref(int public_key_size)
	{
		editor.putInt("public_key_size", public_key_size);
		editor.commit();
	}
	
	public String getPublicKeyPref()
	{
		String public_key=preferences.getString("public_key", "0");
		return public_key;
	}
	public String getPrivateKeyPref()
	{
		String private_key=preferences.getString("private_key", "0");
		return private_key;
	}
	public String getModulusPref()
	{
		String modulus=preferences.getString("modulus", "0");
		return modulus;
	}
	public int getPublicKeySizePref()
	{
		int public_key_size=preferences.getInt("public_key_size", 64);
		return public_key_size;
	}

}

