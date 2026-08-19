package redart15.morepainting.core.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import redart15.morepainting.client.ScreenBetterPaintPicker;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class OpenPaintPickerMessage implements NetworkMessage {

	public OpenPaintPickerMessage(){super();}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {}


	@Environment(EnvType.CLIENT)
	@Override
	public void handleClientEnv(NetworkContext context) {
		Minecraft.getMinecraft().displayScreen(new ScreenBetterPaintPicker(context.player));
	}
}
