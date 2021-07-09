package com.portscanner;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.InetAddress;
import java.net.Socket;

public final class Network {

    public static Mono<String> discoverLocalIpAddress() {
        return Mono.fromCallable(InetAddress::getLocalHost)
            .map(InetAddress::getHostAddress)
            .publishOn(Schedulers.boundedElastic());
    }

    public static Mono<Boolean> isPortAvailable(final String ip, final int port) {
        return Mono.fromCallable(() -> new Socket(ip, port))
            .then(Mono.just(true))
            .onErrorReturn(false)
            .publishOn(Schedulers.boundedElastic());
    }
}
