package com.animationlib.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

// ModelPart keeps its child parts in a private map, and its public API can only look children up by
// name one level down. ModelBones walks the whole tree through this, so a bone nested under
// another (a goat's "left_horn" under "head") can be found by name.
@Mixin(ModelPart.class)
public interface ModelPartAccessor {
    @Accessor("children")
    Map<String, ModelPart> animationlib$children();
}
