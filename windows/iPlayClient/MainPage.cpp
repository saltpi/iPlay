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

using namespace winrt;
using namespace xaml;
using namespace winrt::Windows::UI::ViewManagement;
using namespace winrt::Windows::UI;
using namespace winrt::Windows::ApplicationModel::Core;

namespace winrt::iPlayClient::implementation
{
    MainPage::MainPage()
    {
        InitializeComponent();
        
        auto app = Application::Current().as<App>();
        ReactRootView().ReactNativeHost(app->Host());

        auto view = CoreApplication::GetCurrentView();
        auto appTitleBar = view.TitleBar();
        appTitleBar.ExtendViewIntoTitleBar(true);

        auto appView = ApplicationView::GetForCurrentView();
        appView.Title(L"Hello World");
        auto titleBar = appView.TitleBar();
        titleBar.ForegroundColor(Colors::Transparent());
        titleBar.BackgroundColor(Colors::Transparent());
        titleBar.ButtonBackgroundColor(Colors::Transparent());
        titleBar.ButtonForegroundColor(Colors::Transparent());

    }
}
