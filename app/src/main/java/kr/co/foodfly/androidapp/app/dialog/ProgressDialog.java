package kr.co.foodfly.androidapp.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.widget.ProgressBar;

import kr.co.foodfly.androidapp.R;

/**
 * Created by woozam on 2016-06-29.
 */
public class ProgressDialog extends Dialog {

    public ProgressDialog(Context context) {
        super(context, R.style.ProgressDialog);
        setContentView(R.layout.dialog_progress);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                ((Animatable) progressBar.getIndeterminateDrawable()).start();
            }
        });
    }
}
