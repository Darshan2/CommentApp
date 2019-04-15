package demo.android.com.simpson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;

public class GradeAcivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_acivity);

        Intent intent = getIntent();
        String studentName = intent.getStringExtra("StudentName");
        String id = intent.getStringExtra("id");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference gradesRefernce = databaseReference.child("simpsons/grades");

        Query query = gradesRefernce.orderByChild("student_id").equalTo(id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> gradesList = new ArrayList<>();

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                        Grades grades = iterator.next().getValue(Grades.class);
                        gradesList.add(grades.getCourse_name()+": "+grades.getGrade());
                     }


                Log.d("llllllisttt", gradesList.toString());

                ListView gradeList_LV = findViewById(R.id.grades_LV);
                ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<String>(GradeAcivity.this, android.R.layout.simple_list_item_1, gradesList);

                gradeList_LV.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("onCancelled", databaseError.getDetails());
            }
        });
    }
}

