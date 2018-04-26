package br.com.eduardo.salaovitinho.util;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by Eduardo on 25/03/2018.
 */

public class PhoneAutentication {

    private PhoneAutentication() {

    }

    public void phoneAutentication(String numeroTelefone, Context context,
                                   PhoneAuthProvider.OnVerificationStateChangedCallbacks callback) {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            numeroTelefone,     // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.MINUTES,   // Unit of timeout
            (Activity) context, // Activity (for callback binding)
            callback);          // OnVerificationStateChangedCallbacks
    }
}
