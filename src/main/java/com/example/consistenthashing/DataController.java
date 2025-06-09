package com.example.consistenthashing;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {

    private final ConsistentHashRing ring;

    public DataController(ConsistentHashRing ring) {
        this.ring = ring;
    }

    @PostMapping
    public Map<String, String> put(@RequestParam("key") String key) {
        Node node = ring.getNodeForKey(key);
        node.getKeys().add(key);
        return Map.of("key", key, "node", node.getId());
    }

    @GetMapping("/{key}")
    public Map<String, String> locate(@PathVariable String key) {
        Node node = ring.getNodeForKey(key);
        return Map.of("key", key, "node", node.getId());
    }
}
