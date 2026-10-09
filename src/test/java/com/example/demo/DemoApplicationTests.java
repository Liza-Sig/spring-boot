package com.example.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {
	@BeforeAll
	static void setUp() {
		devapp.start();
		prodapp.start();
	}

	static GenericContainer<?> devapp =
			new GenericContainer<>("devapp")
					.withExposedPorts(8080);

	static GenericContainer<?> prodapp =
			new GenericContainer<>("prodapp")
					.withExposedPorts(8081);

	@Test
	void testDevApp() {
		TestRestTemplate restTemplate = new TestRestTemplate();

		ResponseEntity<String> response = restTemplate.getForEntity(
				"http://" + devapp.getHost() + ":" + devapp.getMappedPort(8080) + "/profile",
				String.class
		);
		assertEquals("Current profile is dev", response.getBody());
	}

	@Test
	void testProdApp() {
		TestRestTemplate restTemplate = new TestRestTemplate();

		ResponseEntity<String> response = restTemplate.getForEntity(
				"http://" + prodapp.getHost() + ":" + prodapp.getMappedPort(8081) + "/profile",
				String.class
		);

		assertEquals("Current profile is production", response.getBody());
	}
}
