#include "pch.h"
#include "ReactPackageProvider.h"
#include "NativeModules.h"
#include "TitleBar.h"

using namespace winrt::Microsoft::ReactNative;

namespace winrt::iPlayClient::implementation
{

void ReactPackageProvider::CreatePackage(IReactPackageBuilder const &packageBuilder) noexcept
{
    AddAttributedModules(packageBuilder, true);
    packageBuilder.AddTurboModule(L"TitleBar", MakeModuleProvider<NativeModuleSample::TitleBar>());
}

} // namespace winrt::iPlayClient::implementation
