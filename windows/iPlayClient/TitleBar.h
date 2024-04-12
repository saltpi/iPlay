#pragma once

#include "pch.h"

#include <codegen\NativeTitleBarSpec.g.h>
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

using namespace top::ourfor::app::iPlayClient;


#include "NativeModules.h"

namespace NativeModuleSample
{
    REACT_MODULE(TitleBar);
    struct TitleBar
    {
        using ModuleSpec = top::ourfor::app::iPlayClient::TitleBarSpec;
        

        REACT_GET_CONSTANTS(GetConstants)
            TitleBarSpec_Constants GetConstants() noexcept {
            TitleBarSpec_Constants constants;
            constants.titleBarHeight = -128;
            return constants;
        }

        REACT_METHOD(Add, L"add");
        double Add(double a, double b) noexcept
        {
            auto view = CoreApplication::GetCurrentView();
            auto appTitleBar = view.TitleBar();
            double result = appTitleBar.Height();
            AddEvent(result);
            return result;
        }

        REACT_EVENT(AddEvent);
        std::function<void(double)> AddEvent;
    };
}