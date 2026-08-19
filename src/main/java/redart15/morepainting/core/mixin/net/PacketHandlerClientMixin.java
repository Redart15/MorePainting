package redart15.morepainting.core.mixin.net;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.client.world.WorldClientMP;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.net.packet.PacketAddPainting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redart15.morepainting.client.Index;
import redart15.morepainting.core.interfaces.PaintingIndex;

@Mixin(value = PacketHandlerClient.class, remap = false)
public abstract class PacketHandlerClientMixin {

	@Inject(method = "handleAddPainting", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
	private void handleSizeWithExistingEntity(
		PacketAddPainting packetAddPainting, CallbackInfo ci,
		@Local EntityPainting entityPainting
		){
		if(entityPainting instanceof PaintingIndex paintingIndex
			&& packetAddPainting instanceof PaintingIndex packetPaintingIndex
		){
			Index index = packetPaintingIndex.morepainting$getIndex();
			paintingIndex.morepainting$setIndex(index);
		}
	}

	@WrapOperation(
		method = "handleAddPainting",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/world/WorldClientMP;addEntityToWorld(ILnet/minecraft/core/entity/Entity;)V"
		)
	)
	private void handleAddPainting(
		WorldClientMP world,
		int id, Entity entity,
		Operation<Void> original,
		@Local(argsOnly = true) PacketAddPainting packetAddPainting
	) {
		if (entity instanceof PaintingIndex paintingIndex
			&& entity instanceof EntityPainting entityPainting
			&& packetAddPainting instanceof PaintingIndex packetPaintingIndex
		) {
			Index index = packetPaintingIndex.morepainting$getIndex();
			paintingIndex.morepainting$setIndex(index);
			entityPainting.setDirection(packetAddPainting.direction);
		}
		original.call(world, id, entity);
	}
}
