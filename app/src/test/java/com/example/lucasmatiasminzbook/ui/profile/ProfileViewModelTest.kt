package com.example.lucasmatiasminzbook.ui.profile

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.local.purchase.Purchase
import com.example.lucasmatiasminzbook.data.local.purchase.PurchaseRepository
import com.example.lucasmatiasminzbook.data.local.user.UserEntity
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import com.example.lucasmatiasminzbook.util.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var profileViewModel: ProfileViewModel
    private val purchaseRepository: PurchaseRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val application: Application = mockk()

    @Before
    fun onBefore() {
        every { userRepository.getLoggedInUserFlow() } returns flowOf(null)
        profileViewModel = ProfileViewModel(application, purchaseRepository, userRepository)
    }

    @Test
    fun `when user is logged in, purchases are loaded`() = runTest {
        // Given
        val user = UserEntity(1, "Test User", "test@test.com", "password")
        val purchases = listOf(Purchase(1, 1, 1, "Book", "Compra", 10, 0))
        every { userRepository.getLoggedInUserFlow() } returns flowOf(user)
        every { purchaseRepository.getPurchasesByUser(user.id) } returns flowOf(purchases)

        // When
        val result = profileViewModel.purchases.first()

        // Then
        Assert.assertEquals(purchases, result)
    }
}