package com.example.consistenthashing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Node {

    private final String id;
    private final List<String> keys = new CopyOnWriteArrayList<>();

    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<String> getKeys() {
        return keys;
    }
}
