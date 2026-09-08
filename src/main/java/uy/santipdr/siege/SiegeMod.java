package uy.santipdr.siege;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SiegeMod.MOD_ID)
public final class SiegeMod {
    public static final String MOD_ID = "siege";
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final RegistryObject<SoundEvent> TALE_CRUEL_WORLD = sound("music.tale_cruel_world");
    public static final RegistryObject<SoundEvent> DARKEST_OF_DAYS = sound("music.darkest_of_days");
    public static final RegistryObject<SoundEvent> KAPTAIN_MUSIC_BOX = sound("music.kaptain_music_box");
    public static final RegistryObject<SoundEvent> HEAVENS_GIFT = sound("music.heavens_hell_sent_gift");

    public SiegeMod() { SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus()); }

    private static RegistryObject<SoundEvent> sound(String id) {
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, id)));
    }
}
