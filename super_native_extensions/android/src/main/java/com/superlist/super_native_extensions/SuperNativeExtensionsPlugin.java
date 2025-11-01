package com.superlist.super_native_extensions;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

/**
 * SuperNativeExtensionsPlugin
 */
public class SuperNativeExtensionsPlugin implements FlutterPlugin {

    static final ClipDataHelper ClipDataHelper = new ClipDataHelper();
    static final DragDropHelper DragDropHelper = new DragDropHelper();

    private static boolean nativeInitialized = false;
    private static boolean libraryLoaded = false;

    private static boolean isEmulatorAbi() {
        try {
            String[] abis = android.os.Build.SUPPORTED_ABIS;
            for (String abi : abis) {
                if ("x86_64".equalsIgnoreCase(abi) || "x86".equalsIgnoreCase(abi)) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        try {
            if (!nativeInitialized && libraryLoaded) {
                init(flutterPluginBinding.getApplicationContext(), ClipDataHelper, DragDropHelper);
                nativeInitialized = true;
            }
        } catch (Throwable e) {
            Log.e("flutter", e.toString());
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

    public static native void init(Context context,
                                   ClipDataHelper ClipDataHelper,
                                   DragDropHelper DragDropHelper);

    static {
        // Only attempt to load native library on emulator ABIs (x86/x86_64).
        // On phones (arm/arm64) this plugin is a no-op to avoid packaging and loading native .so.
        if (isEmulatorAbi()) {
            try {
                System.loadLibrary("super_native_extensions");
                libraryLoaded = true;
            } catch (Throwable t) {
                Log.e("flutter", "Failed to load libsuper_native_extensions.so: " + t);
                libraryLoaded = false;
            }
        } else {
            libraryLoaded = false;
        }
    }
}
