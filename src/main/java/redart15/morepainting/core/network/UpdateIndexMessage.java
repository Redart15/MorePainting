package redart15.morepainting.core.network;

import org.jetbrains.annotations.NotNull;
import redart15.morepainting.core.interfaces.PaintingIndex;
import redart15.morepainting.client.Index;
import redart15.morepainting.client.Size;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class UpdateIndexMessage implements NetworkMessage {
	public Index index;

	public UpdateIndexMessage() {super();}

	public UpdateIndexMessage(Index index) {
		this.index = index;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(this.index.index());
		packet.writeInt(this.index.getX());
		packet.writeInt(this.index.getY());
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		int i = packet.readInt();
		int x = packet.readInt();
		int y = packet.readInt();
		this.index = new Index(i, new Size(x, y));
	}

	@Override
	public void handle(NetworkContext context) {
		((PaintingIndex) context.player).morepainting$setIndex(this.index);
	}
}
