package com.dgrocers.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import static com.dgrocers.util.Constants.REQUEST_PERM_PHONE_CALL;

public class Utils {

	public static void dialPhone(Activity context, String phone) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERM_PHONE_CALL);
		} else {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + phone));
			context.startActivity(callIntent);
		}
	}

	public static void showError(View root, String message) {
		Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
	}

}
