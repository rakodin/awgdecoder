package in.me.oxff.rakodin.awgdecoder.awg.tools;

import lombok.SneakyThrows;
import in.me.oxff.rakodin.awgdecoder.awg.AwgLastConfig;
import net.moznion.wireguard.keytool.InvalidPrivateKeyException;
import net.moznion.wireguard.keytool.WireGuardKey;

import java.util.Arrays;
import java.util.Scanner;

public class ConfigReader {

    public static AwgLastConfig convert(String config) {
        var scanner = new Scanner(config);
        var ret = new AwgLastConfig();
        boolean inInterface = false;
        boolean inPeer = false;
        while (scanner.hasNextLine()) {
            var ln = scanner.nextLine();
            if (inInterface) {
                readInterfaceLine(ln, ret);
            } else if (inPeer) {
                readPeerLine(ln, ret);
            }
            if (ln.trim().startsWith(AwgSection.getSectionDef(AwgSection.Interface))) {
                inInterface = true;
                inPeer = false;
            } else if (ln.startsWith(AwgSection.getSectionDef(AwgSection.Peer))) {
                inInterface = false;
                inPeer = true;
            }
        }
        ret.setConfig(config);
        return ret;
    }

    private static void readPeerLine(String line, AwgLastConfig into) {
        final var l = line.trim();
        Arrays.stream(AwgPeer.values())
                .filter(v -> l.startsWith(v.name()))
                .findFirst().ifPresent(v -> {
                    var value = getVal(l).trim();
                    switch(v) {
                        case Endpoint -> {
                            var ep = value.split(":");
                            into.setHostName(ep[0]);
                            into.setPort(Integer.parseInt(ep[1]));
                        }
                        case PresharedKey -> into.setPskKey(value);
                        case PublicKey -> into.setServerPubKey(value);
                    }
                });
    }

    private static void readInterfaceLine(String line, AwgLastConfig into) {
        final var l = line.trim();
        Arrays.stream(AwgInterface.values())
                .filter(v -> l.startsWith(v.name()))
                .findFirst()
                .ifPresent(v -> {
                    var value = getVal(l).trim();
                    switch (v) {
                        case H1 -> into.setH1(value);
                        case H2 -> into.setH2(value);
                        case H3 -> into.setH3(value);
                        case H4 -> into.setH4(value);
                        case Jc -> into.setJc(value);
                        case Jmin -> into.setJmin(value);
                        case Jmax -> into.setJmax(value);
                        case S1 -> into.setS1(value);
                        case S2 -> into.setS2(value);
                        case Address -> into.setClientIp(value.split("/")[0]);
                        case PrivateKey -> {
                            into.setClientPrivKey(value);
                            into.setClientPubKey(getPubKey(value));
                        }
                    }
                });
    }

    @SneakyThrows({InvalidPrivateKeyException.class})
    private static String getPubKey(String privKey) {
        var wKey = new WireGuardKey(privKey);
        return wKey.getBase64PublicKey();
    }

    private static String getVal(String keyValueString) {
        return keyValueString.replaceFirst("^[^=]+=","");
    }
}
