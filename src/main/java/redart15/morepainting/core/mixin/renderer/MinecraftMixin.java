package redart15.morepainting.core.mixin.renderer;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redart15.morepainting.client.ArtTypes;

@Mixin(value = Minecraft.class)
public class MinecraftMixin {

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextureManager;refreshTextures(Ljava/util/List;)V", shift = At.Shift.AFTER))
	private void assignSizes(CallbackInfo ci){
		ArtTypes.loadPictureWithOutSize();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundRepository;initialize(Lnet/minecraft/client/Minecraft;)V"))
	private void initArtTypeList(CallbackInfo ci){
		ArtTypes.reload();
	}
}
