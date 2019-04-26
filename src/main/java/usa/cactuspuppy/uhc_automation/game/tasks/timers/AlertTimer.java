package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.stream.Collectors;

public abstract class AlertTimer extends TimerTask {
    /**
     * List of countdown marks that will go to chat
     */
    protected LinkedList<Long> chatMarks = new LinkedList<>();
    protected long nextChatMark;
    /**
     * List of countdown marks that will go to title
     */
    protected LinkedList<Long> titleMarks = new LinkedList<>();
    protected long nextTitleMark;

    public AlertTimer(GameInstance gameInstance, long repeatDelay, long currentTimeTo, String configKey) {
        super(gameInstance, true, 0L, repeatDelay);
        parseConfigList(configKey);
        nextChatMark = Long.MAX_VALUE;
        while (nextChatMark >= currentTimeTo) {
            if (!chatMarks.isEmpty()) {
                nextChatMark = chatMarks.removeLast();
            } else {
                nextChatMark = -1;
            }
        }
        nextTitleMark = Long.MAX_VALUE;
        while (nextTitleMark >= currentTimeTo) {
            if (!titleMarks.isEmpty()) {
                nextTitleMark = titleMarks.removeLast();
            } else {
                nextTitleMark = -1;
            }
        }
    }

    private void parseConfigList(String primary) {
        String border = Main.getMainConfig().get(primary);
        if (border == null) {
            border = Main.getMainConfig().get("countdown.default");
            if (border == null) {
                getGameInstance().getUtils().log(Logger.Level.SEVERE, this.getClass(), "No countdown marks found, cannot set up countdown");
                return;
            }
        }
        String[] strings = border.split(",");
        for (String s : strings) {
            boolean title = false;
            if (s.startsWith("+")) {
                title = true;
                s = s.replaceFirst("\\+", "");
            }
            long value;
            try {
                value = Long.parseLong(s);
            } catch (NumberFormatException e) {
                try { //Distinguish if overflow
                    new BigInteger(s);
                } catch (NumberFormatException e1) {
                    getGameInstance().getUtils().log(Logger.Level.WARNING, this.getClass(), "Could not parse " + s + " in countdown list, skipping...");
                    continue;
                }
                getGameInstance().getUtils().log(Logger.Level.WARNING, this.getClass(), s + " is too large of a value, skipping...");
                continue;
            }
            if (title) {
                titleMarks.add(value);
            } else {
                chatMarks.add(value);
            }
        }
        //Sort resulting lists
        titleMarks = titleMarks.stream().sorted().collect(Collectors.toCollection(LinkedList::new));
        chatMarks = chatMarks.stream().sorted().collect(Collectors.toCollection(LinkedList::new));
    }
}
