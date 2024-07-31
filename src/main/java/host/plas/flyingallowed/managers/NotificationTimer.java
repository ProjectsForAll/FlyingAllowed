package host.plas.flyingallowed.managers;

import host.plas.bou.scheduling.BaseRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

@Getter @Setter
public class NotificationTimer extends BaseRunnable {
    @Getter @Setter
    private static ConcurrentSkipListSet<NotificationTimer> notifications = new ConcurrentSkipListSet<>();

    public static Optional<NotificationTimer> addNotification(String identifier, Player player) {
        if (hasNotification(identifier, player)) return Optional.empty();

        NotificationTimer notificationTimer = new NotificationTimer(identifier, player);
        notifications.add(notificationTimer);

        return Optional.of(notificationTimer);
    }

    public static void removeNotification(String identifier, Player player) {
        if (! hasNotification(identifier, player)) return;

        getNotificationTimer(identifier, player).ifPresent(notification -> notifications.remove(notification));
    }

    public static Optional<NotificationTimer> getNotificationTimer(String identifier, Player player) {
        AtomicReference<NotificationTimer> notificationTimer = new AtomicReference<>();

        notifications.forEach(notification -> {
            if (notification.getIdentifier().equals(identifier) && notification.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                notificationTimer.set(notification);
            }
        });

        return Optional.ofNullable(notificationTimer.get());
    }

    public static boolean hasNotification(String identifier, Player player) {
        return getNotificationTimer(identifier, player).isPresent();
    }

    private String identifier;
    private Player player;

    private NotificationTimer(String identifier, Player player) {
        super(5 * 20, 1); // 5 second delayed then cancels. Asynchronous.

        this.identifier = identifier;
        this.player = player;
    }

    @Override
    public void run() {
        removeNotification(identifier, player);

        cancel();
    }
}
