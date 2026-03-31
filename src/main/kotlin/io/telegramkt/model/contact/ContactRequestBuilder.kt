package io.telegramkt.model.contact

class ContactRequestBuilder(val phoneNumber: String, val firstName: String) {
    var lastName: String? = null
    var userId: Int? = null
    var vCard: String? = null

    fun build() = Contact(phoneNumber, firstName, lastName, userId, vCard)
}