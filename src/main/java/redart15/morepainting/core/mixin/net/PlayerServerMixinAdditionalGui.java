package redart15.morepainting.core.mixin.net;

import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import redart15.morepainting.core.interfaces.PlayerAdditionalGui;
import redart15.morepainting.core.network.OpenPaintPickerMessage;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Mixin(value = PlayerServer.class, remap = false)
public abstract class PlayerServerMixinAdditionalGui extends PlayerMixin implements PlayerAdditionalGui {

	@Unique
	private final PlayerServer thisAs = (PlayerServer) (Object) this;

	@Override
	public void morepainting$displayScreenPicker() {
		NetworkHandler.sendToPlayer(thisAs, new OpenPaintPickerMessage());
	}

}

