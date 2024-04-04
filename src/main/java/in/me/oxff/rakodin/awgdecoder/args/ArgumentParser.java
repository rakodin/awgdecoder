package in.me.oxff.rakodin.awgdecoder.args;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Builder
@Getter
public class ArgumentParser {
    private final Mode mode;
    @Setter
    private ModeSettings modeSettings;
    private String error;

    private final static String usage = """
            AmneziaWG config file (en)(de)coder
            Usage: awgdecoder <decode|encode> <options>
            Options:
                decode:
                  -i input file   : required (file in 'vpn://' format)
                  -of <conf|dump> : optional output format:
                           conf: awg config (default)
                           dump: full AmneziaWG json dump
                Example:
                    to get AmneziaWG config in awg conf format execute
                    awgdecoder decode -i ./myConfig.vpn -f conf > myawg0.conf
                encode:
                    -i input file   : required (AmneziaWG awg config)
                    -d dns1:dns2    : optional (dns1 dns2 for vpn://). default: 1.1.1.1:1.0.0.1
                    -of <conf|dump> : optional output format:
                             conf: AmneziaWG full config (vpn://...) (default)
                             dump: full AmneziaWG json dump
                 Example:
                    to convert AmneziaWG awg config into vpn:// link execute
                    awgdecoder encode -f ./myawg0.conf -d 8.8.8.8:4.4.4.4 > myNewConfig.vpn
            """;

    public boolean hasErrors() {
        return error != null && !error.isBlank();
    }
    private static final ArgumentParser ERROR_ARGUMENT_PARSER = ArgumentParser.builder().mode(null).error(usage).build();

    @SneakyThrows(SettingsParseError.class)
    public static ArgumentParser fromArgs(String[] args) {
        var ret = new AtomicReference<ArgumentParser>(ERROR_ARGUMENT_PARSER);
        if (args != null && args.length > 0) {
            var mode = args[0];
            var res = Arrays.stream(Mode.values()).filter(m -> m.name().equals(mode))
                    .map(m -> ArgumentParser.builder().mode(m).build())
                    .findFirst()
                    .orElseGet(() -> ret.get());
            if (!res.hasErrors() && res.getMode() != null) {
                ModeSettings modeSettings = null;
                switch (res.getMode()) {
                    case decode -> modeSettings = new DecodeSettings(res.getMode());
                    case encode -> modeSettings = new EncodeSettings(res.getMode());
                }
                if (modeSettings != null) {
                    modeSettings.parse(args);
                    res.setModeSettings(modeSettings);
                }
            }
            ret.set(res);
        }
        return ret.get();
    }
}
