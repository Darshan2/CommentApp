package demo.android.com.simpson.comment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import demo.android.com.simpson.R;

public class CommentDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String ADAPTER_POSITION = "adapter_pos";
    private static final String COMMENTED_IN_THREAD = "commented_in_thread";
    private int adapterPos;
    private boolean isCommentedInThread;

    private CommentDialogListener listener;

    private EditText commentText;
    private Button addCommentBtn;
    private Button closeBtn;

    public interface CommentDialogListener {
        void onDialogAddCommentClick(String commentStr, int adapterPos, boolean isCommentedInMainThread);
        void onDialogCloseClick();
    }

    public static CommentDialogFragment getNewInstance(int adapterPos, boolean isCommentedInThread) {
        CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ADAPTER_POSITION, adapterPos);
        args.putBoolean(COMMENTED_IN_THREAD, isCommentedInThread);
        commentDialogFragment.setArguments(args);

        return commentDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View itemView = inflater.inflate(R.layout.item_view_comment_dialog, null);
        builder.setView(itemView);

        if(getArguments() != null) {
            adapterPos = getArguments().getInt(ADAPTER_POSITION);
            isCommentedInThread = getArguments().getBoolean(COMMENTED_IN_THREAD);
        }

        initView(itemView);

        return builder.create();
    }

    private void initView(View itemView) {
        closeBtn = itemView.findViewById(R.id.close_btn);
        addCommentBtn = itemView.findViewById(R.id.addcomment_btn);
        commentText = itemView.findViewById(R.id.comment_text);

        closeBtn.setOnClickListener(this);
        addCommentBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_btn:
                listener.onDialogCloseClick();
                getDialog().dismiss();
                break;

            case R.id.addcomment_btn:
                addComment();
                break;
        }
    }

    private void addComment() {
        String commentStr = commentText.getText().toString();
        listener.onDialogAddCommentClick(commentStr, adapterPos, isCommentedInThread);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CommentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement CommentDialogListener");
        }
    }

}