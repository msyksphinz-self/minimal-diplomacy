package core_complex

import freechips.rocketchip.config._

class BaseConfig extends Config((site, here, up) => {
  case BusWidthBytes => 256 / 8
})

class Bus128BitConfig extends Config((site, here, up) => {
  case BusWidthBytes => 128 / 8
  case AddrSize => 0x100 * site(BusWidthBytes)
})

class Bus64BitConfig extends Config((site, here, up) => {
  case BusWidthBytes => 64 / 8
  case AddrSize => 0x200 * here(BusWidthBytes)
})

class Bus32BitConfig extends Config((site, here, up) => {
  case BusWidthBytes => 32 / 8
  case AddrSize => 0x300 * here(BusWidthBytes)
})

class IfuConnectConfig extends Config((site, here, up) => {
  case ConnectIfu => true
})

class IfuNotConnectConfig extends Config((site, here, up) => {
  case ConnectIfu => false
})

class Default1Config extends Config(
  new Bus32BitConfig ++ new IfuConnectConfig
)

class Default2Config extends Config(
  new BaseConfig ++
  new Bus128BitConfig ++ new IfuNotConnectConfig
)
