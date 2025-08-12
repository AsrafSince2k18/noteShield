package com.since.noteShield.presentance.controller

import com.since.noteShield.data.database.modal.Notes
import com.since.noteShield.data.database.modal.User
import com.since.noteShield.data.database.repository.NoteRepository
import com.since.noteShield.domain.mapper.toNoteResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RequestMapping("/notes")
@RestController
class NoteController(
    private val noteRepository: NoteRepository
) {


    fun user(): User {
        return SecurityContextHolder.getContext().authentication.principal as? User
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated")
    }

    data class NoteResponse(
        val id: String,
        val ownerId: String,
        val title: String,
        val content: String,
        val instant: Instant
    )

    data class NoteRequest(
        @field:NotEmpty(message = "Title is empty")
        val title: String,
        @field:NotEmpty(message = "Content is empty")
        val content: String
    )


    @GetMapping
    fun getOwnerAllNote(): List<NoteResponse> {

        val ownerId = user().id
        val noteItem = noteRepository.findOwnerByOwnerId(ownerId = ownerId)

        if (noteItem.isEmpty()) {
            ResponseEntity.status(HttpStatus.OK).body("Note item is empty Add new note")
            return emptyList()
        }
        return noteItem.map { it.toNoteResponse() }

    }

    @GetMapping("/{id}")
    fun getNote(
        @PathVariable id: String
    ): ResponseEntity<NoteResponse> {

        val notes = noteRepository.findById(ObjectId(id))
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Id=$id not found")
            }
        return ResponseEntity.ok(notes.toNoteResponse())
    }


    @PostMapping
    fun insertNote(
        @RequestBody @Valid body: NoteRequest
    ): ResponseEntity<NoteResponse> {
        val ownerId = user().id
        val note = Notes(
            ownerId = ownerId,
            title = body.title,
            content = body.content,
            time = Instant.now()
        )

        val response = noteRepository.save(note)
        return ResponseEntity.status(HttpStatus.CREATED).body(response.toNoteResponse())
    }

    @PutMapping("/{id}")
    fun updateNote(
        @PathVariable id: String,
        @RequestBody @Valid body: NoteRequest
    ): ResponseEntity<NoteResponse> {

        val note = noteRepository.findById(ObjectId(id))
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Id=$id not found")
            }
        val noteUpdate = note.copy(
            title = body.title,
            content = body.content,
            time = Instant.now()
        )
        println("Update data")
        noteRepository.save(noteUpdate)
        return ResponseEntity.ok(noteUpdate.toNoteResponse())
    }


    @DeleteMapping("/{id}")
    fun deleteNote(
        @PathVariable id: String
    ): ResponseEntity<*> {
        val note = noteRepository.findById(ObjectId(id))
            .orElse(null)


        val delete=noteRepository.delete(note)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(delete)
    }

    @DeleteMapping
    fun deleteAllNote(): ResponseEntity<*> {
        val ownerId = user().id

        val allNote = noteRepository.findOwnerByOwnerId(ownerId)
        if (!allNote.isEmpty()) {
            noteRepository.deleteAll(allNote)
        }
        return ResponseEntity.noContent().build<Unit>()
    }


}