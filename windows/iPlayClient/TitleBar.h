#pragma once

#include "pch.h"

#include <codegen\NativeTitleBarSpec.g.h>
using namespace top::ourfor::app::iPlayClient;


#include "NativeModules.h"

namespace NativeModuleSample
{
    static double kTitleBarHeight;

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
            double result = kTitleBarHeight + a + b;
            AddEvent(result);
            return result;
        }

        REACT_EVENT(AddEvent);
        std::function<void(double)> AddEvent;
    };
}