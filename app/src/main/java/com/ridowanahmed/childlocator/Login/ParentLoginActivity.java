package com.ridowanahmed.childlocator.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;
import com.ridowanahmed.childlocator.Dashboard.ParentDashboard;
import com.ridowanahmed.childlocator.R;

public class ParentLoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner_countryCode;
    private final String TAG = "ParentLoginActivity";
    TextInputEditText editText_parent_number;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId, phoneNumber, countryCode;

    SharedPreferences mSharedPreferences;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_login);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        spinner_countryCode = (Spinner)findViewById(R.id.parent_countryCode);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.country, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_countryCode.setAdapter(adapter);
        spinner_countryCode.setOnItemSelectedListener(ParentLoginActivity.this);

        editText_parent_number = (TextInputEditText) findViewById(R.id.editText_parent_number);
        editText_parent_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editText_parent_number.setHint(getString(R.string.mobile_number_hint));
                else
                    editText_parent_number.setHint("");
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mSharedPreferences = ParentLoginActivity.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.putString(getString(R.string.PARENT_GIVE_NUMBER), phoneNumber);
                    mEditor.commit();

                    Log.e(TAG, "mobile " + phoneNumber);

                    Toast.makeText(ParentLoginActivity.this, "Now you are logged into " + firebaseAuth.getCurrentUser().getProviderId(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Starting ParentDashboard Activity");
                    startActivity(new Intent(ParentLoginActivity.this, ParentDashboard.class));
                    finish();
                }
            }
        };

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //Called if it is not needed to enter verification code
                signInWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                //incorrect phone number, verification code, emulator, etc.
                Toast.makeText(ParentLoginActivity.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                //now the code has been sent, save the verificationId we may need it
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                Log.e("ParentLoginActivity", mVerificationId);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {
                //called after timeout if onVerificationCompleted has not been called
                super.onCodeAutoRetrievalTimeOut(verificationId);
                Toast.makeText(ParentLoginActivity.this, "onCodeAutoRetrievalTimeOut :" + verificationId, Toast.LENGTH_SHORT).show();
            }
        };

    }

    public void parentLogin(View view) {
        String mobileNumber = editText_parent_number.getText().toString();
        Log.e(TAG, countryCode);
        if (mobileNumber.length() != 11) {
            editText_parent_number.setError(getString(R.string.number_error));
            return;
        } else if(countryCode == null) {
            editText_parent_number.setError(getString(R.string.spinner_error));
        }
        phoneNumber = countryCode + mobileNumber;
        Log.e(TAG, "Mobile " + phoneNumber);
        Toast.makeText(getApplicationContext(), "Request send. Wait a second", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                                                 // Phone number to verify
                60,                                                          // Timeout duration
                TimeUnit.SECONDS,                                            // Unit of timeout
                ParentLoginActivity.this,                                    // Activity (for callback binding)
                mCallbacks
        );
    }

    private void signInWithPhone(PhoneAuthCredential phoneAuthCredential) {
        mFirebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ParentLoginActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ParentLoginActivity.this, "Failed to log in" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        countryCode =  adapterView.getItemAtPosition(i).toString();
        Log.e(TAG, countryCode);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
