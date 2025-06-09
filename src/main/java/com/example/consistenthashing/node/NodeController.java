package com.example.consistenthashing.node;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

@RestController
@RequestMapping("/nodes")
public class NodeController {

    private final ConsistentHashRing ring;

    public NodeController(ConsistentHashRing ring) {
        this.ring = ring;
    }

    @PostMapping
    public ResponseEntity<?> addNode(@RequestParam("id") String id) {
        ring.addNode(id);
        return ResponseEntity.ok(Map.of("message", "Node added", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeNode(@PathVariable("id") String id) {
        ring.removeNode(id);
        return ResponseEntity.ok(Map.of("message", "Node removed", "id", id));
    }

    @GetMapping
    public Collection<Node> list() {
        return ring.listNodes();
    }

    @GetMapping("/ring")
    public SortedMap<Long, String> ring() {
        return ring.ringView();
    }
}
