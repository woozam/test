package kr.co.foodfly.androidapp.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.dialog.ProgressDialog;

/**
 *
 * Created by woozam on 2016-06-22.
 */
public class BaseActivity extends AppCompatActivity {

    private MaterialDialog mProgressDialog;
    private ProgressDialog mProgressDialog2;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setToolbar();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        setToolbar();
    }

    private void setToolbar() {
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar((Toolbar) toolbar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProgressDialog(String title, String content) {
        dismissProgressDialog();
        mProgressDialog = new MaterialDialog.Builder(this).theme(Theme.LIGHT).widgetColorRes(R.color.colorAccent).progress(true, 0, false).canceledOnTouchOutside(false).title(title).content(content).show();
    }

    public void showProgressDialog(int title, int content) {
        dismissProgressDialog();
        mProgressDialog = new MaterialDialog.Builder(this).theme(Theme.LIGHT).widgetColorRes(R.color.colorAccent).progress(true, 0, false).canceledOnTouchOutside(false).title(title).content(content).show();
    }

    public void showProgressDialog() {
        dismissProgressDialog();
        if (mProgressDialog2 == null) {
            mProgressDialog2 = new ProgressDialog(this);
        }
        mProgressDialog2.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mProgressDialog2 != null && mProgressDialog2.isShowing()) {
            mProgressDialog2.dismiss();
        }
    }
}