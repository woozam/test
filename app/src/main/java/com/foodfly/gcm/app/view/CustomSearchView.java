package com.foodfly.gcm.app.view;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.foodfly.gcm.R;

public class CustomSearchView extends SearchView {

    private TextView mSearchTextView;

    public CustomSearchView(Context context) {
        super(context);
        initialize();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        mSearchTextView = (AutoCompleteTextView) findViewById(R.id.search_src_text);
        mSearchTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
    }

    public void setSearchTextSize(int unit, float size) {
        mSearchTextView.setTextSize(unit, size);
    }

    public TextView getSearchTextView() {
        return mSearchTextView;
    }
}