package redart15.morepainting;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redart15.morepainting.core.network.OpenPaintPickerMessage;
import redart15.morepainting.core.network.UpdateIndexMessage;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.helper.network.NetworkHandler;

public class MorePainting implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("morepainting", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("More Painting initialized.");
		NetworkHandler.registerNetworkMessage(OpenPaintPickerMessage::new);
		NetworkHandler.registerNetworkMessage(UpdateIndexMessage::new);
	}

}
