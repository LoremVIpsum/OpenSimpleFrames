package com.loremv.simpleframes.mixin;

import com.loremv.simpleframes.SimpleFrames;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin is used to track whether we should be capturing new models in BakedQuadFactory
 * it's literally the example mixin
 * is there a better way of doing this? maybe.
 * realistically does this have so much impact on the game? no.
 */
@Mixin(TitleScreen.class)
public class TitleScreenCaptureMixin {
	private static final String[] STATE = new String[]{"NOT_STARTED","READY","FINISHED"};

	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		if(SimpleFrames.STATE<2)
		{
			SimpleFrames.STATE++;
			SimpleFrames.LOGGER.info("Frame capturing state elevated to "+SimpleFrames.STATE+" ["+STATE[SimpleFrames.STATE]+"]");
		}

	}


}
