package domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import infra.HttpService;

import java.util.*;
import java.util.stream.Collectors;

public class SteamService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Long> getFriendsSteamIds(long steamId) {
        String body = HttpService.downloadUrl("https://api.steampowered.com/ISteamUser/GetFriendList/v1?key=1769487E6958B24CCC9EC9C2DF33EABF&steamid=" + steamId);
        List<Long> steamIds = new ArrayList<>();
        try {
            var iterator = objectMapper.readTree(body).get("friendslist").get("friends").iterator();
            while (iterator.hasNext()) {
                JsonNode friend = iterator.next();
                steamIds.add(Long.valueOf(friend.get("steamid").textValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steamIds;
    }

    public List<String> getPastAliases(long steamId) {
        String body = HttpService.downloadUrl("https://steamcommunity.com/profiles/" + steamId + "/ajaxaliases");
        List<String> result = new ArrayList<>();
        try {
            var iterator = objectMapper.readTree(body).iterator();
            while (iterator.hasNext()) {
                JsonNode friend = iterator.next();
                result.add(friend.get("newname").textValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
