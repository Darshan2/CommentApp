package demo.android.com.simpson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import demo.android.com.simpson.comment.CommentActivity;
import demo.android.com.simpson.comment.PostsActivity;
import demo.android.com.simpson.models.Post;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private final String fireBaseEmail = "bsbhavya6@gmail.com";
    private final String fireBasePassword = "fire base";
    ProgressBar progressBar;

    private TextView remoteConfigField;
    private LinearLayout rootLayout;
    private Button signinBtn;

    private FirebaseRemoteConfig mRemoteConfig;
    private FirebaseDatabase mDatabase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String idToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onCreate: token " + idToken);

        mDatabase = FirebaseDatabase.getInstance();

        setupRemoteConfig();

        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(fireBaseEmail, fireBasePassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("signin info -->", "signInWithEmail:Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Log.w("signin info -->", "signInWithEmail:Failure", task.getException());
                        }
                    }
                });
        
        remoteConfigField = findViewById(R.id.remoteField);
        rootLayout = findViewById(R.id.rootLayout);
        signinBtn = findViewById(R.id.signin_btn);


    }

//    public void onClickSignIn(View view) {
//        fetchRemoteValues();
//    }

    private void setupRemoteConfig() {
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mRemoteConfig.setConfigSettings(configSettings);

        //Set default values
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    private void fetchRemoteValues() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                        displayData();
                    }
                });
    }

    private void displayData() {
        String message = mRemoteConfig.getString("widget_name");
        remoteConfigField.setText(message);

        String jsonString = mRemoteConfig.getString("forms");

        try {
            JSONObject forms = new JSONObject(jsonString);
            JSONArray fields = forms.getJSONArray("fields");

            for(int i=0; i<fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String fieldName = field.getString("field_name");
                String hintText = field.getString("hint_text");

                if(fieldName.equals("EditTextView")) {
                    EditText editText = new EditText(MainActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    editText.setLayoutParams(params);
                    editText.setHint(hintText);
                    rootLayout.addView(editText);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        boolean isVip = mRemoteConfig.getBoolean(Constants.IS_VIP);
        if(isVip) {
            signinBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

    }


    public void onClickSignIn(View view) {
        EditText nameET = findViewById(R.id.name_ET);
        String name = nameET.getText().toString();

        EditText passwordET = findViewById(R.id.password_ET);
        String password = passwordET.getText().toString();

        //in here it will take some time for retrieving data from fire base so addPosts progress bar here
        addProgerssbar();
        connectToFireBase(name, password);
    }


    private void connectToFireBase(final String studentName, final String password) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        DatabaseReference students = databaseReference.child("simpsons/students");

        Query studentQuery = students.orderByChild("name").equalTo(studentName);

        studentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {

                //data has Arrived
                /*data : [
                          { name="..", id="..", email="..", password=".." }
                        ]                                                       */
                removeProgressbar();
                Iterator<DataSnapshot> iterator = data.getChildren().iterator();

                while (iterator.hasNext()) {
                    Student student = iterator.next().getValue(Student.class);
                    String correctPassword = student.getPassword();

                    TextView info_TV = findViewById(R.id.info_TV);
                    if(password.equals(correctPassword)) {
                        Log.d("Student Login", "sucess");

                        //start GradeActivity
                        Intent intent = new Intent(MainActivity.this, GradeAcivity.class);
                        intent.putExtra("StudentName", student.getName());
                        intent.putExtra("id", student.getId());

                        startActivity(intent);
                    }
                    else {
                        info_TV.setText("Wrong Pass, Correct:"+correctPassword);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("errrrrrrrrrrrrorrrrrr", databaseError.getDetails());
            }
        });


        /*

        DatabaseReference bart = databaseReference.child("simpsons/students/123");

        bart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                String correctPassword = student.getPassword();

                TextView info_TV = findViewById(R.id.info_TV);
                if(password.equals(correctPassword)) {
                    info_TV.setText("Log in Successfull");
                }
                else {
                    info_TV.setText("Wrong Pass, Correct:"+correctPassword);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("errrrrrrrrrrrrorrrrrr", databaseError.getDetails());

            }
        });      */
    }


    private void addProgerssbar() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void removeProgressbar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void onClickComment(View view) {
        Intent intent = new Intent(this, PostsActivity.class);
        startActivity(intent);
    }


}
