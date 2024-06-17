package com.ocrmission.vitesse.domain

import android.os.Parcelable
import com.ocrmission.vitesse.data.room.entity.CandidateDto
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *  class to create Object Candidate who modeling the candidate data.
 */
@Parcelize
data class Candidate(
    @JvmField var id: Long = 0,
    var firstname: String,
    var lastname: String,
    var email: String,
    var phone: String,
    var birthday: LocalDateTime?,
    var salary: Int,
    var note: String,
    var photoUri: String = "",
    var isFavorite: Boolean

) : Parcelable {
    /**
     *  function to convert Candidate object to CandidateDto object
     *  @return CandidateDto object
     */
    fun toDto(): CandidateDto {
        return CandidateDto(
            firstname = this.firstname,
            lastname = this.lastname,
            email = this.email,
            phone = this.phone,
            birthday = (this.birthday?.atZone(ZoneId.systemDefault())?.toInstant())?.toEpochMilli()?:0,
            salary = this.salary,
            note = this.note,
            photoUri = this.photoUri,
            isFavorite = this.isFavorite
        )
    }

    companion object {
        /**
         *  function to convert CandidateDto object to Candidate object
         *  @param dto CandidateDto object to convert.
         *  @return Candidate object.
         */
        fun fromDto(dto: CandidateDto): Candidate {
            val instant = Instant.ofEpochMilli(dto.birthday)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            return Candidate(
                id = dto.id,
                firstname = dto.firstname,
                lastname = dto.lastname,
                email = dto.email,
                phone = dto.phone,
                birthday = localDateTime,
                salary = dto.salary,
                note = dto.note,
                photoUri = dto.photoUri,
                isFavorite = dto.isFavorite
            )
        }

    }

}