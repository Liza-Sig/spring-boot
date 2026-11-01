
package com.example.demo;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private final AtomicInteger requestCount = new AtomicInteger();

    private final List<byte[]> memoryBlocks = new ArrayList<>();

    private static final int MAX_MEMORY_MB = 120;

    private int allocatedMemoryMb = 0;

    public MonitoringController(MeterRegistry registry) {
        registry.gauge("monitoring_requests_total", requestCount);
    }

    @GetMapping("/success")
    public ResponseEntity<String> success() {
        requestCount.incrementAndGet();
        return ResponseEntity.ok("Request successful");
    }

    @GetMapping("/not-found")
    public ResponseEntity<String> notFound() {
        requestCount.incrementAndGet();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resource not found");
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        requestCount.incrementAndGet();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

    @PostMapping("/memory/allocate")
    public synchronized ResponseEntity<String> allocateMemory(
            @RequestParam(defaultValue = "20") int mb) {

        if (mb <= 0 || allocatedMemoryMb + mb > MAX_MEMORY_MB) {
            return ResponseEntity.badRequest()
                    .body("Requested memory exceeds the allowed limit of 120 MB");
        }

        byte[] memoryBlock = new byte[mb * 1024 * 1024];
        Arrays.fill(memoryBlock, (byte) 1);

        memoryBlocks.add(memoryBlock);
        allocatedMemoryMb += mb;

        return ResponseEntity.ok("Allocated " + mb
                + " MB. Total allocated: " + allocatedMemoryMb + " MB");
    }

    @PostMapping("/memory/clear")
    public synchronized ResponseEntity<String> clearMemory() {
        memoryBlocks.clear();
        allocatedMemoryMb = 0;

        return ResponseEntity.ok("Allocated memory cleared");
    }
}
