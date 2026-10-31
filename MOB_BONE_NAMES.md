# Mob bones, by name

Every bone name an animation can target, per mob class, from the baked model layers in Minecraft 26.3.
Indentation shows nesting; a bone is found by name wherever it is in the tree, so `left_horn` under `head` is just `left_horn`.
A wrapper node called `root` that some models put around everything is left out. Counts are the number of bones in the adult/default model, as in [MOB_BONES.md](MOB_BONES.md).
Variants (baby, no-hat, poses) list only the bones that differ from the default.

## `monster.cubemob.SulfurCube` (1 bones)

```
cube
```

- `sulfur_cube_small`: same bones

## `animal.frog.Tadpole` (2 bones)

_`body`, `tail`_

```
tail
body
```


## `monster.Shulker` (3 bones)

```
head
lid
base
```

- `shulker_box`: without head
- `shulker_bullet`: adds main; without base, head, lid

## `monster.Endermite` (4 bones)

```
segment2
segment1
segment0
segment3
```


## `monster.cubemob.Slime` (4 bones)

```
right_eye
mouth
left_eye
cube
```


## `animal.golem.SnowGolem` (5 bones)

```
head
right_arm
upper_body
left_arm
lower_body
```


## `animal.fish.TropicalFish` (5 bones)

_small 5, large 6_

```
left_fin
top_fin
tail
right_fin
body
```

`tropical_fish_large`: bottom_fin


## `animal.cow.Cow` (6 bones)

```
head
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `cow_baby`: same bones

## `animal.cow.MushroomCow` (6 bones)

```
head
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `mooshroom_baby`: same bones

## `animal.golem.CopperGolem` (6 bones)

_running, sitting and star poses 11_

```
left_leg
right_leg
body
  head
  right_arm
  left_arm
```

- `copper_golem_running`: adds body/body_r1, body/left_arm/left_arm_r1, body/right_arm/right_arm_r1, left_leg/left_leg_r1, right_leg/right_leg_r1
- `copper_golem_sitting`: adds body/body_r1, body/left_arm/left_arm_r1, body/right_arm/right_arm_r1, left_leg/left_leg_r1, right_leg/right_leg_r1
- `copper_golem_star`: adds body/left_arm/left_arm_r1, body/right_arm/rightItem, body/right_arm/right_arm_r1, left_leg/left_leg_r1, right_leg/right_leg_r1

## `monster.Creeper` (6 bones)

```
head
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `creeper_head`: without body, left_front_leg, left_hind_leg, right_front_leg, right_hind_leg

## `animal.golem.IronGolem` (6 bones)

```
head
right_arm
left_leg
left_arm
right_leg
body
```


## `animal.nautilus.Nautilus` (6 bones)

```
shell
body
  lower_mouth
  upper_mouth
  inner_mouth
```

- `nautilus_armor`: same bones
- `nautilus_baby`: same bones

## `animal.nautilus.ZombieNautilus` (6 bones)

_coral model 18_

```
shell
body
  lower_mouth
  upper_mouth
  inner_mouth
```

- `zombie_nautilus_coral`: adds shell/corals, shell/corals/blue_coral, shell/corals/blue_coral/blue_first, shell/corals/blue_coral/blue_second, shell/corals/pink_coral, shell/corals/pink_coral/pink_coral_second, shell/corals/red_coral, shell/corals/red_coral/red_coral_first, shell/corals/red_coral/red_coral_second, shell/corals/yellow_coral, shell/corals/yellow_coral/yellow_coral_first, shell/corals/yellow_coral/yellow_coral_second

## `animal.panda.Panda` (6 bones)

```
head
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `panda_baby`: same bones

## `animal.pig.Pig` (6 bones)

```
head
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `pig_baby`: same bones

## `animal.polarbear.PolarBear` (6 bones)

```
head
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `polar_bear_baby`: same bones

## `animal.sheep.Sheep` (6 bones)

