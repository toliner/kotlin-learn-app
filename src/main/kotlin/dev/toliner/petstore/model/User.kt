package dev.toliner.petstore.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val type: Type,
) {
    @Suppress("EnumEntryName")
    enum class Type {
        customer,
        employee
    }
}
