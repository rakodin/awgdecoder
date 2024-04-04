package in.me.oxff.rakodin.awgdecoder;

import in.me.oxff.rakodin.awgdecoder.args.ArgumentParser;
import in.me.oxff.rakodin.awgdecoder.decoder.Decoder;
import in.me.oxff.rakodin.awgdecoder.args.DecodeSettings;
import in.me.oxff.rakodin.awgdecoder.args.EncodeSettings;
import in.me.oxff.rakodin.awgdecoder.decoder.Encoder;

@SuppressWarnings("rawtypes")
public class Main {
    public static void main(String[] args) {
        var arguments = ArgumentParser.fromArgs(args);
        if (arguments.hasErrors()) {
            System.out.println(arguments.getError());
            System.exit(1);
        }
        Processor processor = null;
        switch (arguments.getMode()) {
            case decode -> processor = new Decoder((DecodeSettings) arguments.getModeSettings());
            case encode -> processor = new Encoder((EncodeSettings) arguments.getModeSettings());
        }
        if (processor != null) {
            System.out.println(processor.process());
        } else {
            System.out.println("Sorry, no output");
            System.exit(1);
        }
    }
}
