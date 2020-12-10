package domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** Maps country codes such as GB or GER to "United Kingdom" and "Germany". **/
public class CountryCodeService {
    private static final Map<String, String> countries = new HashMap<>();

    static {
        var scanner = new Scanner(CountryCodeService.class.getResourceAsStream("/country_codes.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] split = line.split("\t");
            if (split.length >= 2) {
                String[] split2 = split[2].split(" / ");
                countries.put(split2[0], split[0]);
                countries.put(split2[1], split[0]);
            }
        }
    }

    public static String getCountry(String countryCode) {
        return countries.getOrDefault(countryCode, countryCode);
    }
}
