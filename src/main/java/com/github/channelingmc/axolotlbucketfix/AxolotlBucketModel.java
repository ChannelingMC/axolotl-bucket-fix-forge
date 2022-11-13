package com.github.channelingmc.axolotlbucketfix;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AxolotlBucketModel extends BakedModelWrapper<BakedModel> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final EnumMap<Axolotl.Variant, Pair<BakedModel, BakedModel>> variantModels;
    private final Overrides overrides;
    
    public AxolotlBucketModel(BakedModel internal, EnumMap<Axolotl.Variant, Pair<BakedModel, BakedModel>> variantModels) {
        super(internal);
        this.variantModels = variantModels;
        this.overrides = new Overrides();
    }
    
    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }
    
    public class Overrides extends ItemOverrides {
        
        @Nullable
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            CompoundTag tag = stack.getTag();
            Axolotl.Variant variant = Axolotl.Variant.LUCY;
            boolean baby = false;
            if (tag != null) {
                int ordinal = tag.getInt("Variant");
                if (ordinal >= 0 && ordinal < Axolotl.Variant.BY_ID.length) {
                    variant = Axolotl.Variant.BY_ID[ordinal];
                } else {
                    LOGGER.error("Invalid variant: {}", ordinal);
                }
                baby = tag.contains("Age") && tag.getInt("Age") < 0;
            }
            Pair<BakedModel, BakedModel> models = variantModels.get(variant);
            return baby ? models.getSecond() : models.getFirst();
        }
        
    }
    
}
