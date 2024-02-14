package net.agakitsune.clockwork.entity.player;

public interface ManaEntity {
    int DEFAULT_MANA_VALUE = 100;
    int getMaxMana();
    int getMana();
    void addMana(int value);
    void addMaxMana(int value);
    void setMana(int value);
    void setMaxMana(int value);
}
