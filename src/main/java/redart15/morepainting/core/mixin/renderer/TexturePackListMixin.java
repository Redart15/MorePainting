package redart15.morepainting.core.mixin.renderer;

import net.minecraft.client.render.texturepack.TexturePackList;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import redart15.morepainting.client.ArtTypes;

@Mixin(value = TexturePackList.class, remap = false)
public abstract class TexturePackListMixin {

	@Inject(method = "refresh", at = @At("HEAD"))
	private void refreshArtTypeList(CallbackInfo ci){
		ArtTypes.reload();
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void initArtTypeList(CallbackInfo ci){
		ArtTypes.reload();
	}
}
