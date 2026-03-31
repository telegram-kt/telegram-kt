package io.telegramkt.model.checklist.input

import io.telegramkt.model.ParseMode
import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputChecklist(
    @SerialName("title") val title: String,
    @SerialName("tasks") val tasks: List<InputChecklistTask>,
    @SerialName("parse_mode") val parseMode: ParseMode? = null,
    @SerialName("title_entities") val titleEntities: List<MessageEntity>? = null,
    @SerialName("others_can_add_tasks") val othersCanAddTasks: Boolean? = null,
    @SerialName("others_can_mark_tasks_as_done") val othersCanMarkTasksAsDone: Boolean? = null,
)