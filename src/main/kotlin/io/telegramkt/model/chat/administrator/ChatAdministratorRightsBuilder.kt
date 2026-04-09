package io.telegramkt.model.chat.administrator

class ChatAdministratorRightsBuilder {
    var isAnonymous: Boolean = false
    var canManageChat: Boolean = false
    var canDeleteMessages: Boolean = false
    var canManageVideoChats: Boolean = false
    var canRestrictMembers: Boolean = false
    var canPromoteMembers: Boolean = false
    var canChangeInfo: Boolean = false
    var canInviteUsers: Boolean = false
    var canPostStories: Boolean? = null
    var canEditStories: Boolean? = null
    var canDeleteStories: Boolean? = null
    var canPostMessages: Boolean? = null
    var canEditMessages: Boolean? = null
    var canPinMessages: Boolean? = null
    var canManageTopics: Boolean? = null
    var canManagerDirectMessages: Boolean? = null
    var canManageTags: Boolean? = null

    fun build() = ChatAdministratorRights(
        isAnonymous = isAnonymous,
        canManageChat = canManageChat,
        canDeleteMessages = canDeleteMessages,
        canManageVideoChats = canManageVideoChats,
        canRestrictMembers = canRestrictMembers,
        canPromoteMembers = canPromoteMembers,
        canChangeInfo = canChangeInfo,
        canInviteUsers = canInviteUsers,
        canPostStories = canPostStories,
        canEditStories = canEditStories,
        canDeleteStories = canDeleteStories,
        canPostMessages = canPostMessages,
        canEditMessages = canEditMessages,
        canPinMessages = canPinMessages,
        canManageTopics = canManageTopics,
        canManageDirectMessages = canManagerDirectMessages,
        canManageTags = canManageTags,
    )
}

inline fun chatAdministratorRights(block: ChatAdministratorRightsBuilder.() -> Unit): ChatAdministratorRights {
    return ChatAdministratorRightsBuilder().apply(block).build()
}