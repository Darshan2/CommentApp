package demo.android.com.simpson.comment.utils;

import demo.android.com.simpson.models.Comment;

public class StackItem {
    private Comment comment;
    private boolean showReplaysList;

    public StackItem(Comment comment, boolean showReplaysList) {
        this.comment = comment;
        this.showReplaysList = showReplaysList;
    }

    public StackItem() {
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public boolean isShowReplaysList() {
        return showReplaysList;
    }

    public void setShowReplaysList(boolean showReplaysList) {
        this.showReplaysList = showReplaysList;
    }

    @Override
    public String toString() {
        return "StackItem{" +
                "comment=" + comment +
                ", showReplaysList=" + showReplaysList +
                '}';
    }
}
