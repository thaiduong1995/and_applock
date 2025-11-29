package com.example.myapplication.utils

import android.hardware.camera2.CameraCharacteristics
import android.util.SparseIntArray
import android.view.Surface

/**
 * Created by Thinhvh on 06/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
fun sensorToDeviceRotation(c: CameraCharacteristics, deviceOrientation: Int): Int {
    var deviceOrien: Int
    val sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
    deviceOrien = orientations.get(deviceOrientation)

    if (c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
        deviceOrien = -deviceOrien
    }

    return (sensorOrientation + deviceOrien + 360) % 360
}

private var orientations: SparseIntArray = SparseIntArray(4).apply {
    append(Surface.ROTATION_0, 0)
    append(Surface.ROTATION_90, 90)
    append(Surface.ROTATION_180, 180)
    append(Surface.ROTATION_270, 270)
}
