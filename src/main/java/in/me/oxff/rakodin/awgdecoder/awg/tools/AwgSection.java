package in.me.oxff.rakodin.awgdecoder.awg.tools;

public enum AwgSection {
    Interface,
    Peer;
    public static String getSectionDef(AwgSection section) {
        return String.format("[%s]", section.name());
    }

}
