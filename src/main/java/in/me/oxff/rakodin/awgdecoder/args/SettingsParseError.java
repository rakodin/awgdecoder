package in.me.oxff.rakodin.awgdecoder.args;

import lombok.Getter;

@Getter
public class SettingsParseError extends Exception {
    private final String error;

    public SettingsParseError(String error) {
        super(error);
        this.error = error;
    }
}
