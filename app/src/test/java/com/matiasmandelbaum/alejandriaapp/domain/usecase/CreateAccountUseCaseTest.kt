package com.matiasmandelbaum.alejandriaapp.domain.usecase

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class CreateAccountUseCaseTest {

    @RelaxedMockK
    private lateinit var authenticationService: AuthenticationService

    @RelaxedMockK
    private lateinit var userService: UserService

    lateinit var createAccountUseCase: CreateAccountUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        createAccountUseCase = CreateAccountUseCase(authenticationService, userService)
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
    }

    @Test
    fun `when account creation is successful, return true`() = runBlocking {
        // Given: Simulate successful account creation
        val userSignIn = UserSignIn("test name", "123456", "test@example.com", "12/12/2000", "123456", "123456")

        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseUser.uid } returns "mocked-uid"

        val authResult = mockk<AuthResult>()
        every { authResult.user } returns firebaseUser

        coEvery { authenticationService.createAccount(userSignIn.email, userSignIn.password) } returns authResult

        // When
        val result = createAccountUseCase(userSignIn)

        // Then
        assertTrue(result)
    }

    @Test
    fun `when account creation fails, return false`() = runBlocking {
        // Given: Simulate failed account creation
        val userSignIn = UserSignIn("test name", "123456", "test@example.com", "12/12/2000", "123456", "123456")

        coEvery { authenticationService.createAccount(userSignIn.email, userSignIn.password) } returns null

        // When
        val result = createAccountUseCase(userSignIn)

        // Then
        assertFalse(result)
    }

    @Test
    fun `when FirebaseAuthUserCollisionException is thrown, return false`() = runBlocking {
        val userSignIn = UserSignIn("test name", "123456", "test@example.com", "12/12/2000", "123456", "123456")

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        val exception = mockk<FirebaseAuthUserCollisionException>()
        coEvery { authenticationService.createAccount(userSignIn.email, userSignIn.password) } throws exception

        // When
        val result = createAccountUseCase(userSignIn)

        // Then
        assertFalse(result)
    }

}