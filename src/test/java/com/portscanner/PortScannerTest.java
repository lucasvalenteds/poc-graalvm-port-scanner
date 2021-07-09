package com.portscanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortScannerTest {

    private static final String IP_ADDRESS = "127.0.1.1";
    private static final int PORT_RANGE_START = 8080;
    private static final int PORT_RANGE_MIDDLE = 8081;
    private static final int PORT_RANGE_END = 8082;

    private final Network network = Mockito.mock(Network.class);
    private final PortScanner portScanner = new PortScanner(network);

    @BeforeEach
    void beforeEach() {
        Mockito.when(network.getIpAddress())
            .thenReturn(Mono.just(IP_ADDRESS));
    }

    @AfterEach
    void afterEach() {
        Mockito.verify(network, Mockito.times(1))
            .getIpAddress();
    }

    @Test
    void testFindingAvailableAndUnavailablePorts() {
        Mockito.when(network.isPortAvailable(IP_ADDRESS, PORT_RANGE_START))
            .thenReturn(Mono.just(false));
        Mockito.when(network.isPortAvailable(IP_ADDRESS, PORT_RANGE_MIDDLE))
            .thenReturn(Mono.just(true));
        Mockito.when(network.isPortAvailable(IP_ADDRESS, PORT_RANGE_END))
            .thenReturn(Mono.just(false));

        StepVerifier.create(portScanner.scan(PORT_RANGE_START, PORT_RANGE_END))
            .assertNext(result -> {
                StepVerifier.create(result.getAvailable())
                    .assertNext(port -> assertEquals(PORT_RANGE_MIDDLE, port))
                    .verifyComplete();


                StepVerifier.create(result.getUnavailable())
                    .assertNext(port -> assertEquals(PORT_RANGE_START, port))
                    .assertNext(port -> assertEquals(PORT_RANGE_END, port))
                    .verifyComplete();
            })
            .verifyComplete();

        Mockito.verify(network, Mockito.times(3))
            .isPortAvailable(Mockito.eq(IP_ADDRESS), Mockito.anyInt());
        Mockito.verify(network, Mockito.times(1))
            .isPortAvailable(Mockito.eq(IP_ADDRESS), Mockito.eq(PORT_RANGE_START));
        Mockito.verify(network, Mockito.times(1))
            .isPortAvailable(Mockito.eq(IP_ADDRESS), Mockito.eq(PORT_RANGE_MIDDLE));
        Mockito.verify(network, Mockito.times(1))
            .isPortAvailable(Mockito.eq(IP_ADDRESS), Mockito.eq(PORT_RANGE_END));
    }
}
