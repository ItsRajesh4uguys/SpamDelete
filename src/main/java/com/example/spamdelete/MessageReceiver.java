package com.example.spamdelete;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        abortBroadcast();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0; i<pdus.length; i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String message = "Sender : " + smsMessage.getDisplayOriginatingAddress()
                    + "Email From: " + smsMessage.getEmailFrom()
                    + "Emal Body: " + smsMessage.getEmailBody()
                    + "Display message body: " + smsMessage.getDisplayMessageBody()
                    + "Time in millisecond: " + smsMessage.getTimestampMillis()
                    + "Message: " + smsMessage.getMessageBody();
            mListener.messageReceived(message);
           // deleteMessage(context,smsMessage);

            String numberFilter = "address='"+ "+918667567743" + "'";
            String messageid = null;
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    null, numberFilter, null, null);

            if (cursor.moveToFirst()) {
                messageid = cursor.getString(0);
            }
            context.getContentResolver().delete(Uri.parse("content://sms/" + messageid), null, null);
        }
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }

    private int deleteMessage(Context context, SmsMessage msg) {
        Uri deleteUri = Uri.parse("content://sms");
        int count = 0;
        @SuppressLint("Recycle")
        Cursor c = context.getContentResolver().query(deleteUri, null, null, null, null);

        while (c.moveToNext()) {
            try {
                // Delete the SMS
                String pid = c.getString(0); // Get id;
                String uri = "content://sms/" + pid;
                count = context.getContentResolver().delete(Uri.parse(uri),
                        null, null);
            } catch (Exception e) {
            }
        }
        return count;
    }
}