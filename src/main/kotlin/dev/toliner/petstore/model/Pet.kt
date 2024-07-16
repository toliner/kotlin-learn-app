package dev.toliner.petstore.model

import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    val id: Long,
    val name: String,
    val tag: List<String>?,
    val owner: User?,
    val status: Status,
) {
    @Serializable
    enum class Status {
        in_stock,
        sold,
        not_for_sale
    }
}
