package com.example.weatherol.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.weatherol.data.common.AppConstants

object PermissionUtils {

    fun hasLocationPermission(context: Context): Boolean {
        return AppConstants.Permission.LOCATION_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getMissingLocationPermissions(context: Context): List<String> {
        return AppConstants.Permission.LOCATION_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }
}
