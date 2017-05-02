package br.com.costa.agenda.task;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.costa.agenda.clients.WebClient;
import br.com.costa.agenda.converter.StudentConverter;
import br.com.costa.agenda.dao.StudentDAO;
import br.com.costa.agenda.model.Student;

/**
 * Created by Lauciano FA on 01/05/2017.
 */

public class SendStudentsTask extends AsyncTask {

    private Context context;
    private Dialog dialog;

    public SendStudentsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String resultado = "";

        StudentDAO studentDAO = new StudentDAO(context);
        List<Student> students = studentDAO.read();

        StudentConverter converter = new StudentConverter();
        String js = converter.converterJson(students);

        WebClient client = new WebClient();
        resultado = client.post(js);
        return resultado;
    }

    @Override
    protected void onPostExecute(Object o) {
        String response = (String) o;
        System.out.println("ENTROOOOOOOOOOOOU: "+response);
        Toast.makeText(context, "Enviando Notas "+response, Toast.LENGTH_LONG).show();
        System.out.println("ALOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOW, ENTROOOOOOOOOOOOU: "+response);
        super.onPostExecute(o);
        dialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde...", "Enviando dados dos alunos", true, true);
    }
}
