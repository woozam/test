package kr.co.foodfly.androidapp.app.activity.etc;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.toolbox.NetworkImageView;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.network.VolleySingleton;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

/**
 * Created by woody on 2016-05-13.
 */
public class ImageViewerActivity extends BaseActivity implements OnClickListener, OnPhotoTapListener {

    public static final int REQ_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = permission.WRITE_EXTERNAL_STORAGE.hashCode() & 0x000000ff;
    public static final String EXTRA_PATH = "extra_path";

    private ViewGroup mContainer;
    private NetworkImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private boolean mCanSave = false;
    private View mSaveLayout;
    private View mSave;

    public static void createInstance(Context context, String path) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        context.startActivity(intent);
    }

    public static void createInstance(Fragment fragment, String path) {
        Intent intent = new Intent(fragment.getContext(), ImageViewerActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        fragment.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        Intent intent = getIntent();
        String path = intent.getStringExtra(EXTRA_PATH);

        mContainer = (ViewGroup) findViewById(R.id.image_view_root);
        mSaveLayout = findViewById(R.id.image_save_layout);
        mSaveLayout.setVisibility(View.INVISIBLE);
        mSave = findViewById(R.id.image_save);
        mSave.setOnClickListener(this);
        mImageView = new NetworkImageView(this) {
            @Override
            public void setImageBitmap(Bitmap bm) {
                super.setImageBitmap(bm);
                mAttacher.update();
                mCanSave = true;
                showSaveButton();
                dismissProgressDialog();
            }

            @Override
            public void setImageResource(int resId) {
                super.setImageResource(resId);
                mAttacher.update();
                mCanSave = true;
                showSaveButton();
                dismissProgressDialog();
            }

            @Override
            public void setImageDrawable(Drawable drawable) {
                super.setImageDrawable(drawable);
                mAttacher.update();
                mCanSave = true;
                showSaveButton();
                dismissProgressDialog();
            }
        };
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnPhotoTapListener(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mImageView, 0, params);
        mImageView.setImageUrl(path, VolleySingleton.getInstance(this).getImageLoader());
        showProgressDialog();
    }

    private void showSaveButton() {
        if (mCanSave) {
            //            AnimationUtils.showSlideBottomInAnimation(this, mSaveLayout);
        }
    }

    private void hideSaveButton() {
        //        AnimationUtils.hideSlideBottomOutAnimation(this, mSaveLayout);
    }

    private void saveImage() {
        //        if (PermissionChecker.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        //            ActivityCompat.requestPermissions(this, new String[]{permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
        //        } else {
        //            try {
        //                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        //                new InsertImageTask(new Date().toString(), "TEXTORY IMAGE", bitmap) {
        //                    @Override
        //                    protected void onPreExecute() {
        //                        super.onPreExecute();
        //                        showProgressDialog("저장중", "잠시만 기다려주세요.");
        //                    }
        //
        //                    @Override
        //                    protected void onPostExecute(Void aVoid) {
        //                        super.onPostExecute(aVoid);
        //                        dismissProgressDialog();
        //                    }
        //                }.execute();
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mSave) {
            saveImage();
        }
    }

    @Override
    public void onPhotoTap(View view, float v, float v1) {
        if (mSaveLayout.getVisibility() == View.INVISIBLE) {
            showSaveButton();
        } else {
            hideSaveButton();
        }
    }

    @Override
    public void onOutsidePhotoTap() {
        if (mSaveLayout.getVisibility() == View.INVISIBLE) {
            showSaveButton();
        } else {
            hideSaveButton();
        }
    }
}