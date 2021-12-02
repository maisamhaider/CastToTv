package com.example.casttotv.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.casttotv.models.FileModel
import com.example.casttotv.models.FolderModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File


class SharedRepository(val context: Context) {


    @SuppressLint("Range")
    val imageFolderFlow: Flow<List<FolderModel>> = flow {
        val folder: MutableList<FolderModel> = ArrayList()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var folderName = ""
        var folderPath = ""
        val folderList: MutableSet<String> = mutableSetOf()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context.contentResolver.query(uri, projection, null, null, null)!!

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                val firstImage: String = cursor.getString(cursor.getColumnIndex(projection[0]))
                val lastIndex = firstImage.lastIndexOf("/")
                val prevIndex = firstImage.lastIndexOf("/", lastIndex - 1)

                folderPath = firstImage.substring(0, lastIndex)
                folderName = firstImage.substring(prevIndex + 1, lastIndex)
                if (!folderList.contains(folderPath)) {
                    folder.add(FolderModel(folderName, folderPath, firstImage))
                    emit(folder)
                }
                folderList.add(folderPath)
            }
        }
        cursor.close()

    }

    @SuppressLint("Range")
    fun getImagesByBucket(bucketPath: String): Flow<List<FileModel>> = flow {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )
        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val images: MutableList<FileModel> = ArrayList()
        val cursor: Cursor = context.contentResolver
            .query(uri, projection, null, null, orderBy)!!
        var file: File
        var fileModel: FileModel?
        while (cursor.moveToNext()) {
            val path: String = cursor.getString(cursor.getColumnIndex(projection[0]))
            val name: String = cursor.getString(cursor.getColumnIndex(projection[1]))
            val size: Long = cursor.getLong(cursor.getColumnIndex(projection[2]))
            file = File(path)
            fileModel = FileModel(name, size = size, path)
            if (file.exists() && !images.contains(fileModel) && path.contains(bucketPath)) {
                images.add(fileModel)
                emit(images)
            }
        }
        cursor.close()
    }

    @SuppressLint("Range")
    val videosFoldersFlow: Flow<List<FolderModel>> = flow {
        val folder: MutableList<FolderModel> = ArrayList()
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        var folderName = ""
        var folderPath = ""
        val folderList: MutableSet<String> = mutableSetOf()
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor = context.contentResolver.query(uri, projection, null, null, null)!!

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                val firstImage: String = cursor.getString(cursor.getColumnIndex(projection[0]))
                val lastIndex = firstImage.lastIndexOf("/")
                val prevIndex = firstImage.lastIndexOf("/", lastIndex - 1)

                folderPath = firstImage.substring(0, lastIndex)
                folderName = firstImage.substring(prevIndex + 1, lastIndex)
                if (!folderList.contains(folderPath)) {
                    folder.add(FolderModel(folderName, folderPath, firstImage))
                    emit(folder)
                }
                folderList.add(folderPath)
            }
        }
        cursor.close()

    }

    @SuppressLint("Range")
    fun videosByBucket(bucketPath: String): Flow<List<FileModel>> = flow {
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )
        val orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC"
        val images: MutableList<FileModel> = ArrayList()
        val cursor: Cursor = context.contentResolver
            .query(uri, projection, null, null, orderBy)!!
        var file: File
        var fileModel: FileModel?
        while (cursor.moveToNext()) {
            val path: String = cursor.getString(cursor.getColumnIndex(projection[0]))
            val name: String = cursor.getString(cursor.getColumnIndex(projection[1]))
            val duration: Long = cursor.getLong(cursor.getColumnIndex(projection[2]))
            val size: Long = cursor.getLong(cursor.getColumnIndex(projection[3]))
            file = File(path)
            fileModel = FileModel(name, size = size, path, duration)
            if (file.exists() && !images.contains(fileModel) && path.contains(bucketPath) && duration.toInt() != 0) {
                images.add(fileModel)
                emit(images)
            }
        }
        cursor.close()
    }

    @SuppressLint("Range")
    val audioFoldersFlow: Flow<List<FolderModel>> = flow {
        val folder: MutableList<FolderModel> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var folderName: String
        var folderPath: String
        val folderList: MutableSet<String> = mutableSetOf()
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor = context.contentResolver.query(uri, projection, null, null, null)!!

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                val firstImage: String = cursor.getString(cursor.getColumnIndex(projection[0]))
                val lastIndex = firstImage.lastIndexOf("/")
                val prevIndex = firstImage.lastIndexOf("/", lastIndex - 1)

                folderPath = firstImage.substring(0, lastIndex)
                folderName = firstImage.substring(prevIndex + 1, lastIndex)
                if (!folderList.contains(folderPath)) {
                    folder.add(FolderModel(folderName, folderPath, firstImage))
                    emit(folder)
                }
                folderList.add(folderPath)
            }
        }
        cursor.close()

    }

    @SuppressLint("Range")
    fun audiosByBucket(bucketPath: String): Flow<List<FileModel>> = flow {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
        )
        val orderBy = MediaStore.Audio.Media.DATE_ADDED + " DESC"
        val images: MutableList<FileModel> = ArrayList()
        val cursor: Cursor = context.contentResolver
            .query(uri, projection, null, null, orderBy)!!
        var file: File
        var fileModel: FileModel?
        while (cursor.moveToNext()) {
            val path: String = cursor.getString(cursor.getColumnIndex(projection[0]))
            val name: String = cursor.getString(cursor.getColumnIndex(projection[1]))
            val duration: Long = cursor.getLong(cursor.getColumnIndex(projection[2]))
            val size: Long = cursor.getLong(cursor.getColumnIndex(projection[3]))
            file = File(path)
            fileModel = FileModel(name, size = size, path, duration)
            if (file.exists() && !images.contains(fileModel) && path.contains(bucketPath) && (duration.toInt() != 0)) {
                images.add(fileModel)
                emit(images)
            }
        }
        cursor.close()
    }


}