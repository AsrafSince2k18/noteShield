package com.since.noteShield.data.database.repository

import com.since.noteShield.data.database.modal.RefreshToken
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : MongoRepository<RefreshToken, ObjectId> {

    fun findByOwnerIdAndHasToken(ownerId: ObjectId,hasToken:String) : RefreshToken?
    fun deleteByOwnerIdAndHasToken(ownerId: ObjectId,hasToken:String) : Long

}