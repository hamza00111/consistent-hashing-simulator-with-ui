package com.example.consistenthashing.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final int virtualNodes;

    public ConfigController(@Value("${app.virtual-nodes}") int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    @GetMapping
    public Map<String, Object> getConfig() {
        return Map.of("virtualNodes", virtualNodes);
    }
}
