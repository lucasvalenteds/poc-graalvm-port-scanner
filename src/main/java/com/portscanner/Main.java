package com.portscanner;

import picocli.CommandLine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "port-scanner",
    description = "Listing TCP ports indicating whether it has a server attached to it or not.",
    version = "0.1.0",
    synopsisHeading = "%n@|bold USAGE|@%n%n",
    descriptionHeading = "%n@|bold DESCRIPTION |@%n%n",
    optionListHeading = "%n@|bold OPTIONS |@%n%n",
    commandListHeading = "%n@|bold COMMANDS |@%n%n",
    sortOptions = false,
    showDefaultValues = true,
    mixinStandardHelpOptions = true)
public final class Main implements Callable<Integer> {

    @Option(
        names = "--range-start",
        description = "Smaller port to search.",
        defaultValue = "0",
        order = -6)
    int rangeStart;

    @Option(
        names = "--range-end",
        description = "Greater port to search.",
        defaultValue = "65355",
        order = -5)
    int rangeEnd;

    @Option(
        names = "--show-available",
        description = "Show available ports after unavailable ports.",
        defaultValue = "false",
        order = -4)
    boolean showAvailable;

    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_ERROR = 1;

    @Override
    public Integer call() {
        var network = new Network.Local();
        var portScanner = new PortScanner(network);
        var report = new Report.Console(System.out);

        var portScannerResult = portScanner.scan(rangeStart, rangeEnd)
            .doFirst(() -> report.printPortRange(rangeStart, rangeEnd))
            .delayUntil(result ->
                Flux.from(result.getUnavailable())
                    .doFirst(report::printUnavailableHeader)
                    .doOnNext(report::printUnavailablePort)
            )
            .filter(result -> showAvailable)
            .delayUntil(result ->
                Flux.from(result.getAvailable())
                    .doFirst(report::printAvailableHeader)
                    .doOnNext(report::printAvailablePort)
            );

        return Mono.from(portScannerResult)
            .then(Mono.just(EXIT_CODE_SUCCESS))
            .onErrorReturn(EXIT_CODE_ERROR)
            .doOnTerminate(report::clearOutputBuffer)
            .block();
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main())
            .execute(args));
    }
}
