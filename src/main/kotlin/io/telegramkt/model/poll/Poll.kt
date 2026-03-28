package io.telegramkt.model.poll

import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Poll(
    @SerialName("id") val id: String,
    @SerialName("question") val question: String,
    @SerialName("question_entities") val questionEntities: List<MessageEntity>? = null,
    @SerialName("options") val options: List<PollOption>,
    @SerialName("total_voter_count") val totalVoterCount: Int,
    @SerialName("is_closed") val isClosed: Boolean,
    @SerialName("is_anonymous") val isAnonymous: Boolean,
    @SerialName("type") val type: String,
    @SerialName("allows_multiple_answers") val allowsMultipleAnswers: Boolean,
    @SerialName("correct_option_id") val correctOptionId: Int? = null,
    @SerialName("explanation") val explanation: String? = null,
    @SerialName("explanation_entities") val explanationEntities: List<MessageEntity>? = null,
    @SerialName("open_period") val openPeriod: Int? = null,
    @SerialName("close_date") val closeDate: Int? = null
)