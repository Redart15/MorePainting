package redart15.morepainting.core.mixin.net;

import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.net.packet.PacketAddPainting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redart15.morepainting.client.Index;
import redart15.morepainting.client.Size;
import redart15.morepainting.core.interfaces.PaintingIndex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mixin(value = PacketAddPainting.class, remap = false)
public class PacketAddPaintingMixin implements PaintingIndex{

	@Unique
	Index index;

	@Inject(method = "<init>(Lnet/minecraft/core/entity/EntityPainting;)V", at = @At("TAIL"))
	private void addIndex(EntityPainting entitypainting, CallbackInfo ci){
		if(entitypainting instanceof PaintingIndex paintingIndex){
			this.index = paintingIndex.morepainting$getIndex();
		}else {
			this.index = Index.getDefaultIndex();
		}
	}

	@Inject(method = "read", at = @At("TAIL"))
	private void readAdditional(DataInputStream dis, CallbackInfo ci) throws IOException {
		int i = dis.readInt();
		int x = dis.readInt();
		int y = dis.readInt();
		this.index = new Index(i, new Size(x, y));
	}

	@Inject(method = "write", at = @At("TAIL"))
	private void writeAdditional(DataOutputStream dos, CallbackInfo ci) throws IOException{
		dos.writeInt(index.index());
		dos.writeInt(index.getX());
		dos.writeInt(index.getY());
	}

	@Override
	public Index morepainting$getIndex() {
		return this.index;
	}

	@Override
	public void morepainting$setIndex(Index index) {
		this.index = index;
	}
}
