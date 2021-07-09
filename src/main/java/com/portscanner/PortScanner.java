package com.portscanner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;

public final class PortScanner {

    public static final class Result {

        private final Collection<Integer> available;
        private final Collection<Integer> unavailable;

        Result(Collection<Integer> available, Collection<Integer> unavailable) {
            this.available = available;
            this.unavailable = unavailable;
        }

        public Flux<Integer> getAvailable() {
            return Flux.fromIterable(available);
        }

        public Flux<Integer> getUnavailable() {
            return Flux.fromIterable(unavailable);
        }
    }

    private final Network network;

    public PortScanner(Network network) {
        this.network = network;
    }

    public Mono<PortScanner.Result> scan(int rangeStart, int rangeEnd) {
        var amountOfPortsToScan = rangeEnd - rangeStart + 1;

        return Mono.from(network.getIpAddress())
            .flatMap(ip ->
                Flux.range(rangeStart, amountOfPortsToScan)
                    .flatMap(port -> Mono.zip(network.isPortAvailable(ip, port), Mono.just(port)))
                    .collectMultimap(Tuple2::getT1, Tuple2::getT2)
            )
            .flatMap(map -> Mono.just(new PortScanner.Result(map.get(true), map.get(false))));
    }
}
