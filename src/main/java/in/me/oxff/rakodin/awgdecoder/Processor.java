package in.me.oxff.rakodin.awgdecoder;

import in.me.oxff.rakodin.awgdecoder.args.ModeSettings;

public abstract class Processor <X extends ModeSettings> {
    private X settings;
    public Processor(X settings) {
        this.settings = settings;
    }

    public X getSettings() {
        return settings;
    }

    public abstract String process();
}
