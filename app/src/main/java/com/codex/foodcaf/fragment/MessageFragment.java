package com.codex.foodcaf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.MessageAdapter;
import com.codex.foodcaf.model.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    public static boolean isChatOpen = false;
    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private FloatingActionButton btnSend;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId;
    private final String ADMIN_ID = "UO6OFTZdtaRAiWUJLD5TiJIuONj2";


    public MessageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessageInput = view.findViewById(R.id.etMessageInput);
        btnSend = view.findViewById(R.id.btnSend);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
            setupRecyclerView();
            loadMessages();
        } else {
            Toast.makeText(getContext(), "Please login to chat", Toast.LENGTH_SHORT).show();
        }

        btnSend.setOnClickListener(v -> {
            String msgText = etMessageInput.getText().toString().trim();
            if (!msgText.isEmpty()) {
                sendMessage(msgText);
            }
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);

                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.bottom_nav_home);
                } else {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new HomeFragment())
                            .commit();
                }
            }
        });

    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
    }

    private void sendMessage(String msgText) {
        long timestamp = System.currentTimeMillis();

        Message message = new Message();
        message.setSenderId(currentUserId);
        message.setReceiverId(ADMIN_ID);
        message.setMessageText(msgText);
        message.setTimestamp(timestamp);

        db.collection("chats").document(currentUserId).collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    etMessageInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to send", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadMessages() {
        db.collection("chats").document(currentUserId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        List<Message> messageList = new ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            Message msg = doc.toObject(Message.class);
                            messageList.add(msg);
                        }

                        MessageAdapter adapter = new MessageAdapter(messageList, currentUserId);
                        rvMessages.setAdapter(adapter);

                        if (!messageList.isEmpty()) {
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        }

                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        isChatOpen = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isChatOpen = false;
    }
}