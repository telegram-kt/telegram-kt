package io.telegramkt.model.chat.administrator

object AdministratorRightsPresets {
    fun full() = chatAdministratorRights {
        isAnonymous = true
        canManageChat = true
        canDeleteMessages = true
        canManageVideoChats = true
        canRestrictMembers = true
        canPromoteMembers = true
        canChangeInfo = true
        canInviteUsers = true
        canPostStories = true
        canEditStories = true
        canDeleteStories = true
        canPostMessages = true
        canEditMessages = true
        canPinMessages = true
        canManageTopics = true
        canManagerDirectMessages = true
        canManageTags = true
    }

    fun moderator() = chatAdministratorRights {
        canDeleteMessages = true
        canRestrictMembers = true
        canInviteUsers = true
        canPinMessages = true
    }

    fun readonly() = chatAdministratorRights {
        // Only read.
    }
}