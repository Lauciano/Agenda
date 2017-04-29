package br.com.costa.agenda.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import br.com.costa.agenda.R;
import br.com.costa.agenda.model.Student;

/**
 * Created by eqs on 08/04/2017.
 */

public class StudentAdapter extends BaseAdapter {

    private final Context context;
    private List<Student> students;

    public StudentAdapter(Context context, List<Student> students) {
        this.students = students;
        this.context = context;
    }

    @Override
    public int getCount() { return this.students.size(); }

    @Override
    public Object getItem(int position) { return this.students.get(position); }

    @Override
    public long getItemId(int position) { return this.students.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Student student = this.students.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.students_list, parent, false);
        }

        ImageView studentPhoto  = (ImageView) view.findViewById(R.id.studentsList_imageViewPhoto);
        TextView studentName = (TextView) view.findViewById(R.id.studentsList_textViewName);
        TextView studentPhone = (TextView) view.findViewById(R.id.studentsList_textViewNumber);

        if(student.getPhotoPath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(student.getPhotoPath());
            Bitmap bitmapReduce = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            studentPhoto.setImageBitmap(bitmapReduce);
            studentPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        studentName.setText(student.getName());
        studentPhone.setText(student.getNumber());

        return view;
    }
}
