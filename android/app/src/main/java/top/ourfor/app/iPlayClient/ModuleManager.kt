package top.ourfor.app.iPlayClient

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext

class ModuleManager : ReactPackage {

    override fun createViewManagers(
        reactContext: ReactApplicationContext
    ) = listOf(
        PlayerViewManager(reactContext)
    ).toMutableList()

    override fun createNativeModules(
        reactContext: ReactApplicationContext
    ): MutableList<NativeModule> = listOf(
        IntentModule(reactContext)
    ).toMutableList()
}