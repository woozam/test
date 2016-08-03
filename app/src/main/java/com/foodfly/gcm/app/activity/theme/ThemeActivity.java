package com.foodfly.gcm.app.activity.theme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.view.recyclerView.LinearSpacingItemDecoration;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.restaurant.Theme;
import com.foodfly.gcm.model.user.MapAddress;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

import static com.foodfly.gcm.R.id.theme_image;

/**
 * Created by woozam on 2016-07-10.
 */
public class ThemeActivity extends BaseActivity {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, ThemeActivity.class);
        context.startActivity(intent);
    }

    private ArrayList<Theme> mThemeList;
    private ThemeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mThemeList = new ArrayList<>();
        mAdapter = new ThemeAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.theme_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new LinearSpacingItemDecoration(CommonUtils.convertDipToPx(this, 16)));

        loadThemeList();
    }

    private void loadThemeList() {
        MapAddress mapAddress = MapAddress.getAddress();
        String url = APIs.getThemePath().appendQueryParameter(APIs.PARAM_LAT, String.valueOf(mapAddress.getLat())).appendQueryParameter(APIs.PARAM_LON, String.valueOf(mapAddress.getLon())).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(mapAddress.getAreaCode())).toString();
        GsonRequest<Theme[]> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<Theme[]>() {
        }.getType(), APIs.createHeadersWithToken(), new Listener<Theme[]>() {
            @Override
            public void onResponse(Theme[] response) {
                dismissProgressDialog();
                mThemeList.clear();
                Collections.addAll(mThemeList, response);
                mAdapter.notifyDataSetChanged();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(ThemeActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private class ThemeAdapter extends Adapter<ThemeViewHolder> {

        @Override
        public ThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ThemeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_theme, parent, false));
        }

        @Override
        public void onBindViewHolder(final ThemeViewHolder holder, int position) {
            holder.setTheme(mThemeList.get(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThemeRestaurantActivity.createInstance(v.getContext(), mThemeList.get(holder.getAdapterPosition()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mThemeList.size();
        }
    }

    private static class ThemeViewHolder extends ViewHolder {

        private ImageView mImage;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(theme_image);
            mImage.getLayoutParams().height = (int) ((CommonUtils.getScreenWidth() - CommonUtils.convertDipToPx(mImage.getContext(), 32)) * 427f / 640f);
        }

        public void setTheme(Theme theme) {
            Glide.with(mImage.getContext()).load(theme.getTitleImage()).placeholder(R.drawable.placeholder).crossFade().into(mImage);
        }
    }
}