package  com.example.myapplication.extention

import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.Intruder

fun Array<String>.convertToCMD(): String {
    var stringCMD = ""
    this.forEachIndexed { i, value ->
        stringCMD += if (i < size - 1) {
            "$value "
        } else {
            value
        }
    }
    return stringCMD
}

fun List<Any>.isIndexInOfBounds(index: Int): Boolean {
    return index in indices
}

fun List<AppData>.insertNativeToListAppData(): List<Any> {
    val listData = mutableListOf<Any>()
    listData.addAll(this)
//    if (RemoteManager.nativeLock && !DataLocal.isVip()) {
//        if (listData.isNotEmpty()) {
//            listData.add(0, AdmobData.NativeView)
//        }
//    }
    return listData
}

fun List<Intruder>.insertNativeToListIntruder(): List<Any> {
    val listData = mutableListOf<Any>()
    listData.addAll(this)
//    if (RemoteManager.nativeLock && !DataLocal.isVip()) {
//        if (listData.isNotEmpty()) {
//            listData.add(0, AdmobData.NativeView)
//        }
//    }
    return listData
}