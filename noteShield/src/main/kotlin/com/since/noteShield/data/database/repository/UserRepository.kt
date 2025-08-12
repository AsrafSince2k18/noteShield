package com.since.noteShield.data.database.repository

import com.since.noteShield.data.database.modal.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {

    fun findByEmail(email:String) : User?

}