```
head
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `sheep_baby`: same bones

## `boss.wither.WitherBoss` (6 bones)

```
shoulders
ribcage
tail
left_head
right_head
center_head
```

- `wither_skull`: adds head; without center_head, left_head, ribcage, right_head, shoulders, tail

## `animal.fish.Pufferfish` (6 bones)

_small 6, medium 11, big 13_

```
right_eye
left_fin
right_fin
left_eye
body
back_fin
```

`pufferfish_medium`: back_fin, bottom_back_fin, bottom_front_fin, left_back_fin, left_blue_fin, left_eye, left_fin, left_front_fin, right_back_fin, right_blue_fin, right_eye, right_fin, right_front_fin, top_back_fin, top_front_fin

`pufferfish_big`: back_fin, bottom_back_fin, bottom_front_fin, bottom_middle_fin, left_back_fin, left_blue_fin, left_eye, left_fin, left_front_fin, right_back_fin, right_blue_fin, right_eye, right_fin, right_front_fin, top_back_fin, top_front_fin, top_middle_fin


## `animal.allay.Allay` (7 bones)

```
head
body
  right_arm
  left_arm
  right_wing
  left_wing
```


## `animal.fish.Cod` (7 bones)

```
head
nose
left_fin
top_fin
right_fin
body
tail_fin
```


## `monster.Enderman` (7 bones)

```
head
  hat
right_arm
left_leg
left_arm
right_leg
body
```


## `monster.Giant` (7 bones)

```
head
  hat
right_arm
left_leg
body
left_arm
right_leg
```


## `monster.skeleton.Parched` (7 bones)

```
head
  hat
right_arm
left_leg
left_arm
right_leg
body
```


## `monster.skeleton.Skeleton` (7 bones)

```
head
  hat
right_arm
left_leg
left_arm
right_leg
body
```

- `skeleton_skull`: without body, head/hat, left_arm, left_leg, right_arm, right_leg

## `monster.skeleton.Stray` (7 bones)

```
head
  hat
right_arm
left_leg
left_arm
right_leg
body
```


## `animal.turtle.Turtle` (7 bones)

_baby 6_

```
head
right_front_leg
right_hind_leg
egg_belly
left_hind_leg
body
left_front_leg
```

- `turtle_baby`: without egg_belly

## `monster.Vex` (7 bones)

```
head
body
  right_arm
  left_arm
  right_wing
  left_wing
```


## `monster.skeleton.WitherSkeleton` (7 bones)

```
head
  hat
right_arm
left_leg
body
left_arm
right_leg
```

- `wither_skeleton_skull`: without body, head/hat, left_arm, left_leg, right_arm, right_leg

## `monster.zombie.Zombie` (7 bones)

```
head
  hat
right_arm
left_leg
left_arm
right_leg
body
```

- `zombie_baby`: same bones
- `zombie_head`: without body, left_arm, left_leg, right_arm, right_leg

## `monster.zombie.Husk` (7 bones)

```
head
  hat
right_arm
left_leg
body
left_arm
right_leg
```

- `husk_baby`: same bones

## `monster.zombie.Drowned` (7 bones)

```
head
  hat
right_arm
left_leg
left_arm
right_leg
body
```

- `drowned_baby`: same bones

## `animal.feline.Cat` (8 bones)

```
head
tail1
right_front_leg
tail2
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `cat_baby`: same bones

## `animal.chicken.Chicken` (8 bones)

_baby 5_

```
head
  beak
  red_thing
left_leg
right_leg
right_wing
left_wing
body
```

- `chicken_baby`: without head, head/beak, head/red_thing

## `monster.creaking.Creaking` (8 bones)

```
upper_body
  head
  right_arm
  left_arm
  body
left_leg
right_leg
```


## `animal.dolphin.Dolphin` (8 bones)

```
body
  head
    nose
  left_fin
  right_fin
  tail
    tail_fin
  back_fin
```

- `dolphin_baby`: same bones

## `animal.equine.Llama` (8 bones)

```
head
right_front_leg
right_hind_leg
right_chest
left_hind_leg
body
left_front_leg
left_chest
```

- `llama_baby`: same bones
- `llama_spit`: adds main; without body, head, left_chest, left_front_leg, left_hind_leg, right_chest, right_front_leg, right_hind_leg

## `animal.feline.Ocelot` (8 bones)

```
head
tail1
right_front_leg
tail2
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `ocelot_baby`: same bones

## `monster.Phantom` (8 bones)

```
body
  head
  right_wing_base
    right_wing_tip
  tail_base
    tail_tip
  left_wing_base
    left_wing_tip
