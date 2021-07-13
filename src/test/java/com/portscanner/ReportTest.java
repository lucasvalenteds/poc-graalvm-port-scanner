package com.portscanner;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportTest {

    private final OutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream printStream = new PrintStream(outputStream);
    private final Report report = new Report.Console(printStream);

    @Test
    void testPrintingPortRange() {
        var rangeStart = 8080;
        var rangeEnd = 9090;

        report.printPortRange(rangeStart, rangeEnd);

        assertThat(outputStream.toString())
            .contains("Scanning ports from")
            .contains(String.valueOf(rangeStart))
            .contains(" to ")
            .contains(String.valueOf(rangeEnd))
            .contains(" ...");
    }

    @Test
    void testPrintingAvailablePorts() {
        var port1 = 8080;
        var port2 = 8081;
        var port3 = 8082;
        var ports = List.of(port1, port2, port3);

        report.printAvailableHeader();
        ports.forEach(report::printAvailablePort);

        assertThat(outputStream.toString())
            .contains("Available ports:")
            .contains(List.of(
                String.valueOf(port1),
                String.valueOf(port2),
                String.valueOf(port3)
            ));
    }

    @Test
    void testPrintingUnavailablePorts() {
        var port1 = 9080;
        var port2 = 9081;
        var port3 = 9082;
        var ports = List.of(port1, port2, port3);

        report.printUnavailableHeader();
        ports.forEach(report::printUnavailablePort);

        assertThat(outputStream.toString())
            .contains("Unavailable ports:")
            .contains(List.of(
                String.valueOf(port1),
                String.valueOf(port2),
                String.valueOf(port3)
            ));
    }
}
