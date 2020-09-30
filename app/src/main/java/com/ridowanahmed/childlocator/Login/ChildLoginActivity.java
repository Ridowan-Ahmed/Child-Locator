package com.ridowanahmed.childlocator.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ridowanahmed.childlocator.Dashboard.ChildDashboard;
import com.ridowanahmed.childlocator.R;

import java.util.concurrent.TimeUnit;


public class ChildLoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "ChildLoginActivity";
    private LinearLayout linearLayout_inputNumber, linearLayout_inputCode;
    private TextInputEditText editText_child_name, editText_child_number, editText_child_code;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private String childName, phoneNumber, countryCode, mobileNumber;

    SharedPreferences mSharedPreferences;
    private Spinner spinnerChild;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        linearLayout_inputNumber = (LinearLayout) findViewById(R.id.input_number);
        linearLayout_inputCode = (LinearLayout) findViewById(R.id.input_code);
        editText_child_name = (TextInputEditText)findViewById(R.id.editText_child_name);
        editText_child_number = (TextInputEditText)findViewById(R.id.editText_child_number);
        editText_child_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editText_child_number.setHint(getString(R.string.mobile_number_hint));
                else
                    editText_child_number.setHint("");
            }
        });
        editText_child_code = (TextInputEditText)findViewById(R.id.editText_child_code);
        spinnerChild = (Spinner) findViewById(R.id.child_countryCode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.country, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChild.setAdapter(adapter);
        spinnerChild.setOnItemSelectedListener(ChildLoginActivity.this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mSharedPreferences = ChildLoginActivity.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.putString(getString(R.string.CHILD_NAME), childName);
                    mEditor.putString(getString(R.string.CHILD_GIVE_NUMBER), phoneNumber);
                    mEditor.commit();

                    Toast.makeText(ChildLoginActivity.this, "Now you are logged in " + firebaseAuth.getCurrentUser().getProviderId(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChildLoginActivity.this, ChildDashboard.class);
                    Log.e("ChildLoginActivity", "Starting Dashboard Activity");
                    startActivity(intent);
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
                Toast.makeText(ChildLoginActivity.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChildLoginActivity.this, "onCodeAutoRetrievalTimeOut :" + verificationId, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void requestCode(View view) {
        childName = editText_child_name.getText().toString().trim();
        mobileNumber = editText_child_number.getText().toString();
        if(TextUtils.isEmpty(childName)) {
            editText_child_name.setError(getString(R.string.name_error));
            return;
        } else if (mobileNumber.length() != 11) {
            editText_child_number.setError(getString(R.string.number_error));
            return;
        }
        phoneNumber = countryCode + mobileNumber;
        Toast.makeText(getApplicationContext(), "Request send. Wait a second", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                ChildLoginActivity.this,
                mCallbacks
        );

        linearLayout_inputNumber.setVisibility(View.GONE);
        linearLayout_inputCode.setVisibility(View.VISIBLE);
    }
    private void signInWithPhone(PhoneAuthCredential phoneAuthCredential) {
        mFirebaseAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChildLoginActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChildLoginActivity.this, "Failed to log in " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void childLogin(View view){
        String code = editText_child_code.getText().toString();
        if (TextUtils.isEmpty(code)) {
            editText_child_code.setError("Phone code can't empty");
            return;
        }
        signInWithPhone(PhoneAuthProvider.getCredential(mVerificationId, code));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Destroyed");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        countryCode = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}