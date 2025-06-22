package com.example.consistenthashing.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsistentHashRingTest {

    @Test
    void removeLastNodeWhenSingleNodePresent() {
        ConsistentHashRing ring = new ConsistentHashRing(5);
        ring.addNode("node1");
        Node node = ring.getNodeForKey("k1");
        node.getKeys().add("k1");

        assertDoesNotThrow(() -> ring.removeNode("node1"));
        assertTrue(node.getKeys().isEmpty());
    }

    @Test
    void removeLastNodeAfterSequentialRemovals() {
        ConsistentHashRing ring = new ConsistentHashRing(5);
        ring.addNode("node1");
        ring.addNode("node2");

        Node nodeWithKey = ring.getNodeForKey("k2");
        nodeWithKey.getKeys().add("k2");

        String remainingId = nodeWithKey.getId();
        String firstRemoveId = remainingId.equals("node1") ? "node2" : "node1";

        ring.removeNode(firstRemoveId);
        assertDoesNotThrow(() -> ring.removeNode(remainingId));
        assertTrue(nodeWithKey.getKeys().isEmpty());
    }
}
