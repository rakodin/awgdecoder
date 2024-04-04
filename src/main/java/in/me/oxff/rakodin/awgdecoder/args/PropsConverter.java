package in.me.oxff.rakodin.awgdecoder.args;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropsConverter {
    static Map<String, String> convert(String[] input, Set<String> keys) {
        var inputList = Arrays.asList(input);
        var out = new HashMap<String, String>();
        keys.forEach(a -> {
            var index = inputList.indexOf(a);
            if (index != -1) {
                try {
                    var val = inputList.get(++index);
                    out.put(a, val);
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        });
        return out;
    }
}
