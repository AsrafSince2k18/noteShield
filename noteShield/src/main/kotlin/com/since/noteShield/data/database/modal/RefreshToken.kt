package com.since.noteShield.data.database.modal

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("refresh_token")
data class RefreshToken(

    val ownerId: ObjectId,
    val createAt: Instant= Instant.now(),

    @Indexed(expireAfter = "0s")
    val expiryAt: Instant,
    val hasToken:String

)
