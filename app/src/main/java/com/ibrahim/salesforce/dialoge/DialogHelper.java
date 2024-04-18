package com.ibrahim.salesforce.dialoge;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;



/**
 * Created by Yasir on 3/3/2017.
 */
public class DialogHelper {


    public static void showAlert(Context context, String title, String msg)
    {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }

}
