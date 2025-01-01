package modules.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth

suspend fun logInWithPassword(email: String, password: String): AuthResult {
    return Firebase.auth.signInWithEmailAndPassword(email, password)
}

fun currentUser(): FirebaseUser? {
    return Firebase.auth.currentUser
}