package in.me.oxff.rakodin.awgdecoder.args;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Getter
@RequiredArgsConstructor
public abstract class ModeSettings {
    private final Mode mode;
    public abstract File getFile();
    public abstract void parse(String [] args) throws SettingsParseError;
}
