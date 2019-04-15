package demo.android.com.simpson.comment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import demo.android.com.simpson.R;
import demo.android.com.simpson.comment.utils.Constants;
import demo.android.com.simpson.comment.utils.StackItem;
import demo.android.com.simpson.models.Comment;
import demo.android.com.simpson.models.Post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Stack;

public class RepliesActivity extends AppCompatActivity implements View.OnClickListener, PostsRecyclerAdapter.ListItemClickListener, CommentDialogFragment.CommentDialogListener {
    private static final String TAG = "RepliesActivity";

    public static final String ACTION_VIEW = "demo.android.com.simpson.replies.action.ACTION_VIEW";

    private Button backBtn;
    private Button closeBtn;

    private RecyclerView recyclerView;
    private PostsRecyclerAdapter adapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;

    private CommentDialogFragment commentDialog;

    private Comment selectedComment;
    private Comment parentComment;
    private boolean isShowingParent;
    private Stack<StackItem> backStack;
    private String threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replies);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        backStack = new Stack<>();

        recyclerView = findViewById(R.id.replay_list);
        backBtn = findViewById(R.id.back_btn);
        closeBtn = findViewById(R.id.close_btn);

        initViews();
        handleIntent();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                handleBackClick();
                break;

            case R.id.close_btn:
                finish();
                break;
        }
    }


    @Override
    public void onClickPost(Post post) {

    }

    @Override
    public void onClickComment(String commentId) {

    }

    @Override
    public void onClickReplies(Comment comment) {
        Log.d(TAG, "onClickReplies: " + comment);
        if(Integer.valueOf(comment.getNum_replies()) > 0) {
            if(isShowingParent) {
                backStack.push(new StackItem(parentComment, false));
                isShowingParent = false;
            } else {
                backStack.push(new StackItem(parentComment, true));
            }
            loadReplies(comment.getComment_id());
        }
        parentComment = comment;
        printStack();
    }

    @Override
    public void onClickAddComment(Comment comment, int adapterPos) {
        Log.d(TAG, "onClickAddComment: " + comment);
        selectedComment = comment;
        showAddCommentDialog(adapterPos, false);
    }

    @Override
    public void onClickShowParent(Comment comment, int adapterPos) {
        if(isShowingParent) {
            backStack.add(new StackItem(parentComment, false));
        } else {
            backStack.add(new StackItem(parentComment, true));
        }

        isShowingParent = true;
        parentComment = comment;
        printStack();

        showParentPost(comment);
    }

    private void initViews() {
        backBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if(intent.getAction() == null) return;

        switch (intent.getAction()) {
            case ACTION_VIEW:
                if(intent.hasExtra(Constants.COMMENT_EXTRA)) {
                    parentComment = intent.getParcelableExtra(Constants.COMMENT_EXTRA);
                    threadId = parentComment.getThread_id();
                    loadReplies(parentComment.getComment_id());
                }
        }
    }



    private void showRepliesList(ArrayList<Comment> comments) {
        adapter = new PostsRecyclerAdapter( comments, this, false);
        recyclerView.setAdapter(adapter);
    }

    private void loadReplies(String parentId) {
        Log.d(TAG, "loadReplies: " + parentId);

        DatabaseReference commentNode = mDatabase.getReference().child("simpsons").child("comments").child(threadId);
        commentNode.orderByChild("parent_id").equalTo(parentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comment> comments = new ArrayList<>();
                for(DataSnapshot chid : dataSnapshot.getChildren()) {
                    comments.add(chid.getValue(Comment.class));
                }
                showRepliesList(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleBackClick() {
        if (!backStack.isEmpty()) {
            StackItem stackItem = backStack.pop();
            Log.d(TAG, "handleBackClick: " + stackItem);
            if (stackItem.isShowReplaysList()) {
                loadReplies(stackItem.getComment().getComment_id());
            } else {
                showParentPost(stackItem.getComment());
            }
        } else {
            finish();
        }
    }

    private void showParentPost(Comment comment) {
        Log.d(TAG, "showParentPost: " + comment);
        DatabaseReference commentNode = mDatabase.getReference().child("simpsons").child("comments").child(threadId);
        commentNode.orderByChild("comment_id").equalTo(comment.getParent_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comment> comments = new ArrayList<>();
                for(DataSnapshot chid : dataSnapshot.getChildren()) {
                    comments.add(chid.getValue(Comment.class));
                }
                showRepliesList(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAddCommentDialog(int adapterPos, boolean isCommentedInThread) {
        commentDialog = CommentDialogFragment.getNewInstance(adapterPos, false);
        commentDialog.show(getSupportFragmentManager(), "Comment dialog");
    }


    @Override
    public void onDialogAddCommentClick(String commentStr, int adapterPos, boolean isCommentedInMainThread) {
        Log.d(TAG, "onDialogAddCommentClick: ");
        commentDialog.dismiss();
        if(isCommentedInMainThread) {
//            addNewCommentToThread(commentStr);
        } else {
            addNewComment(commentStr, adapterPos);
        }
    }

    @Override
    public void onDialogCloseClick() {
        commentDialog.dismiss();
    }

    private void printStack() {
//        for(StackItem item : backStack) {
//            Log.d(TAG, "printStack: " + item);
//        }
    }

    private void addNewComment(String commentStr, int adapterPos) {
        Log.d(TAG, "addNewComment: ");
        DatabaseReference commentNode = mDatabase.getReference().child("simpsons").child("comments").child(threadId);
        String key = commentNode.push().getKey();

        Comment comment = new Comment(key, commentStr, "1234", selectedComment.getComment_id(), selectedComment.getThread_id(), "0");

        commentNode.child(key).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: comment added successfully");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed to add comment ");
            }
        });

        //Increment number of replies in Parent comment node
        updateReplayCount(selectedComment, adapterPos);
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

    private void incrementReplyCountInList(Comment comment, int adapterPos) {
        adapter.incrementRepliesCount(comment, adapterPos);
    }

}
