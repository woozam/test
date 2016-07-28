package kr.co.foodfly.androidapp.app.view.recyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by woody on 2016-05-20.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected LayoutInflater inflater;
    protected RealmResults<T> realmResults;
    protected Context context;

    private final RealmChangeListener listener;

    public RealmRecyclerViewAdapter(Context context, RealmResults<T> realmResults, RealmChangeListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.realmResults = realmResults;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;

        if (listener != null && realmResults != null) {
            realmResults.addChangeListener(listener);
        }
    }

    public void destroy() {
        if (this.realmResults != null) {
            realmResults.removeChangeListener(listener);
        }
    }

    @Override
    public long getItemId(int i) {
        // TODO: find better solution once we have unique IDs
        return i;
    }

    public T getItem(int i) {
        if (realmResults == null) {
            return null;
        }
        return realmResults.get(i);
    }

    public void updateRealmResults(RealmResults<T> queryResults) {
        if (listener != null) {
            // Making sure that Adapter is refreshed correctly if new RealmResults come from another Realm
            if (this.realmResults != null) {
                realmResults.removeChangeListener(listener);
            }
            if (queryResults != null) {
                queryResults.addChangeListener(listener);
            }
        }

        this.realmResults = queryResults;
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (realmResults == null) {
            return 0;
        }
        return realmResults.size();
    }
}