```


## `animal.fish.Salmon` (8 bones)

```
head
left_fin
body_back
  top_back_fin
  back_fin
right_fin
body_front
  top_front_fin
```

- `salmon_large`: same bones
- `salmon_small`: same bones

## `animal.equine.TraderLlama` (8 bones)

```
head
right_front_leg
right_hind_leg
right_chest
left_hind_leg
body
left_front_leg
left_chest
```

- `trader_llama_baby`: same bones

## `monster.zombie.ZombieVillager` (8 bones)

_baby 9_

```
head
  hat
    hat_rim
right_arm
left_leg
left_arm
right_leg
body
```

- `zombie_villager_baby`: adds head/hat_rim, head/nose; without head/hat/hat_rim
- `zombie_villager_baby_no_hat`: adds head/hat_rim, head/nose; without head/hat/hat_rim
- `zombie_villager_no_hat`: same bones

## `ambient.Bat` (9 bones)

```
head
  right_ear
  left_ear
body
  feet
  right_wing
    right_wing_tip
  left_wing
    left_wing_tip
```


## `animal.goat.Goat` (9 bones)

_baby 11_

```
head
  nose
  right_horn
  left_horn
right_front_leg
right_hind_leg
left_hind_leg
body
left_front_leg
```

- `goat_baby`: adds head/HeadMain, head/left_ear, head/right_ear; without head/nose

## `animal.squid.GlowSquid` (9 bones)

```
tentacle0
tentacle1
tentacle6
tentacle7
body
tentacle4
tentacle5
tentacle2
tentacle3
```

- `glow_squid_baby`: same bones

## `monster.cubemob.MagmaCube` (9 bones)

```
cube0
cube2
cube1
cube4
cube3
cube6
cube5
cube7
inside_cube
```


## `animal.squid.Squid` (9 bones)

```
tentacle0
tentacle1
tentacle6
tentacle7
body
tentacle4
tentacle5
tentacle2
tentacle3
```

- `squid_baby`: same bones

## `monster.Strider` (9 bones)

_baby 6_

```
left_leg
right_leg
body
  right_top_bristle
  right_bottom_bristle
  left_top_bristle
  left_bottom_bristle
  right_middle_bristle
  left_middle_bristle
```

- `strider_baby`: adds body/bristle0, body/bristle1, body/bristle2; without body/left_bottom_bristle, body/left_middle_bristle, body/left_top_bristle, body/right_bottom_bristle, body/right_middle_bristle, body/right_top_bristle

## `npc.villager.Villager` (9 bones)

_baby 11_

```
head
  nose
  hat
    hat_rim
arms
left_leg
body
  jacket
right_leg
```

- `villager_baby`: adds arms/middlearm_r1, arms/right_hand, bb_main, head/hat_rim; without body/jacket, head/hat/hat_rim
- `villager_baby_no_hat`: adds arms/middlearm_r1, arms/right_hand, bb_main, head/hat_rim; without body/jacket, head/hat/hat_rim
- `villager_no_hat`: same bones

## `npc.wanderingtrader.WanderingTrader` (9 bones)

```
head
  nose
  hat
    hat_rim
arms
left_leg
body
  jacket
right_leg
```


## `animal.axolotl.Axolotl` (10 bones)

_baby 12_

```
body
  head
    top_gills
    left_gills
    right_gills
  right_front_leg
  right_hind_leg
  tail
  left_hind_leg
  left_front_leg
```

- `axolotl_baby`: adds body/right_hind_leg/right_leg_r1

## `animal.bee.Bee` (10 bones)

_baby 8_

```
bone
  front_legs
  right_wing
  left_wing
  middle_legs
  body
    right_antenna
    stinger
    left_antenna
  back_legs
```

- `bee_baby`: without bone/body/left_antenna, bone/body/right_antenna
- `bee_stinger`: adds cross_1, cross_2; without bone, bone/back_legs, bone/body, bone/body/left_antenna, bone/body/right_antenna, bone/body/stinger, bone/front_legs, bone/left_wing, bone/middle_legs, bone/right_wing

## `animal.camel.Camel` (10 bones)

_baby 9_

```
right_front_leg
right_hind_leg
left_hind_leg
body
  head
    right_ear
    left_ear
  hump
  tail
