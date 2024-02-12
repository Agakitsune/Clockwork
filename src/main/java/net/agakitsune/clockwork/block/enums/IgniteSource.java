package net.agakitsune.clockwork.block.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum IgniteSource implements StringIdentifiable {
    NONE("none"),
    PLAYER("player"),
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west"),
    NORTH_EAST("north_east"),
    NORTH_WEST("north_west"),
    SOUTH_EAST("south_east"),
    SOUTH_WEST("south_west"),
    NORTH_SOUTH("north_south"),
    EAST_WEST("east_west"),
    NORTH_SOUTH_EAST("north_south_east"),
    NORTH_SOUTH_WEST("north_south_west"),
    NORTH_EAST_WEST("north_east_west"),
    SOUTH_EAST_WEST("south_east_west"),
    NORTH_SOUTH_EAST_WEST("north_south_east_west");

    private final String name;
    private static final String[] maxout = new String[]{"north","south","east","west"};

    IgniteSource(String name) {
        this.name = name;
    }

    public static IgniteSource fromDirection(Direction direction) {
        switch (direction) {
            case UP, DOWN -> {
                return PLAYER;
            }
            case NORTH -> {
                return NORTH;
            }
            case SOUTH -> {
                return SOUTH;
            }
            case EAST -> {
                return EAST;
            }
            case WEST -> {
                return WEST;
            }
        }
        return null;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public IgniteSource union(IgniteSource other) {
        if (this == other)
            return this;
        if (this == NONE)
            return other;
        if (other == NONE)
            return this;
        if (this == PLAYER || other == PLAYER)
            return PLAYER;
        String name = asString();
        String name2 = other.asString();
        String[] split = name.split("_");
        String[] split2 = name2.split("_");
        List<String> result = getStrings(split, split2);
        return IgniteSource.valueOf(String.join("_", result).toUpperCase());
    }

    @NotNull
    private static List<String> getStrings(String[] split, String[] split2) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            found: {
                for (String a : split) {
                    if (a.equals(maxout[i])) {
                        result.add(a);
                        break found;
                    }
                }
                for (String a : split2) {
                    if (a.equals(maxout[i])) {
                        result.add(a);
                        break found;
                    }
                }
            }
        }
        return result;
    }

    public static List<Direction> toDirections(IgniteSource source) {
        List<Direction> list = new ArrayList<>(3);
        switch (source) {
            case NONE, PLAYER -> {
                return new ArrayList<>();
            }
            case NORTH -> list.add(Direction.NORTH);
            case SOUTH -> list.add(Direction.SOUTH);
            case EAST -> list.add(Direction.EAST);
            case WEST -> list.add(Direction.WEST);
            case NORTH_EAST -> {
                list.add(Direction.NORTH);
                list.add(Direction.EAST);
            }
            case NORTH_WEST -> {
                list.add(Direction.NORTH);
                list.add(Direction.WEST);
            }
            case SOUTH_EAST -> {
                list.add(Direction.SOUTH);
                list.add(Direction.EAST);
            }
            case SOUTH_WEST -> {
                list.add(Direction.SOUTH);
                list.add(Direction.WEST);
            }
            case NORTH_SOUTH -> {
                list.add(Direction.NORTH);
                list.add(Direction.SOUTH);
            }
            case EAST_WEST -> {
                list.add(Direction.EAST);
                list.add(Direction.WEST);
            }
            case NORTH_SOUTH_EAST -> {
                list.add(Direction.NORTH);
                list.add(Direction.SOUTH);
                list.add(Direction.EAST);
            }
            case NORTH_SOUTH_WEST -> {
                list.add(Direction.NORTH);
                list.add(Direction.SOUTH);
                list.add(Direction.WEST);
            }
            case NORTH_EAST_WEST -> {
                list.add(Direction.NORTH);
                list.add(Direction.EAST);
                list.add(Direction.WEST);
            }
            case SOUTH_EAST_WEST -> {
                list.add(Direction.SOUTH);
                list.add(Direction.EAST);
                list.add(Direction.WEST);
            }
            case NORTH_SOUTH_EAST_WEST -> {
                list.add(Direction.NORTH);
                list.add(Direction.SOUTH);
                list.add(Direction.EAST);
                list.add(Direction.WEST);
            }
        }
        return list;
    }
}
