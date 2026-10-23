# Mob bone counts

Entity classes (under `net.minecraft.world.entity`) sorted by how many bones their model has, fewest
first. A bone is a `ModelPart` below the model root, counted from the baked `LayerDefinition`s in
Minecraft Java Edition 26.3. The count is for the adult or default model; baby and variant layers
that differ are in the notes column. Variant layers of a class (cold/warm cow, cold pig, cold
chicken, baby models) otherwise share its entity class.

Static objects (boats, minecarts, chests, banners, skulls, armor layers, projectiles, the leash
knot, armor stands) are left out.

| Bones | Class | Notes |
|---|---|---|
| 1 | `monster.cubemob.SulfurCube` | |
| 2 | `animal.frog.Tadpole` | `body`, `tail` |
| 3 | `monster.Shulker` | |
| 4 | `monster.Endermite` | |
| 4 | `monster.cubemob.Slime` | |
| 5 | `animal.golem.SnowGolem` | |
| 5 | `animal.fish.TropicalFish` | small 5, large 6 |
| 6 | `animal.cow.Cow` | |
| 6 | `animal.cow.MushroomCow` | |
| 6 | `animal.golem.CopperGolem` | running, sitting and star poses 11 |
| 6 | `monster.Creeper` | |
| 6 | `animal.golem.IronGolem` | |
| 6 | `animal.nautilus.Nautilus` | |
| 6 | `animal.nautilus.ZombieNautilus` | coral model 18 |
| 6 | `animal.panda.Panda` | |
| 6 | `animal.pig.Pig` | |
| 6 | `animal.polarbear.PolarBear` | |
| 6 | `animal.sheep.Sheep` | |
| 6 | `boss.wither.WitherBoss` | |
| 6 | `animal.fish.Pufferfish` | small 6, medium 11, big 13 |
| 7 | `animal.allay.Allay` | |
| 7 | `animal.fish.Cod` | |
| 7 | `monster.Enderman` | |
| 7 | `monster.Giant` | |
| 7 | `monster.skeleton.Parched` | |
| 7 | `monster.skeleton.Skeleton` | |
| 7 | `monster.skeleton.Stray` | |
| 7 | `animal.turtle.Turtle` | baby 6 |
| 7 | `monster.Vex` | |
| 7 | `monster.skeleton.WitherSkeleton` | |
| 7 | `monster.zombie.Zombie` | |
| 7 | `monster.zombie.Husk` | |
| 7 | `monster.zombie.Drowned` | |
| 8 | `animal.feline.Cat` | |
| 8 | `animal.chicken.Chicken` | baby 5 |
| 8 | `monster.creaking.Creaking` | |
| 8 | `animal.dolphin.Dolphin` | |
| 8 | `animal.equine.Llama` | |
| 8 | `animal.feline.Ocelot` | |
| 8 | `monster.Phantom` | |
| 8 | `animal.fish.Salmon` | |
| 8 | `animal.equine.TraderLlama` | |
| 8 | `monster.zombie.ZombieVillager` | baby 9 |
| 9 | `ambient.Bat` | |
| 9 | `animal.goat.Goat` | baby 11 |
| 9 | `animal.squid.GlowSquid` | |
| 9 | `monster.cubemob.MagmaCube` | |
| 9 | `animal.squid.Squid` | |
| 9 | `monster.Strider` | baby 6 |
| 9 | `npc.villager.Villager` | baby 11 |
| 9 | `npc.wanderingtrader.WanderingTrader` | |
| 10 | `animal.axolotl.Axolotl` | baby 12 |
| 10 | `animal.bee.Bee` | baby 8 |
| 10 | `animal.camel.Camel` | baby 9 |
| 10 | `monster.illager.Evoker` | |
| 10 | `animal.fox.Fox` | baby 7 |
| 10 | `monster.Ghast` | |
| 10 | `animal.happyghast.HappyGhast` | baby 11 |
| 10 | `monster.illager.Illusioner` | |
| 10 | `monster.illager.Pillager` | |
| 10 | `monster.Ravager` | |
| 10 | `monster.Silverfish` | |
| 10 | `monster.illager.Vindicator` | |
| 10 | `animal.wolf.Wolf` | |
| 11 | `monster.breeze.Breeze` | |
| 11 | `monster.spider.CaveSpider` | |
| 11 | `monster.hoglin.Hoglin` | baby 8 |
| 11 | `animal.parrot.Parrot` | |
| 11 | `monster.spider.Spider` | |
| 11 | `monster.warden.Warden` | |
| 11 | `monster.Zoglin` | baby 8 |
| 12 | `animal.equine.Horse` | baby 10 |
| 12 | `animal.equine.SkeletonHorse` | baby 10 |
| 12 | `animal.equine.ZombieHorse` | baby 10 |
| 12 | `player.Player` | player and player_slim |
| 13 | `animal.armadillo.Armadillo` | baby 12 |
| 13 | `monster.Blaze` | |
| 13 | `animal.rabbit.Rabbit` | baby 17 |
| 13 | `animal.sniffer.Sniffer` | |
| 13 | `monster.Witch` | |
| 14 | `monster.skeleton.Bogged` | |
| 14 | `animal.equine.Donkey` | baby 15 |
| 14 | `animal.equine.Mule` | baby 15 |
| 14 | `monster.piglin.Piglin` | baby 11 |
| 14 | `monster.piglin.PiglinBrute` | |
| 14 | `monster.zombie.ZombifiedPiglin` | baby 11 |
| 16 | `animal.frog.Frog` | |
| 17 | `monster.ElderGuardian` | |
| 17 | `monster.Guardian` | |
| 36 | `boss.enderdragon.EnderDragon` | |
