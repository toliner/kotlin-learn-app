package dev.toliner.petstore.repository

import dev.toliner.petstore.model.Pet
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

interface PetRepository {
    suspend fun createPet(name: String, tag: List<String>, status: Pet.Status): Pet
}

internal class PetRepositoryImpl(
    private val db: Database,
) : PetRepository {
    private object PetTable : LongIdTable() {
        val name = varchar("name", length = 50)
        val status = enumeration("status", Pet.Status::class)
        val owner = reference("user", UserRepositoryImpl.Users).nullable()
    }

    private object PetTagTable : LongIdTable() {
        val pet = reference("pet", PetTable)
        val tag = varchar("tag", length = 50)
    }

    init {
        transaction(db) {
            SchemaUtils.create(PetTable, PetTagTable)
        }
    }

    override suspend fun createPet(name: String, tag: List<String>, status: Pet.Status): Pet = db.query {
        val pet = PetTable.insert {
            it[PetTable.name] = name
            it[PetTable.status] = status
        }
        val tags = PetTagTable.batchInsert(tag) {
            this[PetTagTable.pet] = pet[PetTable.id]
            this[PetTagTable.tag] = it
        }
        Pet(
            pet[PetTable.id].value,
            pet[PetTable.name],
            tags.map { it[PetTagTable.tag] },
            null,
            pet[PetTable.status],
        )
    }
}
