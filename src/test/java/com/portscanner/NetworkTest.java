package com.portscanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkTest {

    private static final String TEST_IP = "127.0.1.1";
    private static final int TEST_PORT_AVAILABLE = 4001;
    private static final int TEST_PORT_UNAVAILABLE = 4002;

    @Test
    void testFindingLocalhostIpAddress() {
        StepVerifier.create(Network.discoverLocalIpAddress())
            .assertNext(ip -> assertEquals(TEST_IP, ip))
            .verifyComplete();
    }

    @Test
    void testFindingUnavailablePort() throws IOException {
        var socket = new ServerSocket(TEST_PORT_UNAVAILABLE);

        StepVerifier.create(Network.isPortAvailable(TEST_IP, socket.getLocalPort()))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        socket.close();
    }

    @Test
    @EnabledIf("com.portscanner.NetworkTest#isTestPortAvailable")
    void testFindingAvailablePort() {
        StepVerifier.create(Network.isPortAvailable(TEST_IP, TEST_PORT_AVAILABLE))
            .assertNext(Assertions::assertFalse)
            .verifyComplete();
    }


    @SuppressWarnings("unused")
    private boolean isTestPortAvailable() {
        try (var ignored = new Socket(TEST_IP, TEST_PORT_AVAILABLE)) {
            ignored.close();
            return false;
        } catch (IOException exception) {
            return true;
        }
    }
}
