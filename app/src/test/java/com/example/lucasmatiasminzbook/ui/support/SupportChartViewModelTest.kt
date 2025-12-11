package com.example.lucasmatiasminzbook.ui.support

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.remote.repository.SupportRepository
import com.example.lucasmatiasminzbook.data.remote.support.SupportMessageDto
import com.example.lucasmatiasminzbook.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SupportChartViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var supportChatViewModel: SupportChatViewModel
    private val supportRepository: SupportRepository = mockk()

    @Before
    fun onBefore() {
        supportChatViewModel = SupportChatViewModel(supportRepository)
    }

    @Test
    fun `when messages are loaded, the ui state is updated`() = runTest {
        // Given
        val conversationId = 1L
        val fakeMessages = listOf(SupportMessageDto(1, conversationId, 1, "Hello", null))
        coEvery { supportRepository.getMessages(conversationId) } returns fakeMessages

        // When
        supportChatViewModel.loadMessages(conversationId)

        // Then
        val uiState = supportChatViewModel.uiState.value
        assert(!uiState.isLoading)
        assert(uiState.error == null)
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

        // Then
        val uiState = supportChatViewModel.uiState.value
        assert(!uiState.isLoading)
        Assert.assertEquals(errorMessage, uiState.error)
    }
}