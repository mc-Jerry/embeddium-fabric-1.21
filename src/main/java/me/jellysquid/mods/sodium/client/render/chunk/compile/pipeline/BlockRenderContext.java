package me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.embeddedt.embeddium.model.UnwrappableBakedModel;
import org.embeddedt.embeddium.render.matrix_stack.CachingPoseStack;
import org.embeddedt.embeddium.render.world.WorldSliceLocalGenerator;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class BlockRenderContext {
    private final WorldSlice world;
    private final BlockAndTintGetter localSlice;

    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private final Vector3f origin = new Vector3f();

    private final PoseStack stack = new PoseStack();

    private BlockState state;
    private BakedModel model;

    private long seed;

    private RenderType renderLayer;


    public BlockRenderContext(WorldSlice world) {
        this.world = world;
        this.localSlice = WorldSliceLocalGenerator.generate(world);
        ((CachingPoseStack)this.stack).embeddium$setCachingEnabled(true);
    }

    public void update(BlockPos pos, BlockPos origin, BlockState state, BakedModel model, long seed) {
        this.pos.set(pos);
        this.origin.set(origin.getX(), origin.getY(), origin.getZ());

        this.state = state;
        this.model = model;

        this.seed = seed;

        this.renderLayer = ItemBlockRenderTypes.getChunkRenderType(state);
    }

    /**
     * @return The position (in world space) of the block being rendered
     */
    public BlockPos pos() {
        return this.pos;
    }

    /**
     * @return The world which the block is being rendered from
     */
    public WorldSlice world() {
        return this.world;
    }

    /**
     * @return The world which the block is being rendered from. Guaranteed to be a new object for each subchunk.
     */
    public BlockAndTintGetter localSlice() {
        return this.localSlice;
    }

    /**
     * @return The state of the block being rendered
     */
    public BlockState state() {
        return this.state;
    }

    /**
     * @return A PoseStack for custom renderers
     */
    public PoseStack stack() {
        return this.stack;
    }

    /**
     * @return The model used for this block
     */
    public BakedModel model() {
        return this.model;
    }

    /**
     * @return The origin of the block within the model
     */
    public Vector3fc origin() {
        return this.origin;
    }

    /**
     * @return The PRNG seed for rendering this block
     */
    public long seed() {
        return this.seed;
    }

    /**
     * @return null on Fabric, as it doesn't have model data
     */
    @Deprecated
    public Object modelData() {
        return null;
    }

    /**
     * @return The render layer for model rendering
     */
    public RenderType renderLayer() {
        return this.renderLayer;
    }

    void maybeUnwrapModel(RandomSource random) {
        random.setSeed(this.seed);
        this.model = UnwrappableBakedModel.unwrapIfPossible(this.model, random);
    }
}
