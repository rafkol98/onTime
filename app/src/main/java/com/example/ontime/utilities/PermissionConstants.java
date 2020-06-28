package com.example.ontime.utilities;

import android.Manifest;
import android.annotation.SuppressLint;

public class PermissionConstants {
    public static final String [] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                                                 Manifest.permission.ACCESS_COARSE_LOCATION};
    @SuppressLint("InlinedApi")
    public static final String [] BACKGROUND = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    public static final int PERMISSION_ALL = 134;

    public static final int PERMISSION_BACKGROUND = 321;
}
