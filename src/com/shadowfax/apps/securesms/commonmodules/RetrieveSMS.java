package com.shadowfax.apps.securesms.commonmodules;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RetrieveSMS extends Activity {
	ListView retrieve_sms_list;
	TextView retrieve_sms_list_header;
	int no_messages_in_inbox;

	private Cursor mycursor;

	private void init()
	{
		retrieve_sms_list=(ListView)findViewById(com.shadowfax.apps.securesms.R.id.retrieve_sms_list);
		retrieve_sms_list_header=(TextView)findViewById(com.shadowfax.apps.securesms.R.id.retrieve_sms_list_header);
	}
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);	
		setContentView(com.shadowfax.apps.securesms.R.layout.retrieve_sms_list);
		init();

		retrieve_sms_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				TextView descrip=(TextView)v.findViewById(com.shadowfax.apps.securesms.R.id.retrieve_sms_list_row_descrip);
				String sms_content=(String) descrip.getText();
				Intent sms_content_intent=new Intent();
				sms_content_intent.putExtra("sms_content",sms_content);
				setResult(RESULT_OK,sms_content_intent);
				//Toast.makeText(getApplicationContext(), "Result Set", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		filldata();

	}
	protected void onResume()
	{
		super.onResume();		
		//filldata();	//Check out if you really need to perform this costly operation in onResume().
		//Because in LocationStamp 1.0 the back was working without this costly operation.
	}
	private void filldata() 
	{
		fillRetrieveStampList();

	}
	private void fillRetrieveStampList()
	{

		Uri uriSMSURI = Uri.parse("content://sms/inbox");
		Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
		/*Toast.makeText(getApplicationContext(),String.valueOf(cur.getCount()), Toast.LENGTH_SHORT).show();
		 */        
		no_messages_in_inbox=cur.getCount();
		retrieve_sms_list_header.setText(String.valueOf(no_messages_in_inbox)+"/"+String.valueOf(no_messages_in_inbox));
		if(cur!=null && cur.getCount()!=0)
		{
			cur.moveToFirst();
		}	
		mycursor=cur;
		startManagingCursor(cur);

		String[] from = new String[] {"address","body","date"};

		int[] to = new int[] {com.shadowfax.apps.securesms.R.id.retrieve_sms_list_row_title,
				com.shadowfax.apps.securesms.R.id.retrieve_sms_list_row_descrip, com.shadowfax.apps.securesms.R.id.retrieve_sms_list_row_date };
		retrieve_sms_list.setAdapter(new RetrieveSMSListAdapter(from, to, cur));

	}
	class RetrieveSMSListAdapter extends SimpleCursorAdapter
	{
		TextView title,descrip,date;
		Cursor cursor;
		public RetrieveSMSListAdapter(String from[],int to[],Cursor cursor) {
			super(RetrieveSMS.this,com.shadowfax.apps.securesms.R.layout.retrieve_sms_list_row, cursor, from, to);
			this.cursor=cursor;
		}
		public View getView(int position,View convertView,ViewGroup parent)
		{
			View row=null;

			row=convertView;
			if(row==null)
			{
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(com.shadowfax.apps.securesms.R.layout.retrieve_sms_list_row, parent, false);
			}
			title=(TextView)row.findViewById(com.shadowfax.apps.securesms.R.id.retrieve_sms_list_row_title);
			descrip=(TextView)row.findViewById(com.shadowfax.apps.securesms.R.id.retrieve_sms_list_row_descrip);
			date=(TextView)row.findViewById(com.shadowfax.apps.securesms.R.id.retrieve_sms_list_row_date);

			cursor.moveToPosition(position);

			int date_index=cursor.getColumnIndex("date");
			long date_value=cursor.getLong(date_index);
			Date dateFromSms = new Date(date_value);
			Format formatter = new SimpleDateFormat("HH:mm:ss, yyyy-MM-dd");
			String date_formatted = formatter.format(dateFromSms);
			date.setText(date_formatted);

			String address = cursor.getString(cursor.getColumnIndex("address"));
			title.setText(address);

			String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
			descrip.setText(body);

			return(row);
		}
	}

}
