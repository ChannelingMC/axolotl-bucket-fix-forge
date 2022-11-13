package com.github.channelingmc.axolotlbucketfix;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Map;

@Mod(AxolotlBucketFix.ID)
@Mod.EventBusSubscriber(modid = AxolotlBucketFix.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AxolotlBucketFix {
	public static final String ID = "axolotlbucketfix";
	
	public AxolotlBucketFix() {}
	
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(ID, "axolotl_bucket/adult"), "inventory"));
		ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(ID, "axolotl_bucket/baby"), "inventory"));
		for (Axolotl.Variant variant : Axolotl.Variant.values()) {
			String name = variant.getName();
			ResourceLocation adultId = ResourceLocation.tryParse(ID + ":axolotl_bucket/" + name + "/adult");
			ResourceLocation babyId = ResourceLocation.tryParse(ID + ":axolotl_bucket/" + name + "/baby");
			if (adultId != null && babyId != null) {
				ModelLoader.addSpecialModel(new ModelResourceLocation(adultId, "inventory"));
				ModelLoader.addSpecialModel(new ModelResourceLocation(babyId, "inventory"));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onModelBake(ModelBakeEvent event) {
		Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();
		ModelManager manager = event.getModelManager();
		BakedModel waterBucketModel =
			manager.getModel(new ModelResourceLocation(new ResourceLocation("water_bucket"), "inventory"));
		BakedModel defaultAdultModel =
			manager.getModel(new ModelResourceLocation(new ResourceLocation(ID, "axolotl_bucket/adult"), "inventory"));
		BakedModel defaultBabyModel =
			manager.getModel(new ModelResourceLocation(new ResourceLocation(ID, "axolotl_bucket/baby"), "inventory"));
		EnumMap<Axolotl.Variant, Pair<BakedModel, BakedModel>> variantModels = new EnumMap<>(Axolotl.Variant.class);
		for (Axolotl.Variant variant : Axolotl.Variant.values()) {
			String name = variant.getName();
			ResourceLocation adultId = ResourceLocation.tryParse(ID + ":axolotl_bucket/" + name + "/adult");
			ResourceLocation babyId = ResourceLocation.tryParse(ID + ":axolotl_bucket/" + name + "/baby");
			if (adultId != null && babyId != null) {
				variantModels.put(variant, new Pair<>(
					registry.getOrDefault(new ModelResourceLocation(adultId, "inventory"), defaultAdultModel),
					registry.getOrDefault(new ModelResourceLocation(babyId, "inventory"), defaultBabyModel)
				));
			}
		}
		registry.put(
			new ModelResourceLocation(new ResourceLocation("axolotl_bucket"), "inventory"),
			new AxolotlBucketModel(waterBucketModel, variantModels)
		);
	}

}
