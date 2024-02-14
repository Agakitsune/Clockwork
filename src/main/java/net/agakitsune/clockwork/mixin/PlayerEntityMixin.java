package net.agakitsune.clockwork.mixin;

import net.agakitsune.clockwork.entity.player.ManaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements ManaEntity {
    public int maxMana = DEFAULT_MANA_VALUE;
    public int mana = 25;

    @Override
    public int getMaxMana() {
        return this.maxMana;
    }

    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public void addMana(int value) {
        this.mana = this.mana + value;
        if (this.mana > this.maxMana)
            this.mana = this.maxMana;
    }

    @Override
    public void addMaxMana(int value) {
        this.maxMana = this.maxMana + value;
    }

    @Override
    public void setMana(int value) {
        this.mana = value;
    }

    @Override
    public void setMaxMana(int value) {
        this.maxMana = value;
    }

    @Inject(method= "readCustomDataFromNbt", at= @At("RETURN"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        setMana(nbt.getInt("Mana"));
        setMaxMana(nbt.getInt("MaxMana"));
    }

    @Inject(method= "writeCustomDataToNbt", at= @At("RETURN"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("Mana", this.getMana());
        nbt.putInt("MaxMana", this.getMaxMana());
    }
}
