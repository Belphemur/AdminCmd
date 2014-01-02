package be.Balor.Listeners.Events;

import be.Balor.Tools.Warp;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author TheJeterLP
 */
public class ACWarpRemoveEvent extends Event {

        private static final HandlerList handlers = new HandlerList();
        private final Warp warp;

        public ACWarpRemoveEvent(Warp w) {
                this.warp = w;
        }

        @Override
        public HandlerList getHandlers() {
                return handlers;
        }

        public static HandlerList getHandlerList() {
                return handlers;
        }

        public Warp getWarp() {
                return warp;
        }

}
