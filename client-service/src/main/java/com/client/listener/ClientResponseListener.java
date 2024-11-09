package com.client.listener;

import org.springframework.stereotype.Component;

@Component
public class ClientResponseListener {

    public void handleMessage(String message) {
        System.out.println("Received message: " + message);
    }
}