left_front_leg
```

- `camel_baby`: without body/hump

## `monster.illager.Evoker` (10 bones)

```
head
  nose
  hat
left_leg
right_arm
right_leg
left_arm
arms
  left_shoulder
body
```

- `evoker_fangs`: adds base, base/lower_jaw, base/upper_jaw; without arms, arms/left_shoulder, body, head, head/hat, head/nose, left_arm, left_leg, right_arm, right_leg

## `animal.fox.Fox` (10 bones)

_baby 7_

```
head
  nose
  right_ear
  left_ear
right_front_leg
right_hind_leg
left_hind_leg
body
  tail
left_front_leg
```

- `fox_baby`: without head/left_ear, head/nose, head/right_ear

## `monster.Ghast` (10 bones)

```
tentacle0
tentacle1
tentacle8
tentacle6
tentacle7
body
tentacle4
tentacle5
tentacle2
tentacle3
```


## `animal.happyghast.HappyGhast` (10 bones)

_baby 11_

```
body
  tentacle0
  tentacle1
  tentacle8
  tentacle6
  tentacle7
  tentacle4
  tentacle5
  tentacle2
  tentacle3
```

- `happy_ghast_baby`: adds body/inner_body
- `happy_ghast_baby_harness`: adds goggles, harness; without body, body/tentacle0, body/tentacle1, body/tentacle2, body/tentacle3, body/tentacle4, body/tentacle5, body/tentacle6, body/tentacle7, body/tentacle8
- `happy_ghast_baby_ropes`: adds body/inner_body
- `happy_ghast_harness`: adds goggles, harness; without body, body/tentacle0, body/tentacle1, body/tentacle2, body/tentacle3, body/tentacle4, body/tentacle5, body/tentacle6, body/tentacle7, body/tentacle8
- `happy_ghast_ropes`: same bones

## `monster.illager.Illusioner` (10 bones)

```
head
  nose
  hat
left_leg
right_arm
right_leg
left_arm
arms
  left_shoulder
body
```


## `monster.illager.Pillager` (10 bones)

```
head
  nose
  hat
left_leg
right_arm
right_leg
left_arm
arms
  left_shoulder
body
```


## `monster.Ravager` (10 bones)

```
right_front_leg
right_hind_leg
left_hind_leg
neck
  head
    right_horn
    mouth
    left_horn
body
left_front_leg
```


## `monster.Silverfish` (10 bones)

```
segment2
segment1
segment0
layer0
layer1
layer2
segment6
segment5
segment4
segment3
```


## `monster.illager.Vindicator` (10 bones)

```
head
  nose
  hat
left_leg
right_arm
right_leg
left_arm
arms
  left_shoulder
body
```


## `animal.wolf.Wolf` (10 bones)

```
head
  real_head
right_front_leg
right_hind_leg
upper_body
tail
  real_tail
left_hind_leg
body
left_front_leg
```

- `wolf_armor`: same bones
- `wolf_baby`: adds head/left_ear, head/right_ear, tail/tail_r1; without head/real_head, tail/real_tail, upper_body

## `monster.breeze.Breeze` (11 bones)

```
body
  head
    eyes
  rods
    rod_2
    rod_3
    rod_1
wind_body
  wind_bottom
    wind_mid
      wind_top
```


## `monster.spider.CaveSpider` (11 bones)

```
head
right_front_leg
right_hind_leg
left_middle_front_leg
body0
body1
left_hind_leg
right_middle_hind_leg
right_middle_front_leg
left_middle_hind_leg
left_front_leg
```


## `monster.hoglin.Hoglin` (11 bones)

_baby 8_

```
head
  right_horn
  left_horn
  right_ear
  left_ear
right_front_leg
right_hind_leg
left_hind_leg
body
  mane
left_front_leg
```

- `hoglin_baby`: without body/mane, head/left_horn, head/right_horn

## `animal.parrot.Parrot` (11 bones)

```
head
  beak1
  beak2
  feather
  head2
left_leg
right_wing
right_leg
tail
left_wing
body
```


## `monster.spider.Spider` (11 bones)

```
head
right_front_leg
right_hind_leg
left_middle_front_leg
body0
body1
left_hind_leg
right_middle_hind_leg
right_middle_front_leg
left_middle_hind_leg
left_front_leg
```


## `monster.warden.Warden` (11 bones)

```
bone
  left_leg
  right_leg
  body
    head
      right_tendril
      left_tendril
    right_arm
    left_arm
    left_ribcage
    right_ribcage
