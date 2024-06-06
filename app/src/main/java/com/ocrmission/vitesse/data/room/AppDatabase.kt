package com.ocrmission.vitesse.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ocrmission.vitesse.data.room.dao.CandidateDtoDao
import com.ocrmission.vitesse.data.room.entity.CandidateDto

/**
 * The Database of the App (Room)
 */

@Database(entities = [CandidateDto::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun candidateDtoDao(): CandidateDtoDao
}