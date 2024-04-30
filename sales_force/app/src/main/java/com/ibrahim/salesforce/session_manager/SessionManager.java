//package com.app.salesforce.session_manager;
//
//import static android.content.Context.MODE_PRIVATE;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.util.Log;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.gson.Gson;
//
//
//import java.io.File;
//
//public class SessionManager {
//    // Shared preferences file name
//    private static final String PREF_NAME = "Infinit8AI";
//    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
//    // Shared Preferences
//    private static SharedPreferences pref;
//    private static Editor editor;
//    //intro screen first time lunch
//    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
//    // Shared pref mode
//    // LogCat tag
//
//
//    private static String TAG = SessionManager.class.getSimpleName();
//
//    public static void setLogin(Context context, boolean isLoggedIn) {
//        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        editor = pref.edit();
//        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
//        // commit changes
//        editor.apply();
//
//        Log.d(TAG, "User login session modified!");
//    }
//
//    public static boolean isLoggedIn(Context context) {
//        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
//    }
//
//    public static void setTerm(Context context, boolean isAccepted) {
//        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        editor = pref.edit();
//        editor.putBoolean("term", isAccepted);
//        // commit changes
//        editor.apply();
//
//        Log.d(TAG, "User login session modified!");
//    }
//
//    public static boolean isAccepted(Context context) {
//        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        return pref.getBoolean("term", false);
//    }
//
//    public static void setLoginUser(Context context, Users user) {
//        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        editor = pref.edit();
//        editor.putString("user", new Gson().toJson(user));
//        editor.apply();
//    }
//
//    public static Users getLoginUser(Context context) {
//        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        return new Gson().fromJson(pref.getString("user", ""), Users.class);
//    }
//
//    public static void logoutUser(final Context context, int mode) {
//        if (mode == 0) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
////                dialog.setMessage("Do you want to logout as " + getLoginUser(context).getName() + "?");
//            dialog.setMessage("Do you want to logout?");
//            dialog.setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    logout(context);
//                }
//            });
//            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//
//                }
//            });
//
//            try {
//                dialog.show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//
//            // Logout Admin here
//        }
//
//    }
//
//    public static void logout(Context context) {
//        setLogin(context, false);
//        setLoginUser(context, null);
//        FirebaseAuth.getInstance().signOut();
//
//        deleteRecursive(new File(context.getFilesDir().getPath()));
//        // Launching the login activity
//        Intent intent = new Intent(context, MainActivity.class);
//        context.startActivity(intent);
//        ((AppCompatActivity) context).finish();
//    }
//
//    static void deleteRecursive(File fileOrDirectory) {
//
//        if (fileOrDirectory.isDirectory())
//            for (File child : fileOrDirectory.listFiles())
//                deleteRecursive(child);
//
//        fileOrDirectory.delete();
//
//    }
//
//    public void clear() {
//        editor.clear();
//        editor.commit();
//    }
//}
