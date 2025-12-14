package com.example.lucasmatiasminzbook.ui.support

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.remote.repository.SupportRepository
import com.example.lucasmatiasminzbook.data.remote.support.SupportMessageDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SupportChartViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var supportChatViewModel: SupportChatViewModel
    private val supportRepository: SupportRepository = mockk()

    @Before
    fun onBefore() {
        Dispatchers.setMain(testDispatcher)
        supportChatViewModel = SupportChatViewModel(supportRepository)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when messages are loaded, the ui state is updated`() = runTest {
        // Given
        val conversationId = 1L
        val fakeMessages = listOf(SupportMessageDto(1, conversationId, 1, "Hello", null))
        coEvery { supportRepository.getMessages(conversationId) } returns fakeMessages

        // When
        supportChatViewModel.loadMessages(conversationId)
        advanceUntilIdle()

        // Then
        val uiState = supportChatViewModel.uiState.value
        Assert.assertFalse(uiState.isLoading)
        Assert.assertNull(uiState.error)
        Assert.assertEquals(1, uiState.messages.size)
        Assert.assertEquals("Hello", uiState.messages.first().contenido)
    }

    @Test
    fun `when loading messages fails, error is updated`() = runTest {
        // Given
        val conversationId = 1L
        val errorMessage = "Network error"
        coEvery { supportRepository.getMessages(conversationId) } throws Exception(errorMessage)

        // When
        supportChatViewModel.loadMessages(conversationId)
        advanceUntilIdle()

        // Then
        val uiState = supportChatViewModel.uiState.value
        Assert.assertFalse(uiState.isLoading)
        Assert.assertEquals(errorMessage, uiState.error)
    }

    @Test
    fun `when a message is sent, the message list is updated`() = runTest {
        // Given
        val conversationId = 1L
        val userId = 1L
        val messageContent = "New message"
        val initialMessages = listOf(SupportMessageDto(1, conversationId, userId, "First message", null))
        val updatedMessages = initialMessages + SupportMessageDto(2, conversationId, userId, messageContent, null)

        coEvery { supportRepository.getMessages(conversationId) } returns initialMessages andThen updatedMessages
        coEvery { supportRepository.sendMessage(conversationId, userId, messageContent) } returns mockk()

        // When
        supportChatViewModel.loadMessages(conversationId)
        advanceUntilIdle()
        supportChatViewModel.sendMessage(conversationId, userId, messageContent)
        advanceUntilIdle()

        // Then
        val uiState = supportChatViewModel.uiState.value
        Assert.assertFalse(uiState.sending)
        Assert.assertEquals(2, uiState.messages.size)
        Assert.assertEquals(messageContent, uiState.messages.last().contenido)
    }

    @Test
    fun `when conversation is closed, the ticketClosed flag is updated`() = runTest {
        // Given
        val conversationId = 1L
        coEvery { supportRepository.closeConversation(conversationId) } returns mockk()

        // When
        supportChatViewModel.closeConversation(conversationId)
        advanceUntilIdle()

        // Then
        val uiState = supportChatViewModel.uiState.value
        Assert.assertFalse(uiState.closing)
        Assert.assertTrue(uiState.ticketClosed)
    }
}
