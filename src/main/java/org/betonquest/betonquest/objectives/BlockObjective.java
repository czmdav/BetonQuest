package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Player has to break/place specified amount of blocks. Doing opposite thing
 * (breaking when should be placing) will reverse the progress.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public class BlockObjective extends CountingObjective implements Listener {
    /**
     * Blockselector parameter.
     */
    private final BlockSelector selector;

    /**
     * Optional exactMatch parameter.
     */
    private final boolean exactMatch;

    /**
     * Optional noSafety parameter.
     */
    private final boolean noSafety;

    /**
     * Optional location parameter.
     */
    private final CompoundLocation location;

    /**
     * Optional region parameter. Used together with {@link #location} to form a cuboid region.
     */
    private final CompoundLocation region;

    /**
     * Optional ignorecancel parameter.
     */
    private final boolean ignorecancel;

    /**
     * Logger for exception handling.
     */
    private final BetonQuestLogger logger;

    public BlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        logger = BetonQuest.getInstance().getLoggerFactory().create(BetonQuest.getInstance());
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
        targetAmount = instruction.getVarNum();
        noSafety = instruction.hasArgument("noSafety");
        location = instruction.getLocation(instruction.getOptional("loc"));
        region = instruction.getLocation(instruction.getOptional("region"));
        ignorecancel = instruction.hasArgument("ignorecancel");
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return String.valueOf(targetAmount.getInt(profile));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled() && !ignorecancel) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
            if (!checkLocation(event.getBlock().getLocation(), onlineProfile)) {
                return;
            }
            if (getCountingData(onlineProfile).getDirectionFactor() < 0 && noSafety) {
                return;
            }
            handleDataChange(onlineProfile, getCountingData(onlineProfile).add());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled() && !ignorecancel) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && selector.match(event.getBlock(), exactMatch) && checkConditions(onlineProfile)) {
            if (!checkLocation(event.getBlock().getLocation(), onlineProfile)) {
                return;
            }
            if (getCountingData(onlineProfile).getDirectionFactor() > 0 && noSafety) {
                return;
            }
            handleDataChange(onlineProfile, getCountingData(onlineProfile).subtract());
        }
    }

    private void handleDataChange(final OnlineProfile onlineProfile, final CountingData data) {
        final String message = data.getDirectionFactor() > 0 ? "blocks_to_place" : "blocks_to_break";
        completeIfDoneOrNotify(onlineProfile, message);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    private boolean checkLocation(final Location loc, final Profile profile) {
        try {
            if (location != null) {
                if (region != null) {
                    return isInRange(loc, profile);
                }
                return loc.getBlock().getLocation().equals(location.getLocation(profile));
            }
        } catch (QuestRuntimeException e) {
            logger.error(instruction.getPackage(), e.getMessage());
            return false;
        }
        return true;
    }

    private boolean isInRange(final Location loc, final Profile profile) throws QuestRuntimeException {
        final Location loc1 = location.getLocation(profile);
        final Location loc2 = region.getLocation(profile);
        return inBetween(loc1, loc2, loc);
    }

    private boolean inBetween(final Location range1, final Location range2, final Location pos) {
        return inWorld(range1, range2, pos)
                && betweenCoordinates(range1.getBlockY(), range2.getBlockY(), pos.getBlockY())
                && betweenCoordinates(range1.getBlockZ(), range2.getBlockZ(), pos.getBlockZ())
                && betweenCoordinates(range1.getBlockX(), range2.getBlockX(), pos.getBlockX());
    }

    private boolean betweenCoordinates(final int range1, final int range2, final int pos) {
        return Integer.min(range1, range2) <= pos && pos <= Integer.max(range1, range2);
    }

    private boolean inWorld(final Location range1, final Location range2, final Location pos) {
        return range1.getWorld().equals(range2.getWorld()) && range2.getWorld().equals(pos.getWorld());
    }
}
