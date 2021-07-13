package com.portscanner;

import picocli.CommandLine;

import java.io.PrintStream;

public abstract class Report {

    public static final class Console extends Report {

        private final PrintStream stream;

        public Console(PrintStream stream) {
            this.stream = stream;
        }

        public void printPortRange(int rangeStart, int rangeEnd) {
            this.stream.println(
                CommandLine.Help.Ansi.Style.reset.on()
                    + "Scanning ports from "
                    + CommandLine.Help.Ansi.Style.bold.on() + rangeStart
                    + CommandLine.Help.Ansi.Style.reset.on() + " to "
                    + CommandLine.Help.Ansi.Style.bold.on() + rangeEnd
                    + CommandLine.Help.Ansi.Style.reset.on() + " ..."
            );
        }

        public void printAvailableHeader() {
            this.stream.println("\n" + CommandLine.Help.Ansi.Style.fg_white.on() + "Available ports:");
        }

        public void printUnavailableHeader() {
            this.stream.println("\n" + CommandLine.Help.Ansi.Style.fg_white.on() + "Unavailable ports:");
        }

        public void printAvailablePort(int port) {
            this.stream.println(
                CommandLine.Help.Ansi.Style.reset.on()
                    + "\t* " + CommandLine.Help.Ansi.Style.fg_green.on() + port
            );
        }

        public void printUnavailablePort(int port) {
            this.stream.println(
                CommandLine.Help.Ansi.Style.reset.on()
                    + "\t* " + CommandLine.Help.Ansi.Style.fg_red.on() + port
            );
        }

        public void clearOutputBuffer() {
            this.stream.println(CommandLine.Help.Ansi.Style.reset.on());
        }
    }

    abstract void printPortRange(int rangeStart, int rangeEnd);

    abstract void printAvailableHeader();

    abstract void printUnavailableHeader();

    abstract void printAvailablePort(int port);

    abstract void printUnavailablePort(int port);

    abstract void clearOutputBuffer();
}
