package redart15.morepainting.core.mixin.net;


import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import redart15.morepainting.core.interfaces.PlayerAdditionalGui;
import redart15.morepainting.client.Index;
import redart15.morepainting.client.ScreenBetterPaintPicker;
import redart15.morepainting.core.network.UpdateIndexMessage;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Mixin(value = PlayerLocal.class, remap = false)
public abstract class PlayerLocalMixinAdditionalGui extends PlayerMixin implements PlayerAdditionalGui {

	@Shadow
	protected Minecraft mc;

	@Override
	public void morepainting$displayScreenPicker() {
		mc.displayScreen(new ScreenBetterPaintPicker(this.mc.thePlayer));
	}

	@Override
	public void morepainting$setIndex(Index index){
		this.index = index;
		if(!EnvironmentHelper.isSingleplayerClient()){
			NetworkHandler.sendToServer(new UpdateIndexMessage(this.index));
		}
	}
}
