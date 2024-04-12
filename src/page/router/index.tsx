import { Router as MobileRouter } from "./MobileRouter"
import { Router as DesktopRouter } from "./DesktopRouter"
import { Device } from "@helper/device"

export const Router = Device.isMobile ? MobileRouter : DesktopRouter