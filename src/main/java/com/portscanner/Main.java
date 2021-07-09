package com.portscanner;

import picocli.CommandLine;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "port-scanner",
    description = "Showing TCP ports bound to a server or service running locally",
    version = "0.1.0",
    showDefaultValues = true,
    mixinStandardHelpOptions = true)
public final class Main implements Callable<Integer> {

    @Option(names = {"-s", "--start"}, description = "Smaller port to search", defaultValue = "0")
    int rangeStart;

    @Option(names = {"-e", "--end"}, description = "Greater port to search", defaultValue = "65355")
    int rangeEnd;

    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_ERROR = 1;

    @Override
    public Integer call() {
        var network = new Network.Local();
        var portScanner = new PortScanner(network);

        var portScannerResult = portScanner.scan(rangeStart, rangeEnd)
            .flatMapMany(PortScanner.Result::getUnavailable)
            .doFirst(() -> System.out.println("Ports unavailable:"))
            .doOnNext(port -> System.out.println("\t* " + port));

        return Mono.from(portScannerResult)
            .then(Mono.just(EXIT_CODE_SUCCESS))
            .onErrorReturn(EXIT_CODE_ERROR)
            .block();
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main())
            .execute(args));
    }
}
