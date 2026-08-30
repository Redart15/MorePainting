package redart15.morepainting.core.mixin.world;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Contract;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redart15.morepainting.client.Index;
import redart15.morepainting.client.Size;
import redart15.morepainting.core.interfaces.PaintingIndex;

@Mixin(value = EntityPainting.class, remap = false)
public class EntityPaintingMixin implements PaintingIndex {

	@Unique
	private Index index;

	@Unique
	EntityPainting asThis = (EntityPainting) (Object) this;

	@Inject(method = "<init>(Lnet/minecraft/core/world/World;IIIILjava/lang/String;)V", at = @At("TAIL"))
	private void initIndex(World world, int x, int y, int z, int side, String motive, CallbackInfo ci){
		this.index = Index.getDefaultIndex();
		this.asThis.viewScale = 1.0F; // same as all entities render distance
	}

	@Inject(method = "<init>(Lnet/minecraft/core/world/World;)V", at= @At("TAIL"))
	private void initIndex(World world, CallbackInfo ci){
		this.index = Index.getDefaultIndex();
		this.asThis.viewScale = 1.0F; // same as all entities render distance
	}

	@WrapMethod(method = "setDirection")
	public void setDirection(int direction, Operation<Void> original) {
		this.asThis.direction = direction;
		this.asThis.yRotO = this.asThis.yRot = (direction * 90);
		float sizeX = this.index.getX();
		float sizeY = this.index.getY();
		float sizeZ = this.index.getX();
		if (direction != 0 && direction != 2) {
			sizeX = 0.5F;
		} else {
			sizeZ = 0.5F;
		}

		sizeX /= 32.0F;
		sizeY /= 32.0F;
		sizeZ /= 32.0F;
		double centerX = (double)this.asThis.blockX + (double)0.5F;
		double centerY = (double)this.asThis.blockY + (double)0.5F;
		double centerZ = (double)this.asThis.blockZ + (double)0.5F;
		if (direction == 0) {centerZ -= 0.53F;}
		if (direction == 1) {centerX -= 0.53F;}
		if (direction == 2) {centerZ += 0.53F;}
		if (direction == 3) {centerX += 0.53F;}
		if (direction == 0) {centerX -= this.offsetFromCenter(this.index.getX());}
		if (direction == 1) {centerZ += this.offsetFromCenter(this.index.getX());}
		if (direction == 2) {centerX += this.offsetFromCenter(this.index.getX());}
		if (direction == 3) {centerZ -= this.offsetFromCenter(this.index.getX());}
		centerY += this.offsetFromCenter(this.index.getY());
		this.asThis.setPos(centerX, centerY, centerZ);
		float expand = -0.0F;
		this.asThis.bb
			.setMin(centerX - sizeX - expand, centerY - sizeY - expand, centerZ - sizeZ - expand)
			.setMax(centerX + sizeX + expand, centerY + sizeY + expand, centerZ + sizeZ + expand);
		if (direction == 0 || direction == 2) {
			AABBd bounds = this.asThis.bb;
			bounds.minZ -= 0.01F;
			bounds = this.asThis.bb;
			bounds.maxZ += 0.01F;
		}

		if (direction == 1 || direction == 3) {
			AABBd bounds = this.asThis.bb;
			bounds.minX -= 0.01F;
			bounds = this.asThis.bb;
			bounds.maxX += 0.01F;
		}

	}

	@WrapMethod(method = "canStay")
	public boolean canStay(Operation<Boolean> original) {
		if (!this.asThis.world.getCubes(asThis, asThis.bb).isEmpty()) {
			return false;
		}
		int xSize = this.index.getX() / 16;
		int ySize = this.index.getY() / 16;
		int xPosition = this.asThis.blockX;
		int yPosition = MathHelper.floor(this.asThis.y - (this.index.getY() / 32.0F));
		int zPosition = this.asThis.blockZ;
		if (this.asThis.direction == 0) {xPosition = MathHelper.floor(asThis.x - (this.index.getX() / 32.0F));}
		if (this.asThis.direction == 1) {zPosition = MathHelper.floor(this.asThis.z - (this.index.getX() / 32.0F));}
		if (this.asThis.direction == 2) {xPosition = MathHelper.floor(this.asThis.x - (this.index.getX() / 32.0F));}
		if (this.asThis.direction == 3) {zPosition = MathHelper.floor(this.asThis.z - (this.index.getX() / 32.0F));}
		for(int dx = 0; dx < xSize; ++dx) {
			for(int dy = 0; dy < ySize; ++dy) {
				Material material;
				if (this.asThis.direction != 0 && this.asThis.direction != 2) {
					material = this.asThis.world.getBlockMaterial(this.asThis.blockX, yPosition + dy, zPosition + dx);
				} else {
					material = this.asThis.world.getBlockMaterial(xPosition + dx, yPosition + dy, this.asThis.blockZ);
				}
				if (!material.isSolid()) {
					return false;
				}
			}
		}
		for(Entity entity : this.asThis.world.getEntitiesWithinAABBExcludingEntity(this.asThis, this.asThis.bb)) {
			if (entity instanceof EntityPainting) {
				return false;
			}
		}
		return true;
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void readIndex(CompoundTag tag, CallbackInfo ci){
		int i = tag.getInteger("Index");
		int x = tag.getInteger("sizeX");
		int y = tag.getInteger("sizeY");
		this.index = new Index(i, new Size(x, y));
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void addIndex(CompoundTag tag, CallbackInfo ci){
		tag.putInt("Index", this.index.index());
		tag.putInt("sizeX", this.index.getX());
		tag.putInt("sizeY", this.index.getY());
	}

	@Override
	public Index morepainting$getIndex() {
		return this.index;
	}

	@Override
	public void morepainting$setIndex(Index index) {
		this.index = index;
	}

	@Unique
	@Contract(pure = true)
	private float offsetFromCenter(int pixels) {
		int blocks = pixels / 16;
		return (blocks % 2 == 0) ? 0.5F : 0.0F;
	}
}
