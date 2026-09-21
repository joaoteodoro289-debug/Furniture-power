package com.example.furniturepower.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public final class PowerConfig {
    private PowerConfig() {}

    public static int capacity = 100000;
    public static int generatorOutputPerTick = 80;
    public static int generatorTransfer = 800;
    public static int networkRadius = 16;
    public static int maxNetworkNodes = 1024;
    public static int energyTickInterval = 20;
    public static int startupCost = 2;

    public static int tv = 2;
    public static int modernTv = 2;
    public static int computer = 3;
    public static int fridge = 1;
    public static int freezer = 2;
    public static int microwave = 8;
    public static int dishwasher = 5;
    public static int washingMachine = 6;
    public static int blender = 6;
    public static int toaster = 5;
    public static int printer = 4;
    public static int oven = 4;
    public static int stereo = 2;
    public static int ceilingFan = 2;
    public static int digitalClock = 1;
    public static int ceilingLight = 2;
    public static int modernLight = 2;

    public static void load(File file) {
        Configuration c = new Configuration(file);
        try {
            capacity = c.getInt("capacity", "generator", capacity, 1000, 2000000, "Energia maxima do gerador em FE.");
            generatorOutputPerTick = c.getInt("outputPerTick", "generator", generatorOutputPerTick, 1, 10000, "FE gerados por tick enquanto houver combustivel.");
            generatorTransfer = c.getInt("transfer", "generator", generatorTransfer, 1, 100000, "Limite de transferencia por operacao.");
            networkRadius = c.getInt("radius", "network", networkRadius, 1, 64, "Raio maximo da rede a partir do aparelho.");
            maxNetworkNodes = c.getInt("maxNodes", "network", maxNetworkNodes, 64, 10000, "Numero maximo de blocos visitados em uma busca.");
            energyTickInterval = c.getInt("tickInterval", "network", energyTickInterval, 1, 100, "Intervalo entre cobranças continuas em ticks.");
            startupCost = c.getInt("startupCost", "network", startupCost, 0, 100000, "Custo inicial de interacao com um eletronico.");

            tv = c.getInt("tv", "appliances", tv, 0, 100000, "FE por intervalo.");
            modernTv = c.getInt("modernTv", "appliances", modernTv, 0, 100000, "FE por intervalo.");
            computer = c.getInt("computer", "appliances", computer, 0, 100000, "FE por intervalo.");
            fridge = c.getInt("fridge", "appliances", fridge, 0, 100000, "FE por interacao.");
            freezer = c.getInt("freezer", "appliances", freezer, 0, 100000, "FE por intervalo.");
            microwave = c.getInt("microwave", "appliances", microwave, 0, 100000, "FE por intervalo.");
            dishwasher = c.getInt("dishwasher", "appliances", dishwasher, 0, 100000, "FE por intervalo.");
            washingMachine = c.getInt("washingMachine", "appliances", washingMachine, 0, 100000, "FE por intervalo.");
            blender = c.getInt("blender", "appliances", blender, 0, 100000, "FE por intervalo.");
            toaster = c.getInt("toaster", "appliances", toaster, 0, 100000, "FE por intervalo.");
            printer = c.getInt("printer", "appliances", printer, 0, 100000, "FE por intervalo.");
            oven = c.getInt("oven", "appliances", oven, 0, 100000, "FE por intervalo.");
            stereo = c.getInt("stereo", "appliances", stereo, 0, 100000, "FE por interacao.");
            ceilingFan = c.getInt("ceilingFan", "appliances", ceilingFan, 0, 100000, "FE por intervalo.");
            digitalClock = c.getInt("digitalClock", "appliances", digitalClock, 0, 100000, "FE por interacao.");
            ceilingLight = c.getInt("ceilingLight", "appliances", ceilingLight, 0, 100000, "FE por intervalo.");
            modernLight = c.getInt("modernLight", "appliances", modernLight, 0, 100000, "FE por intervalo.");
        } finally {
            if (c.hasChanged()) c.save();
        }
    }
}