```


## `monster.Zoglin` (11 bones)

_baby 8_

```
head
  right_horn
  left_horn
  right_ear
  left_ear
right_front_leg
right_hind_leg
left_hind_leg
body
  mane
left_front_leg
```

- `zoglin_baby`: without body/mane, head/left_horn, head/right_horn

## `animal.equine.Horse` (12 bones)

_baby 10_

```
right_front_leg
right_hind_leg
head_parts
  head
    right_ear
    left_ear
  mane
  upper_mouth
left_hind_leg
body
  tail
left_front_leg
```

- `horse_armor`: same bones
- `horse_baby`: without head_parts/mane, head_parts/upper_mouth

## `animal.equine.SkeletonHorse` (12 bones)

_baby 10_

```
right_front_leg
right_hind_leg
head_parts
  head
    right_ear
    left_ear
  mane
  upper_mouth
left_hind_leg
body
  tail
left_front_leg
```

- `skeleton_horse_baby`: without head_parts/mane, head_parts/upper_mouth

## `animal.equine.ZombieHorse` (12 bones)

_baby 10_

```
right_front_leg
right_hind_leg
head_parts
  head
    right_ear
    left_ear
  mane
  upper_mouth
left_hind_leg
body
  tail
left_front_leg
```

- `zombie_horse_baby`: without head_parts/mane, head_parts/upper_mouth

## `player.Player` (12 bones)

_player and player_slim_

```
head
  hat
right_arm
  right_sleeve
left_leg
  left_pants
left_arm
  left_sleeve
right_leg
  right_pants
body
  jacket
```

- `player_head`: without body, body/jacket, left_arm, left_arm/left_sleeve, left_leg, left_leg/left_pants, right_arm, right_arm/right_sleeve, right_leg, right_leg/right_pants
- `player_slim`: same bones

## `animal.armadillo.Armadillo` (13 bones)

_baby 12_

```
right_front_leg
right_hind_leg
left_hind_leg
cube
body
  head
    head_cube
    right_ear
      right_ear_cube
    left_ear
      left_ear_cube
  tail
left_front_leg
```

- `armadillo_baby`: adds body/head/head_cube/left_ear, body/head/head_cube/right_ear, body/tail/right_ear_cube; without body/head/left_ear, body/head/left_ear/left_ear_cube, body/head/right_ear, body/head/right_ear/right_ear_cube

## `monster.Blaze` (13 bones)

```
part0
part1
part2
head
part3
part11
part4
part10
part5
part6
part7
part8
part9
```


## `animal.rabbit.Rabbit` (13 bones)

_baby 17_

```
backlegs
  right_hind_leg
    right_haunch
  left_hind_leg
    left_haunch
body
  head
    right_ear
    left_ear
  tail
  frontlegs
    right_front_leg
    left_front_leg
```

- `rabbit_baby`: adds body/body_r1, body/frontlegs/left_front_leg/left_front_leg_r1, body/frontlegs/right_front_leg/right_front_leg_r1, body/tail/tail_r1

## `animal.sniffer.Sniffer` (13 bones)

```
bone
  right_front_leg
  right_hind_leg
  right_mid_leg
  left_hind_leg
  body
    head
      nose
      lower_beak
      right_ear
      left_ear
  left_mid_leg
  left_front_leg
```

- `sniffer_baby`: same bones

## `monster.Witch` (13 bones)

```
head
  nose
    mole
  hat
    hat_rim
    hat2
      hat3
        hat4
arms
left_leg
body
  jacket
right_leg
```


## `monster.skeleton.Bogged` (14 bones)

```
head
  mushrooms
    brown_mushroom_3
    brown_mushroom_2
    brown_mushroom_4
    red_mushroom_1
    red_mushroom_2
    brown_mushroom_1
  hat
right_arm
left_leg
left_arm
right_leg
body
```


## `animal.equine.Donkey` (14 bones)

_baby 15_

```
right_front_leg
right_hind_leg
head_parts
  head
    right_ear
    left_ear
  mane
  upper_mouth
left_hind_leg
body
  right_chest
  tail
  left_chest
