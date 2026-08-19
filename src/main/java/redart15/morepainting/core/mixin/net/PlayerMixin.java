package redart15.morepainting.core.mixin.net;

import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import redart15.morepainting.core.interfaces.PaintingIndex;
import redart15.morepainting.client.Index;

@Mixin(value = Player.class)
public abstract class PlayerMixin implements PaintingIndex {

	@Unique
	public Index index = Index.getDefaultIndex();

	@Override
	public Index morepainting$getIndex() {
		return this.index;
	}

	@Override
	public void morepainting$setIndex(Index index) {
		this.index = index;
	}

}
