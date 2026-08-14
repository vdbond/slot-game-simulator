package com.vdbond;

public record CliOptions(long rounds, String configResource) {

    private static final long DEFAULT_ROUNDS = 10_000_000L;
    private static final String DEFAULT_CONFIG_RESOURCE = "game-config.json";

    public static CliOptions parse(String[] args) {
        long rounds = DEFAULT_ROUNDS;
        String configResource = DEFAULT_CONFIG_RESOURCE;

        int i = 0;
        while (i < args.length) {
            String flag = args[i];
            switch (flag) {
                case "--rounds" -> {
                    rounds = parseRounds(requireValue(args, i, flag));
                    i += 2;
                }
                case "--config" -> {
                    configResource = requireValue(args, i, flag);
                    i += 2;
                }
                default -> throw new IllegalArgumentException("Unknown argument: " + flag);
            }
        }

        return new CliOptions(rounds, configResource);
    }

    private static String requireValue(String[] args, int index, String flag) {
        if (index + 1 >= args.length) {
            throw new IllegalArgumentException(flag + " requires a value");
        }
        return args[index + 1];
    }

    private static long parseRounds(String value) {
        long rounds;
        try {
            rounds = Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("--rounds must be a positive integer: " + value, e);
        }
        if (rounds <= 0) {
            throw new IllegalArgumentException("--rounds must be positive: " + rounds);
        }
        return rounds;
    }

}
