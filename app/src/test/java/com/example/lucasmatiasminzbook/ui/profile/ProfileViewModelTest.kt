package com.example.lucasmatiasminzbook.ui.profile

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.local.purchase.Purchase
import com.example.lucasmatiasminzbook.data.local.purchase.PurchaseRepository
import com.example.lucasmatiasminzbook.data.local.user.UserEntity
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when user is logged in, purchases are loaded`() = runTest {
        // Given
        val user = UserEntity(1, "Test User", "test@test.com", "password")
        val purchases = listOf(Purchase(1, 1, 1, "Book", "Compra", 10, 0))
        val purchaseRepository: PurchaseRepository = mockk()
        val userRepository: UserRepository = mockk()
        val application: Application = mockk(relaxed = true)

        every { userRepository.getLoggedInUserFlow() } returns flowOf(user)
        every { purchaseRepository.getPurchasesByUser(user.id) } returns flowOf(purchases)

        // When
        val profileViewModel = ProfileViewModel(application, purchaseRepository, userRepository)
        
        // Then
        // Esperamos a que el StateFlow emita un valor que no sea la lista vacía inicial
        val result = profileViewModel.purchases.first { it.isNotEmpty() }
        Assert.assertEquals(purchases, result)
    }
}
