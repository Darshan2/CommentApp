package demo.android.com.simpson.comment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import demo.android.com.simpson.R;
import demo.android.com.simpson.comment.utils.Constants;
import demo.android.com.simpson.comment.utils.IntentUtils;
import demo.android.com.simpson.models.Comment;
import demo.android.com.simpson.models.Post;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class CommentActivity extends AppCompatActivity implements View.OnClickListener, PostsRecyclerAdapter.ListItemClickListener, CommentDialogFragment.CommentDialogListener {
    private static final String TAG = "CommentActivity";

    public static final String ACTION_VIEW = "demo.android.com.simpson.comment.action.ACTION_VIEW";


    private RecyclerView recyclerView;
    private FloatingActionButton addCommentBtn;

    private PostsRecyclerAdapter postsRecyclerAdapter;
    private ArrayList<Post> postsList;

    private Comment currentParentComment;
    private String threadId;

    private FirebaseDatabase mDatabase;

    private CommentDialogFragment commentDialog;

    private boolean isPauseCalled;


    @Override
    protected void onResume() {
        isPauseCalled = false;
        if(threadId != null) loadReplies(threadId);
        super.onResume();
    }

    @Override
    protected void onPause() {
        isPauseCalled = true;
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mDatabase = FirebaseDatabase.getInstance();

        recyclerView = findViewById(R.id.posts_list);
        addCommentBtn = findViewById(R.id.add_comment);

        postsList = new ArrayList<>();

        initViews();
        handleIntent();

    }

    private void initViews() {
        addCommentBtn.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        postsRecyclerAdapter = new PostsRecyclerAdapter(postsList, this);
//        recyclerView.setAdapter(postsRecyclerAdapter);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if(intent.getAction() == null) return;

        switch (intent.getAction()) {
            case ACTION_VIEW:
                if(intent.hasExtra(Constants.POST_EXTRA)) {
                    Post post = intent.getParcelableExtra(Constants.POST_EXTRA);
                    threadId = post.getPost_id();
//                    loadReplies(threadId);
                }
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_comment:
                showAddCommentDialog(-1, true);
                break;
        }
    }

    @Override
    public void onClickPost(Post post) {
//        threadId = post.getPost_id();
//        loadReplies(post.getPost_id());
//        previousParentCommentId = postId;
//        currentParentCommentId = postId;
    }

    @Override
    public void onClickComment(String commentId) {
//        currentParentCommentId = commentId;
//        Log.d(TAG, "onClickComment: currentParentId " + currentParentCommentId);
//        if(!previousParentCommentId.equals("")) {
//            Log.d(TAG, "onClickComment: push " + previousParentCommentId);
//            commentStack.push(previousParentCommentId);
//        }
//        previousParentCommentId = commentId;
//        loadReplies(commentId);
    }

    @Override
    public void onClickReplies(Comment comment) {
        if(!isPauseCalled && Integer.valueOf(comment.getNum_replies()) > 0) IntentUtils.startRepliesActivity(this, comment);
    }

    @Override
    public void onClickAddComment(Comment comment, int adapterPos) {
        Log.d(TAG, "onClickAddComment: " + comment);
        currentParentComment = comment;
        showAddCommentDialog(adapterPos, false);
    }

    @Override
    public void onClickShowParent(Comment comment, int adapterPos) {

    }

    @Override
    public void onDialogAddCommentClick(String commentStr, int adapterPos, boolean isCommentedInThread) {
        Log.d(TAG, "onDialogAddCommentClick: ");
        commentDialog.dismiss();
        if(isCommentedInThread) {
            addNewCommentToThread(commentStr);
        } else {
            addNewComment(commentStr, adapterPos);
        }
    }

    @Override
    public void onDialogCloseClick() {
        Log.d(TAG, "onDialogCloseClick: ");
        commentDialog.dismiss();
    }

    //Show all the comment and replies under this thread
    private void loadReplies(String parentId) {
        Log.d(TAG, "loadReplies: " + parentId);

        DatabaseReference commentNode = mDatabase.getReference().child("simpsons").child("comments").child(parentId);
//        commentNode.orderByChild("parent_id").equalTo(parentId).addListenerForSingleValueEvent(new ValueEventListener()
        commentNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comment> comments = new ArrayList<>();
                for(DataSnapshot chid : dataSnapshot.getChildren()) {
                    comments.add(chid.getValue(Comment.class));
                }
                showCommentList(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showCommentList(ArrayList<Comment> comments) {
        postsRecyclerAdapter = new PostsRecyclerAdapter( comments, this, false);
        recyclerView.setAdapter(postsRecyclerAdapter);
    }

    private void showPostsList(ArrayList<Post> posts) {
        postsRecyclerAdapter = new PostsRecyclerAdapter(posts, this);
        recyclerView.setAdapter(postsRecyclerAdapter);
    }

    private void addNewComment(String commentStr, int adapterPos) {
        Log.d(TAG, "addNewComment: ");
        DatabaseReference commentNode = mDatabase.getReference().child("simpsons").child("comments").child(threadId);
        String key = commentNode.push().getKey();

        Comment comment = new Comment(key, commentStr, "1234", currentParentComment.getComment_id(), currentParentComment.getThread_id(), "0");

        commentNode.child(key).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: comment added successfully");
                loadReplies(threadId);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed to add comment ");
            }
        });

        //Increment number of replies in Parent comment node
        updateReplayCount(currentParentComment, adapterPos);
    }

    private void updateReplayCount(Comment comment, int adapterPos) {
//        incrementReplyCountInList(comment, adapterPos);

        Log.d(TAG, "updateReplayCount: " + comment);
        DatabaseReference commentNode = mDatabase.getReference().child("simpsons").child("comments").child(comment.getThread_id());

        String numRepliesStr = comment.getNum_replies();
        if(numRepliesStr.equals("")) {
            numRepliesStr = "0";
        }
        int numReplies = Integer.valueOf(numRepliesStr);
        numReplies++;

        comment.setNum_replies(String.valueOf(numReplies));
        incrementReplyCountInList(comment, adapterPos);
        Log.d(TAG, "updateReplayCount: updated comment " + comment);

        commentNode.child(comment.getComment_id()).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: replay count upadated successfully");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed to update replies count ");
            }
        });

    }

    private void addNewCommentToThread(String commentStr) {
        Log.d(TAG, "addNewComment: ");
        DatabaseReference commentNode = mDatabase.getReference().child("simpsons").child("comments").child(threadId);
        String key = commentNode.push().getKey();

        Comment comment = new Comment(key, commentStr, "1234", threadId, threadId, "0");

        commentNode.child(key).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: comment added successfully");
                loadReplies(threadId);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed to add comment ");
            }
        });

    }


    private void incrementReplyCountInList(Comment comment, int adapterPos) {
        postsRecyclerAdapter.incrementRepliesCount(comment, adapterPos);
    }

    private void showAddCommentDialog(int adapterPos, boolean isCommentedInThread) {
        commentDialog = CommentDialogFragment.getNewInstance(adapterPos, isCommentedInThread);
        commentDialog.show(getSupportFragmentManager(), "Comment dialog");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        super.onBackPressed();

//        if(postsRecyclerAdapter.isPostsList()) {
//            finish();
//        } else {
//            loadPosts();
//        }
    }





}
