package demo.android.com.simpson.comment.utils;

import android.content.Context;
import android.content.Intent;

import demo.android.com.simpson.comment.CommentActivity;
import demo.android.com.simpson.comment.RepliesActivity;
import demo.android.com.simpson.models.Comment;
import demo.android.com.simpson.models.Post;

public class IntentUtils {

    public static void startRepliesActivity(Context context, Comment comment) {
        Intent intent = new Intent(context, RepliesActivity.class);
        intent.setAction(RepliesActivity.ACTION_VIEW);
        intent.putExtra(Constants.COMMENT_EXTRA, comment);
        context.startActivity(intent);
    }

    public static void startCommentActivity(Context context, Post post) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.setAction(CommentActivity.ACTION_VIEW);
        intent.putExtra(Constants.POST_EXTRA, post);
        context.startActivity(intent);
    }
}
