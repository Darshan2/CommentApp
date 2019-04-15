package demo.android.com.simpson.comment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import demo.android.com.simpson.R;
import demo.android.com.simpson.comment.utils.IntentUtils;
import demo.android.com.simpson.models.Comment;
import demo.android.com.simpson.models.Post;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Stack;

public class PostsActivity extends AppCompatActivity implements View.OnClickListener, CommentDialogFragment.CommentDialogListener, PostsRecyclerAdapter.ListItemClickListener{

    private static final String TAG = "PostsActivity";

    private FloatingActionButton addCommentBtn;
    private RecyclerView recyclerView;
    private PostsRecyclerAdapter postsRecyclerAdapter;

    private FirebaseDatabase mDatabase;

    private CommentDialogFragment commentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        mDatabase = FirebaseDatabase.getInstance();

        recyclerView = findViewById(R.id.posts_list);
        addCommentBtn = findViewById(R.id.add_comment);


        initViews();
        loadPosts();

    }

    private void initViews() {
        addCommentBtn.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void loadPosts() {
        Log.d(TAG, "loadPosts: ");

        DatabaseReference postsNode = mDatabase.getReference().child("simpsons").child("posts");

        postsNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Post> posts = new ArrayList<>();
                if(dataSnapshot.hasChildren()) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        posts.add(snapshot.getValue(Post.class));
                    }
                }
                showPostsList(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void showPostsList(ArrayList<Post> posts) {
        postsRecyclerAdapter = new PostsRecyclerAdapter(posts, this);
        recyclerView.setAdapter(postsRecyclerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_comment:
                showAddCommentDialog(-1, false);
                break;
        }
    }

    private void showAddCommentDialog(int adapterPos, boolean isCommentedInThread) {
        commentDialog = CommentDialogFragment.getNewInstance(adapterPos, isCommentedInThread);
        commentDialog.show(getSupportFragmentManager(), "Comment dialog");
    }


    private void addPost(String postStr) {
        DatabaseReference postsNode = mDatabase.getReference().child("simpsons").child("posts");
        String key = postsNode.push().getKey();

        Post post = new Post(key, postStr);

        postsNode.child(key).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: comment added successfully");
                loadPosts();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed to add comment ");
            }
        });
    }

    @Override
    public void onDialogAddCommentClick(String commentStr, int adapterPos, boolean isCommentedInMainThread) {
        commentDialog.dismiss();
        addPost(commentStr);
    }

    @Override
    public void onDialogCloseClick() {
        commentDialog.dismiss();
    }

    @Override
    public void onClickPost(Post post) {
        IntentUtils.startCommentActivity(this, post);
    }

    @Override
    public void onClickComment(String commentId) {

    }

    @Override
    public void onClickReplies(Comment comment) {

    }

    @Override
    public void onClickAddComment(Comment comment, int adapterPos) {

    }

    @Override
    public void onClickShowParent(Comment comment, int adapterPos) {

    }
}
