package com.since.noteShield.data.database.repository

import com.since.noteShield.data.database.modal.Notes
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : MongoRepository<Notes, ObjectId>{

    fun findOwnerByOwnerId(ownerId: ObjectId) : List<Notes>

}