
/*
 * This file is auto-generated from a NativeModule spec file in js.
 *
 * This is a C++ Spec class that should be used with MakeTurboModuleProvider to register native modules
 * in a way that also verifies at compile time that the native module matches the interface required
 * by the TurboModule JS spec.
 */
#pragma once

#include <NativeModules.h>
#include <tuple>

namespace top::ourfor::app::iPlayClient {

struct TitleBarSpec_Constants {
    double titleBarHeight;
};


inline winrt::Microsoft::ReactNative::FieldMap GetStructInfo(TitleBarSpec_Constants*) noexcept {
    winrt::Microsoft::ReactNative::FieldMap fieldMap {
        {L"titleBarHeight", &TitleBarSpec_Constants::titleBarHeight},
    };
    return fieldMap;
}

struct TitleBarSpec : winrt::Microsoft::ReactNative::TurboModuleSpec {
  static constexpr auto constants = std::tuple{
      TypedConstant<TitleBarSpec_Constants>{0},
  };
  static constexpr auto methods = std::tuple{
      Method<void(double, double, Callback<double>) noexcept>{0, L"add"},
      Method<void(std::string) noexcept>{1, L"setTitle"},
  };

  template <class TModule>
  static constexpr void ValidateModule() noexcept {
    constexpr auto constantCheckResults = CheckConstants<TModule, TitleBarSpec>();
    constexpr auto methodCheckResults = CheckMethods<TModule, TitleBarSpec>();

    REACT_SHOW_CONSTANT_SPEC_ERRORS(
          0,
          "TitleBarSpec_Constants",
          "    REACT_GET_CONSTANTS(GetConstants) TitleBarSpec_Constants GetConstants() noexcept {/*implementation*/}\n"
          "    REACT_GET_CONSTANTS(GetConstants) static TitleBarSpec_Constants GetConstants() noexcept {/*implementation*/}\n");

    REACT_SHOW_METHOD_SPEC_ERRORS(
          0,
          "add",
          "    REACT_METHOD(add) void add(double a, double b, std::function<void(double)> const & callback) noexcept { /* implementation */ }\n"
          "    REACT_METHOD(add) static void add(double a, double b, std::function<void(double)> const & callback) noexcept { /* implementation */ }\n");
    REACT_SHOW_METHOD_SPEC_ERRORS(
          1,
          "setTitle",
          "    REACT_METHOD(setTitle) void setTitle(std::string title) noexcept { /* implementation */ }\n"
          "    REACT_METHOD(setTitle) static void setTitle(std::string title) noexcept { /* implementation */ }\n");
  }
};

} // namespace top::ourfor::app::iPlayClient
