package redart15.morepainting.core.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemPainting;
import net.minecraft.core.util.helper.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import redart15.morepainting.client.Index;
import redart15.morepainting.core.interfaces.PaintingIndex;
import redart15.morepainting.core.interfaces.PlayerAdditionalGui;

@Mixin(value = ItemPainting.class, remap = false)
public class ItemPaintingMixin {

	@WrapOperation(method = "onUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;displayPaintingPickerScreen()V"))
	private void openDifferentScreen(Player player, Operation<Void> original){
		((PlayerAdditionalGui)player).morepainting$displayScreenPicker();
	}

	@WrapOperation(method = "onUseItemOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/EntityPainting;canStay()Z"))
	private boolean adjustEnitity(
		EntityPainting instance,
		Operation<Boolean> original,
		@Local EntityPainting entityPainting,
		@Local(argsOnly = true) Player player,
		@Local(argsOnly = true) Side side
	){
		int sideHit = side.getId();
		int direction = 0;
		if (sideHit == 4) {
			direction = 1;
		}
		if (sideHit == 3) {
			direction = 2;
		}
		if (sideHit == 5) {
			direction = 3;
		}
		if(entityPainting instanceof PaintingIndex && player instanceof  PaintingIndex){
			PaintingIndex playerIndex = (PaintingIndex) player;
			PaintingIndex entityIndex = (PaintingIndex) entityPainting;
			Index index = playerIndex.morepainting$getIndex();
			entityIndex.morepainting$setIndex(index);
			entityPainting.setDirection(direction);
		}
		return original.call(instance);
	}
}
