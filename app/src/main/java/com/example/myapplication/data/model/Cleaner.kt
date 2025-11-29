package com.example.myapplication.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.example.myapplication.R

@Keep
sealed class Cleaner(
    @DrawableRes var resId: Int = 0,
    @StringRes var resTitle: Int = 0,
    var isSelected: Boolean = false,
    var cleanCompleted: Boolean = false
)

@Keep
class JunkFile(
    val listFile: ArrayList<FileApp> = arrayListOf(),
    val listJunkFile: ArrayList<FileApp> = arrayListOf()
) : Cleaner(R.drawable.ic_file, R.string.cleaner_junk_file)

@Keep
class Image(
    val listFile: ArrayList<FileApp> = arrayListOf(),
    val listImageDuplicate: ArrayList<FileApp> = arrayListOf()
) : Cleaner(R.drawable.ic_photo, R.string.cleaner_photos)

@Keep
class Video(
    val listFile: ArrayList<FileApp> = arrayListOf(),
    val listVideoDuplicate: ArrayList<FileApp> = arrayListOf()
) : Cleaner(R.drawable.ic_video, R.string.cleaner_videos)

@Keep
class Contacts(
    val contacts: ArrayList<Contact> = arrayListOf(),
    val listContactDuplicate: ArrayList<Contact> = arrayListOf()
) : Cleaner(R.drawable.ic_contact_blue, R.string.cleaner_contacts)
