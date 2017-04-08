package br.com.costa.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Toast;

import br.com.costa.agenda.dao.StudentDAO;
import br.com.costa.agenda.model.Student;
import br.com.costa.agenda.utils.StudentsInsertUtil;

public class StudentsInsertActivity extends AppCompatActivity {


    private StudentsInsertUtil studentsInsertUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_insert);

        studentsInsertUtil = new StudentsInsertUtil(StudentsInsertActivity.this);

        Intent intent = getIntent();
        Student editStudent = (Student) intent.getSerializableExtra("student");


        if (editStudent != null){
            studentsInsertUtil.buildEditStudent(editStudent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.this_menu_insert, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.StudentsInsert_MenuOk:
                Student student = new Student();
                try {
                    StudentDAO studentDAO = new StudentDAO(StudentsInsertActivity.this);
                    student = studentsInsertUtil.buildStudentForInsert();

                    if (student.getId() != null){
                        studentDAO.update(student);
                    }else{
                        studentDAO.create(student);
                    }

                    studentDAO.close();
                    Toast.makeText(StudentsInsertActivity.this,
                            "Novo aluno " + student.getName() + " salvo!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }catch (Exception e){
                    Toast.makeText(StudentsInsertActivity.this,
                            "Erro ao salvar novo aluno. \n" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
