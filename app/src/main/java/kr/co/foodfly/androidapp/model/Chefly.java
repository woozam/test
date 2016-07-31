package kr.co.foodfly.androidapp.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by woozam on 2016-07-26.
 */
public class Chefly {

    public static void createCheflyInstance(Context context) {
        Uri uri = Uri.parse("foodflyios://web?url=http://chefly.foodfly.co.kr?channel=ios");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
    }
}