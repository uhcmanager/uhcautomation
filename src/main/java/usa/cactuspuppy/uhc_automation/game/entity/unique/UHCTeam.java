package usa.cactuspuppy.uhc_automation.game.entity.unique;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamAddGroupsEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamCreateEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamDeleteEvent;
import usa.cactuspuppy.uhc_automation.event.game.team.TeamRemoveGroupsEvent;
import usa.cactuspuppy.uhc_automation.game.types.TeamGameInstance;

import java.util.*;

public class UHCTeam extends UniqueEntity {
    private static Map<Integer, UHCTeam> teamNumMap = new LinkedHashMap<>();
    private static int nextTeamNum = 0;

    @Getter private int teamNumber;
    @Getter private List<Group> groups;
    @Getter private String name;
    @Getter @Setter private ChatColor color = ChatColor.WHITE;

    //Additional options
    @Getter @Setter private String displayName;
    @Getter @Setter private boolean allowFriendlyFire = true;
    @Getter @Setter private boolean canSeeFriendlyInvisibles = true;
    private EnumMap<Team.Option, Team.OptionStatus> optionsMap = new EnumMap<>(Team.Option.class);

    private void initOptionsMap() {
        optionsMap.put(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
        optionsMap.put(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.ALWAYS);
        optionsMap.put(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
    }

    public Team.OptionStatus getOption(Team.Option option) {
        return optionsMap.get(option);
    }

    public void setOption(Team.Option option, Team.OptionStatus status) {
        optionsMap.put(option, status);
    }

    public UHCTeam(TeamGameInstance gameInstance) {
        super(gameInstance);
        groups = new ArrayList<>();
        teamNumber = nextTeamNum;
        nextTeamNum = genNextTeamNum(nextTeamNum);
        name = "Team " + nextTeamNum;
        displayName = name;
        Bukkit.getServer().getPluginManager().callEvent(new TeamCreateEvent(getGameInstance(), this));
    }

    public UHCTeam(TeamGameInstance gameInstance, String name) {
        super(gameInstance);
        groups = new ArrayList<>();
        this.name = name;
        displayName = name;
        teamNumber = nextTeamNum;
        nextTeamNum = genNextTeamNum(nextTeamNum);
        Bukkit.getServer().getPluginManager().callEvent(new TeamCreateEvent(getGameInstance(), this));
    }

    public Set<UUID> getPlayers() {
        return groups.stream().map(Group::getPlayers).reduce((uuids, uuids2) -> {
            uuids.addAll(uuids2);
            return uuids;
        }).orElse(new HashSet<>());
    }

    public int getSize() {
        return getPlayers().size();
    }

    public void addGroups(Group... groups) {
        int index = this.groups.size() + 1;
        for (Group g : groups) {
            this.groups.add(g);
            g.setTeam(this);
            g.setNum(index++);
        }
        Bukkit.getServer().getPluginManager().callEvent(new TeamAddGroupsEvent(getGameInstance(), this, groups));
    }

    public void removeGroups(Group... groups) {
        this.groups.removeAll(Arrays.asList(groups));
        int index = 1;
        for (Group g : this.groups) {
            if (g.getNum() != index) g.setNum(index);
            index++;
        }
        Bukkit.getServer().getPluginManager().callEvent(new TeamRemoveGroupsEvent(getGameInstance(), this, groups));
    }

    public void delete() {
        UniqueEntity.removeEntity(this);
        teamNumMap.remove(this.teamNumber, this);
        nextTeamNum = this.teamNumber;
        groups.forEach(Group::delete);
        Bukkit.getServer().getPluginManager().callEvent(new TeamDeleteEvent(getGameInstance(), this));
    }

    private int genNextTeamNum(int currTeamNum) {
        if (teamNumMap.isEmpty()) {
            return 0;
        }
        for (int i = currTeamNum; i < Collections.max(teamNumMap.keySet()); i++) {
            if (!teamNumMap.keySet().contains(i)) return i;
        }
        return Collections.max(teamNumMap.keySet()) + 1;
    }
}
