package com.example.task81candroidappexample;
//Task 8.1C Chat Bot by Hongming 224385035
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

// Main Activity class for the chat app
public class MainActivity extends AppCompatActivity {

    // UI elements for login screen
    private LinearLayout loginSection;    
    private EditText usernameInput;     
    private Button loginButton;         
    
    // UI elements for chat screen
    private LinearLayout chatSection;     
    private LinearLayout messagesContainer; 
    private EditText chatInputBox;        
    private Button sendButton;            
    private ProgressBar progressBar;      
    
    // User information
    private String username;              

    // To setup UI parts and button clicks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To find login screen parts
        loginSection = findViewById(R.id.loginSection);       
        usernameInput = findViewById(R.id.usernameInput);      
        loginButton = findViewById(R.id.loginButton);         

        // To find chat screen parts
        chatSection = findViewById(R.id.chatSection);          
        messagesContainer = findViewById(R.id.messagesContainer);
        chatInputBox = findViewById(R.id.chatInputBox);        
        sendButton = findViewById(R.id.sendButton);             
        progressBar = findViewById(R.id.progressBar);         

        // Set button clicks
        loginButton.setOnClickListener(v -> login());
        sendButton.setOnClickListener(v -> sendMessage());
    }

    // To handle user login
    private void login() {
        // Get username
        username = usernameInput.getText().toString().trim();
        
        // Check if username is empty
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Switch screens
        loginSection.setVisibility(View.GONE);
        chatSection.setVisibility(View.VISIBLE);
        
        // Show welcome message
        addBotMessage("Welcome " + username + "!");
    }

    // To send message to chatbot
    private void sendMessage() {
        // Get message text
        String userMessage = chatInputBox.getText().toString().trim();
        
        // Check if the input is empty
        if (userMessage.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to chat and clear input
        addUserMessage(userMessage);
        chatInputBox.setText("");

        // Show loading
        progressBar.setVisibility(View.VISIBLE);

        // Send it to server
        String url = "http://10.0.2.2:5000/chat"; 
        
        StringRequest request = new StringRequest(
                Request.Method.POST, 
                url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    // Show response
                    String botMessage = response.trim();
                    addBotMessage(botMessage);
                },
                error -> {
                    // Handle error
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error connecting to server", Toast.LENGTH_LONG).show();
                    addBotMessage("Sorry, I'm unable to respond right now.");
                }
        ) {
            // Add message to request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userMessage", userMessage);
                return params;
            }
        };

        // Set the timeout (30 seconds)
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Send request
        Volley.newRequestQueue(this).add(request);
    }

    // To add user message bubble
    private void addUserMessage(String message) {
        // To create message card
        CardView cardView = new CardView(this);
        
        // Set card style
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.gravity = Gravity.END;
        cardParams.setMargins(50, 10, 10, 10);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(8);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        cardView.setContentPadding(15, 10, 15, 10);

        // Add message text
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        cardView.addView(textView);
        
        // Create user badge
        TextView userIndicator = new TextView(this);
        userIndicator.setText("U");
        userIndicator.setTextSize(12);
        userIndicator.setTextColor(ContextCompat.getColor(this, R.color.black));
        userIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green));
        userIndicator.setPadding(10, 5, 10, 5);
        userIndicator.setGravity(Gravity.CENTER);
        
        // Set badge position
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        indicatorParams.gravity = Gravity.END;
        indicatorParams.setMargins(0, 5, 10, 0);
        userIndicator.setLayoutParams(indicatorParams);
        
        // Add to chat
        messagesContainer.addView(userIndicator);
        messagesContainer.addView(cardView);
    }

    // To add bot message bubble
    private void addBotMessage(String message) {
        // Create bot badge
        TextView botIndicator = new TextView(this);
        botIndicator.setText("🤖");
        botIndicator.setTextSize(16);
        
        // Set badge position
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        indicatorParams.gravity = Gravity.START;
        indicatorParams.setMargins(10, 5, 0, 0);
        botIndicator.setLayoutParams(indicatorParams);
        
        // Create message card
        CardView cardView = new CardView(this);
        
        // Set card style
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.gravity = Gravity.START;
        cardParams.setMargins(10, 10, 50, 20);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(8);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_green_bg));
        cardView.setContentPadding(15, 10, 15, 10);

        // Add message text
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        cardView.addView(textView);
        
        // Add to chat
        messagesContainer.addView(botIndicator);
        messagesContainer.addView(cardView);
    }
}