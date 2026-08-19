package redart15.morepainting.core.mixin.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.entity.EntityRendererPainting;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.entity.EntityPainting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import redart15.morepainting.client.ArtTypes;
import redart15.morepainting.client.Index;
import redart15.morepainting.core.interfaces.PaintingIndex;

@Mixin(value = EntityRendererPainting.class)
public class EntityPaintingRendererMixin {

	@WrapOperation(method = "render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/EntityPainting;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRendererPainting;renderArt(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/EntityPainting;IILnet/minecraft/client/render/texture/stitcher/IconCoordinate;)V"))
	private void rerenderArt(
		EntityRendererPainting entityPaitingRenderer,
		TessellatorGeneral tessellatorGeneral,
		EntityPainting entityPainting,
		int x, int y,
		IconCoordinate texture,
		Operation<Void> original
	){
		if(entityPainting instanceof PaintingIndex paintingIndex){
			Index index = paintingIndex.morepainting$getIndex();
			IconCoordinate actualTexture = ArtTypes.getArt(index).getTexture();
			original.call(
				entityPaitingRenderer, tessellatorGeneral,
				entityPainting, index.getX(), index.getY(),
				actualTexture
			);
			return;
		}
		original.call(entityPaitingRenderer, tessellatorGeneral, entityPainting, x, y, texture);
	}

	@WrapOperation(method = "render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/EntityPainting;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRendererPainting;renderBorder(Lnet/minecraft/core/entity/EntityPainting;II)V"))
	private void rerenderBorder(
		EntityRendererPainting entityPaitingRenderer,
		EntityPainting entityPainting,
		int x, int y,
		Operation<Void> original
	){
		if(entityPainting instanceof PaintingIndex paintingIndex) {
			Index index = paintingIndex.morepainting$getIndex();
			original.call(entityPaitingRenderer, entityPainting, index.getX(), index.getY());
			return;
		}
		original.call(entityPaitingRenderer, entityPainting, x, y);
	}
}
