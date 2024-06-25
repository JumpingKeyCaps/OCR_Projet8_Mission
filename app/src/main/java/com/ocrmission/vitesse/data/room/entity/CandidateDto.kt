package com.ocrmission.vitesse.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  Candidate DTO -
 *  Data Class used by Room to create the Candidate DataBase table and ease the CRUD operation on it.
 */
@Entity(tableName = "candidates")
data class CandidateDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "firstname")
    var firstname: String,

    @ColumnInfo(name = "lastname")
    var lastname: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "phone")
    var phone: String,

    @ColumnInfo(name = "birthday")
    var birthday: Long,

    @ColumnInfo(name = "salary")
    var salary: Int,

    @ColumnInfo(name = "note")
    var note: String,

    @ColumnInfo(name = "photoUri")
    var photoUri: String,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean
)
