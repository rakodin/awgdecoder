package in.me.oxff.rakodin.awgdecoder.args;

import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.Map;

@Getter
@ToString(callSuper = true)
public class DecodeSettings extends ModeSettings {
    private boolean dump = false;
    private File file;

    private final static Map<String, String> args =
            Map.of("-i", "Input file is not set",
                    "-of", "Invalid output format");
    public DecodeSettings(Mode mode) {
        super(mode);
    }

    @Override
    public void parse(String[] input) throws SettingsParseError {
        if (input.length < 3) {
            throw new SettingsParseError(
                    String.format("Invalid decode arguments: %s", String.join(", ", args.values())));
        }
        //split input to key-value
        var out = PropsConverter.convert(input, args.keySet());
        var fileName = out.get("-i");
        if (fileName == null) {
            throw new SettingsParseError(String.format("Invalid decode arguments: %s", args.get("-i")));
        }
        file = new File(fileName);
        if (!file.isFile() && !file.canRead()) {
            throw new SettingsParseError(String.format("Invalid decode arguments: can't read input file %s", fileName));
        }
        dump = out.getOrDefault("-of", "conf").equalsIgnoreCase("dump");
    }
}
