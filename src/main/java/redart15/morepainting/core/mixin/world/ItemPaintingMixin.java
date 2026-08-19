package redart15.morepainting.core.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemPainting;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import redart15.morepainting.client.Index;
import redart15.morepainting.core.interfaces.PaintingIndex;
import redart15.morepainting.core.interfaces.PlayerAdditionalGui;

@Mixin(value = ItemPainting.class, remap = false)
public class ItemPaintingMixin {

	@WrapOperation(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;displayPaintingPickerScreen()V"))
	private void openDifferentScreen(Player player, Operation<Void> original){
		((PlayerAdditionalGui)player).morepainting$displayScreenPicker();
	}

	@Inject(method = "onUseOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/EntityPainting;canStay()Z", shift = At.Shift.BEFORE))
	private void adjustEnitity(
		ItemStack selfStack,
		World world,
		Player player,
		TilePosc blockPos,
		Side side, double xHit, double yHit,
		CallbackInfoReturnable<Boolean> cir,
		@Local EntityPainting entityPainting,
		@Local int direction
	){
		if(entityPainting instanceof PaintingIndex entityIndex && player instanceof  PaintingIndex playerIndex){
			Index index = playerIndex.morepainting$getIndex();
			entityIndex.morepainting$setIndex(index);
			entityPainting.setDirection(direction);
		}
	}
}
