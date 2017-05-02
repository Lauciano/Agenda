package br.com.costa.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.costa.agenda.adapter.StudentAdapter;
import br.com.costa.agenda.dao.StudentDAO;
import br.com.costa.agenda.model.Student;
import br.com.costa.agenda.task.SendStudentsTask;
import br.com.costa.agenda.utils.ConstantCodes;

public class StudentsListActivity extends AppCompatActivity {

    private ListView studentListView;

    @Override
    protected void onResume() {
        super.onResume();
        buildStudentsList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);

        studentListView = (ListView) findViewById(R.id.studentsList_listViewStudents);

        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                Student student = (Student) studentListView.getItemAtPosition(position);
                Intent intentStudentInsert = new Intent(StudentsListActivity.this,
                        StudentsInsertActivity.class);
                intentStudentInsert.putExtra("student", student);
                startActivity(intentStudentInsert);
            }
        });

        Button newButton = (Button) findViewById(R.id.studentsInsert_buttonNew);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStudentInsert = new Intent(StudentsListActivity.this,
                        StudentsInsertActivity.class);
                startActivity(intentStudentInsert);
            }
        });

        registerForContextMenu(studentListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {

        MenuItem callNumber = menu.add("Telefonar");
        final MenuItem callEditedNumber = menu.add("Preparar para telefonar");
        final MenuItem goToSite = menu.add("Visitar site");
        final MenuItem sendSMS = menu.add("Mandar SMS");
        final MenuItem showAddress = menu.add("Ver endereço");
        MenuItem deleteMenuItem = menu.add("Delete");

        AdapterView.AdapterContextMenuInfo adapterMenuInfo =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Student student =
                (Student) studentListView.getItemAtPosition(adapterMenuInfo.position);
        final StudentDAO studentDAO = new StudentDAO(StudentsListActivity.this);

        buildCallNumber(callNumber, student, studentDAO);
        buildCallEditedNumber(callEditedNumber, student, studentDAO);
        buildGoToSite(goToSite, student, studentDAO);
        buildSendSMS(sendSMS, student, studentDAO);
        buildShowAddress(showAddress, student, studentDAO);

        buildDelete((AdapterView.AdapterContextMenuInfo) menuInfo, deleteMenuItem);

        studentDAO.close();

    }

    private void buildCallNumber(MenuItem callNumber, final Student student, final StudentDAO studentDAO) {
        callNumber.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(ActivityCompat.checkSelfPermission(StudentsListActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(StudentsListActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            ConstantCodes.CALL_PHONE_PERMISSION);
                } else {
                    String number = studentDAO.findStudentAttributeById(student.getId(), "number");
                    Intent callNumberIntent = new Intent(Intent.ACTION_CALL);
                    if (number.startsWith("+")) {
                        callNumberIntent.setData(Uri.parse("tel:" + number));
                    } else if (number.length() == 11 || number.length() == 10) {
                        callNumberIntent.setData(Uri.parse("tel:+55" + number));
                    } else {
                        callNumberIntent.setData(Uri.parse("tel:+5583" + number));
                    }
                    startActivity(callNumberIntent);
                }
                return false;
            }
        });
    }

    private void buildCallEditedNumber(MenuItem callEditedNumber, Student student, StudentDAO studentDAO) {
        String number = studentDAO.findStudentAttributeById(student.getId(), "number");
        Intent callEditedNumberIntent = new Intent(Intent.ACTION_VIEW);
        if (number.startsWith("+")) {
            callEditedNumberIntent.setData(Uri.parse("tel:" + number));
        } else if (number.length() == 11 || number.length() == 10) {
            callEditedNumberIntent.setData(Uri.parse("tel:+55" + number));
        } else {
            callEditedNumberIntent.setData(Uri.parse("tel:+5583" + number));
        }
        callEditedNumber.setIntent(callEditedNumberIntent);
    }

    private void buildShowAddress(MenuItem showAddress, Student student, StudentDAO studentDAO) {
        String address = studentDAO.findStudentAttributeById(student.getId(), "address");
        Intent viewAddressIntent = new Intent(Intent.ACTION_VIEW);
        viewAddressIntent.setData(Uri.parse("geo:0,0?q=" + address));
        showAddress.setIntent(viewAddressIntent);
    }

    private void buildSendSMS(MenuItem sendSMS, Student student, StudentDAO studentDAO) {
        String number = studentDAO.findStudentAttributeById(student.getId(), "number");
        Intent sendSMSIntent = new Intent(Intent.ACTION_VIEW);
        if (number.startsWith("+")) {
            sendSMSIntent.setData(Uri.parse("sms:" + number));
        } else if (number.length() == 11 || number.length() == 10) {
            sendSMSIntent.setData(Uri.parse("sms:+55" + number));
        } else {
            sendSMSIntent.setData(Uri.parse("sms:+5583" + number));
        }
        sendSMS.setIntent(sendSMSIntent);
    }

    private void buildDelete(final AdapterView.AdapterContextMenuInfo menuInfo, MenuItem deleteMenuItem) {
        deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo adapterMenuInfo =
                        menuInfo;
                Student student =
                        (Student) studentListView.getItemAtPosition(adapterMenuInfo.position);

                StudentDAO studentDAO = new StudentDAO(StudentsListActivity.this);
                studentDAO.delete(student.getId());
                studentDAO.close();

                buildStudentsList();

                Toast.makeText(StudentsListActivity.this,
                        "Aluno " + student.getName() + " removido!",
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }

    private void buildGoToSite(MenuItem goToSite, Student student, StudentDAO studentDAO) {
        String site = studentDAO.findStudentAttributeById(student.getId(), "site");
        Intent goToSiteIntent = new Intent(Intent.ACTION_VIEW);
        if (site.startsWith("http://")) {
            goToSiteIntent.setData(Uri.parse(site));
        } else if (site.startsWith("www.")) {
            goToSiteIntent.setData(Uri.parse("http://" + site));
        } else {
            goToSiteIntent.setData(Uri.parse("http://www." + site));
        }
        goToSite.setIntent(goToSiteIntent);
    }

    private void buildStudentsList() {

        StudentDAO studentDAO = new StudentDAO(StudentsListActivity.this);
        List<Student> studentList = studentDAO.read();
        studentDAO.close();

        StudentAdapter studentListViewAdapter = new StudentAdapter(this, studentList);
        studentListView.setAdapter(studentListViewAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == ConstantCodes.CALL_PHONE_PERMISSION){
            // Insira algum tratamento a ser realizado na hora da requisição, se houver
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.this_menu_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SendStudentsTask  send = (SendStudentsTask) new SendStudentsTask(this).execute();

        return super.onOptionsItemSelected(item);
    }

}