package br.com.costa.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.widget.Toast;

import br.com.costa.agenda.R;
import br.com.costa.agenda.dao.StudentDAO;

/**
 * Created by Yan on 28/04/2017.
 */

public class SMSReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context,Intent intent) {
        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus");
        byte[] pdu = (byte[]) pdus[0];
        String format = (String) intent.getSerializableExtra("format");

        SmsMessage sms = SmsMessage.createFromPdu(pdu, format);

        String telefone = sms.getDisplayOriginatingAddress();
        StudentDAO dao = new StudentDAO(context);
        String userName = dao.findStudentByPhone(telefone);

        if (userName != null) {
            Toast.makeText(context,"Chegou uma msg bolada de "+ userName, Toast.LENGTH_SHORT).show();
            MediaPlayer media = MediaPlayer.create(context, R.raw.msg);
            media.start();
        }

    }

}
