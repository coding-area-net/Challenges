package net.codingarea.challenges.plugin.spigot.generator;

import net.anweisen.utilities.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.generator.ChunkGenerator;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class VoidMapGenerator extends ChunkGenerator {

	private static final boolean generateEndPortal = MinecraftVersion.current().getMinor() == 18;

//  private static class PortalInfo {
//
//    private final int chunkX;
//    private final int y;
//    private final int chunkZ;
//
//    public PortalInfo(int chunkX, int y, int chunkZ) {
//      this.chunkX = chunkX;
//      this.y = y;
//      this.chunkZ = chunkZ;
//    }
//
//  }

//  private List<PortalInfo> portalChunks;
//
//  public void loadStrongholds(World world) {
//
//    for (int i = 0; i < 3; i++) {
//      Bukkit.getScheduler().runTaskLater(Challenges.getInstance().getPlugin(), () -> {
//        Location portal = world
//            .locateNearestStructure(new Location(world, 0, 0, 0), StructureType.STRONGHOLD, 180,
//                true);
//        if (portal != null) {
//          Chunk chunk = portal.getChunk();
//          chunk.load(true);
//          portalChunks.add(new PortalInfo(chunk.getX(), portal.getBlockY(), chunk.getZ()));
//        }
//      }, i*5);
//
//    }
//
//
//  }

	@Override
	@Nonnull
	public ChunkData generateChunkData(@Nonnull World world, @Nonnull Random random, int x, int z, @Nonnull BiomeGrid biome) {

//    if (portalChunks == null) {
//      portalChunks = Lists.newLinkedList();
//      Bukkit.getScheduler().runTask(Challenges.getInstance().getPlugin(), () -> {
//        loadStrongholds(world);
//      });
//    }

		ChunkData chunkData = createChunkData(world);
		if (x == 0 && z == 0) {
			chunkData.setBlock(0, 59, 0, Material.BEDROCK);
		}

		// Stronghold location is weirdly the same for every void map
		// TODO: FIX FOLLOWING ISSUES
		// Only works in 1.18 worlds
		// maybe try to locate the three first strongholds and place portal in chunk (code commented out above)
		// Maybe don't generate a portal at all to prevent issues with the feature
		if (generateEndPortal) {
			if (x == -7 && z == -105) {
				generateEndPortal(chunkData);
			}
		}

//      if (portalChunk.chunkX == x && portalChunk.chunkZ == z) {
//        Challenges.getInstance().getLogger().info("Generating End Portal");
//        generateEndPortal(chunkData);
//        break;
//      }
//    }

		return chunkData;
	}

	public void generateEndPortal(@Nonnull ChunkData data) {

		int x = 6;
		int y = 29;
		int z = 6;
		for (int x1 = 0; x1 < 5; x1++) {

			for (int z1 = 0; z1 < 5; z1++) {
				if ((x1 == 0 && z1 == 0) || (x1 == 4 && z1 == 4) ||
						(x1 == 0 && z1 == 4) || (x1 == 4 && z1 == 0)) continue;
				if (x1 > 0 && z1 > 0 && x1 < 4 && z1 < 4) continue;

				Directional blockData = (Directional) Bukkit.createBlockData(Material.END_PORTAL_FRAME);

				if (x1 == 0) {
					blockData.setFacing(BlockFace.EAST);
				} else if (x1 == 4) {
					blockData.setFacing(BlockFace.WEST);
				} else if (z1 == 0) {
					blockData.setFacing(BlockFace.SOUTH);
				} else {
					blockData.setFacing(BlockFace.NORTH);
				}

				data.setBlock(x + x1, y, z + z1, blockData);
			}
		}

	}

}
