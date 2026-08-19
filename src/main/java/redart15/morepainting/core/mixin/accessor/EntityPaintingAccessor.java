package redart15.morepainting.core.mixin.accessor;

import net.minecraft.core.entity.EntityPainting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPainting.class)
public interface EntityPaintingAccessor {
	@Invoker
	float callOffsetFromCenter(int i);
}