left_front_leg
```

- `donkey_baby`: adds body/head_parts, body/head_parts/head, body/head_parts/head/head_r1, body/head_parts/head/left_ear, body/head_parts/head/right_ear, body/head_parts/neck_r1, body/left_front_leg, body/left_hind_leg, body/right_front_leg, body/right_hind_leg, body/tail/tail_r1; without head_parts, head_parts/head, head_parts/head/left_ear, head_parts/head/right_ear, head_parts/mane, head_parts/upper_mouth, left_front_leg, left_hind_leg, right_front_leg, right_hind_leg

## `animal.equine.Mule` (14 bones)

_baby 15_

```
right_front_leg
right_hind_leg
head_parts
  head
    right_ear
    left_ear
  mane
  upper_mouth
left_hind_leg
body
  right_chest
  tail
  left_chest
left_front_leg
```

- `mule_baby`: adds body/head_parts, body/head_parts/head, body/head_parts/head/head_r1, body/head_parts/head/left_ear, body/head_parts/head/right_ear, body/head_parts/neck_r1, body/left_front_leg, body/left_hind_leg, body/right_front_leg, body/right_hind_leg, body/tail/tail_r1; without head_parts, head_parts/head, head_parts/head/left_ear, head_parts/head/right_ear, head_parts/mane, head_parts/upper_mouth, left_front_leg, left_hind_leg, right_front_leg, right_hind_leg

## `monster.piglin.Piglin` (14 bones)

_baby 11_

```
head
  right_ear
  hat
  left_ear
right_arm
  right_sleeve
left_leg
  left_pants
left_arm
  left_sleeve
right_leg
  right_pants
body
  jacket
```

- `piglin_baby`: adds head/left_ear/left_ear_r1, head/right_ear/right_ear_r1; without body/jacket, left_arm/left_sleeve, left_leg/left_pants, right_arm/right_sleeve, right_leg/right_pants
- `piglin_head`: without body, body/jacket, head/hat, left_arm, left_arm/left_sleeve, left_leg, left_leg/left_pants, right_arm, right_arm/right_sleeve, right_leg, right_leg/right_pants

## `monster.piglin.PiglinBrute` (14 bones)

```
head
  right_ear
  hat
  left_ear
right_arm
  right_sleeve
left_leg
  left_pants
left_arm
  left_sleeve
right_leg
  right_pants
body
  jacket
```


## `monster.zombie.ZombifiedPiglin` (14 bones)

_baby 11_

```
head
  right_ear
  hat
  left_ear
right_arm
  right_sleeve
left_leg
  left_pants
left_arm
  left_sleeve
right_leg
  right_pants
body
  jacket
```

- `zombified_piglin_baby`: adds head/left_ear/left_ear_r1, head/right_ear/right_ear_r1; without body/jacket, left_arm/left_sleeve, left_leg/left_pants, right_arm/right_sleeve, right_leg/right_pants

## `animal.frog.Frog` (16 bones)

```
left_leg
  left_foot
right_leg
  right_foot
body
  head
    eyes
      right_eye
      left_eye
  right_arm
    right_hand
  tongue
  left_arm
    left_hand
  croaking_body
```


## `monster.ElderGuardian` (17 bones)

```
head
  spike10
  spike11
  spike6
  spike7
  spike8
  spike9
  eye
  tail0
    tail1
      tail2
  spike0
  spike1
  spike2
  spike3
  spike4
  spike5
```


## `monster.Guardian` (17 bones)

```
head
  spike10
  spike11
  spike6
  spike7
  spike8
  spike9
  eye
  tail0
    tail1
      tail2
  spike0
  spike1
  spike2
  spike3
  spike4
  spike5
```


## `boss.enderdragon.EnderDragon` (36 bones)

```
neck4
neck3
tail11
neck0
body
  right_front_leg
    right_front_leg_tip
      right_front_foot
  right_hind_leg
    right_hind_leg_tip
      right_hind_foot
  right_wing
    right_wing_tip
  left_wing
    left_wing_tip
  left_hind_leg
    left_hind_leg_tip
      left_hind_foot
  left_front_leg
    left_front_leg_tip
      left_front_foot
neck2
neck1
head
  jaw
tail0
tail1
tail2
tail3
tail4
tail5
tail10
tail6
tail7
tail8
tail9
```

