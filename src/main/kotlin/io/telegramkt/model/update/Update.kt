package io.telegramkt.model.update

import io.telegramkt.model.business.BusinessConnection
import io.telegramkt.model.business.BusinessMessagesDeleted
import io.telegramkt.model.callback.CallbackQuery
import io.telegramkt.model.chat.boost.ChatBoostRemoved
import io.telegramkt.model.chat.boost.ChatBoostUpdated
import io.telegramkt.model.chat.join.ChatJoinRequest
import io.telegramkt.model.chat.member.ChatMemberUpdated
import io.telegramkt.model.inline.ChosenInlineResult
import io.telegramkt.model.inline.InlineQuery
import io.telegramkt.model.message.Message
import io.telegramkt.model.payment.PaidMediaPurchased
import io.telegramkt.model.payment.order.PreCheckoutQuery
import io.telegramkt.model.payment.order.shipping.ShippingQuery
import io.telegramkt.model.poll.Poll
import io.telegramkt.model.poll.PollAnswer
import io.telegramkt.model.reaction.MessageReactionCountUpdated
import io.telegramkt.model.reaction.MessageReactionUpdated
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Update(
    @SerialName("update_id") val updateId: Int,
    @SerialName("message") val message: Message? = null,
    @SerialName("edited_message") val editedMessage: Message? = null,
    @SerialName("channel_post") val channelPost: Message? = null,
    @SerialName("edited_channel_post") val editedChannelPost: Message? = null,
    @SerialName("business_connection") val businessConnection: BusinessConnection? = null,
    @SerialName("business_message") val businessMessage: Message? = null,
    @SerialName("edited_business_message") val editedBusinessMessage: Message? = null,
    @SerialName("deleted_business_messages") val deletedBusinessMessages: BusinessMessagesDeleted? = null,
    @SerialName("message_reaction") val messageReaction: MessageReactionUpdated? = null,
    @SerialName("message_reaction_count") val messageReactionCount: MessageReactionCountUpdated? = null,
    @SerialName("inline_query") val inlineQuery: InlineQuery? = null,
    @SerialName("chosen_inline_result") val chosenInlineResult: ChosenInlineResult? = null,
    @SerialName("callback_query") val callbackQuery: CallbackQuery? = null,
    @SerialName("shipping_query") val shippingQuery: ShippingQuery? = null,
    @SerialName("pre_checkout_query") val preCheckoutQuery: PreCheckoutQuery? = null,
    @SerialName("purchased_paid_media") val purchasedPaidMedia: PaidMediaPurchased? = null,
    @SerialName("poll") val poll: Poll? = null,
    @SerialName("poll_answer") val pollAnswer: PollAnswer? = null,
    @SerialName("my_chat_member") val myChatMember: ChatMemberUpdated? = null,
    @SerialName("chat_member") val chatMember: ChatMemberUpdated? = null,
    @SerialName("chat_join_request") val chatJoinRequest: ChatJoinRequest? = null,
    @SerialName("chat_boost") val chatBoost: ChatBoostUpdated? = null,
    @SerialName("removed_chat_boost") val removedChatBoost: ChatBoostRemoved? = null,
)
