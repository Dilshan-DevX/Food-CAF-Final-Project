package com.codex.foodcaf.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.codex.foodcaf.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FogotPassActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton    btnSendReset;
    private ProgressBar       progressBar;
    private TextView          tvMessage;


    private static final String FIREBASE_API_KEY = "AIzaSyBS5F6d9s6dOTa-4c4sieDuObkgMCjVVNg";

    private static final String RESET_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key="
                    + FIREBASE_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fogot_pass);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail     = findViewById(R.id.etForgotEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        progressBar = findViewById(R.id.progressForgot);
        tvMessage   = findViewById(R.id.tvForgotMessage);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.tvBackToSignIn).setOnClickListener(v -> finish());

        btnSendReset.setOnClickListener(v -> {
            String email = etEmail.getText() != null
                    ? etEmail.getText().toString().trim() : "";

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email address");
                etEmail.requestFocus();
                return;
            }

            sendPasswordResetEmail(email);
        });
    }

    private static final String TAG = "ForgotPassDebug";

    // ✅ OkHttp use karala Firebase Auth REST API ekata password reset request yawanawa
    private void sendPasswordResetEmail(String email) {
        setBusy(true);
        hideMessage();

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("requestType", "PASSWORD_RESET");
            jsonBody.put("email", email);
        } catch (Exception e) {
            setBusy(false);
            showMessage("Something went wrong. Please try again.", false);
            return;
        }

        Log.d(TAG, "▶ Sending reset to: " + email);
        Log.d(TAG, "▶ URL: " + RESET_URL);
        Log.d(TAG, "▶ Body: " + jsonBody.toString());

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(RESET_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "✖ Network failure: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    setBusy(false);
                    showMessage("Network error: " + e.getMessage(), false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null
                        ? response.body().string() : "";

                Log.d(TAG, "◀ HTTP code: " + response.code());
                Log.d(TAG, "◀ Response body: " + responseBody);

                runOnUiThread(() -> {
                    setBusy(false);

                    if (response.isSuccessful()) {
                        Log.d(TAG, "✔ Email sent successfully!");
                        showMessage(
                                "✓ Password reset email sent to " + email
                                        + "\n\nCheck your inbox and follow the link.",
                                true);
                        btnSendReset.setEnabled(false);
                        btnSendReset.setText("Email Sent ✓");
                    } else {
                        Log.e(TAG, "✖ Firebase error response: " + responseBody);
                        String errorMsg = parseFirebaseError(responseBody);
                        showMessage(errorMsg, false);
                    }
                });
            }
        });
    }

    // ── Parse Firebase error message ──────────────────────────────────────────
    private String parseFirebaseError(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            String code = json.getJSONObject("error").getString("message");
            Log.e(TAG, "✖ Firebase error code: " + code);

            switch (code) {
                case "EMAIL_NOT_FOUND":
                    return "No account found with this email address.";
                case "INVALID_EMAIL":
                    return "Invalid email address format.";
                case "TOO_MANY_ATTEMPTS_TRY_LATER":
                    return "Too many attempts. Please try again later.";
                case "USER_DISABLED":
                    return "This account has been disabled.";
                case "API_KEY_INVALID":
                case "INVALID_API_KEY":
                    return "Configuration error. Please contact support.";
                // ✅ New Firebase REST API v2 error format
                default:
                    if (code.contains("INVALID_LOGIN_CREDENTIALS") || code.contains("EMAIL_NOT_FOUND")) {
                        return "No account found with this email address.";
                    }
                    return "Error: " + code;
            }
        } catch (Exception e) {
            Log.e(TAG, "✖ Failed to parse error: " + responseBody);
            return "Something went wrong. Please try again.\n\nDetails: " + responseBody;
        }
    }

    private void setBusy(boolean busy) {
        btnSendReset.setEnabled(!busy);
        progressBar.setVisibility(busy ? View.VISIBLE : View.GONE);
        etEmail.setEnabled(!busy);
    }

    private void showMessage(String msg, boolean isSuccess) {
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(msg);
        tvMessage.setTextColor(isSuccess
                ? Color.parseColor("#2EBA63")   // green — success
                : Color.parseColor("#F44336")); // red — error
    }

    private void hideMessage() {
        tvMessage.setVisibility(View.GONE);
        tvMessage.setText("");
    }
}