package io.telegramkt.model.checklist

import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Checklist(
    @SerialName("title") val title: String,
    @SerialName("title_entities") val titleEntities: List<MessageEntity>? = null,
    @SerialName("tasks") val tasks: List<ChecklistTask>? = null,
    @SerialName("others_can_add_tasks") val othersCanAddTasks: Boolean? = null,
    @SerialName("others_can_mark_tasks_as_done") val othersCanMarkTasksAsDone: Boolean? = null,
)
