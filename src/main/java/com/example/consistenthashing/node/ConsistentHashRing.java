package com.example.consistenthashing.node;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;

@Component
public class ConsistentHashRing {

    private final int virtualNodes;

    public ConsistentHashRing(@Value("${app.virtual-nodes}") int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    private final SortedMap<Long, String> ring = new TreeMap<>();
    private final Map<String, Node> nodeMap = new ConcurrentHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addNode(String nodeId) {
        lock.writeLock().lock();
        try {
            if (nodeMap.containsKey(nodeId)) {
                return;
            }
            Node node = new Node(nodeId);
            nodeMap.put(nodeId, node);
            for (int i = 0; i < virtualNodes; i++) {
                long hash = HashUtil.hash(nodeId + "#" + i);
                ring.put(hash, nodeId);
            }

            // Reassign keys that should now belong to the new node
            for (Node existingNode : nodeMap.values()) {
                if (!existingNode.getId().equals(nodeId)) {
                    List<String> keysToReassign = new ArrayList<>();
                    for (String key : existingNode.getKeys()) {
                        Node correctNode = getNodeForKey(key);
                        if (correctNode.getId().equals(nodeId)) {
                            keysToReassign.add(key);
                        }
                    }
                    for (String key : keysToReassign) {
                        existingNode.getKeys().remove(key);
                        node.getKeys().add(key);
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeNode(String nodeId) {
        lock.writeLock().lock();
        try {
            if (!nodeMap.containsKey(nodeId)) {
                return;
            }
            List<String> keys = nodeMap.get(nodeId)
                    .getKeys();
            nodeMap.remove(nodeId);
            ring.values().removeIf(nodeId::equals);
            for (String key : keys) {
                Node nodeForKey = getNodeForKey(key);
                nodeForKey.getKeys().add(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Node getNodeForKey(String key) {
        lock.readLock().lock();
        try {
            if (ring.isEmpty()) {
                throw new IllegalStateException("No nodes in ring");
            }
            long hash = HashUtil.hash(key);
            SortedMap<Long, String> tail = ring.tailMap(hash);
            Long nodeHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
            String nodeId = ring.get(nodeHash);
            return nodeMap.get(nodeId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Collection<Node> listNodes() {
        return Collections.unmodifiableCollection(nodeMap.values());
    }

    public SortedMap<Long, String> ringView() {
        return Collections.unmodifiableSortedMap(new TreeMap<>(ring));
    }
}
