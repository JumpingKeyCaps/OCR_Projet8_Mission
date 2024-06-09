package com.ocrmission.vitesse.data.room

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * The Callback used to prepopulate the database.
 */
class DatabasePrepopulateCallback(private val coroutineScope: CoroutineScope) : RoomDatabase.Callback() {

    /**
     * Overriding the onCreate Methode to prepopulate the Database on his call.
     * @param db the database.
     */
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        coroutineScope.launch {
            prepopulateDatabase(db)
        }
    }

    /**
     * Method to Pre-populate the database.
     * @param db the database reference to populate.
     */
    private fun prepopulateDatabase(db: SupportSQLiteDatabase){
        //Start the database transaction
        db.beginTransaction()
        try {


            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Marc")
                    put("lastname", "Claine")
                    put("email", "bob@test.test")
                    put("phone", "0134782934")
                    put("birthday", LocalDateTime.now().minusDays(1).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 3300)
                    put("note", "Fullstack developer, fan of football, and love music!")
                    put("photoUri","")
                    put("isFavorite", false)
                }
            )


            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Alice")
                    put("lastname", "Martin")
                    put("email", "alice@example.com")
                    put("phone", "0298374615")
                    put("birthday", LocalDateTime.now().minusDays(2000).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 4200)
                    put("note", "Développeuse web passionnée par le design,excellent relationnel client et expérimenté.")
                    put("photoUri", "")
                    put("isFavorite", true)
                }
            )

            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Bob")
                    put("lastname", "Dupont")
                    put("email", "bob@exemple.fr")
                    put("phone", "0678239145")
                    put("birthday", LocalDateTime.now().minusDays(3500).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 2800)
                    put("note", "Commercial expérimenté, excellent relationnel client, Graphiste créative avec une forte expérience en branding")
                    put("photoUri", "")
                    put("isFavorite", false)
                }
            )

            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Emma")
                    put("lastname", "Leroy")
                    put("email", "emma@exemple.com")
                    put("phone", "0345621987")
                    put("birthday", LocalDateTime.now().minusDays(1500).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 3500)
                    put("note", "Graphiste créative avec une forte expérience en branding,des compétences solides en gestion")
                    put("photoUri", "")
                    put("isFavorite", false)
                }
            )





            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "David")
                    put("lastname", "Legrand")
                    put("email", "david@exemple.org")
                    put("phone", "0487532691")
                    put("birthday", LocalDateTime.now().minusDays(2200).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 4800)
                    put("note", "Chef de projet informatique avec des compétences solides en gestion d'équipe")
                    put("photoUri", "")
                    put("isFavorite", true)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Sarah")
                    put("lastname", "Petit")
                    put("email", "sarah@exemple.net")
                    put("phone", "0598463254")
                    put("birthday", LocalDateTime.now().minusDays(1800).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 2900)
                    put("note", "Comptable rigoureuse avec une passion pour les chiffres")
                    put("photoUri", "")
                    put("isFavorite", false)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Nicolas")
                    put("lastname", "Durant")
                    put("email", "nicolas@exemple.fr")
                    put("phone", "0678954321")
                    put("birthday", LocalDateTime.now().minusDays(2500).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 3200)
                    put("note", "Développeur mobile enthousiaste, toujours à la recherche de nouveaux défis")
                    put("photoUri", "")
                    put("isFavorite", true)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Marie")
                    put("lastname", "Lopez")
                    put("email", "marie@exemple.com")
                    put("phone", "0785246132")
                    put("birthday", LocalDateTime.now().minusDays(1600).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 4500)
                    put("note", "Responsable marketing expérimentée, capable de créer des campagnes percutantes")
                    put("photoUri", "")
                    put("isFavorite", false)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Thomas")
                    put("lastname", "Martin")
                    put("email", "thomas@exemple.org")
                    put("phone", "0896357241")
                    put("birthday", LocalDateTime.now().minusDays(2100).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 2700)
                    put("note", "Technicien informatique qualifié, capable de résoudre tous vos problèmes informatiques")
                    put("photoUri", "")
                    put("isFavorite", true)
                }
            )




            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Charlie")
                    put("lastname", "Johnson")
                    put("email", "charlie@gmail.com")
                    put("phone", "0123456789")
                    put("birthday", LocalDateTime.now().minusYears(30).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 5500)
                    put("note", "Senior Software Engineer with 10+ years of experience")
                    put("photoUri", "")
                    put("isFavorite", true)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "David",)
                    put("lastname", "Williams")
                    put("email", "david@yahoo.com")
                    put("phone", "0987654321")
                    put("birthday", LocalDateTime.now().minusMonths(6).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 4800)
                    put("note", "Graphic Designer with a passion for creating beautiful visuals")
                    put("photoUri", "")
                    put("isFavorite", false)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Charlie")
                    put("lastname", "Brown")
                    put("email", "charlie@peanuts.com")
                    put("phone", "555-1212")
                    put("birthday", LocalDateTime.now().minusDays(4000).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 2500)
                    put("note", "Snoopy's best friend")
                    put("photoUri", "")
                    put("isFavorite", true)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Emily",)
                    put("lastname", "Taylor")
                    put("email", "emily@yahoo.fr")
                    put("phone", "0487564321")
                    put("birthday", LocalDateTime.now().minusDays(1800).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 2500)
                    put("note", "Graphiste freelance, spécialisée en création de logos et d'illustrations")
                    put("photoUri", "")
                    put("isFavorite", false)
                }
            )
            db.insert(
                "candidates",
                SQLiteDatabase.CONFLICT_IGNORE,
                ContentValues().apply {
                    put("firstname", "Frank",)
                    put("lastname", "Anderson")
                    put("email", "frank@orange.com")
                    put("phone", "0598765432")
                    put("birthday", LocalDateTime.now().minusDays(3000).atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    put("salary", 4000)
                    put("note", "Chef de produit innovant, recherche de nouveaux défis")
                    put("photoUri", "")
                    put("isFavorite", false)
                }
            )




            //transaction is all ok !
            db.setTransactionSuccessful()

        } catch (e: Exception) {
            // Handle potential exceptions during pre-population
        } finally {
            //Finish the transaction
            db.endTransaction()
        }

    }

}