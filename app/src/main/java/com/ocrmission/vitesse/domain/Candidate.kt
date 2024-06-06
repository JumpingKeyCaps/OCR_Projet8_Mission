package com.ocrmission.vitesse.domain

import java.time.LocalDateTime

/**
 *  class to create Object Candidate who modeling the candidate data.
 */
data class Candidate(
    @JvmField var id: Int,
    var firstname: String,
    var lastname: String,
    var email: String,
    var phone: String,
    var birthday: LocalDateTime,
    var salary: Long,
    var note: String,
    var photoUri: String,
    var isFavorite: Boolean

) {





}