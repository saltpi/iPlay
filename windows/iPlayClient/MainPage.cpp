#include "pch.h"
#include "MainPage.h"
#if __has_include("MainPage.g.cpp")
#include "MainPage.g.cpp"
#endif

#include "App.h"

#include <winrt/Windows.UI.Core.h>
#include <winrt/Windows.UI.ViewManagement.h>
#include <winrt/Windows.ApplicationModel.h>
#include <winrt/Windows.ApplicationModel.Core.h>
#include <winrt/Windows.UI.h>
#include "TitleBar.h"

using namespace winrt;
using namespace xaml;
using namespace winrt::Windows::UI::ViewManagement;
using namespace winrt::Windows::UI;
using namespace winrt::Windows::ApplicationModel::Core;
using namespace top::ourfor::app::iPlayClient;

namespace winrt::iPlayClient::implementation
{
    void setupTitlebar() {
        auto view = CoreApplication::GetCurrentView();
        auto appTitleBar = view.TitleBar();
        auto titleBarHeight = appTitleBar.Height();
        NativeModuleSample::kTitleBarHeight = titleBarHeight == 0 ? 48 : titleBarHeight;
        // appTitleBar.ExtendViewIntoTitleBar(true);

        auto navigation = SystemNavigationManager::GetForCurrentView();
        navigation.AppViewBackButtonVisibility(AppViewBackButtonVisibility::Visible);
        navigation.BackRequested([](auto const& sender, auto const& args) {
		});

        auto appView = ApplicationView::GetForCurrentView();
        appView.Title(L"Home");
        //auto titleBar = appView.TitleBar();
        //titleBar.ForegroundColor(Colors::Transparent());
        //titleBar.BackgroundColor(Colors::Transparent());
        //titleBar.ButtonBackgroundColor(Colors::Transparent());
        //titleBar.ButtonForegroundColor(Colors::Transparent());
    }

    MainPage::MainPage()
    {
        InitializeComponent();
        setupTitlebar();
        auto app = Application::Current().as<App>();
        ReactRootView().ReactNativeHost(app->Host());
    }

}
