package buddy.fuzzy.walmart.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

import static buddy.fuzzy.walmart.Main.config;

public class Util {
    public static Role findRole(Member member, String id) {
        List<Role> roles = member.getRoles();

        return roles.stream()
                .filter(role -> role.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
