package dev.toliner.petstore.repository

import dev.toliner.petstore.model.User
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository {
    suspend fun createUser(name: String, type: User.Type): User

    suspend fun findById(id: Long): User?

    suspend fun findByName(name: String): User?

    suspend fun updateUserName(id: Long, newName: String): Boolean
}

internal class UserRepositoryImpl(
    private val db: Database,
) : UserRepository {
    internal object Users : LongIdTable() {
        val name = varchar("name", length = 50).uniqueIndex()
        val type = enumeration("type", User.Type::class)
    }

    init {
        transaction(db) {
            SchemaUtils.create(Users)
        }
    }

    override suspend fun createUser(name: String, type: User.Type): User {
        val exists = db.query {
            Users.select { Users.name eq name }.count() > 0
        }
        if (exists) throw NameConflictException()
        val newUser = db.query {
            Users.insert {
                it[Users.name] = name
                it[Users.type] = type
            }
        }
        return User(
            newUser[Users.id].value,
            newUser[Users.name],
            newUser[Users.type]
        )
    }

    override suspend fun findById(id: Long): User? {
        return db.query {
            Users.select { Users.id eq id }
                .asIterable()
                .map {
                    User(
                        it[Users.id].value,
                        it[Users.name],
                        it[Users.type]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun findByName(name: String): User? {
        return db.query {
            Users.select { Users.name eq name }
                .asIterable()
                .map {
                    User(
                        it[Users.id].value,
                        it[Users.name],
                        it[Users.type]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun updateUserName(id: Long, newName: String): Boolean {
        return db.query {
            Users.update({ Users.id eq id }) {
                it[name] = newName
            } > 0
        }
    }
}
