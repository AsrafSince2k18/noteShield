package com.since.noteShield.domain.mapper

import com.since.noteShield.presentance.controller.NoteController
import com.since.noteShield.data.database.modal.Notes
import java.time.Instant

fun Notes.toNoteResponse(): NoteController.NoteResponse{
    return NoteController.NoteResponse(
        id = id.toHexString(),
        ownerId = ownerId.toHexString(),
        title = title,
        content = content,
        instant = Instant.now()
    )
}