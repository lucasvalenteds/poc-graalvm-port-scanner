package com.portscanner;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.InetAddress;
import java.net.Socket;

public abstract class Network {

    public static final class Local extends Network {

        public Mono<String> getIpAddress() {
            return Mono.fromCallable(InetAddress::getLocalHost)
                .map(InetAddress::getHostAddress)
                .publishOn(Schedulers.boundedElastic());
        }
    }

    public abstract Mono<String> getIpAddress();

    public Mono<Boolean> isPortAvailable(final String ip, final int port) {
        return Mono.fromCallable(() -> new Socket(ip, port))
            .then(Mono.just(false))
            .onErrorReturn(true)
            .publishOn(Schedulers.boundedElastic());
    }
}
