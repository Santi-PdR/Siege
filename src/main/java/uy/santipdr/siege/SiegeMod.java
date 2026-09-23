package uy.santipdr.siege;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uy.santipdr.siege.client.SiegeConfig;

@Mod(SiegeMod.MOD_ID)
public final class SiegeMod {
    public static final String MOD_ID = "siege";
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final RegistryObject<SoundEvent> TALE_CRUEL_WORLD = sound("music.tale_cruel_world");
    public static final RegistryObject<SoundEvent> DARKEST_OF_DAYS = sound("music.darkest_of_days");
    public static final RegistryObject<SoundEvent> KAPTAIN_MUSIC_BOX = sound("music.kaptain_music_box");
    public static final RegistryObject<SoundEvent> HEAVENS_GIFT = sound("music.heavens_hell_sent_gift");
    public static final RegistryObject<SoundEvent> ARC_ENEMY = sound("music.arc_enemy");
    public static final RegistryObject<SoundEvent> STRONGHOLD_BLACK_SIGNAL = sound("music.stronghold_black_signal");
    public static final RegistryObject<SoundEvent> NUCLEUS_SILENT_CARRIER = sound("music.nucleus_silent_carrier");
    public static final RegistryObject<SoundEvent> TESLA_BREACH = sound("music.tesla_breach");
    public static final RegistryObject<SoundEvent> UI_HOVER = sound("ui.hover");
    public static final RegistryObject<SoundEvent> UI_CLICK = sound("ui.click");
    public static final RegistryObject<SoundEvent> UI_BACK = sound("ui.back");
    public static final RegistryObject<SoundEvent> UI_TRACK = sound("ui.track");

    public SiegeMod() {
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SiegeConfig.load();
    }
    private static RegistryObject<SoundEvent> sound(String id) {
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, id)));
    }
}
