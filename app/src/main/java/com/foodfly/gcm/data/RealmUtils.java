package com.foodfly.gcm.data;

import android.graphics.drawable.Drawable;

import com.foodfly.gcm.Application;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmObject;

import com.foodfly.gcm.BuildConfig;

/**
 * Created by woozam on 2016-06-27.
 */
public class RealmUtils {

    private static final RealmMigration MIGRATION_ADDRESS = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    };

    private static final RealmMigration MIGRATION_RESTAURANT = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    };

    private static final RealmMigration MIGRATION_SEARCH_INFO = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    };

    private static final RealmMigration MIGRATION_USER = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    };

    private static final RealmMigration MIGRATION_CONNECT = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    };

    private static final RealmMigration MIGRATION_CART = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    };

    public static final RealmConfiguration CONFIG_ADDRESS =
            BuildConfig.DEBUG
                    ?
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.address").schemaVersion(0).migration(MIGRATION_SEARCH_INFO).deleteRealmIfMigrationNeeded().build()
                    :
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.address").schemaVersion(0).migration(MIGRATION_SEARCH_INFO).build()
            ;

    public static final RealmConfiguration CONFIG_RESTAURANT =
            BuildConfig.DEBUG
                    ?
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.restaurant").schemaVersion(0).migration(MIGRATION_RESTAURANT).deleteRealmIfMigrationNeeded().build()
                    :
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.restaurant").schemaVersion(0).migration(MIGRATION_RESTAURANT).build()
            ;

    public static final RealmConfiguration CONFIG_SEARCH_INFO =
            BuildConfig.DEBUG
                    ?
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.searchInfo").schemaVersion(0).migration(MIGRATION_ADDRESS).deleteRealmIfMigrationNeeded().build()
                    :
                    new RealmConfiguration.Builder(Application.getContext()).name("foodflyt.realm.searchInfo").schemaVersion(0).migration(MIGRATION_ADDRESS).build()
            ;

    public static final RealmConfiguration CONFIG_USER =
            BuildConfig.DEBUG
                    ?
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.user").schemaVersion(0).migration(MIGRATION_USER).deleteRealmIfMigrationNeeded().build()
                    :
                    new RealmConfiguration.Builder(Application.getContext()).name("foodflyt.realm.user").schemaVersion(0).migration(MIGRATION_USER).build()
            ;

    public static final RealmConfiguration CONFIG_CONNECT =
            BuildConfig.DEBUG
                    ?
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.connect").schemaVersion(0).migration(MIGRATION_CONNECT).deleteRealmIfMigrationNeeded().build()
                    :
                    new RealmConfiguration.Builder(Application.getContext()).name("foodflyt.realm.connect").schemaVersion(0).migration(MIGRATION_CONNECT).build()
            ;

    public static final RealmConfiguration CONFIG_CART =
            BuildConfig.DEBUG
                    ?
                    new RealmConfiguration.Builder(Application.getContext()).name("foodfly.realm.cart").schemaVersion(0).migration(MIGRATION_CART).deleteRealmIfMigrationNeeded().build()
                    :
                    new RealmConfiguration.Builder(Application.getContext()).name("foodflyt.realm.cart").schemaVersion(0).migration(MIGRATION_CART).build()
            ;

    public static final Gson REALM_GSON = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaringClass().equals(RealmObject.class) || f.getDeclaringClass().equals(Drawable.class);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }).registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
    }.getType(), new RealmStringDeserializer()).registerTypeAdapter(Date.class, new DateDeserializer()).registerTypeAdapter(Date.class, new DateSerializer()).create();

    public static class RealmStringDeserializer implements JsonDeserializer<RealmList<RealmString>> {

        @Override
        public RealmList<RealmString> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            RealmList<RealmString> realmStrings = new RealmList<>();
            JsonArray stringList = json.getAsJsonArray();

            for (JsonElement stringElement : stringList) {
                realmStrings.add(new RealmString(stringElement.getAsString()));
            }

            return realmStrings;
        }
    }

    public static class DateSerializer implements JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime() / 1000);
        }
    }

    public static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsLong() * 1000);
        }
    }
}
