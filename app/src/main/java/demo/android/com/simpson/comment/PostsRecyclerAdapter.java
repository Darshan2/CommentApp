package demo.android.com.simpson.comment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import demo.android.com.simpson.R;
import demo.android.com.simpson.models.Comment;
import demo.android.com.simpson.models.Post;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PostsRecyclerAdapter";

    public static final int POSTS_LIST = 1;
    public static final int COMMENTS_LIST = 2;

    private List<Post> posts;
    private List<Comment> comments;

    private ListItemClickListener listener;
    private int listType;
    private boolean hideCommentBtn;

    public interface ListItemClickListener {
        void onClickPost(Post post);
        void onClickComment(String commentId);
        void onClickReplies(Comment comment);
        void onClickAddComment(Comment comment, int adapterPos);
        void onClickShowParent(Comment comment, int adapterPos);
    }

    public PostsRecyclerAdapter(List<Post> posts, ListItemClickListener listener) {
        this.posts = posts;
        this.listener = listener;
        this.listType = POSTS_LIST;
    }

    public PostsRecyclerAdapter(List<Comment> comments, ListItemClickListener listener, boolean hideComment) {
        this.comments = comments;
        this.listener = listener;
        this.listType = COMMENTS_LIST;
        hideCommentBtn = hideComment;
    }

    private List<Post> getPostsList() {
        if(posts == null) {
            posts = new ArrayList<>();
        }
        return posts;
    }

    private List<Comment> getCommentsList() {
        if(comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(listType == POSTS_LIST) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_post, parent, false);
            return new PostsViewHolder(itemView, parent.getContext(), posts, listener);
        } else if(listType == COMMENTS_LIST) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_comment, parent, false);
            return new CommentViewHolder(itemView, parent.getContext(), comments, listener, hideCommentBtn);
        } else {
            throw new IllegalArgumentException("unsupported view type = " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  PostsViewHolder) {
            ((PostsViewHolder) holder).bindViews(posts.get(position));
        } else if(holder instanceof  CommentViewHolder) {
            ((CommentViewHolder) holder).bindViews(comments.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(listType == POSTS_LIST) {
            return getPostsList().size();
        } else if(listType == COMMENTS_LIST) {
            return getCommentsList().size();
        } else {
            return 0;
        }
    }

    static class PostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.root_view) View rootView;
        @BindView(R.id.post_id) TextView postIdView;
        @BindView(R.id.body) TextView postText;

        private Context context;
        private List<Post> posts;
        private ListItemClickListener listener;


        public PostsViewHolder(@NonNull View itemView, Context context, List<Post> posts, ListItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
            this.posts = posts;
            this.listener = listener;
        }

        public void bindViews(Post post) {
            postIdView.setText(String.format(context.getResources().getString(R.string.comment_id), post.getPost_id()));
            postText.setText(post.getPost_body());

            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.root_view:
                    listener.onClickPost(posts.get(getAdapterPosition()));
                    break;
            }
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.root_view) View rootView;

        @BindView(R.id.body) TextView postText;
        @BindView(R.id.add_comment) TextView addCommentView;
        @BindView(R.id.replay) TextView replay;
        @BindView(R.id.parentCommentId_view) TextView parentIdText;
        @BindView(R.id.commentId_view) TextView commentIdText;

        private Context context;
        private List<Comment> comments;
        private ListItemClickListener listener;
        private boolean hideComment;

        public CommentViewHolder(@NonNull View itemView, Context context, List<Comment> comments, ListItemClickListener listener, boolean hideComment) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
            this.comments = comments;
            this.listener = listener;
            this.hideComment = hideComment;

        }

        public void bindViews(Comment comment) {
//            Log.d(TAG, "bindViews: " + comment);
            if(hideComment) {
                addCommentView.setVisibility(View.GONE);
            }

            //This is comment is posted under main thread so hide parent id
            if(comment.getParent_id().equals(comment.getThread_id())) {
                parentIdText.setVisibility(View.GONE);
            }

            postText.setText(comment.getComment_body());
            replay.setText(String.format(context.getResources().getString(R.string.replies), comment.getNum_replies()));
            parentIdText.setText(String.format(context.getResources().getString(R.string.parent_id), comment.getParent_id()));
            commentIdText.setText(String.format(context.getResources().getString(R.string.comment_id), comment.getComment_id()));

            rootView.setOnClickListener(this);
            replay.setOnClickListener(this);
            addCommentView.setOnClickListener(this);
            parentIdText.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.root_view:
                    listener.onClickComment(comments.get(getAdapterPosition()).getComment_id());
                    break;

                case R.id.add_comment:
                    listener.onClickAddComment(comments.get(getAdapterPosition()), getAdapterPosition());
                    break;

                case R.id.replay:
                    listener.onClickReplies(comments.get(getAdapterPosition()));
                    break;

                case R.id.parentCommentId_view:
                    listener.onClickShowParent(comments.get(getAdapterPosition()), getAdapterPosition());
                    break;

            }
        }
    }

    public void addPosts(List<Post> newList) {
        if (newList != null && !newList.isEmpty()) {
            int oldSize = getItemCount();
            getPostsList().addAll(newList);
            Log.d(TAG, "addPosts: itemCount " + getItemCount());
            notifyItemRangeInserted(oldSize, getItemCount());
        }
    }

    public void addComments(List<Comment> newList) {
        if (newList != null && !newList.isEmpty()) {
            int oldSize = getItemCount();
            getCommentsList().addAll(newList);
            Log.d(TAG, "addPosts: itemCount " + getItemCount());
            notifyItemRangeInserted(oldSize, getItemCount());
        }
    }

    public boolean isPostsList() {
       return listType == POSTS_LIST;
    }

    public void addCommentAtTop(Comment comment) {
//        if(listType == COMMENTS_LIST) {
//            getCommentsList().add(0, comment);
//            notifyItemInserted(0);
//        }
    }

    public void incrementRepliesCount(Comment comment, int adapterPos) {
        String numRepliesStr = comment.getNum_replies();
//        int numReplies = Integer.valueOf(numRepliesStr);
//        numReplies ++;

        getCommentsList().get(adapterPos).setNum_replies(numRepliesStr);
        notifyItemChanged(adapterPos);
    }
}
