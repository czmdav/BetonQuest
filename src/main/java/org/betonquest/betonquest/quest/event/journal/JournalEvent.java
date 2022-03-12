package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.event.NotificationSender;

/**
 * The journal event, doing what was defined in its instruction.
 */
public class JournalEvent implements Event {

    /**
     * Change to apply to a journal when the event is executed.
     */
    private final JournalChanger journalChanger;

    /**
     * Notification to send after the journal was changed.
     */
    private final NotificationSender notificationSender;

    /**
     * Create a journal event.
     *
     * @param journalChanger     change to apply to a journal
     * @param notificationSender notification to send
     */
    public JournalEvent(final JournalChanger journalChanger, final NotificationSender notificationSender) {
        this.journalChanger = journalChanger;
        this.notificationSender = notificationSender;
    }

    @Override
    public void execute(final String playerId) {
        final PlayerData playerData = BetonQuest.getInstance().getOfflinePlayerData(playerId);
        final Journal journal = playerData.getJournal();
        journalChanger.changeJournal(journal);
        journal.update();
        notificationSender.sendNotification(playerId);
    }
}
