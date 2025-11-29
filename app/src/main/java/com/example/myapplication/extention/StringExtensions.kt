package  com.example.myapplication.extention

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.text.Editable

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun String.toIntArray(): IntArray {
    return removeSurrounding("[", "]").split(",").map { it.toInt() }.toIntArray()
}

fun String.getAppNameFromPkgName(context: Context): String {
    return try {
        val packageManager: PackageManager = context.getPackageManager()
        val info: ApplicationInfo =
            packageManager.getApplicationInfo(this, PackageManager.GET_META_DATA)
        packageManager.getApplicationLabel(info).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }
}

fun String.getFilenameFromPath() = substring(lastIndexOf("/") + 1)
